package qnopy.com.qnopyandroid.services;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Attachment;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DAttachment;
import qnopy.com.qnopyandroid.requestmodel.EventFieldData;
import qnopy.com.qnopyandroid.responsemodel.AttachmentResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FieldDataSyncStaging;
import qnopy.com.qnopyandroid.responsemodel.FielddataResponseModel;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.updateUserLocationResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.DataSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.ui.activity.SplashScreenActivity;
import qnopy.com.qnopyandroid.util.Util;

public class DataSyncService extends IntentService {

    private static final String TAG = "DataSyncService";
    public String filename = "QuizCD.txt";
    int mStateMachine = 0;

    AquaBlueServiceImpl mAquaBlueService;
    // Jackson Serializer
    ObjectMapper mapper = new ObjectMapper();

    String username = null;
    String password = null;
    int eventID = 0;
    int siteID = 0;
    int appID = 0;
    String siteName = null;

    boolean closeEvent = false;
    boolean eventClosed = false;

    public final int STATE_UPLOAD_LOCATION = 0;
    public final int STATE_UPLOAD_EVENTDATA = 1;
    public final int STATE_UPLOAD_EVENTFILE = 2;
    boolean dataSynced = false;
    boolean dataSyncFailedAtLeastOnce = false;

    String guid = null;

    public List<EventFieldData> eventList = null;

    LocationDataSource locSource = null;


    public List<FieldDataSyncStaging> UploadFieldDataList = null;
    public List<NewClientLocation> UploadLocationList = null;
    List<Attachment> attachList = null;
    boolean isLocationsAvailableToSync = false,
            isAttachmentsAvailableToSync = false,
            isFieldDataAvailableToSync = false;
    FieldDataSource fieldSource = null;
    AttachmentDataSource attachDataSource = null;
    LocationDataSource LDSource = null;
    Context mContext = this;

    String mUserAppType;

    public DataSyncService(Context context) {

        super("DataSyncService");

        mContext = context;
    }

    public DataSyncService() {

        super("DataSyncService");

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mContext = GlobalStrings.currentContext;
        if (mContext == null) {
            mContext = LocationDetailActivity.LocDetailActivity;
            if (mContext == null)
                mContext = getApplicationContext();
        }
        mUserAppType = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERAPPTYPE);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String notificationText = null;
        // Gets data from the incoming Intent
//        String dataString = workIntent.getDataString();
        if (CheckNetwork.isInternetAvailable(mContext)) {
            Bundle extras = workIntent.getExtras();
            String response = null;
            username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
            password = Util.getSharedPreferencesProperty(mContext, GlobalStrings.PASSWORD);
            guid = Util.getSharedPreferencesProperty(mContext, username);

            fieldSource = new FieldDataSource(mContext);
            attachDataSource = new AttachmentDataSource(mContext);
            LDSource = new LocationDataSource(mContext);

            isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync_Service();
            isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync_Service();

            Log.e(TAG, "onHandleIntent: finally" + mUserAppType);
            if (mUserAppType == null || mUserAppType.equals("")) {

                if (!isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
                    notificationText = "Great! We didn't find any un-synced data on your device.";
                } else {
                    boolean activityRunning = isMyActivityRunning(DataSyncActivity.class);
                    if (!activityRunning) {
                        response = syncMethod();
                        boolean isUnsyncData = true, isUnsyncAttachment = true;

                        Log.d(TAG, " sync service result= " + response);

                        if (response != null) {

                            isUnsyncData = fieldSource.isFieldDataAvailableToSync_Service();
                            isUnsyncAttachment = attachDataSource.attachmentsAvailableToSync_Service();

                            if (response.equals(HttpStatus.LOCKED.toString()) || response.equals(HttpStatus.NOT_FOUND.toString())) {
//
                                Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);

                            } else if (response.equals(HttpStatus.BAD_REQUEST.toString()) ||
                                    response.equals(HttpStatus.NOT_ACCEPTABLE.toString())) {

                                notificationText = GlobalStrings.responseMessage;
                            } else if (response.equalsIgnoreCase("SUCCESS") && !isUnsyncData && !isUnsyncAttachment) {
                                notificationText = "All your data is synced to server successfully.";

                            } else if (isUnsyncData || isUnsyncAttachment) {
                                notificationText = "Some data failed to Sync in background.";
                            }
                        } else {
                            notificationText = "Some data failed to sync in background.";
                        }
                    }
                }
            }

            try {
                mAquaBlueService = new AquaBlueServiceImpl(mContext);
                updateUserLocationResponse flocRespmodel = null;

                double lat = 0.0;
                double longt = 0.0;

                if (GlobalStrings.CURRENT_GPS_LOCATION != null) {
                    lat = GlobalStrings.CURRENT_GPS_LOCATION.getLatitude();
                    longt = GlobalStrings.CURRENT_GPS_LOCATION.getLongitude();
                }

                if (mAquaBlueService != null) {
                    flocRespmodel = mAquaBlueService.updateUserLocation(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_update_user_location), lat + "", longt + "", guid);

                    if (flocRespmodel != null) {
                        if (flocRespmodel.isSuccess()) {
                            Log.i(TAG, "User current location updated successfully");
                        } else {
                            GlobalStrings.responseMessage = flocRespmodel.getMessage();
                            String res = flocRespmodel.getResponseCode().toString();
                            if (res.equals(HttpStatus.LOCKED.toString()) || res.equals(HttpStatus.NOT_FOUND.toString())) {
                                Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Update user Location Error:" + e.getMessage());
            }
        } else {
            notificationText = "Connect your device to internet.";
        }

        if (mUserAppType == null || mUserAppType.equals("")) {
            if (notificationText != null) {
                Log.d(TAG, notificationText);
            }
            sendNotification(notificationText);
            stopService(workIntent);
        }
    }

//    public void notifySyncResult(String notificationText) {
//        Intent notificationIntent = new Intent(this, MainDrawerActivity.class);
//
//        notificationIntent.putExtra("USER_NAME", username);
//        notificationIntent.putExtra("PASS", password);
//
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        String CHANNEL_ID = "qnopy_channel_02";// The id of the channel.
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID).setContentTitle("Qnopy Data Sync")
//                .setContentText(notificationText).setTicker(notificationText).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent);
//        Notification notification = builder.build();
//
//        // Hide the notification after it's selected
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (notificationManager != null) {
//            notificationManager.cancelAll();
//            notificationManager.notify(0, notification);
//
//        }
//    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String notificationText) {
//        Intent intent = new Intent(this, NotificationActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        // Create an Intent for the activity you want to start

        // TODO: 24-05-2018 Start an Activity from a Notification (https://developer.android.com/training/notify-user/navigation)

        Intent resultIntent;
        String sess = Util.getSharedPreferencesProperty(this, GlobalStrings.IS_SESSION_ACTIVE);
        boolean isSessionActive = false;
        if (sess != null) {
            isSessionActive = Boolean.parseBoolean(sess);
        }

        if (!isSessionActive) {
            resultIntent = new Intent(this, SplashScreenActivity.class);
        } else {
//            resultIntent = new Intent(this, MainDrawerActivity.class);
            resultIntent = new Intent(this, HomeScreenActivity.class);
        }
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String CHANNEL_ID = "qnopy_channel_02";// The id of the channel.
//        CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Qnopy Data Sync")
                .setContentText(notificationText)
                .setAutoCancel(true)
                .setTicker("QNOPY")
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent)
                .setWhen(System.currentTimeMillis());


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "App closed so stop background service");
        Util.stopAlarm(mContext);
        super.onTaskRemoved(rootIntent);

    }

    public String syncMethod() {
        try {

            // Get handle to HTTP service
            guid = Util.getSharedPreferencesProperty(mContext, username);
            String response = null;


            FielddataResponseModel fDataRespmodel = null;
            NewLocationResponseModel locationRespmodel = null;
            String locationID = "0";
            String locationName;
            mAquaBlueService = new AquaBlueServiceImpl(mContext);
            Log.d(TAG, " doInBackground: State = " + mStateMachine);
//            if (isFieldDataAvailableToSync) {
            mStateMachine = STATE_UPLOAD_EVENTDATA;

            dataSyncFailedAtLeastOnce = false;

            switch (mStateMachine) {
                case STATE_UPLOAD_EVENTDATA: {

                    List<Integer> fieldIDList = new ArrayList<Integer>();

                    HashMap<String, List<FieldDataSyncStaging>> mapFields = fieldSource.collectDataForSyncUpload();

                    if (mapFields.containsKey(GlobalStrings.FIELD_DATA_INSERT)) {
                        UploadFieldDataList.clear();
                        UploadFieldDataList.addAll(mapFields.get(GlobalStrings.FIELD_DATA_INSERT));
                    }

                    for (FieldDataSyncStaging field : UploadFieldDataList) {
                        fieldIDList.add(field.getFieldDataSyncStaginId());
                        //  field.setFieldDataId(null);//04-Dec-15 commented
                    }

                    try {

                        fDataRespmodel = mAquaBlueService.v1_setFieldEventData(getResources().getString(R.string.prod_base_uri),
                                getResources().getString(R.string.prod_fielddata_sync), UploadFieldDataList, guid);

                        if (fDataRespmodel != null) {
                            if (fDataRespmodel.isSuccess()) {
                                if (fDataRespmodel.isData()) {
                                    response = "SUCCESS";
                                    fieldSource.setDataSyncFlagDFieldData(AttachmentDataSource.SyncType.data, fieldIDList);
                                }
                            } else {
                                GlobalStrings.responseMessage = fDataRespmodel.getMessage();
                                response = fDataRespmodel.getResponseCode().toString();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "FieldDataSync Error:" + e.getMessage());
                    }

                }

                case STATE_UPLOAD_EVENTFILE: {

                    AttachmentResponseModel resultModel = null;
                    MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
                    EventFieldData devent = new EventFieldData();
                    attachList = new ArrayList<Attachment>();

                    attachList.addAll(attachDataSource.getAttachmentDataListFromDB(AttachmentDataSource.DataForSync.DataNotSynced));

                    if (attachList != null) {
                        Log.i(TAG, "attach list size=" + attachList.size());

                        for (int i = 0; i < attachList.size(); i++) {
                            DAttachment dattachment = new DAttachment();
                            files = new LinkedMultiValueMap<String, Object>();

                            int evntID = attachList.get(i).getEvent().getEventId();
                            String attachType = attachList.get(i).getAttachmentType();
                            locationID = attachList.get(i).getLocationId();
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

                            String name = attachList.get(i).getExtField1();
                            if (name != null) {
                                dattachment.setExtField1(name);
                            }

                            Integer setID = attachList.get(i).getSetId();
                            String filepath = attachList.get(i).getFileLocation();
                            String deviceID = Util.getSharedPreferencesProperty(mContext, GlobalStrings.SESSION_DEVICEID);

                            devent = fieldSource.getDEvent(evntID);
                            devent.setDeviceId(deviceID);
                            devent.setEventId(evntID);
                            devent.setUserId(attachList.get(i).getUserId());

                            Log.i(TAG, "sitename=" + devent.getSiteName());

                            attachList.get(i).setDeviceId(deviceID);

                            dattachment.setAttachmentType(attachType);
                            dattachment.setLocationId(locationID);
                            dattachment.setLocation(locationName);
                            dattachment.setEventId(evntID);
                            dattachment.setDeviceId(deviceID);
                            dattachment.setCreationDate(attachList.get(i).getCreationDate());
                            dattachment.setLatitude(attachList.get(i).getLatitude());
                            dattachment.setLongitude(attachList.get(i).getLongitude());
                            dattachment.setExtField2(attachList.get(i).getExtField2());

                            String fpID = attachList.get(i).getFieldParameterID();
                            if (fpID != null && !fpID.isEmpty()) {
                                dattachment.setFieldParameterId(Integer.valueOf(fpID));
                            }

                            dattachment.setAttachmentDate(attachList.get(i).getAttachmentDate());
                            dattachment.setAttachmentTime(attachList.get(i).getAttachmentTime());
                            dattachment.setModificationDate(attachList.get(i).getModificationDate());
                            dattachment.setAzimuth(attachList.get(i).getAzimuth());
                            dattachment.setUuid(attachList.get(i).getUuid());

                            //27-Nov-15  TimeTaken is set by setID
                            dattachment.setTimeTaken(Long.parseLong("" + attachList.get(i).getSetId()));
                            dattachment.setSiteId(attachList.get(i).getSiteId());
                            if (setID != null) {
                                dattachment.setSetId(attachList.get(i).getSetId());
                            }
                            dattachment.setMobileAppId(attachList.get(i).getMobileAppId());
                            dattachment.setUserId(attachList.get(i).getUserId());
                            dattachment.setNotes(attachList.get(i).getNotes());
                            Log.i(TAG, "OriginalFilePath:" + filepath);

                            File imfile = new File(filepath);
                            if (!imfile.exists()) {
                                boolean isDeleted = new AttachmentDataSource(mContext).deleteImage(filepath);
                                Log.i(TAG, "Removed Attachment:" + filepath + " ->" + isDeleted);
                                continue;
                            }
                            String fileName = filepath.substring(filepath.lastIndexOf("/") + 1);
                            Log.i(TAG, "OriginalFileName:" + fileName);
                            dattachment.setOriginalFileName(fileName);

                            devent.getAttachments().add(dattachment);

                            List<EventFieldData> eventListTemp = new LinkedList<EventFieldData>();
                            eventListTemp.add(devent);
                            String json = mapper.writer().writeValueAsString(eventListTemp);//new Gson().toJson(eventListTemp);
                            String jsonString = json;
                            if (json != null) {
                                json = URLEncoder.encode(json, "UTF-8");
                            }

                            //guid = Util.getSharedPreferencesProperty(mContext,username);

                            files.add("events", json);
                            files.add("files", new FileSystemResource(attachList.get(i).getFileLocation()));
                            files.add("userGuid", guid);//demoguid="f8180e4a-3b36-11e5-9708-0ea7cb7cc776"
                            files.add("jsonString", jsonString);

                            resultModel = mAquaBlueService.v1_SetFieldEventFile(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_upload_file),
                                    files, new File(attachList.get(i).getFileLocation()), jsonString, guid);

                            if (resultModel != null) {
                                if (resultModel.getData().isSuccess()) {
                                    response = "SUCCESS";
                                    //Set the DataSyncFlag
                                    attachDataSource.setImageSyncFlag(AttachmentDataSource.SyncType.data, attachList.get(i));
                                } else {
                                    if (resultModel.getMessage() != null)
                                        GlobalStrings.responseMessage = resultModel.getMessage();
                                    response = resultModel.getResponseCode().toString();
                                    String msg = GlobalStrings.responseMessage;
                                    Log.e(TAG, msg);
                                }
                            } else {
                                String msg = "Attachment " + filepath + " Failed to sync.";
                                Log.e(TAG, msg);

                            }

                        }
                    }

                    return response;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error=" + e.getLocalizedMessage());
        }

        return null;
    }

    void showAlert(Context context, String title, String msg, final boolean datasynced) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(false)
                .setMessage(msg)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isMyActivityRunning(Class<?> activityClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningTaskInfo task : manager.getRunningTasks(Integer.MAX_VALUE)) {

            if ((activityClass.getName()).equalsIgnoreCase(task.topActivity.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
