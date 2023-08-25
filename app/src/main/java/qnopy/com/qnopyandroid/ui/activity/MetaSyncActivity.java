package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.TaskModelClasses.TaskAttachments;
import qnopy.com.qnopyandroid.TaskModelClasses.TaskComments;
import qnopy.com.qnopyandroid.TaskModelClasses.TaskDataList;
import qnopy.com.qnopyandroid.clientmodel.LocationProfilePictures;
import qnopy.com.qnopyandroid.clientmodel.Lov;
import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.clientmodel.metaForms.FormTabs;
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse;
import qnopy.com.qnopyandroid.db.CMMethodsDataSource;
import qnopy.com.qnopyandroid.db.CocDetailDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.DefaultValueDataSource;
import qnopy.com.qnopyandroid.db.FormSitesDataSource;
import qnopy.com.qnopyandroid.db.LocationAttributeDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.LocationProfilePictureDataSource;
import qnopy.com.qnopyandroid.db.LovDataSource;
import qnopy.com.qnopyandroid.db.MetaDataAttributesDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource;
import qnopy.com.qnopyandroid.db.SyncStatusDataSource;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.CocDataModel;
import qnopy.com.qnopyandroid.requestmodel.MetaSyncDataModel;
import qnopy.com.qnopyandroid.requestmodel.RUnitConverter;
import qnopy.com.qnopyandroid.requestmodel.SCocDetails;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;
import qnopy.com.qnopyandroid.requestmodel.SLocation;
import qnopy.com.qnopyandroid.requestmodel.SLovItem;
import qnopy.com.qnopyandroid.requestmodel.SMetaData;
import qnopy.com.qnopyandroid.requestmodel.SMobileApp;
import qnopy.com.qnopyandroid.requestmodel.SSite;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole;
import qnopy.com.qnopyandroid.responsemodel.CocObjectModel;
import qnopy.com.qnopyandroid.responsemodel.CocResponseModel;
import qnopy.com.qnopyandroid.responsemodel.DefaultValueModel;
import qnopy.com.qnopyandroid.responsemodel.JsonCocDetailsObjectModel;
import qnopy.com.qnopyandroid.responsemodel.MetaSyncResponseModel;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.SLocationAttribute;
import qnopy.com.qnopyandroid.responsemodel.SyncCocResponseModel;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.sitesProjectUser.AllSitesActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

/*
 * Activity to handle user registration process
 * Handles the network request in background using async task
 */
@AndroidEntryPoint
public class MetaSyncActivity extends ProgressDialogActivity {

    private static final String LOG = MetaSyncActivity.class.getName();
    Button mBtnDLMetaData, mBtnClear;
    EditText mEditText = null;
    int mStateMachine = 0;
    String strResponseBody = null;

    String username = null;
    String userGuid = null;
    String password = null;
    int userId;
    String lastSyncDate = "0";
    SyncStatusDataSource syncStatusDataSource;

    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    SMetaData[] mRetSMetaData = null;
    RUnitConverter[] mRetSMetaDataSync = null;
    SSiteUserRole[] mRetSiteUserRole = null;
    MetaSyncDataModel mRetMetaSyncData = null;
    MetaSyncResponseModel mRetMetaSyncResponse = null;
    CocResponseModel mRetCocResponse = null;
    TaskDataResponse taskDataResponse = null;
    CocDataModel mRetCocData = null;

    SSite[] mRetSite = null;
    SLocation[] mRetLocation = null;
//	RUnitConverter[] mRUnitConverter = null;

    List<SMetaData> mRetMetaDataList = null;
    List<SSite> mRetSiteList = null;
    List<SLocation> mRetLocationList = null;
    List<SMobileApp> mRetMobileAppList = null;
    List<SLovItem> mRetLovItemList = null;
    List<SSiteMobileApp> mRetSiteAppList = null;
    List<DefaultValueModel> mRetDefaultValueList = null;

    List<SSiteUserRole> mRetSiteUserRoleList = null;
    List<Lov> mRetLovList = null;
    List<SLocationAttribute> mRetLocAttributeList = null;
    // List<RFieldParameter> mRetFieldParamList = null;
    private Integer statusForGuid;
    Context objContext = null;
    int companyid = 0;
    Bundle extras;
    public List<NewClientLocation> UploadLocationList = null;
    public List<CocObjectModel> UploadCoCList = null;
    @Inject
    LocationDataSource locationDataSource;

    public final int STATE_DOWNLOAD_METADATA_USERROLE = 0;
    public final int STATE_DOWNLOAD_METADATA_UNITCONVERTER = 1;
    public final int STATE_DOWNLOAD_METADATA_SITE = 2;
    public final int STATE_DOWNLOAD_METADATA_LOCATION = 3;
    public final int STATE_DOWNLOAD_METADATA = 4;
    public final int STATE_UPLOAD_LOCATION = 5;
    boolean fromactivation, fromaddSite, rollappdownload = false;

    public static int mState = 0, DOWNLOAD_FORM_REQUEST_COUNT = 0;
    //    public static String mErrorString = null;
    public static int mFlag = 0;
    String deviceID;

    String serverLastSyncDate;
    String mUrlTaskListData;
    RequestQueue mRequestQueue;
    ArrayList<TaskDataList> mArrayListTaskDataList;
    ArrayList<TaskAttachments> mArrayListTaskAttachments;
    ArrayList<TaskComments> mArrayListTaskComments;
    private boolean isFromTabAct;
    private boolean isForceDownload;
    private MetaFormsJsonResponse jsonFormsResponse = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        objContext = this;

        if (isTaskRoot())
            setContentView(R.layout.layout_transparent_bg);

        username = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(objContext, username);
        password = Util.getSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD);
        userId = Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID));
        companyid = Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.COMPANYID));
        deviceID = DeviceInfo.getDeviceID(objContext);
        Log.e("abhishek", "onCreate: " + userId + "------------" + userGuid + "-----------------------" + username);
        //Toast.makeText(objContext, "onCreate", Toast.LENGTH_SHORT).show();
        extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("fromactivation"))
                fromactivation = extras.getBoolean("fromactivation");
            if (extras.containsKey("fromaddsite"))
                fromaddSite = extras.getBoolean("fromaddsite");
            if (extras.containsKey("RollAppdownloadFail"))
                rollappdownload = extras.getBoolean("RollAppdownloadFail");

            isFromTabAct = extras.getBoolean(GlobalStrings.IS_FROM_TASK_TAB_ACT);
            isForceDownload = extras.getBoolean(GlobalStrings.FROM_DASHBOARD);
        }

        syncStatusDataSource = new SyncStatusDataSource(objContext);
        lastSyncDate = syncStatusDataSource.getLastSyncDate(userId, GlobalStrings.SYNC_DATE_TYPE_META);

        if (CheckNetwork.isInternetAvailable(objContext)) {
/*            if (Util.isUrlV16(this)) {
            } else
                new MetaSyncDataTask().execute();*/
            new FormsMetaTask().execute();
        } else {
            CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), 5);
            launchDashboard();
            finish();
        }
        Util.setSharedPreferencesProperty(objContext, "SiteSuccess", "");
    }

    private void showAgreement() {
        Intent intent = new Intent(MetaSyncActivity.this, AgreementActivity.class);
        intent.putExtra("input", "metasync");
//        startActivity(intent);
        Toast.makeText(objContext, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void tasksDownload() {
        if (CheckNetwork.isInternetAvailable(objContext)) {
            new FetchAllTasksAndCOC().execute();
        } else {
            cancelAlertProgress();
//            dismissProgressDialog();
            Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            launchDashboard();
            //finish();
        }
    }

    private class FetchAllTasksAndCOC extends AsyncTask<MediaType, String, Object> {

        @Override
        protected void onPreExecute() {

            //loading already in progress
//            showLoadingProgressDialog();
            updateAlertProgressMsg(getString(R.string.downloading_tasks_wait));
        }

        @Override
        protected String doInBackground(MediaType... params) {
            String response = null;
            Log.i(TAG, "CoC Start Time:" + System.currentTimeMillis());
            lastSyncDate = syncStatusDataSource.getLastSyncDate(userId, GlobalStrings.SYNC_DATE_TYPE_META);
            try {
                if (null != mAquaBlueService) {

                    //fetching all tasks
                    taskDataResponse = mAquaBlueService.getAllTasks(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.get_all_tasks), "0");

                    if (taskDataResponse != null) {
                        if (taskDataResponse.isSuccess()) {
                            TaskDetailsDataSource taskDetailsDataSource
                                    = new TaskDetailsDataSource(objContext);

                            //deleting task tables. Note:- lastSyncDate sent as 0 you'll get all tasks so
                            //no issues to delete tables but if the lastSync date is managed not 0 then
                            //please update table if the data exist
                            taskDetailsDataSource.truncateTaskTables();

                            publishProgress("Processing Tasks data...");
/*                            long taskCount = taskDetailsDataSource
                                    .insertTaskData(taskDataResponse.getData()
                                            .getTaskDataList(), 1);*/
                            long taskCount = taskDetailsDataSource
                                    .storeBulkBindTaskData(taskDataResponse.getData()
                                            .getTaskDataList(), 1);
                            Log.i(TAG, "Tasks stored:" + taskCount);

                            TaskAttachmentsDataSource attachmentsDataSource =
                                    new TaskAttachmentsDataSource(objContext);
/*                            long attachmentCount = attachmentsDataSource.insertAttachmentData(taskDataResponse.getData()
                                    .getAttachmentList(), 1);*/
                            long attachmentCount = attachmentsDataSource
                                    .storeBulkBindAttachmentData(taskDataResponse.getData()
                                            .getAttachmentList(), 1);
                            Log.i(TAG, "Task attachments stored:" + attachmentCount);

                            TaskCommentsDataSource commentsDataSource = new TaskCommentsDataSource(objContext);
/*                            long commentCount = commentsDataSource
                                    .insertTaskComments(taskDataResponse.getData().getCommentList(),
                                            1);*/
                            long commentCount = commentsDataSource
                                    .storeBulkBindTaskComments(taskDataResponse.getData().getCommentList(),
                                            1);
                            Log.i(TAG, "Task comments stored:" + commentCount);
                        }
                    }

                    //commented on 14 Sep, 21 as far we'll not use this COC from now on as COC are handled through web now so commenting. You may uncomment it in case.
/*                    publishProgress(getString(R.string.downloading_coc));
                    mRetCocResponse = mAquaBlueService.v1_getCOCListData(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_get_coclist), userGuid + "", lastSyncDate);*/

                    if (mRetCocResponse != null) {
                        if (mRetCocResponse.isSuccess()) {
                            Log.e("matasync", "doInBackground: SUCCESS");
                            mRetCocData = mRetCocResponse.getData();

                            response = "SUCCESS";

                            CocDetailDataSource cocDetailDataSource = new CocDetailDataSource(objContext);
                            cocDetailDataSource.truncateCocDetail();

                            CocMasterDataSource cocMasterDataSource = new CocMasterDataSource(objContext);
                            cocMasterDataSource.truncateCocMaster();

                            publishProgress("Processing COC data...");
                            saveCoCTaskData();
                        } else {
                            GlobalStrings.responseMessage = mRetCocResponse.getMessage();
                            response = mRetCocResponse.getResponseCode().toString();
                        }
                    } else {
                        Log.e("metasync", "doInBackground: FAILS");
                        response = "RETRY";
                    }
                } else {
                    return response;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "PostMessageTaskforCoC error:" + e.getMessage());
            }

            return response;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            updateAlertProgressMsg(values[0]);
        }

        @Override
        protected void onPostExecute(Object res) {
            super.onPostExecute(res);
            cancelAlertProgress();
            Log.i(TAG, "CoC End Time:" + System.currentTimeMillis());
            launchDashboard();
        }
    }

    private void saveCoCTaskData() {

        if (mRetCocData != null) {

            List<SCocDetails> cocDetailsList = mRetCocData.getCocDetailsList();
            List<SCocMaster> cocMasterList = mRetCocData.getCocMasterList();
            List<CocDataModel.CocMethod> cocMethodsList = mRetCocData.getCocMethodsList();

            if (cocDetailsList != null && cocDetailsList.size() > 0) {
                CocDetailDataSource cocDetailsDataSource = new CocDetailDataSource(objContext);
//                int count = cocDetailsDataSource.storeBulkCoCDetails(cocDetailsList);
                int count = cocDetailsDataSource.storeBulkBindCoCDetails(cocDetailsList);
                Log.i(TAG, "Coc details stored:" + count);
            }

            if (cocMasterList != null && cocMasterList.size() > 0) {
                CocMasterDataSource coc_masterDataSource = new CocMasterDataSource(objContext);
//                int count = coc_masterDataSource.storeBulkCoCMaster(cocMasterList);
                int count = coc_masterDataSource.storeBulkBindCoCMaster(cocMasterList);
                Log.i(TAG, "Coc master stored:" + count);
            }

            if (cocMethodsList != null && cocMethodsList.size() > 0) {
                CMMethodsDataSource cmMethodsDataSource = new CMMethodsDataSource(objContext);
//                int count = cmMethodsDataSource.storeCMMethods(cocMethodsList);
                int count = cmMethodsDataSource.storeBulkBindCMMethods(cocMethodsList);
                Log.i(TAG, "cm coc methods stored:" + count);
            }
        }
    }

    // end of onCreate

    // Handles sign-up request in back ground
    private class MetaSyncDataTask extends AsyncTask<MediaType, String, String> {

        @Override
        protected void onPreExecute() {
            // Init the progress dialog
            showAlertProgress();
            updateAlertProgressMsg(getString(R.string.downloading_forms));
//            showLoadingProgressDialog();
        }// end of onPreExecute

        @Override
        protected String doInBackground(MediaType... params) {
            mStateMachine = STATE_DOWNLOAD_METADATA_USERROLE;
            String response = null, result = null;
            try {
                if (null != mAquaBlueService) {
                    if (result != null) {
                        if (result.equals("SUCCESS") || result.equals("NO_PREF")) {
                            if (fromaddSite) {
                                publishProgress(getString(R.string.processing));
                            } else if (fromactivation) {
                                publishProgress(getString(R.string.please_wait_while_we_setup_app));
                            } else {
                                publishProgress(getString(R.string.downloading_forms));
                            }
                            response = downloadForms();
                        } else {
                            //BAD REQUEST OR UN AUTHORISED USER
                            response = result;
                        }
                    } else {
                        if (fromaddSite) {
                            publishProgress(getString(R.string.processing));
                        } else if (fromactivation) {
                            publishProgress(getString(R.string.please_wait_while_we_setup_app));
                        } else {
                            publishProgress(getString(R.string.downloading_forms));
                        }
                        response = downloadForms();
                    }
                } else {
                    response = "RETRY";
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG, "MetaDataSync doInBackground() Exception:" + e.getMessage());
                return null;
            }

            return response;
        }// end of doInBackground

        @Override
        protected void onProgressUpdate(final String... values) {
            super.onProgressUpdate(values);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateAlertProgressMsg(values[0]);
//                    updateLoadingProgressDialogMsg(values[0]);
                }
            });
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(LOG, " onPostExecute: Result = " + result);
            showResult(result);
        }// end of onPostExecute
    }// end of PostMessageTask

    private class FormsMetaTask extends AsyncTask<Void, String, Integer> {

        @Override
        protected void onPreExecute() {
            showAlertProgress("Downloading Meta Data...");
        }// end of onPreExecute

        @Override
        protected Integer doInBackground(Void... params) {
            int response = 0;
            try {
                if (null != mAquaBlueService) {

                    MetaFormsJsonResponse metaFormsResponse
                            = mAquaBlueService.getMetaJsonFormsData(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_metaformsjson),
                            userId + "",
                            "0");

                    publishProgress(getString(R.string.processing));
                    response = saveMetaForms(metaFormsResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG, "MetaDataSync doInBackground() Exception:" + e.getMessage());
                return 0;
            }

            return response;
        }// end of doInBackground

        @Override
        protected void onProgressUpdate(final String... values) {
            super.onProgressUpdate(values);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateAlertProgressMsg(values[0]);
                }
            });
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(LOG, " onPostExecute: Result = " + result);
            new MetaSyncDataTask().execute();
        }// end of onPostExecute
    }// end of FormsMetaJson Task

    private String downloadForms() {

        NewLocationResponseModel locationRespmodel = null;
        CocMasterDataSource cds = new CocMasterDataSource(objContext);
        UploadLocationList = locationDataSource.collectLocationsToUpload();
        UploadCoCList = cds.getallCoCMasterData();

        String response = null;
        if (UploadLocationList.size() > 0) {
            locationRespmodel = mAquaBlueService.v1_setAddLocationData(getResources().getString(R.string.prod_base_uri),
                    getResources().getString(R.string.prod_add_new_location), UploadLocationList, userGuid);

            if (locationRespmodel != null) {
                if (locationRespmodel.isSuccess()) {

                    locationDataSource.setLocationSyncFlagSlocation((ArrayList<NewClientLocation>) locationRespmodel.getData());

                    //01-04-2018 SYNC COC
                    UploadCoCList = cds.getallCoCMasterData();
                    if (UploadCoCList != null && UploadCoCList.size() > 0) {
                        CocDetailDataSource cocSource = new CocDetailDataSource(objContext);

                        try {
                            JsonCocDetailsObjectModel cocRespmodel
                                    = mAquaBlueService.syncCoc(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_sync_coc), UploadCoCList, userGuid);

                            if (cocRespmodel != null) {
                                if (cocRespmodel.isSuccess()) {
                                    response = "SUCCESS";

                                    for (SyncCocResponseModel item : cocRespmodel.getData()) {
                                        cocSource.updateSyncedCoc(item.getCocDetailsList());
                                    }
                                    response = hitMetaSyncApi();
                                } else {
                                    GlobalStrings.responseMessage = cocRespmodel.getMessage();
                                    response = cocRespmodel.getResponseCode().toString();
                                    return null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "CoC Data Sync Error:" + e.getMessage());
                            return null;
                        }
                    } else {
                        response = hitMetaSyncApi();
                    }
                } else {
                    GlobalStrings.responseMessage = locationRespmodel.getMessage();
                    response = locationRespmodel.getResponseCode().toString();
                }
            }
        } else {
            //01-04-2018 SYNC COC

            if (UploadCoCList != null && UploadCoCList.size() > 0) {
                CocDetailDataSource cocSource = new CocDetailDataSource(objContext);

                try {
                    JsonCocDetailsObjectModel cocRespmodel = mAquaBlueService.syncCoc(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_sync_coc), UploadCoCList, userGuid);

                    if (cocRespmodel != null) {
                        if (cocRespmodel.isSuccess()) {
                            response = "SUCCESS";

                            for (SyncCocResponseModel item : cocRespmodel.getData()) {
                                cocSource.updateSyncedCoc(item.getCocDetailsList());
                            }

                            response = hitMetaSyncApi();
                        } else {
                            GlobalStrings.responseMessage = cocRespmodel.getMessage();
                            return null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "CoC Data Sync Error:" + e.getMessage());
                    return null;
                }
            } else {

                lastSyncDate = syncStatusDataSource.getLastSyncDate(userId, GlobalStrings.SYNC_DATE_TYPE_META);

                if (lastSyncDate == null || lastSyncDate.isEmpty() || isForceDownload)
                    lastSyncDate = "0";

                //added on 27 Oct, 21 to force sync
                lastSyncDate = "0";

                //03-Oct-16 DO META SYNC
                mRetMetaSyncResponse = mAquaBlueService.getMetaSyncData(getResources().getString(R.string.prod_base_uri),
                        getResources().getString(R.string.prod_metadata_sync),
                        userGuid,
                        password, lastSyncDate);

                if (null != mRetMetaSyncResponse) {
                    Log.e("CallsCheck", "downloadForms: Meta sync response called");
                    if (!mRetMetaSyncResponse.isSuccess()) {

                        String code = mRetMetaSyncResponse.getResponseCode().toString();
                        if (code.equals("401")) {
                            setGlobalResponseUnAuthMsg(mRetMetaSyncResponse.getMessage());
                            response = "DEVICE-DISABLE";
                        } else if (code.equals("417")) {
                            response = "USER-SUSPENDED";
                        } else {
                            response = "FALSE";
                            Log.e("Agreement", "downloadForms: mRetMetaSyncResponse.isSuccess() is--- " + mRetMetaSyncResponse.isSuccess() + " " + mRetMetaSyncResponse.getResponseCode() + " " + mRetMetaSyncResponse.getMessage());
                        }
                    } else if (mRetMetaSyncResponse.isSuccess()) {
                        mRetMetaSyncData = mRetMetaSyncResponse.getData();
                        response = "SUCCESS";
                        Log.e("Agreement", "downloadForms: mRetMetaSyncResponse.isSuccess() is--- " + mRetMetaSyncResponse.isSuccess());

                        saveSyncedData();
                    } else {
                        GlobalStrings.responseMessage = mRetMetaSyncResponse.getMessage();
                        response = mRetMetaSyncResponse.getResponseCode().toString();
                    }
                } else {
                    response = "RETRY";
                }
            }
        }

        return response;
    }

    private void setGlobalResponseUnAuthMsg(String message) {
        if (message != null && !message.isEmpty())
            GlobalStrings.responseMessage = message;
        else
            GlobalStrings.responseMessage = getString(R.string.device_disabled_contact_to_activate_device);
    }

    private int saveMetaForms(MetaFormsJsonResponse metaFormsResponse) {

        if (!metaFormsResponse.isSuccess()
                && metaFormsResponse.getMessage()
                .equalsIgnoreCase(HttpStatus.UNAUTHORIZED.toString())) {
            GlobalStrings.responseMessage = mRetMetaSyncResponse.getMessage();
            Util.setDeviceNOT_ACTIVATED((Activity) objContext, username, password);
            return 0;
        }

        SiteMobileAppDataSource siteAppSource = new SiteMobileAppDataSource(objContext);

        if (metaFormsResponse != null && metaFormsResponse.isSuccess()) {

            MetaDataSource metaSource = new MetaDataSource(objContext);
            metaSource.truncateMetaData();
            jsonFormsResponse = metaFormsResponse;

            int count = 0;
            for (MetaFormsJsonResponse.Forms form : metaFormsResponse.getData().getForms()) {
                int rollAppId = form.getFormsDetails().getFormId();
                for (FormTabs formTabs : form.getFormsDetails().getFormTabs()) {
                    count = (int) siteAppSource.storeSiteMobileApp(formTabs, companyid, rollAppId,
                            form.getFormsDetails().getLocationStatusQuery());
                }
            }

            Log.i("SiteMobileAppData", "Data Stored for v16" + count);
            return count;
        }
        return 0;
    }

    private String hitMetaSyncApi() {
        String response;
        lastSyncDate = syncStatusDataSource.getLastSyncDate(userId, GlobalStrings.SYNC_DATE_TYPE_META);

        if (lastSyncDate == null || lastSyncDate.isEmpty() || isForceDownload)
            lastSyncDate = "0";

        //for now do force download by keeping it 0 as there is no provision for LOV's to update
        //with lastSyncDate at backend yet that might have updated.
        lastSyncDate = "0";

        //03-Oct-16 DO META SYNC
        mRetMetaSyncResponse = mAquaBlueService.getMetaSyncData(getResources().getString(R.string.prod_base_uri),
                getResources().getString(R.string.prod_metadata_sync),
                userGuid,
                password, lastSyncDate);

        if (null != mRetMetaSyncResponse) {
            if (mRetMetaSyncResponse.isSuccess()) {
                mRetMetaSyncData = mRetMetaSyncResponse.getData();
                response = "SUCCESS";

                saveSyncedData();
            } else {
                GlobalStrings.responseMessage = mRetMetaSyncResponse.getMessage();
                response = mRetMetaSyncResponse.getResponseCode().toString();
            }
        } else {
            response = "RETRY";
        }

        return response;
    }

    // Method to display the result
    private void showResult(String result) {
        Log.d(LOG, " MetaDataSync result= " + result);
        if (result != null) {
            if (result.equals("SUCCESS")) {
//                dismissProgressDialog();
                tasksDownload();
            } else if (result.equals("FALSE")) {
                showAgreement();
            } else if (result.equals("USER-SUSPENDED")) {
                String msg = getString(R.string.your_acc_was_suspended);
                CustomAlert.showAlert(objContext, msg, getString(R.string.alert));
            } else if (result.equals("DEVICE-DISABLE")) {
                String msg = GlobalStrings.responseMessage;
                CustomAlert.showUnAuthAlert(objContext, msg, getString(R.string.alert));
            } else if (result.equals(HttpStatus.NOT_ACCEPTABLE.toString())) {
//                dismissProgressDialog();
                cancelAlertProgress();
                Toast.makeText(this, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                //  launchDashboard();
                Util.setLogout(MetaSyncActivity.this);
            } else if (result.equals("RETRY")) {
//                dismissProgressDialog();
                cancelAlertProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
                launchDashboard();
            } else if (result.equals(HttpStatus.LOCKED.toString()) ||
                    result.equals(HttpStatus.NOT_FOUND.toString()) ||
                    result.equals(HttpStatus.EXPECTATION_FAILED.toString())
                    || result.equals(HttpStatus.UNAUTHORIZED.toString())
            ) {
//                dismissProgressDialog();
                cancelAlertProgress();
                GlobalStrings.responseMessage = mRetMetaSyncResponse.getMessage();
                //Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                Util.setDeviceNOT_ACTIVATED((Activity) objContext, username, password);
            } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
//                dismissProgressDialog();
                cancelAlertProgress();
                Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                launchDashboard();
            } else if (result.equalsIgnoreCase(HttpStatus.CONFLICT.toString())) {
//                dismissProgressDialog();
                cancelAlertProgress();
                Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                Util.setDeviceNOT_ACTIVATED((Activity) objContext, username, password);
            }
        } else {
//            dismissProgressDialog();
            cancelAlertProgress();
            Toast.makeText(getApplicationContext(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
            launchDashboard();
        }
    }// end of showResult

    void saveSyncedData() {
        Log.i(TAG, "MetaSync Save Start Time:" + (System.currentTimeMillis()));

/*        if (!Util.isUrlV16(this)) {
            MetaDataSource metaSource = new MetaDataSource(objContext);
            if (mRetMetaSyncData.isForceReset()) {
                metaSource.truncateMetaData();
            }
        }*/

        syncStatusDataSource.insertLastSyncDate(userId, mRetMetaSyncData.getLastSyncDate(),
                GlobalStrings.SYNC_DATE_TYPE_META);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateAlertProgressMsg("Processing data...");
            }
        });

        List<LocationProfilePictures> locProfilePicturesList = null;
        int count = 0;
        if (mRetMetaSyncData != null) {
            mRetSiteList = mRetMetaSyncData.getSite();
            mRetLocationList = mRetMetaSyncData.getLocation();
            mRetMobileAppList = mRetMetaSyncData.getMobileApp();
            mRetSiteAppList = mRetMetaSyncData.getSiteMobileApp();
            mRetMetaDataList = mRetMetaSyncData.getMetaData();
            mRetLovItemList = mRetMetaSyncData.getLovItem();
            statusForGuid = mRetMetaSyncData.getStatusForGuid();
            mRetDefaultValueList = mRetMetaSyncData.getDefaultValues();
            mRetSiteUserRoleList = mRetMetaSyncData.getSiteUserRole();
            mRetLovList = mRetMetaSyncData.getLovList();
            mRetLocAttributeList = mRetMetaSyncData.getLocationAttribute();
            locProfilePicturesList = mRetMetaSyncData.getLocationProfilePictures();
        }

        SiteDataSource site = new SiteDataSource(objContext);
        LocationAttributeDataSource laDS = new LocationAttributeDataSource(objContext);
        LocationProfilePictureDataSource pictureDataSource
                = new LocationProfilePictureDataSource(this);

        if (locProfilePicturesList != null) {
            count = pictureDataSource.insertLocProfilePictures(locProfilePicturesList);
            Log.i(TAG, count + " Loc Profile Pictures Stored");
            count = 0;
        }

        if (mRetSiteList != null) {
            count = site.storeBulkSiteList(mRetSiteList);
            Log.i(TAG, count + " Sites Stored");
            count = 0;
        }

        if (mRetLocAttributeList != null) {
            count = laDS.storeBulkLocationAttributeList(mRetLocAttributeList);
            Log.i(TAG, count + " Location Attributes Stored");
            count = 0;
        }

        if (mRetMetaSyncData.getMetaDataAttributes() != null
                && !mRetMetaSyncData.getMetaDataAttributes().isEmpty()) {
            MetaDataAttributesDataSource attributesDataSource
                    = new MetaDataAttributesDataSource(this);
            attributesDataSource.insertMetadataAttributes(mRetMetaSyncData.getMetaDataAttributes());
            Log.d("Meta attributes stored", "");
        }

/*
        if (mRetMetaSyncData.getSiteFormFields() != null
                && !mRetMetaSyncData.getSiteFormFields().isEmpty()) {
            SiteFormFieldsSource siteFormFieldsSource
                    = new SiteFormFieldsSource(this);
            siteFormFieldsSource.insertSiteFormFields(mRetMetaSyncData.getSiteFormFields());
            Log.d("Site form field stored", "");
        }
*/

        UserDataSource userD = new UserDataSource(objContext);
        User user = userD.getUser(username);
        int userID = 0;
        if (user != null) {
            userID = user.getUserID();
        } else {
            userID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID));
        }

        SiteUserRoleDataSource siteUserRole = new SiteUserRoleDataSource(objContext);
        //siteUserRole.insertSiteUserRoleFromSite(mRetSiteList, userID);
        if (mRetSiteUserRoleList != null) {
//            count = siteUserRole.bulkinsertSiteUserRole(mRetSiteUserRoleList);
            count = siteUserRole.storeBulkSiteUserRole(mRetSiteUserRoleList);
            Log.i(TAG, count + " SiteUserRole Stored");
        }

        if (mRetLovList != null && !mRetLovList.isEmpty()) {
            LovDataSource lovDataSource = new LovDataSource(objContext);
//            count = lovDataSource.storebulkLovlist(mRetLovList);
            long ct = lovDataSource.storeBulkBindLovList(mRetLovList);
            Log.i(TAG, ct + " Lovs Stored");
        }

//        loc.storeLocationList(mRetLocationList);

        //08-Jun-16 Bulk Insert

        if (mRetLocationList != null) {
//            count = loc.storeBulkLocationList(mRetLocationList);
            Log.i(TAG, locationDataSource.storeBulkBindLocationList(mRetLocationList)
                    + " Locations Stored");
            count = 0;
        }

        MobileAppDataSource mob = new MobileAppDataSource(objContext);
        // mob.insertMobileAppList(mRetMobileAppList);

        if (mRetMobileAppList != null) {
//            count = mob.storeBulkMobileAppList(mRetMobileAppList);
            Log.i(TAG, mob.storeBulkBindMobileAppList(mRetMobileAppList) + " Mobile App Stored");
            count = 0;
        }

        //this array will come in v16 only
        if (mRetMetaSyncData.getFormSites() != null
                && mRetMetaSyncData.getFormSites().size() > 0) {
            FormSitesDataSource formSitesDataSource = new FormSitesDataSource(this);
            Log.i(TAG, formSitesDataSource.storeBulkMobileAppList(mRetMetaSyncData.getFormSites(), false) + " Mobile App Stored");
        }

        //updating formSites cols upon formSites table items are inserted from above loop
        if (jsonFormsResponse != null) {
            for (MetaFormsJsonResponse.Forms form : jsonFormsResponse.getData().getForms()) {
                FormSitesDataSource formSitesDataSource = new FormSitesDataSource(this);
                formSitesDataSource.updateAppTypeAndApproval(form.getFormsDetails());
            }
        }

        SiteMobileAppDataSource siteApp = new SiteMobileAppDataSource(objContext);
//        count = (int) siteApp.insertSiteMobileApp(mRetSiteAppList, companyid);
        if (mRetSiteAppList != null && mRetSiteAppList.size() > 0)
            count = (int) siteApp.storeBulkBindSiteMobileApp(mRetSiteAppList, companyid);

//        count = siteApp.storeBulkSiteMobileAppList(mRetSiteAppList, companyid);
        Log.i(TAG, count + " Site Mobile App Stored");
        count = 0;

        LovDataSource lov = new LovDataSource(objContext);
//        lov.storeLovItemsListWithStmt(mRetLovItemList);
//        lov.storeBulkLovItems(mRetLovItemList);
        if (mRetLovItemList != null && mRetLovItemList.size() > 0)
            lov.storeBulkBindLovItems(mRetLovItemList);

        MetaDataSource metaSource = new MetaDataSource(objContext);
        if (mRetMetaDataList != null && mRetMetaDataList.size() > 0)
            metaSource.storeMetaDataArray(mRetMetaDataList);

//        count = metaSource.storeBulkMetaDataList(mRetMetaDataList);
//        Log.i(TAG, count + " Rows of MetaData Stored");

        DefaultValueDataSource dv = new DefaultValueDataSource(objContext);
        if (mRetDefaultValueList != null && mRetDefaultValueList.size() > 0) {
            long defVal = dv.saveDefaultValueList(mRetDefaultValueList);
            Log.i(TAG, "Default Value stored :" + defVal);
        }

        Log.i(TAG, "MetaSync Save End Time:" + (System.currentTimeMillis()));
    }

    void launchDashboard() {
        String userAppType = Util.getSharedPreferencesProperty(this, GlobalStrings.USERAPPTYPE);
        if (userAppType != null) {
            ScreenReso.isProjectUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_PROJECT);
            ScreenReso.isLimitedUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_LIMITED);
        }

        if (fromactivation) {
            String userid = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID);
            String isFirsttime = Util.getSharedPreferencesProperty(objContext, userid);

            if (isFirsttime == null) {
                LayoutInflater factory = LayoutInflater.from(objContext);
                final View DialogView = factory.inflate(R.layout.welcomealert, null);
                final AlertDialog Dialog = new AlertDialog.Builder(objContext).create();
                Dialog.setCanceledOnTouchOutside(false);
                Dialog.setView(DialogView);
                Button s = DialogView.findViewById(R.id.btn_yes);
                s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Dialog.dismiss();
                        Intent applicationIntent = new Intent(objContext, RollAppFormActivity.class);
                        startActivity(applicationIntent);
                        //    finish();

                    }
                });
                Dialog.show();

            }

        } else if (fromaddSite) {
            Intent applicationIntent = new Intent(objContext, SiteActivity.class);
            applicationIntent.putExtra("selfsignupflag", true);
            applicationIntent.putExtra("fromaddsite", fromaddSite);
            startActivity(applicationIntent);
            finish();
        } else if (rollappdownload && DOWNLOAD_FORM_REQUEST_COUNT < 3) {
            Intent applicationIntent = new Intent(objContext, RollAppFormActivity.class);
            startActivity(applicationIntent);
        } else if (isFromTabAct) {
            Intent intent = new Intent();
            intent.putExtra(GlobalStrings.IS_TASK_REFRESH, true);
            setResult(RESULT_OK, intent);
            finish();
        } else if (ScreenReso.isProjectUser || ScreenReso.isLimitedUser) {
            if (isTaskRoot()) {
                Intent applicationIntent = new Intent(objContext, AllSitesActivity.class);
                applicationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(applicationIntent);
            }
            finish();
        } else {
            DOWNLOAD_FORM_REQUEST_COUNT = 0;

            if (isTaskRoot()) {
                Intent applicationIntent = new Intent(objContext, HomeScreenActivity.class);
//                Intent applicationIntent = new Intent(objContext, MainDrawerActivity.class);
                applicationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(applicationIntent);
            }
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        cancelAlertProgress();
        super.onDestroy();
    }
}
// end of SignUpActivity
    
