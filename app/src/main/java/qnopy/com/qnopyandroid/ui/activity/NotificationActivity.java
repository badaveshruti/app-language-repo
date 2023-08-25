package qnopy.com.qnopyandroid.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.NotificationAdapter;
import qnopy.com.qnopyandroid.clientmodel.NotificationRow;
import qnopy.com.qnopyandroid.db.NotificationsDataSource;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class NotificationActivity extends ProgressDialogActivity
        implements NotificationAdapter.OnNotificationClickListener {

    private static final String TAG = "NotificationActivity";
    RecyclerView recyclerView;
    TextView emptyView;
    Context context;
    private LinearLayoutManager layoutManager;
    NotificationAdapter adapter;
    int userID;
    ActionBar actionBar;
    MyBroadCastReceiver myBroadCastReceiver;
    private AquaBlueServiceImpl mAquaBlueService;
    private String cocDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        context = this;
        myBroadCastReceiver = new MyBroadCastReceiver();
        mAquaBlueService = new AquaBlueServiceImpl(this);
        cocDir = Util.getFileFolderDirPathForCOCPDF(this);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.notifications));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.noti_recyclerView);
        emptyView = findViewById(R.id.noti_emptyView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

//        ArrayList<NotificationRow> list = collectData();
//        if (list == null || list.size() < 1) {
//            emptyView.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//        } else {
        showData();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    public ArrayList<NotificationRow> collectData() {
        ArrayList<NotificationRow> list;
        NotificationsDataSource ds = new NotificationsDataSource(context);
        list = ds.getAllNotificationsAvailable(userID);
        return list;
    }

    public void showData() {

        ArrayList<NotificationRow> list = collectData();
        if (list == null || list.size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {

            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Collections.sort(list, new CustomComparator());

            adapter = new NotificationAdapter(list, context, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onNotificationClicked(NotificationRow notification) {

        if (notification.fileName != null) {
            List<String> list = Util.splitStringToArray("|", notification.fileName);
            if (list.size() == 2) {
                File file = new File(cocDir, list.get(1));
                if (!file.exists())
                    new DownloadCOCFile(list.get(0), list.get(1)).execute();
                else
                    startShowFilesActivity(cocDir);
            }
        }
    }

    private void startShowFilesActivity(String filePath) {
        Intent intent = new Intent(this, ShowFilesActivity.class);
        intent.putExtra(GlobalStrings.KEY_FILE_PATH, filePath);
        intent.setAction(GlobalStrings.PRINT_COC);
        startActivity(intent);
    }

    public class CustomComparator implements Comparator<NotificationRow> {
        @Override
        public int compare(NotificationRow lhs, NotificationRow rhs) {
            int res = 0;
            try {

                long lhsdate = Long.parseLong(lhs.date);
                long rhsdate = Long.parseLong(rhs.date);

                Log.i(TAG, "LHS:" + lhsdate);
                Log.i(TAG, "RHS:" + rhsdate);

                if (rhsdate > lhsdate) {
                    res = 1;
                } else {
                    res = -1;
                }
                //res = (int) (rhs.getStartDate() - lhs.getStartDate());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "CustomComparator compare() Error:" + e.getMessage());
                return -1;
            }

            return res;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerMyReceiver();
        showData();
    }

    /**
     * This method is responsible to register an action to BroadCastReceiver
     */
    private void registerMyReceiver() {

        try {
            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(GlobalStrings.BROADCAST_ACTION);
            registerReceiver(myBroadCastReceiver, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * MyBroadCastReceiver is responsible to receive broadCast from register action
     */
    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Log.d(TAG, "onReceive() called");
                Toast.makeText(context, getString(R.string.you_have_anew_notification), Toast.LENGTH_LONG).show();
                showData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(myBroadCastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // make sure to unregister your receiver after finishing of this activity
    }

    class DownloadCOCFile extends AsyncTask<Void, Void, String> {

        String key;
        String fileName;

        public DownloadCOCFile(String key, String fileName) {
            this.key = key;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress(getString(R.string.downloading_coc));
        }

        @Override
        protected String doInBackground(Void... voids) {
            return downloadCOC(key, fileName);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (!response.contains("false")) {
                doPrintAction(response);
            }
            cancelAlertProgress();
        }
    }

    private String downloadCOC(String key, String fileName) {
        return mAquaBlueService.cocFileDownload(getString(R.string.prod_base_uri),
                getString(R.string.url_download_gen_coc), "", fileName, key, "");
    }

    private void doPrintAction(String path) {

        File file = new File(path);

        if (file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                PrintAttributes.Builder printAttributeBuilder = new PrintAttributes.Builder();
                printAttributeBuilder.setMediaSize(new PrintAttributes.MediaSize("LABEL",
                        "android", 612, 792));
                printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

                PDFUtil.printPdf(this, file, printAttributeBuilder.build());
            }
        } else
            showToast("Pdf file doesn't exist", true);
    }
}
