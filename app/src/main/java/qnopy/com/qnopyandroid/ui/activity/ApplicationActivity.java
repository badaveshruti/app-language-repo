package qnopy.com.qnopyandroid.ui.activity;

import static qnopy.com.qnopyandroid.util.Util.delete_All_Log;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.adapter.MainFormAdapter;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.db.FormSitesDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.db.WorkOrderTaskDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
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
import qnopy.com.qnopyandroid.uiutils.BaseActivity;
import qnopy.com.qnopyandroid.uiutils.DividerItemDecoration;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

@AndroidEntryPoint
public class ApplicationActivity extends BaseActivity implements OnTaskCompleted {

    private static final String TAG = "ApplicationActivity";
    public static Activity applicationActivity;
    protected int userID = 0;
    List<SSiteMobileApp> values;
    //    List<SSiteMobileApp> stdforms;
    //    List<SSiteMobileApp> stdforms;
    HashMap<String, List<SSiteMobileApp>> sectionedList;

    //    List<MobileApp> childAppList = null;
    String username = null;
    String password = null;
    Context context;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final int PHONE_STATE_PERMISSION_REQUEST_CODE = 2;
    int selectedAppID = 0;
    int eventID = 0;
    ProgressDialog procDialog = null;

    int siteID = 0;
    int companyID = 0;
    String siteName = null;
    Bundle extras;

    Location loc = null;
    double lat = 0;
    double longt = 0;
    //    ListView listView;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    androidx.appcompat.app.ActionBar actionBar;
    String msgBoard;
    //    TextView siteMap;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;
    Toolbar toolbar;

    TextView emptylist_view;
    LinearLayout form_list_container;
    MaterialSearchView searchView;
    MainFormAdapter adapter;
    Boolean isfromaddsite = false;
    static public final int REQUEST_LOCATION = 1;
    String mLocationPermission;
    boolean mGpsPermissionStatus;
    double mLatitude = 0, mLongitude = 0;
    private String guid;
    private String displayNameSelected;
    private ProgressDialog progressDialog;
    private long eventStartDate = 0L;

    @Inject
    FormSitesDataSource formSitesDataSource;
    @Inject
    LocationDataSource locationDataSource;
    @Inject
    MobileAppDataSource mobileAppSource;
    private boolean isAppTypeNoLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        context = this;
        applicationActivity = this;
        GlobalStrings.currentContext = this;

        mGpsPermissionStatus = checkWriteExternalPermission();
        Log.e("gpsPermissionStatus", "onCreate: " + mGpsPermissionStatus);

        try {
            if (mGpsPermissionStatus && GlobalStrings.CURRENT_GPS_LOCATION != null) {
                mLatitude = GlobalStrings.CURRENT_GPS_LOCATION.getLatitude();
                mLongitude = GlobalStrings.CURRENT_GPS_LOCATION.getLongitude();
            } else {
                mLatitude = 0.00;
                mLongitude = 0.00;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLatitude = 0.00;
            mLongitude = 0.00;
        }

        recyclerView = findViewById(R.id.appListView);
        toolbar = findViewById(R.id.form_toolbar);
        form_list_container = findViewById(R.id.formLis);
        emptylist_view = findViewById(R.id.empty_form);
        searchView = findViewById(R.id.search_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setSupportActionBar(toolbar);

        extras = getIntent().getExtras();

        if (extras != null) {
            siteID = extras.getInt("SITE_ID");
            siteName = extras.getString("SITE_NAME");
            isfromaddsite = extras.getBoolean("fromaddsite");
            eventStartDate = extras.getLong(GlobalStrings.EVENT_STAR_DATE); //this value is used to
            // send the date to create event screen in case of calendar user want to start event
            // from calendar screen
        } else {
            siteID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID));
            siteName = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME);
        }

        if (isfromaddsite) {
            boolean isFirstTime = (Util.getSharedPreferencesProperty(context, GlobalStrings.IS_FIRST_TIME_LAUNCH) == null);

            if (isFirstTime) {
                LayoutInflater factory = LayoutInflater.from(context);
                final View DialogView = factory.inflate(R.layout.rollformlayout, null);
                final androidx.appcompat.app.AlertDialog Dialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
                Dialog.setCanceledOnTouchOutside(false);
                Dialog.setView(DialogView);
                Button s = DialogView.findViewById(R.id.btn_yes);
                s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Dialog.dismiss();
                        Util.setSharedPreferencesProperty(context, GlobalStrings.IS_FIRST_TIME_LAUNCH, "false");
//                    Intent intent = new Intent(context, MetaSyncActivity.class);
//                    // intent.putExtra("ArrayList",formlist);
//                    intent.putExtra("fromaddsite", true);
//                    startActivity(intent);
                    }
                });
                Dialog.show();

            }
        }

        Log.i(TAG, "siteID:" + siteID);
        Log.i(TAG, "siteName:" + siteName);

        loc = GlobalStrings.CURRENT_GPS_LOCATION;

        if (loc != null) {
            lat = loc.getLatitude();
            longt = loc.getLongitude();
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.select_form));
        }

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i(TAG, "Search Text:" + query);
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onResume() {
        super.onResume();

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        guid = Util.getSharedPreferencesProperty(context, username);

        companyID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID));

        try {
            userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Parsing Shared pref:" + e.getMessage());

            UserDataSource userSource = new UserDataSource(context);
            User user = userSource.getUser(username);

            if (user != null) {
                userID = user.getUserID();
                Util.setSharedPreferencesProperty(context, GlobalStrings.USERID, userID + "");
            }
        }
        populateAppData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_site_actions, menu);
        MenuItem item = menu.findItem(R.id.add_new_site);
        String companyid = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);

//        if(companyid.equals("99999"))
//        {
//            item.setVisible(true);
//        }else
//        {
//            item.setVisible(false);
//        }

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.findItem(R.id.user).setTitle("Hi " + username + "!");
        menu.findItem(R.id.enable_log).setVisible(false);

        AppPreferenceDataSource ds = new AppPreferenceDataSource(context);
        menu.findItem(R.id.filefolder).setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_PROJECT_FILE, getUserID()));
        menu.findItem(R.id.nearby).setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_EMERGENCY, getUserID()));

        MenuItem item1 = menu.findItem(R.id.action_search).setVisible(false);
        searchView.setMenuItem(item1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Log.i(TAG, "Option Item Selected:" + item.getTitle());
        switch (item.getItemId()) {

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

//            case R.id.action_delete_data:
//
//                FieldDataSource fd = new FieldDataSource(context);
//                AttachmentDataSource attachDataSource = new AttachmentDataSource(context);
//
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

            case R.id.workorder_mytask:

                WorkOrderTaskDataSource wd = new WorkOrderTaskDataSource(context);
                boolean isPlanAvailable = wd.isDataAvailableforUser(getUserID() + "");
                if (isPlanAvailable) {
                    Intent taskintent = new Intent(context, TaskDetailActivity.class);
                    taskintent.putExtra("SITE_ID", siteID);
                    startActivity(taskintent);
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
                } else {
                    Toast.makeText(context, "No Task Available.", Toast.LENGTH_LONG).show();
                }

                return true;

//            case R.id.update_apk:
//                // TODO: 21-Mar-16  Link to Update app from Play Store
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("market://details?id=com.aqua.fieldbuddy"));
//                startActivity(intent);
//
//                return true;
            case R.id.download_forms:
                syncAlert();
//                SyncData();
                return true;

//            case R.id.add_new_site:
//
//                Intent applicationIntent = new Intent(context, RollAppFormActivity.class);
//                applicationIntent.putExtra("fromApplicationActivity",true);
//                startActivity(applicationIntent);
//                return true;

//                   Intent intent1 = new Intent(context, CreateProjectActivity.class);
//                // intent1.putExtra("GOTO_SITE_ACTIVITY", true);
//                startActivity(intent1);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
//                return true;

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
            case R.id.app_preferences:
                Intent pref_intent = new Intent(context, AppPreferencesActivity.class);
                startActivity(pref_intent);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;

            case R.id.filefolder:

                List<FileFolderItem> list = new FileFolderDataSource(context).getHomeFileFolderItemList(siteID + "");

                if (list.size() < 1) {
                    startActivity(new Intent(context, FileFolderSyncActivity.class));
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
                } else {
                    startActivity(new Intent(context, FileFolderMainActivity.class));
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
                }

                return true;

//            case R.id.send_db:
//
//                if (CheckNetwork.isInternetAvailable(context)) {
//                    new SendDBTask(context).execute();
//                } else {
//                    CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), 10);
//
//                }
//
//                return true;

            case android.R.id.home:
                super.onBackPressed();
                Log.i(TAG, "Back Pressed");
                if (isfromaddsite) {
//                    startActivity(new Intent(context, MainDrawerActivity.class));
                    startActivity(new Intent(context, HomeScreenActivity.class));
                    //                    Intent intent = new Intent(context,MainDrawerActivity.class);
////                    intent.setAction(Intent.ACTION_MAIN);
////                    intent.addCategory(Intent.CATEGORY_HOME);
//                    startActivity(intent);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        finishAffinity();
//                    }

                } else {
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                }
                finish();
            default:
                return false;
        }
    }

    private static final int FILE_SELECT_CODE = 0;

    private void syncAlert() {
//		   SharedPref.putBoolean("RETRACE", true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ApplicationActivity.this);

        alertDialogBuilder.setTitle(getString(R.string.changes_to_forms));
        alertDialogBuilder.setMessage(getString(R.string.download_latest_forms));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                if (CheckNetwork.isInternetAvailable(context)) {
                    Intent metaIntent = new Intent(context, MetaSyncActivity.class);
                    startActivity(metaIntent);
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
                    finish();
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.dismiss();

            }
        });
                       /*
                           // set neutral button: Exit the app message
		   				alertDialogBuilder.setNeutralButton("Exit the app",new DialogInterface.OnClickListener() {
		   						public void onClick(DialogInterface dialog,int id) {
		   								// exit the app and go to the HOME
		   								MainActivity.this.finish();
		   	 }
		   				});
		   				*/

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    void beforeSendEmail() {
        procDialog = new ProgressDialog(this);
        procDialog.setIndeterminate(true);
        procDialog.setCancelable(false);
        procDialog.setMessage("Uploading database, please wait...!");
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
                Log.e(TAG, "AfterSendDB:" + e.getLocalizedMessage());
            }
        }
        // uploadFieldData();
    }

    public int getUserID() {
        return this.userID;
    }

    public int getSelectedAppID() {
        return this.selectedAppID;
    }

    public void setSelectedAppID(int appID) {
        this.selectedAppID = appID;
    }

    public void populateAppData() {
        SiteMobileAppDataSource appData = new SiteMobileAppDataSource(context);

        values = appData.getAllAppsV16(siteID);

        if (values.size() > 1) {
            Collections.sort(values, new CustomComparator());
        }
        setAdapter((ArrayList<SSiteMobileApp>) values);
    }

    public void onclickAppItem(SSiteMobileApp selectedItem) {
        displayNameSelected = selectedItem.getDisplay_name();

        setSelectedAppID(selectedItem.getMobileAppId());
        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_APPNAME, selectedItem.getDisplay_name_roll_into_app());

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }

        EventDataSource eventData = new EventDataSource(context);

        long eventDateTime = System.currentTimeMillis();

        eventID = eventData.pickEventID(getSelectedAppID(), siteID, getUserID(),
                GlobalStrings.CURRENT_GPS_LOCATION, DeviceInfo.getDeviceID(this));

        if (eventStartDate > 0) {//for calendar purpose
            goToCreateNewScreen();
        } else if (eventID == 0 && CheckNetwork.isInternetAvailable(context)) {
            if (!isfromaddsite)
                checkActiveEvents();
            else
                eventID = genServerEventID(getSelectedAppID(), siteID, getUserID(), mLatitude
                        , mLongitude, DeviceInfo.getDeviceID(this), eventDateTime);

            Log.i(TAG, "EventID From Server:" + eventID);
        } else if (eventID == 0 && !CheckNetwork.isInternetAvailable(context)) {
            goToCreateNewScreen();
        } else {
            postGetEvent();
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
        String label = getString(R.string.there_is_an_ongoing_event_created) + " " + userName
                + " " + getString(R.string.on) + " " + date
                + getString(R.string.do_you_want_to_use_this_event);

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
                goToCreateNewScreen();
            }
        });

        view.findViewById(R.id.btnCancelAlert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void insertEventDataToDb(EventResponseData data) {

        DEvent event = new DEvent();
        event.setEventId(data.getEventId());
        event.setMobileAppId(data.getMobileAppId());
        event.setSiteId(data.getSiteId());
        event.setUserId(userID);
        event.setLatitude(data.getLatitude());
        event.setLongitude(data.getLongitude());
        event.setDeviceId(data.getDeviceId());
        event.setEventDate(data.getEventDate());
        event.setEventStartDate(data.getEventStartDate());
        event.setEventEndDate(data.getEventEndDate());
        event.setEventName(data.getEventName());
        event.setEventUserName(data.getEventUserName());

        String generatedBy = "S";
        EventDataSource eventData = new EventDataSource(context);
        eventData.insertEventData(event, generatedBy);

        isAppTypeNoLoc = formSitesDataSource.isAppTypeNoLoc(data.getMobileAppId() + "", siteID + "");

        if (isAppTypeNoLoc) {
            showFormDetailsScreen(data.getEventId(), data.getMobileAppId());
            return;
        }

        Intent locationIntent = new Intent(context, LocationActivity.class);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(this) && isSplitScreenEnabled)
            locationIntent = new Intent(context, SplitLocationAndMapActivity.class);

        locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        locationIntent.putExtra("APP_ID", event.getMobileAppId());
        locationIntent.putExtra("EVENT_ID", event.getEventId());
        locationIntent.putExtra("fromaddsite", isfromaddsite);
        startActivity(locationIntent);

        if (!ScreenReso.isLimitedUser)
            SiteActivity.siteActivity.finish();

        ApplicationActivity.applicationActivity.finish();
        finish();
    }

    //20/10/22 if the siteType is no_loc then the event will show the default location form directly w\o location screen
    //the default location taken is currently in lowest id and very first
    private void showFormDetailsScreen(int eventId, Integer appId) {

        int serverEventId = eventId;

        ArrayList<qnopy.com.qnopyandroid.clientmodel.Location> locations = locationDataSource.getDefaultLocation(siteID, appId);

        if (locations.size() >= 1) {

            qnopy.com.qnopyandroid.clientmodel.Location location = locations.get(0);
            String locName = location.getLocationName();
            String locationId = location.getLocationID();
            String deviceId = DeviceInfo.getDeviceID(this);
            String userId = "0";

            try {
                userId = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in parsing Shared preferences for userID:" + e.getMessage());

                UserDataSource userData = new UserDataSource(this);
                User newUser = userData.getUser(username);
                if (newUser != null) {
                    userId = newUser.getUserID() + "";
                }
            }

            if (eventId < 0) {
                serverEventId = new EventDataSource(this).getServerEventID(eventId + "");
            }

            String dispAppName = new SiteMobileAppDataSource(this)
                    .getMobileAppDisplayNameRollIntoApp(appId, siteID);

            Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_APPNAME, dispAppName);

            Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_LOCATIONID, locationId);
            Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_LOCATIONNAME, locName);
            Util.setSharedPreferencesProperty(this, GlobalStrings.SESSION_USERID, userId);
            Util.setSharedPreferencesProperty(this, GlobalStrings.SESSION_DEVICEID, deviceId);

            List<MobileApp> childAppList = mobileAppSource.getChildApps(appId, siteID, locationId);

            int maxApps = childAppList.size();

            if (maxApps == 0) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.no_forms_for_this_location), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent locationDetailIntent = new Intent(this,
                    LocationDetailActivity.class);

            locationDetailIntent.putExtra("EVENT_ID", serverEventId);
            locationDetailIntent.putExtra("LOCATION_ID", locationId);
            locationDetailIntent.putExtra("APP_ID", appId);
            locationDetailIntent.putExtra("SITE_ID", siteID);
            locationDetailIntent.putExtra("SITE_NAME", siteName);
            locationDetailIntent.putExtra("APP_NAME", dispAppName);
            String cocId = null;
            locationDetailIntent.putExtra("COC_ID", cocId);

            locationDetailIntent.putExtra("LOCATION_NAME", locName);
            locationDetailIntent.putExtra("LOCATION_DESC", location.getLocationDesc() == null ? "" : location.getLocationDesc());
            locationDetailIntent.putExtra(GlobalStrings.FORM_DEFAULT, location.getFormDefault());

            try {
                startActivity(locationDetailIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "no_loc site event creation Error in Redirecting to Details Form:" + e.getMessage());
                Toast.makeText(this, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void checkActiveEvents() {

        DEvent event = new DEvent();
        event.setSiteId(siteID);
        event.setMobileAppId(selectedAppID);
        event.setUserId(userID);
        event.setDeviceId(DeviceInfo.getDeviceID(this));
        event.setLatitude(lat);
        event.setLongitude(longt);
        event.setUserName(username);
        event.setEventDate(System.currentTimeMillis());
        event.setEventStartDate(System.currentTimeMillis());

        AsyncEventCheck eventHandler = new AsyncEventCheck(event);
        eventHandler.execute();
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    public void postGetEvent() {

        if (eventID == 0) { //not created or could not connect to server yet.
            //commented on 14 May, 2020 not allow to create offline events
      /*      eventID = genClientEventID(getSelectedAppID(), siteID, getUserID(),
                    mLatitude, mLongitude, DeviceInfo.getDeviceID(getApplicationContext()),
                    eventDateTime);*/
        }

        try {

            Intent locationIntent = new Intent(this, LocationActivity.class);

            boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                    GlobalStrings.ENABLE_SPLIT_SCREEN);

            if (Util.isTablet(this) && isSplitScreenEnabled)
                locationIntent = new Intent(context, SplitLocationAndMapActivity.class);

            locationIntent.putExtra("APP_ID", getSelectedAppID());
            locationIntent.putExtra("EVENT_ID", eventID);

            if (!ScreenReso.isLimitedUser || !ScreenReso.isCalendarUser)
                locationIntent.putExtra("fromaddsite", isfromaddsite);

            locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            EventDataSource eventDataSource = new EventDataSource(this);
            ArrayList<DEvent> eventList = eventDataSource.getSiteEvents(getSelectedAppID(), siteID);

            if (eventList.size() > 0)
                gotoStartNewScreen();

           /* if (eventList.size() > 1)
                gotoStartNewScreen();
            else if (eventList.size() == 1)
                startActivity(locationIntent);*/

            if (SiteActivity.siteActivity != null)
                SiteActivity.siteActivity.finish();

            finish();

/*            //have added check for is fromAddSite to know that i need to create or show alerts to create event everywhere
            if (isfromaddsite || eventsAvailable) {
                Intent locationIntent = new Intent(this, LocationActivity.class);
                locationIntent.putExtra("APP_ID", getSelectedAppID());
                locationIntent.putExtra("EVENT_ID", eventID);

                if (!ScreenReso.isLimitedUser)
                    locationIntent.putExtra("fromaddsite", isfromaddsite);

                locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (!ScreenReso.isLimitedUser)
                    startActivity(locationIntent);
                else {
                    EventDataSource eventDataSource = new EventDataSource(this);
                    ArrayList<DEvent> eventList = eventDataSource.getSiteEvents(getSelectedAppID(), siteID);
                    if (eventList.size() > 1)
                        gotoStartNewScreen();
                    else if (eventList.size() == 1)
                        startActivity(locationIntent);
                }

                if (!ScreenReso.isLimitedUser)
                    SiteActivity.siteActivity.finish();

                ApplicationActivity.applicationActivity.finish();
                finish();
            } else {
                gotoStartNewScreen();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error to start Location Activity:" + e.getLocalizedMessage());
            CustomToast.showToast(this, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG);
        }
    }

    private void gotoStartNewScreen() {
        Intent intent = new Intent(ApplicationActivity.this, StartNewEventActivity.class);
        intent.putExtra(GlobalStrings.CURRENT_APPID, getSelectedAppID());
        intent.putExtra(GlobalStrings.CURRENT_SITEID, siteID);
        intent.putExtra(GlobalStrings.FORM_NAME, displayNameSelected);
        intent.putExtra(GlobalStrings.CURRENT_SITENAME, siteName);
        intent.putExtra(GlobalStrings.USERID, userID);
        startActivity(intent);
    }

    public int getEventID(int mobileAppID, int siteID, int userID, Location gpsLocation, String deviceID, long eventDateTime) {
        double lat = 0;
        double longt = 0;
        EventDataSource eventData = new EventDataSource(context);

        if (gpsLocation != null) {
            lat = gpsLocation.getLatitude();
            longt = gpsLocation.getLongitude();
        }
        int eventID = 0;
        eventID = eventData.pickEventID(mobileAppID, siteID, userID, gpsLocation, deviceID);
        if (eventID == 0) {
            eventID = genServerEventID(mobileAppID, siteID, userID, lat, longt, deviceID, eventDateTime);
        }
/*        if (eventID == 0) {
            eventID = genClientEventID(mobileAppID, siteID, userID, lat, longt, deviceID, eventDateTime);
        }*/
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
                userID, lat, longt, deviceID, eventDateTime, 0, "");
        return eventID;
    }

    @SuppressWarnings("unchecked")
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
        EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(this, event, this.username, this.password, false);
        eventHandler.execute();
        return eventID;
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
            showAlertProgress(getString(R.string.please_wait));
//        startProgressDialog();
        }

        @Override
        protected EventResponseModel doInBackground(Void... voids) {

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

            return mAquaBlueService.generateEventIDFromServer(context.getResources().getString(R.string.prod_base_uri),
                    context.getResources().getString(R.string.prod_event_check), event,
                    username,
                    jsonObject);
        }

        @Override
        protected void onPostExecute(EventResponseModel eventResponse) {
            super.onPostExecute(eventResponse);
//            dismissProgressDialog();
            cancelAlertProgressWithMsg();
            if (eventResponse != null) {
                if (eventResponse.isSuccess()) {
                    Log.e("check events response", eventResponse.getMessage());
                    boolean hasActiveEvents = eventResponse.getMessage().equalsIgnoreCase("Active");

                    if (hasActiveEvents) {
                        if (!ScreenReso.isLimitedUser)
                            showCreateEventAlert(eventResponse.getData());
                        else
                            insertEventDataToDb(eventResponse.getData());
                    } else {
                        if (!ScreenReso.isLimitedUser)
                            goToCreateNewScreen();
                        else
                            showNoEventAlert();
                    }
                }
            }
        }
    }

    private void showNoEventAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.event_alert));
        builder.setMessage(getString(R.string.there_are_no_active_events_available));

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

/*    void startProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }*/

    private void goToCreateNewScreen() {
        Intent intent = new Intent(ApplicationActivity.this,
                CreateNewEventActivity.class);
        intent.putExtra(GlobalStrings.CURRENT_APPID, getSelectedAppID());
        intent.putExtra(GlobalStrings.CURRENT_SITEID, siteID);
        intent.putExtra(GlobalStrings.FORM_NAME, displayNameSelected);
        intent.putExtra(GlobalStrings.CURRENT_SITENAME, siteName);
        intent.putExtra(GlobalStrings.USERID, userID);
        intent.putExtra(GlobalStrings.EVENT_STAR_DATE, eventStartDate);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        if (isfromaddsite) {
//            Intent intent = new Intent(context, MainDrawerActivity.class);
            Intent intent = new Intent(context, HomeScreenActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        }
        finish();
    }

    @Override
    public void onTaskCompleted() {
        postGetEvent();
    }

    @Override
    public void setGeneratedEventID(int id) {

    }

    @Override
    public void setGeneratedEventID(Object obj) {
        EventResponseModel result = (EventResponseModel) obj;

        eventID = result.getData().getEventId();
        EventDataSource eventSource = new EventDataSource(context);
        long eventDateTime = Long.parseLong(result.getData().getEventCreationDate());

        if (eventID > 0) {
            /*eventSource.insertEventId(eventID, "S", getSelectedAppID(), siteID, getUserID(),
                    GlobalStrings.CURRENT_GPS_LOCATION.getLatitude()
                    , GlobalStrings.CURRENT_GPS_LOCATION.getLongitude(), DeviceInfo.getDeviceID(this), eventDateTime);*/
            eventSource.insertEventId(eventID, "S", getSelectedAppID(), siteID, getUserID(),
                    mLatitude
                    , mLongitude, DeviceInfo.getDeviceID(this), eventDateTime,
                    0, "");
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
                        //04-Mar-16
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                    if ((result.getResponseCode() == HttpStatus.NOT_FOUND) || (result.getResponseCode() == HttpStatus.LOCKED)) {
                        Util.setDeviceNOT_ACTIVATED((Activity) context, username, password);
//                    Toast.makeText(context,GlobalStrings.responseMessage,Toast.LENGTH_LONG).show();
                    }

                    if (result.getResponseCode() == HttpStatus.BAD_REQUEST) {
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                    }
                    if ((result.getResponseCode() == HttpStatus.EXPECTATION_FAILED) ||
                            (result.getResponseCode() == HttpStatus.UNAUTHORIZED) ||
                            (result.getResponseCode() == HttpStatus.CONFLICT)
                    ) {
                        //02-AuG-16
                        Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                        Util.setLogout((Activity) context);
                    }
                }
            }
        } else {
            postGetEvent();
        }
    }

    public void uploadFieldData() {
        Log.i(TAG, "Upload Field Data Called");
        Intent dataUpload = new Intent(this, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", 0);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
//        overridePendingTransition(R.anim.right_to_left,
//                R.anim.left_to_right);
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

    public class CustomComparator implements Comparator<SSiteMobileApp> {
        @Override
        public int compare(SSiteMobileApp lhs, SSiteMobileApp rhs) {
            return lhs.getDisplay_name_roll_into_app().compareTo(rhs.getDisplay_name_roll_into_app());
        }
    }

    void setAdapter(ArrayList<SSiteMobileApp> data) {
        if (data.size() > 0) {
            form_list_container.setVisibility(View.VISIBLE);
            emptylist_view.setVisibility(View.GONE);

            adapter = new MainFormAdapter(data, ApplicationActivity.this);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
            recyclerView.setAdapter(adapter);
        } else {
            form_list_container.setVisibility(View.GONE);
            emptylist_view.setVisibility(View.VISIBLE);
        }
    }
}
