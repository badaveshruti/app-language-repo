package qnopy.com.qnopyandroid.ui.splitLocationAndMap.fragment;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.MapLocation;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.sample_tag;
import qnopy.com.qnopyandroid.db.EventLocationDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
        CustomAlert.LocationServiceAlertListener {

    private static final String TAG = "MapFragment";
    ArrayList<Marker> gpspoint = new ArrayList<>();
    String address = null;
    Double lati, lngi;
    LocationManager mLocationManager;
    GoogleMap googleMap;

    int parentAppID, eventID;
    String siteID;
    String username;
    String locID;
    String password;
    String userId;
    String deviceId;
    int OPERATION = -1;//0-Show all tagged Locations,1-Tag Location,3-Show Old Map
    boolean IS_LONG_CLICKKED = false, IS_MARKER_CLICKKED = false;
    String sitename = "";
    String prev_context;
    private boolean addNewLocation = true;

    Marker dragMarker;
    protected String latitude, longitude;
    protected boolean gps_enabled = false, network_enabled = false;
    List<String> strList = new ArrayList<>();
    Context context;
    RadioGroup rgViews;
    private BadELFGPSTracker badElf;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handlerForUI = new Handler(Looper.getMainLooper());
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private Location lastLocation;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        ScreenReso application = ((ScreenReso) getActivity().getApplication());
        application.getScreenReso(getActivity());

        setUpUi(view);
    }

    private void setUpUi(View view) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        rgViews = view.findViewById(R.id.rg_views);

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

        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        siteID = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID);
        userId = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        deviceId = Util.getSharedPreferencesProperty(context, GlobalStrings.DEVICEID);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(getActivity(), this);
            } else
                addGoogleMap();
        } else
            addGoogleMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        badElf = new BadELFGPSTracker(getActivity());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CustomAlert.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                addGoogleMap();
            }
        }
    }

    private void addGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    public void addMarkersToMap() {

        executor.execute(() -> {

            handlerForUI.post(() -> {
                if (googleMap != null)
                    googleMap.clear();
            });

//            ClusterManager<Marker> clusterManager = new ClusterManager<Marker>(getActivity(), googleMap);

            gpspoint = new ArrayList<>();

            FieldDataSource fieldDataSource = new FieldDataSource(context);
            MetaDataSource metaDataSource = new MetaDataSource(context);
            String inputType = "GPS";
            ArrayList<String> fpidList = metaDataSource.getFieldParameterIDIfExist(inputType,
                    parentAppID);
            if (fpidList != null && fpidList.size() > 0) {
                int gpsPointCount = 0;

                ArrayList<String> latlngList = fieldDataSource.getStringValueForGPS(eventID,
                        parentAppID + "");
                for (String latlngstring : latlngList) {

                    if (latlngstring != null && latlngstring.contains(",")) {
                        gpsPointCount++;
                        String[] separated = latlngstring.split(",");
                        strList = Arrays.asList(separated);

                        for (int i = 0; i < strList.size(); i++) {
                            lati = Double.valueOf(strList.get(0));
                            lngi = Double.valueOf(strList.get(1));
                        }

                        IconGenerator mBubbleFactory = new IconGenerator(context);

                        mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);

                        Drawable myIcon = VectorDrawableUtils.getDrawable(context, R.drawable.gpsicon_vector);
                        mBubbleFactory.setBackground(myIcon);

                        Bitmap iconBitmap = mBubbleFactory.makeIcon(gpsPointCount + "");
                        int finalGpsPointCount = gpsPointCount;
                        handlerForUI.post(() -> {
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .draggable(false).title(finalGpsPointCount + "")
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                                    .position(new LatLng(lati, lngi)));
                            gpspoint.add(marker);
                        });
                    }
                }
            }

            for (qnopy.com.qnopyandroid.clientmodel.Location item : SplitLocationAndMapActivity.tempLocations) {
                if (item != null && item.getLatitude() != null && item.getLongitude() != null) {
                    if ((!item.getLatitude().equals("0") && !item.getLatitude().equals("0.0")) &&
                            (!item.getLongitude().equals("0") && !item.getLongitude().equals("0.0"))) {
                        LatLng ll = new LatLng(Double.parseDouble(item.getLatitude()),
                                Double.parseDouble(item.getLongitude()));

                        Log.i(TAG, "Marker Ready for Location :" + item.getLocationName()
                                + " with lat:" + ll.latitude + " longitude:" + ll.longitude);

                        IconGenerator mBubbleFactory = new IconGenerator(context);

                        boolean hasData = fieldDataSource.isLocationInStatus(item.getLocationID());
                        if (hasData) {
                            mBubbleFactory.setStyle(IconGenerator.STYLE_GREEN);
                        } else
                            mBubbleFactory.setStyle(IconGenerator.STYLE_RED);

                        Bitmap iconBitmap = mBubbleFactory.makeIcon(item.getLocationName())
                                .copy(Bitmap.Config.ARGB_8888, true);

                        handlerForUI.post(() -> {
                            googleMap.addMarker(new MarkerOptions()
                                    .draggable(false).title(item.getLocationName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(ll));
                        });
                    }
                }
            }
        });
    }

    private List<MapLocation> populateLocation() {

        //RETRIEVING SELECTED ATTRIBUTE USING HASHMAP STORED IN SHARED PREFERENCE
        HashMap<String, String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getActivity()
                .getSharedPreferences("MULTIPLEATTRIBUTE", MODE_PRIVATE);
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

        LocationDataSource location = new LocationDataSource(context);

        List<MapLocation> temp = null;

        if (outputMap.isEmpty()) {
            //11-Apr-16
            temp = location.getLocationForFormOnMap(Integer.parseInt(siteID), parentAppID);
        } else {
            temp = location.getLocationForFormOnMapWithAttribute(Integer.parseInt(siteID),
                    parentAppID, outputMap);

            boolean hasDuplicateLocation = hasDuplicate(temp);
            List<MapLocation> commonAttributeLocation = new ArrayList<MapLocation>();
            Log.e("hasDuplicate", "populateLocation: " + hasDuplicateLocation);

            if (hasDuplicateLocation) {
                commonAttributeLocation = hasDuplicateLocations(temp);
                temp = commonAttributeLocation;
            }
        }

/*        if ((temp == null) || (temp.size() == 0)) {
            Toast.makeText(getActivity(), "Unable to populate locations",
                    Toast.LENGTH_SHORT).show();
        }*/
        return temp;
    }

    private boolean hasDuplicate(List<MapLocation> tempLocations) {

        List<MapLocation> noRepeat = new ArrayList<>();

        boolean isFound = false;
        for (MapLocation event : tempLocations) {
            // check if the event name exists in noRepeat
            for (MapLocation e : noRepeat) {
                if (e.getLocationName().equals(event.getLocationName()) || (e.equals(event))) {

                    Log.e("commonLocation", "populateLocation: " + event.getLocationName());
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);  // todo if need to remove duplicate add here.
            //tempLocations = noRepeat;
        }
        return isFound;
    }

    private List<MapLocation> hasDuplicateLocations(List<MapLocation> tempLocations) {

        List<MapLocation> noRepeat = new ArrayList<>();
        List<MapLocation> commonLocation = new ArrayList<>();

        boolean isFound = false;
        for (MapLocation event : tempLocations) {
            // check if the event name exists in noRepeat
            for (MapLocation e : noRepeat) {
                if (e.getLocationName().equals(event.getLocationName()) || (e.equals(event))) {
                    Log.e("commonLocation", "populateLocation: " + event.getLocationName());
                    commonLocation.add(event);
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);  //if need to remove duplicate add here.
        }
        boolean hasDuplicateLocation = hasDuplicate(commonLocation);
        if (hasDuplicateLocation) {
            commonLocation = hasDuplicateLocations(commonLocation);
        } else {
            return commonLocation;
        }
        return commonLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGoogleMap();
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        badElf.disconnectTracker();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (badElf != null)
            badElf.disconnectTracker();
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel();
        super.onStop();
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
                builder2.setTitle("Alert!")
                        .setMessage("No address found.")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
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
        LinearLayout infoView = new LinearLayout(getActivity());
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        LinearLayout subInfoView = new LinearLayout(getActivity());
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoLat = new TextView(getActivity());
        subInfoLat.setText(address);
        subInfoLat.setTextSize(15);
        subInfoLat.setTextColor(context.getResources().getColor(R.color.color_chooser_black));
        subInfoLat.setTypeface(Typeface.DEFAULT_BOLD);

        subInfoView.addView(subInfoLat);

        infoView.addView(subInfoView);
        return infoView;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i(TAG, "Clicked on map (" + latLng.latitude + "," + latLng.longitude + ")");
        IS_LONG_CLICKKED = false;
        IS_MARKER_CLICKKED = false;
    }

    @Override
    public void onMapLongClick(LatLng lt) {

        LatLng pos = null;
        @SuppressWarnings("unused")
        String add = null;

        if (lt != null) {

            if (dragMarker != null && OPERATION == GlobalStrings.SHOW_TAGGED_LOCATION && !addNewLocation) {
                dragMarker.setPosition(lt);
                pos = dragMarker.getPosition();
                saveTaggedLocation(pos);
            } else if (OPERATION == GlobalStrings.SHOW_TAGGED_LOCATION && addNewLocation) {
                setTagLocationListeners();
                LocationDataSource lds = new LocationDataSource(context);

                IconGenerator mBubbleFactory = new IconGenerator(context);
                mBubbleFactory.setStyle(IconGenerator.STYLE_ORANGE);
                String customName = sitename + "_Location" + Util.randInt(1, 9999);

                while (lds.islocationAlreadyExists(customName, Integer.parseInt(siteID))) {
                    customName = sitename + "_Location" + Util.randInt(1, 9999);
                }

                NewClientLocation obj = new NewClientLocation();
                obj.setSiteId(siteID);
                obj.setLocationId(0 + "");
                obj.setLocation(customName);
                obj.setLatitude(lt.latitude + "");
                obj.setLongitude(lt.longitude + "");
                obj.setCreatedBy(userId);
                obj.setLocationType(parentAppID + "");

                NewClientLocation addedLocation = lds.storeLocation(obj, true);
                List<NewClientLocation> addedLocations = new ArrayList<>();
                addedLocations.add(addedLocation);
                saveEventLocation(addedLocations);

                Bitmap iconBitmap = mBubbleFactory.makeIcon(customName);

                dragMarker = googleMap.addMarker(new MarkerOptions().draggable(true).title(customName).
                        icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(lt));
                addNewLocation = true;
                setTagLocationListeners();
                ((SplitLocationAndMapActivity) getActivity()).populateLocation();
            }
        }
    }

    private void saveEventLocation(List<NewClientLocation> addedLocations) {
        SiteDataSource siteDataSource = new SiteDataSource(getActivity());
        if (siteDataSource.isSiteTypeDefault(Integer.parseInt(siteID))) {
            EventLocationDataSource locationDataSource = new EventLocationDataSource(getActivity());
            locationDataSource.insertEventLocations(addedLocations, eventID + "",
                    parentAppID + "");
        }
    }

    void saveTaggedLocation(LatLng lt) {

        if (googleMap != null) {
            if (dragMarker != null) {
                LocationDataSource lm = new LocationDataSource(context);
                lm.updateLocationLatLong(locID + "", lt.latitude + "", lt.longitude + "");
                Log.i(TAG, "Update Location to :" + lt.latitude + " , " + lt.longitude);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng fromPosition = marker.getPosition();
        Log.d(getClass().getSimpleName(), "Drag start at: " + fromPosition);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // do nothing during drag
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        System.out.println("current1");
        LatLng toPosition = marker.getPosition();
        dragMarker = marker;
        Log.d(getClass().getSimpleName(), "Drag end at: " + toPosition);

        sample_tag updateTag = new sample_tag();
//        updateTag.setLocationID(locID);
        updateTag.setEventID(eventID + "");
        updateTag.setSiteID(siteID);
        updateTag.setUserID(userId);
        updateTag.setMobAppID(parentAppID + "");
//        updateTag.setFieldParamID(fparamID);
        updateTag.setSampleValue(marker.getTitle());
//        updateTag.setSetID(getSetID());
        updateTag.setLatitude(toPosition.latitude);
        updateTag.setLongitude(toPosition.longitude);

        Log.i(TAG, "Tag to Update :" + updateTag);
        updateSampleTag(updateTag);
    }

    void updateSampleTag(sample_tag sTag) {
        SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
        sd.updateRow_SampleMapTag(sTag);
        Log.i(TAG, "Update Tag:" + sTag.getSampleValue());
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;

        SplitLocationAndMapActivity splitActivity = ((SplitLocationAndMapActivity) getActivity());
        if (splitActivity != null) {
            parentAppID = splitActivity.appID;
        }
        eventID = SplitLocationAndMapActivity.eventID;
        prev_context = "Location";
        OPERATION = GlobalStrings.SHOW_TAGGED_LOCATION;
        if (splitActivity != null) {
            sitename = splitActivity.siteName;
        }

        //We are here from Location Screen
        addMarkersToMap();

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        requestCurrentLocation();

        setTagLocationListeners();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

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
                        LocationDataSource locDS = new LocationDataSource(getActivity());
                        qnopy.com.qnopyandroid.clientmodel.Location loc = locDS.getLocationDetailsByName(locName, siteID);

                        if (loc.getLocationID().length() > 0) {
                            Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONID, loc.getLocationID());
                            Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME, locName);

                            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_USERID, userId + "");
                            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, deviceId);

                            onclickLocationItem(loc.getCocflag(), loc.getLocationID(), loc.getLocationName(), loc.getLocationDesc());
                        }
                    }
                } else {
                    LocationDataSource locDS = new LocationDataSource(getActivity());
                    qnopy.com.qnopyandroid.clientmodel.Location loc = locDS.getLocationDetailsByName(locName, siteID);

                    if (loc.getLocationID().length() > 0) {
                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONID, loc.getLocationID());
                        Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME, locName);

                        Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_USERID, userId + "");
                        Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, deviceId);

                        onclickLocationItem(loc.getCocflag(), loc.getLocationID(), loc.getLocationName(), loc.getLocationDesc());
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {

        cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        lastLocation = location;
                        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15);
                        googleMap.animateCamera(zoom);
                    }
                });

        // Request permission
        if (ActivityCompat.checkSelfPermission(
                getActivity(),
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
                        if (location != null) {
                            animateCameraZoom(location.getLatitude(), location.getLongitude());

                            result = "Location (success): " +
                                    location.getLatitude() +
                                    ", " +
                                    location.getLongitude();
                        } else if (lastLocation != null) {
                            animateCameraZoom(lastLocation.getLatitude(), lastLocation.getLongitude());
                        }
                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        result = "Exception thrown: " + exception;

                        if (lastLocation != null) {
                            animateCameraZoom(lastLocation.getLatitude(), lastLocation.getLongitude());
                        }
                    }

                    Log.d(TAG, "getCurrentLocation() result: " + result);
                }
            }));
        } else {
            Log.d(TAG, "Request fine location permission.");
        }
    }

    private void animateCameraZoom(double latitude, double longitude) {
        LatLng currentLoc = new LatLng(latitude, longitude);
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15);
        googleMap.animateCamera(zoom);
    }

/*
    private void setListeners() {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setTrafficEnabled(true);
        }
    }
*/

    @SuppressLint("MissingPermission")
    private void setTagLocationListeners() {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
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

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }

            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
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

    public void onclickLocationItem(String cocflag, String locID, String locName, String locDesc) {

        List<MobileApp> childAppList = null;

        MobileAppDataSource mobSource = new MobileAppDataSource(context);
        String appType = mobSource.getAppType(parentAppID);
        // BoreDefinitionDataSource boreDef = new BoreDefinitionDataSource();
        FieldDataSource fieldData = new FieldDataSource(context);
        //01-Aug-17  LAST VISITED LOCATION & IS COC LOCATION
        Util.setSharedPreferencesProperty(context, eventID + "", locID);
        //01-Aug-17 IS COC LOCATION
        String isCOCLocation;
        if (cocflag == null || cocflag.isEmpty()) {
            isCOCLocation = "0";
        } else {
            isCOCLocation = cocflag;
        }

        Util.setSharedPreferencesProperty(context, locID, isCOCLocation);


        childAppList = mobSource.getChildApps(parentAppID,
                Integer.parseInt(siteID), locID);
        int maxApps = childAppList.size();

        if (maxApps == 0) {
            Toast.makeText(getActivity(),
                    "No Forms for this Location", Toast.LENGTH_SHORT).show();
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
            boolean exists = fieldData.isExistsHeaderData(locID, eventID,
                    Integer.parseInt(userId), id, Integer.parseInt(siteID));
            System.out.println("header data " + exists);
            if (!exists) {
                //invokeWellLogHeader(locID, locName, locDesc);
            } else {
                //invokeWellLog(locID, locName, locDesc);
            }
        } else {

            Log.i(TAG, "Location ID Selected:" + locID);
            Intent LocationDetailIntent = new Intent(getActivity(),
                    LocationDetailActivity.class);

            LocationDetailIntent.putExtra("EVENT_ID", eventID);
            LocationDetailIntent.putExtra("LOCATION_ID", locID);
            LocationDetailIntent.putExtra("APP_ID", parentAppID);
            LocationDetailIntent.putExtra("SITE_ID", Integer.parseInt(siteID));
            LocationDetailIntent.putExtra("SITE_NAME", sitename);
            LocationDetailIntent.putExtra("JUMP_FROM_MAP", "YES");
            LocationDetailIntent.putExtra("LOCATION_NAME", locName);
            LocationDetailIntent.putExtra("LOCATION_DESC", locDesc == null ? "" : locDesc);

            try {
                startActivity(LocationDetailIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in Redirecting to Details Form:" + e.getMessage());
                Toast.makeText(getActivity(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public void onLocationDeny() {
        //no use
    }
}