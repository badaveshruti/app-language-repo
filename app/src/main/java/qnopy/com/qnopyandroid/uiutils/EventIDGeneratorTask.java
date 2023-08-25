package qnopy.com.qnopyandroid.uiutils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CompletionPercentageDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.EventLocationDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseData;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.FormActivity;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class EventIDGeneratorTask extends AsyncTask<Object, Void, Object> {
    private static final String TAG = "EventIDGeneratorTask";
    AquaBlueServiceImpl mAquaBlueService;
    String username = null;
    String password = null;
    Context mContext = null;
    DEvent event = null;
    ProgressDialog procDialog = null;
    EventResponseData response;
    EventResponseModel eventresponse;
    boolean syncAll = false;
    private OnTaskCompleted listener;

    private String guid;
    private AlertDialog progressBar;

    public EventIDGeneratorTask(OnTaskCompleted listner, DEvent event, String userName,
                                String passwd, boolean syncAll) {
        mContext = (Context) listner;
        this.event = event;
        this.listener = listner;
        this.username = userName;
        this.password = passwd;
        mAquaBlueService = new AquaBlueServiceImpl(mContext);
        this.syncAll = syncAll;
    }

    public EventIDGeneratorTask(OnTaskCompleted listner, DEvent event, String userName,
                                String passwd, boolean syncAll, Context context) {
        mContext = context;
        this.event = event;
        this.listener = listner;
        this.username = userName;
        this.password = passwd;
        mAquaBlueService = new AquaBlueServiceImpl(mContext);
        this.syncAll = syncAll;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startProcDialog();
    }

    @Override
    protected Object doInBackground(Object... arg0) {
        try {
            int eventID = 0;
            username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
            guid = Util.getSharedPreferencesProperty(mContext, username);
            if (null != mAquaBlueService) {

                // if syncAll then response will be success/fail returned from this method to
                // determine operation finished else EventResModel is returned if single event is created
                if (syncAll) {

                    EventDataSource eventData = new EventDataSource(mContext);
                    FieldDataSource fieldData = new FieldDataSource(mContext);
                    AttachmentDataSource attachDataSrc = new AttachmentDataSource(mContext);
                    CompletionPercentageDataSource cp = new CompletionPercentageDataSource(mContext);

                    ArrayList<DEvent> eventList = eventData
                            .getClientGeneratedEventIDs(mContext);

                    int count = eventList.size();

                    if (count > 0) {
                        for (DEvent mEvent : eventList) {

//                            eventresponse = mAquaBlueService.generateEventIDFromServer(GlobalStrings.Local_Base_URL,
//                                    getApplicationContext().getResources().getString(R.string.prod_event_create), mEvent,
//                                    username,
//                                    password);


                            JSONObject jsonObject = new JSONObject();

                            try {
                                jsonObject.put("siteId", mEvent.getSiteId());
                                jsonObject.put("mobileAppId", mEvent.getMobileAppId());
                                jsonObject.put("userGuid", guid);//"f8180e4a-3b36-11e5-9708-0ea7cb7cc776"
                                jsonObject.put("userId", mEvent.getUserId());
                                jsonObject.put("deviceId", DeviceInfo.getDeviceID(mContext));
                                jsonObject.put("latitude", mEvent.getLatitude());
                                jsonObject.put("longitude", mEvent.getLongitude());
                                jsonObject.put("eventDate", mEvent.getEventDate());
                                jsonObject.put("eventStartDate", mEvent.getEventStartDate());
                                jsonObject.put("eventEndDate", mEvent.getEventEndDate());
                                jsonObject.put("eventName", mEvent.getEventName());
                                jsonObject.put("eventId", mEvent.getEventId());
                                jsonObject.put("clientEventId", mEvent.getEventId());
                                jsonObject.put("createEventFlag", 1);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
                                return "FAIL";
                            }

                            eventresponse = mAquaBlueService.generateEventIDFromServer(mContext
                                            .getResources().getString(R.string.prod_base_uri),
                                    mContext.getResources().getString(R.string.prod_event_create), mEvent,
                                    username,
                                    jsonObject);

                            GlobalStrings.responseMessage = eventresponse.getMessage();

                            if (eventresponse != null) {
                                if (eventresponse.isSuccess()) {

                                    int serverGenEventID = eventresponse.getData().getEventId();

                                    //changing event id to avoid any discrepancy by client id
                                    if (LocationActivity.eventID == mEvent.getEventId()
                                            || SplitLocationAndMapActivity.eventID == mEvent.getEventId()) {
                                        LocationActivity.eventID = serverGenEventID;
                                        SplitLocationAndMapActivity.eventID = serverGenEventID;
                                        FormActivity.eventIDChanged = serverGenEventID;
                                    }

                                    EventLocationDataSource eventLocationDS = new EventLocationDataSource(mContext);
                                    eventLocationDS.updateEventId(mEvent.getEventId() + "",
                                            serverGenEventID + "");
                                    fieldData.updateEventID(mEvent.getEventId(), serverGenEventID);
                                    attachDataSrc.updateEventID(mEvent.getEventId(), serverGenEventID);
                                    eventData.updateEventID(mEvent.getEventId(), eventresponse);
                                    new SampleMapTagDataSource(mContext)
                                            .updateEventID_SampleMapTag(mEvent.getEventId()
                                                    + "", serverGenEventID + "");
                                } else {
                                    if ((eventresponse.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) ||
                                            (eventresponse.getResponseCode() == HttpStatus.EXPECTATION_FAILED) ||
                                            (eventresponse.getResponseCode() == HttpStatus.UNAUTHORIZED) ||
                                            (eventresponse.getResponseCode() == HttpStatus.CONFLICT)
                                    ) {
                                        //02-AuG-16
                                        Toast.makeText(mContext, GlobalStrings.responseMessage,
                                                Toast.LENGTH_LONG).show();
                                        Util.setLogout((Activity) mContext);
                                    }
                                    if ((eventresponse.getResponseCode() == HttpStatus.NOT_FOUND)
                                            || (eventresponse.getResponseCode() == HttpStatus.LOCKED)) {
                                        Util.setDeviceNOT_ACTIVATED((Activity) mContext,
                                                username, password);
                                    }

                                    if (eventresponse.getResponseCode() == HttpStatus.BAD_REQUEST) {
                                        Toast.makeText(mContext, GlobalStrings.responseMessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                    return "FAIL";
                                }
                            } else {
                                return "FAIL";
                            }
                        }
                    }
                    return "SUCCESS";
                } else {
                    eventresponse = mAquaBlueService.generateEventIDFromServer(mContext
                                    .getResources().getString(R.string.prod_base_uri),
                            mContext.getResources().getString(R.string.prod_event_create), event,
                            username,
                            null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in EventIDGeneratorTask:" + e.getMessage());
        }

        return eventresponse;
    }

    @Override
    protected void onPostExecute(Object obj) {
        if ((progressBar != null) && (progressBar.isShowing())) {
            try {
                progressBar.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onPostExecute() procDialog dismiss error:" + e.getLocalizedMessage());
            }
        }
        listener.onTaskCompleted(obj);
    }

    void startProcDialog() {
        procDialog = new ProgressDialog(mContext);
        procDialog.setCancelable(false);
        procDialog.setTitle(mContext.getString(R.string.creating_event));
        procDialog.setMessage(mContext.getString(R.string.please_wait));
        procDialog.setIndeterminate(true);
//        procDialog.show();

        String msg = "";

/*        if (event.getCreateEventFlag() == 0)
            msg = mContext.getString(R.string.updating_event_name);
        else*/
        msg = mContext.getString(R.string.creating_event);

        progressBar = AlertManager.showQnopyProgressBar((AppCompatActivity) mContext, msg);
        progressBar.show();
    }
}
