package qnopy.com.qnopyandroid.ui.activity;

import static androidx.core.content.FileProvider.getUriForFile;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.MEDIA_TYPE_IMAGE;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.getMediaStorageDirectory;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.NotificationsDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.db.TempLogsDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.responsemodel.CSVDataModel;
import qnopy.com.qnopyandroid.responsemodel.EventResponseData;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.calendarUser.CalendarFragment;
import qnopy.com.qnopyandroid.ui.events.SubmittalsFragment;
import qnopy.com.qnopyandroid.ui.fragment.SiteFragment;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.ui.task.TasksTabFragment;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.BadgeDrawerArrowDrawable;
import qnopy.com.qnopyandroid.uiutils.BadgeDrawerToggle;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.uiutils.SendDBTask;
import qnopy.com.qnopyandroid.util.CSVUtil;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.FileUtils;
import qnopy.com.qnopyandroid.util.Util;

public class MainDrawerActivity extends ProgressDialogActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SiteFragment.OnSiteClickListener,
        SubmittalsFragment.OnListFragmentInteractionListener,
        OnTaskCompleted,
        CustomAlert.LocationServiceAlertListener {
    private static final String TAG = "MainDrawerActivity";
    public static Activity mainDrawerActivity;
    static String password, userID, companyID, username;
    Context context;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    private boolean isForceDownload;

    private static final String MY_EVENTS = "Events";
    private CheckBox splitScreenCheckbox;
    private CheckBox fasterFormsCheckbox;

    SearchView search_by;
    int SELECTED_TAB = 0;
    int ACTIVE = 1, CLOSED = 0, CONFLICT = 2;
    ActionBar actionbar;
    //  MaterialSearchView searchView;
    TabLayout tabLayout;
    TextView view;
    TextView counter_view;
    CheckBox checkView, backg_sync_checkview, enable_Compression;
    MyBroadCastReceiver myBroadCastReceiver;
    Activity mDrawerActivity;
    int userrole = 0;
    boolean PHOTO_COMPRESSION = false;
    DrawerLayout drawer;
    NavigationView navigationView;
    private ProgressDialog progressDialog;
    private String locationName = "", userGuid, lastSyncDate;
    private FrameLayout redCircle;
    private BadgeDrawerToggle toggle;
    private BadgeDrawerArrowDrawable badgeDrawable;
    private int REQUEST_CODE_COPY_DB = 148;
    private BottomNavigationView bottomNavView;
    private Menu navigationMenu;
    private BadELFGPSTracker badElf;
    private FusedLocationProviderClient fusedLocationClient;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private String siteIdForProjectUser = "";
    private String siteNameProjectUser = "";

    public String getSiteIdForProjectUser() {
        return siteIdForProjectUser;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.navigation_my_events) {
                setTitle(getString(R.string.my_events));
                tabLayout.setVisibility(View.VISIBLE);
                loadSubmittalsFragment();
                return true;
            } else if (item.getItemId() == R.id.navigation_task) {
                setTitle(getString(R.string.tasks));
                tabLayout.setVisibility(View.GONE);
                loadFragment(new TasksTabFragment());
                return true;
            } else if (item.getItemId() == R.id.navigation_my_projects) {
                setTitle(getString(R.string.my_projects));
                tabLayout.setVisibility(View.GONE);
                loadFragment(new SiteFragment());
                return true;
            } else if (item.getItemId() == R.id.navigation_calendar) {
                setTitle(getString(R.string.calendar));
                tabLayout.setVisibility(View.GONE);
                loadFragment(new CalendarFragment());
                return true;
            }
            return false;
        }
    };

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (getIntent() != null) {
            siteIdForProjectUser = getIntent().getStringExtra(GlobalStrings.KEY_SITE_ID);
            siteNameProjectUser = getIntent().getStringExtra(GlobalStrings.KEY_SITE_NAME);
        }

        String userAppType = Util.getSharedPreferencesProperty(this, GlobalStrings.USERAPPTYPE);

        if (userAppType != null) {
            ScreenReso.isLimitedUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_LIMITED);
            ScreenReso.isProjectUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_PROJECT);
            ScreenReso.isCalendarUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_CALENDAR);
        }

        if (!ScreenReso.isCalendarUser && !ScreenReso.isProjectUser && !ScreenReso.isLimitedUser) {
            ScreenReso.isCalendarUser = true;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        //   searchView = (MaterialSearchView) findViewById(R.id.search_view);
        mainDrawerActivity = this;
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.my_events));
        context = this;
        mDrawerActivity = this;
        GlobalStrings.currentContext = this;
        actionbar = getSupportActionBar();
        myBroadCastReceiver = new MyBroadCastReceiver();

        bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navItemSelectedListener);

        if (actionbar != null) {
            actionbar.setElevation(0);
        }

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.active));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.closed));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.conflict));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                getLocation();
        } else
            getLocation();

/*        if (ScreenReso.isLimitedUser) {
            tabLayout.setVisibility(View.GONE);
            bottomNavView.getMenu().clear();
            bottomNavView.inflateMenu(R.menu.menu_limited_user);
        }*/

        if (ScreenReso.isCalendarUser || ScreenReso.isProjectUser) {
            tabLayout.setVisibility(View.GONE);
            bottomNavView.getMenu().clear();
            bottomNavView.inflateMenu(R.menu.menu_calendar_user);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                SELECTED_TAB = tab.getPosition();
                loadSubmittalsFragment();
//                if (getFragmentRefreshListener() != null) {
//                    getFragmentRefreshListener().onRefresh(SELECTED_TAB);
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mAquaBlueService = new AquaBlueServiceImpl(context);
        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        companyID = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);

        drawer = findViewById(R.id.drawer_layout_main);

        //26-05-2018 SHOW BADGE ON DRAWERTOGGLE BUTTON
        toggle = new BadgeDrawerToggle(mDrawerActivity, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        badgeDrawable = new BadgeDrawerArrowDrawable(actionbar.getThemedContext());
        toggle.setDrawerArrowDrawable(badgeDrawable);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (siteIdForProjectUser != null && !siteIdForProjectUser.isEmpty())
            setDrawerState(false);

        navigationView = findViewById(R.id.nav_view_main);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.header_user_name);
        nav_user.setText(username);

        String capture = Util.getSharedPreferencesProperty(context, GlobalStrings.CAPTURE_SIGNATURE);
        String bg_service = Util.getSharedPreferencesProperty(context, GlobalStrings.BG_SERVICE);

        boolean CAPTURE = false, BG_SERVICE = false;
        if (capture == null) {
            CAPTURE = false;
        } else {
            CAPTURE = Boolean.parseBoolean(capture);
        }

        if (bg_service == null) {
            BG_SERVICE = true;
        } else {
            BG_SERVICE = Boolean.parseBoolean(bg_service);
        }

        Util.setSharedPreferencesProperty(context, GlobalStrings.CAPTURE_SIGNATURE, String.valueOf(CAPTURE));

        navigationMenu = navigationView.getMenu();

        UserDataSource userDataSource = new UserDataSource(this);
        int userRole = userDataSource.getUserRole(username);

        if (userRole == 1 || userRole == 2 || userRole == 5) {
            MenuItem itemDashBoard = navigationMenu.findItem(R.id.nav_reset_app);
            itemDashBoard.setVisible(true);
        }

        if (ScreenReso.isLimitedUser) {
            MenuItem itemDashBoard = navigationMenu.findItem(R.id.nav_my_submittal);
            itemDashBoard.setVisible(false);
            MenuItem itemDashBoardCal = navigationMenu.findItem(R.id.nav_calendar);
            itemDashBoardCal.setVisible(false);
        }

        navigationMenu.findItem(R.id.nav_app_version).setTitle(getString(R.string.version)
                + Util.getAppVersionName(context));

        MenuItem navItem = navigationMenu.findItem(R.id.nav_notification);
        FrameLayout rootView = (FrameLayout) navItem.getActionView();

        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        counter_view = rootView.findViewById(R.id.view_alert_count_textview);

        initializeCountDrawer();

        setSplitScreenCheckbox();
        setFasterFormsCheckbox();

        MenuItem checkItem = navigationMenu.findItem(R.id.enable_signature);
        checkView = (CheckBox) checkItem.getActionView();
        checkView.setChecked(CAPTURE);

        checkView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Util.setSharedPreferencesProperty(context, GlobalStrings.CAPTURE_SIGNATURE, String.valueOf(b));
            }
        });

        checkItem = navigationMenu.findItem(R.id.enable_background_sync_service);

        backg_sync_checkview = (CheckBox) checkItem.getActionView();
        backg_sync_checkview.setChecked(BG_SERVICE);
        backg_sync_checkview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Util.setSharedPreferencesProperty(context, GlobalStrings.BG_SERVICE, String.valueOf(b));

                if (b) {
                    Util.scheduleAlarm(getApplicationContext());
                } else {
                    Util.stopAlarm(getApplicationContext());
                }
            }
        });

        String u_role = Util.getSharedPreferencesProperty(context, GlobalStrings.USERROLE);
        if (u_role != null) {
            if (!u_role.isEmpty()) {
                userrole = Integer.parseInt(u_role);
            }
        } else {
            int userrole1 = userDataSource.getUserRolefromID(Integer.parseInt(userID));
            if (userrole1 != 0) {
                Util.setSharedPreferencesProperty(context, GlobalStrings.USERROLE, String.valueOf(userrole1));
                userrole = userrole1;
            }
        }

        //SSL ISSUE SO NEW TEMP APK
        if (userrole != 0) {
            navigationMenu.findItem(R.id.assign_project)
                    .setVisible(userrole == GlobalStrings.SUPER_ADMIN
                            || userrole == GlobalStrings.CLIENT_ADMIN ||
                            userrole == GlobalStrings.PROJECT_MANAGER);
        } else {
            navigationMenu.findItem(R.id.assign_project).setVisible(false);
        }

        navigationMenu.findItem(R.id.enable_background_sync_service)
                .setVisible(Build.VERSION.SDK_INT < Build.VERSION_CODES.O);

        if (!BuildConfig.DEBUG) {
            navigationMenu.findItem(R.id.nav_copy_db).setVisible(false);
        }

        navigationMenu.findItem(R.id.nav_new_flow).setVisible(true);

/*        if (ScreenReso.isLimitedUser) {
            setTitle(getString(R.string.my_projects));
            loadFragment(new SiteFragment());
        } else*/
        if (ScreenReso.isCalendarUser || ScreenReso.isProjectUser) {
            setTitle(getString(R.string.calendar));
            loadFragment(new CalendarFragment());
            //fragment is loaded in on resume kept the same condition here to avoid compiling else statement
        } else {
            loadSubmittalsFragment();
        }

        navigationView.setNavigationItemSelectedListener(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                if (GlobalStrings.mDeviceConnectedStatusFlag == 1) {
                    //Toast.makeText(context, "Drawer open", Toast.LENGTH_SHORT).show();
                    navigationView.getMenu().findItem(R.id.nav_badelfcnnectionstatus).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_badelfcnnectionstatus).setTitle("" + GlobalStrings.mDeviceConnectedName + " CONNECTED.");
                }
                if (GlobalStrings.mDeviceConnectedStatusFlag == 0) {
                    navigationView.getMenu().findItem(R.id.nav_badelfcnnectionstatus).setVisible(false);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                //Toast.makeText(context, "Drawer close", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        toggle.syncState();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // Request permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            // Main code
            Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken()
            );

            currentLocationTask.addOnCompleteListener((new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    String result = "";

                    if (task.isSuccessful()) {
                        // Task completed successfully
                        Location location = task.getResult();
                        GlobalStrings.CURRENT_GPS_LOCATION = location;
                        if (location != null)
                            result = "Location (success): " +
                                    location.getLatitude() +
                                    ", " +
                                    location.getLongitude();
                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        result = "Exception thrown: " + exception;
                    }
                    Log.d(TAG, "getCurrentLocation() result: " + result);
                }
            }));
        } else {
            Log.d(TAG, "Request fine location permission.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setTaskBadge() {
        TaskDetailsDataSource tdSource = new TaskDetailsDataSource(this);

        if (siteIdForProjectUser != null && !siteIdForProjectUser.isEmpty()
                && siteIdForProjectUser.equals("-1"))
            siteIdForProjectUser = null;

        int count = tdSource.getTaskCountForBadge(siteIdForProjectUser);

        BadgeDrawable badge = bottomNavView.getOrCreateBadge(R.id.navigation_task);

        if (count > 0)
            bottomNavView.getOrCreateBadge(R.id.navigation_task).setNumber(count);
        else if (badge.isVisible())
            bottomNavView.removeBadge(R.id.navigation_task);
/*
        if (menuItemTask != null) {
            Util.setBadgeCount(this, menuItemTask, tdSource.getTaskCountForBadge() + "",
                    true);
        }*/
    }

    public void startApplicationActivity(String dateSelected) {
        int companyID = Integer.parseInt(Util.getSharedPreferencesProperty(this,
                GlobalStrings.COMPANYID));

        SiteMobileAppDataSource appData = new SiteMobileAppDataSource(this);
        List<SSiteMobileApp> siteFormList = new ArrayList<>();

        siteFormList = appData.getAllAppsV16(Integer.parseInt(siteIdForProjectUser));

/*        if (siteFormList.size() > 1)
            invokeApplicationActivity(siteNameProjectUser, Integer.parseInt(siteIdForProjectUser));
        else if (siteFormList.size() == 1) {
            checkEventExist(siteFormList.get(0), Integer.parseInt(siteIdForProjectUser), siteNameProjectUser);
        }*/

        if (!siteFormList.isEmpty())
            invokeApplicationActivity(siteNameProjectUser, Integer.parseInt(siteIdForProjectUser),
                    dateSelected);
        else
            Toast.makeText(this, "You don't have any forms to proceed!",
                    Toast.LENGTH_SHORT).show();

        Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_SITEID, siteIdForProjectUser);
    }

    private void invokeApplicationActivity(String siteName, int siteID, String dateSelected) {

        Intent applicationIntent = new Intent(context, ApplicationActivity.class);

        //for calendar to carry selected date
        if (!dateSelected.isEmpty()) {
            long milliSec = Util.getTimeInMillisAddingCurrentTime(dateSelected);
            applicationIntent.putExtra(GlobalStrings.EVENT_STAR_DATE, milliSec);
        }

        applicationIntent.putExtra("SITE_NAME", siteName);
        applicationIntent.putExtra("SITE_ID", siteID);
        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID, ""
                + siteID);
        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME, siteName);
        Log.i(TAG, "Selected site id:" + siteID + "& siteName:" + siteName);
        context.startActivity(applicationIntent);
    }

    private void checkEventExist(SSiteMobileApp siteMobApp, int siteId, String siteName) {
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
                    siteId);

            if (eventList.size() > 1)
                gotoStartNewScreen(siteMobApp, siteName, siteId);
            else
                startLocationActivity(eventID, siteMobApp.getMobileAppId());
        }
    }

    private void startLocationActivity(int eventID, Integer mobileAppId) {
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

    private void checkActiveEvents(Integer mobileAppId, Integer siteId) {

        DEvent event = new DEvent();
        event.setSiteId(siteId);
        event.setMobileAppId(mobileAppId);
        event.setUserId(Integer.parseInt(userID));
        event.setDeviceId(DeviceInfo.getDeviceID(this));
        event.setLatitude(0.0);
        event.setLongitude(0.0);
        event.setUserName(username);
        event.setEventDate(System.currentTimeMillis());
        event.setEventStartDate(System.currentTimeMillis());

        AsyncEventCheck eventHandler = new AsyncEventCheck(event);
        eventHandler.execute();
    }

    private void gotoStartNewScreen(SSiteMobileApp siteMobApp, String siteName, int siteId) {
        Intent intent = new Intent(this, StartNewEventActivity.class);
        intent.putExtra(GlobalStrings.CURRENT_APPID, siteMobApp.getMobileAppId());
        intent.putExtra(GlobalStrings.CURRENT_SITEID, siteId);
        intent.putExtra(GlobalStrings.FORM_NAME, siteMobApp.getDisplay_name());
        intent.putExtra(GlobalStrings.CURRENT_SITENAME, siteName);
        intent.putExtra(GlobalStrings.USERID, userID);
        startActivity(intent);
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

            return mAquaBlueService.generateEventIDFromServer(context.getResources().getString(R.string.prod_base_uri),
                    context.getResources().getString(R.string.prod_event_check), event,
                    username,
                    jsonObject);
        }

        @Override
        protected void onPostExecute(EventResponseModel eventResponse) {
            super.onPostExecute(eventResponse);
            cancelAlertProgress();
            if (eventResponse != null) {
                if (eventResponse.isSuccess()) {
                    Log.e("check events response", eventResponse.getMessage());
                    boolean hasActiveEvents = eventResponse.getMessage().equalsIgnoreCase("Active");

                    if (hasActiveEvents) {
                        insertEventDataToDb(eventResponse.getData());
                    } else {
                        showNoEventAlert();
                    }
                }
            }
        }
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
            locationIntent = new Intent(context, SplitLocationAndMapActivity.class);

        locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        locationIntent.putExtra("APP_ID", event.getMobileAppId());
        locationIntent.putExtra("EVENT_ID", event.getEventId());
        locationIntent.putExtra("fromaddsite", false);
        startActivity(locationIntent);
    }

    private void showNoEventAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.event_alert));
        builder.setMessage(getString(R.string.no_active_events_contact_qnopy_admin));

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setSplitScreenCheckbox() {
        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(context, GlobalStrings.ENABLE_SPLIT_SCREEN);

        MenuItem checkItem = navigationMenu.findItem(R.id.menu_enable_split_screen);

        if (!Util.isTablet(this))
            checkItem.setVisible(false);

        splitScreenCheckbox = (CheckBox) checkItem.getActionView();
        splitScreenCheckbox.setChecked(isSplitScreenEnabled);

        splitScreenCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Util.setSharedPreferencesProperty(context, GlobalStrings.ENABLE_SPLIT_SCREEN, isChecked);
            }
        });
    }

    private void setFasterFormsCheckbox() {

        boolean isFasterFormsEnabled = Util.isShowNewForms(this);

        MenuItem checkItem = navigationMenu.findItem(R.id.menu_faster_forms);
//        String title = getString(R.string.faster_forms);
        String title = "New Faster Forms<font color='#F44336'><i> (beta)</i></font>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            checkItem.setTitle(Html.fromHtml(title,
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            checkItem.setTitle(Html.fromHtml(title));
        }

        fasterFormsCheckbox = (CheckBox) checkItem.getActionView();
        fasterFormsCheckbox.setChecked(isFasterFormsEnabled);

        fasterFormsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Util.setSharedPreferencesProperty(context, GlobalStrings.IS_SHOW_FASTER_FORMS,
                        isChecked);
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.load_fragment, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerMyReceiver();
        initializeCountDrawer();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                getLocation();
            }
        }

        SharedPreferences.Editor editor = getSharedPreferences("BADELFGPS", MODE_PRIVATE).edit();
        editor.clear().apply();

        badElf = new BadELFGPSTracker(context);

        boolean isCalendarRefresh = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.IS_CALENDAR_REFRESH);

        boolean isEventsRefresh = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.IS_EVENTS_REFRESH);

        boolean isSiteRefresh = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.IS_SITE_REFRESH);

        if (isCalendarRefresh) {
            Util.setSharedPreferencesProperty(this, GlobalStrings.IS_CALENDAR_REFRESH,
                    false);
            navigationCalendarItemClicked();
        }

        if (isEventsRefresh) {
            Util.setSharedPreferencesProperty(this, GlobalStrings.IS_EVENTS_REFRESH,
                    false);
            navigationSubmittalItemClicked();
        }

        if (isSiteRefresh) {
            Util.setSharedPreferencesProperty(this, GlobalStrings.IS_SITE_REFRESH,
                    false);
            navigationSiteItemClicked();
        }

        setTaskBadge();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBroadCastReceiver);
        // make sure to unregister your receiver after finishing of this activity
        badElf.disconnectTracker();
    }

    @Override
    protected void onStop() {
        badElf.disconnectTracker();
        super.onStop();
    }

    /**
     * This method is responsible to register an action to BroadCastReceiver
     */
    private void registerMyReceiver() {

        try {
            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(GlobalStrings.BROADCAST_ACTION);
            registerReceiver(myBroadCastReceiver, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeCountDrawer() {
        if (counter_view != null) {
            int[] count = new NotificationsDataSource(context).getNotificationCount(Integer.parseInt(userID));

            if (count[1] > 0) {
                redCircle.setVisibility(View.VISIBLE);
                counter_view.setText(count[1] > 99 ? "99+" : count[1] + "");
                toggle.setBadgeText(count[1] > 99 ? "99+" : count[1] + "");
                // toggle.setBadgeEnabled(true);
                badgeDrawable.setEnabled(true);

                RunAnimation();
            } else {
                redCircle.setVisibility(View.GONE);
                //  toggle.setBadgeEnabled(false);
                badgeDrawable.setEnabled(false);
            }
        }
    }

    private void RunAnimation() {
        Animation alert_anim = AnimationUtils.loadAnimation(this, R.anim.notification_alert);
        alert_anim.reset();
        redCircle.clearAnimation();
        redCircle.startAnimation(alert_anim);
    }

    private void loadSubmittalsFragment() {
        Class fragmentClass = null;
        Fragment fragment = null;

        fragmentClass = SubmittalsFragment.class;
        fragmentClass = SubmittalsFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragment != null) {
            Bundle args = new Bundle();
            args.putInt(GlobalStrings.SELECTED_TAB, SELECTED_TAB);
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.load_fragment, fragment);
            //  transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            this.finish();
            if (!ScreenReso.isLimitedUser || !ScreenReso.isCalendarUser)
                if (SplashScreenActivity.mSplashScreenActivity != null)
                    SplashScreenActivity.mSplashScreenActivity.finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_my_submittal:
                navigationSubmittalItemClicked();
                break;
            case R.id.nav_calendar:
                navigationCalendarItemClicked();
                break;
            case R.id.nav_site:
                navigationSiteItemClicked();
                break;
            case R.id.nav_notification:
                Intent notificationIntent = new Intent(context, NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.nav_hospital:
                if (CheckNetwork.isInternetAvailable(context)) {
                    Intent mapIntent = new Intent(context, MapForSiteActivity.class);
                    mapIntent.putExtra("PREV_CONTEXT", "LocationDetail");
                    mapIntent.putExtra("OPERATION", "nearby");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_reset_app:
                FieldDataSource fd = new FieldDataSource(context);
                AttachmentDataSource attachDataSource = new AttachmentDataSource(context);

                if (fd.isFieldDataAvailableToSync() || attachDataSource.attachmentsAvailableToSync()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.warning))
                            .setMessage(R.string.there_is_unsync_data)
                            .setPositiveButton(R.string.erase, (dialog, which) -> alertForDeletingData())
                            .setNegativeButton(R.string.cancel_upper_case, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    alertForDeletingData();
                }
                break;

            case R.id.nav_update_app:
                //21-Mar-16  Link to Update app from Play Store
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.aqua.fieldbuddy"));
                startActivity(intent);
                break;

            case R.id.assign_project:
                Intent intent2 = new Intent(this, SiteActivity.class);
                intent2.putExtra("FromAssignProject", true);
                startActivity(intent2);
                break;

            case R.id.enable_signature:

                boolean CAPTURE = !checkView.isChecked();
                Util.setSharedPreferencesProperty(context, GlobalStrings.CAPTURE_SIGNATURE, String.valueOf(CAPTURE));
                checkView.setChecked(CAPTURE);
                break;

            case R.id.menu_enable_split_screen:

                boolean isSplitScreenEnabled = !splitScreenCheckbox.isChecked();
                Util.setSharedPreferencesProperty(context, GlobalStrings.ENABLE_SPLIT_SCREEN, isSplitScreenEnabled);
                splitScreenCheckbox.setChecked(isSplitScreenEnabled);
                break;

            case R.id.menu_faster_forms:

                boolean isFasterForms = !fasterFormsCheckbox.isChecked();
                Util.setSharedPreferencesProperty(context, GlobalStrings.IS_SHOW_FASTER_FORMS, isFasterForms);
                fasterFormsCheckbox.setChecked(isFasterForms);
                break;

            case R.id.enable_background_sync_service:

                boolean BG_SERVICE = !backg_sync_checkview.isChecked();
                Util.setSharedPreferencesProperty(context, GlobalStrings.BG_SERVICE, String.valueOf(BG_SERVICE));
                backg_sync_checkview.setChecked(BG_SERVICE);
                break;

            case R.id.nav_copy_db:
                Intent intentChooser = new Intent(Intent.ACTION_GET_CONTENT);
                intentChooser.setType("*/*");
                intentChooser.addCategory(Intent.CATEGORY_DEFAULT);

                try {
                    startActivityForResult(
                            Intent.createChooser(intentChooser, getString(R.string.select_db_file_to_copy)),
                            REQUEST_CODE_COPY_DB);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(this, getString(R.string.install_file_mgr),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_download_forms:
                isForceDownload = true;
                downloadForms();
                break;
            case R.id.nav_new_flow:
                //pravin desai
                startActivity(new Intent(this, HomeScreenActivity.class));
                //finish();
                break;

            case R.id.nav_contact_support:
                showContactSupportAlert();
                break;

            case R.id.nav_signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.sign_out))
                        .setMessage(getString(R.string.are_you_sure_to_sign_out))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAllData();
//                                Util.setLogout(MainDrawerActivity.this);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null);
                AlertDialog dia = builder.create();
                dia.show();
                break;

            case R.id.nav_send_db:
                if (CheckNetwork.isInternetAvailable(context)) {
                    new SendDBTask(this).execute();
                } else {
                    CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), 10);
                }
                break;

            case R.id.nav_help:
                startActivity(new Intent(context, HelpActivity.class));
                break;

            case R.id.export_all_data_csv:
                FieldDataSource fieldDataSource = new FieldDataSource(this);
                ArrayList<CSVDataModel> list = fieldDataSource.getDataForCSV("");

                if (list.size() > 0) {
                    String csvData = CSVUtil.toCSV(list, ',', true);
                    exportCSVFile(csvData);
                } else {
                    CustomToast.showToast(this, getString(R.string.no_data_to_export), Toast.LENGTH_SHORT);
                }

                break;

            case R.id.export_today_csv_data:
                FieldDataSource fieldDataSource1 = new FieldDataSource(this);
                ArrayList<CSVDataModel> listTodaysData = fieldDataSource1.
                        getDataForCSV(Util.getFormattedDate(System.currentTimeMillis()));

                if (listTodaysData.size() > 0) {
                    String csvData = CSVUtil.toCSV(listTodaysData, ',', true);
                    exportCSVFile(csvData);
                } else {
                    CustomToast.showToast(this, getString(R.string.no_data_to_export), Toast.LENGTH_SHORT);
                }
                break;

            default:
                break;
        }

//        try {
//            if (fragmentClass != null) {
//                fragment = (Fragment) fragmentClass.newInstance();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        // Insert the fragment by replacing any existing fragment
////        FragmentManager fragmentManager = getSupportFragmentManager();
////        fragmentManager.beginTransaction().replace(R.id.load_fragment, fragment).commit();
//
//        // Create fragment and give it an argument for the selected article
////        SiteFragment newFragment = new SiteFragment();
//
//        if (fragment != null) {
//            Bundle args = new Bundle();
//            args.putInt(SiteFragment.ARG_USERID, Integer.parseInt(userID));
//            args.putInt(SiteFragment.ARG_USER_COMPANY, Integer.parseInt(companyID));
//            args.putString(SiteFragment.ARG_USER, username);
//            args.putString(SiteFragment.ARG_PASS, password);
//            fragment.setArguments(args);
//
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//            // Replace whatever is in the fragment_container view with this fragment,
//            // and add the transaction to the back stack so the user can navigate back
//            transaction.replace(R.id.load_fragment, fragment);
//            transaction.addToBackStack(null);
//
//            // Commit the transaction
//            transaction.commit();
//        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void navigationSiteItemClicked() {
        if (!ScreenReso.isLimitedUser) {
            Intent applicationIntent = new Intent(context, SiteActivity.class);
            startActivity(applicationIntent);
        } /*else {
            setTitle(getString(R.string.my_projects));
            loadFragment(new SiteFragment());
            bottomNavView.getMenu().findItem(R.id.navigation_my_projects).setChecked(true);
        }*/
    }

    private void navigationCalendarItemClicked() {
        setTitle(getString(R.string.calendar));
        showAlertProgress();

        tabLayout.setVisibility(View.GONE);

/*
        if (ScreenReso.isCalendarUser) {
            bottomNavView.getMenu().clear();
            bottomNavView.inflateMenu(R.menu.menu_calendar_user);
        }
*/

//        loadFragment(new CalendarFragment());
        bottomNavView.getMenu().findItem(R.id.navigation_calendar).setChecked(true);
        new Handler().postDelayed(this::cancelAlertProgress, 200);
    }

    private void navigationSubmittalItemClicked() {
        setTitle(getString(R.string.my_events));
        showAlertProgress();

        tabLayout.setVisibility(View.VISIBLE);

/*        if (ScreenReso.isCalendarUser) {
            bottomNavView.getMenu().clear();
            bottomNavView.inflateMenu(R.menu.menu_calendar_user);
        } else {
            bottomNavView.getMenu().clear();
            bottomNavView.inflateMenu(R.menu.bottom_nav_menu);
        }*/

//        loadSubmittalsFragment();
        bottomNavView.getMenu().findItem(R.id.navigation_my_events).setChecked(true);
        new Handler().postDelayed(this::cancelAlertProgress, 200);
    }

    private void showContactSupportAlert() {
        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle(R.string.contact_support);
        builder.setMessage("\n" + getString(R.string.email) + ":" + GlobalStrings.SUPPORT_EMAIL
                + "\n" + getString(R.string.phone) + ":" + GlobalStrings.SUPPORT_PHONE);
        boolean isTabletSize = getResources().getBoolean(R.bool.isTablet);

        if (!isTabletSize) {
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

            builder.setPositiveButton(getString(R.string.call), (dialog, which) -> {
                dialog.cancel();

                String mobileNumber = GlobalStrings.SUPPORT_PHONE;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                intent.setData(Uri.parse("tel: " + mobileNumber)); // Data with intent respective action on intent
                startActivity(intent);
            });
        } else {
            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.cancel());
        }

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void downloadForms() {

        if (!CheckNetwork.isInternetAvailable(context)) {
            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            return;
        }

        //1. Check any offline events, if there is any create events and update event ids then
        //2. Upload event data, coc data, location data in case there is any then
        //3. Check any task data to upload task data.
        EventDataSource eventDbSource = new EventDataSource(this);
        ArrayList<DEvent> eventList = eventDbSource
                .getClientGeneratedEventIDs(this);

        if (eventList.size() > 0) {
            new EventIDGeneratorTask(this, null,
                    username, password, true, this).execute();
        }/* else if (checkAnyTaskToSync()) {
         *//*            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.lose_unsaved_changes_sync_tasks);
            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();*//*
            syncTasks();
        }*/ else {
            //checking if any field data to upload then call download forms and later events will
            //be fetched as we'll be clearing tables to let submittals fragment know that it
            //should download events
            //then sync tasks
            uploadFieldData();
        }
    }

    private void callMetaSync() {
        Intent metaIntent = new Intent(getApplicationContext(), MetaSyncActivity.class);
        metaIntent.putExtra(GlobalStrings.FROM_DASHBOARD, isForceDownload);
        isForceDownload = false;//reset
        startActivity(metaIntent);
//        finish();
    }

    private boolean checkAnyTaskToSync() {
        final TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(this);
        final TaskCommentsDataSource commentsDataSource = new TaskCommentsDataSource(this);
        TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(this);
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments("");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks("");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments("");

        return commentList.size() != 0 || dataList.size() != 0 || attachmentList.size() != 0;
    }

    public void uploadFieldData() {

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(this);

        LocationDataSource LDSource = new LocationDataSource(this);
        FieldDataSource fieldSource = new FieldDataSource(this);
        AttachmentDataSource attachDataSource = new AttachmentDataSource(this);

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        LDSource.checkAndUpdateClientLocationInFieldData();
        LDSource.checkAndUpdateClientLocationInAttachmentData();

        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
        logDetails.setScreenName("Event Dashboard Screen");
        logDetails.setDetails("Has field data before checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());

        tempLogsDataSource.insertTempLogs(logDetails);

        boolean isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();
        boolean isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        boolean isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(this);

        boolean isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        logDetails.setDetails("Has field data upon checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());
        tempLogsDataSource.insertTempLogs(logDetails);

        logDetails.setDetails("CHECKING DATA TO SYNC - " + " Has locations:" + isLocationsAvailableToSync
                + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync);
        tempLogsDataSource.insertTempLogs(logDetails);

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            syncTasks();
        } else {

            Intent dataUpload = new Intent(this, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", 0); //this id is used to close
            //the event which we don't require here
            startActivityForResult(dataUpload, BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE);
        }
    }

    private void syncTasks() {

        if (!CheckNetwork.isInternetAvailable(this)) {
            CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_SHORT);
            return;
        }

        final TaskDataResponse.Data taskDataRequest = new TaskDataResponse.Data();

        final TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(this);
        final TaskCommentsDataSource commentsDataSource = new TaskCommentsDataSource(this);
        TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(this);
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments("");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks("");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments("");

        if (commentList.size() == 0 && dataList.size() == 0 && attachmentList.size() == 0) {
            callMetaSync();
            return;
        }

        taskDataRequest.setTaskDataList(dataList);
        taskDataRequest.setCommentList(commentList);

        String baseUrl = this.getString(R.string.prod_base_uri)
                + this.getString(R.string.prod_user_task_sync_data);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(taskDataRequest));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showAlertProgress();
        updateAlertProgressMsg(getString(R.string.syncing_tasks_please_wait));
//        showProgressDialog(getString(R.string.syncing_tasks_please_wait));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, baseUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                TaskDataResponse syncRes = new Gson().fromJson(response.toString(), TaskDataResponse.class);

                if (syncRes.getData().getCommentList().size() > 0) {
                    for (TaskDataResponse.CommentList comment : syncRes.getData().getCommentList()) {
                        commentsDataSource.updateIdAndSyncFlag(comment.getTaskCommentId() + "",
                                comment.getTaskId() + "",
                                comment.getClientCommentId() + "");
                    }
                }

                if (syncRes.getData().getTaskDataList().size() > 0) {
                    for (TaskDataResponse.TaskDataList details : syncRes.getData().getTaskDataList()) {
                        taskDetailsDataSource.updateSyncFlagAndId(details.getTaskId() + "",
                                details.getClientTaskId() + "");
                        attachmentsDataSource.updateTaskId(details.getTaskId() + "",
                                details.getClientTaskId() + "");
                    }
                }

                ArrayList<TaskDataResponse.AttachmentList> attachmentList
                        = attachmentsDataSource.getAllUnSyncAttachments("");

                if (attachmentList.size() > 0) {
                    syncAttachments(attachmentList);
                } else {
//                    dismissProgressDialog();
                    cancelAlertProgress();
                    callMetaSync();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                dismissProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                DeviceInfoModel ob = DeviceInfo.getDeviceInfo(MainDrawerActivity.this);
                String deviceToken = Util.getSharedPreferencesProperty(MainDrawerActivity.this,
                        GlobalStrings.NOTIFICATION_REGISTRATION_ID);
                String uID = Util.getSharedPreferencesProperty(MainDrawerActivity.this,
                        GlobalStrings.USERID);

                Map<String, String> paramsHeader = new HashMap<String, String>();
                paramsHeader.put("user_guid", ob.getUser_guid());
                paramsHeader.put("device_id", ob.getDeviceId());
                paramsHeader.put("user_id", uID);
                paramsHeader.put("device_token", deviceToken);
                paramsHeader.put("Content-Type", "application/json");
                return paramsHeader;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonObjectRequest);
    }

    private void syncAttachments(ArrayList<TaskDataResponse.AttachmentList> list) {

        for (TaskDataResponse.AttachmentList attachment : list) {
            String path = getMediaStorageDirectory(MEDIA_TYPE_IMAGE).getAbsolutePath();
            File imagePath = new File(path, attachment.getFileName());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new Gson().toJson(attachment));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SyncMedia(this, jsonObject, imagePath.getAbsolutePath(), list.size()).execute();
        }
    }

    @Override
    public void onLocationDeny() {
        //no use
    }

    int countMediaSync = 0;

    private class SyncMedia extends AsyncTask<MediaType, Void, String> {

        //        File mFile;
        MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
        AttachmentTaskResponseModel resultModel = null;
        Context mContext;
        JSONObject mJsonObjectMediaData;
        String mPath;
        int mMediaCount;

        SyncMedia(Context context, JSONObject jsonObjectMediaData, String path, int mediaCount) {
            mContext = context;
            mJsonObjectMediaData = jsonObjectMediaData;
            mPath = path;
            mMediaCount = mediaCount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(MediaType... mediaTypes) {
            String response = null;

            File file = new File(mPath);
            try {
                if (file.exists()) {
                    files.add("files", new FileSystemResource(file));
                }
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
            files.add("media", mJsonObjectMediaData.toString());

            resultModel = mAquaBlueService.TaskMediaUpload(getResources().getString(R.string.prod_base_uri),
                    getResources().getString(R.string.prod_user_task_attachment_sync),
                    files);
            if (resultModel != null) {
                if (resultModel.isSuccess()) {
                    response = "SUCCESS";
                } else {
                    response = "FALSE";
                }
            } else {
                Log.e("imageUpload", "doInBackground: fails to upload image attachment");
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                countMediaSync++;
                if (s.equals("FALSE")) {
                    Log.e("imageUpload", "onPostExecute: fails to upload image attachment");
                } else if (s.equals("SUCCESS")) {
                    Log.e("imageUpload", "onPostExecute: image attachment upload success" + resultModel.getData().getTaskId());

                    TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(mContext);
                    attachmentsDataSource.updateDataSyncFlag(resultModel.getData().getTaskId(),
                            resultModel.getData().getFileName(),
                            resultModel.getData().getClientTaskAttachmentId() == null
                                    ? resultModel.getData().getTaskAttachmentId()
                                    : resultModel.getData().getClientTaskAttachmentId(),
                            resultModel.getData().getTaskAttachmentId());
                }
            }

            if (mMediaCount == countMediaSync) {
                cancelAlertProgress();
//                dismissProgressDialog();
//                Toast.makeText(mContext, getString(R.string.tasks_synced_successfully), Toast.LENGTH_SHORT).show();
                callMetaSync();
            }
        }
    }

    /*method is used when offline events are uploaded to server*/
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
            }
        }
    }

    @Override
    public void onTaskCompleted() {
        //no use
    }

    @Override
    public void setGeneratedEventID(int id) {
        //no use
    }

    @Override
    public void setGeneratedEventID(Object obj) {
        //no use
    }

    private void exportCSVFile(String csvData) {
        try {

            String fileName = "qnopy_sheet_" + System.currentTimeMillis() + ".csv";
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(csvData.getBytes());
            outputStream.close();

            File fileLocation = new File(getFilesDir(), fileName);
            Uri contentUri = getUriForFile(getApplicationContext(), "com.aqua.fieldbuddy.provider", fileLocation);

            Intent csvIntent = new Intent(Intent.ACTION_VIEW);
            csvIntent.setDataAndType(contentUri, "text/csv");
            csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            csvIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager manager = context.getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(csvIntent, 0);
            if (infos.size() > 0) {
                startActivity(Intent.createChooser(csvIntent, getString(R.string.choose_app_to_open)));
            } else {
                CustomToast.showToast(MainDrawerActivity.this,
                        getString(R.string.you_may_not_have_proper_app), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSiteClicked(Site item) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COPY_DB) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                assert uri != null;
                try {
                    copyDataBase(uri, MainDrawerActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                syncTasks();
            }
        }
    }

    public static void copyDataBase(Uri uri, Context context) throws IOException {

        File fileDb = new File(FileUtils.getRealPath(context, uri));
        if (!fileDb.getName().equals("aqua")) {
            Toast.makeText(context, context.getString(R.string.choose_valid_db_file), Toast.LENGTH_SHORT).show();
            return;
        }

        // Open your local db as the input stream
        FileInputStream fileInputStream =
                new FileInputStream(fileDb);

        Log.d("Path", "" + FileUtils.getRealPath(context, uri));
        // Path to the just created empty db
        String outFileName = Util.getBaseContextPath(context) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        fileInputStream.close();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void alertForDeletingData() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainDrawerActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_erase_data,
                null, false);
        alertDialogBuilder.setView(view);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        TextView btnErase = view.findViewById(R.id.tvErase);
        EditText edtErase = view.findViewById(R.id.edtErase);

        edtErase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equalsIgnoreCase("Erase")) {
                    btnErase.setVisibility(View.VISIBLE);
                } else {
                    btnErase.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnErase.setOnClickListener(v -> {
            deleteAllData();
            alertDialog.cancel();
        });
    }

    public void deleteAllData() {
        Util.setLogout(MainDrawerActivity.this);
    }

    @Override
    public void onListFragmentInteraction(EventData item) {

    }

    /**
     * MyBroadCastReceiver is responsible to receive broadCast from register action
     */
    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Log.d(TAG, "onReceive() called");
                Toast.makeText(context, "Notification Received.", Toast.LENGTH_LONG).show();
                initializeCountDrawer();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method called when this Activity finished
     * Override this method to unregister MyBroadCastReceiver
     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        // make sure to unregister your receiver after finishing of this activity
//        unregisterReceiver(myBroadCastReceiver);
//    }

}
