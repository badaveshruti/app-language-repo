package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Attachment;
import qnopy.com.qnopyandroid.clientmodel.DataSyncStatus;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocDetailDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.DataSyncDateSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.requestmodel.DAttachment;
import qnopy.com.qnopyandroid.requestmodel.EventFieldData;
import qnopy.com.qnopyandroid.responsemodel.AttachmentResponseModel;
import qnopy.com.qnopyandroid.responsemodel.CocObjectModel;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FieldDataSyncStaging;
import qnopy.com.qnopyandroid.responsemodel.FielddataResponseModel;
import qnopy.com.qnopyandroid.responsemodel.JsonCocDetailsObjectModel;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.SyncCocResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/*
 * Activity to handle user registration process
 * Handles the network request in background using async task
 */
public class DataSyncActivity extends ProgressDialogActivity {
    private static final String LOG = DataSyncActivity.class.getName();
    public String filePath = "/data/data/com.aquablue.client/QuizCD.txt";
    public String filename = "QuizCD.txt";
    Button mBtnDLMetaData, mBtnClear;
    EditText mEditText = null;
    int mStateMachine = 0;
    String strResponseCode = null;
    public static Handler mHandler;

    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    // Jackson Serializer
    ObjectMapper mapper = new ObjectMapper();

    Boolean mRetVal = false;
    public static String mUserid = null;
    String username = null;
    String password = null;
    String guid = null;
    String siteid = null;
    int eventID = 0;
    boolean closeEvent = false;
    boolean eventClosed = false;
    String eventEndDate = 0 + "";
    public final int STATE_MAX = 1;

    public final int STATE_UPLOAD_LOCATION = 0;
    public final int STATE_UPLOAD_EVENTDATA = 1;
    public final int STATE_UPLOAD_EVENTFILE = 2;
    public final int STATE_SYNC_WORK_ORDER = 3;
    //    boolean dataSynced = false;
//    boolean dataSyncFailedAtLeastOnce = false;

    public static int mState = 0;

    //    public List<EventFieldData> eventList = null;
    public List<NewClientLocation> UploadLocationList = null;
    public List<CocObjectModel> UploadCoCList = null;
    //    public List<WorkOrderTask> UploadWorkOrderTaskList = null;
    List<Attachment> attachList = null;
    boolean isLocationsAvailableToSync = false, isCoCAvailableToSync = false,
            isAttachmentsAvailableToSync = false,
            isFieldDataAvailableToSync = false;
    //            isWorkkOrderAvailableToSync=false;
    FieldDataSource fieldSource = null;
    LocationDataSource LDSource = null;
    AttachmentDataSource attachDataSource = null;
    CocMasterDataSource cocDataSource = null;
    Context mContext = this;
    private boolean isResyncData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.home_selected);
        Bundle extras = getIntent().getExtras();
        mContext = this;
        username = extras.getString("USER_NAME");
        password = extras.getString("PASS");
        eventID = extras.getInt("EVENT_ID");
        siteid = Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_SITEID);
        isResyncData = extras.getBoolean(GlobalStrings.IS_RESYNC_DATA);

        if (extras.containsKey("CLOSE_EVENT")) {
            closeEvent = extras.getBoolean("CLOSE_EVENT");
        }

        LDSource = new LocationDataSource(mContext);
        fieldSource = new FieldDataSource(mContext);
        attachDataSource = new AttachmentDataSource(mContext);

        isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();
        isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();

        cocDataSource = new CocMasterDataSource(mContext);

        isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        new PostMessageTask().execute();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
                String msg = (String) message.obj;
                CustomToast.showToast((Activity) mContext, msg, 5);
            }
        };
    }// end of onCreate

    // Handles sign-up request in back ground
    private class PostMessageTask extends AsyncTask<MediaType, String, String> {

        @Override
        protected void onPreExecute() {
            showAlertProgress(mContext.getString(R.string.syncing_data));
            // Init the progress dialog
//            showLoadingProgressDialog();
//            showProgressDialog(getString(R.string.data_sync_in_progress));
        }// end of onPreExecute

        @Override
        protected String doInBackground(MediaType... params) {
            String response = null;
            HashMap<String, DataSyncStatus> syncStatusMap = new HashMap<>();

            try {
                // Get handle to HTTP service
                guid = Util.getSharedPreferencesProperty(mContext, username);
                FielddataResponseModel fDataRespmodel = null;
                FielddataResponseModel fieldDataInsertRes = null;
                FielddataResponseModel fieldDataUpdateRes = null;
                NewLocationResponseModel locationRespmodel = null;
                JsonCocDetailsObjectModel cocRespmodel = null;
                String locationID = "0";
                String locationName;
                DataSyncDateSource syncDateSource = new DataSyncDateSource(mContext);

                Log.d(LOG, " doInBackground: State = " + mStateMachine);

                if (isLocationsAvailableToSync) {
                    mStateMachine = STATE_UPLOAD_LOCATION;//15-May-16 IF LOCATION FOR SYNC IS AVAILABLE
                } else if (isCoCAvailableToSync) {
                    mStateMachine = STATE_SYNC_WORK_ORDER;//22-March-18 IF COC FOR SYNC IS AVAILABLE
                } else if (isFieldDataAvailableToSync || isResyncData) {
                    mStateMachine = STATE_UPLOAD_EVENTDATA;//30-Dec-15 IF DATA FOR SYNC IS AVAILABLE
                } else {
                    mStateMachine = STATE_UPLOAD_EVENTFILE;//30-Dec-15 IF DATA FOR SYNC IS NOT AVAILABLE
                }

                switch (mStateMachine) {

                    case STATE_UPLOAD_LOCATION: {

                        try {

                            UploadLocationList = LDSource.collectLocationsToUpload();

                            locationRespmodel = mAquaBlueService.v1_setAddLocationData(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_add_new_location), UploadLocationList, guid);

                            if (locationRespmodel != null) {
                                if (!locationRespmodel.isSuccess()) { // AGREEMENT CHECK

                                    String code = locationRespmodel.getResponseCode().toString();

                                    if (code.equals("401")) {
                                        setGlobalResponseUnAuthMsg(locationRespmodel.getMessage());
                                        response = "DEVICE-DISABLE";
                                    } else if (code.equals("417")) {
                                        response = "USER-SUSPENDED";
                                    } else {
                                        Log.e("disableCheck", "doInBackground: " + locationRespmodel.getMessage().toString());
                                        response = "FALSE";
                                    }
                                } else if (locationRespmodel.isSuccess()) {
                                    response = "SUCCESS";
                                    LDSource.setLocationSyncFlagSlocation((ArrayList<NewClientLocation>) locationRespmodel.getData());
                                } else {
//                                    dataSyncFailedAtLeastOnce = true;
                                    GlobalStrings.responseMessage = locationRespmodel.getMessage();
                                    response = locationRespmodel.getResponseCode().toString();
                                    return response;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Location Data Synch Error:" + e.getMessage());
                            return null;
                        }

                        isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
                        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
                        if (!closeEvent && (!isAttachmentsAvailableToSync && !isFieldDataAvailableToSync)) {
                            return response;
                        }
                    }

                    case STATE_SYNC_WORK_ORDER: {
                        try {
                            cocDataSource = new CocMasterDataSource(mContext);
                            CocDetailDataSource cocSource = new CocDetailDataSource(mContext);
                            UploadCoCList = cocDataSource.getallCoCMasterData();
                            cocRespmodel = mAquaBlueService.syncCoc(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_sync_coc), UploadCoCList, guid);

                            if (cocRespmodel != null) {
                                if (!cocRespmodel.isSuccess()) { // AGREEMENT CHECK

                                    String code = cocRespmodel.getResponseCode().toString();

                                    if (code.equals("401")) {
                                        setGlobalResponseUnAuthMsg(cocRespmodel.getMessage());
                                        response = "DEVICE-DISABLE";
                                    } else if (code.equals("417")) {
                                        response = "USER-SUSPENDED";
                                    } else {
                                        Log.e("disableCheck", "doInBackground: " + cocRespmodel.getMessage().toString());
                                        response = "FALSE";
                                    }
                                } else if (cocRespmodel.isSuccess()) {
                                    response = "SUCCESS";
                                    for (SyncCocResponseModel item : cocRespmodel.getData()) {
                                        cocSource.updateSyncedCoc(item.getCocDetailsList());
                                    }
                                } else {
                                    GlobalStrings.responseMessage = cocRespmodel.getMessage();
                                    response = cocRespmodel.getResponseCode().toString();
                                    return response;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "CoC Data Sync Error:" + e.getMessage());
                            return null;
                        }

                        isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
                        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
                        if (!closeEvent && (!isAttachmentsAvailableToSync && !isFieldDataAvailableToSync)) {
                            return response;
                        }
                    }

                    case STATE_UPLOAD_EVENTDATA: {

                        List<Integer> fieldIDList = new ArrayList<>();
                        List<Integer> fieldIDInsertList = new ArrayList<>();
                        List<Integer> fieldIDUpdateList = new ArrayList<>();
                        List<FieldDataSyncStaging> fieldDataInsertList = new ArrayList<>();
                        List<FieldDataSyncStaging> fieldDataUpdateList = new ArrayList<>();
                        HashMap<String, List<FieldDataSyncStaging>> mapFieldsList = fieldSource.collectDataForSyncUpload();

                        if (!isResyncData) {
                            if (mapFieldsList.containsKey(GlobalStrings.FIELD_DATA_INSERT))
                                fieldDataInsertList = mapFieldsList.get(GlobalStrings.FIELD_DATA_INSERT);
                            if (mapFieldsList.containsKey(GlobalStrings.FIELD_DATA_UPDATE))
                                fieldDataUpdateList = mapFieldsList.get(GlobalStrings.FIELD_DATA_UPDATE);
                        } else
                            fieldDataInsertList = fieldSource.collectDataToReSyncAll();

                        HashMap<Integer, ArrayList<FieldDataSyncStaging>> mapSyncData
                                = new HashMap<>();

                        if (fieldDataInsertList != null) {
                            for (FieldDataSyncStaging field : fieldDataInsertList) {
                                fieldIDInsertList.add(field.getFieldDataSyncStaginId());

                                String keyValue = field.getEventId() + "|" + field.getSiteId();
                                if (!syncStatusMap.containsKey(keyValue)) {
                                    syncStatusMap.put(keyValue,
                                            new DataSyncStatus(field.getEventId() + "",
                                                    field.getSiteId() + "", 0));
                                }
                            }
                        }

                        if (fieldDataUpdateList != null) {
                            for (FieldDataSyncStaging field : fieldDataUpdateList) {
                                fieldIDUpdateList.add(field.getFieldDataSyncStaginId());

                                String keyValue = field.getEventId() + "|" + field.getSiteId();
                                if (!syncStatusMap.containsKey(keyValue)) {
                                    syncStatusMap.put(keyValue,
                                            new DataSyncStatus(field.getEventId() + "",
                                                    field.getSiteId() + "", 0));
                                }
                            }
                        }

                        try {
                            //insert fields
                            if (fieldDataInsertList != null && !fieldDataInsertList.isEmpty()) {

                                fieldDataInsertRes = mAquaBlueService.v1_setFieldEventData(getResources()
                                                .getString(R.string.prod_base_uri),
                                        getResources().getString(R.string.prod_fielddata_sync_insert), fieldDataInsertList, guid);

                                if (fieldDataInsertRes != null) {
                                    if (!fieldDataInsertRes.isSuccess()) { // Agreement check

                                        String code = fieldDataInsertRes.getResponseCode().toString();

                                        if (code.equals("401")) {
                                            setGlobalResponseUnAuthMsg(fieldDataInsertRes.getMessage());
                                            response = "DEVICE-DISABLE";
                                        } else if (code.equals("417")) {
                                            response = "USER-SUSPENDED";
                                        } else {
                                            response = "FALSE";
                                        }
                                    } else if (fieldDataInsertRes.isSuccess()
                                            && fieldDataInsertRes.getResponseCode().equals(HttpStatus.OK)) {
                                        if (fieldDataInsertRes.isData()) {
                                            response = "SUCCESS";
                                            fieldSource.setDataSyncFlagDFieldData(AttachmentDataSource.SyncType.data, fieldIDInsertList);
                                            for (DataSyncStatus value : syncStatusMap.values()) {
                                                value.setLastSyncDate(System.currentTimeMillis());
                                                syncDateSource.insertDownloadDataSyncDate(value);
                                            }
                                        }
                                    } else {
                                        GlobalStrings.responseMessage = fieldDataInsertRes.getMessage();
                                        response = fieldDataInsertRes.getResponseCode().toString();
                                        break;
                                    }
                                }
                            }//end insert fields

                            //updateField
                            if (fieldDataUpdateList != null && !fieldDataUpdateList.isEmpty()) {
                                fieldDataUpdateRes = mAquaBlueService.v1_setFieldEventData(getResources()
                                                .getString(R.string.prod_base_uri),
                                        getResources().getString(R.string.prod_fielddata_sync_update), fieldDataUpdateList, guid);

                                if (fieldDataUpdateRes != null) {
                                    if (!fieldDataUpdateRes.isSuccess()) { // Agreement check

                                        String code = fieldDataUpdateRes.getResponseCode().toString();

                                        if (code.equals("401")) {
                                            setGlobalResponseUnAuthMsg(fieldDataUpdateRes.getMessage());
                                            response = "DEVICE-DISABLE";
                                        } else if (code.equals("417")) {
                                            response = "USER-SUSPENDED";
                                        } else {
                                            response = "FALSE";
                                        }
                                    } else if (fieldDataUpdateRes.isSuccess()
                                            && fieldDataUpdateRes.getResponseCode().equals(HttpStatus.OK)) {
                                        if (fieldDataUpdateRes.isData()) {
                                            response = "SUCCESS";
                                            fieldSource.setDataSyncFlagDFieldData(AttachmentDataSource.SyncType.data, fieldIDUpdateList);
                                            for (DataSyncStatus value : syncStatusMap.values()) {
                                                value.setLastSyncDate(System.currentTimeMillis());
                                                syncDateSource.insertDownloadDataSyncDate(value);
                                            }
                                        }
                                    } else {
                                        GlobalStrings.responseMessage = fieldDataUpdateRes.getMessage();
                                        response = fieldDataUpdateRes.getResponseCode().toString();
                                        break;
                                    }
                                }
                            } //end update fields
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "FieldDataSync Error:" + e.getMessage());
                        }

                        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
                        if (!closeEvent && !isAttachmentsAvailableToSync && !isResyncData) {
                            return response;
                        }
                    }

                    case STATE_UPLOAD_EVENTFILE: {

                        AttachmentResponseModel resultModel = null;
                        MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
                        EventFieldData devent = new EventFieldData();
                        attachList = new ArrayList<Attachment>();

                        if (!isResyncData)
                            attachList.addAll(attachDataSource
                                    .getAttachmentDataListFromDB(AttachmentDataSource.DataForSync.DataNotSynced));
                        else
                            attachList.addAll(attachDataSource
                                    .getAttachmentDataToResyncAll());

                        HashMap<String, DataSyncStatus> mapAttachment = new HashMap<>();

                        if (attachList != null) {

                            publishProgress(getString(R.string.processing_photos));
                            for (Attachment attachment : attachList) {
                                if (attachment.getFileThumb() == null || attachment.getFileThumb().isEmpty()) {
                                    String ogFileName = new File(attachment.getFileLocation())
                                            .getName().replaceAll(".jpg", "");

                                    String dirPath = attachment.getFileLocation().replaceAll(new File(attachment.getFileLocation())
                                            .getName(), "");

                                    String thumbsDirPath = dirPath + GlobalStrings.THUMBNAILS_DIR + File.separator;

                                    if (!new File(thumbsDirPath).exists()) {
                                        new File(thumbsDirPath).mkdirs();
                                    }

                                    String thumbFileName = ogFileName + GlobalStrings.THUMBNAIL_EXTENSION;
                                    File dirThumbDest = new File(thumbsDirPath, thumbFileName);

                                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(attachment.getFileLocation()),
                                            GlobalStrings.THUMBNAIL_HEIGHT_WIDTH, GlobalStrings.THUMBNAIL_HEIGHT_WIDTH);
                                    if (thumbnail != null) {
                                        Util.saveBitmapToSDCard(thumbnail, dirThumbDest, GlobalStrings.COMPRESSION_RATE_100);
                                        attachment.setFileThumb(dirThumbDest.getAbsolutePath());
                                        new AttachmentDataSource(mContext).updateAttachmentThumbnail(attachment);
                                    }
                                }
                            }

                            Log.i(TAG, "attach list size=" + attachList.size());

                            publishProgress(getString(R.string.uploading_photos));

                            for (Attachment attachment : attachList) {
                                DAttachment dattachment = new DAttachment();
                                files = new LinkedMultiValueMap<>();

                                int evntID = attachment.getEvent().getEventId();
                                String attachType = attachment.getAttachmentType();
                                locationID = attachment.getLocationId();
                                locationName = LDSource.getLocationName(locationID);

                                if (locationName == null) {
                                    locationName = "";
                                }
                                if (locationID == null) {
                                    locationID = "0";
                                }
                                if (locationID != null && locationID.equalsIgnoreCase("null")) {
                                    locationID = "0";
                                }

                                String name = attachment.getExtField1();
                                if (name != null) {
                                    dattachment.setExtField1(name);
                                }

                                Integer setID = attachment.getSetId();
                                String filepath = attachment.getFileLocation();
                                String deviceID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.SESSION_DEVICEID);

                                devent = fieldSource.getDEvent(evntID);
                                devent.setDeviceId(deviceID);
                                //27-Mar-17 OTHER USER's DATA DOESN'T HAVE EVENTID
                                devent.setEventId(evntID);
                                devent.setUserId(attachment.getUserId());

                                Log.i(TAG, "sitename=" + devent.getSiteName());

                                attachment.setDeviceId(deviceID);

                                dattachment.setAttachmentType(attachType);
                                dattachment.setLocationId(locationID == null ? "0" : locationID);
                                dattachment.setLocation(locationName);
                                dattachment.setEventId(evntID);
                                dattachment.setDeviceId(deviceID);
                                dattachment.setCreationDate(attachment.getCreationDate());
                                dattachment.setLatitude(attachment.getLatitude());
                                dattachment.setLongitude(attachment.getLongitude());
                                dattachment.setExtField2(attachment.getExtField2());

                                String fpID = attachment.getFieldParameterID();
                                if (fpID != null && !fpID.isEmpty()) {
                                    dattachment.setFieldParameterId(Integer.valueOf(fpID));
                                }

                                dattachment.setAttachmentDate(attachment.getAttachmentDate());
                                dattachment.setAttachmentTime(attachment.getAttachmentTime());
                                dattachment.setModificationDate(attachment.getModificationDate());
                                dattachment.setAzimuth(attachment.getAzimuth());

                                //27-Nov-15  TimeTaken is set by setID
                                dattachment.setTimeTaken(Long.parseLong("" + attachment.getSetId()));
                                dattachment.setSiteId(attachment.getSiteId());
                                if (setID != null) {
                                    dattachment.setSetId(attachment.getSetId());
                                }
                                dattachment.setMobileAppId(attachment.getMobileAppId());
                                dattachment.setUserId(attachment.getUserId());
                                dattachment.setNotes(attachment.getNotes());
                                dattachment.setUuid(attachment.getUuid());

                                Log.i(TAG, "OriginalFilePath:" + filepath);

                                String keyValue = dattachment.getEventId() + "|" + dattachment.getSiteId();
                                if (!syncStatusMap.containsKey(keyValue)) {
                                    mapAttachment.put(keyValue,
                                            new DataSyncStatus(dattachment.getEventId() + "",
                                                    dattachment.getSiteId() + "", 0));
                                }

                                File imfile = new File(filepath);
                                if (!imfile.exists()) {
                                    boolean isDeleted = new AttachmentDataSource(mContext).deleteImage(filepath);
                                    Log.i(TAG, "Removed Attachment:" + filepath + " ->" + isDeleted);
                                    String msg = "Attachment " + filepath + " is Missing.";
                                    Util.msgHandler(msg, mHandler);
                                    continue;
                                }

                                String fileName = filepath.substring(filepath.lastIndexOf("/") + 1);
                                Log.i(TAG, "OriginalFileName:" + fileName);
                                dattachment.setOriginalFileName(fileName);

                                //notes were having issue with + sign on web so adding below changes
                                if (dattachment.getNotes() != null && dattachment.getNotes().contains("+"))
                                    dattachment.setNotes(dattachment.getNotes()
                                            .replaceAll("\\+", "%2b"));

                                devent.getAttachments().add(dattachment);

                                List<EventFieldData> eventListTemp = new LinkedList<EventFieldData>();
                                eventListTemp.add(devent);
                                String json = mapper.writer().writeValueAsString(eventListTemp);//new Gson().toJson(eventListTemp);
                                String jsonString = json;
                                if (json != null) {
                                    json = URLEncoder.encode(json, "UTF-8");
                                }

                                //guid = Util.getSharedPreferencesProperty(mContext,username);

                                files.add("events", jsonString);
                                files.add("files", new FileSystemResource(attachment.getFileLocation()));
                                files.add("userGuid", guid);//demoguid="f8180e4a-3b36-11e5-9708-0ea7cb7cc776"

                                String thumbPath = attachment.getFileThumb();
                                String path1000 = attachment.getFile1000();
                                if (path1000 != null && new File(path1000).exists())
                                    files.add("files1000", new FileSystemResource(attachment.getFile1000()));
                                else
                                    files.add("files1000", new FileSystemResource(attachment.getFileLocation()));

                                if (thumbPath != null && new File(thumbPath).exists())
                                    files.add("thumbnails", new FileSystemResource(attachment.getFileThumb()));

                                resultModel = mAquaBlueService.v1_SetFieldEventFile(getResources().getString(R.string.prod_base_uri),
                                        getResources().getString(R.string.prod_upload_file),
                                        files, new File(attachment.getFileLocation()), jsonString, guid);

                                if (resultModel != null) {
                                    if (!resultModel.isSuccess()) {

                                        String code = resultModel.getResponseCode().toString();

                                        if (code.equals("401")) {
                                            setGlobalResponseUnAuthMsg(resultModel.getMessage());
                                            response = "DEVICE-DISABLE";
                                        } else if (code.equals("417")) {
                                            response = "USER-SUSPENDED";
                                        } else {
                                            response = "FALSE";
                                        }
                                    }
                                    if (resultModel.getData().isSuccess()) {
                                        response = "SUCCESS";
                                        //Set the DataSyncFlag
                                        attachDataSource.setImageSyncFlag(AttachmentDataSource.SyncType.data, attachment);

                                        for (DataSyncStatus value : mapAttachment.values()) {
                                            value.setLastSyncDate(System.currentTimeMillis());
                                            syncDateSource.insertDownloadDataSyncDate(value);
                                        }
                                    } else {

                                        if (resultModel.getMessage() != null)
                                            GlobalStrings.responseMessage = resultModel.getMessage();
                                        response = resultModel.getResponseCode().toString();
                                        String msg = GlobalStrings.responseMessage;
                                        Log.e(TAG, msg);
                                        Util.msgHandler(msg, mHandler);
                                    }
                                } else {
                                    String msg = "Attachment " + filepath + " Failed to sync.";
                                    Log.e(TAG, msg);
                                    Util.msgHandler(msg, mHandler);
                                }
                            }
                        }

                        if ((!closeEvent)) {
                            return response;
                        }

                        isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
                        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();

                        if (closeEvent && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
                            try {
                                publishProgress(getString(R.string.finalizing_event));

                                EventResponseModel evntResp = null;
                                if (null != mAquaBlueService) {
                                    evntResp = mAquaBlueService.v1_closeEventID(getApplicationContext().getResources().getString(R.string.prod_base_uri),
                                            getApplicationContext().getResources().getString(R.string.prod_event_close), eventID);

                                    Log.i(TAG, "EventID Returned = " + eventID);
                                    if (evntResp != null) {
                                        if (!evntResp.isSuccess()) {

                                            String code = evntResp.getResponseCode().toString();

                                            if (code.equals("401")) {
                                                setGlobalResponseUnAuthMsg(evntResp.getMessage());
                                                response = "DEVICE-DISABLE";
                                            } else if (code.equals("417")) {
                                                response = "USER-SUSPENDED";
                                            } else {
                                                response = "FALSE";
                                            }
                                        } else if (evntResp.isSuccess()) {
                                            eventClosed = true;
                                            eventEndDate = evntResp.getData().getEventModificationDate();
                                            response = "SUCCESS";
                                        } else {
                                            GlobalStrings.responseMessage = evntResp.getMessage();
                                            response = evntResp.getResponseCode().toString();
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "Closeevent: error: " + e.getLocalizedMessage());
                            }
                            return response;
                        }
                    }

                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, " DataSync PostTask Error:" + e.getLocalizedMessage());
            }

            return response;
        }// end ofdoInBackground

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            updateAlertProgressMsg(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(LOG, " onPostExecute: Result = " + result);
            // Close the dialog
            cancelAlertProgress();
            // display the result
//            showResult(result);
            // close the activity
            boolean isUnsyncData = true, isUnsyncAttachment = true;

            Log.d(LOG, " showResult: result= " + result);
            if (result != null) {

                isUnsyncData = fieldSource.isFieldDataAvailableToSync();
                isUnsyncAttachment = attachDataSource.attachmentsAvailableToSync();

                if (result.equals(HttpStatus.LOCKED.toString()) || result.equals(HttpStatus.NOT_FOUND.toString())) {
//                    Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                    showAlert(mContext, getString(R.string.attention) + "!", GlobalStrings.responseMessage, false);
                } else if (result.equalsIgnoreCase("SUCCESS") && !isUnsyncData && !isUnsyncAttachment) {
                    showAlert(mContext, getString(R.string.sync_success), getString(R.string.all_data_syned_to_server), true);
                } else if (result.equals("FALSE")) {
                    showAgreement();
                } else if (result.equals("USER-SUSPENDED")) {
                    String msg = getString(R.string.your_acc_was_suspended);
                    CustomAlert.showAlert(mContext, msg, getString(R.string.alert));
                } else if (result.equals("DEVICE-DISABLE")) {
                    CustomAlert.showUnAuthAlert(mContext, GlobalStrings.responseMessage, getString(R.string.alert));
                }
//                else if (isUnsyncData || isUnsyncAttachment) {
//                    showAlert(mContext, "Sync Failed!", "Some data failed to Sync. Please try again.", false);
//                }
                else if (result.equals(HttpStatus.EXPECTATION_FAILED.toString())) {
                    Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                } else if (result.equals(HttpStatus.UNAUTHORIZED.toString())) {
                    Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                } else if (result.equals(HttpStatus.CONFLICT.toString())) {
                    Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                } else if (result.equals(HttpStatus.NOT_ACCEPTABLE.toString()) || result.equals("2000")) {
                    Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                }
            } else {

                isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
                isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();

                if (!isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
                    showAlert(mContext, getString(R.string.sync_success), getString(R.string.all_data_syned_to_server), true);
                } else {
                    showAlert(mContext, getString(R.string.sync_failed), getString(R.string.some_data_failed_to_sync_try_again), false);
                }
            }
        }// end of onPostExecute
    }// end of PostMessageTask

    private void setGlobalResponseUnAuthMsg(String message) {
        if (message != null && !message.isEmpty())
            GlobalStrings.responseMessage = message;
        else
            GlobalStrings.responseMessage = getString(R.string.device_disabled_contact_to_activate_device);
    }

    private void showAgreement() {
        Intent intent = new Intent(DataSyncActivity.this, AgreementActivity.class);
        intent.putExtra("input", "datasyncact");
//        startActivity(intent);
        Toast.makeText(mContext, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAlertProgress();
    }

    void showAlert(Context context, String title, String msg, final boolean datasynced) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(false)
                .setMessage(msg)
                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent output = new Intent();
                        if (closeEvent) {
                            output.putExtra("SYNC_FLAG", eventClosed);
                            output.putExtra("EVENT_END_DATE", eventEndDate);
                        }
                        output.putExtra("SYNC_SUCCESS", datasynced);
                        setResult(RESULT_OK, output);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public File readFile(String dirPath, String fileName) {

        //Get the text file
        File file = new File(dirPath, fileName);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        Log.d(TAG, " readFile: " + text.toString());

        return file;
    }

    private byte[] readFile(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, " readFile: " + bytes.length);
        return bytes;
    }
}// end of SignUpActivity
    
