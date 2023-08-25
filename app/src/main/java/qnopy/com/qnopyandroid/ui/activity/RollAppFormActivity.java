package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.RollAppFormAdapter;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 12/3/2017.
 */

public class RollAppFormActivity extends ProgressDialogActivity {

    Context context;
    private static final String TAG = "RollAppFormActivity";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    androidx.appcompat.app.ActionBar actionBar;
    Toolbar toolbar;

    TextView emptylist_view;
    LinearLayout form_list_container;
    MaterialSearchView searchView;
    List<SSiteMobileApp> values;
    Bundle extras;
    RollAppFormAdapter adapter;
    String pending = null;
    boolean fromApplication = false;
    // MenuItem item;
    ArrayList<Integer> formlist = new ArrayList<>();
    String isFirsttime = null;
    String userid = null;
    int companyID = 0;
    Menu mMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rollappform);
        context = this;

        recyclerView = findViewById(R.id.formListView);
        toolbar = findViewById(R.id.rollapp_toolbar);
        form_list_container = findViewById(R.id.rollappLis);
        emptylist_view = findViewById(R.id.empty_form);
        searchView = findViewById(R.id.search_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setSupportActionBar(toolbar);

        extras = getIntent().getExtras();
        if (extras != null) {
            pending = extras.getString("Pending");
            fromApplication = extras.getBoolean("fromApplicationActivity");
        }

        if (!fromApplication) {
            Util.setSharedPreferencesProperty(context, "RollAppFormActivity", "active");
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select Form from the list below");
        }

        String compnyid = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);
        if (compnyid != null) {
            try {
                companyID = Integer.parseInt(compnyid);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "companyID parse Error-" + e.getMessage());
            }
        }

        SiteMobileAppDataSource siteMobileAppDataSource = new SiteMobileAppDataSource(context);
        values = siteMobileAppDataSource.getSignUpForms(companyID);

        Collections.sort(values, new Comparator<SSiteMobileApp>() {
            @Override
            public int compare(SSiteMobileApp lhs, SSiteMobileApp rhs) {
                return lhs.getDisplay_name_roll_into_app().compareToIgnoreCase(rhs.getDisplay_name_roll_into_app());
            }
        });

        if (values != null && values.size() > 0) {
            emptylist_view.setVisibility(View.GONE);
            adapter = new RollAppFormAdapter(values, context, RollAppFormActivity.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Download Forms");
            //alertDialogBuilder.setCancelable(false);
            alertDialogBuilder
                    .setMessage("There was an error in downloading forms.Please try again.");
            // set positive button: Yes message
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(context, MetaSyncActivity.class);
                            intent.putExtra("RollAppdownloadFail", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            MetaSyncActivity.DOWNLOAD_FORM_REQUEST_COUNT++;
                            dialog.dismiss();
                        }
                    });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            //Toast.makeText(context, "Forms are not downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(RollAppFormActivity.this, MainDrawerActivity.class);
        Intent intent = new Intent(RollAppFormActivity.this, HomeScreenActivity.class);
        startActivity(intent);

        if (MainDrawerActivity.mainDrawerActivity != null)
            MainDrawerActivity.mainDrawerActivity.finish();

        if (SiteActivity.siteActivity != null)
            SiteActivity.siteActivity.finish();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.self_signup, menu);
        mMenu = menu;
        toggleSaveMenuItem(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                formlist = adapter.getFormList();
                //  if (b) {
                if (formlist != null && formlist.size() > 0) {
                    Intent intent = new Intent(context, CreateProjectActivity.class);
                    intent.putExtra("ArrayList", formlist);
                    intent.putExtra("Pending", pending);
                    intent.putExtra("fromApplicationActivity", fromApplication);
                    startActivity(intent);
                } else {
                    //item.setVisible(false);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setCancelable(false);
                    dialog.setTitle("Select Form");
                    dialog.setMessage("Please select at least one form.");
                    dialog.setNeutralButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Delete".
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog alert = dialog.create();
                    alert.show();

                }
                //  }
                return true;

            case android.R.id.home:
                if (fromApplication) {
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }
                return true;
        }
        return true;
    }

    public void toggleSaveMenuItem(boolean isEnable) {
        MenuItem item = mMenu.findItem(R.id.action_save);
        item.setVisible(isEnable);
    }
}
