package qnopy.com.qnopyandroid.ui.splitLocationAndMap;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.CAMERA_PERMISSION_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.DOWNLOAD_KMZ_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.MAP_ACTIVITY_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.NETWORK_PERMISSION_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.PHONE_STATE_PERMISSION_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.STORAGE_PERMISSION_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.SYNC_ACTIVITY_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.WIFI_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.adapter.LocationAdapter;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.clientmodel.Location;
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
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
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
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchAllReportByIdResponse;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchReportsById;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.ReportsAdapter;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.map.MapActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
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
import qnopy.com.qnopyandroid.ui.activity.RequiredFieldsListActivity;
import qnopy.com.qnopyandroid.ui.activity.SiteActivity;
import qnopy.com.qnopyandroid.ui.activity.TaskDetailActivity;
import qnopy.com.qnopyandroid.ui.calendarUser.CalendarFragment;
import qnopy.com.qnopyandroid.ui.calendarUser.DownloadEventListTask;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.locations.LocationAttributeActivity;
import qnopy.com.qnopyandroid.ui.locations.adapter.LocationsAdapter;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.adapter.SplitLocationAdapter;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.fragment.LocationFragment;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.fragment.MapFragment;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.uiutils.QRScannerActivity;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;

public class SplitLocationAndMapActivity extends ProgressDialogActivity
        implements SplitLocationAdapter.OnEraseLocationListener,
        OnTaskCompleted, DownloadEventListTask.OnEventDownloadListener,
        LocationsAdapter.OnLocationActionListener, CustomAlert.LocationServiceAlertListener {

    private Toolbar toolbar;
    private FieldDataSource fieldSource;
    private AquaBlueServiceImpl mAquaBlueService;
    private ActionBar actionBar;
    private TabLayout tabLayout;
    private int SELECTED_TAB = 0;
    private FloatingActionsMenu menuMultipleActions;
    FloatingActionButton syncDatabtn, submitEndbtn, newlocationbtn, mobileReportbtn,
            mobileReportRequired, sendReportToPM;
    private ListView listView;
    TextView emptylist_view, switch_map, location_attribute_hdr_tv;
    ImageView switch_map_iv, filter_iv, mSortLocationList, mGeoSearch;
    private MaterialSearchView searchView;
    private RelativeLayout mRelativeLayoutBottomSheet;
    public String siteName, username = null, password = null, SELECTED_ATTRIBUTE_VALUE = null, SELECTED_ATTRIBUTE_NAME = null;
    String deviceID;
    public static int eventID = 0;
    public int siteID = 0, appID = 0, userID = 0, setID = 0, companyID = 0;
    private String locID = "0"; // attachments for the site will have location as zero.

    private String dispappName;
    private String mSiteId;
    private ArrayList<ModelClassLocationsWithAttribute> arrayListLocationHavingAttribute;
    String mSortSelection = "null";
    public static List<Location> tempLocations = null;
    private MobileAppDataSource mobileAppSource;
    private LocationFragment locationFrag;
    public Location selectedLocationObj = new Location();
    private String locationID = null;
    private String locationName = null;
    private Location locationObj = null;
    private Context objContext = SplitLocationAndMapActivity.this;

    private List<MobileApp> childAppList;
    private int serverGenEventID = 0;
    private boolean closeEvent;
    private int TOTAL_FORM_FIELD_COUNT = 0;
    private MapFragment mapFragment;
    private float mTotalWidth; // pixels
    private float mPercentLeft = 50; // percent of screen
    private float mMinimumWidth = 100; // percent of screen
    private static final String EXTRA_CURRENT_INDEX = "MySplitPaneActivity.EXTRA_CURRENT_INDEX";
    private static final String EXTRA_PERCENT_LEFT = "MySplitPaneActivity.EXTRA_PERCENT_LEFT";
    private static final String EXTRA_MINIMUM_WIDTH_DIP = "MySplitPaneActivity.EXTRA_MINIMUM_WIDTH_DIP";
    private FrameLayout mLeftPane;
    private FrameLayout mRightPane;
    private boolean isRefreshCalled;
    public static boolean isRefreshCalledFromTabScreen;
    int countMediaSync = 0;
    private BottomSheetDialog mBottomSheetEmailLogs;
    private MenuItem menuItemSync;
    private SiteDataSource siteDataSource;
    private boolean isSiteTypeDefault;
    private boolean isFromCreateEventScreen;
    private HashMap<String, ArrayList<Location>> mapLocations = new HashMap<>();
    private BadELFGPSTracker badElf;
    public static ProgressBar pbLocations;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handlerForUI = new Handler(Looper.getMainLooper());
    private LocationsAdapter newLocationsAdapter;
    private RecyclerView rvLocations;
    private FusedLocationProviderClient fusedLocationClient;
    private android.location.Location lastLocation;

    private volatile CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private CustomBoldTextView tvEventDate;
    private CustomBoldTextView tvEventName;
    private CustomTextView tvProjectName;
    private RelativeLayout layoutCloseEvent;
    private ImageView ivEventOptions;
    private CustomTextView tvFormName;
    private CustomEditText edtSearchLocation;
    private ActivityResultLauncher<Intent> qrCodeLauncher;
    private String qrText;

    private boolean isSiteTypeDemo;
    private String savedFilePath;
    private BottomSheetDialog mBottomSheetReportsList;
    private FetchAllReportByIdResponse.Data reportDataToGenerate;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_location_and_map);

        toolbar = findViewById(R.id.location_toolbar);
        setSupportActionBar(toolbar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fieldSource = new FieldDataSource(this);

        SharedPref.globalContext = getApplicationContext();
        mAquaBlueService = new AquaBlueServiceImpl(this);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.select_field_point);
        }

        setUpEventCard();

        CheckPermission();

        siteDataSource = new SiteDataSource(this);

        processExtraData();
        setEssentialIds();

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

        setUpUi();

        locationFrag = new LocationFragment();
        loadFragment(locationFrag, R.id.content_split_left_pane);
        listView = locationFrag.listView;
        rvLocations = locationFrag.rvLocations;
        emptylist_view = locationFrag.emptylist_view;
        location_attribute_hdr_tv = locationFrag.location_attribute_hdr_tv;

        setTabLayout();
        setImageDividerPane();
        setClickListeners();

        LocationDataSource locationSource = new LocationDataSource(objContext);
        isSiteTypeDefault = siteDataSource.isSiteTypeDefault(siteID);

        ArrayList<Location> allEventLocations = locationSource.getDataForEventLocation(siteID, appID,
                eventID, false, true);

        if (isSiteTypeDefault && allEventLocations.isEmpty() && isFromCreateEventScreen) {
            Intent intent = new Intent(objContext, AddLocationActivity.class);
            intent.putExtra("MOBILEAPP_ID", appID);
            startActivityForResult(intent, LocationActivity.REQUEST_CODE_ADD_LOCATION);
        }
    }

    private void setUpEventCard() {
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventName = findViewById(R.id.tvEventName);
        tvFormName = findViewById(R.id.tvFormName);
        tvProjectName = findViewById(R.id.tvProjectName);
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

        ivEventOptions.setOnClickListener(view -> {

        });

        layoutCloseEvent.setOnClickListener(view -> {
            submitEndbtn.performClick();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImageDividerPane() {
        // get screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mTotalWidth = size.x;

        // convert minimum width from dip to percent
        mMinimumWidth = convertDipToPercent(100);

        // get left and right pane and set weights
        mLeftPane = (FrameLayout) findViewById(R.id.content_split_left_pane);
        mRightPane = (FrameLayout) findViewById(R.id.content_split_right_pane);
        RelativeLayout divider = findViewById(R.id.split_pane_divider);
        divider.setOnTouchListener(new DividerTouchListener());
    }

    private void CheckPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else if (!checkPermission(Manifest.permission.READ_PHONE_STATE)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_PHONE_STATE)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            PHONE_STATE_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_NETWORK_STATE)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                            NETWORK_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(Manifest.permission.ACCESS_WIFI_STATE)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_WIFI_STATE)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                            WIFI_PERMISSION_REQUEST_CODE);
                }
            } else if (!checkPermission(Manifest.permission.CAMERA)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        } else requestCurrentLocation();
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
                        GlobalStrings.CURRENT_GPS_LOCATION = lastLocation;
                        result = "Location (success): " +
                                lastLocation.getLatitude() +
                                ", " +
                                lastLocation.getLongitude();
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
                requestCurrentLocation();
                if (!checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION_REQUEST_CODE);
                    }
                }

            case PHONE_STATE_PERMISSION_REQUEST_CODE:

                if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                    }
                }

            case STORAGE_PERMISSION_REQUEST_CODE:
                if (!checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, NETWORK_PERMISSION_REQUEST_CODE);
                    }
                }

            case NETWORK_PERMISSION_REQUEST_CODE:

                if (!checkPermission(Manifest.permission.ACCESS_WIFI_STATE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, WIFI_PERMISSION_REQUEST_CODE);
                    }
                }

            case WIFI_PERMISSION_REQUEST_CODE:
                if (!checkPermission(Manifest.permission.CAMERA)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
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

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(objContext, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void setClickListeners() {

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

        deviceID = DeviceInfo.getDeviceID(objContext);
        Log.i(TAG, "onCreate() Current WIFI DeviceID:" + deviceID);

        switch_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean gpsPermissionStatus = checkWriteExternalPermission();
                if (gpsPermissionStatus) {
                    Intent i = new Intent(objContext, MapActivity.class);
                    i.putExtra("SITE_ID", siteID);
                    i.putExtra("SITE_NAME", siteName);
                    i.putExtra("EVENT_ID", eventID);
                    i.putExtra("APP_ID", appID);
                    i.putExtra("USER_ID", userID);
                    i.putExtra("PREV_CONTEXT", "Location");
                    i.putExtra("OPERATION", GlobalStrings.SHOW_TAGGED_LOCATION);
                    startActivity(i);
                    finish();

                    if (searchView.isSearchOpen()) {
                        searchView.closeSearch();
                    }
                    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);

                } else {
                    CheckPermission();
                }
            }
        });

        switch_map_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean gpsPermissionStatus = checkWriteExternalPermission();
                if (gpsPermissionStatus) {
                    Intent i = new Intent(objContext, MapActivity.class);
                    i.putExtra("SITE_ID", siteID);
                    i.putExtra("SITE_NAME", siteName);
                    i.putExtra("EVENT_ID", eventID);
                    i.putExtra("APP_ID", appID);
                    i.putExtra("USER_ID", userID);
                    i.putExtra("PREV_CONTEXT", "Location");
                    i.putExtra("OPERATION", GlobalStrings.SHOW_TAGGED_LOCATION);
                    startActivity(i);

                    finish();
                    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                } else {
                    CheckPermission();
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(objContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(objContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onCreate() Location access permission Granted.");
            return;
        }

        newlocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Add new Location Click time:" + System.currentTimeMillis());

                menuMultipleActions.collapse();
                Intent intent = new Intent(objContext, AddLocationActivity.class);
                intent.putExtra("MOBILEAPP_ID", appID);
                startActivityForResult(intent, LocationActivity.REQUEST_CODE_ADD_LOCATION);
            }
        });

/*        SiteDataSource siteData = new SiteDataSource(objContext);
        String siteMobileReportRequired = siteData.getSiteMobileReportRequiredStatus(siteID);

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
        }*/

        mobileReportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();

                Intent i = new Intent(objContext, MobileReportActivity.class);
                i.putExtra("SITE_NAME", siteName);
                i.putExtra("SITE_ID", siteID + "");
                i.putExtra("EVENT_ID", eventID + "");
                i.putExtra("APP_NAME", dispappName);
                startActivity(i);

            }
        });
        final String siteId = siteID + "";
        final String eventId = eventID + "";

        sendReportToPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailLogsBottomSheet();
            }
        });

        mobileReportRequired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                String data;

                String filePath = Util.getFileFolderDirPathForPDF(objContext,
                        String.valueOf(siteID), String.valueOf(eventID));

                if (filePath.isEmpty()) {
                    Toast.makeText(objContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                    androidx.appcompat.app.AlertDialog.Builder builder
                            = new androidx.appcompat
                            .app.AlertDialog.Builder(SplitLocationAndMapActivity.this);
                    builder.setTitle(getString(R.string.report_generate))
                            .setMessage(getString(R.string.click_view_to_download_new_report_or_view))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.view), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent
                                            = new Intent(SplitLocationAndMapActivity.this,
                                            MobileReportRequiredActivity.class);
                                    intent.putExtra("USER_ID", userID + "");
                                    intent.putExtra("FORM_ID", appID + "");
                                    intent.putExtra("SITE_ID", siteID + "");
                                    intent.putExtra("EVENT_ID", eventID + "");
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

        submitEndbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                requestCurrentLocation();
                Log.i(TAG, "Submit and End onClick() Start time:" + System.currentTimeMillis());

                if (CheckNetwork.isInternetAvailable(objContext)) {
                    FieldDataSource fieldDataSource = new FieldDataSource(objContext);

                    ArrayList<RequiredFieldRowItem> reqDataList
                            = fieldDataSource.getMandatoryFieldList(appID + "",
                            eventID + "", siteID + "");

                    if (CalendarFragment.hasRequiredLocationsFields(siteID, eventID,
                            appID, SplitLocationAndMapActivity.this)) {
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

        TOTAL_FORM_FIELD_COUNT = new MetaDataSource(objContext)
                .getRoll_Into_Form_Fields_Count(siteID + "", appID + "");
    }

    private void setEssentialIds() {
        siteID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext,
                GlobalStrings.CURRENT_SITEID));
        username = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD);
        companyID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext,
                GlobalStrings.COMPANYID));
        siteName = Util.getSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_SITENAME);

        Log.e("abc", "onCreate: " + siteID);
        username = Util.getSharedPreferencesProperty(objContext, GlobalStrings.USERNAME);

        try {
            userID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext,
                    GlobalStrings.USERID));
            Log.i(TAG, "Session UserID:" + userID);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in parsing Shared preferences for userID:" + e.getMessage());

            UserDataSource userData = new UserDataSource(objContext);
            User newUser = userData.getUser(username);
            if (newUser != null) {
                userID = newUser.getUserID();
                Log.i(TAG, "Current user by username=" + username
                        + " from DeviceDB is UserID:" + userID);
            }
        }
    }

    private void setTabLayout() {
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("ALL"));
        tabLayout.addTab(tabLayout.newTab().setText("HAS DATA"));
        tabLayout.addTab(tabLayout.newTab().setText("NO DATA"));
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

    private void getReport(boolean isForPM, boolean isPdf, boolean isForSelf) {
        //even if isPdf has value, use of it depends on the isForPM value in api call

        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
        }

        if (CheckNetwork.isInternetAvailable(objContext)) {
            new ReportAsyncTask(isForPM, isPdf, isForSelf).execute();
        } else {
            Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
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

            LinearLayout llSendPdf = sheetView.findViewById(R.id.llSendPdf);
            LinearLayout llSendDoc = sheetView.findViewById(R.id.llSendDoc);
            LinearLayout llCancel = sheetView.findViewById(R.id.llCancel);

            llSendPdf.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                fetchReportsByIdNames(true);
            });

            llSendDoc.setOnClickListener(v -> {
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
        FetchReportsById fetchReportsById = new FetchReportsById(this, appID + "",
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

    private void processExtraData() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            SharedPreferences.Editor editor = getSharedPreferences("Event_AppId", MODE_PRIVATE).edit();
            editor.clear();

            editor.putInt("EventId", extras.getInt("EVENT_ID"));
            editor.putInt("AppId", extras.getInt("APP_ID"));
            editor.apply();
            editor.commit();

            eventID = extras.getInt("EVENT_ID");
            appID = extras.getInt("APP_ID");

            Log.e("Split Loc AppId", "onCreate: " + appID);
            if (extras.containsKey("Action_Sync")) {
                // syncAll=true;
                SyncData();
            }

            if (extras.containsKey("SITE_NAME")) {
                siteName = extras.getString("SITE_NAME");
            }

            isFromCreateEventScreen = extras.getBoolean(GlobalStrings.IS_FROM_CREATE_EVENT_SCREEN);
            setEventDetails();
        }
    }

    private void setEventDetails() {
        EventData event = new EventDataSource(this)
                .getEvent(eventID + "", appID + "");

        if (event != null) {
            tvEventDate.setText(Util.getFormattedDateFromMilliS(event.getStartDate(),
                    " dd \nMMM"));
            tvEventName.setText(event.getEventName());
            tvFormName.setText(event.getSiteName());
            tvProjectName.setText(event.getMobAppName());
        }
    }

    private void setUpUi() {
        menuMultipleActions = findViewById(R.id.multiple_actions);
        syncDatabtn = findViewById(R.id.action_upload);
        newlocationbtn = findViewById(R.id.action_addnew);
        submitEndbtn = findViewById(R.id.action_closeevent);
        pbLocations = findViewById(R.id.pbLocations);
        edtSearchLocation = findViewById(R.id.edtSearchLocation);
        setUpSearchView();

        if (ScreenReso.isLimitedUser)
            submitEndbtn.setVisibility(View.GONE);

        submitEndbtn.setVisibility(View.GONE);

        mobileReportbtn = findViewById(R.id.action_mobilereport);
        mobileReportRequired = findViewById(R.id.action_mobileReportRequired);
        sendReportToPM = findViewById(R.id.fabSendReportToPM);
        switch_map = findViewById(R.id.switch_map_tv);

        switch_map_iv = findViewById(R.id.list_mapview_iv);
        filter_iv = findViewById(R.id.filter_ib);
        searchView = findViewById(R.id.search_view);

        mRelativeLayoutBottomSheet = findViewById(R.id.relativeLayoutBottomSheet);
        mSortLocationList = findViewById(R.id.sortLocationList);
        mGeoSearch = findViewById(R.id.geoSearch);

        dispappName = new SiteMobileAppDataSource(this)
                .getMobileAppDisplayNameRollIntoApp(appID, siteID);

        mSiteId = Util.getSharedPreferencesProperty(this, GlobalStrings.CURRENT_SITEID);
        LocationAttributeDataSource locationAttributeDataSource
                = new LocationAttributeDataSource(this);
        arrayListLocationHavingAttribute = locationAttributeDataSource
                .getAllLocationWithAttribute(mSiteId);

        filter_iv.setVisibility(View.GONE);
        if (arrayListLocationHavingAttribute.size() > 0) {
            filter_iv.setVisibility(View.VISIBLE);
        } else {
            filter_iv.setVisibility(View.GONE);
            SharedPreferences settings = getSharedPreferences("MULTIPLEATTRIBUTE",
                    Context.MODE_PRIVATE);
            settings.edit().clear().commit();
        }

        filter_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplitLocationAndMapActivity.this,
                        LocationAttributeActivity.class));
            }
        });

        mSortLocationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog
                        = new BottomSheetDialog(SplitLocationAndMapActivity.this);
                bottomSheetDialog.setContentView(R.layout.location_sort);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                Button btnAscending = bottomSheetDialog.findViewById(R.id.buttonAscending);
                Button btnDescending = bottomSheetDialog.findViewById(R.id.buttonDescending);
                Button btnCancel = bottomSheetDialog.findViewById(R.id.buttonCancel);

                btnAscending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSortSelection = "Ascending";
                        bottomSheetDialog.dismiss();
                        setLocationsAdapter();
                    }
                });

                btnDescending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSortSelection = "Descending";
                        bottomSheetDialog.dismiss();
                        setLocationsAdapter();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });

        mGeoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGeoSearch = new Intent(SplitLocationAndMapActivity.this,
                        GeoSearchMapsActivity.class);
                startActivity(intentGeoSearch);
            }
        });

        if (dispappName == null) {
            dispappName = new SiteMobileAppDataSource(this)
                    .getMobileAppDisplayNameRollIntoAppForSite(appID);
        }

        Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_APPNAME, dispappName);

        String APP_TYPE = Util.getSharedPreferencesProperty(this, GlobalStrings.APP_TYPE);

        AppPreferenceDataSource ds = new AppPreferenceDataSource(this);

        //CHECK IS ADD NEW LOCATION FEATURE AVAILABLE OR NOT
        //KEY_ADD_LOCATION
        if (ds.isFeatureAvailable(GlobalStrings.KEY_ADD_LOCATION, userID)) {
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

    public void populateLocation() {
        executor.execute(() -> {

            handlerForUI.post(() -> pbLocations.setVisibility(View.VISIBLE));

            mapLocations.clear();
            if (tempLocations != null)
                tempLocations.clear();

            LocationDataSource locationSource = new LocationDataSource(SplitLocationAndMapActivity.this);
            mobileAppSource = new MobileAppDataSource(SplitLocationAndMapActivity.this);

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
                    tempLocations = locationSource.getDataForEventLocationV15(siteID, appID,
                            eventID, true, false);
                } else if (outputMap.isEmpty()) {
                    mapLocations
                            = locationSource.getHasDataLocFormDefaultOrNon(siteID, appID,
                            eventID + "");
                } else {
                    mapLocations = locationSource.getNoOrHasDataLocFormDefNonWithAttr(siteID, appID,
                            eventID, outputMap, false);
                }
            } else if (SELECTED_TAB == 2) {
                Log.i(TAG, "NO DATA TAB");
                if (isSiteTypeDefault) {
                    tempLocations = locationSource.getDataForEventLocationV15(siteID, appID,
                            eventID, false, false);
                } else if (outputMap.isEmpty()) {
                    mapLocations
                            = locationSource.getNoDataLocFormDefaultOrNon(siteID, appID,
                            eventID + "");
                } else {
                    mapLocations = locationSource.getNoOrHasDataLocFormDefNonWithAttr(siteID, appID,
                            eventID, outputMap, true);
                }
            } else {
                Log.i(TAG, "ALL DATA TAB");

                if (isSiteTypeDefault) {
                    tempLocations = locationSource.getDataForEventLocationV15(siteID, appID,
                            eventID, false, true);
                } else if (outputMap.isEmpty()) {
                    mapLocations = locationSource.getAllDataLocFormDefaultOrNon(siteID, appID);
                } else {
                    mapLocations = locationSource.getAllDataLocFormDefOrNonWithAttr(siteID, appID, outputMap);
                }

                handlerForUI.post(() -> {
                    if (tempLocations == null || tempLocations.size() < 1) {
                        emptylist_view.setText(R.string.no_fieldpoints_alert);
                    } else {
                        emptylist_view.setText(R.string.no_field_points);
                    }
                });
            }

            //commented to handle it later to add header location first and later recent location
/*            if (!mapLocations.isEmpty()) {
                tempLocations = new ArrayList<>();
                for (ArrayList<Location> loc : mapLocations.values()) {
                    tempLocations.addAll(loc);
                }
            }*/

            handlerForUI.post(() -> {
                setLocationsAdapter();
                mapFragment = new MapFragment();
                loadFragment(mapFragment, R.id.content_split_right_pane);
            });

            handlerForUI.post(() -> pbLocations.setVisibility(View.GONE));
            Log.i(TAG, "populateLocation() OUT time:" + System.currentTimeMillis());
        });
    }

    void SyncData() {
        Log.i(TAG, "SyncData() IN time:" + System.currentTimeMillis());

        if (CheckNetwork.isInternetAvailable(objContext)) {
            Log.i(TAG, "EventDataSource Instance Created call start time:" + System.currentTimeMillis());

            EventDataSource eventData = new EventDataSource(objContext);

            Log.i(TAG, "EventDataSource Instance Created call end time:" + System.currentTimeMillis());

            Log.i(TAG, "collect ClientGeneratedEventIDs list start time:" + System.currentTimeMillis());

            Log.i("TimeStampCheck", "TIME STAMP CHECK     " + System.currentTimeMillis());

            String creationDate = Util.parseMillisToMMMddyyy_hh_mm_ss_aa(System.currentTimeMillis());

            Log.i("TimeStampCheck", "TIME STAMP CHECK ************  " + creationDate);

            ArrayList<DEvent> eventList = eventData
                    .getClientGeneratedEventIDs(objContext);
            Log.i(TAG, "collect ClientGeneratedEventIDs list end time:" + System.currentTimeMillis());

            int count = eventList.size();
            Log.i(TAG, "ClientGeneratedEventIDs list Size:" + count);

            if (count > 0) {
                Log.i(TAG, "EventIDGeneratorTask Started :" + System.currentTimeMillis());

                EventIDGeneratorTask eventHandler
                        = new EventIDGeneratorTask(this, null, username,
                        password, true);

                eventHandler.execute();
                Log.i(TAG, "EventIDGeneratorTask Ended :" + System.currentTimeMillis());
            } else {

                boolean serviceRunning = isMyServiceRunning(DataSyncService.class);
                Log.i(TAG, "Service is Already Running Status:" + serviceRunning);
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
        Log.i(TAG, "SyncData() OUT time:" + System.currentTimeMillis());
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager
                    .getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actions, menu);
        menu.findItem(R.id.user).setTitle("Hi " + username + "!");

        AppPreferenceDataSource ds = new AppPreferenceDataSource(objContext);
        //KEY_PROJECT_FILE
        menu.findItem(R.id.filefolder).setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_PROJECT_FILE, userID));

        //KEY_EMERGENCY
        menu.findItem(R.id.nearby).setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_EMERGENCY, userID));

        menu.findItem(R.id.download_your_owndata).setVisible(ds
                .isFeatureAvailable(GlobalStrings.KEY_DOWNLOAD_DATA, userID));

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        menuItemSync = menu.findItem(R.id.download_forms);
        setSyncBadge();
        return super.onCreateOptionsMenu(menu);
    }

    private void setSyncBadge() {
        if (menuItemSync != null) {
            Util.setBadgeCount(this, menuItemSync, "",
                    Util.isThereAnyDataToSync(this));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //super.onOptionsItemSelected(item);

        String title = getString(R.string.erase_data);
        String msg = getString(R.string.are_you_sure_you_want_to_erase_all_the_data_from_this_device);
        String pos = getString(R.string.yes);
        String neg = getString(R.string.no);
        AlertDialog alert;

        Log.i(TAG, "Item Selected:" + item.getTitle());
        switch (item.getItemId()) {

            case R.id.nearby:


                if (CheckNetwork.isInternetAvailable(this)) {

                    Intent mapIntent = new Intent(this, MapForSiteActivity.class);
                    mapIntent.putExtra("PREV_CONTEXT", "LocationDetail");
                    mapIntent.putExtra("OPERATION", "nearby");
                    startActivity(mapIntent);
//                    overridePendingTransition(R.anim.right_to_left,
//                            R.anim.left_to_right);
                } else {
                    Toast.makeText(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.app_preferences:
                Intent pref_intent = new Intent(this, AppPreferencesActivity.class);
                startActivity(pref_intent);
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;


            case R.id.workorder_mytask:
                Intent tskintent = new Intent(this, TaskDetailActivity.class);
                tskintent.putExtra("SITE_ID", siteID);
                tskintent.putExtra("PARENTAPPID", appID);
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

                List<FileFolderItem> list = new FileFolderDataSource(this).getHomeFileFolderItemList(siteID + "");

                if (list.size() < 1) {
                    startActivity(new Intent(this, FileFolderSyncActivity.class));
                } else {
                    startActivity(new Intent(this, FileFolderMainActivity.class));
                }
//                overridePendingTransition(R.anim.right_to_left,
//                        R.anim.left_to_right);
                return true;

            case R.id.download_your_owndata:

                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(objContext);

                alertDialogBuilder1.setTitle(getString(R.string.downloadYourData));
                alertDialogBuilder1.setMessage(getString(R.string.do_you_want_to_download_your_data));
                // set positive button: Yes message
                alertDialogBuilder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to a new activity of the app
                        if (CheckNetwork.isInternetAvailable(objContext)) {
                            Intent downloadintent = new Intent(objContext, DownloadYourOwnDataActivity.class);
                            downloadintent.putExtra("SITEID", siteID);
                            downloadintent.putExtra("EVENTID", eventID);
                            downloadintent.putExtra("PARENTAPPID", appID);
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
                //Link to Update app from Play Store
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
                if (getCurrentFocus().getWindowToken() != null)
                    ((InputMethodManager) getApplicationContext()
                            .getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onEraseLocationClicked(LocationAdapter.ViewHolder viewHolder, String locId) {
        showLocationEraseAlert(viewHolder, locId);
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
                Toast.makeText(objContext, getString(R.string.location_data_is_cleared),
                        Toast.LENGTH_SHORT).show();
                if (newLocationsAdapter != null)
                    newLocationsAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void downloadForms() {
        countMediaSync = 0;

        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(this,
                    getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            return;
        }

        EventDataSource eventDbSource = new EventDataSource(this);
        ArrayList<DEvent> eventList = eventDbSource
                .getClientGeneratedEventIDs(this);

        if (eventList.size() > 0) {
            new EventIDGeneratorTask(this, null,
                    username, password, true, this).execute();
            isRefreshCalled = true;
        } else {
            //checking if any field data to upload then call download forms and later events will
            //be fetched as we'll be clearing tables to let submittals fragment know that it
            //should download events
            //then sync tasks
            uploadFieldData();
        }
    }

    public void logout(final Context context) {
        Log.i(TAG, "Logout() IN time:" + System.currentTimeMillis());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.are_you_sure_to_logout))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.setLogout(SplitLocationAndMapActivity.this);
                    }
                })
                .setNegativeButton(getString(R.string.no), null);
        AlertDialog dia = builder.create();
        dia.show();
        Log.i(TAG, "Logout() OUT time:" + System.currentTimeMillis());
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
    public void onTaskCompleted(Object obj) {

        FieldDataSource fieldData = new FieldDataSource(objContext);
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(objContext);
        EventDataSource eventData = new EventDataSource(objContext);

        if (obj != null) {
            if (obj instanceof String) {
                String result = (String) obj;
                if (result.equals("SUCCESS")) {
                    Event event = eventData.getEvent(appID, siteID, userID, deviceID);
                    int currentEventID = event.getEventId();
                    eventID = currentEventID;
                    uploadFieldData();
                } else {
                    Toast.makeText(objContext, getString(R.string.unable_to_connect_to_server),
                            Toast.LENGTH_LONG).show();
                }
            } else if (obj instanceof EventResponseModel) {

                EventResponseModel result = (EventResponseModel) obj;
                GlobalStrings.responseMessage = result.getMessage();

                if (result.isSuccess()) {

                    serverGenEventID = result.getData().getEventId();
                    setGeneratedEventID(result);
                    Log.i(TAG, "Event ID From Server:" + serverGenEventID);

                    fieldData.updateEventID(eventID, serverGenEventID);
                    attachDataSrc.updateEventID(eventID, serverGenEventID);
                    eventData.updateEventID(eventID, result);
                    new SampleMapTagDataSource(objContext)
                            .updateEventID_SampleMapTag(eventID + "",
                                    serverGenEventID + "");

                    eventID = serverGenEventID;

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
    public void onTaskCompleted() {
        //no use
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

    private void callMetaSync() {
        if (CheckNetwork.isInternetAvailable(objContext)) {
            Intent metaIntent = new Intent(objContext, MetaSyncActivity.class);
            startActivity(metaIntent);
            finish();
        } else {
            Toast.makeText(objContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }
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
    public void onLocationItemClicked(Location location) {
        locationObj = selectedLocationObj;
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
                Toast.makeText(objContext, "Location data is cleared", Toast.LENGTH_SHORT).show();
                if (newLocationsAdapter != null)
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

    @Override
    public void onLocationDeny() {
        //no use yet
    }

    public class CustomComparator implements Comparator<Location> {
        @Override
        public int compare(Location lhs, Location rhs) {
            return lhs.getLocationName().compareTo(rhs.getLocationName());
        }
    }

    void setLocationsAdapter() {

        if (!mapLocations.isEmpty()) {
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
                    eventID + "", Location.class);
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

            newLocationsAdapter = new LocationsAdapter(this,
                    tempLocations, appID + "",
                    eventID + "", this);

            rvLocations.setAdapter(newLocationsAdapter);
            rvLocations.addItemDecoration(new DividerItemDecoration(this,
                    LinearLayoutManager.VERTICAL));
            rvLocations.setVisibility(View.VISIBLE);
            emptylist_view.setVisibility(View.GONE);
        } else {
            rvLocations.setVisibility(View.GONE);
            emptylist_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRefreshCalledFromTabScreen) {
            isRefreshCalledFromTabScreen = false;
            finish();
        }

        //re-initialising in case fragment takes time to load
        listView = locationFrag.listView;
        rvLocations = locationFrag.rvLocations;
        emptylist_view = locationFrag.emptylist_view;
        location_attribute_hdr_tv = locationFrag.location_attribute_hdr_tv;

        badElf = new BadELFGPSTracker(this);

        HashMap<String, String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getSharedPreferences("MULTIPLEATTRIBUTE",
                MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("AttributeHashMap",
                        (new JSONObject()).toString());
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
        siteID = Integer.parseInt(Util.getSharedPreferencesProperty(objContext,
                GlobalStrings.CURRENT_SITEID));

        isSiteTypeDemo = siteDataSource.isSiteTypeDemo(siteID);

        if (isSiteTypeDemo) {
            layoutCloseEvent.setVisibility(View.INVISIBLE);
            menuMultipleActions.setVisibility(View.INVISIBLE);
        }

        String guid = Util.getSharedPreferencesProperty(objContext, username);
        // searchLoc.setText(null);
        if (guid == null || guid.isEmpty()) {
            Util.reLogin(this, username, password);
            if (ApplicationActivity.applicationActivity != null)
                ApplicationActivity.applicationActivity.finish();
            if (SiteActivity.siteActivity != null)
                SiteActivity.siteActivity.finish();
            finish();
            Log.i(TAG, "onResume() relogin End time:" + System.currentTimeMillis());
        } else {
            //21-Feb-17 Make it as a feature
            FieldDataSource obj = new FieldDataSource(objContext);
            obj.truncateLocFormStatus();
            obj.insertChildAppStatus(eventID);
            populateLocation();

/*            mapFragment = new MapFragment();
            loadFragment(mapFragment, R.id.content_split_right_pane);*/
        }

        boolean isSwitched = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.IS_LOCATION_SWITCHED);

        if (isSwitched) {
            appID = Util.getSharedPrefIntProperty(this, GlobalStrings.SWITCHED_APPID);
            siteID = Util.getSharedPrefIntProperty(this, GlobalStrings.SWITCHED_SITE_ID);
            siteName = Util.getSharedPreferencesProperty(this,
                    GlobalStrings.SWITCHED_SITENAME);
            eventID = Util.getSharedPrefIntProperty(this, GlobalStrings.SWITCHED_EVENT_ID);

            SharedPreferences.Editor editor = getSharedPreferences("Event_AppId",
                    MODE_PRIVATE).edit();
            editor.clear();

            editor.putInt("EventId", eventID);
            editor.putInt("AppId", appID);
            editor.apply();
            editor.commit();

            dispappName = new SiteMobileAppDataSource(objContext)
                    .getMobileAppDisplayNameRollIntoApp(appID, siteID);
            populateLocation();

            //resetting value again
            Util.setSharedPreferencesProperty(this, GlobalStrings.IS_LOCATION_SWITCHED,
                    false);
        }

        setSyncBadge();
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
    }

    public void loadFragment(Fragment fragment, int contentFrameId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(contentFrameId, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = objContext.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onclickTagLocation(String locID, String lat, String longi, String locationName) {
        Intent i = new Intent(objContext, MapActivity.class);
        i.putExtra("LOC_ID", locID);
        i.putExtra("SITE_NAME", siteName);
        i.putExtra("LOCATION_NAME", locationName);
        i.putExtra("EVENT_ID", eventID);
        i.putExtra("APP_ID", appID);
        i.putExtra("PREV_CONTEXT", "Location");
        i.putExtra("OPERATION", GlobalStrings.TAG_LOCATION);
        i.putExtra("LATITUDE", lat);
        i.putExtra("LONGITUDE", longi);
        startActivity(i);

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }

        Log.i(TAG, "onclickTagLocation() :\n Arguments to Map Activity-\n" +
                " EventID=" + eventID + "\n LocationID:" + locID + "\n AppID:" + appID
                + "\n SiteID:" + siteID + "\n LocationName:" + locationName + "\n Operation:"
                + 1 + "\n Latitude:" + lat + "\n Longitude:" + longi);
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

        boolean gpsPermissionStatus = checkWriteExternalPermission();
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
        if (eventID < 0) {
            eventID = new EventDataSource(objContext).getServerEventID(eventID + "");
        }

        Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_APPNAME, dispappName);

        Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_LOCATIONID, locID);
        Util.setSharedPreferencesProperty(objContext, GlobalStrings.CURRENT_LOCATIONNAME, locName);
        Util.setSharedPreferencesProperty(objContext, GlobalStrings.SESSION_USERID,
                userID + "");
        Util.setSharedPreferencesProperty(objContext, GlobalStrings.SESSION_DEVICEID, deviceID);
        Util.setSharedPreferencesProperty(objContext, eventID + "", locID);
        Util.setSharedPreferencesProperty(objContext, eventID + "",
                new Gson().toJson(locationObj));//saving location for last visited location

        Log.i(TAG, "onclickLocationItem() Set Session Location ID-" + locID + "" +
                " And Location Name-" + locName + " And UserID-" + userID + " And DeviceID-"
                + deviceID);

        String appType = mobileAppSource.getAppType(appID);

        Log.i(TAG, "onclickLocationItem() App_Type:" + appType);

        FieldDataSource fieldData = new FieldDataSource(objContext);

        childAppList = mobileAppSource.getChildApps(appID, siteID, locationID);
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
                    Log.i(TAG, "onclickLocationItem() App Form:" + appForm
                            + " , MobileAppID:" + id);
                    break;
                }
            }
            Log.i(TAG, "onclickLocationItem() check Header Data for Location:" + locID
                    + " , UserID:" + userID + " " +
                    ", EventID:" + eventID + " , MobileAppID:" + id + " , SiteID:" + siteID);

            boolean exists = fieldData.isExistsHeaderData(locID, eventID,
                    userID, id, siteID);
        } else {

            Log.i(TAG, "Location ID Selected:" + locID);
            Intent locationDetailIntent = new Intent(objContext,
                    LocationDetailActivity.class);

            locationDetailIntent.putExtra("EVENT_ID", eventID);
            locationDetailIntent.putExtra("LOCATION_ID", locID);
            locationDetailIntent.putExtra("APP_ID", appID);
            locationDetailIntent.putExtra("SITE_ID", siteID);
            locationDetailIntent.putExtra("SITE_NAME", siteName);
            locationDetailIntent.putExtra("APP_NAME", dispappName);
            locationDetailIntent.putExtra("COC_ID", "");

            locationDetailIntent.putExtra("LOCATION_NAME", locName);
            locationDetailIntent.putExtra("LOCATION_DESC", locDesc == null ? "" : locDesc);
            locationDetailIntent.putExtra(GlobalStrings.FORM_DEFAULT, formDefault);

            Log.i(TAG, "onclickLocationItem() :\n Arguments to Location Detail Activity-\n" +
                    " EventID=" + eventID + "\n LocationID:" + locID + "\n AppID:" + appID
                    + "\n SiteID:" + siteID + "\n SiteName:" + siteName + "\n App Name:"
                    + dispappName + " \n Location Name:" + locName
                    + "\n Location Desc:" + locDesc + "\n\n");

            try {
                startActivity(locationDetailIntent);
                if (searchView.isSearchOpen()) {
                    searchView.closeSearch();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onclickLocationItem( )Error in Redirecting to Details Form:"
                        + e.getMessage());
                Toast.makeText(objContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("EventID", eventID);
        Log.i(TAG, "onSaveInstanceState() Save EventID:" + eventID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("EventID")) {
            eventID = savedInstanceState.getInt("EventID");
            Log.i(TAG, "onRestoreInstanceState() Restore EventID:" + eventID);
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
                Log.i(TAG, "onActivityResult() CAPTURE_SIGNATURE Start time:"
                        + System.currentTimeMillis());
                boolean isOk = true;
                closingEvents();
                Log.i(TAG, "onActivityResult() CAPTURE_SIGNATURE End time:"
                        + System.currentTimeMillis());
            } else if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                    && resultCode == RESULT_OK) {

                Log.i(TAG, "onActivityResult() SYNC_ACTIVITY Start time:"
                        + System.currentTimeMillis());
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
                        CompletionPercentageDataSource cp
                                = new CompletionPercentageDataSource(objContext);

                        eventData.closeEventStatus(appID, siteID, eventEndDate, eventID + "");
                        cp.truncatePercentageByRollAppID_And_SiteID(siteID + "",
                                appID + "");
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
                Log.i(TAG, "onActivityResult() SYNC_ACTIVITY End time:"
                        + System.currentTimeMillis());
            } else if (requestCode == DOWNLOAD_KMZ_REQUEST_CODE
                    && resultCode == RESULT_OK) {

                Log.i(TAG, "onActivityResult() DOWNLOAD_KMZ Start time:"
                        + System.currentTimeMillis());

                if (data.hasExtra("SUCCESS_FLAG") && data.hasExtra("SUCCESS_MESSAGE")) {
                    boolean dataDownloaded = data.getBooleanExtra("SUCCESS_FLAG",
                            false);
                    String resultMessage = data.getStringExtra("SUCCESS_MESSAGE");

                    Log.i(TAG, "KMZ Download result:" + dataDownloaded + " , Result Message:"
                            + resultMessage);
                    if (dataDownloaded) {

                        FileFolderDataSource fd = new FileFolderDataSource(objContext);
                        final ArrayList<FileFolderItem> list = fd.getKMZList(siteID + "");
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
                            String projectFolderPath = Util.getFileFolderDirPath(objContext, siteID + "");
                            Log.i(TAG, "KML Location:" + projectFolderPath);
                            if (!projectFolderPath.isEmpty())
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

            } else if (requestCode == LocationActivity.REQUEST_CODE_ADD_LOCATION
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
            locationDataSource.insertEventLocations(addedLocations, eventID + "",
                    appID + "");
        }
    }

    protected void show_Dialog(final String path) {
        Log.i(TAG, "showDialog() IN time:" + System.currentTimeMillis());

        FileFolderDataSource fd = new FileFolderDataSource(objContext);
        final ArrayList<FileFolderItem> list = fd.getKMZList(siteID + "");
        final String[] fileList = new String[list.size()];

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

                    String mChosenFile = fileList[which];
                    //you can do stuff with the file here too
                    String f_guid = list.get(which).getItemGuid();
                    Log.i(TAG, "showDialog() Selected File GUID:" + f_guid);

                    if (Util.unpackZip(path, f_guid)) {

                        Intent i = new Intent(objContext, MapActivity.class);
                        i.putExtra("SITE_NAME", siteName);
                        i.putExtra("EVENT_ID", eventID);
                        i.putExtra("APP_ID", appID);
                        i.putExtra("PREV_CONTEXT", "Location");
                        i.putExtra("OPERATION", GlobalStrings.LOAD_KMZ);
                        i.putExtra("KMZ_PATH", path);
                        startActivity(i);

                        Log.i(TAG, "unpackZip() Arguments to Map: " +
                                "SiteName=" + siteName + ",EventID=" + eventID +
                                ",App ID=" + appID + ",Context=Location,Operation=2,KMZ_PATH=" + path);

                        // startActivity(new Intent(objContext, KmlDemoActivity.class));
                    }
                }
            });
        }

        dialog = builder.create();
        dialog.show();

        Log.i(TAG, "showDialog() OUT time:" + System.currentTimeMillis());
    }

    private void removeAttachmentAfterSyncResult(String filePath) {
        Log.i(TAG, "removeAttachmentAfterSyncResult() EventID Not Server Generated ");

        Log.i(TAG, "removeAttachmentAfterSyncResult() IN time:" + System.currentTimeMillis());
        deleteFileFromStorage(filePath);
        int count = new AttachmentDataSource(objContext).deleteAttachment(eventID, "S");
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

    public void closeEventAlert() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SplitLocationAndMapActivity.this);

        //1/29/2018
        alertDialogBuilder.setTitle(getString(R.string.close_end_field_event));
        alertDialogBuilder
                .setMessage(getString(R.string.sure_submit_data_and_close_event));

        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String capture = Util.getSharedPreferencesProperty(objContext,
                                GlobalStrings.CAPTURE_SIGNATURE);
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
                            intent.putExtra("EVENT_ID", eventID);
                            intent.putExtra("APP_ID", appID);
                            intent.putExtra("SITE_ID", siteID);
                            intent.putExtra("CLOSE", "true");
                            intent.putExtra("UserID", userID);
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
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Log.i(TAG, "closeEventAlert() End time:" + System.currentTimeMillis());
    }

    public void RequiredDataInFormAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SplitLocationAndMapActivity.this);

        alertDialogBuilder.setTitle(getString(R.string.attention));
        alertDialogBuilder
                .setMessage(getString(R.string.some_forms_have_mandatory_field_need_to_be_filled));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getString(R.string.show),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent req_intent = new Intent(objContext, RequiredFieldsListActivity.class);

                        req_intent.putExtra("APP_ID", appID);
                        req_intent.putExtra("EVENT_ID", eventID);
                        req_intent.putExtra("SITE_ID", siteID);
                        req_intent.putExtra("SITENAME", siteName);
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

    public void uploadFieldData() {

        isRefreshCalled = true;

        LocationDataSource locDataSource = new LocationDataSource(objContext);
        AttachmentDataSource attachDataSource = new AttachmentDataSource(objContext);

        //CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        boolean isLocationsAvailableToSync = locDataSource.isOfflineLocationsAvailable();
        boolean isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        boolean isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(objContext);

        boolean isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync
                && !isAttachmentsAvailableToSync) {
            syncTasks();
        } else {
            Log.i(TAG, "uploadFieldData() Upload Field Data Called:"
                    + System.currentTimeMillis());
            Intent dataUpload = new Intent(objContext, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", eventID);
            startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        }
    }

    public void uploadFieldDataBeforeEndEvent() {
        Log.i(TAG, "Upload Field Data endEvent Call  start:" + System.currentTimeMillis());
        Intent dataUpload = new Intent(objContext, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", username);
        dataUpload.putExtra("PASS", password);
        dataUpload.putExtra("EVENT_ID", eventID);
        dataUpload.putExtra("CLOSE_EVENT", true);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        Log.i(TAG, "Upload Field Data endEvent arguments:EventID-=" + eventID +
                ",UserName=" + username + ",Password=" + password);
        Log.i(TAG, "Upload Field Data endEvent Call End:" + System.currentTimeMillis());
    }

    public void closingEvents() {

        EventDataSource eventData = new EventDataSource(objContext);
        Log.i(TAG, "closingEvents() Session UserID:" + userID);
        Log.i(TAG, "closingEvents() getEvent arguments :AppID=" + appID +
                ",SiteID=" + siteID + ",userID=" + userID + ",DeviceID=" + deviceID);
//        event = eventData.getEvent(appID, siteID, userID, getDeviceID());
        Log.i(TAG, "closingEvents() getEvent End:" + System.currentTimeMillis());
        closeEvent = true;
        Log.i(TAG, "closingEvents() Call GPS Tracker start time:" + System.currentTimeMillis());

        boolean serverGenerated = eventData
                .isEventIDServerGenerated(eventID);
        Log.i(TAG, "closingEvents() Check EventID Server Generated result:" + serverGenerated);

        if (!serverGenerated) {
            Log.i(TAG, "closingEvents() EventID Not Found Server Generated ");

            final DEvent event = new DEvent();
            event.setSiteId(siteID);
            event.setMobileAppId(appID);
            event.setUserId(userID);
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
             * fieldSource.updateEventEndDateTime(appID, dateString);
             */

            Log.i(TAG, "closingEvents() EventID Found Server Generated ");

            if (CheckNetwork.isInternetAvailable(objContext)) {
                uploadFieldDataBeforeEndEvent();
            } else {
                Log.i(TAG, "closingEvents() No Internet.Delete captured signture(s) = "
                        + savedFilePath);

                removeAttachmentAfterSyncResult(savedFilePath);
                CustomToast.showToast((Activity) objContext,
                        getString(R.string.bad_internet_connectivity), 5);
            }
        }

        Log.i(TAG, "closingEvents() End time:" + System.currentTimeMillis());
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
                    syncTaskAttachments(attachmentList);
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
                DeviceInfoModel ob = DeviceInfo.getDeviceInfo(SplitLocationAndMapActivity.this);
                String deviceToken = Util.getSharedPreferencesProperty(SplitLocationAndMapActivity.this,
                        GlobalStrings.NOTIFICATION_REGISTRATION_ID);
                String uID = Util.getSharedPreferencesProperty(SplitLocationAndMapActivity.this,
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

    private void syncTaskAttachments(ArrayList<TaskDataResponse.AttachmentList> list) {

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

    private class ReportAsyncTask extends AsyncTask<MediaType, Void, String> {

        private boolean isForSelf;
        private boolean isForPM;
        private boolean isPdf;
        private ProgressDialog progressDialog;
        private String isDownloaded = "false";
        private String mResponseString;
        private String mErrorString = null;

        public ReportAsyncTask(boolean isForPm, boolean isPdf, boolean isForSelf) {
            this.isForPM = isForPm;
            this.isPdf = isPdf;
            this.isForSelf = isForSelf;
            progressDialog = new ProgressDialog(objContext);
            progressDialog.setMessage(getString(R.string.generating_report_please_wait));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isForPM)
                progressDialog.show();
        }

        @Override
        protected String doInBackground(MediaType... params) {
            try {
                if (null != mAquaBlueService) {
/*                    if (!Util.isUrlV20OrMobileTest(objContext))
                        isDownloaded = mAquaBlueService.generateReport(getResources().getString(R.string.prod_base_uri),
                                getResources().getString(R.string.mobile_report_required),
                                siteID + "", eventID + "", appID + "",
                                userID + "", isForPM, isPdf, isForSelf);
                    else*/
                    isDownloaded = mAquaBlueService.generateReport(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.generate_report_by_id),
                            siteID + "", eventID + "", appID + "",
                            userID + "", isForPM, isPdf, isForSelf,
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
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
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
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * custom TouchListener
     * note:
     * since I want to return the global coordinate of the touch event, I need to use getRawX(.), not getX(.)
     */
    private class DividerTouchListener implements View.OnTouchListener {

        @SuppressLint({"ClickableViewAccessibility", "MissingPermission"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                case MotionEvent.ACTION_CANCEL:
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(40);
                    break;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    rebuildView(event.getRawX());
                    break;
            }
            return true;
        }
    }

    /**
     * rebuilds entire view by forcing layout pass on root view
     *
     * @param draggedToX
     */
    private void rebuildView(float draggedToX) {
        // reset weights
        mPercentLeft = computeNewPercentLeft(draggedToX);
        setWeights(mPercentLeft);

        // save to extras
        getIntent().putExtra(EXTRA_PERCENT_LEFT, mPercentLeft);

        // force layout pass
        ViewGroup viewGroup = findViewById(R.id.activity_split_pane);
        viewGroup.requestLayout();
    }

    /**
     * Computes the new percent left based on draggedToX from the touch listener
     */
    private float computeNewPercentLeft(float draggedToX) {
        return (100 - (100 * (mTotalWidth - draggedToX) / mTotalWidth));
    }

    /**
     * converts dip dimension to percentage of the screen
     *
     * @param dip
     * @return
     */
    private float convertDipToPercent(int dip) {
        return (dip / mTotalWidth) * 100;
    }

    /**
     * sets the layout weights of the left and right panes
     *
     * @param percentLeft
     */
    private void setWeights(float percentLeft) {
        Log.d("TAG", "minimum width = " + mMinimumWidth);

        float percentRight = 100 - percentLeft;

        // if left side too small, resize
        if (percentLeft < mMinimumWidth) {
            percentLeft = mMinimumWidth;
            percentRight = 100 - percentLeft;
        }

        // if right side too small, resize
        if (percentRight < mMinimumWidth) {
            percentRight = mMinimumWidth;
            percentLeft = 100 - percentRight;
        }

        // set weights
        mLeftPane.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, percentLeft));
        mRightPane.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, percentRight));
    }
}