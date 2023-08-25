package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.SubmittalsAdapter;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CompletionPercentageDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.SyncStatusDataSource;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.SubmittalModel;
import qnopy.com.qnopyandroid.responsemodel.SubmittalResponseCollector;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class DashboardActivity extends ProgressDialogActivity implements
        OnTaskCompleted {

    private static final String TAG = "DashboardActivity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SubmittalsAdapter adapter;
    Context context;
    int SYNC_TIME_PERIOD = -1;//Get Back From Current time to
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    SyncStatusDataSource SyncStatusob;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;

    private int eventID = 0;
    private String locID = "0";
    private int siteID = 0;
    private int userID = 0;
    private int mobileAppID = 0;
    private int setID = 0, count = 0;
    private String siteName = "";
    private String locationName = "", username, password, userGuid, lastSyncDate;
    Bundle extras;
    ActionBar actionBar;
    private ProgressDialog progressDialog;
    int eventcount = 0;
    public static EventData itemToDelete = new EventData();


    LinearLayout search_container;
    TextView emptyview, activetb, closedtb, conflicttb;

    SearchView search_by;
    int SELECTED_TAB = 0;
    int ACTIVE = 1, CLOSED = 0, CONFLICT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        context = this;

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Submittals");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Util.setOverflowButtonColor(DashboardActivity.this, Color.BLACK);

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(context, username);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

//
//        activetb = (TextView) findViewById(R.id.activetab);
//        closedtb = (TextView) findViewById(R.id.closedtab);
//        conflicttb = (TextView) findViewById(R.id.conflicttab);

        activetb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SELECTED_TAB = 0;
                activetb.setBackgroundColor(getResources().getColor(R.color.qnopy_teal));
                activetb.setTextColor(getResources().getColor(R.color.white));

                closedtb.setBackgroundColor(0x00000000);
                closedtb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));
                conflicttb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));
                conflicttb.setBackgroundColor(0x00000000);

                ArrayList<EventData> list = collectData(ACTIVE);
                showData(list);

            }
        });

        closedtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SELECTED_TAB = 1;
                closedtb.setBackgroundColor(getResources().getColor(R.color.qnopy_teal));
                closedtb.setTextColor(getResources().getColor(R.color.white));

                activetb.setBackgroundColor(0x00000000);
                activetb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));

                conflicttb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));
                conflicttb.setBackgroundColor(0x00000000);

                ArrayList<EventData> list = collectData(CLOSED);
                showData(list);

            }
        });

        conflicttb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SELECTED_TAB = 2;
                conflicttb.setBackgroundColor(getResources().getColor(R.color.qnopy_teal));
                conflicttb.setTextColor(getResources().getColor(R.color.white));

                activetb.setBackgroundColor(0x00000000);
                activetb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));

                closedtb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));
                closedtb.setBackgroundColor(0x00000000);

                ArrayList<EventData> list = collectData(CONFLICT);
                showData(list);

            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        emptyview = findViewById(R.id.empty);
        search_by = findViewById(R.id.searchby);
        search_by.setQueryHint("Search & select project");

        search_by.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                adapter.getFilter().filter(query);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }
        });


        SyncStatusob = new SyncStatusDataSource(context);
        lastSyncDate = SyncStatusob.getLastSyncDate(userID, GlobalStrings.SYNC_DATE_TYPE_EVENT);
        Log.i(TAG, "LastSync date for card:" + lastSyncDate);
        ArrayList<EventData> list = collectData(ACTIVE);
        showData(list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.city_data_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_sync:
                if (CheckNetwork.isInternetAvailable(context)) {

                    PostMessageTask pt = new PostMessageTask();
                    pt.execute();
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }

                break;
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                //startActivity(new Intent(context, AddNewCardActivity.class));
                break;
        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {

            if (data.hasExtra("SYNC_FLAG")) {
                long date = System.currentTimeMillis();

                boolean eventClosed = data.getBooleanExtra("SYNC_FLAG", false);
                long eventEndDate = Long.parseLong(data.getStringExtra("EVENT_END_DATE"));
                boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);

                if (eventEndDate < 1) {
                    eventEndDate = date;
                }
                if (dataSynced && eventClosed) {
                    EventDataSource eventData = new EventDataSource(context);
                    CompletionPercentageDataSource cp = new CompletionPercentageDataSource(context);

                    eventData.closeEventStatus(itemToDelete.getMobAppID(), itemToDelete.getSiteID(), eventEndDate, itemToDelete.getEventID() + "");
                    cp.truncatePercentageByRollAppID_And_SiteID(itemToDelete.getSiteID() + "", itemToDelete.getMobAppID() + "");
//                    SharedPref.resetCamOrMap();

                    Toast.makeText(getApplicationContext(),
                            "The Event has been closed.", Toast.LENGTH_LONG)
                            .show();
                    adapter.removeItem(itemToDelete);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "The event can not be closed,please try again!",
                            Toast.LENGTH_LONG).show();
                }

            }
            Log.i(TAG, "onActivityResult() SYNC_ACTIVITY End time:" + System.currentTimeMillis());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<EventData> list;

        if (SELECTED_TAB == 0) {
            list = collectData(ACTIVE);


        } else if (SELECTED_TAB == 1) {
            list = collectData(CLOSED);

        } else {
            list = collectData(CONFLICT);

        }

        showData(list);

    }


    public ArrayList<EventData> collectData(int event_status) {
        ArrayList<EventData> list = new ArrayList<>();
        EventDataSource eds = new EventDataSource(context);
        list = eds.getSubmittalsList(event_status);
        return list;
    }

    public void showData(ArrayList<EventData> list) {
        // Collections.sort(list, new CustomComparator());

        if (list == null || list.size() < 1) {
            emptyview.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyview.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new SubmittalsAdapter(list, DashboardActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }


    @Override
    public void onTaskCompleted(Object obj) {


        FieldDataSource fieldData = new FieldDataSource(context);
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(context);
        EventDataSource eventData = new EventDataSource(context);

        if (obj instanceof EventResponseModel) {

            EventResponseModel result = (EventResponseModel) obj;
            GlobalStrings.responseMessage = result.getMessage();

            if (result.isSuccess()) {

                setGeneratedEventID(result);

                fieldData.updateEventID(itemToDelete.getEventID(), result.getData().getEventId());
                attachDataSrc.updateEventID(itemToDelete.getEventID(), result.getData().getEventId());
                eventData.updateEventID(itemToDelete.getEventID(), result);
                new SampleMapTagDataSource(context).
                        updateEventID_SampleMapTag(itemToDelete.getEventID() + "",
                                result.getData().getEventId() + "");

                itemToDelete.setEventID(result.getData().getEventId());

                if (CheckNetwork.isInternetAvailable(context)) {
                    uploadFieldDataBeforeEndEvent();

                } else {
                    CustomToast.showToast((Activity) context,
                            getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG);
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

        } else {
            Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
        }
    }

    public void uploadFieldDataBeforeEndEvent() {
        Intent dataUpload = new Intent(context, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", itemToDelete.getEventID());
        dataUpload.putExtra("CLOSE_EVENT", true);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onTaskCompleted() {

    }

    @Override
    public void setGeneratedEventID(int id) {

    }

    @Override
    public void setGeneratedEventID(Object obj) {
        EventResponseModel res = (EventResponseModel) obj;
        int id = res.getData().getEventId();
        Log.i(TAG, "setGeneratedEventID() Old Event:" + itemToDelete.getEventID() + " new eventID :" + id);
    }


    public class CustomComparator implements Comparator<EventData> {

        @Override
        public int compare(EventData lhs, EventData rhs) {
//            if (lhs.getMobAppName() != null && rhs.getMobAppName() != null) {
//
//                Long date1 = Long.parseLong(lhs.getTxtDate());
//                Long date2 = Long.parseLong(rhs.getTxtDate());
//
//                int count = date2.compareTo(date1);
//                return count;
//
//            }
            return 0;
        }
    }


    // Handles sign-up request in back ground
    private class PostMessageTask extends AsyncTask<MediaType, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Refreshing...Please wait!");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }// end of onPreExecute

        @Override
        protected String doInBackground(MediaType... params) {

            SubmittalResponseCollector mResponse = new SubmittalResponseCollector();
            List<SubmittalModel> mSubmittalList = new ArrayList<>();
            String response;
            try {
                if (null != mAquaBlueService) {

                    mResponse = mAquaBlueService.getEventList(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.download_eventlist),
                            userID + "", lastSyncDate);

                    if (null != mResponse) {
                        if (mResponse.isSuccess()) {
                            response = "SUCCESS";

                            String newSyncTime_server = mResponse.getData().getLastSyncDate() + "";
                            Log.i(TAG, "All submittal last sync time:" + newSyncTime_server);

                            if (mResponse.getData().getEventList().size() > 0) {

                                mSubmittalList = mResponse.getData().getEventList();
                                // TODO: 11/10/2017 INSERT EVENT LIST
                                EventDataSource eds = new EventDataSource(context);
                                eds.saveSubmittalsList(mSubmittalList, userID);

                                SyncStatusob = new SyncStatusDataSource(context);
                                // TODO: 24-May-16 Update LastSyncDate in d_sync_status table
                                SyncStatusob.insertLastSyncDate(userID,
                                        Long.valueOf(newSyncTime_server),
                                        GlobalStrings.SYNC_DATE_TYPE_EVENT);
                            }
                        } else {
                            GlobalStrings.responseMessage = mResponse.getMessage();
                            response = mResponse.getResponseCode().toString();
                            if (mResponse.getResponseCode() == HttpStatus.LOCKED) {
                                response = HttpStatus.LOCKED.toString();
                            }
                            if (mResponse.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                                response = HttpStatus.NOT_ACCEPTABLE.toString();
                            }
                            if (mResponse.getResponseCode() == HttpStatus.NOT_FOUND) {
                                response = HttpStatus.NOT_FOUND.toString();
                            }
                            if (mResponse.getResponseCode() == HttpStatus.BAD_REQUEST) {
                                response = HttpStatus.BAD_REQUEST.toString();
                            }
                        }
                    } else {
                        response = "RETRY";
                    }
                } else {
                    response = "RETRY";
                }

            } catch (Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.e(TAG, "All submittals doInBackground() Exception:" + e.getMessage());
                }

                return null;

            }

            return response;
        }// end ofdoInBackground

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, " onPostExecute: Result = " + result);

            showResult(result);

        }// end of onPostExecute
    }// end of PostMessageTask


    // Method to display the result
    private void showResult(String result) {
        Log.d(TAG, " All Submittals sync result= " + result);
        if (result != null) {
            if (result.equals("SUCCESS")) {
                dismissDashProgressDialog();

                SELECTED_TAB = 0;
                activetb.setBackgroundColor(getResources().getColor(R.color.qnopy_teal));
                activetb.setTextColor(getResources().getColor(R.color.white));

                closedtb.setBackgroundColor(0x00000000);
                closedtb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));
                conflicttb.setTextColor(getResources().getColor(R.color.rectangle_primary_color));
                conflicttb.setBackgroundColor(0x00000000);

                ArrayList<EventData> list = collectData(ACTIVE);
                showData(list);

            } else if (result.equals(HttpStatus.NOT_ACCEPTABLE.toString())) {
                dismissDashProgressDialog();

                Toast.makeText(this, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
//                launchSiteActivity();

            } else if (result.equals("RETRY")) {
                dismissDashProgressDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
//                launchSiteActivity();

            } else if (result.equals(HttpStatus.LOCKED.toString()) || result.equals(HttpStatus.NOT_FOUND.toString())) {

                dismissDashProgressDialog();
                //Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);

            } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                dismissDashProgressDialog();
                Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
//                launchSiteActivity();
            }
        } else {
            dismissDashProgressDialog();
            Toast.makeText(getApplicationContext(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
//            launchSiteActivity();
        }

    }// end of showResult

    void dismissDashProgressDialog() {
        progressDialog.dismiss();
    }

//    void launchSiteActivity() {
//        finish();
//        Intent applicationIntent = new Intent(context, SiteActivity.class);
//        startActivity(applicationIntent);
//    }
}
