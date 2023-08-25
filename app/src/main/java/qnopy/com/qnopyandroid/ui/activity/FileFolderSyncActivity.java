package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.downloadFileModel;
import qnopy.com.qnopyandroid.responsemodel.FileDataModel;
import qnopy.com.qnopyandroid.responsemodel.FileFolderResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FileResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FolderDataModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class FileFolderSyncActivity extends ProgressDialogActivity {

    Context context;
    FileFolderResponseModel mRetFFMetaSyncData = null;
    FileResponseModel mRetFileData = null;
    String isDownloaded = "false";
    List<FolderDataModel> mRetFolderList = null;
    List<FileDataModel> mRetFileList = null;

    int mStateMachine = 0;
    String strResponseBody = null;
    public static String mErrorString = null;
    AquaBlueServiceImpl mAquaBlueService;

    public final int STATE_DOWNLOAD_FILEFOLDER_METADATA = 0;
    public final int STATE_DOWNLOAD_FILE = 1;
    String siteID = null;
    boolean RETURN_TO_LOADKMZ = false, SUCCESS_FLAG = false;
    String ERROR_MESSAGE = "";
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_file_folder_sync);
        context = this;
        TAG = "FileFolderSyncActivity";

        ERROR_MESSAGE = getString(R.string.bad_internet_connectivity);
        userId = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey("RETURN_TO_LOADKMZ")) {
            RETURN_TO_LOADKMZ = extras.getBoolean("RETURN_TO_LOADKMZ");
        }

        mAquaBlueService = new AquaBlueServiceImpl(context);
        if (CheckNetwork.isInternetAvailable(context)) {
            new PostMessageTask().execute();

        } else {
            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            SUCCESS_FLAG = false;

            if (RETURN_TO_LOADKMZ) {
                Intent output = new Intent();
                output.putExtra("SUCCESS_FLAG", SUCCESS_FLAG);
                output.putExtra("SUCCESS_MESSAGE", ERROR_MESSAGE);
                setResult(RESULT_OK, output);
            } else {
                launchFileFolderActivity();
            }
            finish();
        }
    }

    // Handles sign-up request in back ground
    public class PostMessageTask extends AsyncTask<MediaType, Void, Object> {


        String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        String password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);

        @Override
        protected void onPreExecute() {
            showAlertProgress("Downloading files...");
        }// end of onPreExecute

        @Override
        protected Object doInBackground(MediaType... params) {
            try {
                // Get handle to HTTP service
                mStateMachine = STATE_DOWNLOAD_FILEFOLDER_METADATA;
                mErrorString = null;
                String guid = Util.getSharedPreferencesProperty(context, username);//"f817f2f2-3b36-11e5-9708-0ea7cb7cc776";//
                siteID = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID);//"1";//
                String response = null;
                Log.i(TAG, "State Download FileFolder metadata for siteID:-" + siteID + " GUID:" + guid);
                Log.i("folder", "State Download FileFolder metadata for siteID:-" + siteID + " GUID:" + guid);
                switch (mStateMachine) {
                    case STATE_DOWNLOAD_FILEFOLDER_METADATA:

                        if (null != mAquaBlueService && (siteID != null && guid != null)) {

//                           mRetFFMetaSyncData = mAquaBlueService.getFileFolderSyncData(GlobalStrings.Local_Base_URL,
//                                    getResources().getString(R.string.url_filefolder),
//                                    guid,
//                                    siteID);

                            mRetFFMetaSyncData = mAquaBlueService.getFileFolderSyncData(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.url_filefolder),
                                    guid,
                                    siteID);

                            if (null != mRetFFMetaSyncData) {
                                if (mRetFFMetaSyncData.isSuccess()) {
                                    saveSyncedData();
                                } else {
                                    return mRetFFMetaSyncData;
                                }

                                 /*else if (!mRetFFMetaSyncData.isSuccess() && mRetFFMetaSyncData.getData() == null){
                                    return mRetFFMetaSyncData;
                                }*/
                            } else {
                                Log.i(TAG, "FFMetaDataSync response :" + mRetFFMetaSyncData);
                                mErrorString = getString(R.string.bad_internet_connectivity);
                                break;
                            }
                        } else {
                            mErrorString = getString(R.string.bad_internet_connectivity);
                            break;

                        }


                    case STATE_DOWNLOAD_FILE:
                        ArrayList<downloadFileModel> downloadList = new ArrayList<>();
                        String dirToStoreFiles = Util.getFileFolderDirPath(context, siteID);

                        if (dirToStoreFiles.isEmpty())
                            break;

                        Log.e("Qnopy", "doInBackground: dirToStoreFiles--------------------->>>> " + dirToStoreFiles);
                        downloadList = new FileFolderDataSource(context).getFileListToDownload(siteID, guid);
                        int downloadCount = downloadList.size();
                        Log.i("Qnopy", "FileDownload Count:" + downloadCount);
                        if (downloadCount > 0) {
                            for (downloadFileModel file : downloadList) {

                                String filename = file.getFileName();
                                String fileguid = file.getFileGuid();
                                String userGuid = file.getUserGuid();
                                String siteId = file.getSiteId();

                                Log.e("folderFile", "doInBackground: " + filename + " | " + fileguid + " | " + siteId);
                                if (null != mAquaBlueService && (siteID != null && guid != null)) {

//                                    isDownloaded = mAquaBlueService.FileDownload(GlobalStrings.Local_Base_URL,
//                                            getResources().getString(R.string.url_filedownload),
//                                            userGuid,
//                                            siteId, fileguid, filename);
                                    isDownloaded = mAquaBlueService.FileDownload(
                                            getResources().getString(R.string.prod_base_uri),
                                            getResources().getString(R.string.url_filedownload),
                                            userGuid,
                                            siteId, fileguid, filename, userId);

                                    if (isDownloaded.equals("true")) {
                                        //System.out.println("gggg"+"size = "+mRetSiteUserRole.length);
                                        response = mAquaBlueService.getResponseCode();
                                        if (response == null) {
                                            mErrorString = getString(R.string.failed_to_download_file);
                                        } else {
                                            strResponseBody = mAquaBlueService.getResponseBody();
                                            Log.i(TAG, "FDownloadData response body:" + strResponseBody);
                                            FileFolderDataSource ob = new FileFolderDataSource(context);
                                            ob.updateS_File_Download_status(siteId, fileguid, "TRUE");
                                            mErrorString = null;
                                        }
                                    } else if (isDownloaded.equals("false")) {
//                                        FileFolderDataSource ob = new FileFolderDataSource(context);
//                                        ob.deleteRow_sFile(fileguid);
                                        mErrorString = getString(R.string.all_fiels_are_not_downloaded);
                                        Log.i(TAG, "FDownloadData response :" + isDownloaded);
                                    }
                                } else {
                                    mErrorString = isDownloaded;
                                    break;
                                }
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "FFMetaDataSync doInBackground() Exception:" + e.getMessage());
                mErrorString = getString(R.string.unable_to_connect_to_server);
                return null;
            }
            return null;
        }// end of doInBackground

        @Override
        protected void onPostExecute(Object result) {
            Log.d(TAG, " onPostExecute: Result = " + result);
            // Close the dialog
            cancelAlertProgress();

            Log.d("File Folder", " FFMetaDataSync result= " + result);

            if (result != null) {
                FileFolderResponseModel ffmodel = (FileFolderResponseModel) result;

                GlobalStrings.responseMessage = ffmodel.getMessage();

                ERROR_MESSAGE = GlobalStrings.responseMessage;

                HttpStatus respCode = ffmodel.getResponseCode();
                String respMessage = ffmodel.getMessage();
                if (respCode.equals(HttpStatus.NOT_ACCEPTABLE)) {
                    SUCCESS_FLAG = false;

                    Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    if (RETURN_TO_LOADKMZ) {
                        Intent output = new Intent();
                        output.putExtra("SUCCESS_FLAG", SUCCESS_FLAG);
                        output.putExtra("SUCCESS_MESSAGE", ERROR_MESSAGE);
                        setResult(RESULT_OK, output);
                    } else {
                        launchFileFolderActivity();
                    }

                    finish();

                } else if (respMessage.contains("User license agreement is not accepted")) {
                    showAgreement();
                } else if (respCode.equals(HttpStatus.NOT_FOUND) || respCode.equals(HttpStatus.LOCKED)) {
                    Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
                    // finish();
                } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                    Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
//                    launchFileFolderActivity();

                    SUCCESS_FLAG = false;

                    if (RETURN_TO_LOADKMZ) {
                        Intent output = new Intent();
                        output.putExtra("SUCCESS_FLAG", SUCCESS_FLAG);
                        output.putExtra("SUCCESS_MESSAGE", ERROR_MESSAGE);
                        setResult(RESULT_OK, output);
                    } else {
                        launchFileFolderActivity();
                    }
                    finish();
                } else if (
                        (result.equals(HttpStatus.EXPECTATION_FAILED)) ||
                                (result.equals(HttpStatus.UNAUTHORIZED)) ||
                                (result.equals(HttpStatus.CONFLICT))
                ) {
                    Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
                } else if (result.equals(HttpStatus.FAILED_DEPENDENCY.toString())) {
                    Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                }

            } else {
                if (mErrorString == null) {

//            // display a notification to the user with the response message
                    Toast.makeText(context, getString(R.string.all_your_files_are_downloaded), Toast.LENGTH_LONG).show();
                    Log.e("abhi", "onPostExecute: " + getString(R.string.all_your_files_are_downloaded));
                    // launchFileFolderActivity();
                    ERROR_MESSAGE = getString(R.string.all_your_files_are_downloaded);
                    SUCCESS_FLAG = true;

                } else {
                    Log.e(TAG, mErrorString);
                    Toast.makeText(context, mErrorString, Toast.LENGTH_LONG).show();
                    ERROR_MESSAGE = mErrorString;
                    SUCCESS_FLAG = false;
                }

                if (RETURN_TO_LOADKMZ) {
                    Intent output = new Intent();
                    output.putExtra("SUCCESS_FLAG", SUCCESS_FLAG);
                    output.putExtra("SUCCESS_MESSAGE", ERROR_MESSAGE);
                    setResult(RESULT_OK, output);
                } else {
                    launchFileFolderActivity();
                }
                finish();

//                launchFileFolderActivity();
            }
            // close the activity

        }// end of onPostExecute
    }// end of PostMessageTask

    private void showAgreement() {
        Intent intent = new Intent(FileFolderSyncActivity.this, AgreementActivity.class);
        intent.putExtra("input", "filefolder");
//        startActivity(intent);
        Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        finish();
    }

    String saveSyncedData() {
        mErrorString = null;
// 		long t = System.currentTimeMillis();

        FileFolderDataSource ffmetaSource = new FileFolderDataSource(context);


        // TODO: 26-Feb-16  1st time Visit to FileFolder
        if (mRetFFMetaSyncData != null) {

            ffmetaSource.truncateTempFileFolder(siteID);

            mRetFolderList = mRetFFMetaSyncData.getData().getFolderData();
            mRetFileList = mRetFFMetaSyncData.getData().getFileData();


            if (mRetFolderList != null && mRetFolderList.size() > 0) {
                ffmetaSource.truncateS_Folder(siteID);
                ffmetaSource.insertFolderList(mRetFolderList);

            } else {
                // TODO: 10-Jun-16  Remove All Folders
                ffmetaSource.truncateS_Folder(siteID);
            }

            if (mRetFileList != null && mRetFileList.size() > 0) {
                ffmetaSource.insertTempFileList(context, siteID, mRetFileList);
                ffmetaSource.removeRowFrom_sFile(siteID);
                ffmetaSource.insertFileList(ffmetaSource.getFileListToMove(siteID));

            } else {
                // TODO: 10-Jun-16 Remove All Files
                ffmetaSource.truncateS_File(siteID);
            }

            if ((mRetFolderList == null || mRetFolderList.size() < 1) && (mRetFileList == null || mRetFileList.size() < 1)) {
                mErrorString = "No files uploaded for this ' "
                        + Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME) + " ' Site.";
            }

            //Toast.makeText(this, GlobalStrings.FFmetaSyncSuccessMsg, Toast.LENGTH_SHORT).show();
        }

        return mErrorString;
    }

    void launchFileFolderActivity() {
        Intent applicationIntent = new Intent(context, FileFolderMainActivity.class);
        //startActivity(applicationIntent);
        finish();
    }

}
