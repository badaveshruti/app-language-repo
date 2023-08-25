package qnopy.com.qnopyandroid.ui.activity;

import static qnopy.com.qnopyandroid.util.Util.delete_All_Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.adapter.SiteAdapter;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.db.WorkOrderTaskDataSource;
import qnopy.com.qnopyandroid.interfacemodel.AlertButtonOnClick;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.responsemodel.EventResponseData;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class SiteActivity extends ProgressDialogActivity implements AlertButtonOnClick,
        OnTaskCompleted {
    private static final String TAG = "SiteActivity";
    List<Site> values;

    private String username = null;
    private String password = null;
    ListView listView;
    private String siteName = null;
    private int siteID = 0;
    private String userID = "0";
    Boolean retracing = false;
    Context context;
    public static Activity siteActivity;
    ActionBar actionBar;
    ProgressDialog procDialog = null;
    String msgBoard;
    static int META_SYNC_REQUEST_COUNT = 0;
    FloatingActionsMenu menuMultipleActions;
    FloatingActionButton addsite;
    TextView emptylist_view;
    LinearLayout site_list_container;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;
    Boolean isDownloadForms, isfromaddsite = false, isfromassignproject = false;
    private boolean isChecked = false;
    Bundle extras;
    SiteAdapter siteAdapter;
    private String displayNameSelected;
    private ProgressDialog progressDialog;
    private Location location;
    private double lat = 0;
    private double longt = 0;
    private long eventStartDate = 0L;
    private boolean isFromTasks = false;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    Toolbar toolbar;
    MaterialSearchView searchView;
    ArrayList<String> formlist = new ArrayList<>();
    Boolean selfsignupflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);

        toolbar = findViewById(R.id.site_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Select Project");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        location = GlobalStrings.CURRENT_GPS_LOCATION;

        if (location != null) {
            lat = location.getLatitude();
            longt = location.getLongitude();
        }

        listView = findViewById(R.id.siteListView);
        //   searchTV = (SearchView) findViewById(R.id.search_txt);
        site_list_container = findViewById(R.id.siteLis);
        emptylist_view = findViewById(R.id.empty_site);
        searchView = findViewById(R.id.search_view);
        menuMultipleActions = findViewById(R.id.multiple_actions);
        addsite = findViewById(R.id.action_addsite);

        context = this;
        siteActivity = this;
        GlobalStrings.currentContext = this;

        Util.setSharedPreferencesProperty(context, "CreateProjectActivity", "");
        Util.setSharedPreferencesProperty(context, "Activation_Code", "");
        Util.setSharedPreferencesProperty(context, "RollAppFormActivity", "");

        extras = getIntent().getExtras();
        if (extras != null) {
            isfromaddsite = extras.getBoolean("fromaddsite");
            isfromassignproject = extras.getBoolean("FromAssignProject");
            isFromTasks = extras.getBoolean(GlobalStrings.IS_FROM_ADD_EDIT_TASK);//will be used to select site and get back with site info to task activity to assign site
            eventStartDate = extras.getLong(GlobalStrings.EVENT_STAR_DATE); //this value is used to
            // send the date to create event screen in case of calendar user want to start event
            // from calendar screen
        }

/*        if (ScreenReso.isCalendarUser)
            menuMultipleActions.setVisibility(View.GONE);
        else
            menuMultipleActions.setVisibility(View.VISIBLE);*/

        menuMultipleActions.setVisibility(View.GONE);

        setUsername(Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME));
        setPassword(Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD));
        setUserID(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

        String companyID = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);

        //if(companyID!=null && companyID.equalsIgnoreCase("99999"))
        //  {
        addsite.setVisibility(View.VISIBLE);
        //emptylist_view.setText(R.string.nositealert);
        //}
//        else if(isfromassignproject)
//        {
//            addsite.setVisibility(View.GONE);
//        }else
//        {
//            addsite.setVisibility(View.GONE);
//            menuMultipleActions.setVisibility(View.GONE);
//
//        }

        UserDataSource ud = new UserDataSource(context);

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.addsite_customactionbar, null);

        ImageButton drawer = mCustomView.findViewById(R.id.imgbtnadd);

        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Toast.makeText(getApplicationContext(), "Add Site", Toast.LENGTH_LONG).show();
                //  Intent intent=new Intent(SiteActivity.this,AddSiteActivity.class);
                //    startActivity(intent);
            }
        });

//        checkGpsConnectivity();

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "Search Text:" + query);
                if (siteAdapter != null) {
                    siteAdapter.getFilter().filter(query);
                    siteAdapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (siteAdapter != null) {
                    siteAdapter.getFilter().filter(newText);
                    siteAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        addsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();
                Intent applicationIntent = new Intent(context, RollAppFormActivity.class);
                applicationIntent.putExtra("fromApplicationActivity", true);
                startActivity(applicationIntent);
            }
        });

        Util.isPlayServicesAvailable(context);

        populateSiteData();
    }

    @Override
    public void onBackPressed() {
        //11-Sep-17 STOP ALARM SERVICE

        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
        }
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else if (isfromaddsite) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
            Util.stopAlarm(context);

            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
            finish();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (menuMultipleActions.isExpanded()) {
                Rect outRect = new Rect();
                menuMultipleActions.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    menuMultipleActions.collapse();
            }

            try {
                if (getCurrentFocus() != null)
                    ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isChecked = Boolean.parseBoolean(Util.getSharedPreferencesProperty(context, GlobalStrings.IS_CAPTURE_LOG));
        GlobalStrings.CAPTURE_LOG = isChecked;

        String guid = Util.getSharedPreferencesProperty(context, getUsername());

        if (guid == null || guid.isEmpty()) {
            UserDataSource ud = new UserDataSource(context);
            guid = ud.getUser(getUsername()).getUserGuid();

            if (guid == null || guid.isEmpty()) {
                Util.reLogin(SiteActivity.this, getUsername(), getPassword());
                finish();
            } else {
                Util.setSharedPreferencesProperty(context, getUsername(), guid);
            }
        }
    }

    void SyncData() {
        if (CheckNetwork.isInternetAvailable(context)) {
            EventDataSource eventData = new EventDataSource(context);

            ArrayList<DEvent> eventList = eventData
                    .getClientGeneratedEventIDs(context);

            int count = eventList.size();

            if (count > 0) {
                EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(SiteActivity.this, null, username,
                        password, true);

                eventHandler.execute();

            } else {
                uploadFieldData();

            }
        } else {
            CustomToast.showToast((Activity) context,
                    getString(R.string.bad_internet_connectivity), 5);
        }
    }

    public void uploadFieldData() {

        Log.i(TAG, "Upload Field Data Called");
        Intent dataUpload = new Intent(this, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", 0);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
    }


    public void populateSiteData() {
        SiteDataSource siteData = new SiteDataSource(context);
        // listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        int userID = 0;
        try {
            userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Parsing Shared pref:" + e.getMessage());

            UserDataSource userSource = new UserDataSource(context);
            User user = userSource.getUser(getUsername());
            if (user != null) {
                userID = user.getUserID();
                Util.setSharedPreferencesProperty(context, GlobalStrings.USERID,
                        userID + "");
            } else {
                CustomToast.showToast((Activity) context, "User Not Found,Please Re-login.!", 4);
                Util.setLogout(SiteActivity.this);
                finish();
                return;
            }
        }

        values = siteData.getAllSitesForUser(userID);

        if (values == null) {
            Log.e(TAG, "Site Data List is Null");
            isDownloadForms = true;
        } else {
            isDownloadForms = false;
        }

        if (values != null && values.isEmpty()) {

            //     if (META_SYNC_REQUEST_COUNT >= 2)
            //   {
            //  Toast.makeText(context, "Failed to download forms", Toast.LENGTH_LONG).show();
            String companyID = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);

            //  if(companyID!=null && companyID.equalsIgnoreCase("99999")){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Create Project")
                    .setMessage("You don't have any project assigned. Please create your first project.")
                    .setCancelable(false)
                    .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, RollAppFormActivity.class);
                            intent.putExtra("fromApplicationActivity", true);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog dia = builder.create();
            dia.show();
            //      }
        } else {
            Collections.sort(values, new SiteActivity.CustomComparator());

            setSiteAdapter(values);

            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Site site = (Site) parent.getAdapter().getItem(position);//values.get(position);
                    siteName = site.getSiteName();
                    setSiteID(site.getSiteID());
                    Log.i(TAG, "Site List onItemClick():" + site.getSiteName());
                    Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID, site.getSiteID() + "");
                    Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME,
                            site.getSiteName());
                    Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_CARD, "INACTIVE");

                    if (isfromassignproject) {

                        Intent applicationIntent = new Intent(context, ShowuserlistActivity.class);

                        applicationIntent.putExtra("SITE_NAME", site.getSiteName());
                        applicationIntent.putExtra("SITE_ID", site.getSiteID());
                        applicationIntent.putExtra("fromassignuser", isfromassignproject);
                        startActivity(applicationIntent);
                    } else if (isFromTasks) {
                        Intent intent = new Intent();
                        intent.putExtra(GlobalStrings.SITE_DETAILS, site);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        onclickSiteItem(site);
                    }
                }
            });
        }
    }

    public void onclickSiteItem(Site siteObj) {
        siteID = siteObj.getSiteID();

        if (new SiteDataSource(this).isSiteTypeTimeSheet(siteID)) {
            Toast.makeText(this, getString(R.string.no_permission_to_create_event),
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (new SiteDataSource(this).isSiteTypeDemo(siteID)) {
            Toast.makeText(this, getString(R.string.no_permission_to_create_event),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SiteMobileAppDataSource appData = new SiteMobileAppDataSource(context);
        List<SSiteMobileApp> siteFormList = new ArrayList<>();

        siteFormList = appData.getAllAppsV16(siteObj.getSiteID());

        if (eventStartDate > 0) {
            //when start date is available then it is calendar user who needs to create event
            invokeApplicationActivity(siteName, siteObj.getSiteID());
        } else if (siteFormList.size() > 1)
            invokeApplicationActivity(siteName, siteObj.getSiteID());
        else if (siteFormList.size() == 1) {
            checkEventExist(siteFormList.get(0), siteObj.getSiteID());
        }

//        invokeApplicationActivity(siteObj.getSiteName(), siteObj.getSiteID());

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
    }

    void invokeApplicationActivity(String siteName, int siteID) {
        Log.i(TAG, "Redirect to Application with Sitename:" + siteName + " SiteID:" + siteID);

        Intent applicationIntent = new Intent(this, ApplicationActivity.class);

        applicationIntent.putExtra("SITE_NAME", siteName);
        applicationIntent.putExtra("SITE_ID", siteID);
        applicationIntent.putExtra("fromaddsite", isfromaddsite);
        applicationIntent.putExtra(GlobalStrings.EVENT_STAR_DATE, eventStartDate);

        startActivity(applicationIntent);
        Log.i(TAG, "\n\n\nRedirect to Application start time:- " + System.currentTimeMillis());
    }

    private void checkEventExist(SSiteMobileApp siteMobApp, int siteId) {
        displayNameSelected = siteMobApp.getDisplay_name();

        int eventID = 0;
        EventDataSource eventData = new EventDataSource(context);
        eventID = eventData.pickEventID(siteMobApp.getMobileAppId(), siteId,
                Integer.parseInt(userID),
                GlobalStrings.CURRENT_GPS_LOCATION, DeviceInfo.getDeviceID(this));

        if (eventID == 0 && CheckNetwork.isInternetAvailable(context)) {
            checkActiveEvents(siteMobApp.getMobileAppId(), siteId);
        } else {

            EventDataSource eventDataSource = new EventDataSource(this);
            ArrayList<DEvent> eventList = eventDataSource.getSiteEvents(siteMobApp.getMobileAppId(),
                    siteID);

            if (eventList.size() > 1)
                gotoStartNewScreen(siteMobApp);
            else
                startLocationActivity(eventID, siteMobApp.getMobileAppId(), eventList.get(0));
        }
    }

    private void gotoStartNewScreen(SSiteMobileApp siteMobApp) {
        Intent intent = new Intent(this, StartNewEventActivity.class);
        intent.putExtra(GlobalStrings.CURRENT_APPID, siteMobApp.getMobileAppId());
        intent.putExtra(GlobalStrings.CURRENT_SITEID, siteID);
        intent.putExtra(GlobalStrings.FORM_NAME, displayNameSelected);
        intent.putExtra(GlobalStrings.CURRENT_SITENAME, siteName);
        intent.putExtra(GlobalStrings.USERID, Integer.parseInt(userID));
        startActivity(intent);
    }

    private void checkActiveEvents(Integer mobileAppId, Integer siteId) {

        DEvent event = new DEvent();
        event.setSiteId(siteId);
        event.setMobileAppId(mobileAppId);
        event.setUserId(Integer.parseInt(userID));
        event.setDeviceId(DeviceInfo.getDeviceID(this));
        event.setLatitude(lat);
        event.setLongitude(longt);
        event.setUserName(username);
        event.setEventDate(System.currentTimeMillis());
        event.setEventStartDate(System.currentTimeMillis());

        AsyncEventCheck eventHandler = new AsyncEventCheck(event);
        eventHandler.execute();
    }

    private void startLocationActivity(int eventID, Integer mobileAppId, DEvent event) {
        Intent locationIntent = new Intent(this, LocationActivity.class);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(this) && isSplitScreenEnabled)
            locationIntent = new Intent(this, SplitLocationAndMapActivity.class);

        locationIntent.putExtra("APP_ID", mobileAppId);
        locationIntent.putExtra("EVENT_ID", eventID);

        locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(locationIntent);
    }

    class AsyncEventCheck extends AsyncTask<Void, Void, EventResponseModel> {

        private final DEvent event;
        private final AquaBlueServiceImpl mAquaBlueService;

        public AsyncEventCheck(DEvent event) {
            this.event = event;
            mAquaBlueService = new AquaBlueServiceImpl(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Please wait...");
        }

        @Override
        protected EventResponseModel doInBackground(Void... voids) {
            String guid = Util.getSharedPreferencesProperty(context, username);

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("deviceId", DeviceInfo.getDeviceID(context));
                jsonObject.put("siteId", event.getSiteId());
                jsonObject.put("mobileAppId", event.getMobileAppId());
                jsonObject.put("userGuid", guid);//"f8180e4a-3b36-11e5-9708-0ea7cb7cc776"
                jsonObject.put("userId", event.getUserId());
                jsonObject.put("eventDate", event.getEventDate());
                jsonObject.put("eventStartDate", event.getEventStartDate());
                jsonObject.put("createEventFlag", 0);

            } catch (JSONException e1) {
                e1.printStackTrace();
                Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
                return null;
            }

            return mAquaBlueService.generateEventIDFromServer(context.getResources()
                            .getString(R.string.prod_base_uri),
                    context.getResources().getString(R.string.prod_event_check), event,
                    username,
                    jsonObject);
        }

        @Override
        protected void onPostExecute(EventResponseModel eventResponse) {
            super.onPostExecute(eventResponse);
            dismissProgressDialog();
            if (eventResponse != null) {
                if (eventResponse.isSuccess()) {
                    Log.e("check events response", eventResponse.getMessage());
                    boolean hasActiveEvents = eventResponse.getMessage()
                            .equalsIgnoreCase("Active");

                    if (hasActiveEvents) {
                        showCreateEventAlert(eventResponse.getData());
                    } else {
                        goToCreateNewScreen(event.getMobileAppId());
                    }
                }
            }
        }
    }

    private void showCreateEventAlert(final EventResponseData event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_alert_events_new_existing, null, false);
        builder.setView(view);
        builder.setCancelable(false);

        TextView tvOngoingEvent = view.findViewById(R.id.tvOngoingEvent);

        UserDataSource userDataSource = new UserDataSource(this);
        String userName = userDataSource.getUserNameFromID(event.getCreatedBy() + "");
        String date = Util.getFormattedDateTime(event.getEventDate(), GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN);
        String label = "There is an ongoing event created by " + userName +
                " on " + date + ". Do you want to use this event or create new?";

        tvOngoingEvent.setText(label);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        view.findViewById(R.id.btnUseExisting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //save data to db and move to location activity
                insertEventDataToDb(event);
            }
        });

        view.findViewById(R.id.btnCreateNewEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                goToCreateNewScreen(event.getMobileAppId());
            }
        });

        view.findViewById(R.id.btnCancelAlert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void goToCreateNewScreen(int mobileAppId) {
        Intent intent = new Intent(this, CreateNewEventActivity.class);
        intent.putExtra(GlobalStrings.CURRENT_APPID, mobileAppId);
        intent.putExtra(GlobalStrings.CURRENT_SITEID, siteID);
        intent.putExtra(GlobalStrings.FORM_NAME, displayNameSelected);
        intent.putExtra(GlobalStrings.CURRENT_SITENAME, siteName);
        intent.putExtra(GlobalStrings.USERID, Integer.parseInt(userID));
        startActivity(intent);
    }

    private void insertEventDataToDb(EventResponseData data) {

        DEvent event = new DEvent();
        event.setEventId(data.getEventId());
        event.setMobileAppId(data.getMobileAppId());
        event.setSiteId(data.getSiteId());
        event.setUserId(Integer.parseInt(userID));
        event.setLatitude(data.getLatitude());
        event.setLongitude(data.getLongitude());
        event.setDeviceId(data.getDeviceId());
        event.setEventDate(data.getEventDate());
        event.setEventStartDate(data.getEventStartDate());
        event.setEventEndDate(data.getEventEndDate());
        event.setEventName(data.getEventName());

        String generatedBy = "S";
        EventDataSource eventData = new EventDataSource(context);
        eventData.insertEventData(event, generatedBy);

        Intent locationIntent = new Intent(context, LocationActivity.class);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(this) && isSplitScreenEnabled)
            locationIntent = new Intent(this, SplitLocationAndMapActivity.class);

        locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        locationIntent.putExtra("APP_ID", event.getMobileAppId());
        locationIntent.putExtra("EVENT_ID", event.getEventId());
        locationIntent.putExtra("fromaddsite", isfromaddsite);
        startActivity(locationIntent);

        finish();
    }

    /**
     * @return the username
     */
    String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the siteName
     */
    String getSiteName() {
        return siteName;
    }

    /**
     * @param siteName the siteName to set
     */
    void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * @return the siteID
     */
    int getSiteID() {
        return siteID;
    }

    /**
     * @param siteID the siteID to set
     */
    void setSiteID(int siteID) {
        this.siteID = siteID;
    }

//    public void showPopUp(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        popup.setOnMenuItemClickListener(this);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_site_actions, popup.getMenu());
//        popup.show();
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
//            case R.id.help:
//                Intent helpIntent = new Intent(context, HelpActivity.class);
//                startActivity(helpIntent);
//
//                return true;

            case R.id.nearby:

                if (CheckNetwork.isInternetAvailable(context)) {
                    Intent mapIntent = new Intent(context, MapForSiteActivity.class);
                    mapIntent.putExtra("PREV_CONTEXT", "LocationDetail");
                    mapIntent.putExtra("OPERATION", "nearby");
                    startActivity(mapIntent);
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }

                return true;

            case R.id.workorder_mytask:
                WorkOrderTaskDataSource wd = new WorkOrderTaskDataSource(context);
                boolean isPlanAvailable = wd.isDataAvailableforUser(getUserID());
                if (isPlanAvailable) {
                    Intent taskintent = new Intent(context, TaskDetailActivity.class);
                    startActivity(taskintent);
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
                } else {
                    Toast.makeText(context, "No Task Available.", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.enable_log:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                Util.setSharedPreferencesProperty(context, GlobalStrings.IS_CAPTURE_LOG, String.valueOf(isChecked));
                GlobalStrings.CAPTURE_LOG = isChecked;
                return true;

//            case R.id.action_delete_data:
//                FieldDataSource fd = new FieldDataSource(context);
//                AttachmentDataSource attachDataSource = new AttachmentDataSource(context);
//                if (fd.isFieldDataAvailableToSync() || attachDataSource.attachmentsAvailableToSync()) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Wait!")
//                            .setMessage("Looks like there is un-synced data.Do you want to proceed with reset?")
//                            .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    alertForDeletingData();
//
//                                }
//                            })
//                            .setNegativeButton("Cancel", null);
//
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//
//                } else {
//                    alertForDeletingData();
//                }
//                return true;
//            case R.id.update_apk:
//                PackageManager pm = this.getApplicationContext().getPackageManager();
//                String packageName = this.getApplicationContext().getPackageName();
//                int version = 0;
//                // TODO: 21-Mar-16  Link to Update app from Play Store
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("market://details?id=com.aqua.fieldbuddy"));
//                startActivity(intent);
//
//                return true;
            case R.id.download_forms:
                syncAlert();
                //  SyncData();
                return true;

//            case R.id.about:
//                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
//                alertBuilder.setMessage(Util.getAboutMsg(context));
//                alertBuilder.setTitle("Qnopy");
//                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                AlertDialog aboutalert = alertBuilder.create();
//                aboutalert.show();
//                return true;

            case R.id.logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert")
                        .setMessage("Are you sure to Logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                Util.setLogout(SiteActivity.this);
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog dia = builder.create();
                dia.show();

                return true;

//            case R.id.add_new_site:
//                Intent applicationIntent = new Intent(context, RollAppFormActivity.class);
//                applicationIntent.putExtra("fromApplicationActivity",true);
//                startActivity(applicationIntent);

//                Intent intent1 = new Intent(SiteActivity.this, AddSiteActivity.class);
//                intent1.putExtra("DOWNLOAD_FORMS", isDownloadForms);
//                startActivity(intent1);
////                overridePendingTransition(R.anim.right_to_left,
////                        R.anim.left_to_right);
//                return true;

            case R.id.app_preferences:
                Intent pref_intent = new Intent(SiteActivity.this, AppPreferencesActivity.class);
                startActivity(pref_intent);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;

//            case R.id.send_db:
//                if (CheckNetwork.isInternetAvailable(context)) {
//                    new SendDBTask(context).execute();
//                } else {
//                    CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), 10);
//
//                }
//                return true;

            case android.R.id.home:
                Util.stopAlarm(context);

                if (isfromaddsite) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    startActivity(intent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }
                } else {
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                    finish();
                }
                return true;

            default:
                return true;
        }
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


    @Override
    public void positiveButtonClick() {
        Boolean result = false;
        String mesg = null;
        UserDataSource userSource = new UserDataSource(context);
        result = userSource.deleteUser(getUsername(), getPassword());
        if (result) {
            mesg = "User " + "'" + getUsername() + "'"
                    + " Deleted Successfully";
        } else {
            mesg = "User Deletion Failed";
        }
        Toast.makeText(getApplicationContext(), mesg, Toast.LENGTH_LONG).show();
        Util.setLogout(SiteActivity.this);
        finish();
        // startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    public void negativeButtonClick() {
        Log.i(TAG, "Negative Button clicked");
    }

    void beforeSendEmail() {
        procDialog = new ProgressDialog(this);
        procDialog.setIndeterminate(true);
        procDialog.setCancelable(false);
        procDialog.setMessage("Uploading Database,Please wait...!");
        procDialog.show();
    }

    void afterSendEmail() {
        if ((procDialog != null) && (procDialog.isShowing())) {
            try {
                procDialog.dismiss();
                CustomToast.showToast((Activity) context, msgBoard,
                        Toast.LENGTH_SHORT);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "AfterSendEmail:" + e.getLocalizedMessage());
            }
        }
        // uploadFieldData();
    }

    private void syncAlert() {
//		   SharedPref.putBoolean("RETRACE", true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SiteActivity.this);

        alertDialogBuilder.setTitle("Changes to forms");
        alertDialogBuilder.setMessage("Do you want to download latest forms?");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(" YES ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                initiateMetaSync();
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.dismiss();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }


    void initiateMetaSync() {
        if (CheckNetwork.isInternetAvailable(getApplicationContext())) {
            Intent metaIntent = new Intent(context, MetaSyncActivity.class);
            startActivity(metaIntent);
//            overridePendingTransition(R.anim.right_to_left,
//                    R.anim.left_to_right);
            finish();
        } else {
            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_site_actions, menu);
        menu.findItem(R.id.filefolder).setVisible(false);
        MenuItem item = menu.findItem(R.id.add_new_site);
        String companyid = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);

//        if(companyid.equals("99999"))
//        {
//            item.setVisible(true);
//        }else
//        {
//            item.setVisible(false);
//        }


        menu.findItem(R.id.user).setTitle("Hi " + getUsername() + "!");
        AppPreferenceDataSource ds = new AppPreferenceDataSource(context);

        if (ds.isFeatureAvailable(GlobalStrings.KEY_EMERGENCY, Integer.parseInt(getUserID()))) {
            menu.findItem(R.id.nearby).setVisible(true);
        } else {
            menu.findItem(R.id.nearby).setVisible(false);
        }

        isChecked = Boolean.parseBoolean(Util.getSharedPreferencesProperty(context,
                GlobalStrings.IS_CAPTURE_LOG));
        menu.findItem(R.id.enable_log).setChecked(isChecked);

        item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onTaskCompleted(Object obj) {

        if (obj != null && obj instanceof String) {

            String result = (String) obj;

            if (result.equals("SUCCESS")) {
                uploadFieldData();

            } else {
                Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
        }
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

    public class CustomComparator implements Comparator<Site> {
        @Override
        public int compare(Site lhs, Site rhs) {
            return lhs.getSiteName().compareTo(rhs.getSiteName());
        }
    }

    void setSiteAdapter(List<Site> data) {
        if (data.size() > 0) {
            site_list_container.setVisibility(View.VISIBLE);
            emptylist_view.setVisibility(View.GONE);
            siteAdapter = new SiteAdapter(context,
                    R.layout.list_item, data);
            listView.setAdapter(siteAdapter);
        } else {
            site_list_container.setVisibility(View.GONE);
            emptylist_view.setVisibility(View.VISIBLE);
        }
    }
}
