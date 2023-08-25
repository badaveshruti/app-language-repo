package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;

import org.springframework.http.MediaType;

import java.io.File;
import java.util.List;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.NotificationsDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class MobileReportRequiredActivity extends ProgressDialogActivity {

    Bundle extras;
    String TAG = "MobileReportRequiredAct";
    String userId = "", formId = "", eventID = "", siteID = "";
    String deviceToken;
    String deviceID;
    String username = null;
    String userGuid = null;
    Context context;
    AquaBlueServiceImpl mAquaBlueService;
    String isDownloaded = "false";
    public static String mErrorString = null;
    LinearLayout progressll;

    WebView webView;
    PDFView mPdfView;
    String call;
    String pdfByteArray;

    File file = null;
    String mResponseString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mobile_report_required);
        setContentView(R.layout.layout_transparent);

/*
        progressll = findViewById(R.id.progress_container_mobile_report);
        webView = findViewById(R.id.webViewPDF_Report);
        mPdfView = findViewById(R.id.pdfViewReport);
*/

        mAquaBlueService = new AquaBlueServiceImpl(MobileReportRequiredActivity.this);
        context = MobileReportRequiredActivity.this;
        extras = getIntent().getExtras();

        if (extras != null) {
            userId = extras.getString("USER_ID");
            formId = extras.getString("FORM_ID");
            siteID = extras.getString("SITE_ID");
            eventID = extras.getString("EVENT_ID");
            call = extras.getString("call");
        } else {
            Log.i(TAG, "NO SiteID & EventID found");
            finish();
        }

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(context, username);
        deviceID = DeviceInfo.getDeviceID(context);
        deviceToken = Util.getSharedPreferencesProperty(context, GlobalStrings.NOTIFICATION_REGISTRATION_ID);
        Log.e(TAG, "onCreate: userID- " + userId + " formID- " + formId + " siteId- " + siteID + " eventID- " + eventID + " deviceID- " + deviceID + " userName- " + username + " userGuid-" + userGuid + " DEVICE_TOKEN-- " + deviceToken);

        if (call.equals("NotificationAdapter")) {

            Log.e("calltoReportActivity", "onCreate: called from notification adapter" + userId + " formID- " + formId + " siteId- " + siteID + " eventID- " + eventID);
            if (CheckNetwork.isInternetAvailable(context)) {
                new PostMessageTask().execute();
            } else {
                Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            }
        }
        if (call.equals("MyFirebaseMessagingService")) {

            Log.e("calltoReportActivity", "onCreate: called from MyFirebaseMessagingService" + userId + " formID- " + formId + " siteId- " + siteID + " eventID- " + eventID);

            NotificationsDataSource nds = new NotificationsDataSource(MobileReportRequiredActivity.this);
            String notificationStatus = "1";
            nds.updateNotifcationStatusWhenDownloaded(notificationStatus, userId, formId, siteID, eventID);

            if (CheckNetwork.isInternetAvailable(context)) {
                new PostMessageTask().execute();
            } else {
                Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            }
        }
        if (call.equals("LocationActivity")) {
            Log.e("calltoReportActivity", "onCreate: called from location activity");
            showPdf();
        }
        if (call.equals("AgreementActivity")) {

            Log.e("calltoReportActivity", "onCreate: called from agreement activity");
            if (CheckNetwork.isInternetAvailable(context)) {
                new PostMessageTask().execute();
            } else {
                Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showPdf() {

        String filePath = Util.getFileFolderDirPathForPDF(context, siteID, eventID);

        if (filePath.isEmpty()) {
            Toast.makeText(context, "No file found", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath, "" + eventID + ".pdf");
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);

        if (type == null) {
            type = "*/*";
        }

        Uri dataa = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dataa = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            dataa = Uri.fromFile(file);
            //dataa = Uri.parse(file);
        }

        try {
            //mPdfView.fromUri(dataa).load();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(dataa, type);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, dataa, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> resInfos = pm.queryIntentActivities(intent, 0);
            Intent intent1 = Intent.createChooser(intent, "Open File");
            if (resInfos.size() > 0) {
                startActivity(intent1);
                this.finish();
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setNeutralButton("OK", null);
                builder.setMessage("No suitable application installed on your device to view this(." + ext + ") file.");
                builder.setTitle("Oops!");
                android.app.AlertDialog dialog = builder.create();
                dialog.show();

                //  Toast.makeText(context, "No suitable application installed to view this file", Toast.LENGTH_LONG).show();
            }

        } catch (Exception n) {
            n.printStackTrace();
        }
    }

    private void dismissLoading() {
//        progressll.setVisibility(View.GONE);
    }

    private void showLoading() {
//        progressll.setVisibility(View.VISIBLE);
    }

    private class PostMessageTask extends AsyncTask<MediaType, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress(getString(R.string.downloading_file));
        }

        @Override
        protected String doInBackground(MediaType... params) {
            String response = null;
            String strResponseBody = null;
            try {
                if (null != mAquaBlueService) {
                    isDownloaded = mAquaBlueService.FileDownloadPDF(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.mobile_download_report_required),
                            userGuid,
                            siteID, eventID, formId, userId, deviceID);
                    if (isDownloaded.equals("true")) {
                        mResponseString = "true";
                    } else if (isDownloaded.equals("false")) {
                        mErrorString = "All Files are not Downloaded.Please,try again!";
                        Log.i(TAG, "FDownloadData response :" + isDownloaded);
                        mResponseString = "false";
                    }
                } else {
                    mErrorString = isDownloaded;
                }
                Log.e(TAG, "downloadReport: " + mErrorString);
            } catch (Exception n) {
                n.printStackTrace();
            }
            return mResponseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cancelAlertProgress();
            showResult(s);
        }
    }

    private void showResult(String s) {
        if (s != null) {
            if (s.equals("false")) {
                showAgreement();
            } else if (s.equals("true")) {
                showPdf();
            }
        } else {
            showToast(getString(R.string.something_went_wrong), true);
        }
    }

    private void showAgreement() {
        Intent intent = new Intent(MobileReportRequiredActivity.this, AgreementActivity.class);
        intent.putExtra("input", "mobilereport");
//        startActivity(intent);
        Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        finish();
    }
}
