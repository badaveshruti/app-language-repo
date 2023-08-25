package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.PlanListAdapter;
import qnopy.com.qnopyandroid.clientmodel.TaskFromSite;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.WorkOrderTaskDataSource;
import qnopy.com.qnopyandroid.gps.GPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 7/18/2017.
 */

public class TaskDetailActivity extends ProgressDialogActivity implements OnTaskCompleted {

    Context context;
    private static final String TAG = "TaskDetailActivity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    PlanListAdapter adapter;
    TextView emptyview;
    ActionBar actionBar;
    GPSTracker tracker;
    int formid, siteid, userid, eventID, woid = 0;
    String password, username, planname = null;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;
    Bundle extras;
    int SiteID, parentAppID = 0;
    String siteName = null;
    SearchView searchTaskView;
    //  ArrayList<TaskFromSite> planCardlist = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workordertask_layout);
        context = this;
        tracker = new GPSTracker(this);

        getExtras();
        recyclerView = findViewById(R.id.taskrecyclerView);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userid = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchTaskView = findViewById(R.id.search_tasktxt);
        emptyview = findViewById(R.id.taskemptyview);
        View cView = getLayoutInflater().inflate(R.layout.notedialog_actionbar, null);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setCustomView(cView);
        }

        if (SiteID != 0 && parentAppID == 0) {
            ArrayList<TaskFromSite> planCardlist = collectDataforFormPlan();
            if (planCardlist != null && planCardlist.size() > 0) {
                emptyview.setVisibility(View.GONE);
                adapter = new PlanListAdapter(planCardlist, (TaskDetailActivity) context);
                recyclerView.setAdapter(adapter);
            } else {
                //emptyview.setVisibility(View.VISIBLE);
                Toast.makeText(context, "No task assigned.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else if (SiteID != 0 && parentAppID != 0) {
            ArrayList<TaskFromSite> planCardlistforLoc = collectDataforLocationPlan();
            if (planCardlistforLoc != null && planCardlistforLoc.size() > 0) {
                emptyview.setVisibility(View.GONE);
                adapter = new PlanListAdapter(planCardlistforLoc, (TaskDetailActivity) context);
                recyclerView.setAdapter(adapter);
            } else {
                //  emptyview.setVisibility(View.VISIBLE);
                Toast.makeText(context, "No task assigned.", Toast.LENGTH_LONG).show();
                this.finish();

            }
        } else {
            ArrayList<TaskFromSite> planCardlist = collectDataforPlan();
            if (planCardlist != null || planCardlist.size() > 0) {
                emptyview.setVisibility(View.GONE);
                showPlanData(planCardlist);

            } else {
                // emptyview.setVisibility(View.VISIBLE);
                Toast.makeText(context, "No task assigned.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
        if (searchTaskView != null) {
            String s = searchTaskView.getQuery().toString();
            if (!s.isEmpty()) {
                adapter.getFilter().filter(s);
            }
        }
        searchTaskView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void showPlanData(ArrayList<TaskFromSite> planCardlist) {
        Collections.sort(planCardlist, new CustomComparator());

        adapter = new PlanListAdapter(planCardlist, (TaskDetailActivity) context);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getExtras() {
        extras = getIntent().getExtras();
        if (extras != null) {
            SiteID = extras.getInt("SITE_ID");
            siteName = extras.getString("SITE_NAME");
            parentAppID = extras.getInt("PARENTAPPID");
        }
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

    public class CustomComparator implements Comparator<TaskFromSite> {

        @Override
        public int compare(TaskFromSite lhs, TaskFromSite rhs) {
            if (lhs.getFormname() != null && rhs.getFormname() != null) {

                String lhsname = lhs.getFormname();
                String rhsname = lhs.getFormname();

                int count = rhsname.compareTo(lhsname);
                return count;
            }
            return 0;
        }
    }

    private ArrayList<TaskFromSite> collectDataforPlan() {
        ArrayList<TaskFromSite> list = new ArrayList<>();
        WorkOrderTaskDataSource workOrderTaskDataSource = new WorkOrderTaskDataSource(context);
        list = workOrderTaskDataSource.getPlanDatafromDB();
        return list;
    }

    private ArrayList<TaskFromSite> collectDataforLocationPlan() {
        ArrayList<TaskFromSite> list = new ArrayList<>();
        WorkOrderTaskDataSource workOrderTaskDataSource = new WorkOrderTaskDataSource(context);
        list = workOrderTaskDataSource.getPlanDataforLocfromDB(SiteID, parentAppID);
        return list;
    }

    private ArrayList<TaskFromSite> collectDataforFormPlan() {
        ArrayList<TaskFromSite> list = new ArrayList<>();
        WorkOrderTaskDataSource workOrderTaskDataSource = new WorkOrderTaskDataSource(context);
        list = workOrderTaskDataSource.getPlanDataforFormfromDB(SiteID);
        return list;
    }

    public int getWoid() {
        return woid;
    }

    public void setWoid(int woid) {
        this.woid = woid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG, "HomeUp Pressed");
                finish();

        }
        return true;
    }

    public int getFormid() {
        return formid;
    }

    public void setFormid(int formid) {
        this.formid = formid;
    }

    public int getSiteid() {
        return siteid;
    }

    public void setSiteid(int siteid) {
        this.siteid = siteid;
    }

    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public void getEventIDForTask(int mformid, int msiteid, String planname, int woid) {
        setFormid(mformid);
        setSiteid(msiteid);
        setPlanname(planname);
        setWoid(woid);
        onclickAppItem();
    }

    public void uploadFieldData() {

        Log.i(TAG, "Upload Field Data Called");
        Intent dataUpload = new Intent(context, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", 0);
        ((Activity) context).startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);
            if (dataSynced) {
                if (CheckNetwork.isInternetAvailable(context)) {
                    syncAlert();
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private int genServerEventID(int mobileAppID, int siteID, int userID,
                                 double lat, double longt, String deviceID, long eventDateTime) {

//		final EventDataSource eventSource = new EventDataSource ();
        final DEvent event = new DEvent();
        event.setSiteId(siteID);
        event.setMobileAppId(mobileAppID);
        event.setUserId(userID);
        event.setDeviceId(deviceID);
        event.setLatitude(lat);
        event.setLongitude(longt);
        event.setUserName(username);
        event.setEventDate(System.currentTimeMillis());
//        event.setEventDate(System.currentTimeMillis() - 86400000);

        EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(this, event, this.username, this.password, false);
        eventHandler.execute();

        return eventID;
    }


    public int genClientEventID(int mobileAppID, int siteID, int userID, double lat, double longt, String deviceID, long eventDateTime) {
        String generatedBy = "C";
        boolean isEventIdExists = false;
        EventDataSource eventData = new EventDataSource(context);

        int eventID = 0;
        long currntTime = System.currentTimeMillis();
        Log.i(TAG, "genClientEventID() Event Timestamp :" + currntTime);
        eventID = (int) currntTime;
        Log.i(TAG, "genClientEventID() Client Event :" + eventID);

        isEventIdExists = eventData.isEventIdExists(eventID);
        while (isEventIdExists) {
            eventID = (int) System.currentTimeMillis();
            isEventIdExists = eventData.isEventIdExists(eventID);
        }
        if (eventID > 0) {
            eventID = -(eventID);
        }
        System.out.println("eventt " + eventID);

        eventData.insertEventId(eventID, generatedBy, mobileAppID, siteID,
                userID, lat, longt, deviceID, eventDateTime,
                0, "");
        return eventID;
    }

    private void syncAlert() {
//		   SharedPref.putBoolean("RETRACE", true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Changes to forms");
        alertDialogBuilder.setMessage("Do You Want to download latest forms?");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(" YES ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                if (CheckNetwork.isInternetAvailable(context)) {
                    Intent metaIntent = new Intent(context, MetaSyncActivity.class);
                    context.startActivity(metaIntent);
                    //context.finish();
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }

            }

        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.dismiss();

            }
        });
    }

    public void onclickAppItem() {

        EventDataSource eventData = new EventDataSource(context);

        long eventDateTime = System.currentTimeMillis();
        userid = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);

        tracker = new GPSTracker(context);
        String loc = String.valueOf(tracker.getLocation());
        String deviceid = DeviceInfo.getDeviceID(context);

        eventID = eventData.pickEventID(formid, siteid, userid, tracker.getLocation(), DeviceInfo.getDeviceID(context));
        if (eventID == 0 && CheckNetwork.isInternetAvailable(context)) {
            eventID = genServerEventID(formid, siteid, userid, tracker.getLatitude()
                    , tracker.getLongitude(), DeviceInfo.getDeviceID(context), eventDateTime);
            Log.i(TAG, "EventID From Server:" + eventID);

        } else {
            postGetEvent();
        }
    }

    @Override
    public void onTaskCompleted(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {

                String result = (String) obj;

                if (result.equals("SUCCESS")) {
                    uploadFieldData();

                } else {
                    Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();

                }
            } else {
                EventResponseModel result = (EventResponseModel) obj;
                GlobalStrings.responseMessage = result.getMessage();

                if (result.isSuccess()) {
                    eventID = result.getData().getEventId();
                    setGeneratedEventID(result);

                    Log.i(TAG, "Event ID From Server:" + eventID);
                    postGetEvent();

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
            postGetEvent();

        }
    }

    public void postGetEvent() {

        long eventDateTime = System.currentTimeMillis();
//		UserDataSource userData = new UserDataSource();

        Log.i(TAG, "Current EventID:" + eventID);
        Log.i(TAG, "Event DateTime in milliecond:" + eventDateTime);

        if (eventID == 0) { //not created or could not connect to server yet.
            eventID = genClientEventID(getFormid(), getSiteid(), userid,
                    tracker.getLatitude(), tracker.getLongitude(), DeviceInfo.getDeviceID(getApplicationContext()),
                    eventDateTime);
            Log.i(TAG, "EventID From Client:" + eventID);

        }

        // SharedPref.putInt("APP_ID", getSelectedAppID());

        try {

            Intent LocationIntent = new Intent(this, TaskViewActivity.class);
            LocationIntent.putExtra("APP_ID", getFormid());
            LocationIntent.putExtra("EVENT_ID", eventID);
            LocationIntent.putExtra("PLANNAME", getPlanname());
            LocationIntent.putExtra("WOID", getWoid());
            LocationIntent.putExtra("SITE_ID", getSiteid());
            startActivity(LocationIntent);
            Log.i(TAG, "Location Activity with APP_ID:" + getFormid() + " SITE_ID:" + getSiteid() + " EVENT_ID:" + eventID);
            Log.i(TAG, "Redirecting to Location Activity start time:" + System.currentTimeMillis());

           /* if (values.size() == 1) {
                // TODO: 31-Jan-17 PHASE 1 APP INTEGRATION

                finish();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error to start Location Activity:" + e.getLocalizedMessage());
            CustomToast.showToast(this, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG);
        }
    }


    @Override
    public void onTaskCompleted() {
        postGetEvent();

    }

    @Override
    public void setGeneratedEventID(int id) {
        eventID = id;
        EventDataSource eventSource = new EventDataSource(context);
        long eventDateTime = System.currentTimeMillis();

        if (eventID > 0) {
            eventSource.insertEventId(eventID, "S", getFormid(), getSiteid(), userid,
                    tracker.getLatitude()
                    , tracker.getLongitude(), DeviceInfo.getDeviceID(context), eventDateTime,
                    0, "");
        }

    }

    @Override
    public void setGeneratedEventID(Object obj) {

        EventResponseModel res = (EventResponseModel) obj;
        eventID = res.getData().getEventId();

        EventDataSource eventSource = new EventDataSource(context);
        long eventDateTime = Long.parseLong(res.getData().getEventCreationDate());

        if (eventID > 0) {
            eventSource.insertEventId(eventID, "S", getFormid(), getSiteid(), userid,
                    tracker.getLatitude()
                    , tracker.getLongitude(), DeviceInfo.getDeviceID(context), eventDateTime,
                    0, "");
        }
    }


}

