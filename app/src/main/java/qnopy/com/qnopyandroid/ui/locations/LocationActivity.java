package qnopy.com.qnopyandroid.ui.locations;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;
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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.adapter.COCLabelsAdapter;
import qnopy.com.qnopyandroid.adapter.CustomListAdapterDialog;
import qnopy.com.qnopyandroid.adapter.LocationAdapter;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.ModelClassLocationsWithAttribute;
import qnopy.com.qnopyandroid.clientmodel.RequiredFieldRowItem;
import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.customView.CustomBoldTextView;
import qnopy.com.qnopyandroid.customView.CustomEditText;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.databinding.BottomsheetReportNamesBinding;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.AttachmentData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocDetailDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.CompletionPercentageDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.EventLocationDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.db.LocationAttributeDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.db.TempLogsDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchAllReportByIdResponse;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchReportsById;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.ReportsAdapter;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.map.MapActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.photogallery.GalleryActivity;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.SCocDetails;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.LocPercentageRespModel;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.responsemodel.newLocPercentageResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.services.DataSyncService;
import qnopy.com.qnopyandroid.signature.CaptureSignature;
import qnopy.com.qnopyandroid.ui.activity.AddLocationActivity;
import qnopy.com.qnopyandroid.ui.activity.AppPreferencesActivity;
import qnopy.com.qnopyandroid.ui.activity.ApplicationActivity;
import qnopy.com.qnopyandroid.ui.activity.DataSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.DownloadYourOwnDataActivity;
import qnopy.com.qnopyandroid.ui.activity.FileFolderMainActivity;
import qnopy.com.qnopyandroid.ui.activity.FileFolderSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.FormActivity;
import qnopy.com.qnopyandroid.ui.activity.GeoSearchMapsActivity;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.ui.activity.MapForSiteActivity;
import qnopy.com.qnopyandroid.ui.activity.MetaSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.MobileReportActivity;
import qnopy.com.qnopyandroid.ui.activity.MobileReportRequiredActivity;
import qnopy.com.qnopyandroid.ui.activity.ReportView;
import qnopy.com.qnopyandroid.ui.activity.RequiredFieldsListActivity;
import qnopy.com.qnopyandroid.ui.activity.SiteActivity;
import qnopy.com.qnopyandroid.ui.activity.TaskDetailActivity;
import qnopy.com.qnopyandroid.ui.calendarUser.CalendarFragment;
import qnopy.com.qnopyandroid.ui.calendarUser.DownloadEventListTask;
import qnopy.com.qnopyandroid.ui.locations.adapter.LocationsAdapter;
import qnopy.com.qnopyandroid.ui.printCOC.PrintCOCActivity;
import qnopy.com.qnopyandroid.ui.printCOC.PrintCOCLabelsActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.uiutils.QRScannerActivity;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;

@AndroidEntryPoint
public class LocationActivity extends ProgressDialogActivity implements
        OnTaskCompleted, OnMenuItemClickListener,
        LocationAdapter.OnEraseLocationListener, DownloadEventListTask.OnEventDownloadListener,
        LocationsAdapter.OnLocationActionListener, CustomAlert.LocationServiceAlertListener {
    private static final String TAG = "LocationActivity";
    public static final int REQUEST_CODE_ADD_LOCATION = 1742;
    static int ASCENDING = 0, DESCENDING = 1;
    static int SORT_ORDER = ASCENDING;
    //    List<Location> values;
    LocationAdapter locationAdapter;
    LocationsAdapter newLocationsAdapter;
    boolean drawer = false;
    AquaBlueServiceImpl mAquaBlueService;
    String isDownloaded = "false";
    String mResponseString;
    String mErrorString = null;
//    int lastSelectedPos = 0;

    public Location selectedLocatonObj = new Location();
    public SCocMaster CocObj = new SCocMaster();

    int TOTAL_SIZE = 20971520; //5242880;
    //    int INCREASED_TOTAL_SIZE = 52428800;
    AttachmentData attachData = new AttachmentData();
    FloatingActionButton syncDatabtn, submitEndbtn, newlocationbtn, mobileReportbtn, mobileReportRequired;//loadkmllocationbtn
//    ImageButton mapView, disable_listview;

    public static final int SYNC_ACTIVITY_REQUEST_CODE = 103;
    public static final int MAP_ACTIVITY_REQUEST_CODE = 102;
    public static final int CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE = 104;
    public static final int DOWNLOAD_KMZ_REQUEST_CODE = 105;

    public static int eventID = 0;
    private int siteID = 0, appID = 0, userID = 0, setID = 0, companyID = 0;
    private String locID = "0"; // attachments for the site will have location as
    // zero.

    private String siteName, username = null, password = null, SELECTED_ATTRIBUTE_VALUE = null, SELECTED_ATTRIBUTE_NAME = null;
    //  MetaSyncDataModel mRetMetaSyncData = null;
    Dialog dialog1;
    int parentAppIDWS, siteidWS = 0;
    String deviceIDWS, userGUIDWS = null;
    private List<String> signFilePaths = new ArrayList<String>();

    private boolean delete = false;
    //syncAll=false

    User user;
    Context objContext;
    public static Activity LocActivity;

    List<MobileApp> childAppList = null;
    List<SSiteMobileApp> dispnamelist = null;
    MobileAppDataSource mobileAppSource;
    SiteMobileAppDataSource siteMobileAppDataSource;
    //    Event event = new Event();
    public List<Integer> totalChildAppIdList = new ArrayList<Integer>();

    public static int serverGenEventID = 0;
    boolean closeEvent = false;
    int version = 0;
    FloatingActionsMenu menuMultipleActions;
    String deviceID;
    boolean isLocationsAvailableToSync = false, isCoCAvailableToSync = false,
            isAttachmentsAvailableToSync = false, isFieldDataAvailableToSync = false;

    @Inject
    FieldDataSource fieldSource;

    @Inject
    LocationDataSource LDSource;

    @Inject
    AttachmentDataSource attachDataSource;

    private String mChosenFile;
    private String projectFolderPath = "";
    private static final String FTYPE = ".kmz";
    private String[] mFileList;
    String dispappName;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    public static final int LOCATION1_PERMISSION_REQUEST_CODE = 1;
    public static final int PHONE_STATE_PERMISSION_REQUEST_CODE = 2;
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 3;
    public static final int NETWORK_PERMISSION_REQUEST_CODE = 4;
    public static final int WIFI_PERMISSION_REQUEST_CODE = 5;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 6;
    List<Location> tempLocations = null;
    List<Location> cocLocations = null;
    ListView listView;

    String startDateTime, endDateTime = null;
    newLocPercentageResponseModel mRetLocpercResponse = null;
    List<LocPercentageRespModel> locPercentageRespModels = null;
    androidx.appcompat.app.ActionBar actionBar;

    AlertDialog.Builder alertBuilder;
    private TabLayout tabLayout;
    private FloatingActionButton sendReportToPM;
    private BottomSheetDialog mBottomSheetEmailLogs;
    private boolean isRefreshCalled;
    public static boolean isRefreshCalledFromTabScreen;
    private MenuItem menuItemSync;

    @Inject
    SiteDataSource siteDataSource;

    private boolean isSiteTypeDefault;
    private boolean isFromCreateEventScreen;
    private FloatingActionButton fabPrintCoc;
    private FloatingActionButton fabPrintLabels;
    private ArrayList<SCocMaster> listCOCDisplayNames = new ArrayList<>();
    private HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();
    private BadELFGPSTracker badElf;
    private RecyclerView rvLocations;
    private ProgressBar pbLocations;
    private FusedLocationProviderClient fusedLocationClient;
    private android.location.Location lastLocation;
    private volatile CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private CustomBoldTextView tvEventDate;
    private CustomBoldTextView tvEventName;
    //    private CustomTextView tvUserName;
    private CustomTextView tvProjectName;
    private RelativeLayout layoutCloseEvent;
    private ImageView ivEventOptions;
    private CustomTextView tvFormName;
    private ActivityResultLauncher<Intent> qrCodeLauncher;
    private CustomEditText edtSearchLocation;
    private String qrText;
    private boolean isSiteTypeDemo;
    private BottomSheetDialog mBottomSheetReportsList;
    private FetchAllReportByIdResponse.Data reportDataToGenerate = null;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    int TOTAL_FORM_FIELD_COUNT = 0;

    public String curr_cocid = null;

    Toolbar toolbar;
    MaterialSearchView searchView;
    TextView emptylist_view, switch_map, location_attribute_hdr_tv;
    ImageView switch_map_iv, filter_iv, mSortLocationList, mGeoSearch;
    String mSortSelection = "null";

    int SELECTED_TAB = 0;
    //    boolean IS_FORM_LOCATIONS_AVAILABLE = false;
    ArrayList<SCocMaster> cocMasterArrayList = new ArrayList<>();
    CustomListAdapterDialog customListAdapterDialog = null;
    String locationID = null;
    String locationName = null;
    Location locationObj = null;

    public static ArrayList<ModelClassLocationsWithAttribute> arrayListLocationHavingAttribute;
    String mSiteId;

    String deviceToken;
    String userGuid = null;

    RelativeLayout mRelativeLayoutBottomSheet;
    int countMediaSync = 0;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handlerForUI = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_ux);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        objContext = this;
        LocActivity = this;
        GlobalStrings.currentContext = this;
        badElf = new BadELFGPSTracker(LocationActivity.this);

/*        siteDataSource = new SiteDataSource(objContext);
        LDSource = new LocationDataSource(objContext);
        attachDataSource = new AttachmentDataSource(objContext);*/

        toolbar = findViewById(R.id.location_toolbar);
        setSupportActionBar(toolbar);

        qrCodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        qrText = data.getStringExtra(GlobalStrings.QR_SCANNED_TEXT);

                        edtSearchLocation.setText(qrText);
                        edtSearchLocation.setSelection(edtSearchLocation.getText().length());

                        if (newLocationsAdapter != null) {
                            newLocationsAdapter.getFilter().filter(qrText);
                            newLocationsAdapter.notifyDataSetChanged();
                        }
                    }
                });

        setUpEventCard();

        SharedPref.globalContext = getApplicationContext();
        mAquaBlueService = new AquaBlueServiceImpl(LocationActivity.this);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.select_location);
        }

        //7/17/2018 Location Attribute Header
        location_attribute_hdr_tv = findViewById(R.id.location_attribute_header);

        processExtraData();

        setTabLayout();

        CheckPermission();

        Log.i(TAG, "onCreate() extract arguments end time:- " + System.currentTimeMillis());

        setUsername(Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERNAME));
        setPassword(Util.getSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD));
        companyID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.COMPANYID));
        siteName = Util.getSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_SITENAME);
        //refreshing in onResume also if locationAct is initiated from form screen if event changed
        setSiteID(Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_SITEID)));
        setSiteName(siteName);

        Log.e("abc", "onCreate: " + getSiteID());
        //deviceID = DeviceInfo.getDeviceID(objContext);
        deviceToken = Util.getSharedPreferencesProperty(objContext, GlobalStrings.NOTIFICATION_REGISTRATION_ID);
        username = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(objContext, username);

        try {
            setUserID(Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERID)));
            Log.i(TAG, "Session UserID:" + getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in parsing Shared preferences for userID:" + e.getMessage());

            UserDataSource userData = new UserDataSource(objContext);
            User newUser = userData.getUser(username);
            if (newUser != null) {
                setUserID(newUser.getUserID());
                Log.i(TAG, "Current user by username=" + username + " from DeviceDB is UserID:" + getUserID());
            }
        }

        init();

        setDeviceID(DeviceInfo.getDeviceID(objContext));
        Log.i(TAG, "onCreate() Current WIFI DeviceID:" + getDeviceID());

        setClickListeners();

        TOTAL_FORM_FIELD_COUNT = new MetaDataSource(objContext).getRoll_Into_Form_Fields_Count(getSiteID() + "", getAppID() + "");

        LocationDataSource locationSource = new LocationDataSource(objContext);
        isSiteTypeDefault = siteDataSource.isSiteTypeDefault(siteID);

        if (isSiteTypeDefault && isFromCreateEventScreen) {

            ArrayList<Location> allEventLocations = locationSource.getDataForEventLocation(getSiteID(), getAppID(), getEventID(), false, true);

            if (allEventLocations.isEmpty()) {
                Intent intent = new Intent(objContext, AddLocationActivity.class);
                intent.putExtra("MOBILEAPP_ID", getAppID());
                startActivityForResult(intent, REQUEST_CODE_ADD_LOCATION);
            }
        }
    }

    private void setUpEventCard() {
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventName = findViewById(R.id.tvEventName);
        tvFormName = findViewById(R.id.tvFormName);
        tvProjectName = findViewById(R.id.tvProjectName);
//        tvUserName = findViewById(R.id.tvUserName);
        layoutCloseEvent = findViewById(R.id.layoutCloseEvent);
        ivEventOptions = findViewById(R.id.ivEventOptions);
        ivEventOptions.setVisibility(View.GONE);

        tvEventDate.setBackgroundColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                )
        );

        tvEventName.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorBlack
                )
        );

        tvFormName.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorBlack
                )
        );

        tvProjectName.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorBlack
                )
        );

/*        tvUserName.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorBlack
                )
        );*/

        ivEventOptions.setOnClickListener(view -> {

        });

        layoutCloseEvent.setOnClickListener(view -> {
            submitEndbtn.performClick();
        });
    }

    private void setTabLayout() {
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.has_data));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.no_data));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SELECTED_TAB = tab.getPosition();
                searchView.closeSearch();
                populateLocation();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setClickListeners() {

        fabPrintCoc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                PrintCOCActivity.startPrintCOCActivity(LocationActivity.this,
                        eventID + "", siteID + "");
            }
        });

        fabPrintLabels.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPrintCOCLabelsPopup(v, false);
                menuMultipleActions.collapse();
//                generateHtmlForLaebls(new ArrayList<>());
                PrintCOCLabelsActivity.startPrintCOCLabelsActivity(LocationActivity.this,
                        siteID, eventID, appID);
            }
        });

        filter_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(objContext, LocationAttributeActivity.class));
            }
        });

        mSortLocationList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LocationActivity.this);
                bottomSheetDialog.setContentView(R.layout.location_sort);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                Button btnAscending = bottomSheetDialog.findViewById(R.id.buttonAscending);
                Button btnDescending = bottomSheetDialog.findViewById(R.id.buttonDescending);
                Button btnCancel = bottomSheetDialog.findViewById(R.id.buttonCancel);

                btnAscending.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(LocationActivity.this, "Ascending", Toast.LENGTH_SHORT).show();
                        mSortSelection = "Ascending";
                        bottomSheetDialog.dismiss();
//                        setLocationAdapter();
                        setLocationsAdapter();
                    }
                });

                btnDescending.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(LocationActivity.this, "Descending", Toast.LENGTH_SHORT).show();

                        mSortSelection = "Descending";
                        bottomSheetDialog.dismiss();
//                        setLocationAdapter();
                        setLocationsAdapter();
                    }
                });

                btnCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(LocationActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        //setLocationAdapter();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        mGeoSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGeoSearch = new Intent(LocationActivity.this, GeoSearchMapsActivity.class);
                startActivity(intentGeoSearch);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i(TAG, "Search Text:" + query);
                if (newLocationsAdapter != null) {
                    newLocationsAdapter.getFilter().filter(query);
                    newLocationsAdapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newLocationsAdapter != null) {
                    newLocationsAdapter.getFilter().filter(newText);
                    newLocationsAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        switch_map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean gpsPermissionStatus = checkLocationPermission();
                if (gpsPermissionStatus) {
                    startMapActivity();

                    if (searchView.isSearchOpen()) {
                        searchView.closeSearch();
                    }
                    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);

                } else {
                    CheckPermission();
                }
            }
        });

        switch_map_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean gpsPermissionStatus = checkLocationPermission();
                if (gpsPermissionStatus) {
                    startMapActivity();
                    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                } else {
                    CheckPermission();
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(objContext, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(objContext, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onCreate() Location access permission Granted.");
            return;
        }

        newlocationbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Add new Location Click time:" + System.currentTimeMillis());

                menuMultipleActions.collapse();
                Intent intent = new Intent(objContext, AddLocationActivity.class);
                intent.putExtra("MOBILEAPP_ID", getAppID());
                startActivityForResult(intent, REQUEST_CODE_ADD_LOCATION);
            }
        });

/*        SiteDataSource siteData = new SiteDataSource(objContext);
        String siteMobileReportRequired = siteData.getSiteMobileReportRequiredStatus(getSiteID());

        try {
            if (siteMobileReportRequired != null) {
                if (siteMobileReportRequired.equals("download_pdf")) {
                    mobileReportRequired.setVisibility(View.VISIBLE);
                } else {
                    mobileReportRequired.setVisibility(View.GONE);
                }
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/ //commented on 30 March, 21 to disable mobile report floating btn

        mobileReportbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                Intent i = new Intent(objContext, MobileReportActivity.class);
                i.putExtra("SITE_NAME", getSiteName());
                i.putExtra("SITE_ID", getSiteID() + "");
                i.putExtra("EVENT_ID", getEventID() + "");
                i.putExtra("APP_NAME", dispappName);
                startActivity(i);
            }
        });

        sendReportToPM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                showSendReportPopupWindow(v);
                showEmailLogsBottomSheet();
            }
        });

        mobileReportRequired.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                String data;
                String filePath = Util.getFileFolderDirPathForPDF(objContext,
                        String.valueOf(siteID), String.valueOf(eventID));

                if (filePath.isEmpty())
                    return;

                File file = new File(filePath, "" + eventID + ".pdf");
                if (file.exists()) {
                    FileReader fr = null;
                    try {
                        fr = new FileReader(file);
                        BufferedReader br = new BufferedReader(fr);
                        data = br.readLine();
                        Log.e("pdfData", "onClick: " + data);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LocationActivity.this);
                    builder.setTitle(getString(R.string.report_generate))
                            .setMessage(getString(R.string.click_view_to_download_new_report_or_view))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.view), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(LocationActivity.this, MobileReportRequiredActivity.class);
                                    intent.putExtra("USER_ID", getUserID() + "");
                                    intent.putExtra("FORM_ID", getAppID() + "");
                                    intent.putExtra("SITE_ID", getSiteID() + "");
                                    intent.putExtra("EVENT_ID", getEventID() + "");
                                    intent.putExtra("call", "LocationActivity");
                                    startActivity(intent);
                                }
                            })
                            .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getReport(false, true, true);
                                }
                            });
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    getReport(false, true, true);
                }
            }
        });

        syncDatabtn.setOnClickListener(v -> {
            menuMultipleActions.collapse();
            SyncData();
        });

        submitEndbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                requestCurrentLocation();

                Log.i(TAG, "Submit and End onClick() Start time:" + System.currentTimeMillis());

                if (CheckNetwork.isInternetAvailable(objContext)) {
                    ArrayList<RequiredFieldRowItem> reqDataList
                            = fieldSource.getMandatoryFieldList(getAppID() + "",
                            getEventID() + "", getSiteID() + "");

                    if (CalendarFragment.hasRequiredLocationsFields(getSiteID(), getEventID(),
                            getAppID(), LocationActivity.this)) {
                        //all operations are done in the condition method
                    } else if (reqDataList != null && reqDataList.size() > 0 && reqDataList.get(0).getCount() > 0) {
                        RequiredDataInFormAlert();
                    } else {
                        closeEventAlert();
                    }
                } else {
                    Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
                Log.i(TAG, "Submit and End onClick() End time:" + System.currentTimeMillis());
            }
        });
    }

    private void startMapActivity() {
        Intent i = new Intent(objContext, MapActivity.class);
        i.putExtra("SITE_ID", getSiteID());
        i.putExtra("SITE_NAME", getSiteName());
        i.putExtra("EVENT_ID", getEventID());
        i.putExtra("APP_ID", getAppID());
        i.putExtra("USER_ID", getUserID());
        i.putExtra("PREV_CONTEXT", "Location");
        i.putExtra("OPERATION", GlobalStrings.SHOW_TAGGED_LOCATION);
        startActivity(i);
        finish();
    }

    private void openQRCodeActivity() {
        if (checkCameraPermission()) {
            Intent intent = new Intent(objContext, QRScannerActivity.class);
            qrCodeLauncher.launch(intent);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestCameraPermission();
            }
        }
    }

    String mPermissionCamera = Manifest.permission.CAMERA;

    public boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23)
            return checkSelfPermission(mPermissionCamera) == PackageManager.PERMISSION_GRANTED;
        else
            return true;
    }

    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkCameraPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[]{mPermissionCamera,
                        },
                        FormActivity.REQUEST_CODE_CAMERA_PERMISSION);
            }
        }
    }

    public void showSendReportPopupWindow(View view) {
        if (menuMultipleActions.isExpanded())
            menuMultipleActions.collapse();

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_alert_send_report, null);

        TextView btnPdf = popupView.findViewById(R.id.tvSendAsPdf);
        TextView btnDoc = popupView.findViewById(R.id.tvSendAsDoc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnPdf.setBackground(ContextCompat.getDrawable(this, R.drawable.ripple_effect_white));
            btnDoc.setBackground(ContextCompat.getDrawable(this, R.drawable.ripple_effect_white));
        } else {
            btnPdf.setBackground(ContextCompat.getDrawable(this, R.drawable.drawable_bg_white));
            btnDoc.setBackground(ContextCompat.getDrawable(this, R.drawable.drawable_bg_white));
        }

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        btnPdf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getReport(true, true, true);
                popupWindow.dismiss();
            }
        });
        btnDoc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getReport(true, false, false);
                popupWindow.dismiss();
            }
        });
    }

    public void showPrintCOCLabelsPopup(View view, boolean isPrintCOC) {
        if (menuMultipleActions.isExpanded())
            menuMultipleActions.collapse();

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_print_labels_coc, null);

        RecyclerView rvCOCDisplayName = popupView.findViewById(R.id.rvCOCLabels);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        rvCOCDisplayName.setLayoutManager(new LinearLayoutManager(this));
        rvCOCDisplayName.setHasFixedSize(true);
        rvCOCDisplayName.setItemAnimator(new DefaultItemAnimator());

        rvCOCDisplayName.setAdapter(new COCLabelsAdapter(listCOCDisplayNames, cocSelected -> {
            popupWindow.dismiss();
            printCOCLabels(cocSelected, isPrintCOC);
        }));
    }

    private void printCOCLabels(SCocMaster cocSelected, boolean isPrintCOC) {
        if (!isPrintCOC) {
            ArrayList<String> locIds = new ArrayList<>();
            locIds.add("270254");
            CocDetailDataSource cocDetailDataSource = new CocDetailDataSource(this);
            ArrayList<SCocDetails> cocLabels = cocDetailDataSource.getAllLabelsToPrint(cocSelected.getCocId() + "", locIds);
        }
    }

    private void showReportNamesBottomSheet(ArrayList<FetchAllReportByIdResponse.Data> reportsList, boolean isEmailMyself) {

        if (menuMultipleActions.isExpanded())
            menuMultipleActions.collapse();

        BottomsheetReportNamesBinding binding
                = BottomsheetReportNamesBinding.inflate(LayoutInflater.from(this));

        mBottomSheetReportsList = new BottomSheetDialog(this);
        mBottomSheetReportsList.setContentView(binding.getRoot());
        mBottomSheetReportsList.show();

        // Remove default white color background
        FrameLayout bottomSheet = mBottomSheetReportsList
                .findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackground(null);
        }

        ReportsAdapter adapter = new ReportsAdapter(reportsList, new ReportsAdapter.ReportClickedListener() {
            @Override
            public void onReportClicked(@NonNull FetchAllReportByIdResponse.Data report) {
                mBottomSheetReportsList.cancel();
                reportDataToGenerate = report;
                if (isEmailMyself)
                    getReport(true, true, true);
                else
                    getReport(true, false, false);
            }
        });

        binding.rvReportNames.setAdapter(adapter);
    }

    private void showEmailLogsBottomSheet() {

        if (menuMultipleActions.isExpanded())
            menuMultipleActions.collapse();

        try {
            View sheetView = LayoutInflater.from(this)
                    .inflate(R.layout.layout_bottom_sheet_email_logs, null);
            mBottomSheetEmailLogs = new BottomSheetDialog(this);
            mBottomSheetEmailLogs.setContentView(sheetView);
            mBottomSheetEmailLogs.show();

            // Remove default white color background
            FrameLayout bottomSheet = mBottomSheetEmailLogs
                    .findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackground(null);
            }

            sheetView.findViewById(R.id.llRename).setVisibility(View.GONE);
            sheetView.findViewById(R.id.llViewReport).setVisibility(View.GONE);
            sheetView.findViewById(R.id.dividerViewReport).setVisibility(View.GONE);
            sheetView.findViewById(R.id.llDownloadData).setVisibility(View.GONE);
            sheetView.findViewById(R.id.dividerDownloadEvent).setVisibility(View.GONE);

            LinearLayout llEmailMyself = sheetView.findViewById(R.id.llSendPdf);
            LinearLayout llEmailTeam = sheetView.findViewById(R.id.llSendDoc);
            LinearLayout llCancel = sheetView.findViewById(R.id.llCancel);

            llEmailMyself.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                fetchReportsByIdNames(true);
            });

            llEmailTeam.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                fetchReportsByIdNames(false);
            });

            llCancel.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchReportsByIdNames(boolean isEmailMyself) {
        FetchReportsById fetchReportsById = new FetchReportsById(this, getAppID() + "",
                new FetchReportsById.FetchReportsByIdResponseListener() {
                    @Override
                    public void onGetReportsSuccess(@NonNull FetchAllReportByIdResponse response, boolean isEmailMyself) {
                        Log.d("reports list", response.toString());
                        if (response.getData().size() == 1) {
                            reportDataToGenerate = response.getData().get(0);
                            if (isEmailMyself)
                                getReport(true, true, true);
                            else
                                getReport(true, false, false);
                        } else
                            showReportNamesBottomSheet(response.getData(), isEmailMyself);
                    }

                    @Override
                    public void onGetReportsFailed(@NonNull String message) {
                        Log.d("reports list failed", message);
                    }
                }, isEmailMyself);

        fetchReportsById.fetchReportNamesList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();

        try {
            tabLayout.getTabAt(0).select();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showAlertProgress();
    }

    private void processExtraData() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            SharedPreferences.Editor editor = getSharedPreferences("Event_AppId", MODE_PRIVATE).edit();
            editor.clear();

            editor.putInt("EventId", extras.getInt("EVENT_ID"));
            editor.putInt("AppId", extras.getInt("APP_ID"));
            editor.apply();
            editor.commit();

            setEventID(extras.getInt("EVENT_ID"));
            setAppID(extras.getInt("APP_ID"));

            if (extras.containsKey("Action_Sync")) {
                // syncAll=true;
                SyncData();
            }

            if (extras.containsKey("SITE_NAME")) {
                setSiteName(extras.getString("SITE_NAME"));
            }

            isFromCreateEventScreen = extras.getBoolean(GlobalStrings.IS_FROM_CREATE_EVENT_SCREEN);

            setEventDetails();
        }
    }

    private void setEventDetails() {
        EventData event = new EventDataSource(this)
                .getEvent(eventID + "", getAppID() + "");

        if (event != null) {
            tvEventDate.setText(Util.getFormattedDateFromMilliS(event.getStartDate(),
                    " dd \nMMM"));
            tvEventName.setText(event.getEventName());
            tvFormName.setText(event.getSiteName());
            tvProjectName.setText(event.getMobAppName());
//            String userName = new UserDataSource(this).getUserNameFromID(event.getUpdatedBy());
//            tvUserName.setText(userName);
        }
    }

    private boolean checkLocationPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = objContext.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void getReport(boolean isForPM, boolean isPdf, boolean isForSelf) {
        //even if isPdf has value, use of it depends on the isForPM value in api call

        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
        }

        if (CheckNetwork.isInternetAvailable(objContext)) {
            new FetchReport(isForPM, isPdf, isForSelf).execute();
        } else {
            Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onEraseLocationClicked(LocationAdapter.ViewHolder viewHolder, String locId) {
        showLocationEraseAlert(viewHolder, locId);
    }

    @Override
    public void onLocationItemClicked(Location location) {
        locationObj = selectedLocatonObj;//setting empty loc
        locationObj = location;
        onLocationItemClickAction();
    }

    @Override
    public void onEraseLocationClicked(LocationsAdapter.ViewHolder viewHolder, String locId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.remove_location_data));
        builder.setMessage(getString(R.string.do_u_want_to_clear_location_data));
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            if (fieldSource.clearLocationData(eventID + "", locId, siteID + "")) {
                viewHolder.btnErase.setVisibility(View.GONE);
                viewHolder.circleProgressView.setVisibility(View.GONE);
                Toast.makeText(objContext, getString(R.string.location_data_is_cleared), Toast.LENGTH_SHORT).show();
                newLocationsAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onTagLocationClicked(Location location) {
        onclickTagLocation(location.getLocationID(), location.getLatitude(),
                location.getLongitude(), location.getLocationName());
    }

    private void showLocationEraseAlert(LocationAdapter.ViewHolder viewHolder, String locId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.remove_location_data));
        builder.setMessage(getString(R.string.do_u_want_to_clear_location_data));
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            if (fieldSource.clearLocationData(eventID + "", locId, siteID + "")) {
                viewHolder.btnErase.setVisibility(View.GONE);
                viewHolder.circleProgressView.setVisibility(View.GONE);
                Toast.makeText(objContext, getString(R.string.location_data_is_cleared), Toast.LENGTH_SHORT).show();
                locationAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onEventDownloadSuccess() {
        callMetaSync();
    }

    @Override
    public void onEventDownloadFailed() {
        callMetaSync();
    }

    @Override
    public void onLocationDeny() {

    }

    private class FetchReport extends AsyncTask<MediaType, Void, String> {

        private final boolean isForSelf;
        private boolean isForPM;
        private boolean isPdf;

        public FetchReport(boolean isForPm, boolean isPdf, boolean isForSelf) {
            this.isForPM = isForPm;
            this.isPdf = isPdf;
            this.isForSelf = isForSelf;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isForPM)
                showAlertProgress(getString(R.string.generating_report_please_wait));
        }

        @Override
        protected String doInBackground(MediaType... params) {
            try {
                if (null != mAquaBlueService) {
/*                    if (!Util.isUrlV20OrMobileTest(objContext))
                        isDownloaded = mAquaBlueService.generateReport(getResources().getString(R.string.prod_base_uri),
                                getResources().getString(R.string.mobile_report_required),
                                getSiteID() + "", getEventID() + "", getAppID() + "",
                                getUserID() + "", isForPM, isPdf, isForSelf);
                    else*/
                    isDownloaded = mAquaBlueService.generateReport(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.generate_report_by_id),
                            getSiteID() + "", getEventID() + "", getAppID() + "",
                            getUserID() + "", isForPM, isPdf, isForSelf,
                            reportDataToGenerate.getReportId() + "", reportDataToGenerate.getReportName());

                    if (isDownloaded.equals("false")) {
                        Log.i(TAG, "FDownloadData response :" + isDownloaded);
                        mResponseString = "false";
                    } else {
                        mResponseString = isDownloaded;
                    }
                } else {
                    mErrorString = isDownloaded;
                }
            } catch (Exception n) {
                n.printStackTrace();
            }
            return mResponseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isForPM) {
                cancelAlertProgress();
                String msg;
                if (s.equals("false")) {
                    msg = getString(R.string.unable_to_gen_report);
                } else {
                    msg = s;
                }
                showReportAlert(msg);
            } else {
                showResult(s);
            }
        }
    }

    private void showReportAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showResult(String s) {
        try {
            if (s.equals("false")) {
                Toast.makeText(objContext, getString(R.string.pdf_not_generated), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(objContext, "" + s, Toast.LENGTH_LONG).show();
            }
        } catch (Exception n) {
            n.printStackTrace();
        }
    }

    private void init() {
        menuMultipleActions = findViewById(R.id.multiple_actions);
        syncDatabtn = findViewById(R.id.action_upload);
        newlocationbtn = findViewById(R.id.action_addnew);
        submitEndbtn = findViewById(R.id.action_closeevent);
        fabPrintCoc = findViewById(R.id.fab_print_coc);
        fabPrintLabels = findViewById(R.id.fab_print_labels);
        edtSearchLocation = findViewById(R.id.edtSearchLocation);

        if (ScreenReso.isLimitedUser)
            submitEndbtn.setVisibility(View.GONE);

        submitEndbtn.setVisibility(View.GONE);

        setUpSearchView();

        mobileReportbtn = findViewById(R.id.action_mobilereport);
        mobileReportRequired = findViewById(R.id.action_mobileReportRequired);
        sendReportToPM = findViewById(R.id.fabSendReportToPM);
        listView = findViewById(R.id.locListView);
        pbLocations = findViewById(R.id.pbLocations);
        rvLocations = findViewById(R.id.rvLocations);
        emptylist_view = findViewById(R.id.empty_location);
        switch_map = findViewById(R.id.switch_map_tv);

        switch_map_iv = findViewById(R.id.list_mapview_iv);
        filter_iv = findViewById(R.id.filter_ib);
        searchView = findViewById(R.id.search_view);

        mRelativeLayoutBottomSheet = findViewById(R.id.relativeLayoutBottomSheet);
        mSortLocationList = findViewById(R.id.sortLocationList);
        mGeoSearch = findViewById(R.id.geoSearch);

        dispappName = new SiteMobileAppDataSource(objContext)
                .getMobileAppDisplayNameRollIntoApp(getAppID(), getSiteID());

        mSiteId = Util.getSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_SITEID);
        LocationAttributeDataSource locationAttributeDataSource
                = new LocationAttributeDataSource(objContext);
        arrayListLocationHavingAttribute = locationAttributeDataSource
                .getAllLocationWithAttribute(mSiteId);
        Log.e(TAG, "onCreate: " + arrayListLocationHavingAttribute.size());

        filter_iv.setVisibility(View.GONE);
        if (arrayListLocationHavingAttribute.size() > 0) {
            filter_iv.setVisibility(View.VISIBLE);
        } else {
            filter_iv.setVisibility(View.GONE);
            SharedPreferences settings = getSharedPreferences("MULTIPLEATTRIBUTE",
                    Context.MODE_PRIVATE);
            settings.edit().clear().commit();
        }

        if (dispappName == null) {
            dispappName = new SiteMobileAppDataSource(objContext).getMobileAppDisplayNameRollIntoAppForSite(getAppID());
        }
//        actionBar.setTitle(dispappName);

        Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_APPNAME, dispappName);

        String APP_TYPE = Util.getSharedPreferencesProperty(objContext, GlobalStrings.APP_TYPE);

        AppPreferenceDataSource ds = new AppPreferenceDataSource(objContext);

        //19-Jun-17 CHECK IS ADD NEW LOCATION FETAURE AVAILBLE OR NOT
        //KEY_ADD_LOCATION
        if (ds.isFeatureAvailable(GlobalStrings.KEY_ADD_LOCATION, getUserID())) {
            if (APP_TYPE != null && APP_TYPE.equalsIgnoreCase("EHS")) {
                newlocationbtn.setVisibility(View.GONE);
            } else {
                newlocationbtn.setVisibility(View.VISIBLE);
            }
        } else {
            newlocationbtn.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpSearchView() {
        edtSearchLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() >= (edtSearchLocation.getRight()
                            - edtSearchLocation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        openQRCodeActivity();
                        return true;
                    }
                }
                return false;
            }
        });

        edtSearchLocation.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (newLocationsAdapter != null) {
                    newLocationsAdapter.getFilter().filter(Objects.requireNonNull(edtSearchLocation.getText()).toString());
                    newLocationsAdapter.notifyDataSetChanged();
                }
                return true;
            }
            return false;
        });

        edtSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (newLocationsAdapter != null) {
                    newLocationsAdapter.getFilter().filter(editable.toString());
                    newLocationsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setPrintLabelCOCBtns() {
/*        if (!listCOCDisplayNames.isEmpty())
            listCOCDisplayNames.clear();

        CocMasterDataSource cocMasterDataSource = new CocMasterDataSource(this);
        listCOCDisplayNames = cocMasterDataSource.getCoCListForSelectedEvent(eventID + "");

        if (!listCOCDisplayNames.isEmpty()) {
            fabPrintCoc.setVisibility(View.VISIBLE);
            fabPrintLabels.setVisibility(View.VISIBLE);
        } else {
            fabPrintCoc.setVisibility(View.GONE);
            fabPrintLabels.setVisibility(View.GONE);
        }*/

        SiteMobileAppDataSource mobileAppDataSource = new SiteMobileAppDataSource(this);
        String mobAppId = mobileAppDataSource.getAppTypeCOC(appID);
        boolean isCOC = mobAppId != null && !mobAppId.isEmpty();

        if (isCOC) {
            fabPrintCoc.setVisibility(View.VISIBLE);
            fabPrintLabels.setVisibility(View.VISIBLE);
        } else {
            fabPrintCoc.setVisibility(View.GONE);
            fabPrintLabels.setVisibility(View.GONE);
        }
    }

    private void CheckPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else if (!checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(permission.WRITE_EXTERNAL_STORAGE)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, NETWORK_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(Manifest.permission.ACCESS_WIFI_STATE)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.ACCESS_WIFI_STATE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, WIFI_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(Manifest.permission.CAMERA)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        } else {
            requestCurrentLocation();
        }
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(objContext, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {

/*        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastLocation = location;
                    }
                });*/

        //added new instance again in case if activity is resumed and cancellationToken is cancelled
        //then it gives crash as it cant be used if cancelled
        cancellationTokenSource = new CancellationTokenSource();

        // Request permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            // Main code
            Task<android.location.Location> currentLocationTask = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken()
            );

            currentLocationTask.addOnCompleteListener((new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {

                    String result = "";

                    if (task.isSuccessful()) {
                        // Task completed successfully
                        lastLocation = task.getResult();
                        if (lastLocation != null) {

                            try {
                                result = "Location (success): " +
                                        lastLocation.getLatitude() +
                                        ", " +
                                        lastLocation.getLongitude();
                                GlobalStrings.CURRENT_GPS_LOCATION = lastLocation;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:

/*                Intent intent = new Intent(LocationActivity.this, LocationActivity.class);
                intent.putExtra("EVENT_ID", getEventID());
                intent.putExtra("APP_ID", getAppID());
                intent.putExtra("SITE_NAME", getSiteName());
                startActivity(intent);*/
                requestCurrentLocation();
                if (!checkPermission(permission.READ_PHONE_STATE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, permission.READ_PHONE_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION_REQUEST_CODE);
                    }
                }

            case PHONE_STATE_PERMISSION_REQUEST_CODE:

                if (!checkPermission(permission.WRITE_EXTERNAL_STORAGE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(this, new String[]{permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                    }
                }

            case STORAGE_PERMISSION_REQUEST_CODE:
                if (!checkPermission(permission.ACCESS_NETWORK_STATE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, permission.ACCESS_NETWORK_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_NETWORK_STATE}, NETWORK_PERMISSION_REQUEST_CODE);
                    }
                }

            case NETWORK_PERMISSION_REQUEST_CODE:

                if (!checkPermission(permission.ACCESS_WIFI_STATE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, permission.ACCESS_WIFI_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_WIFI_STATE}, WIFI_PERMISSION_REQUEST_CODE);
                    }
                }

            case WIFI_PERMISSION_REQUEST_CODE:
                if (!checkPermission(permission.CAMERA)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, permission.CAMERA)) {
                        ActivityCompat.requestPermissions(this, new String[]{permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                }

            case FormActivity.REQUEST_CODE_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermission();
                } else {
                    AlertManager.showPermissionSettingsAlert("Qnopy require camera permission to capture Photos or scan Barcode/QRCode. " +
                            "Please accept permission manually from settings.", this);
                }
            }
        }
    }

    void SyncData() {
        Log.i(TAG, "SyncData() IN time:" + System.currentTimeMillis());

        if (CheckNetwork.isInternetAvailable(objContext)) {
            EventDataSource eventData = new EventDataSource(objContext);

            String creationDate = Util.parseMillisToMMMddyyy_hh_mm_ss_aa(System.currentTimeMillis());

            ArrayList<DEvent> eventList = eventData
                    .getClientGeneratedEventIDs(objContext);

            int count = eventList.size();

            if (count > 0) {
                EventIDGeneratorTask eventHandler
                        = new EventIDGeneratorTask(LocationActivity.this, null, username,
                        password, true);

                eventHandler.execute();
            } else {
                boolean serviceRunning = isMyServiceRunning(DataSyncService.class);
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
    }

    public void openGallery() {
        Intent i = new Intent(getApplicationContext(),
                GalleryActivity.class);
        i.putExtra("SITE_ID", getSiteID());
        i.putExtra("EVENT_ID", getEventID());
        i.putExtra("LOC_ID", getLocID());
        i.putExtra("MOBILE_APP_ID", getAppID());
        i.putExtra("SET_ID", setID);
        i.putExtra("USER_ID", getUserID());
        try {
            startActivity(i);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
        } else {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            } else {
                super.onBackPressed();
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();
            }
        }
        //getFragmentManager().popBackStack();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            //            case R.id.download_forms:
//                downloadDialog();
//                return true;

            case R.id.takephoto:
                Log.i(TAG, "menu takephoto clicked time:" + System.currentTimeMillis());
                // handleCamera();
                return true;

            case R.id.gallery:
                Log.i(TAG, "menu gallery clicked time:" + System.currentTimeMillis());
                openGallery();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onPause() {
        badElf.disconnectTracker();
        super.onPause();
    }

    @Override
    protected void onStop() {
        badElf.disconnectTracker();
        cancellationTokenSource.cancel();
        super.onStop();
    }

    public void closeEventAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LocationActivity.this);

        //  alertDialogBuilder.setTitle(this.getTitle() + " DECISION");

        ////1/29/2018
        alertDialogBuilder.setTitle(getString(R.string.close_end_field_event));
        alertDialogBuilder
                .setMessage(getString(R.string.sure_submit_data_and_close_event));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//19-Jun-17
//                        AppPreferenceDataSource ds = new AppPreferenceDataSource(objContext);
//                        //KEY_SIGNATURE
//                        if (ds.isFeatureAvailable(GlobalStrings.KEY_SIGNATURE, getUserID())) {
//                            //19-Jun-17 CAPTURE SIGNATURE AFTER SUBMIT AND END
//
                        String capture = Util.getSharedPreferencesProperty(objContext, GlobalStrings.CAPTURE_SIGNATURE);
                        Boolean CAPTURE_SIGNATURE = false;

                        if (capture == null) {
                            CAPTURE_SIGNATURE = false;
                        } else {
                            CAPTURE_SIGNATURE = Boolean.parseBoolean(capture);
                        }

                        if (CAPTURE_SIGNATURE) {
                            Intent intent = new Intent(
                                    getApplicationContext(),
                                    CaptureSignature.class);
                            intent.putExtra("EVENT_ID", getEventID());
                            intent.putExtra("APP_ID", getAppID());
                            intent.putExtra("SITE_ID", getSiteID());
                            intent.putExtra("CLOSE", "true");
                            intent.putExtra("UserID", getUserID());
                            startActivityForResult(intent,
                                    CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE);
                        } else {
                            closingEvents();
                        }
                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getString(R.string.no),
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

    public void RequiredDataInFormAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LocationActivity.this);

        alertDialogBuilder.setTitle(getString(R.string.attention));
        alertDialogBuilder
                .setMessage(getString(R.string.some_forms_have_mandatory_field_need_to_be_filled));

        alertDialogBuilder.setPositiveButton(getString(R.string.show),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent req_intent = new Intent(objContext, RequiredFieldsListActivity.class);

                        req_intent.putExtra("APP_ID", getAppID());
                        req_intent.putExtra("EVENT_ID", getEventID());
                        req_intent.putExtra("SITE_ID", getSiteID());
                        req_intent.putExtra("SITENAME", getSiteName());
                        startActivity(req_intent);

                        dialog.dismiss();
                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_upper_case),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void MandatoryFieldForRequiredLocAlert(ArrayList<Location> requiredLocationList,
                                                         Context context, int eventId, int appId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(context.getString(R.string.attention));
        alertDialogBuilder
                .setMessage("Some locations have mandatory fields that need to be filled before ending this event.");

        alertDialogBuilder.setPositiveButton(context.getString(R.string.show),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(context,
                                RequiredLocationFieldsActivity.class);
                        intent.putExtra(GlobalStrings.REQUIRED_LOCATION, requiredLocationList);
                        intent.putExtra(GlobalStrings.KEY_EVENT_ID, eventId);
                        intent.putExtra(GlobalStrings.KEY_ROLL_APP_ID, appId);
                        context.startActivity(intent);
                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel_upper_case),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void uploadFieldData() {

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(this);

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("EventId: " + getEventID() + ", Site Id: " + getSiteID() + " AppId: " + appID);
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
        logDetails.setScreenName("Location Screen");
        logDetails.setDetails("Has field data before checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());

        tempLogsDataSource.insertTempLogs(logDetails);

        isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();//24-Mar-17
        isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(objContext);

        isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        logDetails.setDetails("Has field data upon checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());
        tempLogsDataSource.insertTempLogs(logDetails);

        logDetails.setDetails("CHECKING DATA TO SYNC - " + " Has locations:" + isLocationsAvailableToSync
                + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync);
        tempLogsDataSource.insertTempLogs(logDetails);

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync
                && !isAttachmentsAvailableToSync) {
            syncTasks();
        } else {
            Log.i(TAG, "uploadFieldData() Upload Field Data Called:" + System.currentTimeMillis());
            isRefreshCalled = true;

            Intent dataUpload = new Intent(objContext, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", getUsername());
            dataUpload.putExtra("PASS", getPassword());
            dataUpload.putExtra("EVENT_ID", getEventID());
            startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        }
        Log.i(TAG, "Upload Field Data arguments:username=" + getUsername() + ",password=" + getPassword() + ",EventID=" + getEventID());
    }

    public void uploadFieldDataBeforeEndEvent() {
        Log.i(TAG, "Upload Field Data endEvent Call  start:" + System.currentTimeMillis());
        Intent dataUpload = new Intent(objContext, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", getUsername());
        dataUpload.putExtra("PASS", getPassword());
        dataUpload.putExtra("EVENT_ID", getEventID());
        dataUpload.putExtra("CLOSE_EVENT", true);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        Log.i(TAG, "Upload Field Data endEvent arguments:EventID-=" + getEventID() +
                ",UserName=" + getUsername() + ",Password=" + getPassword());
        Log.i(TAG, "Upload Field Data endEvent Call End:" + System.currentTimeMillis());
    }

    public void closingEvents() {

        EventDataSource eventData = new EventDataSource(objContext);
        closeEvent = true;

        boolean serverGenerated = eventData
                .isEventIDServerGenerated(getEventID());

        if (!serverGenerated) {
            Log.i(TAG, "closingEvents() EventID Not Found Server Generated ");

            final DEvent event = new DEvent();
            event.setSiteId(siteID);
            event.setMobileAppId(getAppID());
            event.setUserId(getUserID());
            event.setEventDate(System.currentTimeMillis());
//            event.setEventDate(System.currentTimeMillis() - 86400000);
            event.setDeviceId(deviceID);

            if (lastLocation != null) {
                event.setLatitude(lastLocation.getLatitude());
                event.setLongitude(lastLocation.getLongitude());
            } else {
                event.setLatitude(0.0);
                event.setLongitude(0.0);
            }

            event.setUserName(username);
            EventIDGeneratorTask eventHandler = new EventIDGeneratorTask(this,
                    event, this.username, this.password, false);
            eventHandler.execute();
        } else {
            /*
             * fieldSource.updateEventEndDateTime(getAppID(), dateString);
             */

            Log.i(TAG, "closingEvents() EventID Found Server Generated ");

            if (CheckNetwork.isInternetAvailable(objContext)) {
                uploadFieldDataBeforeEndEvent();
            } else {
                Log.i(TAG, "closingEvents() No Internet.Delete captured signture(s) = " + savedFilePath);

                removeAttachmentAfterSyncResult(savedFilePath);
                CustomToast.showToast((Activity) objContext,
                        getString(R.string.bad_internet_connectivity), 5);
            }
        }

        Log.i(TAG, "closingEvents() End time:" + System.currentTimeMillis());
    }

    public int getAppID() {
        return appID;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // if (getEventID() == 0) {
        outState.putInt("EventID", getEventID());
        Log.i(TAG, "onSaveInstanceState() Save EventID:" + getEventID());
        // }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("EventID")) {
            setEventID(savedInstanceState.getInt("EventID"));
            Log.i(TAG, "onRestoreInstanceState() Restore EventID:" + getEventID());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, " Location Activity OnActivityResult code=" + requestCode);
        Log.i(TAG, " onActivityResult() Initiate AttachmentDataSource and GPSTracker Start time:" + System.currentTimeMillis());

        AttachmentDataSource attachDataSource = new AttachmentDataSource(objContext);
        Log.i(TAG, "onActivityResult() Initiate AttachmentDataSource and GPSTracker End time:" + System.currentTimeMillis());

        try {

            if (requestCode == MAP_ACTIVITY_REQUEST_CODE) {

            } else if (requestCode == CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE
                    && resultCode == RESULT_OK) {
                boolean isOk = true;
                closingEvents();
            } else if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                    && resultCode == RESULT_OK) {
                if (isRefreshCalled) {
                    isRefreshCalled = false;
                    syncTasks();
                } else if (data.hasExtra("SYNC_FLAG")) {
                    long date = System.currentTimeMillis();

                    boolean eventClosed = data.getBooleanExtra("SYNC_FLAG", false);
                    long eventEndDate = Long.parseLong(data.getStringExtra("EVENT_END_DATE"));
                    boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);

                    if (eventEndDate < 1) {
                        eventEndDate = date;
                    }
                    if (dataSynced && eventClosed) {
                        EventDataSource eventData = new EventDataSource(objContext);
                        CompletionPercentageDataSource cp = new CompletionPercentageDataSource(objContext);

                        eventData.closeEventStatus(getAppID(), getSiteID(), eventEndDate, getEventID() + "");
                        cp.truncatePercentageByRollAppID_And_SiteID(getSiteID() + "", getAppID() + "");
                        SharedPref.resetCamOrMap();

                        Toast.makeText(getApplicationContext(),
                                        getString(R.string.event_has_been_closed), Toast.LENGTH_LONG)
                                .show();

                        setResult(RESULT_OK);//setting OK for calendar fragment to update list again
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.event_cannot_be_closed),
                                Toast.LENGTH_LONG).show();
                    }
                    if (savedFilePath != null) {
                        removeAttachmentAfterSyncResult(savedFilePath);
                    }
                }
            } else if (requestCode == DOWNLOAD_KMZ_REQUEST_CODE
                    && resultCode == RESULT_OK) {

                Log.i(TAG, "onActivityResult() DOWNLOAD_KMZ Start time:" + System.currentTimeMillis());

                if (data.hasExtra("SUCCESS_FLAG") && data.hasExtra("SUCCESS_MESSAGE")) {
                    boolean dataDownloaded = data.getBooleanExtra("SUCCESS_FLAG", false);
                    String resultMessage = data.getStringExtra("SUCCESS_MESSAGE");

                    Log.i(TAG, "KMZ Download result:" + dataDownloaded + " , Result Message:" + resultMessage);
                    if (dataDownloaded) {

                        FileFolderDataSource fd = new FileFolderDataSource(objContext);
                        final ArrayList<FileFolderItem> list = fd.getKMZList(getSiteID() + "");
                        //13-Jun-17 KMZ FILE IS DOWNLOADED OR NOT
                        if (list.size() < 1) {
                            resultMessage = getString(R.string.no_kmz_files_uploaded) + "' "
                                    + Util.getSharedPreferencesProperty(objContext,
                                    GlobalStrings.CURRENT_SITENAME) + " ' " + getString(R.string.site).replace(":", "");
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(objContext);
                            alertBuilder.setMessage(resultMessage);
                            alertBuilder.setTitle(getString(R.string.message_lower_case));
                            alertBuilder.setNeutralButton(getString(R.string.ok), null);
                            Dialog alert = alertBuilder.create();
                            alert.show();

                        } else {
                            projectFolderPath = Util.getFileFolderDirPath(objContext, getSiteID() + "");
                            Log.i(TAG, "KML Location:" + projectFolderPath);

                            show_Dialog(projectFolderPath);
                        }
                    } else {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(objContext);
                        alertBuilder.setMessage(resultMessage);
                        alertBuilder.setTitle(getString(R.string.message_lower_case));
                        alertBuilder.setNeutralButton(getString(R.string.ok), null);
                        Dialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
            } else if (requestCode == REQUEST_CODE_ADD_LOCATION
                    && resultCode == RESULT_OK && data != null) {

                try {
                    NewLocationResponseModel locationsList
                            = (NewLocationResponseModel) data.getSerializableExtra(GlobalStrings.ADDED_LOCATIONS);

                    assert locationsList != null;
                    if (locationsList.getData() != null)
                        saveEventLocation(locationsList.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "Camera/Drawing Demo Pic NOT saved");
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult() Error:" + e.getMessage());
            CustomToast.showToast((Activity) objContext, getString(R.string.unable_to_connect_to_server)
                    , 5);
        }
        Log.i(TAG, "onActivityResult() End time:" + System.currentTimeMillis());
    }

    private void saveEventLocation(List<NewClientLocation> addedLocations) {

        if (siteDataSource.isSiteTypeDefault(siteID)) {
            EventLocationDataSource locationDataSource = new EventLocationDataSource(this);
            locationDataSource.insertEventLocations(addedLocations, getEventID() + "",
                    getAppID() + "");
        }
    }

    private void removeAttachmentAfterSyncResult(String filePath) {
        Log.i(TAG, "removeAttachmentAfterSyncResult() EventID Not Server Generated ");

        Log.i(TAG, "removeAttachmentAfterSyncResult() IN time:" + System.currentTimeMillis());
        deleteFileFromStorage(filePath);
        int count = new AttachmentDataSource(objContext).deleteAttachment(getEventID(), "S");
        Log.i(TAG, "Removed No.of Attachment:" + count);
        Log.i(TAG, "removeAttachmentAfterSyncResult() OUT time:" + System.currentTimeMillis());
    }

    public void deleteFileFromStorage(String filePath) {

        Log.i(TAG, "deleteFileFromStorage() Start time:" + System.currentTimeMillis());
        Log.i(TAG, "deleteFileFromStorage() FilePath:" + filePath);

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{filePath}, null, null);

        Log.i(TAG, "deleteFileFromStorage() End time:" + System.currentTimeMillis());
    }

    long start = 0;
    long end = 0;

    private void populateLocation() {

        executor.execute(() -> {
            handlerForUI.post(() -> {
                pbLocations.setVisibility(View.VISIBLE);
                emptylist_view.setVisibility(View.GONE);
            });

            mapLocations.clear();
            if (tempLocations != null)
                tempLocations.clear();
            Log.e(TAG, "Populate locations: " + Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                    GlobalStrings.DATE_FORMAT_H_M_S));
            start = System.currentTimeMillis();

            LocationDataSource locationSource = new LocationDataSource(objContext);

            mobileAppSource = new MobileAppDataSource(objContext);

            totalChildAppIdList = new ArrayList<Integer>();
            totalChildAppIdList = mobileAppSource.getAllChildAppIDForParentApp(getAppID(), getSiteID());

            //RETRIEVING SELECTED ATTRIBUTE USING HASHMAP STORED IN SHARED PREFERENCE
            HashMap<String, String> outputMap = new HashMap<>();
            SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
            try {
                if (pSharedPref != null) {
                    String jsonString = pSharedPref.getString("AttributeHashMap", (new JSONObject()).toString());
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String k = keysItr.next();
                        String v = (String) jsonObject.get(k);
                        outputMap.put(k, v);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (SELECTED_TAB == 1) {
                Log.i(TAG, "HAS DATA TAB");
                if (isSiteTypeDefault) {
                    tempLocations = locationSource.getDataForEventLocationV15(getSiteID(), getAppID(),
                            getEventID(), true, false);
                } else if (outputMap.isEmpty()) {
                    mapLocations
                            = locationSource.getHasDataLocFormDefaultOrNon(getSiteID(), getAppID(),
                            getEventID() + "");
                } else {
                    mapLocations = locationSource.getNoOrHasDataLocFormDefNonWithAttr(getSiteID(),
                            getAppID(), getEventID(), outputMap, false);
                }
            } else if (SELECTED_TAB == 2) {
                Log.i(TAG, "NO DATA TAB");
                if (isSiteTypeDefault) {
                    tempLocations = locationSource.getDataForEventLocationV15(getSiteID(), getAppID(),
                            getEventID(), false, false);
                } else if (outputMap.isEmpty()) {
                    mapLocations
                            = locationSource.getNoDataLocFormDefaultOrNon(getSiteID(), getAppID(),
                            getEventID() + "");
                } else {
                    mapLocations = locationSource.getNoOrHasDataLocFormDefNonWithAttr(getSiteID(),
                            getAppID(), getEventID(), outputMap, true);
                }
            } else {
                Log.i(TAG, "ALL DATA TAB");
                Log.i(TAG, "ALL DATA TAB app id " + getAppID());

                if (isSiteTypeDefault) {
                    tempLocations = locationSource.getDataForEventLocationV15(getSiteID(), getAppID(),
                            getEventID(), false, true);
                } else if (outputMap.isEmpty()) {
                    mapLocations
                            = locationSource.getAllDataLocFormDefaultOrNon(getSiteID(), getAppID());
                } else {
                    mapLocations = locationSource.getAllDataLocFormDefOrNonWithAttr(getSiteID(), getAppID(), outputMap);
                }

                handlerForUI.post(() -> {
                    if (tempLocations == null || tempLocations.size() < 1) {
                        emptylist_view.setText(R.string.no_fieldpoints_alert);
                    } else {
                        emptylist_view.setText(R.string.no_field_points);
                    }
                });
            }

/*            if (!mapLocations.isEmpty()) {
                tempLocations = new ArrayList<>();
                for (ArrayList<Location> loc : mapLocations.values()) {
                    tempLocations.addAll(loc);
                }
            }*/

            handlerForUI.post(() -> {
//                    setLocationAdapter();
                setLocationsAdapter();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (newLocationsAdapter != null && qrText != null) {
                            newLocationsAdapter.getFilter().filter(qrText);
                            newLocationsAdapter.notifyDataSetChanged();
                        }
                    }
                }, 200);
                pbLocations.setVisibility(View.GONE);
            });
        });

        Log.i(TAG, "populateLocation() OUT time:" + System.currentTimeMillis());
    }

    private boolean hasDuplicate(List<Location> tempLocations) {

        List<Location> noRepeat = new ArrayList<Location>();

        boolean isFound = false;
        for (Location event : tempLocations) {
            // check if the event name exists in noRepeat
            for (Location e : noRepeat) {
                if (e.getLocationName().equals(event.getLocationName()) || (e.equals(event))) {

                    Log.e("commonLocation", "populateLocation: " + event.getLocationName());
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);  //if need to remove duplicate add here.
            //tempLocations = noRepeat;
        }
        return isFound;
    }

    private List<Location> hasDuplicateLocations(List<Location> tempLocations) {

        List<Location> noRepeat = new ArrayList<Location>();
        List<Location> commonLocation = new ArrayList<Location>();

        boolean isFound = false;
        for (Location event : tempLocations) {
            // check if the event name exists in noRepeat
            for (Location e : noRepeat) {
                if (e.getLocationName().equals(event.getLocationName()) || (e.equals(event))) {
                    Log.e("commonLocation", "populateLocation: " + event.getLocationName());
                    commonLocation.add(event);
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);  //if need to remove duplicate add here.
            //tempLocations = noRepeat;
        }
        boolean hasDuplicateLocation = hasDuplicate(commonLocation);
        if (hasDuplicateLocation) {
            commonLocation = hasDuplicateLocations(commonLocation);
        } else {
            return commonLocation;
        }
        return commonLocation;
    }

    void setLocationsAdapter() {

        if (!mapLocations.isEmpty()) {

/*            SectionalLocationsAdapter adapter = new SectionalLocationsAdapter(this,
                    mapLocations, getAppID(), getEventID(), this);
            rvLocations.setAdapter(adapter);*/

            tempLocations = new ArrayList<>();
            if (mapLocations.containsKey(GlobalStrings.NON_FORM_DEFAULT)) {
                try {
                    ArrayList<Location> listNoFormDefault = mapLocations.get(GlobalStrings.NON_FORM_DEFAULT);
                    if (listNoFormDefault != null && listNoFormDefault.size() > 0)
                        tempLocations.addAll(listNoFormDefault);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (tempLocations != null && tempLocations.size() > 0) {

            Location lastLocation = Util.getSharedPrefLocJsonProperty(objContext,
                    getEventID() + "", Location.class);
            Location lastLoc = null;

            if (mSortSelection.equals("null")) {
                Collections.sort(tempLocations, new CustomComparator());
            }

            if (mSortSelection.equals("Ascending")) {
                Collections.sort(tempLocations, new Comparator<Location>() {
                    @Override
                    public int compare(Location lhs, Location rhs) {
                        mSortSelection = "Ascending";
                        return lhs.getLocationName().compareToIgnoreCase(rhs.getLocationName());
                    }
                });
            }

            if (mSortSelection.equals("Descending")) {
                mSortSelection = "Descending";
                Collections.sort(tempLocations, new Comparator<Location>() {
                    @Override
                    public int compare(Location lhs, Location rhs) {
                        return rhs.getLocationName().compareToIgnoreCase(lhs.getLocationName());
                    }
                });
            }

            ArrayList<Location> traverseLocations = new ArrayList<>(tempLocations);

            if (lastLocation != null)
                for (Location locitem : traverseLocations) {
                    if (lastLocation.getLocationID() != null && !lastLocation.getLocationID().isEmpty() &&
                            lastLocation.getLocationID().equals(locitem.getLocationID())) {
                        tempLocations.remove(locitem);
                        lastLoc = locitem;
                        break;
                    }
                }

            if (lastLoc != null) {
                tempLocations.add(0, lastLoc);
            }

            //all this hassle is to keep header locations at top and recent below it
            ArrayList<Location> listLocations = new ArrayList<>();
            if (mapLocations.containsKey(GlobalStrings.FORM_DEFAULT)) {
                try {
                    ArrayList<Location> listFormDef = mapLocations.get(GlobalStrings.FORM_DEFAULT);
                    if (listFormDef != null && listFormDef.size() > 0) {
                        listLocations.add(new Location("-1"));//header section
                        listLocations.addAll(listFormDef);
                        listLocations.add(new Location("-1"));//header section
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            listLocations.addAll(tempLocations);
            tempLocations.clear();
            tempLocations.addAll(listLocations);

            if (isSiteTypeDemo) {
                layoutCloseEvent.setVisibility(View.GONE);
            }

            newLocationsAdapter = new LocationsAdapter(this,
                    tempLocations, getAppID() + "",
                    getEventID() + "", this);

            rvLocations.setAdapter(newLocationsAdapter);
/*            rvLocations.addItemDecoration(new DividerItemDecoration(this,
                    LinearLayoutManager.VERTICAL));*/
            rvLocations.setVisibility(View.VISIBLE);
            emptylist_view.setVisibility(View.GONE);
        } else {
            rvLocations.setVisibility(View.GONE);
            emptylist_view.setVisibility(View.VISIBLE);
        }
    }

    void setLocationAdapter() {

        if (tempLocations != null && tempLocations.size() > 0) {

            //21-Jul-17 SET LAST_VISITED EVENT LOCATION TO TOP OF LIST
            String lastvisited_locID = Util.getSharedPreferencesProperty(objContext, getEventID() + "");
            Location lastvisitedobj = null;
            cocLocations = new ArrayList<>();
            ArrayList<Location> traverseLocations = new ArrayList<>(tempLocations);

            Log.i(TAG, "setLocationAdapter() Sort Locations Start time:" + System.currentTimeMillis());
            if (mSortSelection.equals("null")) {
                Collections.sort(tempLocations, new CustomComparator());
            }

            if (mSortSelection.equals("Ascending")) {
                Collections.sort(tempLocations, new Comparator<Location>() {
                    @Override
                    public int compare(Location lhs, Location rhs) {
                        mSortSelection = "Ascending";
                        return lhs.getLocationName().compareToIgnoreCase(rhs.getLocationName());
                    }
                });
            }

            if (mSortSelection.equals("Descending")) {
                mSortSelection = "Descending";
                //Collections.reverse(tempLocations);
                Collections.sort(tempLocations, new Comparator<Location>() {
                    @Override
                    public int compare(Location lhs, Location rhs) {
                        //mSortSelection = "Ascending";
                        return rhs.getLocationName().compareToIgnoreCase(lhs.getLocationName());
                    }
                });
            }

            Log.i(TAG, "setLocationAdapter() Sort Locations End time:" + System.currentTimeMillis());

            for (Location locitem : traverseLocations) {
                if (lastvisited_locID != null && !lastvisited_locID.isEmpty() &&
                        lastvisited_locID.equals(locitem.getLocationID())) {
                    tempLocations.remove(locitem);
                    lastvisitedobj = locitem;
//                        break;
                } else if (locitem.getCocflag() != null && locitem.getCocflag().equals("1")) {
                    cocLocations.add(locitem);//Seperate out COC Loactions from temp list
                    tempLocations.remove(locitem);
                }
            }

            if (cocLocations != null & cocLocations.size() > 0) {
                //  Collections.sort(cocLocations, new CustomComparator());
                for (int i = (cocLocations.size() - 1); i > -1; i--) {
                    //put last item first from CoC list and push original arraylist to down
                    tempLocations.add(0, cocLocations.get(i));
                }
            }

            if (lastvisitedobj != null) {
                tempLocations.add(0, lastvisitedobj);
            }

            locationAdapter = new LocationAdapter(this, R.layout.adapter_location,
                    R.id.tvLocationName, tempLocations, getAppID() + "",
                    getEventID() + "", this);
            listView.setVisibility(View.VISIBLE);
            emptylist_view.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            emptylist_view.setVisibility(View.VISIBLE);
        }

        if (listView != null) {

            listView.setAdapter(locationAdapter);
            end = System.currentTimeMillis();
            Log.e(TAG, "Locations populated in " + (start - end) + "ms");
            //locationAdapter.notifyDataSetChanged();
            listView.setItemsCanFocus(true);

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    locationObj = selectedLocatonObj;//tempLocations.get(lastSelectedPos);
                    locationObj = (Location) parent.getAdapter().getItem(position);

                    onLocationItemClickAction();
                }
            });
        }
    }

    private void onLocationItemClickAction() {
        locationID = locationObj.getLocationID();
        locationName = locationObj.getLocationName();

        String isCOCLocation;
        if (locationObj.getCocflag() == null || locationObj.getCocflag().isEmpty()) {
            isCOCLocation = "0";
        } else {
            isCOCLocation = locationObj.getCocflag();
        }

        Util.setSharedPreferencesProperty(objContext, locationID, isCOCLocation);

        boolean gpsPermissionStatus = checkLocationPermission();
        if (!gpsPermissionStatus)
            Toast.makeText(objContext, getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT).show();

        Log.i(TAG, " User Selected Location:" + locationObj.getLocationName()
                + " , LocationID:" + locationID + "\n\n");

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }

        onclickLocationItem(locationID,
                locationName,
                locationObj.getLocationDesc(), locationObj.getFormDefault());
    }

    public void onclickLocationItem(String locID, String locName, String locDesc, int formDefault) {

        if (getEventID() < 0) {
            setEventID(new EventDataSource(objContext).getServerEventID(getEventID() + ""));
        }

        Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_APPNAME, dispappName);

        Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_LOCATIONID, locID);
        Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_LOCATIONNAME, locName);
        Util.setSharedPreferencesProperty(objContext, GlobalStrings.SESSION_USERID, getUserID() + "");
        Util.setSharedPreferencesProperty(objContext, GlobalStrings.SESSION_DEVICEID, getDeviceID());
        Util.setSharedPreferencesProperty(objContext, getEventID() + "", locID);//setting location id for last visited location
        Util.setSharedPreferencesProperty(objContext, getEventID() + "", new Gson().toJson(locationObj));//saving location for last visited location

        Log.i(TAG, "onclickLocationItem() Set Session Location ID-" + locID + "" +
                " And Location Name-" + locName + " And UserID-" + getUserID() + " And DeviceID-" + getDeviceID());

        String appType = mobileAppSource.getAppType(getAppID());

        Log.i(TAG, "onclickLocationItem() App_Type:" + appType);

        FieldDataSource fieldData = new FieldDataSource(objContext);

        childAppList = mobileAppSource.getChildApps(getAppID(), getSiteID(), locationID);

        int maxApps = childAppList.size();

        if (maxApps == 0) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_forms_for_this_location), Toast.LENGTH_SHORT).show();
            return;
        }

        if (appType == null) {
            appType = "std_app";
        }

        if (appType.equalsIgnoreCase("well_log")) {
            MobileApp app = new MobileApp();
            int id = 0;
            for (int i = 0; i < childAppList.size(); i++) {
                app = childAppList.get(i);
                String appForm = app.getExtField4();
                Log.i(TAG, "onclickLocationItem() App Form:" + appForm);
                if (appForm == null) {
                    appForm = "Detail";
                }
                if (appForm.equalsIgnoreCase("header")) {

                    id = app.getAppID();
                    Log.i(TAG, "onclickLocationItem() App Form:" + appForm + " , MobileAppID:" + id);

                    break;
                }
            }
            Log.i(TAG, "onclickLocationItem() check Header Data for Location:" + locID + " , UserID:" + getUserID() + " " +
                    ", EventID:" + getEventID() + " , MobileAppID:" + id + " , SiteID:" + getSiteID());

            boolean exists = fieldData.isExistsHeaderData(locID, getEventID(),
                    getUserID(), id, getSiteID());

        } else {

            Log.i(TAG, "Location ID Selected:" + locID);
            Intent locationDetailIntent = new Intent(objContext,
                    LocationDetailActivity.class);

            locationDetailIntent.putExtra("EVENT_ID", getEventID());
            locationDetailIntent.putExtra("LOCATION_ID", locID);
            locationDetailIntent.putExtra("APP_ID", getAppID());
            locationDetailIntent.putExtra("SITE_ID", getSiteID());
            locationDetailIntent.putExtra("SITE_NAME", getSiteName());
            locationDetailIntent.putExtra("APP_NAME", dispappName);
            locationDetailIntent.putExtra("COC_ID", curr_cocid);

            locationDetailIntent.putExtra("LOCATION_NAME", locName);
            locationDetailIntent.putExtra("LOCATION_DESC", locDesc == null ? "" : locDesc);
            locationDetailIntent.putExtra(GlobalStrings.FORM_DEFAULT, formDefault);

            Log.i(TAG, "onclickLocationItem() :\n Arguments to Location Detail Activity-\n" +
                    " EventID=" + getEventID() + "\n LocationID:" + locID + "\n AppID:" + getAppID()
                    + "\n SiteID:" + getSiteID() + "\n SiteName:" + getSiteName() + "\n App Name:" + dispappName +
                    " \n Location Name:" + locName + "\n Location Desc:" + locDesc + "\n\n");

            try {
                startActivity(locationDetailIntent);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);

                if (searchView.isSearchOpen()) {
                    searchView.closeSearch();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onclickLocationItem( )Error in Redirecting to Details Form:" + e.getMessage());
                Toast.makeText(objContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void onclickTagLocation(String locID, String lat, String longi, String locationName) {
        Intent i = new Intent(objContext, MapActivity.class);
        i.putExtra("LOC_ID", locID);
        i.putExtra("SITE_NAME", getSiteName());
        i.putExtra("LOCATION_NAME", locationName);
        i.putExtra("EVENT_ID", getEventID());
        i.putExtra("APP_ID", getAppID());
        i.putExtra("PREV_CONTEXT", "Location");
        i.putExtra("OPERATION", GlobalStrings.TAG_LOCATION);
        i.putExtra("LATITUDE", lat);
        i.putExtra("LONGITUDE", longi);
        startActivity(i);

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }

        Log.i(TAG, "onclickTagLocation() :\n Arguments to Map Activity-\n" +
                " EventID=" + getEventID() + "\n LocationID:" + locID + "\n AppID:" + getAppID()
                + "\n SiteID:" + getSiteID() + "\n LocationName:" + locationName + "\n Operation:" + 1 + "\n Latitude:" + lat + "\n Longitude:" + longi);

        // finish();
    }

    public void onClick(View view) {
        // Intent LocationDtailIntent = new Intent ( this,
        // LocationDetailActivity.class);
        // startActivity(LocationDtailIntent);
    }

    public void handleInsurance(View v) {
        Intent repot = new Intent(objContext, ReportView.class);
        // intent.putExtra("USER_NAME", username);
        repot.putExtra("PARENT_APP_ID", getAppID());
        repot.putExtra("CURRENT_APP_ID", 0);// akshatha
        repot.putExtra("SITE_ID", getSiteID());
        repot.putExtra("SITE_NAME", getSiteName());
        repot.putExtra("USER_NAME", getUsername());
        //reportview needs location id also to sort by location to view report
        startActivity(repot);
    }

/*
    public void handleMap(View v) {
        Intent intent = new Intent(objContext, MapActivity.class);
        startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
    }
*/

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actions, menu);
        menu.findItem(R.id.user).setTitle(getString(R.string.Salutation) + " " + getUsername() + "!");

        AppPreferenceDataSource ds = new AppPreferenceDataSource(objContext);
        //KEY_PROJECT_FILE
        menu.findItem(R.id.filefolder)
                .setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_PROJECT_FILE, getUserID()));

        //KEY_EMERGENCY
        menu.findItem(R.id.nearby)
                .setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_EMERGENCY, getUserID()));
        // KEY_PERCENTAGE
//        if (ds.isFeatureAvailable(GlobalStrings.KEY_PERCENTAGE, getUserID())) {
//            menu.findItem(R.id.locPer).setVisible(true);
//        } else {
//            menu.findItem(R.id.locPer).setVisible(false);
//
//        }
        // KEY_DOWNLOAD_DATA
        menu.findItem(R.id.download_your_owndata)
                .setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_DOWNLOAD_DATA, getUserID()));

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        menuItemSync = menu.findItem(R.id.download_forms);
        setSyncBadge();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //super.onOptionsItemSelected(item);

        final LocationActivity context = LocationActivity.this;
        String title = getString(R.string.erase_data);
        String msg = getString(R.string.are_you_sure_you_want_to_erase_all_the_data_from_this_device);
        String pos = getString(R.string.yes);
        String neg = getString(R.string.no);
        AlertDialog alert;

        Log.i(TAG, "Item Selected:" + item.getTitle());
        switch (item.getItemId()) {

            case R.id.nearby:

                //hospital
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

            case R.id.app_preferences:
                Intent pref_intent = new Intent(context, AppPreferencesActivity.class);
                startActivity(pref_intent);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;

            case R.id.workorder_mytask:
                Intent tskintent = new Intent(context, TaskDetailActivity.class);
                tskintent.putExtra("SITE_ID", getSiteID());
                tskintent.putExtra("PARENTAPPID", getAppID());
                startActivity(tskintent);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;

            case android.R.id.home:
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();
                return true;

            case R.id.filefolder:

                List<FileFolderItem> list = new FileFolderDataSource(context)
                        .getHomeFileFolderItemList(getSiteID() + "");

                if (list.size() < 1) {
                    startActivity(new Intent(context, FileFolderSyncActivity.class));
                } else {
                    startActivity(new Intent(context, FileFolderMainActivity.class));
                }
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;

            case R.id.download_your_owndata:

                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(objContext);

                alertDialogBuilder1.setTitle(getString(R.string.downloadYourData));
                alertDialogBuilder1.setMessage(getString(R.string.do_you_want_to_dwld_view_report));
                // set positive button: Yes message
                alertDialogBuilder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to a new activity of the app
                        if (CheckNetwork.isInternetAvailable(objContext)) {
                            Intent downloadintent = new Intent(objContext, DownloadYourOwnDataActivity.class);
                            downloadintent.putExtra("SITEID", getSiteID());
                            downloadintent.putExtra("EVENTID", getEventID());
                            downloadintent.putExtra("PARENTAPPID", getAppID());
                            downloadintent.putExtra("SITEID", siteID);
                            startActivity(downloadintent);
//                            overridePendingTransition(R.anim.right_to_left,
//                                    R.anim.left_to_right);
                        } else {
                            Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                // set negative button: No message
                alertDialogBuilder1.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog1 = alertDialogBuilder1.create();
                // show alert
                alertDialog1.show();

                return true;

            case R.id.update_apk:
                //21-Mar-16  Link to Update app from Play Store
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.aqua.fieldbuddy"));
                startActivity(intent);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;

            case R.id.download_forms:
                downloadForms();
                //SyncData();
                return true;

            case R.id.action_sync:
                return true;

            default:
                return false;
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
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
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

        if (isRefreshCalledFromTabScreen) {
            isRefreshCalledFromTabScreen = false;
            finish();
        }

        HashMap<String, String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("AttributeHashMap", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k, v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // CheckPermission();
        location_attribute_hdr_tv.setText("");
        String header_text = getString(R.string.list_is_filtered_by_attribute);
        location_attribute_hdr_tv.setText(header_text);
        if (outputMap.isEmpty()) {
            location_attribute_hdr_tv.setVisibility(View.GONE);
        } else {

            Map<String, String> map = new TreeMap<String, String>(outputMap);
            Set set2 = map.entrySet();
            Iterator iterator2 = set2.iterator();
            while (iterator2.hasNext()) {

                Map.Entry entry = (Map.Entry) iterator2.next();
                String attrName = String.valueOf(entry.getKey());
                String attrValue = String.valueOf(entry.getValue());
                String Name, Value;
                StringTokenizer st = new StringTokenizer(attrName, "|");
                Name = st.nextToken();
                Value = st.nextToken();

                if (iterator2.hasNext()) {
                    location_attribute_hdr_tv.append(Name.toString() + "-" + Value + ", ");
                } else {
                    location_attribute_hdr_tv.append(Name.toString() + "-" + Value);
                }
                location_attribute_hdr_tv.setVisibility(View.VISIBLE);
            }
        }

        //01-Oct-15 Set LocFormStatus Stable
        setSiteID(Integer.parseInt(Util.getSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_SITEID)));

        isSiteTypeDemo = siteDataSource.isSiteTypeDemo(getSiteID());

        if (isSiteTypeDemo) {
            layoutCloseEvent.setVisibility(View.INVISIBLE);
            menuMultipleActions.setVisibility(View.INVISIBLE);
        }

        String guid = Util.getSharedPreferencesProperty(objContext, getUsername());
        // searchLoc.setText(null);
        if (guid == null || guid.isEmpty()) {
            Util.reLogin(LocationActivity.this, getUsername(), getPassword());
            ApplicationActivity.applicationActivity.finish();
            SiteActivity.siteActivity.finish();
            finish();
            Log.i(TAG, "onResume() re login End time:" + System.currentTimeMillis());

        } else {
            //21-Feb-17 Make it as a feature
            FieldDataSource obj = new FieldDataSource(objContext);
            obj.truncateLocFormStatus();
            obj.insertChildAppStatus(getEventID());
            populateLocation();
        }

        boolean isSwitched = Util.getSharedPrefBoolProperty(this, GlobalStrings.IS_LOCATION_SWITCHED);

        if (isSwitched) {
            appID = Util.getSharedPrefIntProperty(this, GlobalStrings.SWITCHED_APPID);
            siteID = Util.getSharedPrefIntProperty(this, GlobalStrings.SWITCHED_SITE_ID);
            siteName = Util.getSharedPreferencesProperty(this, GlobalStrings.SWITCHED_SITENAME);
            eventID = Util.getSharedPrefIntProperty(this, GlobalStrings.SWITCHED_EVENT_ID);

            SharedPreferences.Editor editor = getSharedPreferences("Event_AppId", MODE_PRIVATE).edit();
            editor.clear();

            editor.putInt("EventId", eventID);
            editor.putInt("AppId", appID);
            editor.apply();
            editor.commit();

            dispappName = new SiteMobileAppDataSource(objContext)
                    .getMobileAppDisplayNameRollIntoApp(getAppID(), getSiteID());
            populateLocation();

            //resetting value again
            Util.setSharedPreferencesProperty(this, GlobalStrings.IS_LOCATION_SWITCHED, false);
        }

        setSyncBadge();
        setPrintLabelCOCBtns();
    }

    private void setSyncBadge() {
        if (menuItemSync != null) {
            Util.setBadgeCount(this, menuItemSync, "",
                    Util.isThereAnyDataToSync(this));
        }
    }

    private void downloadForms() {
        countMediaSync = 0;
//		   SharedPref.putBoolean("RETRACE", true);
/*        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(objContext);
        alertDialogBuilder.setTitle(getString(R.string.changes_to_forms));
        alertDialogBuilder.setMessage(getString(R.string.download_latest_forms));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callMetaSync();
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();*/

        if (!CheckNetwork.isInternetAvailable(LocationActivity.this)) {
            Toast.makeText(this,
                    getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            return;
        }

        EventDataSource eventDbSource = new EventDataSource(LocationActivity.this);
        ArrayList<DEvent> eventList = eventDbSource
                .getClientGeneratedEventIDs(LocationActivity.this);

        if (eventList.size() > 0) {
            new EventIDGeneratorTask(LocationActivity.this, null,
                    username, password, true, LocationActivity.this).execute();
            isRefreshCalled = true;
        } else {
            //checking if any field data to upload then call download forms and later events will
            //be fetched as we'll be clearing tables to let submittals fragment know that it
            //should download events
            //then sync tasks
            uploadFieldData();
        }
    }

    private void callMetaSync() {
        if (CheckNetwork.isInternetAvailable(objContext)) {
            Intent metaIntent = new Intent(objContext, MetaSyncActivity.class);
            startActivity(metaIntent);
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
            finish();
        } else {
            Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }
    }

    private void downloadAllEvents() {
        if (CheckNetwork.isInternetAvailable(this)) {
            new DownloadEventListTask(this, this).execute();
        } else {
            Toast.makeText(this, getString(R.string.bad_internet_connectivity),
                    Toast.LENGTH_SHORT).show();
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
            downloadAllEvents();
            return;
        }

        isRefreshCalled = true;

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
        showProgressDialog(getString(R.string.syncing_tasks_please_wait));
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
                    dismissProgressDialog();
                    downloadAllEvents();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                DeviceInfoModel ob = DeviceInfo.getDeviceInfo(LocationActivity.this);
                String deviceToken = Util.getSharedPreferencesProperty(LocationActivity.this,
                        GlobalStrings.NOTIFICATION_REGISTRATION_ID);
                String uID = Util.getSharedPreferencesProperty(LocationActivity.this,
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
            File imagePath = new File(attachment.getFileName());

            //note that we have added image path in fileName and server need only fileName not path
            attachment.setFileName(imagePath.getName());

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new Gson().toJson(attachment));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SyncMedia(this, jsonObject, imagePath.getAbsolutePath(), list.size()).execute();
        }
    }

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
                Toast.makeText(mContext, getString(R.string.tasks_synced_successfully), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
                downloadAllEvents();
            }
        }
    }

    public void handlerLeftNavigation(View v) {

    }

    String toastMsg;
    private String savedFilePath;

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

    /**
     * @return the eventID
     */
    int getEventID() {
        return eventID;
    }

    /**
     * @param eventID the eventID to set
     */
    void setEventID(int eventID) {
        LocationActivity.eventID = eventID;
    }

    /**
     * @return the locID
     */
    String getLocID() {
        return locID;
    }

    /**
     * @param locID the locID to set
     */
    void setLocID(String locID) {
        this.locID = locID;
    }

    /**
     * @param appID the appID to set
     */
    void setAppID(int appID) {
        this.appID = appID;
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

    private boolean isTouchInsideView(final MotionEvent ev,
                                      final View currentFocus) {
        final int[] loc = new int[2];
        currentFocus.getLocationOnScreen(loc);
        return ev.getRawX() > loc[0] && ev.getRawY() > loc[1]
                && ev.getRawX() < (loc[0] + currentFocus.getWidth())
                && ev.getRawY() < (loc[1] + currentFocus.getHeight());
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public void onTaskCompleted(Object obj) {

        FieldDataSource fieldData = new FieldDataSource(objContext);
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(objContext);
        EventDataSource eventData = new EventDataSource(objContext);

        if (obj != null) {
            if (obj instanceof String) {
                String result = (String) obj;
                if (result.equals("SUCCESS")) {
                    Event event = eventData.getEvent(getAppID(), getSiteID(), getUserID(), getDeviceID());
                    int currentEventID = event.getEventId();
                    setEventID(currentEventID);
                    uploadFieldData();
                } else {
                    Toast.makeText(objContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
                }
            } else if (obj instanceof EventResponseModel) {
                EventResponseModel result = (EventResponseModel) obj;
                GlobalStrings.responseMessage = result.getMessage();

                if (result.isSuccess()) {
                    serverGenEventID = result.getData().getEventId();
                    setGeneratedEventID(result);
                    Log.i(TAG, "Event ID From Server:" + serverGenEventID);

                    fieldData.updateEventID(getEventID(), serverGenEventID);
                    attachDataSrc.updateEventID(getEventID(), serverGenEventID);
                    eventData.updateEventID(getEventID(), result);
                    new SampleMapTagDataSource(objContext).updateEventID_SampleMapTag(getEventID() + "", serverGenEventID + "");

                    setEventID(serverGenEventID);

                    if (CheckNetwork.isInternetAvailable(objContext)) {
                        if (closeEvent) {
                            uploadFieldDataBeforeEndEvent();
                        } else {
                            uploadFieldData();
                        }
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
                }
            }
        } else {
            Toast.makeText(objContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setGeneratedEventID(int id) {
        if (id != 0) {
            serverGenEventID = id;
            Log.i(TAG, "setGeneratedEventID() serverEventID:" + id);
        }
    }

    @Override
    public void setGeneratedEventID(Object obj) {

        EventResponseModel res = (EventResponseModel) obj;
        int id = res.getData().getEventId();
        if (id != 0) {
            serverGenEventID = id;
            Log.i(TAG, "setGeneratedEventID() serverEventID:" + id);
        }
    }

    @Override
    public void onTaskCompleted() {
        //no use
    }


    public class CustomComparator implements Comparator<Location> {
        @Override
        public int compare(Location lhs, Location rhs) {
            return lhs.getLocationName().compareTo(rhs.getLocationName());
        }
    }

    private boolean unpackZip(String path, String zipname) {

        Log.i(TAG, "unpackZip() IN time:" + System.currentTimeMillis());
        Log.i(TAG, "unpackZip() File Path:" + path + ",ZipName=" + zipname);

        InputStream is;
        ZipInputStream zis;
        try {
            path = path + File.separator;

            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {

                filename = ze.getName();

                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in UN-Zipping File:" + e.getMessage());
            return false;
        }
        Log.i(TAG, "unpackZip() OUT time:" + System.currentTimeMillis());

        return true;
    }

    private void loadFileList(String Path) {


        File mPath = new File(Path);


        if (mPath.exists()) {

            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(FTYPE) || sel.isDirectory();
                }

            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[0];
        }

    }

    protected void show_Dialog(final String path) {
        Log.i(TAG, "showDialog() IN time:" + System.currentTimeMillis());

        FileFolderDataSource fd = new FileFolderDataSource(objContext);
        final ArrayList<FileFolderItem> list = fd.getKMZList(getSiteID() + "");
        final String[] fileList = new String[list.size()];

//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                objContext,
//                android.R.layout.select_dialog_singlechoice);

        for (int i = 0; i < list.size(); i++) {
            FileFolderItem item = list.get(i);
            fileList[i] = item.getItemTitle();
        }


        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(objContext);

        builder.setTitle(R.string.choose_file);
        if (list == null || list.size() < 1) {
            builder.setTitle(getString(R.string.no_kmz_file));

            builder.setMessage(getString(R.string.dont_see_any_kmzs_for_this_site));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (CheckNetwork.isInternetAvailable(objContext)) {
                        Intent intent1 = new Intent(objContext, FileFolderSyncActivity.class);
                        intent1.putExtra("RETURN_TO_LOADKMZ", true);
                        startActivityForResult(intent1, DOWNLOAD_KMZ_REQUEST_CODE);
                    } else {
                        Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.no), null);
        } else {

            builder.setItems(fileList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    Log.i(TAG, "showDialog() File Selected:" + mChosenFile);

                    mChosenFile = fileList[which];
                    //you can do stuff with the file here too
                    String f_guid = list.get(which).getItemGuid();
                    Log.i(TAG, "showDialog() Selected File GUID:" + f_guid);

                    if (path.isEmpty()) {
                        dialog.cancel();
                        return;
                    }

                    if (unpackZip(path, f_guid)) {

                        Intent i = new Intent(objContext, MapActivity.class);
                        i.putExtra("SITE_NAME", getSiteName());
                        i.putExtra("EVENT_ID", getEventID());
                        i.putExtra("APP_ID", getAppID());
                        i.putExtra("PREV_CONTEXT", "Location");
                        i.putExtra("OPERATION", GlobalStrings.LOAD_KMZ);
                        i.putExtra("KMZ_PATH", path);
                        startActivity(i);

                        Log.i(TAG, "unpackZip() Arguments to Map: " +
                                "SiteName=" + getSiteName() + ",EventID=" + getEventID() +
                                ",App ID=" + getAppID() + ",Context=Location,Operation=2,KMZ_PATH=" + path);

                        // startActivity(new Intent(objContext, KmlDemoActivity.class));
                    }
                }
            });
        }

        dialog = builder.create();
        dialog.show();

        Log.i(TAG, "showDialog() OUT time:" + System.currentTimeMillis());
    }
}
