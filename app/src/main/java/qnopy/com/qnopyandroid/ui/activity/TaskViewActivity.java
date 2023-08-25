package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.TaskViewAdapter;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.clientmodel.TaskView;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CompletionPercentageDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.WorkOrderTaskDataSource;
import qnopy.com.qnopyandroid.gps.GPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 7/20/2017.
 */

public class TaskViewActivity extends ProgressDialogActivity implements OnTaskCompleted {
    private static final String TAG = "DashboardActivity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    Context context;
    TextView emptyview;
    Button dispplannamebtnView;
    ActionBar actionBar;
    Bundle extras;
    int formid, eventid, woid, siteid, userid = 0;
    String planname, deviceid = null;
    boolean drawer = false;
    Event event = new Event();
    boolean closeEvent = false;
    GPSTracker tracker;
    String username, password = null;
    int serverGenEventID = 0;
    boolean isLocationsAvailableToSync = false, isAttachmentsAvailableToSync = false, isFieldDataAvailableToSync = false;
    FieldDataSource fieldSource = null;
    LocationDataSource LDSource = null;
    AttachmentDataSource attachDataSource = null;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;


    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskview_layout);
        context = this;

        tracker = new GPSTracker(this);

        recyclerView = findViewById(R.id.taskviewrecycler);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        emptyview = findViewById(R.id.taskviewempty);
        dispplannamebtnView = findViewById(R.id.planNameTextView);
        View cView = getLayoutInflater().inflate(R.layout.notedialog_actionbar, null);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setCustomView(cView);
        }
        extras = getIntent().getExtras();

        if (extras != null) {

            setFormid(extras.getInt("APP_ID"));
            setEventid(extras.getInt("EVENT_ID"));
            setPlanname(extras.getString("PLANNAME"));
            setWoid(extras.getInt("WOID"));
            setSiteid(extras.getInt("SITE_ID"));
        }
        userid = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        setUserid(userid);
        deviceid = Util.getSharedPreferencesProperty(context, GlobalStrings.DEVICEID);
        setDeviceid(deviceid);

        dispplannamebtnView.setText(getPlanname());

        ArrayList<TaskView> taskCardlist = collectDataforTask();
        if (taskCardlist != null || taskCardlist.size() < 1) {
            adapter = new TaskViewAdapter(taskCardlist, context, getEventid(), getFormid(), getSiteid());
            recyclerView.setAdapter(adapter);
        } else {
            emptyview.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_task_actions, menu);
        // menu.findItem(R.id.filefolder).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.syncdata:
                Intent dataUpload = new Intent(this, DataSyncActivity.class);
                String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
                String password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
                dataUpload.putExtra("USER_NAME", username);
                dataUpload.putExtra("PASS", password);
                dataUpload.putExtra("EVENT_ID", getEventid());
                startActivity(dataUpload);
                return true;

            case R.id.submitnend:
                if (CheckNetwork.isInternetAvailable(context)) {
                    closeEventAlert();
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();

                }
                return true;


            case android.R.id.home:
                Log.i(TAG, "HomeUp Pressed");
                finish();

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {

            Log.i(TAG, "onActivityResult() SYNC_ACTIVITY Start time:" + System.currentTimeMillis());



            long date = System.currentTimeMillis();

            boolean eventClosed = data.getBooleanExtra("SYNC_FLAG", false);
            long eventEndDate = Long.parseLong(data.getStringExtra("EVENT_END_DATE"));
            boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);

            if (dataSynced && eventClosed) {

                EventDataSource eventData = new EventDataSource(context);
                CompletionPercentageDataSource cp = new CompletionPercentageDataSource(context);

                eventData.closeEventStatus(getFormid(), getSiteid(), eventEndDate, getEventid() + "");
                cp.truncatePercentageByRollAppID_And_SiteID(getSiteid() + "", getFormid() + "");
                // SharedPref.resetCamOrMap();

                Toast.makeText(getApplicationContext(),
                        "The Event has been Closed.", Toast.LENGTH_SHORT)
                        .show();
                finish();

            } else {
                Toast.makeText(getApplicationContext(),
                        "The Event can not be closed,please try again!",
                        Toast.LENGTH_LONG).show();
            }

        }


    }


    public void closeEventAlert() {
        Log.i(TAG, "closeEventAlert() Start time:" + System.currentTimeMillis());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TaskViewActivity.this);

        alertDialogBuilder.setTitle(this.getTitle() + " DECISION");
        alertDialogBuilder
                .setMessage("Are you sure you want to submit the data and close the event?\nData will be committed to the server and you will not able to change it.\nPlease click yes to proceed or cancel to go back");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(" YES ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
// TODO: 19-Jun-17
//                        AppPreferenceDataSource ds = new AppPreferenceDataSource(context);
//                        //KEY_SIGNATURE
//                        if (ds.isFeatureAvailable(GlobalStrings.KEY_SIGNATURE, getUserID())) {
//                            // TODO: 19-Jun-17 CAPTURE SIGNATURE AFTER SUBMIT AND END
//
//                            Intent intent = new Intent(
//                                    getApplicationContext(),
//                                    CaptureSignature.class);
//                            startActivityForResult(intent,
//                                    CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE);
//
//
//                        } else {
//                            closingEvents();
//
//                        }
                        closingEvents();

                        Log.i(TAG, "Submit_and_End() capture signature call time:" + System.currentTimeMillis());

                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                        drawer = false;
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Log.i(TAG, "closeEventAlert() End time:" + System.currentTimeMillis());

    }

    public void closingEvents() {
        Log.i(TAG, "closingEvents() Start time:" + System.currentTimeMillis());


        Log.i(TAG, "closingEvents() EventDataSource Instance Start:" + System.currentTimeMillis());


        EventDataSource eventData = new EventDataSource(context);

        Log.i(TAG, "closingEvents() EventDataSource Instance End:" + System.currentTimeMillis());

        //   Log.i(TAG, "closingEvents() Session UserID:" + getUserID());

        Log.i(TAG, "closingEvents() getEvent Start:" + System.currentTimeMillis());
        //  Log.i(TAG, "closingEvents() getEvent arguments :AppID=" + getAppID() +
        //     ",SiteID=" + getSiteID() + ",userID=" + userID + ",DeviceID=" + getDeviceID());


        event = eventData.getEvent(getFormid(), getSiteid(), userid, deviceid);
        Log.i(TAG, "closingEvents() getEvent End:" + System.currentTimeMillis());

        closeEvent = true;

        Log.i(TAG, "closingEvents() Call GPS Tracker start time:" + System.currentTimeMillis());

        double lat = tracker.getLatitude();
        double longt = tracker.getLongitude();
        Log.i(TAG, "closingEvents() GPS Tracker latitude:" + lat + ",longitude:" + longt);

        Log.i(TAG, "closingEvents() Call GPS Tracker End time:" + System.currentTimeMillis());

        Log.i(TAG, "closingEvents() Check EventID Server Generated start time:" + System.currentTimeMillis());

        boolean serverGenerated = eventData
                .isEventIDServerGenerated(getEventid());
        Log.i(TAG, "closingEvents() Check EventID Server Generated result:" + serverGenerated);

        Log.i(TAG, "closingEvents() Check EventID Server Generated End time:" + System.currentTimeMillis());
        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        if (!serverGenerated) {

            Log.i(TAG, "closingEvents() EventID Not Found Server Generated ");

            final DEvent event = new DEvent();
            event.setSiteId(getSiteid());
            event.setMobileAppId(getFormid());
            int userid1 = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

            event.setUserId(userid1);
            event.setEventDate(System.currentTimeMillis());
//          event.setEventDate(System.currentTimeMillis() - 86400000);
            event.setDeviceId(getDeviceid());
            event.setLatitude(lat);
            event.setLongitude(longt);

            event.setUserName(username);
            EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(TaskViewActivity.this,
                    event, username, password, false);
            eventHandler.execute();

        } else {
            /*
             * fieldSource.updateEventEndDateTime(getAppID(), dateString);
			 */

            Log.i(TAG, "closingEvents() EventID Found Server Generated ");

            if (CheckNetwork.isInternetAvailable(context)) {
                uploadFieldDataBeforeEndEvent();
            } else {
                //  Log.i(TAG, "closingEvents() No Internet.Delete captured signture(s) = " + savedFilePath);

                //removeAttachmentAfterSyncResult(savedFilePath);
                CustomToast.showToast((Activity) context,
                        getString(R.string.bad_internet_connectivity), 5);
            }
        }

        Log.i(TAG, "closingEvents() End time:" + System.currentTimeMillis());
    }

    @Override
    protected void onPause() {
        if (tracker != null)
            tracker.stopUsingGPS();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (tracker != null)
            tracker.stopUsingGPS();
        super.onStop();
    }

    private ArrayList<TaskView> collectDataforTask() {

        ArrayList<TaskView> list = new ArrayList<>();
        WorkOrderTaskDataSource workOrderTaskDataSource = new WorkOrderTaskDataSource(context);
        int cur_woid = getWoid();
        list = workOrderTaskDataSource.getTaskListFromDB(cur_woid);
        return list;

    }

    public int getSiteid() {
        return siteid;
    }

    public void setSiteid(int siteid) {
        this.siteid = siteid;
    }

    public int getFormid() {
        return formid;
    }

    public void setFormid(int formid) {
        this.formid = formid;
    }


    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public int getWoid() {
        return woid;
    }

    public void setWoid(int woid) {
        this.woid = woid;
    }

    @Override
    public void onTaskCompleted(Object obj) {
        FieldDataSource fieldData = new FieldDataSource(context);
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(context);
        EventDataSource eventData = new EventDataSource(context);

        if (obj != null) {

            if (obj instanceof String) {

                String result = (String) obj;

                if (result.equals("SUCCESS")) {

                    Event event = eventData.getEvent(getFormid(), getSiteid(), getUserid(), getDeviceid());
                    int currentEventID = event.getEventId();
                    setEventid(currentEventID);
                    uploadFieldData();

                } else {
                    Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
                }

            } else if (obj instanceof EventResponseModel) {

                EventResponseModel result = (EventResponseModel) obj;
                GlobalStrings.responseMessage = result.getMessage();

                if (result.isSuccess()) {

                    serverGenEventID = result.getData().getEventId();
                    setGeneratedEventID(result);
                    Log.i(TAG, "Event ID From Server:" + serverGenEventID);

                    fieldData.updateEventID(getEventid(), serverGenEventID);
                    attachDataSrc.updateEventID(getEventid(), serverGenEventID);
                    eventData.updateEventID(getEventid(), result);
                    new SampleMapTagDataSource(context).updateEventID_SampleMapTag(getEventid() + "", serverGenEventID + "");


                    setEventid(serverGenEventID);

                    if (CheckNetwork.isInternetAvailable(context)) {
                        if (closeEvent) {
                            uploadFieldDataBeforeEndEvent();
                        } else {
                            uploadFieldData();
                        }
                    } else {
                        CustomToast.showToast((Activity) context,
                                getString(R.string.bad_internet_connectivity), 5);
                    }

                } else {
                    if (result.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                        // TODO: 04-Mar-16
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                    if ((result.getResponseCode() == HttpStatus.NOT_FOUND) || (result.getResponseCode() == HttpStatus.LOCKED)) {
                        Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
//                    Toast.makeText(context,GlobalStrings.responseMessage,Toast.LENGTH_LONG).show();

                    }
                    if (result.getResponseCode() == HttpStatus.BAD_REQUEST) {
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();

                    }
                }

            }

        } else {
            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();

        }

        Log.i(TAG, "onTaskCompleted() OUT time:" + System.currentTimeMillis());
    }

    public void uploadFieldDataBeforeEndEvent() {
        Log.i(TAG, "Upload Field Data endEvent Call  start:" + System.currentTimeMillis());
        Intent dataUpload = new Intent(this, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", getEventid());
        dataUpload.putExtra("CLOSE_EVENT", true);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        Log.i(TAG, "Upload Field Data endEvent arguments:EventID-=" + getEventid() + ",UserName=" + username + ",Password=" + password);

        Log.i(TAG, "Upload Field Data endEvent Call End:" + System.currentTimeMillis());

    }

    public void uploadFieldData() {

        LDSource = new LocationDataSource(context);
        fieldSource = new FieldDataSource(context);
        attachDataSource = new AttachmentDataSource(context);

        // TODO: 12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();// TODO: 24-Mar-17
        isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();

        if (!isLocationsAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            Toast.makeText(context, "NO DATA TO SYNC", Toast.LENGTH_LONG).show();

        } else {
            Log.i(TAG, "uploadFieldData() Upload Field Data Called:" + System.currentTimeMillis());
            Intent dataUpload = new Intent(this, DataSyncActivity.class);
            username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
            password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", getEventid());
            startActivity(dataUpload);
        }

        Log.i(TAG, "Upload Field Data Call End:" + System.currentTimeMillis());

    }


    @Override
    public void onTaskCompleted() {
    }

    @Override
    public void setGeneratedEventID(int id) {
//        if (id != 0) {
//            this.serverGenEventID = id;
//            Log.i(TAG, "setGeneratedEventID() serverEventID:" + id);
//        }
    }

    @Override
    public void setGeneratedEventID(Object obj) {
        EventResponseModel res = (EventResponseModel) obj;
        int id = res.getData().getEventId();

        if (id != 0) {
            this.serverGenEventID = id;
            Log.i(TAG, "setGeneratedEventID() serverEventID:" + id);
        }
    }
}
