package qnopy.com.qnopyandroid.uiutils;

import static qnopy.com.qnopyandroid.util.Util.getBaseContextPath;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.responsemodel.AttachmentResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.AgreementActivity;
import qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by myog3 on 30-11-2017.
 */

public class SendDBTask extends AsyncTask<MediaType, Void, String> {

    private static final String TAG = "SendDBTask";
    private AppCompatActivity context;
    private String msgBoard, username, password, userGuid;
    private String userIssue = "";
    String filepath;
    private AlertDialog progressDialog;

    public SendDBTask(AppCompatActivity context) {
        this.context = context;
        msgBoard = null;
        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userGuid = Util.getSharedPreferencesProperty(context, username);
    }

    public SendDBTask(AppCompatActivity context, String userIssue) {
        this.context = context;
        msgBoard = null;
        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userGuid = Util.getSharedPreferencesProperty(context, username);
        this.userIssue = userIssue;
    }

    @Override
    protected void onPreExecute() {
        showProgress();
    }

    @Override
    protected String doInBackground(MediaType... mediaTypes) {

        String response = null;
        AttachmentResponseModel resultModel = null;
        MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
        String userGuid = Util.getSharedPreferencesProperty(context, username);
        AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(context);

        String root = BaseMenuActivity.getDBZipStorageDir(context).getAbsolutePath();
        filepath = root + File.separator + userGuid + ".zip";

        String dbPath = getBaseContextPath(context) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME;

        if (Util.createZipFile(dbPath, root, userGuid + ".zip")) {

            files.add("password", password);
            files.add("notes", "Database for user: " + username + " Device :" + DeviceInfo.getDeviceID(context));
            files.add("file", new FileSystemResource(filepath));
            files.add("userGuid", userGuid);
            files.add("userIssue", userIssue);

            resultModel = mAquaBlueService.upload_DB_toServer(context.getResources().getString(R.string.prod_base_uri),
                    context.getResources().getString(R.string.prod_upload_db),
                    files);

            if (resultModel != null) {
                if (!resultModel.isSuccess()) {
                    response = "FALSE";
                    Log.e("Agreement", "doInBackground: SEND DB TO SUPPORT-- " + resultModel.isSuccess());
                } else if (resultModel.isSuccess()) {
                    GlobalStrings.responseMessage = resultModel.getMessage();
                    response = "SUCCESS";
                    msgBoard = "Database sent successfully.";
                } else if (resultModel.equals(HttpStatus.EXPECTATION_FAILED.toString())) {
                    Util.setLogout((Activity) context);
                    msgBoard = resultModel.getMessage();
                } else if (resultModel.getResponseCode().toString().equals(HttpStatus.NOT_ACCEPTABLE.toString())) {
                    msgBoard = resultModel.getMessage();
                    Util.setLogout((Activity) context);
                } else if (resultModel.getResponseCode().toString().equals(HttpStatus.BAD_REQUEST.toString())) {
                    msgBoard = resultModel.getMessage();
                    Util.setLogout((Activity) context);
                } else if (resultModel.getResponseCode().toString().equals(HttpStatus.NOT_FOUND.toString())) {
                    msgBoard = resultModel.getMessage();
                    Util.setLogout((Activity) context);
                } else if (resultModel.getResponseCode().toString().equals(HttpStatus.UNAUTHORIZED.toString())) {
                    msgBoard = resultModel.getMessage();
                    Util.setLogout((Activity) context);
                } else if (resultModel.getResponseCode().toString().equals(HttpStatus.CONFLICT.toString())) {
                    msgBoard = resultModel.getMessage();
                    Util.setLogout((Activity) context);
                } else {
                    msgBoard = "Database failed to upload.";
                    Log.i(TAG, "Response by server: " + msgBoard);
                }
            } else {
                String msg = "Attachment " + filepath + " Failed to sync.";
                Log.i(TAG, msg);
                msgBoard = "Database failed to upload.";
            }
        } else {
            Log.i(TAG, "Fail to create .zip");
            msgBoard = "Database failed to upload.";
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        AlertManager.showNormalAlert("Send Data to Support", msgBoard, "OK",
                "", false, context);
//        Toast.makeText(context, msgBoard, Toast.LENGTH_LONG).show();
        Util.deleteRecursive(BaseMenuActivity.getDBZipStorageDir(context));//deleting zip db folder

        cancelProgress();

        if (result != null) {
            if (result.equals("FALSE")) {
                showAgreement();
            }
        }
    }

    private void showAgreement() {
        Intent intent = new Intent(context, AgreementActivity.class);
        intent.putExtra("input", "sendDB");
//        context.startActivity(intent);
        Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        cancelProgress();
    }

    private void showProgress() {
        progressDialog = AlertManager.showQnopyProgressBar((AppCompatActivity) context,
                context.getString(R.string.uploading_db));
        progressDialog.show();
    }

    private void cancelProgress() {
        if (progressDialog.isShowing())
            progressDialog.cancel();
    }
}
