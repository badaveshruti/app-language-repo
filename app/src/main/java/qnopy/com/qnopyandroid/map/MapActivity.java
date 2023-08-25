package qnopy.com.qnopyandroid.map;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlLineString;
import com.google.maps.android.data.kml.KmlMultiGeometry;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.maps.android.data.kml.KmlPolygon;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.sample_tag;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.AttachmentData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.EventLocationDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.kml.GeometryObjects;
import qnopy.com.qnopyandroid.kml.Placemark;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.ui.activity.AppPreferencesActivity;
import qnopy.com.qnopyandroid.ui.activity.FileFolderMainActivity;
import qnopy.com.qnopyandroid.ui.activity.FileFolderSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.ui.activity.MetaSyncActivity;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.uiutils.SendDBTask;
import qnopy.com.qnopyandroid.util.Util;

public class MapActivity extends ProgressDialogActivity implements
        OnMarkerClickListener, OnMarkerDragListener,
        OnMapLongClickListener, OnMapClickListener, OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, CustomAlert.LocationServiceAlertListener {

    public static final String IMAGE_STORAGE_DIR = "QnopyPictures";
    ArrayList<Placemark> pList = null;
    ArrayList<Marker> lastVisitedpoint = new ArrayList<>();
    ArrayList<Marker> gpspoint = new ArrayList<>();
    ArrayList<Marker> currentLocationList = new ArrayList<>();
    KmlLayer kmlLayer;
    PopupWindow infopopup = null;
    private static final String TAG = "MapActivity";
    Location mCurrentLocation;
    String address = null;
    Double lati, lngi;
    ProgressDialog procDialog = null;
    String msgBoard;
    LinearLayout switchContainer;
    boolean zoomCurrentLocation = false;
    LocationManager mLocationManager;
    GoogleMap googleMap;

    int parentAppID, eventID;
    String siteID, username, locID, password, locationName, UserID, DeviceID;
    int OPERATION = -1;//0-Show all tagged Locations,1-Tag Location,3-Show Old Map
    boolean IS_LONG_CLICKKED = false, IS_MARKER_CLICKKED = false;
    LatLng focus_kmzpoint = null;
    String sitename, prev_context, samplePrefix, setID, fparamID, CurrentSampleValue;
    private boolean isAddNewTag = true, addNewLocation = false;

    private HashMap<String, ArrayList<qnopy.com.qnopyandroid.clientmodel.Location>> mapLocations = new HashMap<>();
    private BadELFGPSTracker badElf;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;
    private volatile CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getCurrentSampleValue() {
        return CurrentSampleValue;
    }

    public void setCurrentSampleValue(String currentSampleValue) {
        CurrentSampleValue = currentSampleValue;
    }

    public String getSetID() {
        return setID;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public String getFparamID() {
        return fparamID;
    }

    public void setFparamID(String fparamID) {
        this.fparamID = fparamID;
    }

    public String getSamplePrefix() {
        return samplePrefix;
    }

    public void setSamplePrefix(String samplePrefix) {
        this.samplePrefix = samplePrefix;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getOPERATION() {
        return OPERATION;
    }

    public void setOPERATION(int OPERATION) {
        this.OPERATION = OPERATION;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getPrev_context() {
        return prev_context;
    }

    public void setPrev_context(String prev_context) {
        this.prev_context = prev_context;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public int getParentAppID() {
        return parentAppID;
    }

    public void setParentAppID(int parentAppID) {
        this.parentAppID = parentAppID;
    }

    public String getLocID() {
        return locID;
    }

    public void setLocID(String locID) {
        this.locID = locID;
    }

    Marker dragMarker;
    protected String latitude, longitude;
    protected boolean gps_enabled = false, network_enabled = false;

    LatLng markerlt;
    boolean enableScreenCapture = false;
    private String filePrefix;
    List<String> strlist = new ArrayList<>();
    Switch locationswitchbtn;
    LinearLayout locationswitcher;
    Bundle extras;
    Context context;
    int POPUP_HEIGHT = 300;
    float screen_density;
    private static final LocationRequest REQUEST =
            LocationRequest.create()
                    .setInterval(10000)       //10sec  // 5 seconds prev
                    .setFastestInterval(10000)    // 16ms = 60fps
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private File file = null;

    RadioGroup rgViews;
    TextView toList;
    ImageView toList_iv;
    Button done, capture;
    public static Activity mapActivity = null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_ux);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        context = this;
        mapActivity = this;
        ScreenReso application = ((ScreenReso) this.getApplication());
        application.getScreenReso(this);
        screen_density = application.getDensity();

//        requestCurrentLocation();
        String title = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_APPNAME);
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (!CheckNetwork.isInternetAvailable(context)) {
            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }

        capture = (Button) findViewById(R.id.capture);
        done = (Button) findViewById(R.id.close);
        toList = (TextView) findViewById(R.id.tolist);
        toList_iv = (ImageView) findViewById(R.id.map_list_iv);

        switchContainer = (LinearLayout) findViewById(R.id.switchContainer);
        locationswitcher = (LinearLayout) findViewById(R.id.locationswitch_container);
        locationswitchbtn = (Switch) findViewById(R.id.locationswitch);
        locationswitchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                invokeTouchEvent = false;
                if (isChecked) {
                    if (!addMarkersToMap()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(getString(R.string.attention) + "!")
                                .setCancelable(false)
                                .setMessage(getString(R.string.you_have_not_tagged_any_location_yet))
                                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setKmzListeners();
                                        //customZoomMyLocation(null, 15f);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    googleMap.clear();
                    currentLocationList.clear();
                    currentLocationList = new ArrayList<Marker>();
                    addKmlLayer();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(focus_kmzpoint, 16f));
                }
            }
        });

        MapsInitializer.initialize(getApplicationContext());

        rgViews = (RadioGroup) findViewById(R.id.rg_views);
        if (rgViews != null) {
            rgViews.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (googleMap != null) {
                        if (checkedId == R.id.rb_normal) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        } else if (checkedId == R.id.rb_satellite) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        }
                    }
                }
            });
        }

        extras = getIntent().getExtras();
        setUsername(Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME));
        setPassword(Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD));
        setSiteID(Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID));
        setUserID(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        setDeviceID(Util.getSharedPreferencesProperty(context, GlobalStrings.DEVICEID));

        if (extras != null) {
            setPrev_context(extras.getString("PREV_CONTEXT"));
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                addGoogleMap();
        } else
            addGoogleMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {// If the permission request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGoogleMap();
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    private List<qnopy.com.qnopyandroid.clientmodel.Location> populateLocation() {

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

        SiteDataSource siteDataSource = new SiteDataSource(this);
        boolean isSiteTypeDefault = siteDataSource.isSiteTypeDefault(Integer.parseInt(siteID));

        LocationDataSource locationSource = new LocationDataSource(context);
        ArrayList<qnopy.com.qnopyandroid.clientmodel.Location> tempLocations = new ArrayList<>();

        mapLocations.clear();
        if (isSiteTypeDefault) {
            mapLocations.put(GlobalStrings.NON_FORM_DEFAULT,
                    locationSource.getDataForEventLocationV15(Integer.parseInt(getSiteID()),
                            getParentAppID(),
                            getEventID(), true, false));
        } else if (outputMap.isEmpty()) {
            mapLocations
                    = locationSource.getAllDataLocFormDefaultOrNon(Integer.parseInt(getSiteID()), getParentAppID());
        } else {
            mapLocations = locationSource.getAllDataLocFormDefOrNonWithAttr(Integer.parseInt(getSiteID()), getParentAppID(), outputMap);
        }

/*        if (Util.isStagingUrlOrV15(this)) {
            mapLocations
                    = location.getAllDataLocFormDefaultOrNonForMap(getSiteID(), getParentAppID());

            if (!mapLocations.isEmpty()) {
                temp = new ArrayList<>();
                for (ArrayList<MapLocation> loc : mapLocations.values()) {
                    temp.addAll(loc);
                }
            }
        } else if (isSiteTypeDefault) {
            ArrayList<qnopy.com.qnopyandroid.clientmodel.Location> listLoc
                    = location.getDataForEventLocationV15(Integer.parseInt(siteID), getParentAppID(),
                    getEventID(), false, true);

            for (qnopy.com.qnopyandroid.clientmodel.Location loc : listLoc) {

                MapLocation mapLocation = new MapLocation();
                mapLocation.setLocationName(loc.getLocationName());
                mapLocation.setLocationID(loc.getLocationID() + "");
                mapLocation.setSiteID(loc.getSiteID());
                mapLocation.setLocationDesc(loc.getLocFormHeader());
                mapLocation.setExtField2(loc.getExtField2());
                mapLocation.setExtField7(loc.getExtField7());
                mapLocation.setLatitude(loc.getLatitude());
                mapLocation.setLongitude(loc.getLongitude());

                FieldDataSource fd = new FieldDataSource(this);
                mapLocation.setData(fd.isLocationInStatus(loc.getLocationID()));//isDataAvailableForLocation(locID)

                if (loc.getLatitude() != null && loc.getLongitude() != null) {
                    if (!loc.getLatitude().isEmpty() && !loc.getLongitude().isEmpty()) {
                        temp.add(mapLocation);
                    }
                }
            }

        } else if (outputMap.isEmpty()) {
            //11-Apr-16
            temp = location.getLocationForFormOnMap(Integer.parseInt(getSiteID()), getParentAppID());
        } else {
            temp = location.getLocationForFormOnMapWithAttribute(Integer.parseInt(getSiteID()), getParentAppID(), outputMap);

            boolean hasDuplicateLocation = hasDuplicate(temp);
            List<MapLocation> commonAttributeLocation = new ArrayList<MapLocation>();
            Log.e("hasDuplicate", "populateLocation: " + hasDuplicateLocation);

            if (hasDuplicateLocation) {
                commonAttributeLocation = hasDuplicateLocations(temp);
                temp = commonAttributeLocation;
            }
        }*/

        //  if (temp == null || temp.size() < 1) {
        //      temp = location.getLocationForSiteonMap(Integer.parseInt(getSiteID()));
        //  }

        if (!mapLocations.isEmpty()) {
            for (ArrayList<qnopy.com.qnopyandroid.clientmodel.Location> loc : mapLocations.values()) {
                tempLocations.addAll(loc);
            }
        }

        if (tempLocations.size() == 0) {
            Toast.makeText(this, getString(R.string.unable_to_populate_locations),
                    Toast.LENGTH_SHORT).show();
        }
        return tempLocations;
    }

    @Override
    public void onResume() {
        super.onResume();
        badElf = new BadELFGPSTracker(MapActivity.this);

        //adding google map again in case user allows location permission from settings
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                addGoogleMap();
            }
        }
    }

    @Override
    public void onPause() {
        badElf.disconnectTracker();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_menu, menu);

        AppPreferenceDataSource ds = new AppPreferenceDataSource(context);

        if (extras == null) {
            menu.findItem(R.id.done).setVisible(false);
            menu.findItem(R.id.add_location).setVisible(false);
        } else {

            if (getOPERATION() == GlobalStrings.SHOW_TAGGED_LOCATION) {
                //KEY_ADD_LOCATION
                menu.findItem(R.id.add_location).setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_ADD_LOCATION, Integer.parseInt(getUserID())));
            }
            if (getOPERATION() == GlobalStrings.TAG_LOCATION) {

                menu.findItem(R.id.done).setVisible(true);
                menu.findItem(R.id.add_location).setVisible(false);

            }
            if (getOPERATION() == GlobalStrings.TAG_SAMPLE) {
                menu.findItem(R.id.add_location).setVisible(false);
                menu.findItem(R.id.done).setVisible(false);

            }
            if (getOPERATION() == GlobalStrings.LOAD_GPSTRACK) {
                menu.findItem(R.id.done).setVisible(true);
                menu.findItem(R.id.add_location).setVisible(false);
            }

            if (getOPERATION() == GlobalStrings.LOAD_KMZ) {
                menu.findItem(R.id.add_location).setVisible(false);
            }

            if (getPrev_context() != null && (getPrev_context().equals("Draw") ||
                    getPrev_context().equals("LocationDetail"))) {
                menu.findItem(R.id.done).setVisible(false);
//                menu.findItem(R.id.action_delete_user).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(false);
                menu.findItem(R.id.send_db).setVisible(false);
//                menu.findItem(R.id.update_apk).setVisible(false);
                menu.findItem(R.id.filefolder).setVisible(false);
                menu.findItem(R.id.download_forms).setVisible(false);
                menu.findItem(R.id.add_location).setVisible(false);
            }
        }

        //KEY_PROJECT_FILE
        menu.findItem(R.id.filefolder).setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_PROJECT_FILE, Integer.parseInt(getUserID())));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //super.onOptionsItemSelected(item);

        String title = "Erase Data";
        String msg = "Are you sure you want to erase all the data from this device?";

        String pos = "Yes";
        String neg = "No";
        AlertDialog alert;

        Log.i(TAG, "Item Selected:" + item.getTitle());
        switch (item.getItemId()) {

            case R.id.done:
                if (dragMarker != null) {
                    saveTaggedLocation(dragMarker.getPosition());
                    setResult(Activity.RESULT_OK);
                }
                this.finish();
                return true;
            case R.id.action_offline:
                //  startActivity(new Intent(context, OfflineMapActivity.class));
                return true;

            case R.id.app_preferences:
                Intent pref_intent = new Intent(context, AppPreferencesActivity.class);
                startActivity(pref_intent);
                return true;

            case R.id.add_location:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.add_field_point))
                        .setMessage(getString(R.string.long_press_on_map_add_field_pt))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setTagLocationListeners();
                                addNewLocation = true;
                            }
                        })
                        .setCancelable(false);

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;


            case android.R.id.home:
                finish();
                return true;

            case R.id.filefolder:

                List<FileFolderItem> list = new FileFolderDataSource(context).getHomeFileFolderItemList(getSiteID() + "");
                if (list.size() < 1) {
                    startActivity(new Intent(context, FileFolderSyncActivity.class));
                } else {
                    startActivity(new Intent(context, FileFolderMainActivity.class));
                }

                return true;

            case R.id.download_forms:
                syncAlert();
                return true;
            default:
                return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    private void addGoogleMap() {
        try {
            ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Map " + e.getLocalizedMessage());
        }
    }

    private boolean addMarkersToMap() {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        gpspoint = new ArrayList<>();
        List<qnopy.com.qnopyandroid.clientmodel.Location> list = populateLocation();

        FieldDataSource fieldDataSource = new FieldDataSource(context);
        MetaDataSource metaDataSource = new MetaDataSource(context);
        String inputType = "GPS";
        ArrayList<String> fpidList = metaDataSource.getFieldParameterIDIfExist(inputType, parentAppID);
        if (fpidList != null && fpidList.size() > 0) {
            int gpsPointCount = 0;

            // int maxSetForEvent = fieldDataSource.getMaxOfSetForEvent(eventID);

            // for (int j = 1; j <= maxSetForEvent; j++) {

//                for (String GPS_fpID : fpidList) {
            ArrayList<String> latlngList = fieldDataSource.getStringValueForGPS(eventID, parentAppID + "");
            for (String latlngstring : latlngList) {

                if (latlngstring != null && latlngstring.contains(",")) {
                    gpsPointCount++;
                    String[] separated = latlngstring.split(",");
                    strlist = Arrays.asList(separated);

                    for (int i = 0; i < strlist.size(); i++) {
                        lati = Double.valueOf(strlist.get(0));
                        lngi = Double.valueOf(strlist.get(1));
                    }

                    IconGenerator mBubbleFactory = new IconGenerator(context);

                    mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);

                    Drawable myIcon = ContextCompat.getDrawable(context, R.drawable.gpsicon_vector);
                    mBubbleFactory.setBackground(myIcon);

                    Bitmap iconBitmap = mBubbleFactory.makeIcon(gpsPointCount + "");
                    Marker marker = googleMap.addMarker(new MarkerOptions().draggable(true).title(gpsPointCount + "")
                            .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(new LatLng(lati, lngi)));

                    //09-Jun-17 collect gps point
                    gpspoint.add(marker);
                }
            }
        }

        int tagcount = 0;

        for (qnopy.com.qnopyandroid.clientmodel.Location item : list) {

            if ((!item.getLatitude().equals("0") && !item.getLatitude().equals("0.0")) &&
                    (!item.getLongitude().equals("0") && !item.getLongitude().equals("0.0"))) {
                tagcount++;
                LatLng ll = new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));

                Log.i(TAG, "Marker Ready for Location :" + item.getLocationName() + " with lat:" + ll.latitude + " longitude:" + ll.longitude);

                IconGenerator mBubbleFactory = new IconGenerator(context);
//                    mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
                FieldDataSource fd = new FieldDataSource(this);

                if (fd.isLocationInStatus(item.getLocationID())) {
                    mBubbleFactory.setStyle(IconGenerator.STYLE_GREEN);
                } else
                    mBubbleFactory.setStyle(IconGenerator.STYLE_RED);

                Bitmap iconBitmap = mBubbleFactory.makeIcon(item.getLocationName()).copy(Bitmap.Config.ARGB_8888, true);
                Marker locmarker = googleMap.addMarker(new MarkerOptions().draggable(true).title(item.getLocationName())
                        .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(ll));

                currentLocationList.add(locmarker);
            }
        }

        progressDialog.dismiss();

        return tagcount > 0;

    }

//    private boolean addmarkerForGps() {
//        int tagcount = 0;
//
//        FieldDataSource fieldDataSource = new FieldDataSource(context);
//        MetaDataSource metaDataSource = new MetaDataSource(context);
//        String inputType = "GPS";
//        ArrayList<String> fpidList = metaDataSource.getFieldParameterIDIfExist(inputType, parentAppID);
//        if (fpidList != null && fpidList.size() > 0) {
//
//            int maxSetForEvent = fieldDataSource.getMaxOfSetForEvent(eventID);
//
//            for (int j = 1; j <= maxSetForEvent; j++) {
//
//                for (String GPS_fpID : fpidList) {
//                    String latlngstring = fieldDataSource.getStringValueForGPS(eventID, GPS_fpID, j);
//
//                    if (latlngstring != null && latlngstring.contains(",")) {
//                        String[] separated = latlngstring.split(",");
//                        strlist = Arrays.asList(separated);
//
//                        for (int i = 0; i < strlist.size(); i++) {
//                            lati = Double.valueOf(strlist.get(0));
//                            lngi = Double.valueOf(strlist.get(1));
//                        }
//
//                        IconGenerator mBubbleFactory = new IconGenerator(context);
//
////                    mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);
//                        mBubbleFactory.setColor(R.color.blue);
//
//                        Bitmap iconBitmap = mBubbleFactory.makeIcon();
//                        googleMap.addMarker(new MarkerOptions().draggable(true).title(j + "")
//                                .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(new LatLng(lati, lngi)));
//
//                        tagcount++;
//                    }
//
//
////                    googleMap.addMarker(new MarkerOptions().draggable(false).position(new LatLng(lati, lngi))
////                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gpsicon_vector)));
//                }
//            }
//
//        }
//
//        return tagcount > 0;
//
//    }

    private boolean addSampleTagToMap(ArrayList<sample_tag> dList) {

        int tagcount = 0;

        for (sample_tag item : dList) {

            if (item.getLatitude() != 0 && item.getLongitude() != 0) {
                tagcount++;
                LatLng ll = new LatLng(item.getLatitude(), item.getLongitude());
                IconGenerator mBubbleFactory = new IconGenerator(context);
//                mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
                mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);

//                TextView txt=new TextView(context);
//
//                txt.setText();
//                txt.setTextColor(Color.BLACK);
//                txt.setId(tagcount);
//
//                mBubbleFactory.setTextAppearance(context,txt.getId());
                Bitmap iconBitmap = mBubbleFactory.makeIcon(item.getSampleValue());

                googleMap.addMarker(new MarkerOptions().draggable(true).title(item.getSampleValue()).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(ll));
            }
        }
        return tagcount > 0;
    }

    private void setListeners() {
        if (googleMap != null) {
//					if(enableScreenCapture == false) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setTrafficEnabled(true);
        }
    }

    private void setKmzListeners() {
        if (googleMap != null) {
//					if(enableScreenCapture == false) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMarkerDragListener(this);
            googleMap.setOnMapLongClickListener(this);
            googleMap.setOnMapClickListener(this);
            googleMap.setTrafficEnabled(true);
        }
    }

    private void setTagLocationListeners() {
        if (googleMap != null) {
//					if(enableScreenCapture == false) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMarkerDragListener(this);
            googleMap.setOnMapLongClickListener(this);
            googleMap.setOnMapClickListener(this);
            googleMap.setTrafficEnabled(true);
        }
    }

    private String getAddress(LatLng lt) {
        String finalAddress = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lt.latitude, lt.longitude, 1);

            String CityName = addresses.get(0).getAddressLine(0);
            String StateName = addresses.get(0).getAddressLine(1);
            String CountryName = addresses.get(0).getAddressLine(2);
            finalAddress = CityName + "\n" + StateName + "\n" + CountryName;
            System.out.println("current4" + CountryName);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("current3:" + e.getLocalizedMessage());
        }

        return finalAddress;
    }

    public void capture(View v) {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
        captureScreenShot();
    }

    public void captureScreenShot() {
        SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {

                String currentLocationName = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME);
                bitmap = snapshot;
                try {
                    String baseDir = Util.getMainBaseDirPath(context);

                    String filename1;
                    if (getOPERATION() == GlobalStrings.TAG_SAMPLE) {
                        File mediaStorageDir = new File(
                                baseDir,
                                IMAGE_STORAGE_DIR);
                        baseDir = mediaStorageDir.getAbsolutePath();
                        filename1 = getSamplePrefix() + ".png";
                    } else {
                        filename1 = "temp_" + System.currentTimeMillis() + ".png";
                    }

                    //File folder = new File(baseDir, "Fetch Draw");
                    File folder = new File(baseDir);
                    folder.mkdir();
                    if (folder.exists()) {
                        file = new File(folder, filename1);
                        file.createNewFile();
                        //		fn = file.getAbsolutePath();
                        OutputStream os = null;
                        os = new FileOutputStream(file);
                        bitmap.compress(CompressFormat.PNG, 100, os);
                        if (file != null) {

                            if (getOPERATION() == GlobalStrings.TAG_SAMPLE) {
                                sample_tag updateTag = new sample_tag();
                                updateTag.setLocationID(getLocID());
                                updateTag.setEventID(getEventID() + "");
                                updateTag.setSiteID(getSiteID());
                                updateTag.setUserID(getUserID());
                                updateTag.setMobAppID(getParentAppID() + "");
                                updateTag.setFieldParamID(getFparamID());
                                updateTag.setSampleValue(getCurrentSampleValue());
                                updateTag.setSetID(getSetID());
                                updateTag.setFilePath(file.getPath());


                                Log.i(TAG, "Tag to Update on capture screen shot:" + updateTag.getFilePath());

                                updateSampleTagFilePath(updateTag);

                                AttachmentDataSource attachDataSource = new AttachmentDataSource(context);
                                AttachmentData attachData = new AttachmentData();

                                //14-Apr-17
                                String filePath = file.getPath();

                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
                                String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                                //            Shader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

                                String roundedLat = Util.RoundUpto_AfterDecimal(mCurrentLocation.getLatitude(), 6);
                                String roundedLongi = Util.RoundUpto_AfterDecimal(mCurrentLocation.getLongitude(), 6);
                                String waterMarkString = dateTime + "|Location:" + currentLocationName + "|Latitude:" + roundedLat +
                                        " , Longitude:" + roundedLongi;

                                WaterMarkPhoto(context, filePath, waterMarkString);


                                if (!attachDataSource.isFileAlreadyExist(file.getPath())) {
                                    attachData.setEventID(getEventID());
                                    attachData.setLocationID(getLocID());
                                    attachData.setAttachementType("C");
                                    attachData.setFileLocation(file.getPath());
                                    attachData.setCreationDate(System.currentTimeMillis());
                                    attachData.setLatitude(mCurrentLocation.getLatitude());
                                    attachData.setLongitude(mCurrentLocation.getLongitude());
                                    attachData.setDataSyncFlag(null);
                                    attachData.setEmailSentFlag(null);
                                    attachData.setTimeTaken(System.currentTimeMillis());

                                    attachData.setSiteId(Integer.valueOf(getSiteID()));
                                    attachData.setUserId(Integer.valueOf(getUserID()));
                                    attachData.setMobileAppId(getParentAppID());
                                    attachData.setSetId(Integer.valueOf(getSetID()));
                                    // attachData.setAzimuth(mSensorTracker.getAzimuthInDegress() + "");

                                    attachDataSource.insertAttachmentData(attachData, false);
                                } else {
                                    //19-Aug-16 Update DataSync Flag to NULL
                                    attachDataSource.updateModificationDate(file.getPath(),
                                            mCurrentLocation.getLatitude() + "",
                                            mCurrentLocation.getLongitude() + "");
                                }
                            }

                            Intent outPut = new Intent();
                            outPut.putExtra("FILE_NAME", file);
                            setResult(RESULT_OK, outPut);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error in Capture ScreenShot:" + e.getMessage());
                }
            }
        };

        googleMap.snapshot(callback);
    }

    void WaterMarkPhoto(Context mContext, String fileName, String waterMarkString) {
        Bitmap scaledBitmap = BitmapFactory.decodeFile(fileName);
        int y_start_position = 0;
        double width = 0, height = 0;
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();
        int textSize;
        if ((int) height < 550) {
            y_start_position = (int) (height - 40);

        } else {
            y_start_position = (int) (height - (height * 0.045));
        }

        //06-Apr-17 Add Water mark

        Point pont = new Point();
        pont.x = 20;
        pont.y = y_start_position;//1% of total height is reduced and is a start point


        if (screen_density >= 240) {
            if ((int) height < 550) {
                textSize = 15;
            } else {
                textSize = 18;
            }
        } else {
            if ((height - y_start_position) > 100) {
                textSize = 18;
            } else {
                textSize = 15;
            }
        }

        Log.i(TAG, "Water mark text size:" + textSize);
        //height<500?(int) (height*0.020)*density:(int) (height*0.011)*density
        scaledBitmap = Util.CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.WHITE, 100, textSize, false);

        Log.i(TAG, "Point x=" + pont.x + " y=" + pont.y);
//        Log.i(TAG, "WaterMarkPhoto() Water mark=" + waterMarkString);
        FileOutputStream out = null;
        // String filename = getFilename();

        File outfile = new File(fileName);
        try {

            out = new FileOutputStream(outfile);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "WaterMarkPhoto() error:" + e.getMessage());
        }

    }

    public void onclickLocationItem(String cocflag, String locID, String locName, String locDesc) {

        List<MobileApp> childAppList = null;

        MobileAppDataSource mobSource = new MobileAppDataSource(context);
        String appType = mobSource.getAppType(getParentAppID());
        // BoreDefinitionDataSource boreDef = new BoreDefinitionDataSource();
        FieldDataSource fieldData = new FieldDataSource(context);
        //01-Aug-17  LAST VISITED LOCATION & IS COC LOCATION
        Util.setSharedPreferencesProperty(context, getEventID() + "", locID);
        //01-Aug-17 IS COC LOCATION
        String isCOCLocation;
        if (cocflag == null || cocflag.isEmpty()) {
            isCOCLocation = "0";
        } else {
            isCOCLocation = cocflag;
        }

        Util.setSharedPreferencesProperty(context, locID, isCOCLocation);

        childAppList = mobSource.getChildApps(getParentAppID(),
                Integer.parseInt(getSiteID()), getLocID());

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
                if (appForm == null) {
                    appForm = "Detail";
                }
                if (appForm.equalsIgnoreCase("header")) {
                    id = app.getAppID();
                    break;
                }
            }
            boolean exists = fieldData.isExistsHeaderData(locID, getEventID(),
                    Integer.parseInt(getUserID()), id, Integer.parseInt(getSiteID()));
            System.out.println("header data " + exists);
            if (!exists) {
                //invokeWellLogHeader(locID, locName, locDesc);
            } else {
                //invokeWellLog(locID, locName, locDesc);
            }
        } else {

            Log.i(TAG, "Location ID Selected:" + locID);
            Intent LocationDetailIntent = new Intent(this,
                    LocationDetailActivity.class);

            LocationDetailIntent.putExtra("EVENT_ID", getEventID());
            LocationDetailIntent.putExtra("LOCATION_ID", locID);
            LocationDetailIntent.putExtra("APP_ID", getParentAppID());
            LocationDetailIntent.putExtra("SITE_ID", Integer.parseInt(getSiteID()));
            LocationDetailIntent.putExtra("SITE_NAME", getSitename());
            LocationDetailIntent.putExtra("JUMP_FROM_MAP", "YES");
            LocationDetailIntent.putExtra("LOCATION_NAME", locName);
            LocationDetailIntent.putExtra("LOCATION_DESC", locDesc == null ? "" : locDesc);

            try {
                startActivity(LocationDetailIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in Redirecting to Details Form:" + e.getMessage());
                Toast.makeText(this, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void close(View v) {
        Intent output = new Intent();
        if (googleMap != null) {
            if (dragMarker != null) {
                output.putExtra("CORRECTED_LATITUDE", dragMarker.getPosition().latitude);
                output.putExtra("CORRECTED_LONGITUDE", dragMarker.getPosition().longitude);
                System.out.println("mappppp" + dragMarker.getPosition().latitude + " " + dragMarker.getPosition().longitude);
                setResult(RESULT_OK, output);
            } else {
                if (mCurrentLocation != null) {
                    output.putExtra("CORRECTED_LATITUDE", mCurrentLocation.getLatitude());
                    output.putExtra("CORRECTED_LONGITUDE", mCurrentLocation.getLongitude());
                    Log.i(TAG, "My LAst Location Lat:" + mCurrentLocation.getLatitude() + " ,Lon:" + mCurrentLocation.getLongitude());
                    setResult(RESULT_OK, output);
                }
            }
        }
        this.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // if (getEventID() == 0) {
        outState.putInt("EventID", getEventID());
        // }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("EventID")) {
            setEventID(savedInstanceState.getInt("EventID"));
        }
    }

    @Override
    public void onBackPressed() {

        if (getOPERATION() == GlobalStrings.TAG_LOCATION) {
            if (dragMarker != null) {
                saveTaggedLocation(dragMarker.getPosition());
            } else if (getOPERATION() == GlobalStrings.LOAD_KMZ) {
                Intent output1 = new Intent();
                if (googleMap != null) {
                    if (dragMarker != null) {
                        output1.putExtra("CORRECTED_LATITUDE", dragMarker.getPosition().latitude);
                        output1.putExtra("CORRECTED_LONGITUDE", dragMarker.getPosition().longitude);
                        System.out.println("mappppp" + dragMarker.getPosition().latitude + " " + dragMarker.getPosition().longitude);
                        setResult(RESULT_OK, output1);
                    }
                }
            }
        } else if (getOPERATION() == GlobalStrings.SHOW_TAGGED_LOCATION) {
            Intent output = new Intent();
            if (googleMap != null) {
                if (dragMarker != null) {
                    output.putExtra("CORRECTED_LATITUDE", dragMarker.getPosition().latitude);
                    output.putExtra("CORRECTED_LONGITUDE", dragMarker.getPosition().longitude);
                    System.out.println("mappppp" + dragMarker.getPosition().latitude + " " + dragMarker.getPosition().longitude);
                    setResult(RESULT_OK, output);
                } else {
                    if (mCurrentLocation != null) {
                        output.putExtra("CORRECTED_LATITUDE", mCurrentLocation.getLatitude());
                        output.putExtra("CORRECTED_LONGITUDE", mCurrentLocation.getLongitude());
                        Log.i(TAG, "My LAst Location Lat:" + mCurrentLocation.getLatitude() + " ,Lon:" + mCurrentLocation.getLongitude());
                        setResult(RESULT_OK, output);
                    }
                }
            }
        }

        finish();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // do nothing during drag
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        System.out.println("current1");
        LatLng toPosition = marker.getPosition();
        //getAddress(toPosition);
        dragMarker = marker;
        Log.d(getClass().getSimpleName(), "Drag end at: " + toPosition);
        if (getOPERATION() != GlobalStrings.TAG_SAMPLE) {
            saveDraggedLocation(marker, getSiteID());
        } else {
            sample_tag updateTag = new sample_tag();
            updateTag.setLocationID(getLocID());
            updateTag.setEventID(getEventID() + "");
            updateTag.setSiteID(getSiteID());
            updateTag.setUserID(getUserID());
            updateTag.setMobAppID(getParentAppID() + "");
            updateTag.setFieldParamID(getFparamID());
            updateTag.setSampleValue(marker.getTitle());
            updateTag.setSetID(getSetID());
            updateTag.setLatitude(toPosition.latitude);
            updateTag.setLongitude(toPosition.longitude);

            Log.i(TAG, "Tag to Update :" + updateTag);

            updateSampleTag(updateTag);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng fromPosition = marker.getPosition();
        Log.d(getClass().getSimpleName(), "Drag start at: " + fromPosition);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        String add = "";
        final LatLng pos = marker.getPosition();

        //24-May-17 HANDLE : AVOID 2 TIMES TOUCH TO SHOW INFO POPUP FOR KML-LAYER
//        if (lastVisitedpoint != null && lastVisitedpoint.size() > 0 && lastVisitedpoint.get(0) == marker) {
//            invokeTouchEvent = true;
//
//        } else {
//            invokeTouchEvent = false;
//
//        }
        if (getOPERATION() != GlobalStrings.TAG_SAMPLE) {
//
//            add = getAddress(pos);
//            marker.setSnippet(add);
            if (getOPERATION() == GlobalStrings.LOAD_KMZ) {

                if (currentLocationList != null && currentLocationList.size() > 0) {
                    for (Marker markr : currentLocationList) {
                        if (markr.getId().equals(marker.getId())) {
                            marker.showInfoWindow();
                            break;
                        }
                    }
                } else if (!istapEventInside(marker.getPosition())) {
                    marker.showInfoWindow();
                }
            } else {
                marker.showInfoWindow();
            }
        } else {
//            marker.setTitle(getCurrentSampleValue());

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getString(R.string.attention) + "!")
                    .setMessage(getString(R.string.do_want_to_remove_sample_tag) + " '" + marker.getTitle() + "' ?")
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.no), null)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
                            sample_tag deleteTag = new sample_tag();
                            deleteTag.setLocationID(getLocID());
                            deleteTag.setEventID(getEventID() + "");
                            deleteTag.setSiteID(getSiteID());
                            deleteTag.setUserID(getUserID());
                            deleteTag.setMobAppID(getParentAppID() + "");
                            deleteTag.setFieldParamID(getFparamID());
                            deleteTag.setSampleValue(marker.getTitle());
                            deleteTag.setFilePath(null);
                            deleteTag.setSetID(getSetID());
                            deleteTag.setLatitude(pos.latitude);
                            deleteTag.setLongitude(pos.longitude);

                            sd.deleteRow_SampleMapTag(deleteTag);
                            marker.remove();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
//            marker.showInfoWindow();
        }
        return true;
    }


    @Override
    public void onMapLongClick(LatLng lt) {

        if (getOPERATION() == GlobalStrings.LOAD_KMZ) {
            Log.i(TAG, "long press on map.");
            IS_LONG_CLICKKED = true;
            // invokeTouchEvent = false;

            LocationDataSource lds = new LocationDataSource(context);

            IconGenerator mBubbleFactory = new IconGenerator(context);
            mBubbleFactory.setStyle(IconGenerator.STYLE_ORANGE);
            String customName = getSitename() + "_Location" + Util.randInt(1, 9999);

            while (lds.islocationAlreadyExists(customName, Integer.parseInt(getSiteID()))) {
                customName = getSitename() + "_Location" + Util.randInt(1, 9999);
            }

            NewClientLocation obj = new NewClientLocation();
            obj.setSiteId(getSiteID());
            obj.setLocationId(0 + "");
            obj.setLocation(customName);
            obj.setLatitude(lt.latitude + "");
            obj.setLongitude(lt.longitude + "");
            obj.setCreatedBy(getUserID());
            //   if (Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID).equalsIgnoreCase("99999")) {
            obj.setLocationType(getParentAppID() + "");
            //  }
            //obj.setLocationType(getParentAppID()+"");
            //long locallocationID = -(System.currentTimeMillis());
            //  obj.setLocationType(getParentAppID()+"");
            //  obj.setLocationId(locallocationID + "");


            lds.storeLocation(obj, true);

//            if (Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID).equalsIgnoreCase("99999")) {
//                DefaultValueDataSource dv = new DefaultValueDataSource(context);
//                //dv.insertDefaultValueList(respModel.getData());
//                dv.storeMapLocation(obj, true);
//            }

            Bitmap iconBitmap = mBubbleFactory.makeIcon(customName);

            Marker locmarker = googleMap.addMarker(new MarkerOptions().draggable(true).title(customName).
                    icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(lt));
            currentLocationList.add(locmarker);

        } else if (dragMarker != null && getOPERATION() != GlobalStrings.TAG_SAMPLE &&
                getOPERATION() != GlobalStrings.SHOW_TAGGED_LOCATION) {
            // dragMarker.remove();
            dragMarker.setPosition(lt);
            dragMarker.showInfoWindow();
        } else {
            LatLng pos = null;
            @SuppressWarnings("unused")
            String add = null;
            //markerlt = new LatLng(location.getLatitude(), location.getLongitude());
            if (lt != null) {

                if (dragMarker != null && getOPERATION() == GlobalStrings.SHOW_TAGGED_LOCATION && !addNewLocation) {

                    dragMarker.setPosition(lt);

                    pos = dragMarker.getPosition();

                    saveTaggedLocation(pos);

                } else if (getOPERATION() == GlobalStrings.TAG_LOCATION) {

                    new MarkerOptions().position(lt)
                            .title(getLocationName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .draggable(true);

                    IconGenerator mBubbleFactory = new IconGenerator(context);
//                   mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
                    mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);

                    Bitmap iconBitmap = mBubbleFactory.makeIcon(getLocationName());
                    dragMarker = googleMap.addMarker(new MarkerOptions().draggable(true).title(getLocationName())
                            .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(lt));

                    pos = dragMarker.getPosition();
                    //  add = getAddress(pos);
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(pos)      // Sets the center of the map to Mountain View
                            .zoom(19f)       // Sets the zoom
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    dragMarker.setTitle(getLocationName());

                    saveTaggedLocation(dragMarker.getPosition());

                } else if (getOPERATION() == GlobalStrings.TAG_SAMPLE && isAddNewTag) {


                    MarkerOptions dragableMarkerOp = new MarkerOptions().position(lt)
                            .title(getCurrentSampleValue()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .draggable(true);

                    //dragMarker.remove();
//                    dragMarker = googleMap.addMarker(dragableMarkerOp);

                    IconGenerator mBubbleFactory = new IconGenerator(context);
//                    mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
                    mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);

                    Bitmap iconBitmap = mBubbleFactory.makeIcon(getCurrentSampleValue());
                    dragMarker = googleMap.addMarker(new MarkerOptions().draggable(true).title(getCurrentSampleValue()).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(lt));

                    pos = dragMarker.getPosition();
                    //  add = getAddress(pos);
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(pos)      // Sets the center of the map to Mountain View
                            .zoom(19f)       // Sets the zoom
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    dragMarker.setTitle(getCurrentSampleValue());

                    sample_tag newTag = new sample_tag();
                    newTag.setLocationID(getLocID());
                    newTag.setEventID(getEventID() + "");
                    newTag.setSiteID(getSiteID());
                    newTag.setUserID(getUserID());
                    newTag.setMobAppID(getParentAppID() + "");
                    newTag.setFieldParamID(getFparamID());
                    newTag.setSampleValue(getCurrentSampleValue());
                    newTag.setFilePath(null);
                    newTag.setSetID(getSetID());
                    newTag.setLatitude(lt.latitude);
                    newTag.setLongitude(lt.longitude);

                    Log.i(TAG, "New tag to save :" + newTag);
                    saveSampleTag(newTag);
                    isAddNewTag = false;


                } else if (getOPERATION() == GlobalStrings.SHOW_TAGGED_LOCATION && addNewLocation) {
                    LocationDataSource lds = new LocationDataSource(context);

                    IconGenerator mBubbleFactory = new IconGenerator(context);
                    mBubbleFactory.setStyle(IconGenerator.STYLE_ORANGE);
                    String customName = getSitename() + "_Location" + Util.randInt(1, 9999);

                    while (lds.islocationAlreadyExists(customName, Integer.parseInt(getSiteID()))) {
                        customName = getSitename() + "_Location" + Util.randInt(1, 9999);
                    }

                    NewClientLocation obj = new NewClientLocation();
                    obj.setSiteId(getSiteID());
                    obj.setLocationId(0 + "");
                    obj.setLocation(customName);
                    obj.setLatitude(lt.latitude + "");
                    obj.setLongitude(lt.longitude + "");
                    obj.setCreatedBy(getUserID());
                    //    if (Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID).equalsIgnoreCase("99999")) {
                    obj.setLocationType(getParentAppID() + "");
                    //     }
                    //   long locallocationID = -(System.currentTimeMillis());
                    //  obj.setLocationType(getParentAppID()+"");
                    //   obj.setLocationId(locallocationID + "");


                    NewClientLocation addedLocation = lds.storeLocation(obj, true);
                    List<NewClientLocation> addedLocations = new ArrayList<>();
                    addedLocations.add(addedLocation);
                    saveEventLocation(addedLocations);

//                    if (Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID).equalsIgnoreCase("99999")) {
//                        DefaultValueDataSource dv = new DefaultValueDataSource(context);
//                        //dv.insertDefaultValueList(respModel.getData());
//                        dv.storeMapLocation(obj, true);
//
//                    }

                    //   lds.storeMapLocation(obj,true);

                    Bitmap iconBitmap = mBubbleFactory.makeIcon(customName);

                    dragMarker = googleMap.addMarker(new MarkerOptions().draggable(true).title(customName).
                            icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(lt));
                    addNewLocation = false;
                    setTagLocationListeners();

                }

            }

        }

//        Toast.makeText(getApplicationContext(), "Corrected coordinates are saved.", Toast.LENGTH_LONG).show();
    }

    private void saveEventLocation(List<NewClientLocation> addedLocations) {
        SiteDataSource siteDataSource = new SiteDataSource(this);
        if (siteDataSource.isSiteTypeDefault(Integer.parseInt(siteID))) {
            EventLocationDataSource locationDataSource = new EventLocationDataSource(this);
            locationDataSource.insertEventLocations(addedLocations, getEventID() + "",
                    getParentAppID() + "");
        }
    }

    @Override
    public void onMapClick(LatLng ltpoint) {
        Log.i(TAG, "Clicked on map (" + ltpoint.latitude + "," + ltpoint.longitude + ")");
        IS_LONG_CLICKKED = false;
        IS_MARKER_CLICKKED = false;
        //invokeTouchEvent = true;

        if (getOPERATION() == GlobalStrings.LOAD_KMZ) {
            if (infopopup != null && infopopup.isShowing()) {
                infopopup.dismiss();
            }
            istapEventInside(ltpoint);
        }
    }

    @Override
    protected void onStop() {
        badElf.disconnectTracker();
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel();
        super.onStop();
    }

    String getFilePrefix() {
        return filePrefix;
    }

    void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    private void syncAlert() {
//		   SharedPref.putBoolean("RETRACE", true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(getString(R.string.changes_to_forms));
        alertDialogBuilder.setMessage(getString(R.string.download_latest_forms));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                if (CheckNetwork.isInternetAvailable(context)) {
                    Intent metaIntent = new Intent(context, MetaSyncActivity.class);
                    startActivity(metaIntent);
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
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    void beforeSendEmail() {
        procDialog = new ProgressDialog(this);
        procDialog.setIndeterminate(true);
        procDialog.setCancelable(false);
        procDialog.setMessage(getString(R.string.uploading_database_please_wait));

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

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {

        cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastLocation = location;
                        mCurrentLocation = location;
                        zoomToCurLocation();
                    }
                });

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
                        mCurrentLocation = task.getResult();
                        zoomToCurLocation();
                        result = "Location (success): " +
                                mCurrentLocation.getLatitude() +
                                ", " +
                                mCurrentLocation.getLongitude();
                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        result = "Exception thrown: " + exception;

                        if (lastLocation != null) {
                            zoomToCurLocation();
                        }
                    }

                    Log.d(TAG, "getCurrentLocation() result: " + result);
                }
            }));
        } else {
            Log.d(TAG, "Request fine location permission.");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    mCurrentLocation = location;
                    zoomToCurLocation();
                });

        if (extras != null) {
            setParentAppID(extras.getInt("APP_ID", 0));
            setEventID(extras.getInt("EVENT_ID"));
            setPrev_context(extras.getString("PREV_CONTEXT"));
            setOPERATION(extras.getInt("OPERATION"));
            setSitename(extras.getString("SITE_NAME"));

            if (extras.containsKey("CARD")) {
                switchContainer.setVisibility(View.GONE);
            }

            switch (getPrev_context()) {
                case "Location":
                    if (getOPERATION() == GlobalStrings.TAG_LOCATION) { //TAG Location
                        switchContainer.setVisibility(View.GONE);
                        setTagLocationListeners();

                        setLocID(extras.getString("LOC_ID"));
                        setLocationName(extras.getString("LOCATION_NAME"));
                        latitude = extras.getString("LATITUDE");
                        longitude = extras.getString("LONGITUDE");

                        if (!latitude.equals("0.0") || !longitude.equals("0.0")) {
                            Log.i(TAG, "Latitude:" + latitude + " , Longitude:" + longitude);
                            onMapLongClick(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(getString(R.string.attention) + "!")
                                    .setMessage(getString(R.string.long_press_on_map_to_tag_location))
                                    .setCancelable(false)
                                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            zoomToCurLocation();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    } else if (getOPERATION() == GlobalStrings.SHOW_TAGGED_LOCATION) {//We are here from Location Screen
                        if (!addMarkersToMap()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(getString(R.string.attention) + "!")
                                    .setCancelable(false)
                                    .setMessage(getString(R.string.you_have_not_tagged_any_location_yet))
                                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setListeners();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else {
                            setTagLocationListeners();
                            zoomToCurLocation();
                        }
                    } else if (getOPERATION() == GlobalStrings.LOAD_KMZ) {
                        //We are here from Location Screen
                        switchContainer.setVisibility(View.GONE);
                        locationswitcher.setVisibility(View.VISIBLE);

                        try {
                            kmlLayer = addKmlLayer();
                            if (kmlLayer != null) {
                                pList = moveCameraToKml(kmlLayer);
                            }
                            setKmzListeners();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Parse KML Error:" + e.getMessage());
                        }
                    } else {//We r here from draw Screen
                        setListeners();
                    }
                    break;
                case "Draw":
                    enableScreenCapture = extras.getBoolean("ENABLE_SCREEN_CAPTURE", false);
                    setFilePrefix(extras.getString("FILE_NAME_PREFIX"));
                    switchContainer.setVisibility(View.GONE);
                    if (googleMap != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //Consider calling
                            return;
                        }

                        setListeners();
                    }
                    break;
                case "Sample":
                    enableScreenCapture = extras.getBoolean("ENABLE_SCREEN_CAPTURE", false);
                    String sample_prefix = extras.getString("SAMPLE_PREFIX", "sample");
                    setSamplePrefix(sample_prefix);
                    switchContainer.setVisibility(View.GONE);

                    setSiteID(extras.getInt("SITE_ID") + "");
                    setUserID(extras.getInt("USER_ID") + "");
                    setLocID(extras.getString("LOC_ID"));
                    setSitename(extras.getString("SITE_NAME"));
                    setSetID(extras.getString("SET_ID"));
                    setFparamID(extras.getString("PARAM_ID"));
                    setCurrentSampleValue(extras.getString("SAMPLE_VALUE"));

                    SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
//                sd.autoDeleteSampleTagRowData();
//                 sd.autoUpdateSampleTagRowData();
                    ArrayList<sample_tag> tagList = (ArrayList<sample_tag>) sd.getSampleTagListForUser(getLocID(), getSiteID(), getParentAppID() + "", getUserID(), getEventID() + "", getFparamID(), null);
                    int count = tagList.size();
                    //17-Aug-16 check no. of tag  added for fieldParam

                    if (count > 0) {
                        Log.i(TAG, "Multiple tag found for field parameter:" + getFparamID());

                        for (sample_tag tag : tagList) {
                            if (tag.getSetID().equals(getSetID())) {
                                isAddNewTag = false;
                                tag.setSampleValue(getCurrentSampleValue());
                                sd.updateRow_SampleMapTag(tag);
                                break;
                            }
                        }

                        if (isAddNewTag) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(getString(R.string.attention) + "!")
                                    .setMessage(getString(R.string.long_press_on_map_to_tag_sample)
                                            + " '" + getCurrentSampleValue() + "'")
                                    .setCancelable(false)
                                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (googleMap != null) {
                                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                    //Consider calling
                                                    return;
                                                }

                                                setTagLocationListeners();
                                                zoomToCurLocation();
                                            }
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {

                            if (googleMap != null) {
                                if (ActivityCompat.checkSelfPermission(this,
                                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(this,
                                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    //Consider calling
                                    return;
                                }
                                setTagLocationListeners();
                                zoomToCurLocation();
                            }
                        }
                        addSampleTagToMap(tagList);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(getString(R.string.attention) + "!")
                                .setMessage(getString(R.string.long_press_on_map_to_tag_sample)
                                        + " '" + getCurrentSampleValue() + "'")
                                .setCancelable(false)
                                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (googleMap != null) {
                                            if (ActivityCompat.checkSelfPermission(context,
                                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                                    ActivityCompat.checkSelfPermission(context,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                //Consider calling
                                                return;
                                            }

                                            setTagLocationListeners();
                                            zoomToCurLocation();
                                        }
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    break;
            }
            done.setVisibility(View.GONE);
        } else {
            done.setVisibility(View.VISIBLE);
        }

        if (!enableScreenCapture) {
            capture.setVisibility(View.GONE);
        }

        if (toList != null) {
            toList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, LocationActivity.class);
                    i.putExtra("SITE_ID", getSiteID());
                    i.putExtra("SITE_NAME", getSitename());
                    i.putExtra("EVENT_ID", getEventID());
                    i.putExtra("APP_ID", getParentAppID());
                    startActivity(i);
                    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                    finish();
                }
            });
        }

        if (toList_iv != null) {

            toList_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, LocationActivity.class);
                    i.putExtra("SITE_ID", getSiteID());
                    i.putExtra("SITE_NAME", getSitename());
                    i.putExtra("EVENT_ID", getEventID());
                    i.putExtra("APP_ID", getParentAppID());
                    startActivity(i);
                    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                    finish();
                }
            });
        }

        if (getOPERATION() != GlobalStrings.TAG_SAMPLE) {
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

//                    if (getOPERATION() != GlobalStrings.LOAD_KMZ) {

                    boolean goto_form_flag = true;
                    String locName = marker.getTitle();

                    if (gpspoint.size() > 0) {

                        for (Marker mark : gpspoint) {
                            //09-Jun-17 CHECK WHETHER CLICKED POINT IS GPS-POINT OR LOCATION

                            String clickptID = marker.getId();
                            String gpsptID = mark.getId();
                            if (clickptID.equals(gpsptID)) {
                                goto_form_flag = false;
                                break;
                            }
                        }

                        if (goto_form_flag) {
                            LocationDataSource locDS = new LocationDataSource(MapActivity.this);
                            qnopy.com.qnopyandroid.clientmodel.Location loc = locDS.getLocationDetailsByName(locName, getSiteID());

                            if (loc.getLocationID().length() > 0) {
                                Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONID, loc.getLocationID());
                                Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME, locName);

                                FieldDataSource dataSource = new FieldDataSource(context);

                                Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_USERID, getUserID() + "");
                                Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, getDeviceID());


                                onclickLocationItem(loc.getCocflag(), loc.getLocationID(), loc.getLocationName(), loc.getLocationDesc());

                            }
                        }
                    } else {
                        LocationDataSource locDS = new LocationDataSource(MapActivity.this);
                        qnopy.com.qnopyandroid.clientmodel.Location loc = locDS.getLocationDetailsByName(locName, getSiteID());

                        if (loc.getLocationID().length() > 0) {
                            Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONID, loc.getLocationID());
                            Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME, locName);

                            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_USERID, getUserID() + "");
                            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, getDeviceID());


                            onclickLocationItem(loc.getCocflag(), loc.getLocationID(), loc.getLocationName(), loc.getLocationDesc());

                        }
                    }

                }
            });
        }
    }

    private void zoomToCurLocation() {
        if (mCurrentLocation != null) {
            LatLng currentLoc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15);
            googleMap.animateCamera(zoom);
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (getOPERATION() == GlobalStrings.LOAD_KMZ ) {
//            if (infopopup != null && infopopup.isShowing()) {
//                infopopup.dismiss();
//            }
//
////            if (lastVisitedpoint!=null && lastVisitedpoint.size() > 0) {
////                lastVisitedpoint.get(0).remove();
////                Log.i(TAG, "LastVisited marker:" + lastVisitedpoint.get(0).getTitle());
////
////                lastVisitedpoint.clear();
////
////            }
//        }
//
//        return super.dispatchTouchEvent(event);
//
//    }

    private boolean istapEventInside(LatLng ltpoint) {

//        Projection pp = googleMap.getProjection();
//        LatLng point = pp.fromScreenLocation(xypoint);
        boolean isInside = false;
        if (pList != null && pList.size() > 0) {
            outerloop:
            for (Placemark p : pList) {
                ArrayList<GeometryObjects> geolist = p.placemarkgeometries;
                if (geolist != null && geolist.size() > 0) {
                    for (GeometryObjects geoitem : geolist) {
                        List<LatLng> cordinates = geoitem.coordinates;

                        //17-May-17 IF POINT INSIDE OVERLAY
                        if (geoitem.geometry instanceof KmlPolygon && PolyUtil.containsLocation(ltpoint, cordinates, false)) {

                            isInside = true;
                            if (infopopup != null && infopopup.isShowing()) {
                                infopopup.dismiss();
                            }
                            if (lastVisitedpoint.size() > 0) {
                                for (Marker mrkr : lastVisitedpoint) {
                                    mrkr.remove();
                                    Log.i(TAG, "LastVisited marker:" + lastVisitedpoint.get(0).getTitle());

                                }
                                lastVisitedpoint.clear();


                            }
//                            final CameraPosition cameraPosition = new CameraPosition.Builder()
//                                    .target(ltpoint)
//                                    .zoom(14f)// Sets the center of the map to Mountain View
//                                    .build();
//                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//

                            loadPopup(p, ltpoint);

                        } else if ((geoitem.geometry instanceof KmlPoint || geoitem.geometry instanceof KmlLineString) &&
                                ((int) getdistance(ltpoint.latitude, ltpoint.longitude, cordinates.get(0).latitude, cordinates.get(0).longitude) < 10)) {
                            isInside = true;
                            Log.i(TAG, "Clicked on:" + geoitem.geometry.toString());
                            if (infopopup != null && infopopup.isShowing()) {
                                infopopup.dismiss();
                            }

                            if (lastVisitedpoint.size() > 0) {
                                for (Marker mrkr : lastVisitedpoint) {
                                    mrkr.remove();
                                    Log.i(TAG, "LastVisited marker:" + lastVisitedpoint.get(0).getTitle());

                                }
                                lastVisitedpoint.clear();
                            }
//                            final CameraPosition cameraPosition = new CameraPosition.Builder()
//                                    .target(ltpoint)
//                                    .zoom(14f)// Sets the center of the map to Mountain View
//                                    .build();
//                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
                            loadPopup(p, ltpoint);
                        }
                    }
                }
            }
        }
        if (!isInside) {

            if (infopopup != null && infopopup.isShowing()) {
                infopopup.dismiss();
            }

            if (lastVisitedpoint.size() > 0) {
                for (Marker mrkr : lastVisitedpoint) {
                    mrkr.remove();
                    Log.i(TAG, "LastVisited marker:" + lastVisitedpoint.get(0).getTitle());

                }
                lastVisitedpoint.clear();
            }
        }
        return isInside;
    }

//
//        public void tapEvent(int x, int y) {
//        Log.d(TAG, String.format("tap event x=%d y=%d", x, y));
//        Point xypoint = new Point(x, y - 130);
//        Projection pp = googleMap.getProjection();
//        LatLng point = pp.fromScreenLocation(xypoint);
//        boolean isInside = false;
//        if (pList != null && pList.size() > 0) {
//            outerloop:
//            for (Placemark p : pList) {
//                ArrayList<GeometryObjects> geolist = p.placemarkgeometries;
//                if (geolist != null && geolist.size() > 0) {
//                    for (GeometryObjects geoitem : geolist) {
//                        List<LatLng> cordinates = geoitem.coordinates;
//                        //// TODO: 17-May-17 IF POINT INSIDE OVERLAY
//                        if (PolyUtil.containsLocation(point, cordinates, false)) {
//
//                            isInside = true;
//
//                            if (lastVisitedpoint.size() > 0) {
//                                lastVisitedpoint.get(0).remove();
//                                Log.i(TAG, "LastVisited marker:" + lastVisitedpoint.get(0).getTitle());
//
//                                lastVisitedpoint.clear();
//
//                            }
//                            Marker visited = googleMap.addMarker(new MarkerOptions()
//                                    .alpha(01f)
//                                    .position(point)
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.details_marker))
//                                    .title(p.name));
//                            lastVisitedpoint.add(visited);
//
//                            loadPopup(p.desc, xypoint);
//
//                        }
//                    }
//                }
//            }
//        }
//        if (!isInside) {
//
//            if (infopopup != null && infopopup.isShowing()) {
//                infopopup.dismiss();
//            }
//
//            if (lastVisitedpoint.size() > 0) {
//                lastVisitedpoint.get(0).remove();
//                Log.i(TAG, "LastVisited marker:" + lastVisitedpoint.get(0).getTitle());
//
//                lastVisitedpoint.clear();
//
//            }
//
//        }
//    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double getdistance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371000; // 3958.75 in miles, 6371 for kilometer and  6371000 meters output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;
        Log.i(TAG, "Distance between 2 points (meters):" + dist);
        return dist; // output distance, in Meters
    }


    private void loadPopup(final Placemark p, final LatLng taplatlng) {


        String text = p.desc;
        final String title = p.name;


        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.kmzviewinfo, null);


        if (screen_density >= 240) {
            POPUP_HEIGHT = 500;
        } else {
            POPUP_HEIGHT = 300;
        }
        infopopup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, POPUP_HEIGHT, false);

        infopopup.setBackgroundDrawable(new ColorDrawable(
                Color.WHITE));
        final Button close = (Button) layout.findViewById(R.id.okbtn);
        WebView info = (WebView) layout.findViewById(R.id.location_details_txt);
        TextView titletxt = (TextView) layout.findViewById(R.id.popuptitletxt);
        titletxt.setText(title);
        info.setVerticalScrollBarEnabled(true);

        Projection projection = googleMap.getProjection();
        final Point anchor = projection.toScreenLocation(taplatlng);
        Marker visited;
        Bitmap anchorIcon;

        if (screen_density >= 240) {
            if (anchor.y <= 350f) {
                anchorIcon = Util.getBitmapFromVectorDrawable(context, R.drawable.anchor_up);
            } else {
                anchorIcon = Util.getBitmapFromVectorDrawable(context, R.drawable.anchor_down);
            }

        } else {
            if (anchor.y <= 230f) {
                anchorIcon = Util.getBitmapFromVectorDrawable(context, R.drawable.anchor_up);
            } else {
                anchorIcon = Util.getBitmapFromVectorDrawable(context, R.drawable.anchor_down);
            }

        }

        visited = googleMap.addMarker(new MarkerOptions()
                .alpha(01f)
                .position(taplatlng)
                .icon(BitmapDescriptorFactory.fromBitmap(anchorIcon))
                .title(title));
        lastVisitedpoint.add(visited);

        layout.post(new Runnable() {
            public void run() {

                Log.i(TAG, "Screen Density:" + screen_density);

                if (screen_density >= 240) {
                    if (anchor.y <= 350f) {
                        Log.i(TAG, "Clicked on map (x,y):" + anchor.x + "," + anchor.y);
                        infopopup.showAtLocation(layout, Gravity.NO_GRAVITY, anchor.x, anchor.y + 260);
                    } else {
                        Log.i(TAG, "Clicked on map (x,y):" + anchor.x + "," + anchor.y);
                        infopopup.showAtLocation(layout, Gravity.NO_GRAVITY, anchor.x, anchor.y - 298);
                    }
                } else {
                    if (anchor.y <= 230f) {
                        Log.i(TAG, "Clicked on map (x,y):" + anchor.x + "," + anchor.y);

                        infopopup.showAtLocation(layout, Gravity.NO_GRAVITY, anchor.x, anchor.y + 130);
                    } else {
                        Log.i(TAG, "Clicked on map (x,y):" + anchor.x + "," + anchor.y);
                        infopopup.showAtLocation(layout, Gravity.NO_GRAVITY, anchor.x, anchor.y - 202);
                    }
                }


            }

        });

        if (text != null && !text.isEmpty()) {
            info.getSettings().setJavaScriptEnabled(true);
//      details.loadDataWithBaseURL("",html, "text/html", "UTF-8", "");
            info.loadData(text, "text/html;charset=utf-8", "UTF-8");
        } else {
            info.setVisibility(View.GONE);
        }

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                infopopup.dismiss();
                if (lastVisitedpoint.size() > 0) {
                    for (Marker mrkr : lastVisitedpoint) {
                        mrkr.remove();
                        Log.i(TAG, "LastVisited marker:" + lastVisitedpoint.get(0).getTitle());
                    }
                    lastVisitedpoint.clear();
                }
            }
        });
    }

    private KmlLayer addKmlLayer() {

        InputStream is = null;
        String kmz_path;
        String kmz_file;
        kmz_path = extras.getString("KMZ_PATH");
        kmz_file = kmz_path + File.separator + "doc.kml";
        // is =getAssets().open("doc1.kml") ;

        //12-Jun-17 TEST BAD KML FILE
        File kmlfile = new File(kmz_file);
        testAndValidate_BadKml(kmlfile);

        KmlLayer kmllayer = null;
        try {
            is = new FileInputStream(kmz_file);
//            kmllayer = new KmlLayer(googleMap, R.raw.doc, getApplicationContext());
            kmllayer = new KmlLayer(googleMap, is, context);
            kmllayer.addLayerToMap();


        } catch (Exception e) {
            e.printStackTrace();
            alertForUnsupportedKml();
        }
        return kmllayer;
    }

    public boolean testAndValidate_BadKml(File kmlfile) {
        try {
            String filepath = kmlfile.getPath();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(kmlfile);
            String attr_name = "xmlns:xsi";
            String attr_value = "http://www.w3.org/2001/XMLSchema-instance";

            // Get the root element
            Element kml = doc.getDocumentElement();

            // Get the staff element , it may not working if tag has spaces, or
            // whatever weird characters in front...it's better to use
            // getElementsByTagName() to get it directly.
            // Node staff = company.getFirstChild();
            // Get the staff element by tag name directly
            // Node staff = doc.getElementsByTagName("kml").item(0);
            // update staff attribute
            NamedNodeMap attr = kml.getAttributes();
            Node nodeAttr = attr.getNamedItem(attr_name);
            if (nodeAttr == null) {
                (kml).setAttributeNS("http://www.w3.org/2000/xmlns/", attr_name, attr_value);
            }
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);

            Log.i(TAG, "testAndValidate_BadKml() Validated kml file");

        } catch (Exception pce) {
            pce.printStackTrace();
            Log.e(TAG, "Error in test and validate kml:" + pce.getMessage());
            return false;
        }
        return true;
    }

    void alertForUnsupportedKml() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(getString(R.string.sorry) + "!");
        alertDialogBuilder
                .setMessage(getString(R.string.this_kml_file_cant_be_processed_check_format));
        // set positive button: Yes message
        alertDialogBuilder.setNeutralButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        mapActivity.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //  return TODO;
            }

            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private ArrayList<Placemark> moveCameraToKml(KmlLayer kmlLayer) {
        ArrayList<Placemark> placemarkList = new ArrayList<>();

        CameraUpdate cameraUpdate = null;

        placemarkList = getGeometry(kmlLayer.getContainers());

        if (placemarkList != null && placemarkList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLngBounds bounds = null;
            String focusDesc = "";
            String focusTitle = "";
            for (Placemark p : placemarkList) {
                ArrayList<GeometryObjects> geolist = p.placemarkgeometries;
                if (geolist != null && geolist.size() > 0) {
                    for (GeometryObjects geoitem : geolist) {
                        List<LatLng> cordinates = geoitem.coordinates;
                        focus_kmzpoint = new LatLng(cordinates.get(0).latitude, cordinates.get(0).longitude);
                        focusDesc = p.desc;
                        focusTitle = p.name;
                        builder.include(focus_kmzpoint);
                    }
                }
            }

            bounds = builder.build();

            if (bounds != null) {
                focus_kmzpoint = bounds.getCenter();

//                Marker marker = mMap.addMarker(new MarkerOptions()
//                        .position(focusLatLng)
//                        .snippet(focusDesc)
//                        .title(focusTitle));
//
//                marker.showInfoWindow();
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 1);

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(focus_kmzpoint, 16f));
            }
        }
        return placemarkList;
    }

    private ArrayList<Placemark> getGeometry(Iterable<KmlContainer> containers) {
//        List<KmlPolygon> polygons = new ArrayList<>();
        ArrayList<Placemark> placemarksList = new ArrayList<>();
        if (containers == null) {
            return placemarksList;
        }

        for (KmlContainer container : containers) {
            // polygons.addAll(getPolygons(container));
            placemarksList.addAll(getGeometryObjectFromContainer(container));
        }

        return placemarksList;
    }

    private ArrayList<Placemark> getGeometryObjectFromContainer(KmlContainer container) {
        ArrayList<Placemark> PlacemarkList = new ArrayList<>();
        if (container == null) {
            return PlacemarkList;
        }
        Iterable<KmlPlacemark> Iterableplacemarks = container.getPlacemarks();
        try {
            if (Iterableplacemarks != null) {
                for (KmlPlacemark kmlPlacemark : Iterableplacemarks) {
                    Placemark placemark = new Placemark();

                    placemark.id = kmlPlacemark.getStyleId();
                    placemark.name = kmlPlacemark.getProperty("name");
                    placemark.desc = kmlPlacemark.getProperty("description");

                    ArrayList<GeometryObjects> geoList = new ArrayList<>();

                    Geometry geometry = kmlPlacemark.getGeometry();

                    if (geometry instanceof KmlMultiGeometry) {
                        Object geometryObj = (Object) geometry.getGeometryObject();

                        ArrayList obj = (ArrayList) geometryObj;
                        if (obj.size() > 0) {
                            for (int i = 0; i < obj.size(); i++) {

                                GeometryObjects geoitem = new GeometryObjects();
                                //POLYGON
                                if (obj.get(i) instanceof KmlPolygon) {
                                    KmlPolygon polygon = (KmlPolygon) obj.get(i);
                                    geoitem.geometry = polygon;
                                    geoitem.coordinates = polygon.getOuterBoundaryCoordinates();
                                    geoList.add(geoitem);
                                }

                                //POINT
                                if (obj.get(i) instanceof KmlPoint) {
                                    KmlPoint point = (KmlPoint) obj.get(i);
                                    geoitem.geometry = point;
                                    List<LatLng> pointcord = new ArrayList<LatLng>();
                                    pointcord.add(point.getGeometryObject());
                                    geoitem.coordinates = pointcord;
                                    geoList.add(geoitem);
                                }
                                //LineString
                                if (obj.get(i) instanceof KmlLineString) {
                                    KmlLineString linestring = (KmlLineString) obj.get(i);
                                    geoitem.geometry = linestring;
                                    geoitem.coordinates = linestring.getGeometryObject();
                                    geoList.add(geoitem);
                                }
                            }
                        }
                    }

                    //POLYGON
                    if (geometry instanceof KmlPolygon) {
                        GeometryObjects geoitem = new GeometryObjects();

                        KmlPolygon polygon = (KmlPolygon) geometry;
                        geoitem.geometry = polygon;
                        geoitem.coordinates = polygon.getOuterBoundaryCoordinates();
                        geoList.add(geoitem);
                    }

//                    //LATLNG
//                    if ( geometry instanceof LatLng ) {
//                        GeometryObjects geoitem = new GeometryObjects();
//
//                        LatLng latlng = (LatLng) geometry;
//                        geoitem.geometry = latlng;
//                        geoitem.coordinates.add(latlng);
//                        geoList.add(geoitem);
//
//                    }
                    //POINT
                    if (geometry instanceof KmlPoint) {
                        GeometryObjects geoitem = new GeometryObjects();

                        KmlPoint point = (KmlPoint) geometry;
                        geoitem.geometry = point;
                        List<LatLng> pointcord = new ArrayList<LatLng>();
                        pointcord.add(point.getGeometryObject());
                        geoitem.coordinates = pointcord;
                        geoList.add(geoitem);
                    }

                    //LineString
                    if (geometry instanceof KmlLineString) {
                        GeometryObjects geoitem = new GeometryObjects();

                        KmlLineString linestring = (KmlLineString) geometry;
                        geoitem.geometry = linestring;
                        geoitem.coordinates = linestring.getGeometryObject();
                        geoList.add(geoitem);
                    }


                    placemark.placemarkgeometries = geoList;
                    Log.i(TAG, "Placemark ID:" + placemark.id + "\n Name:" + placemark.name + "\nGeometry:" + geometry.getGeometryType() + "\nDescription:" + placemark.desc);
                    PlacemarkList.add(placemark);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "getGeometryObjectFromContainer Exception:" + e.getMessage());
        }

        if (container.hasContainers()) {
//            polygons.addAll(getPolygons(container.getContainers()));
            PlacemarkList.addAll(getGeometry(container.getContainers()));
        }

        return PlacemarkList;
    }

    void saveTaggedLocation(LatLng lt) {

        if (googleMap != null) {
            if (dragMarker != null) {
                LocationDataSource lm = new LocationDataSource(context);
                lm.updateLocationLatLong(getLocID() + "", lt.latitude + "", lt.longitude + "");
                Log.i(TAG, "Update Location to :" + lt.latitude + " , " + lt.longitude);
            }
        }
    }

    void saveDraggedLocation(Marker dragmrkr, String siteID) {

        LocationDataSource lm = new LocationDataSource(context);
        qnopy.com.qnopyandroid.clientmodel.Location loc = lm.getLocationDetailsByName(dragmrkr.getTitle(), siteID);

        if (loc.getLocationID() != null) {
            lm.updateLocationLatLong(loc.getLocationID(), dragmrkr.getPosition().latitude + "",
                    dragmrkr.getPosition().longitude + "");
        }

        Log.i(TAG, "Update Location :" + dragmrkr.getTitle() + "  to :"
                + dragmrkr.getPosition().latitude + " , " + dragmrkr.getPosition().longitude);
    }

    void saveSampleTag(sample_tag sTag) {

        if (googleMap != null) {
            if (dragMarker != null) {
                SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
                sd.saveSampleMapTag(sTag);
                Log.i(TAG, "Save Tag:" + sTag.getSampleValue());
            }
        }
    }

    void updateSampleTag(sample_tag sTag) {
        SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
        sd.updateRow_SampleMapTag(sTag);
        Log.i(TAG, "Update Tag:" + sTag.getSampleValue());
    }

    void updateSampleTagFilePath(sample_tag sTag) {
        SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
        sd.updateRow_SampleMapTagFilePath(sTag);
        Log.i(TAG, "Update Tag:" + sTag.getSampleValue());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoWindow = null;
        List<Address> addresses;
        Geocoder geocoder =
                new Geocoder(context, Locale.getDefault());
        try {

            try {
                addresses = geocoder.getFromLocation(lati,
                        lngi, 1);
                address = String.valueOf(addresses.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String addr = address;
            if (addr == null || addr.isEmpty()) {

                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle(getString(R.string.alert))
                        .setMessage(getString(R.string.no_address_found))
                        .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog2, int which) {
                                dialog2.dismiss();
                            }
                        });

                AlertDialog dialog2 = builder2.create();
                dialog2.show();

            } else {
                infoWindow = prepareInfoView(marker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoWindow;
    }

    private View prepareInfoView(final Marker marker) {
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(MapActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        LinearLayout subInfoView = new LinearLayout(MapActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoLat = new TextView(MapActivity.this);
        subInfoLat.setText(address);
        subInfoLat.setTextSize(15);
        subInfoLat.setTextColor(context.getResources().getColor(R.color.color_chooser_black));
        subInfoLat.setTypeface(Typeface.DEFAULT_BOLD);
        subInfoView.addView(subInfoLat);

        infoView.addView(subInfoView);

        return infoView;
    }

    @Override
    public void onLocationDeny() {
        finish();
    }
}