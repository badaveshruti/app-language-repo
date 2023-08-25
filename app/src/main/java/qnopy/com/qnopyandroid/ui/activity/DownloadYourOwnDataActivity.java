package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.DownloadEventDataResponse;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.DownloadEventDataModel;
import qnopy.com.qnopyandroid.requestmodel.FieldDataForEventDownload;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FieldDataSyncStaging;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.services.DataSyncService;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by shantanu on 6/26/17.
 */

@AndroidEntryPoint
public class DownloadYourOwnDataActivity extends ProgressDialogActivity implements OnTaskCompleted {
    private static final String LOG = DownloadYourOwnDataActivity.class.getName();
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    Context objContext = null;
    DownloadEventDataModel mRetDownloadData = null;
    ArrayList<FieldDataForEventDownload> mRetDownloadEventDataList = null;
    DownloadEventDataResponse dataWithAttachmentResponse = null;
    List<FieldDataForEventDownload> mRetFieldDataList = null;

    private int siteid, parentappid = 0;
    boolean isLocationsAvailableToSync = false, isAttachmentsAvailableToSync = false, isFieldDataAvailableToSync = false;

    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;

    Bundle extras;
    private String deviceid = null;
    private String username, password, userguid = null, sitename, appName;
    private int current_eventID = 0, userID = 0;
    boolean SHOW_REPORT = false;

    private String eventIds = "";

    @Inject
    FieldDataSource fieldDataSource;
    @Inject
    AttachmentDataSource attachmentDataSource;
    @Inject
    LocationDataSource locationDataSource;
    @Inject
    EventDataSource eventDataSource;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handlerForUI = new Handler(Looper.getMainLooper());
    private ArrayList<String> eventIdList = new ArrayList<>();

    //loop to decrement recursively download method for eventIds
    int count = 0;
    private boolean isDemoSiteDataDownload;

    public int getCurrent_eventID() {
        return current_eventID;
    }

    public void setCurrent_eventID(int current_eventID) {
        this.current_eventID = current_eventID;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objContext = this;

        extras = getIntent().getExtras();

        if (extras != null) {

            eventIds = extras.getString(GlobalStrings.KEY_EVENT_IDS, "");
            isDemoSiteDataDownload = extras.getBoolean(GlobalStrings.KEY_DEMO_SITES, false);

            setCurrent_eventID(extras.getInt("EVENTID", 0));
            setParentappid(extras.getInt("PARENTAPPID", 0));
            setSiteid(extras.getInt("SITEID", 0));

            if (extras.containsKey("REPORT")) {
                sitename = extras.getString("SITE_NAME");
                appName = extras.getString("APP_NAME");

                SHOW_REPORT = true;
            }
        }

        deviceid = Util.getSharedPreferencesProperty(objContext, GlobalStrings.SESSION_DEVICEID);
        username = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD);
        userguid = Util.getSharedPreferencesProperty(objContext, username);
        userID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID));

        if (!eventIds.isEmpty())
            eventIdList = new ArrayList<>(Util.splitStringToArray(",",
                    eventIds));

        //sync will decide what to process either download all data or single event data
        SyncData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);
            if (dataSynced) {
                if (CheckNetwork.isInternetAvailable(objContext)) {
                    downloadEventsData();
                } else {
                    CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG);
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    void SyncData() {
        Log.i(TAG, "SyncData() IN time:" + System.currentTimeMillis());

        if (CheckNetwork.isInternetAvailable(objContext)) {

            ArrayList<DEvent> eventList = eventDataSource
                    .getClientGeneratedEventIDs(objContext);
            int count = eventList.size();
            if (count > 0) {
                EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(DownloadYourOwnDataActivity.this,
                        null, username,
                        password, true);
                eventHandler.execute();
            } else {
                boolean serviceRunning = isMyServiceRunning(DataSyncService.class);
                Log.i(TAG, "Service is Already Running Status:" + serviceRunning);
                if (!serviceRunning) {
                    uploadFieldData();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Service already running in the background, please try after sometime.",
                            Toast.LENGTH_LONG).show();
                }
            }

        } else {
            CustomToast.showToast((Activity) objContext,
                    getString(R.string.bad_internet_connectivity), 10);
        }
        Log.i(TAG, "SyncData() OUT time:" + System.currentTimeMillis());
    }

    @Override
    public void onTaskCompleted(Object obj) {

        FieldDataSource fieldData = new FieldDataSource(objContext);
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(objContext);

        if (obj != null) {

            if (obj instanceof String) {

                String result = (String) obj;

                if (result.equals("SUCCESS")) {
                    Event event = eventDataSource.getEvent(getParentappid(), getSiteid(), userID, deviceid);
                    int currentEventID = event.getEventId();
                    setCurrent_eventID(currentEventID);
                    uploadFieldData();
                } else {
                    Log.e(TAG, "onTaskCompleted() event not generated");
                    Toast.makeText(objContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
                }
            } else if (obj instanceof EventResponseModel) {

                EventResponseModel result = (EventResponseModel) obj;
                GlobalStrings.responseMessage = result.getMessage();

                if (result.isSuccess()) {

                    int serverGenEventID = result.getData().getEventId();

                    Log.i(TAG, "Event ID From Server:" + serverGenEventID);

                    fieldData.updateEventID(getCurrent_eventID(), serverGenEventID);
                    attachDataSrc.updateEventID(getCurrent_eventID(), serverGenEventID);
                    eventDataSource.updateEventID(getCurrent_eventID(), result);
                    new SampleMapTagDataSource(objContext).updateEventID_SampleMapTag(getCurrent_eventID() + "", serverGenEventID + "");
                    setCurrent_eventID(serverGenEventID);

                    if (CheckNetwork.isInternetAvailable(objContext)) {
                        uploadFieldData();
                    } else {
                        CustomToast.showToast((Activity) objContext,
                                getString(R.string.bad_internet_connectivity), 5);
                    }
                } else {
                    if (result.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                        //04-Mar-16
                        Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                    if ((result.getResponseCode() == HttpStatus.NOT_FOUND) || (result.getResponseCode() == HttpStatus.LOCKED)) {
                        Util.setDeviceNOT_ACTIVATED((Activity) objContext, username, password);
//                    Toast.makeText(context,GlobalStrings.responseMessage,Toast.LENGTH_LONG).show();

                    }
                    if (result.getResponseCode() == HttpStatus.BAD_REQUEST) {
                        Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();

                    }
                    if ((result.getResponseCode() == HttpStatus.EXPECTATION_FAILED) ||
                            (result.getResponseCode() == HttpStatus.UNAUTHORIZED) ||
                            (result.getResponseCode() == HttpStatus.CONFLICT)
                    ) {
                        //02-AuG-16
                        Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                        Util.setLogout((Activity) objContext);
                    }
                }

            }

        } else {
            Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            finish();
        }

        Log.i(TAG, "onTaskCompleted() OUT time:" + System.currentTimeMillis());
    }

    @Override
    public void onTaskCompleted() {

    }

    @Override
    public void setGeneratedEventID(int id) {

    }

    @Override
    public void setGeneratedEventID(Object obj) {

    }

    public void uploadFieldData() {

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldDataSource.checkAndUpdateClientEventInFieldData();
        fieldDataSource.checkAndUpdateClientEventInAttachmentData();

        isLocationsAvailableToSync = locationDataSource.isOfflineLocationsAvailable();//24-Mar-17
        isFieldDataAvailableToSync = fieldDataSource.isFieldDataAvailableToSync();
        isAttachmentsAvailableToSync = attachmentDataSource.attachmentsAvailableToSync();

        if (!isLocationsAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            if (CheckNetwork.isInternetAvailable(objContext)) {
                downloadEventsData();
            } else {
                CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG);
                finish();
            }
        } else {
            Log.i(TAG, "uploadFieldData() Upload Field Data Called:" + System.currentTimeMillis());
            Intent dataUpload = new Intent(this, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", getCurrent_eventID());
            startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        }
//        Log.i(TAG, "Upload Field Data arguments:username=" + getUsername() + ",password=" + getPassword() + ",EventID=" + getEventID());

        Log.i(TAG, "Upload Field Data Call End:" + System.currentTimeMillis());
    }

    private void downloadEventsData() {

        if (eventIdList.size() > 0) {
            if (eventIds.equals("0"))
                showAlertProgress("Downloading data for all events. Please wait..");
            else if (isDemoSiteDataDownload)
                showAlertProgress("Downloading data for demo site event " + (count + 1) + "/" + eventIdList.size());
            else
                showAlertProgress("Downloading data for event " + (count + 1) + "/" + eventIdList.size());
            downloadAllEventsData();
        } else
            new TaskDownloadEventData().execute();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void downloadAllEventsData() {
        executor.execute(() -> {
            if (!eventIds.equals("0"))
                handlerForUI.post(() -> {
                    if (isDemoSiteDataDownload)
                        updateAlertProgressMsg("Downloading data for demo site event " + (count + 1) + "/" + eventIdList.size());
                    else
                        updateAlertProgressMsg("Downloading data for event " + (count + 1) + "/" + eventIdList.size());
                });

            String response = null;

            try {
                if (mAquaBlueService != null) {

                    long lastSync = Util.getSharedPrefLongProperty(this, GlobalStrings.ALL_EVENT_DATA_LAST_SYNC);

                    long lastSyncDate = 0;

                    if (lastSync > 0)
                        lastSyncDate = lastSync;

                    current_eventID = Integer.parseInt(eventIdList.get(count));//this value is used in set reordering while saving event data

                    if (current_eventID > 0)
                        lastSyncDate = 0;// 05/01/2023 as we are not managing lastsync date on server in case of single event download

                    dataWithAttachmentResponse = mAquaBlueService.downloadEventDataWithAttachments(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_download_active_event_data), userguid, deviceid,
                            current_eventID, lastSyncDate);

                    if (dataWithAttachmentResponse != null) {
                        if (dataWithAttachmentResponse.isSuccess()) {
                            if (dataWithAttachmentResponse.getData().getLastSyncDate() != null
                                    && !dataWithAttachmentResponse.getData().getLastSyncDate().isEmpty()) {
                                long syncDate = Long.parseLong(dataWithAttachmentResponse.getData().getLastSyncDate());
                                Util.setSharedPreferencesProperty(this,
                                        GlobalStrings.ALL_EVENT_DATA_LAST_SYNC, syncDate);
                            }

                            saveEventData();
                            response = "SUCCESS";
                        } else {
                            GlobalStrings.responseMessage = dataWithAttachmentResponse.getMessage();
                            response = dataWithAttachmentResponse.getResponseCode().toString();
                        }
                    } else {
                        response = "RETRY";
                    }
                } else {
                    response = "RETRY";
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error:" + e.getMessage());
            }

            showResult(response);
            count++;
        });
    }

    private void clearEventDataOrTable() {
        if (eventIds.equals("0"))
            fieldDataSource.truncateFieldTable();
        else
            fieldDataSource.deleteEventData(current_eventID + "");
    }

    // 05/01/2023 in case of single event data download no need to manage lastsync date we'll receive all data as discussed
    private class TaskDownloadEventData extends AsyncTask<MediaType, Void, String> {

        @Override
        protected void onPreExecute() {
            showAlertProgress("Checking to see if there is data for this event..");
        }// end of onPreExecute

        @Override
        protected String doInBackground(MediaType... params) {
            String response = null;

            try {
                if (null != mAquaBlueService) {

                    long lastsyncdate = 0;
                    int eventid = getCurrent_eventID();

/*
                    mRetDownloadResponse = mAquaBlueService.downloadActiveEventDataForUser(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_download_active_event_data), userguid, deviceid, eventid, lastsyncdate);
*/

                    dataWithAttachmentResponse = mAquaBlueService.downloadEventDataWithAttachments(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_download_active_event_data), userguid, deviceid, eventid, lastsyncdate);

                    if (dataWithAttachmentResponse != null) {
                        if (dataWithAttachmentResponse.isSuccess()) {
                            saveEventData();
                            response = "SUCCESS";
                        } else {
                            GlobalStrings.responseMessage = dataWithAttachmentResponse.getMessage();
                            response = dataWithAttachmentResponse.getResponseCode().toString();
                        }
                    } else {
                        response = "RETRY";
                    }
                } else {
                    response = "RETRY";
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error:" + e.getMessage());
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            showResult(s);
        }
    }

    private void showResult(String result) {

        String resp_msg = getString(R.string.download_data_fail_msg);
        if (result != null) {
            if (result.equals("SUCCESS")) {
                resp_msg = getString(R.string.download_data_success_msg);
            } else if ((result.equals(HttpStatus.EXPECTATION_FAILED.toString())) ||
                    (result.equals(HttpStatus.CONFLICT.toString()))) {
                //02-AuG-16
                Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                // Util.setLogout((Activity) objContext);
                resp_msg = GlobalStrings.responseMessage;
            } else if (result.equals(HttpStatus.UNAUTHORIZED.toString())) {
                Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                Util.setLogout((Activity) objContext);
                //  resp_msg = GlobalStrings.responseMessage;
            } else if (result.equals(HttpStatus.NOT_FOUND.toString())) {
                Toast.makeText(objContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                Util.setLogout((Activity) objContext);
                //  resp_msg = GlobalStrings.responseMessage;
            } else {
                resp_msg = getString(R.string.download_data_fail_msg);
            }
        }

        if (eventIdList.size() > 0) {
            if (count == eventIdList.size() - 1) {
                cancelAlertProgress();
                String finalResp_msg = resp_msg;
                handlerForUI.post(() -> {
                    Toast.makeText(this, finalResp_msg, Toast.LENGTH_SHORT).show();
                });
                finish();
            } else
                downloadAllEventsData();
        } else {
            cancelAlertProgress();
            showAlert(objContext, getString(R.string.downloadYourData), resp_msg);
        }
    }

    void showAlert(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(false)
                .setMessage(msg)
                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (SHOW_REPORT) {
                            Intent i = new Intent(objContext, MobileReportActivity.class);
                            i.putExtra("SITE_NAME", sitename);
                            i.putExtra("SITE_ID", getSiteid() + "");
                            i.putExtra("EVENT_ID", getCurrent_eventID() + "");
                            i.putExtra("APP_NAME", appName);
//                            startActivity(i);
                        }
                        setResult(Activity.RESULT_OK);//result is set to let new event screen know to move to location screen
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveEventData() {
        if (dataWithAttachmentResponse != null) {

            clearEventDataOrTable();

            FieldDataSource fielddatasource = new FieldDataSource(objContext);
            AttachmentDataSource attachmentDataSource = new AttachmentDataSource(objContext);

            //commented on 22/10/22 as we r using uuid now so we can insert downloaded data over
            //other data. Just we need to reorder the sets so that we can see all data
/*            TempFieldDataSource temp_fielddatasource = new TempFieldDataSource(objContext);
            //03-Jul-17 COLLECT FIELD-DATA FOR EVENT TO MOVE IN TEMP TABLE
            List<FieldData> fieldDataListToMove = fielddatasource.getListForEventDownload(getCurrent_eventID());
            int res = temp_fielddatasource.moveFieldDataListinTemp(fieldDataListToMove, getCurrent_eventID());
            Log.i(TAG, "move Field data list in Temp result :" + res);

            //03-Jul-17 TRUNCATE DATA FROM D_FIELDDATA
            int ret = fielddatasource.deleteFieldDataforEvent(getCurrent_eventID());
            Log.i(TAG, "Delete FieldData for user and event:" + ret);*/

//            attachmentDataSource.deleteAttachmentsForEvent(getCurrent_eventID());

            if (dataWithAttachmentResponse != null)
                mRetDownloadEventDataList = dataWithAttachmentResponse.getData().getFieldDataList();

            //saving dfield data
            if (mRetDownloadEventDataList != null && mRetDownloadEventDataList.size() > 0) {
                if (eventIds.equals("0")) {//case when all data download
                    long cnt = fielddatasource.storeBulkBindFieldData(mRetDownloadEventDataList);
                    Log.i(TAG, "insert FieldData List for Event result:" + cnt);
                } else {
                    for (FieldDataForEventDownload fieldDataForEventDownload : mRetDownloadEventDataList) {
                        int cnt = fielddatasource.insertFieldDataListforUser(fieldDataForEventDownload,
                                fieldDataForEventDownload.getUserId() + "");
                        Log.i(TAG, "insert FieldData List for Event result:" + cnt);
                    }
                }
            }

            ArrayList<DownloadEventDataResponse.FieldDataAttachment> listAttachment
                    = new ArrayList<>();
            //saving attachment
            if (dataWithAttachmentResponse != null) {
                listAttachment
                        = dataWithAttachmentResponse.getData().getFieldDataAttachmentList();

                if (listAttachment != null) {

                    if (eventIds.equals("0")) {
                        attachmentDataSource.storeBulkAttachments(listAttachment);
                    } else {
                        for (DownloadEventDataResponse.FieldDataAttachment attachment
                                : listAttachment) {
                            attachmentDataSource.insertDownloadedAttachmentData(attachment,
                                    false);
                        }
                    }
                }
            }

            if (mRetDownloadEventDataList.size() > 0 ||
                    (listAttachment != null ? listAttachment.size() : 0) > 0)
                reorderSetsForEventData();
        }
    }

    //added on 22/10/2022 to reorder fieldData and attachments
    private void reorderSetsForEventData() {
        ArrayList<FieldDataSyncStaging> allFieldDataList
                = fieldDataSource.getAllFieldDataForReordering(current_eventID);

        HashMap<String, Integer> mapSetIndex = new HashMap<>();

        for (FieldDataSyncStaging fieldData :
                allFieldDataList) {

            String key = fieldData.getEventId() + "|" + fieldData.getMobileAppId() + "|" + fieldData.getLocationId();

            int setIndex = 0;

            if (mapSetIndex.containsKey(key)) {
                if (mapSetIndex.get(key) != null)
                    setIndex = mapSetIndex.get(key);
            }

            setIndex += 1;

            if (fieldData.getExtField1() != null
                    && setIndex == Integer.parseInt(fieldData.getExtField1())) {
                fieldDataSource.updateFieldDataSet(fieldData, setIndex);
                attachmentDataSource.updateAttachmentSet(fieldData, setIndex);
            }

            mapSetIndex.put(key, setIndex);
        }
    }

    public int getSiteid() {
        return siteid;
    }

    public void setSiteid(int siteid) {
        this.siteid = siteid;
    }

    public int getParentappid() {
        return parentappid;
    }

    public void setParentappid(int parentappid) {
        this.parentappid = parentappid;
    }
}
