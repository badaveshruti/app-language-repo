package qnopy.com.qnopyandroid.map;

import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class OfflineMapActivity extends ProgressDialogActivity
        // implements OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener
{
//    private static final String TAG = "OfflineMapActivity";
//    MapView mapView;
//    MapboxMap mMap;
//    Location myCurrentLocation;
//    String mapType, msgBoard;
//    boolean setNormalMap;
//    FloatingActionButton floatingActionButton, downloadButton, listButton, float_map_type;
//    FloatingActionsMenu menuMultipleActions;
//    ImageButton toList;
//    protected String latitude, longitude;
//    ProgressDialog procDialog = null;
//
//    //LocationServices locationServices;
//    String mapStyle = Style.MAPBOX_STREETS;
//
//    private static final int PERMISSIONS_LOCATION = 0;
//
//
//    private boolean isEndNotified;
//    private ProgressBar progressBar;
//    ProgressDialog progressD;
//    LinearLayout switchContainer;
//
//
//    // JSON encoding/decoding
//    public final static String JSON_CHARSET = "UTF-8";
//    public final static String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
//
//    private OfflineManager mOfflineManager;
//    private OfflineRegion mOfflineRegion;
//
//    private int regionSelected = -1;
//    ActionBar actionBar;
//    public static Activity offline_mapActivity = null;
//
//
//    int parentAppID, eventID;
//    String siteID, username, locID, password, locationName, UserID, DeviceID;
//    int OPERATION = -1;//0-Show all tagged Locations,1-Tag Location,3-Show Old Map
//    String sitename, prev_context, samplePrefix, setID, fparamID, CurrentSampleValue, filePrefix;
//
//    private boolean isAddNewTag = true, addNewLocation = false, enableScreenCapture = false;
//
//    Bundle extras;
//    Context context;
//
//
//    public String getFilePrefix() {
//        return filePrefix;
//    }
//
//    public void setFilePrefix(String filePrefix) {
//        this.filePrefix = filePrefix;
//    }
//
//    public int getEventID() {
//        return eventID;
//    }
//
//    public void setEventID(int eventID) {
//        this.eventID = eventID;
//    }
//
//    public int getParentAppID() {
//        return parentAppID;
//    }
//
//    public void setParentAppID(int parentAppID) {
//        this.parentAppID = parentAppID;
//    }
//
//    public String getSiteID() {
//        return siteID;
//    }
//
//    public void setSiteID(String siteID) {
//        this.siteID = siteID;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getLocID() {
//        return locID;
//    }
//
//    public void setLocID(String locID) {
//        this.locID = locID;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getUserID() {
//        return UserID;
//    }
//
//    public void setUserID(String userID) {
//        UserID = userID;
//    }
//
//    public String getDeviceID() {
//        return DeviceID;
//    }
//
//    public void setDeviceID(String deviceID) {
//        DeviceID = deviceID;
//    }
//
//    public int getOPERATION() {
//        return OPERATION;
//    }
//
//    public void setOPERATION(int OPERATION) {
//        this.OPERATION = OPERATION;
//    }
//
//    public String getSitename() {
//        return sitename;
//    }
//
//    public void setSitename(String sitename) {
//        this.sitename = sitename;
//    }
//
//    public String getPrev_context() {
//        return prev_context;
//    }
//
//    public void setPrev_context(String prev_context) {
//        this.prev_context = prev_context;
//    }
//
//    public String getSetID() {
//        return setID;
//    }
//
//    public void setSetID(String setID) {
//        this.setID = setID;
//    }
//
//    public String getFparamID() {
//        return fparamID;
//    }
//
//    public void setFparamID(String fparamID) {
//        this.fparamID = fparamID;
//    }
//
//    public String getCurrentSampleValue() {
//        return CurrentSampleValue;
//    }
//
//    public void setCurrentSampleValue(String currentSampleValue) {
//        CurrentSampleValue = currentSampleValue;
//    }
//
//    public boolean isAddNewTag() {
//        return isAddNewTag;
//    }
//
//    public void setAddNewTag(boolean addNewTag) {
//        isAddNewTag = addNewTag;
//    }
//
//    public boolean isAddNewLocation() {
//        return addNewLocation;
//    }
//
//    public void setAddNewLocation(boolean addNewLocation) {
//        this.addNewLocation = addNewLocation;
//    }
//
//    public String getLocationName() {
//        return locationName;
//    }
//
//    public void setLocationName(String locationName) {
//        this.locationName = locationName;
//    }
//
//    public String getSamplePrefix() {
//        return samplePrefix;
//    }
//
//    public void setSamplePrefix(String samplePrefix) {
//        this.samplePrefix = samplePrefix;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Mapbox.getInstance(this, getString(R.string.access_token));
//
//        setContentView(R.layout.activity_offline_map);
//        mapView = (MapView) findViewById(R.id.mapView);
//
//        mapView.onCreate(savedInstanceState);
//
//        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
//        switchContainer = (LinearLayout) findViewById(R.id.switchContainer);
//        toList = (ImageButton) findViewById(R.id.tolist);
//
//        // progressBar.getProgressDrawable().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);
//        context = this;
//        offline_mapActivity = this;
//        extras = getIntent().getExtras();
//
//
//        setUsername(Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME));
//        setPassword(Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD));
//        setSiteID(Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID));
//        setUserID(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
//        setDeviceID(Util.getSharedPreferencesProperty(context, GlobalStrings.DEVICEID));
//
//        if (extras != null) {
//            setPrev_context(extras.getString("PREV_CONTEXT"));
//            setParentAppID(extras.getInt("APP_ID"));
//            setEventID(extras.getInt("EVENT_ID"));
//            setPrev_context(extras.getString("PREV_CONTEXT"));
//            setOPERATION(extras.getInt("OPERATION"));
//            setSitename(extras.getString("SITE_NAME"));
//
//        }
//
//        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
//
//        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
//        downloadButton = (FloatingActionButton) findViewById(R.id.action_download_region);
//        listButton = (FloatingActionButton) findViewById(R.id.action_saved_region);
//        float_map_type = (FloatingActionButton) findViewById(R.id.action_map_type);
//
//
//        View cView = getLayoutInflater().inflate(R.layout.notedialog_actionbar, null);
//
//        actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//            actionBar.setTitle("");
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setCustomView(cView);
//        }
//
//        // set MapBox streets style.
//        mOfflineManager = OfflineManager.getInstance(this);
//        mapType = Util.getSharedPreferencesProperty(OfflineMapActivity.this, "m_Type");
//        setNormalMap = (mapType == null || mapType.equals("normal"));
//
//        if (setNormalMap) {
//            mapStyle = Style.MAPBOX_STREETS;
//            mapView.setStyleUrl(mapStyle);
//
//        } else {
//            mapStyle = Style.SATELLITE_STREETS;
//            mapView.setStyleUrl(mapStyle);
//
//        }
//
//
//        if (toList != null) {
//            toList.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(context, LocationActivity.class);
//                    i.putExtra("SITE_ID", getSiteID());
//                    i.putExtra("SITE_NAME", getSitename());
//                    i.putExtra("EVENT_ID", getEventID());
//                    i.putExtra("APP_ID", getParentAppID());
//                    startActivity(i);
//                    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
//                    finish();
//
//                }
//            });
//        }
//
//        // locationServices =LocationServices. getLocationServices(OfflineMapActivity.this);
//
//        downloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (Connectivity.isConnectedFast(context)) {
//                    menuMultipleActions.collapse();
//                    downloadRegionDialog();
//
//                } else {
//                    Toast.makeText(context, "To download the region connect device to Fast Internet Connection", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//        listButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                menuMultipleActions.collapse();
//                downloadedRegionList();
//            }
//        });
//
//        float_map_type.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                menuMultipleActions.collapse();
//                ShowMaptypeDialog();
//            }
//        });
//
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(MapboxMap mapboxMap) {
//                mMap = mapboxMap;
//
//                // Interact with the map using mapboxMap here
//                mMap.setMyLocationEnabled(true);
//
//                if (extras != null) {
//
//
//                    if (getPrev_context().equals("Location")) {
//
//                        if (getOPERATION() == GlobalStrings.SHOW_TAGGED_LOCATION) {//We are here from Location Screen
//                            if (!addMarkersToMap()) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                                builder.setTitle("Attention!")
//                                        .setCancelable(false)
//                                        .setMessage("You have not tagged any location yet.")
//                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                customZoomMyLocation();
//                                            }
//                                        });
//
//                                android.app.AlertDialog dialog = builder.create();
//                                dialog.show();
//
//                            } else {
//                                customZoomMyLocation();
//                            }
//                        }
//
//                        if (getOPERATION() == GlobalStrings.TAG_LOCATION) { //TAG Location
//                            switchContainer.setVisibility(View.GONE);
//                            setTagLocationListeners();
//
//                            setLocID(extras.getString("LOC_ID"));
//                            setLocationName(extras.getString("LOCATION_NAME"));
//                            latitude = extras.getString("LATITUDE");
//                            longitude = extras.getString("LONGITUDE");
//
//                            if (!latitude.equals("0.0") || !longitude.equals("0.0")) {
//                                Log.i(TAG, "Latitude:" + latitude + " , Longitude:" + longitude);
//                                onMapLongClick(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
//
//                            } else {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                                builder.setTitle("Attention!")
//                                        .setMessage("Long press on map to tag a location.")
//                                        .setCancelable(false)
//                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                customZoomMyLocation();
//                                            }
//                                        });
//                                android.app.AlertDialog dialog = builder.create();
//                                dialog.show();
//                            }
//
//                        }
//                    } else if (getPrev_context().equals("Draw")) {
//                        enableScreenCapture = extras.getBoolean("ENABLE_SCREEN_CAPTURE", false);
//                        setFilePrefix(extras.getString("FILE_NAME_PREFIX"));
//
//                        if (mMap != null) {
//                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
//                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                // TODO: Consider calling
//                                return;
//                            }
//
////                            setListeners();
//                            customZoomMyLocation();
////                    googleMap.setMyLocationEnabled(false);
//                        }
//                    } else if (getPrev_context().equals("Sample")) {
//                        switchContainer.setVisibility(View.GONE);
//
//                        enableScreenCapture = extras.getBoolean("ENABLE_SCREEN_CAPTURE", false);
//                        String sample_prefix = extras.getString("SAMPLE_PREFIX", "sample");
//                        setSamplePrefix(sample_prefix);
//
//                        setSiteID(extras.getInt("SITE_ID") + "");
//                        setUserID(extras.getInt("USER_ID") + "");
//                        setLocID(extras.getString("LOC_ID"));
//                        setSitename(extras.getString("SITE_NAME"));
//                        setSetID(extras.getString("SET_ID"));
//                        setFparamID(extras.getString("PARAM_ID"));
//                        setCurrentSampleValue(extras.getString("SAMPLE_VALUE"));
//
//                        SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
//                        sd.autoDeleteSampleTagRowData();
//                        ArrayList<sample_tag> tagList = (ArrayList<sample_tag>) sd.getSampleTagListForUser(getLocID(), getSiteID(), getParentAppID() + "", getUserID(), getEventID() + "", getFparamID(), getSetID());
//                        int count = tagList.size();
//                        // TODO: 17-Aug-16 check no. of tag  added for fieldParam
//
//                        if (count > 0) {
//                            Log.i(TAG, "Multiple tag found for field parameter:" + getFparamID());
//
//                            for (sample_tag tag : tagList) {
//                                if (tag.getSetID().equals(getSetID())) {
//                                    isAddNewTag = false;
//                                    tag.setSampleValue(getCurrentSampleValue());
//                                    sd.updateRow_SampleMapTag(tag);
//                                    break;
//                                }
//                            }
//
//                            if (isAddNewTag) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                                builder.setTitle("Attention!")
//                                        .setMessage("Long press on map to tag a current sample '" + getCurrentSampleValue() + "'")
//                                        .setCancelable(false)
//                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                if (mMap != null) {
//                                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                                                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                        // TODO: Consider calling
//                                                        return;
//                                                    }
//
//                                                    setSampleTagListeners();
////                                                    zoomMyLocation(mCurrentLocation);
//
//                                                    customZoomMyLocation();
//                                                }
//                                            }
//                                        });
//                                android.app.AlertDialog dialog = builder.create();
//                                dialog.show();
//                            } else {
//                                LatLng zoomToSamplePoint = new LatLng(tagList.get(0).getLatitude(), tagList.get(0).getLongitude());
//                                if (mMap != null) {
//                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                        // TODO: Consider calling
//                                        return;
//                                    }
//
//                                    setSampleTagListeners();
//
//                                    customZoomToLocation(zoomToSamplePoint);
////                                    zoomMyLocation(mCurrentLocation);
//                                }
//
////                    googleMap.setMyLocationEnabled(false);
//                            }
//
//                            addSampleTagToMap(tagList);
//
//
//                        } else {
//                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                            builder.setTitle("Attention!")
//                                    .setMessage("Long press on map to tag a current sample '" + getCurrentSampleValue() + "'")
//                                    .setCancelable(false)
//                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (mMap != null) {
//                                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                                                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                    // TODO: Consider calling
//                                                    return;
//                                                }
//
//                                                setTagLocationListeners();
//                                                customZoomMyLocation();
////                                                zoomMyLocation(mCurrentLocation);
////                    googleMap.setMyLocationEnabled(false);
//                                            }
//                                        }
//                                    });
//                            android.app.AlertDialog dialog = builder.create();
//                            dialog.show();
//                        }
//
//                    }
//                }
//
//
//                mMap.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
//                    @Override
//                    public boolean onInfoWindowClick(@NonNull Marker marker) {
//                        final LatLng pos = marker.getPosition();
//
//                        if (getOPERATION() != GlobalStrings.LOAD_KMZ && getOPERATION() != GlobalStrings.TAG_SAMPLE) {
//
//                            String locName = marker.getTitle();
//                            LocationDataSource locDS = new LocationDataSource(OfflineMapActivity.this);
//                            qnopy.com.qnopyandroid.clientmodel.Location loc = locDS.getLocationDetailsByName(locName, getSiteID());
//                            Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONID, loc.getLocationID());
//                            Util.setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME, locName);
//
//                            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_USERID, getUserID() + "");
//                            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, getDeviceID());
//
//                            onclickLocationItem(loc.getLocationID(), loc.getLocationName(), loc.getLocationDesc());
//
//                        }
//
//                        return true;
//                    }
//                });
//
//            }
//        });
//
//
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                menuMultipleActions.collapse();
//
//                if (mMap != null) {
//                    myCurrentLocation = mMap.getMyLocation();
//                    if (myCurrentLocation != null) {
//
//                        // Move the map camera to where the user location is
////                        CameraPosition cameraPosition = new CameraPosition.Builder()
////                                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
////                                .zoom(17)                   // Sets the zoom
////                                .bearing(180)                // Sets the orientation of the camera to east
////                                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
////                                .build();                    // Creates a CameraPosition from the builder
////                        mMap.setCameraPosition(cameraPosition);
////                        //  mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
////                        CameraUpdate cu=CameraUpdateFactory.newCameraPosition(cameraPosition);
////                       // mMap.easeCamera(cu);
////                        mMap.animateCamera(cu);
//
//                        CameraPosition position = new CameraPosition.Builder()
//                                .target(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude())) // Sets the new camera position
//                                .zoom(17) // Sets the zoom
//                                .bearing(180) // Rotate the camera
//                                .tilt(30) // Set the camera tilt
//                                .build(); // Creates a CameraPosition from the builder
//
//                        mMap.animateCamera(CameraUpdateFactory
//                                .newCameraPosition(position), 7000);
//                        Log.i(TAG, "MyCurrent Position Lat:" + myCurrentLocation.getLatitude() + " Longitude:" + myCurrentLocation.getLongitude());
//
//                    }
//
////                    toggleGps(!mMap.isMyLocationEnabled());
//                }
//            }
//        });
//    }
//
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (menuMultipleActions.isExpanded()) {
//                Rect outRect = new Rect();
//                menuMultipleActions.getGlobalVisibleRect(outRect);
//
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
//                    menuMultipleActions.collapse();
//            }
//
//            try {
//                ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
//                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            } catch (Exception e) {
//                e.printStackTrace();
//
//            }
//
//        }
//
//        return super.dispatchTouchEvent(event);
//    }
//
//    private void setTagLocationListeners() {
//        if (mMap != null) {
////					if(enableScreenCapture == false) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
////					}
//            mMap.getUiSettings().setCompassEnabled(true);
////            mMap.setOnMyLocationButtonClickListener(this);
//            mMap.setOnMarkerClickListener(this);
////            mMap.setOnMarkerDragListener(this);
//            mMap.setOnMapLongClickListener(this);
//            mMap.setOnMapClickListener(this);
////			mMap googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
////            mMap.setTrafficEnabled(true);
//        }
//    }
//
//    private void setSampleTagListeners() {
//        if (mMap != null) {
////					if(enableScreenCapture == false) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
////					}
//            mMap.getUiSettings().setCompassEnabled(true);
////            mMap.setOnMyLocationButtonClickListener(this);
//            mMap.setOnMarkerClickListener(this);
////            mMap.setOnMarkerDragListener(this);
//            mMap.setOnMapLongClickListener(this);
//            mMap.setOnMapClickListener(this);
////			mMap googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
////            mMap.setTrafficEnabled(true);
//        }
//    }
//
//    private void customZoomMyLocation() {
//        myCurrentLocation = mMap.getMyLocation();
//
//        if (myCurrentLocation != null) {
//            CameraPosition position = new CameraPosition.Builder()
//                    .target(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude())) // Sets the new camera position
//                    .zoom(17) // Sets the zoom
//                    .bearing(180) // Rotate the camera
//                    .tilt(30) // Set the camera tilt
//                    .build(); // Creates a CameraPosition from the builder
//
//            mMap.animateCamera(CameraUpdateFactory
//                    .newCameraPosition(position), 7000);
//        }
//
//    }
//
//    private void customZoomToLocation(LatLng zoomTolocation) {
//
//        if (zoomTolocation != null) {
//            CameraPosition position = new CameraPosition.Builder()
//                    .target(new LatLng(zoomTolocation.getLatitude(), zoomTolocation.getLongitude())) // Sets the new camera position
//                    .zoom(17) // Sets the zoom
//                    .bearing(180) // Rotate the camera
//                    .tilt(30) // Set the camera tilt
//                    .build(); // Creates a CameraPosition from the builder
//
//            mMap.animateCamera(CameraUpdateFactory
//                    .newCameraPosition(position), 7000);
//        }
//
//    }
//
//    public void onclickLocationItem(String locID, String locName, String locDesc) {
//
//        List<MobileApp> childAppList = null;
//
//        MobileAppDataSource mobSource = new MobileAppDataSource(context);
//        String appType = mobSource.getAppType(getParentAppID());
//        // BoreDefinitionDataSource boreDef = new BoreDefinitionDataSource();
//        FieldDataSource fieldData = new FieldDataSource(context);
//
//        childAppList = mobSource.getChildApps(Integer.parseInt(getUserID()), getParentAppID(), Integer.parseInt(getSiteID()), -1);
//        int maxApps = childAppList.size();
//
//        if (maxApps == 0) {
//            Toast.makeText(getApplicationContext(),
//                    "No Forms For This Location", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (appType == null) {
//            appType = "std_app";
//        }
//        if (appType.equalsIgnoreCase("well_log")) {
//            MobileApp app = new MobileApp();
//            int id = 0;
//            for (int i = 0; i < childAppList.size(); i++) {
//                app = childAppList.get(i);
//                String appForm = app.getExtField4();
//                if (appForm == null) {
//                    appForm = "Detail";
//                }
//                if (appForm.equalsIgnoreCase("header")) {
//                    id = app.getAppID();
//                    break;
//                }
//            }
//            boolean exists = fieldData.isExistsHeaderData(locID, getEventID(),
//                    Integer.parseInt(getUserID()), id, Integer.parseInt(getSiteID()));
//            System.out.println("header data " + exists);
//            if (!exists) {
//                //invokeWellLogHeader(locID, locName, locDesc);
//            } else {
//                //invokeWellLog(locID, locName, locDesc);
//            }
//        } else {
//
//            Log.i(TAG, "Location ID Selected:" + locID);
//            Intent LocationDetailIntent = new Intent(this,
//                    LocationDetailActivity.class);
//
//            LocationDetailIntent.putExtra("EVENT_ID", getEventID());
//            LocationDetailIntent.putExtra("LOCATION_ID", locID);
//            LocationDetailIntent.putExtra("APP_ID", getParentAppID());
//            LocationDetailIntent.putExtra("SITE_ID", Integer.parseInt(getSiteID()));
//            LocationDetailIntent.putExtra("SITE_NAME", getSitename());
//            LocationDetailIntent.putExtra("JUMP_FROM_MAP", "YES");
//            LocationDetailIntent.putExtra("LOCATION_NAME", locName);
//            LocationDetailIntent.putExtra("LOCATION_DESC", locDesc == null ? "" : locDesc);
//
//
//            try {
//                startActivity(LocationDetailIntent);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, "Error in Redirecting to Details Form:" + e.getMessage());
//                Toast.makeText(this, GlobalStrings.exception_alert, Toast.LENGTH_LONG)
//                        .show();
//            }
//        }
//    }
//
//
//    private boolean addMarkersToMap() {
//        List<mapLocation> list = populateLocation();
//        int tagcount = 0;
//
//        for (mapLocation item : list) {
//
//            if ((!item.getLatitude().equals("0") && !item.getLatitude().equals("0.0")) && (!item.getLongitude().equals("0") && !item.getLongitude().equals("0.0"))) {
//                tagcount++;
//                LatLng ll = new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));
////                BitmapDescriptor bitmapMarker;
////                bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
//                Log.i(TAG, "Marker Ready for Location :" + item.getLocationName() + " with lat:" + ll.getLatitude() + " longitude:" + ll.getLongitude());
////
////                googleMap.addMarker(new MarkerOptions().position(ll).title(item.getLocationName())
////                        .icon(bitmapMarker));
//
////                IconGenerator mBubbleFactory = new IconGenerator(context);
//////                    mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
////
////                if (item.isData()) {
////                    mBubbleFactory.setStyle(IconGenerator.STYLE_GREEN);
////
////                } else
////                    mBubbleFactory.setStyle(IconGenerator.STYLE_RED);
//
////                Bitmap iconBitmap = mBubbleFactory.makeIcon(item.getLocationName());
//
//                IconGenerator mBubbleFactory = new IconGenerator(context);
////                    mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
//
//                if (item.isData()) {
//                    mBubbleFactory.setStyle(IconGenerator.STYLE_GREEN);
//
//                } else
//                    mBubbleFactory.setStyle(IconGenerator.STYLE_RED);
//
//                Bitmap iconBitmap = mBubbleFactory.makeIcon(item.getLocationName());
//                IconFactory iconFactory = IconFactory.getInstance(OfflineMapActivity.this);
//                Icon icon = iconFactory.fromBitmap(iconBitmap);
//                mMap.addMarker(new MarkerViewOptions()
//
//                        .position(ll)
//                        .icon(icon)
//                        .title(item.getLocationName())
//                        .infoWindowAnchor(0.5f, 0.5f));
//
//            }
//        }
//        return tagcount > 0;
//
//
//    }
//
//
//    private List<mapLocation> populateLocation() {
//
//        LocationDataSource location = new LocationDataSource(context);
//
//        List<mapLocation> temp = null;
//
//        // TODO: 11-Apr-16
//        temp = location.getLocationForFormOnMap(Integer.parseInt(getSiteID()), getParentAppID());
//
//        if (temp == null || temp.size() < 1) {
//            temp = location.getLocationForSiteonMap(Integer.parseInt(getSiteID()));
//        }
//
//        if ((temp == null) || (temp.size() == 0)) {
//            Toast.makeText(this, "Unable to populate locations",
//                    Toast.LENGTH_SHORT).show();
//        }
//        return temp;
//
//
//    }
//
//    void saveTaggedLocation(LatLng lt) {
//
//
//        if (lt != null) {
//            LocationDataSource lm = new LocationDataSource(context);
//            lm.updateLocationLatLong(getLocID() + "", lt.getLatitude() + "", lt.getLongitude() + "");
//            Log.i(TAG, "Update Location to (latitude,longitude):" + lt.getLatitude() + " , " + lt.getLongitude());
//        }
//
//
//    }
//
//
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//
//    private boolean addSampleTagToMap(ArrayList<sample_tag> dList) {
//
//        int tagcount = 0;
//
//        for (sample_tag item : dList) {
//
//            if ((!item.getLatitude().equals("0") && !item.getLatitude().equals("0.0")) && (!item.getLongitude().equals("0") && !item.getLongitude().equals("0.0"))) {
//                tagcount++;
//                LatLng ll = new LatLng(item.getLatitude(), item.getLongitude());
//                IconGenerator mBubbleFactory = new IconGenerator(context);
////                mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
//                mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);
//                Bitmap iconBitmap = mBubbleFactory.makeIcon(item.getSampleValue());
//                IconFactory iconFactory = IconFactory.getInstance(OfflineMapActivity.this);
//                Icon icon = iconFactory.fromBitmap(iconBitmap);
//                mMap.addMarker(new MarkerViewOptions()
//
//                        .position(ll)
//                        .icon(icon)
//                        .title(item.getSampleValue())
//                        .infoWindowAnchor(0.5f, 0.5f));
//
//            }
//        }
//        return tagcount > 0;
//
//
//    }
//
//
//    private void enableLocation(boolean enabled) {
//        if (enabled) {
//
//            myCurrentLocation = mMap.getMyLocation();
//            if (myCurrentLocation != null) {
//                CameraPosition position = new CameraPosition.Builder()
//                        .target(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude())) // Sets the new camera position
//                        .zoom(17) // Sets the zoom
//                        .bearing(90) // Rotate the camera
//                        .tilt(30) // Set the camera tilt
//                        .build(); // Creates a CameraPosition from the builder
//
//                mMap.animateCamera(CameraUpdateFactory
//                        .newCameraPosition(position), 7000);
//
//            }
//            floatingActionButton.setImageResource(R.mipmap.ic_location_searching_black_24dp);
//        }
//        // Enable or disable the location layer on the map
//        mMap.setMyLocationEnabled(enabled);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSIONS_LOCATION: {
//                if (grantResults.length > 0 &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    enableLocation(true);
//                }
//            }
//        }
//    }
//
//
//    private void downloadRegionDialog() {
//        // Set up download interaction. Display a dialog
//        // when the user clicks download button and require
//        // a user-provided region name
//        AlertDialog.Builder builder = new AlertDialog.Builder(OfflineMapActivity.this);
//
//        final EditText regionNameEdit = new EditText(OfflineMapActivity.this);
//        regionNameEdit.setHint("Enter name");
//
//        // Build the dialog box
//        builder.setTitle("Name new region")
//                .setView(regionNameEdit)
//                .setMessage("Downloads the map region you currently are viewing")
//                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        String regionName = regionNameEdit.getText().toString();
//                        // Require a region name to begin the download.
//                        // If the user-provided string is empty, display
//                        // a toast message and do not begin download.
//                        if (regionName.length() == 0) {
//                            Toast.makeText(OfflineMapActivity.this, "Region name cannot be empty.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Begin download process
//
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(regionNameEdit.getWindowToken(), 0);
//                            downloadRegion(regionName);
//                        }
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//        // Display the dialog
//        builder.show();
//    }
//
//    private void downloadRegion(final String regionName) {
//        // Define offline region parameters, including bounds,
//        // min/max zoom, and metadata
//
//        // Start the progressBar
//        startProgress();
//
//        // Create offline definition using the current
//        // style and boundaries of visible map area
//        String styleURL = mapStyle;
//        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
//        double minZoom = mMap.getCameraPosition().zoom;
//        double maxZoom = mMap.getMaxZoomLevel();
//        float pixelRatio = this.getResources().getDisplayMetrics().density;
//        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
//                styleURL, bounds, minZoom, maxZoom, pixelRatio);
//
//        // Build a JSONObject using the user-defined offline region title,
//        // convert it into string, and use it to create a metadata variable.
//        // The metadata varaible will later be passed to createOfflineRegion()
//        byte[] metadata;
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
//            String json = jsonObject.toString();
//            metadata = json.getBytes(JSON_CHARSET);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "Failed to encode metadata: " + e.getMessage());
//            metadata = null;
//        }
//
//        // Create the offline region and launch the download
//        mOfflineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
//            @Override
//            public void onCreate(OfflineRegion offlineRegion) {
//                Log.d(TAG, "Offline region created: " + regionName);
//                OfflineMapActivity.this.mOfflineRegion = offlineRegion;
//                launchDownload();
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.e(TAG, "Error: " + error);
//            }
//        });
//    }
//
//    private void launchDownload() {
//        // Set up an observer to handle download progress and
//        // notify the user when the region is finished downloading
//        mOfflineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
//            @Override
//            public void onStatusChanged(OfflineRegionStatus status) {
//                // Compute a percentage
//                double percentage = status.getRequiredResourceCount() >= 0 ?
//                        (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
//                        0.0;
//
//                if (status.isComplete()) {
//                    // Download complete
//                    endProgress("Region downloaded successfully.");
//                    return;
//                } else if (status.isRequiredResourceCountPrecise()) {
//                    // Switch to determinate state
//                    setPercentage((int) Math.round(percentage));
//
//                }
//
//                // Log what is being currently downloaded
//                Log.d(TAG, String.format("%s/%s resources; %s bytes downloaded.",
//                        String.valueOf(status.getCompletedResourceCount()),
//                        String.valueOf(status.getRequiredResourceCount()),
//                        String.valueOf(status.getCompletedResourceSize())));
//            }
//
//            @Override
//            public void onError(OfflineRegionError error) {
//                Log.e(TAG, "onError reason: " + error.getReason());
//                Log.e(TAG, "onError message: " + error.getMessage());
//            }
//
//            @Override
//            public void mapboxTileCountLimitExceeded(long limit) {
//                Toast.makeText(context, "Mapbox tile count limit exceeded: " + limit, Toast.LENGTH_LONG).show();
//                endProgress("Mapbox tile count limit exceeded: " + limit);
//
//            }
//        });
//
//        // Change the region state
//        mOfflineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
//    }
//
//
//    private void ShowMaptypeDialog() {
//        final Dialog dialog = new Dialog(OfflineMapActivity.this, android.R.style.Theme_Holo_Light_Dialog);
//        dialog.setContentView(R.layout.custom_radio_dialog);
//        dialog.setCancelable(true);
//        dialog.setTitle("Map Type");
//        // there are a lot of settings, for dialog, check them all out!
//        // set up radiobutton
//        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.rg);
//        final RadioButton rd_normal = (RadioButton) dialog.findViewById(R.id.radioButton_normal);
//        final RadioButton rd_satellite = (RadioButton) dialog.findViewById(R.id.radioButton_satellite);
//
//        mapType = Util.getSharedPreferencesProperty(OfflineMapActivity.this, "m_Type");
//        setNormalMap = (mapType == null || mapType.equals("normal"));
//
//        if (setNormalMap) {
//            rd_normal.setChecked(true);
//            rd_satellite.setChecked(false);
//        } else {
//            rd_normal.setChecked(false);
//            rd_satellite.setChecked(true);
//        }
//
//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                switch (i) {
//                    case R.id.radioButton_normal:
//                        mapStyle = Style.MAPBOX_STREETS;
//                        mapView.setStyleUrl(mapStyle);
//                        rd_satellite.setChecked(false);
//                        dialog.dismiss();
//                        Util.setSharedPreferencesProperty(OfflineMapActivity.this, "m_Type", "normal");
//                        break;
//                    case R.id.radioButton_satellite:
//                        mapStyle = Style.SATELLITE_STREETS;
//                        mapView.setStyleUrl(mapStyle);
//                        rd_normal.setChecked(false);
//                        dialog.dismiss();
//                        Util.setSharedPreferencesProperty(OfflineMapActivity.this, "m_Type", "satellite");
//                        break;
//                }
//            }
//        });
//
//        dialog.show();
//    }
//
//    private void downloadedRegionList() {
//        // Build a region list when the user clicks the list button
//
//        // Reset the region selected int to 0
//
//        // Query the DB asynchronously
//        mOfflineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
//            @Override
//            public void onList(final OfflineRegion[] offlineRegions) {
//                // Check result. If no regions have been
//                // downloaded yet, notify user and return
//                if (offlineRegions == null || offlineRegions.length == 0) {
//                    Toast.makeText(OfflineMapActivity.this, "You have no regions downloaded yet.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Add all of the region names to a list
//                ArrayList<String> offlineRegionsNames = new ArrayList<>();
//                for (OfflineRegion offlineRegion : offlineRegions) {
//                    offlineRegionsNames.add(getRegionName(offlineRegion));
//                }
//                final CharSequence[] items = offlineRegionsNames.toArray(new CharSequence[offlineRegionsNames.size()]);
//
//                // Build a dialog containing the list of regions
//                AlertDialog dialog = new AlertDialog.Builder(OfflineMapActivity.this)
//                        .setTitle("Downloaded Region")
//
//                        .setSingleChoiceItems(items, regionSelected, new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Track which region the user selects
//                                regionSelected = which;
//                            }
//                        })
//                        .setPositiveButton("Navigate To", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                if (regionSelected == -1) {
//                                    Toast.makeText(OfflineMapActivity.this, "Please select the region to navigate.", Toast.LENGTH_LONG).show();
//
//                                } else {
//                                    Toast.makeText(OfflineMapActivity.this, items[regionSelected], Toast.LENGTH_LONG).show();
//
//                                    // Get the region bounds and zoom
//                                    LatLngBounds bounds = ((OfflineTilePyramidRegionDefinition) offlineRegions[regionSelected].getDefinition()).getBounds();
//                                    double regionZoom = ((OfflineTilePyramidRegionDefinition) offlineRegions[regionSelected].getDefinition()).getMinZoom();
//
//                                    // Create new camera position
//                                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                                            .target(bounds.getCenter())
//                                            .zoom(regionZoom)
//                                            .build();
//
//                                    // Move camera to new position
//                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                                }
//
//
//                            }
//                        })
//                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                // Make progressBar indeterminate and
//                                // set it to visible to signal that
//                                // the deletion process has begun
//                                if (regionSelected == -1) {
//                                    Toast.makeText(OfflineMapActivity.this, "Please select the region to delete.", Toast.LENGTH_LONG).show();
//
//                                } else {
//                                    progressBar.setIndeterminate(true);
//                                    progressBar.setVisibility(View.VISIBLE);
//
//                                    // Begin the deletion process
//                                    offlineRegions[regionSelected].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
//                                        @Override
//                                        public void onDelete() {
//                                            // Once the region is deleted, remove the
//                                            // progressBar and display a toast
//                                            progressBar.setVisibility(View.INVISIBLE);
//                                            progressBar.setIndeterminate(false);
//                                            Toast.makeText(OfflineMapActivity.this, "Region deleted", Toast.LENGTH_LONG).show();
//                                        }
//
//                                        @Override
//                                        public void onError(String error) {
//                                            progressBar.setVisibility(View.INVISIBLE);
//                                            progressBar.setIndeterminate(false);
//                                            Log.e(TAG, "Error: " + error);
//                                        }
//                                    });
//                                }
//
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                // When the user cancels, don't do anything.
//                                // The dialog will automatically close
//                            }
//                        }).create();
//                dialog.show();
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.e(TAG, "Error: " + error);
//            }
//        });
//    }
//
//    private String getRegionName(OfflineRegion offlineRegion) {
//        // Get the retion name from the offline region metadata
//        String regionName;
//
//        try {
//            byte[] metadata = offlineRegion.getMetadata();
//            String json = new String(metadata, JSON_CHARSET);
//            JSONObject jsonObject = new JSONObject(json);
//            regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to decode metadata: " + e.getMessage());
//            regionName = "Region " + offlineRegion.getID();
//        }
//        return regionName;
//    }
//
//    // Progress bar methods
//    private void startProgress() {
//
//        // Disable buttons
////        downloadButton.setEnabled(false);
////        listButton.setEnabled(false);
//
//        // Start and show the progress bar
//        isEndNotified = false;
//        progressD = new ProgressDialog(this);
//        progressD.setCancelable(false);
//        progressD.setMessage("Downloading Region");
//        progressD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressD.setIndeterminate(true);
//        progressD.setProgress(0);
//        progressD.setProgressNumberFormat(null);
//        progressD.show();
////        progressBar.setIndeterminate(true);
////        progressBar.setVisibility(View.VISIBLE);
////        progress.setVisibility(View.VISIBLE);
//
//    }
//
//    private void setPercentage(final int percentage) {
//        progressD.setIndeterminate(false);
//        progressD.setProgress(percentage);
////        progress.setText(percentage+"%");
//    }
//
//    private void endProgress(final String message) {
//        // Don't notify more than once
//        if (isEndNotified) return;
//
//        // Enable buttons
////        downloadButton.setEnabled(true);
////        listButton.setEnabled(true);
//
//        // Stop and hide the progress bar
//        isEndNotified = true;
//        progressD.dismiss();
////        progressBar.setIndeterminate(false);
////        progressBar.setVisibility(View.GONE);
////        progress.setVisibility(View.GONE);
////        floatingActionButton.setEnabled(true);
//
//        // Show a toast
//        Toast.makeText(OfflineMapActivity.this, message, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public boolean onMarkerClick(@NonNull final Marker marker) {
//
//        String add = "";
//        final LatLng pos = marker.getPosition();
//
//        if (getOPERATION() != GlobalStrings.TAG_SAMPLE) {
////
////            add = getAddress(pos);
////            marker.setSnippet(add);
//            marker.showInfoWindow(mMap, mapView);
//
//        } else {
////            marker.setTitle(getCurrentSampleValue());
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("Attention!")
//                    .setMessage("Do you want to remove sample tag '" + marker.getTitle() + "' ?")
//                    .setCancelable(false)
//                    .setNegativeButton("No", null)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
//                            sample_tag deleteTag = new sample_tag();
//                            deleteTag.setLocationID(getLocID());
//                            deleteTag.setEventID(getEventID() + "");
//                            deleteTag.setSiteID(getSiteID());
//                            deleteTag.setUserID(getUserID());
//                            deleteTag.setMobAppID(getParentAppID() + "");
//                            deleteTag.setFieldParamID(getFparamID());
//                            deleteTag.setSampleValue(marker.getTitle());
//                            deleteTag.setFilePath(null);
//                            deleteTag.setSetID(getSetID());
//                            deleteTag.setLatitude(pos.getLatitude());
//                            deleteTag.setLongitude(pos.getLongitude());
//
//                            sd.deleteRow_SampleMapTag(deleteTag);
//                            marker.remove();
//
//
//                        }
//                    });
//            AlertDialog dialog = builder.create();
//            dialog.show();
//            //  marker.showInfoWindow();
//        }
//
//
//        return true;
//    }
//
//    @Override
//    public void onMapLongClick(@NonNull LatLng point) {
//
//        if (point != null) {
//
//            if (getOPERATION() != GlobalStrings.TAG_SAMPLE && !addNewLocation) {
//
//                mMap.clear();
//
//                IconGenerator mBubbleFactory = new IconGenerator(context);
////                    mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
//                mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);
//
//                Bitmap iconBitmap = mBubbleFactory.makeIcon(getLocationName());
//                IconFactory iconFactory = IconFactory.getInstance(OfflineMapActivity.this);
//                Icon icon = iconFactory.fromBitmap(iconBitmap);
//                Marker dragMarker = mMap.addMarker(new MarkerViewOptions()
//
//                        .position(point)
//                        .icon(icon)
//                        .title(getLocationName())
//                        .infoWindowAnchor(0.5f, 0.5f));
//
//                dragMarker.setTitle(getLocationName());
//                customZoomToLocation(point);
//                saveTaggedLocation(dragMarker.getPosition());
//
//            } else if (getOPERATION() == GlobalStrings.TAG_SAMPLE) {
//
//                if (isAddNewTag) {
//
//                    IconGenerator mBubbleFactory = new IconGenerator(context);
////                    mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
//                    mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);
//
//                    Bitmap iconBitmap = mBubbleFactory.makeIcon(getCurrentSampleValue());
//                    IconFactory iconFactory = IconFactory.getInstance(OfflineMapActivity.this);
//                    Icon icon = iconFactory.fromBitmap(iconBitmap);
//                    Marker dragMarker = mMap.addMarker(new MarkerViewOptions()
//
//                            .position(point)
//                            .icon(icon)
//                            .title(getCurrentSampleValue())
//                            .infoWindowAnchor(0.5f, 0.5f));
//
//
//                    dragMarker.setTitle(getCurrentSampleValue());
//
//                    sample_tag newTag = new sample_tag();
//                    newTag.setLocationID(getLocID());
//                    newTag.setEventID(getEventID() + "");
//                    newTag.setSiteID(getSiteID());
//                    newTag.setUserID(getUserID());
//                    newTag.setMobAppID(getParentAppID() + "");
//                    newTag.setFieldParamID(getFparamID());
//                    newTag.setSampleValue(getCurrentSampleValue());
//                    newTag.setFilePath(null);
//                    newTag.setSetID(getSetID());
//                    newTag.setLatitude(point.getLatitude());
//                    newTag.setLongitude(point.getLongitude());
//
//                    Log.i(TAG, "New tag to save :" + newTag.getSampleValue());
//                    saveSampleTag(newTag);
//                    isAddNewTag = false;
//
//
//                } else {
//                    //Already Tagged sample so update it now
//
//                    sample_tag updateTag = new sample_tag();
//                    updateTag.setLocationID(getLocID());
//                    updateTag.setEventID(getEventID() + "");
//                    updateTag.setSiteID(getSiteID());
//                    updateTag.setUserID(getUserID());
//                    updateTag.setMobAppID(getParentAppID() + "");
//                    updateTag.setFieldParamID(getFparamID());
//                    updateTag.setSampleValue(getCurrentSampleValue());
//                    updateTag.setSetID(getSetID());
//                    updateTag.setLatitude(point.getLatitude());
//                    updateTag.setLongitude(point.getLongitude());
//
//                    Log.i(TAG, "Tag to Update :value :" + updateTag.getSampleValue() + " ,setID :" + updateTag.getSetID());
//                    if (updateSampleTag(updateTag)) {
//                        mMap.clear();
//                        IconGenerator mBubbleFactory = new IconGenerator(context);
////                    mBubbleFactory.setColor(getResources().getColor(R.color.snackbar_bg));
//                        mBubbleFactory.setStyle(IconGenerator.STYLE_BLUE);
//
//                        Bitmap iconBitmap = mBubbleFactory.makeIcon(getCurrentSampleValue());
//                        IconFactory iconFactory = IconFactory.getInstance(OfflineMapActivity.this);
//                        Icon icon = iconFactory.fromBitmap(iconBitmap);
//                        Marker dragMarker = mMap.addMarker(new MarkerViewOptions()
//
//                                .position(point)
//                                .icon(icon)
//                                .title(getCurrentSampleValue())
//                                .infoWindowAnchor(0.5f, 0.5f));
//
//
//                        dragMarker.setTitle(getCurrentSampleValue());
//                        customZoomToLocation(point);
//                    }
//                }
//
//
//            } else if (getOPERATION() == GlobalStrings.SHOW_TAGGED_LOCATION && addNewLocation) {
//                LocationDataSource lds = new LocationDataSource(context);
//
//                IconGenerator mBubbleFactory = new IconGenerator(context);
//                mBubbleFactory.setStyle(IconGenerator.STYLE_ORANGE);
//                String customName = getSitename() + "_Location" + Util.randInt(1, 9999);
//
//                while (lds.islocationAlreadyExists(customName, Integer.parseInt(getSiteID()))) {
//                    customName = getSitename() + "_Location" + Util.randInt(1, 9999);
//                }
//
//                newClientLocation obj = new newClientLocation();
//                obj.setSiteId(getSiteID());
//                obj.setLocationId(0 + "");
//                obj.setLocation(customName);
//                obj.setLatitude(point.getLatitude() + "");
//                obj.setLongitude(point.getLongitude() + "");
//
//                lds.storeLocation(obj, true);
//
//                Bitmap iconBitmap = mBubbleFactory.makeIcon(customName);
//                IconFactory iconFactory = IconFactory.getInstance(OfflineMapActivity.this);
//                Icon icon = iconFactory.fromBitmap(iconBitmap);
//                mMap.addMarker(new MarkerViewOptions()
//
//                        .position(point)
//                        .icon(icon)
//                        .title(customName)
//                        .infoWindowAnchor(0.5f, 0.5f));
//                addNewLocation = false;
//
//            }
//
//        }
//
//
//    }
//
//    @Override
//    public void onMapClick(@NonNull LatLng point) {
//
//    }
//
//
//    void saveSampleTag(sample_tag sTag) {
//
//        if (mMap != null) {
//
//            SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
//            sd.saveSampleMapTag(sTag);
//            Log.i(TAG, "Save Tag:" + sTag.getSampleValue());
//
//
//        }
//    }
//
//
//    boolean updateSampleTag(sample_tag sTag) {
//
//        boolean updated = false;
//        SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
//        updated = sd.updateRow_SampleMapTag(sTag) > 0;
//        Log.i(TAG, "Update Tag:" + sTag.getSampleValue());
//        return updated;
//    }
//
//    void updateSampleTagFilePath(sample_tag sTag) {
//
//        SampleMapTagDataSource sd = new SampleMapTagDataSource(context);
//        sd.updateRow_SampleMapTagFilePath(sTag);
//        Log.i(TAG, "Update Tag:" + sTag.getSampleValue());
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.map_menu, menu);
//
//        if (extras == null) {
//            menu.findItem(R.id.done).setVisible(false);
//            menu.findItem(R.id.add_location).setVisible(false);
//        } else {
//            if (getOPERATION() == GlobalStrings.TAG_LOCATION) {
//
//                menu.findItem(R.id.done).setVisible(true);
//                menu.findItem(R.id.add_location).setVisible(false);
//
//            }
//            if (getOPERATION() == GlobalStrings.TAG_SAMPLE) {
//                menu.findItem(R.id.add_location).setVisible(false);
//                menu.findItem(R.id.done).setVisible(false);
//
//            }
//            if (getOPERATION() == GlobalStrings.LOAD_GPSTRACK) {
//                menu.findItem(R.id.done).setVisible(true);
//                menu.findItem(R.id.add_location).setVisible(false);
//            }
//
//            if (getOPERATION() == GlobalStrings.LOAD_KMZ) {
//                menu.findItem(R.id.add_location).setVisible(false);
//            }
//
//            if (getPrev_context() != null && (getPrev_context().equals("Draw") ||
//                    getPrev_context().equals("LocationDetail"))) {
//                menu.findItem(R.id.done).setVisible(false);
//                menu.findItem(R.id.action_delete_user).setVisible(false);
//                menu.findItem(R.id.action_delete).setVisible(false);
//                menu.findItem(R.id.send_db).setVisible(false);
//                menu.findItem(R.id.update_apk).setVisible(false);
//                menu.findItem(R.id.filefolder).setVisible(false);
//                menu.findItem(R.id.download_forms).setVisible(false);
//                menu.findItem(R.id.add_location).setVisible(false);
//
//            }
//        }
//
//        return true;
//
//    }
//
//    public static void Logout(final Context context) {
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        builder.setTitle("Alert")
//                .setMessage("Are you sure to Logout?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        Util.setLogout(context);
//                    }
//                })
//                .setNegativeButton("No", null);
//        android.app.AlertDialog dia = builder.create();
//        dia.show();
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        //super.onOptionsItemSelected(item);
//
//        String title = "Erase Data";
//        String msg = "Are you sure you want to erase all the data from this device?";
//
//        String pos = "Yes";
//        String neg = "No";
//        android.app.AlertDialog alert;
//
//        Log.i(TAG, "Item Selected:" + item.getTitle());
//        switch (item.getItemId()) {
//
//            case R.id.done:
////                if (dragMarker != null) {
////                    saveTaggedLocation(dragMarker.getPosition());
////                }
//                this.finish();
//                return true;
//
//            case R.id.add_location:
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                builder.setTitle("Instruction")
//                        .setMessage("Long press on map to add new Location.")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                setTagLocationListeners();
//                                addNewLocation = true;
//
//                            }
//                        })
//                        .setCancelable(false);
//
//                android.app.AlertDialog dialog = builder.create();
//                dialog.show();
//                return true;
//
//
//            case android.R.id.home_selected:
//                finish();
//                return true;
//
//            case R.id.filefolder:
//
//                List<FileFolderItem> list = new FileFolderDataSource(context).getHomeFileFolderItemList(getSiteID() + "");
//                if (list.size() < 1) {
//                    startActivity(new Intent(context, FileFolderSyncActivity.class));
//                } else {
//                    startActivity(new Intent(context, FileFolderMainActivity.class));
//                }
//
//                return true;
//
//            case R.id.action_delete_user:
//                title = "Delete User";
//                msg = "Do you want to Delete the user " + "'"
//                        + this.username + "'" + " ?";
//                pos = "Yes";
//                neg = "No";
//
//                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
//                alertBuilder.setMessage(msg);
//                alertBuilder.setTitle(title);
//                alertBuilder.setPositiveButton(pos, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteUser(context);
//                    }
//                });
//                alertBuilder.setNegativeButton(neg, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                alert = alertBuilder.create();
//                alert.show();
//
//                return true;
//
//            case R.id.update_apk:
//                // TODO: 21-Mar-16  Link to Update app from Play Store
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("market://details?id=com.aqua.fieldbuddy"));
//                startActivity(intent);
////                if (CheckNetwork.isInternetAvailable(objContext)) {
////                    CheckingUpdates check = new CheckingUpdates(LocationActivity.this, getUsername(), getPassword(), 1, version);
////                    check.execute();
////                } else {
////                    Toast.makeText(objContext,GlobalStrings.network_alert,Toast.LENGTH_LONG).show();
////                }
//                return true;
//
//            case R.id.action_delete:
//                FieldDataSource fd = new FieldDataSource(context);
//                AttachmentDataSource attachDataSource = new AttachmentDataSource(context);
//
//                if (fd.isFieldDataAvailableToSync() || attachDataSource.attachmentsAvailableToSync()) {
//                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
//                    builder1.setTitle("Wait!")
//                            .setMessage("Looks like there is un-synced data.Do you want to proceed with reset?")
//                            .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    alertForDeletingData();
//
//                                }
//                            })
//                            .setNegativeButton("Cancel", null);
//
//                    android.app.AlertDialog dialog1 = builder1.create();
//                    dialog1.show();
//
//                } else {
//                    alertForDeletingData();
//                }
//                return true;
//
//            case R.id.logout:
//
//                Logout(context);
//                return true;
//
//            case R.id.download_forms:
//                syncAlert();
//                return true;
//
//            case R.id.send_db:
////                 fsource.resetEmailSendFlag(getEventID());
////                 aSource.resetEmailSendFlagForImage(getEventID());
////                if (CheckNetwork.isInternetAvailable(getApplicationContext())) {
////                    try {
////                        new AsyncTask<Void, Void, String>() {
////
////                            @Override
////                            protected void onPreExecute() {
////                                super.onPreExecute();
////                                beforeSendEmail();
////                            }
////
////                            @Override
////                            protected String doInBackground(Void... params) {
////
////                                int ret = 0;
////                                ret = Util.sendDatabase(context, getUsername());
////                                if (ret == 0) {
////                                    msgBoard = "No Data To Mail";
////                                    publishProgress();
////                                } else if (ret > 0) {
////                                    msgBoard = "Database sent successfully";
////                                }
////                                return null;
////                            }
////
////                            @Override
////                            protected void onProgressUpdate(Void[] values) {
////                                System.out
////                                        .println("Send Data base In Progress");
////                                procDialog.setMessage(msgBoard);
////                            }
////
////                            private void publishProgress() {
////
////                            }
////
////                            @Override
////                            protected void onPostExecute(String fn) {
////                                afterSendEmail();
////                            }
////                        }.execute();
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                } else {
////                    CustomToast.showToast((Activity) context,
////                            GlobalStrings.network_alert, 5);
////                }
////            case R.id.resync_data:
////                fsource.resetDataSyncFlag(getEventID());
////                aSource.resetDataSyncFlagForImage(getEventID());
////                uploadFieldData();
////                return true;
//
//                if (CheckNetwork.isInternetAvailable(getApplicationContext())) {
//                    try {
//                        new AsyncTask<Void, Void, String>() {
//
//                            @Override
//                            protected void onPreExecute() {
//                                super.onPreExecute();
//                                beforeSendEmail();
//
//                            }
//
//                            @Override
//                            protected String doInBackground(Void... params) {
//
//                                // msgBoard="Uploading Database";
//                                String response = null;
//                                AttachmentResponseModel resultModel = null;
//                                MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
//                                String userguid = Util.getSharedPreferencesProperty(context, getUsername());
//                                String filepath = "";
//                                AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(context);
//
//                                String root = Environment.getExternalStorageDirectory().toString();
//                                // String demo_path=root+GlobalStrings.DEMO_IMAGE_PATH;
//                                String db_zipFile = root + GlobalStrings.ZIP_DB_PATH + userguid + ".zip";
//                                String db_zipFilepath = root + GlobalStrings.ZIP_DB_PATH;
//
//                                String dbName = getBaseContextPath(context) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME;
//                                File dbFile = new File(dbName);
//
//                                // TODO: 06-Nov-15 Zip DB File Location
//                                if (Util.createZipFile(dbName, db_zipFilepath, userguid + ".zip")) {
//                                    filepath = db_zipFile;
////                                    dbAttachment.setFileLocation(db_zipFile);
//                                    Log.i(TAG, "DB File Attached:" + db_zipFile);
//
//                                    files.add("password", getPassword());
//                                    files.add("notes", "Database for user: " + getUsername() + " Device :" + DeviceInfo.getDeviceID(context));
//                                    files.add("file", new FileSystemResource(filepath));
//                                    files.add("userGuid", userguid);//demoguid="f8180e4a-3b36-11e5-9708-0ea7cb7cc776"
//
//                                    resultModel = mAquaBlueService.upload_DB_toServer(getResources().getString(R.string.prod_base_uri),
//                                            getResources().getString(R.string.prod_upload_db),
//                                            files);
//
////                                    resultModel = mAquaBlueService.upload_DB_toServer(GlobalStrings.Local_Base_URL,
////                                            getResources().getString(R.string.prod_upload_db),
////                                            files);
//
//                                    if (resultModel != null) {
//                                        if (resultModel.isSuccess()) {
//                                            response = "SUCCESS";
//                                            //Set the DataSyncFlag
//                                            msgBoard = "Database sent successfully.";
//
//                                        } else {
////                                            GlobalStrings.responseMessage = resultModel.getMessage();
//
//
//                                            msgBoard = "Database failed to upload.";
//                                            Log.i(TAG, "Response by server: " + msgBoard);
//
//
//                                        }
//                                    } else {
//
//                                        String msg = "Attachment " + filepath + " Failed to sync.";
//                                        Log.i(TAG, msg);
//
//                                        msgBoard = "Database failed to upload.";
//
//                                    }
//
//
//                                } else {
//
//                                    Log.i(TAG, "Fail to create .zip");
//                                    msgBoard = "Database failed to upload.";
//
//                                }
////                                else {
////                                    filepath=dbName;
////
//////                                    dbAttachment.setFileLocation(dbName);
////                                    Log.i(TAG, "DB FIle Attached:" + dbName);
////                                }
//
//                                //  publishProgress();
//
//
////                                int ret = 0;
////                                ret = Util.sendDatabase(context, getUsername());
////                                if (ret == 0) {
////                                    msgBoard = "No Data To Mail";
////                                    publishProgress();
////                                } else if (ret > 0) {
////                                    msgBoard = "Database sent successfully";
////                                }
//                                return null;
//                            }
//
//                            @Override
//                            protected void onProgressUpdate(Void[] values) {
//                                System.out
//                                        .println("Send Data base In Progress");
//                                procDialog.setMessage(msgBoard);
//                            }
//
//
//                            private void publishProgress() {
//
//                            }
//
//                            @Override
//                            protected void onPostExecute(String fn) {
//                                afterSendEmail();
//                            }
//                        }.execute();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    CustomToast.showToast((Activity) context,
//                            GlobalStrings.network_alert, 5);
//                }
//
//                return true;
//            case R.id.about:
//
//                alertBuilder = new android.app.AlertDialog.Builder(context);
//                alertBuilder.setMessage(Util.getAboutMsg(context));
//                alertBuilder.setTitle("Qnopy");
//                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                alert = alertBuilder.create();
//                alert.show();
//                return true;
//
//            default:
//                return false;
//        }
//
//    }
//
//    private void deleteUser(Context context) {
//        Boolean result = false;
//        String mesg = null;
//        UserDataSource userSource = new UserDataSource(context);
//        result = userSource.deleteUser(getUsername(), getPassword());
//        if (result) {
//            mesg = "User " + "'" + username + "'"
//                    + " Deleted Successfully";
//
//            Util.setLogout(context);
//            finish();
//        } else {
//            mesg = "User Deletion Failed";
//        }
//        Toast.makeText(getApplicationContext(), mesg, Toast.LENGTH_LONG).show();
//
//    }
//
//    void alertForDeletingData() {
//        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
//                context);
//
//        alertDialogBuilder.setTitle(this.getTitle() + " DECISION");
//        alertDialogBuilder
//                .setMessage("Are you sure you want to erase all the data from this device?");
//        // set positive button: Yes message
//        alertDialogBuilder.setPositiveButton(" YES ",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        deleteAllData();
//                    }
//                });
//        // set negative button: No message
//        alertDialogBuilder.setNegativeButton("NO",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // cancel the alert box and put a Toast to the user
//                        dialog.cancel();
//                    }
//                });
//
//        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//    }
//
//    public void deleteAllData() {
//        MetaDataSource md = new MetaDataSource(context);
//        md.ResetAppData();
//        Util.setLogout(context);
//
////        FieldDataSource field = new FieldDataSource(context);
////        AttachmentDataSource attach = new AttachmentDataSource(context);
////        EventDataSource event = new EventDataSource(context);
////        int retField = field.deleteFieldData(getEventID());
////        int retAttach = attach.deleteAttachment(getEventID());
////        SampleMapTagDataSource smTag = new SampleMapTagDataSource(context);
////        smTag.truncate_SampleMapTag();
////        FileFolderDataSource fd = new FileFolderDataSource(context);
////        fd.truncateFileFolder();
////
////        SyncStatusDataSource sd = new SyncStatusDataSource(context);
////        sd.truncateD_SyncStatus();
////        // if ((retAttach > 0 || retField > 0)) {
////        int retEvent = event.deleteEvents(getEventID());
////
////        if (retEvent > 0) {
////            this.finish();
////            LocationActivity.LocActivity.finish();
////            if (getPrev_context().contains("Sample")) {
////                LocationDetailActivity.LocDetailActivity.finish();
////            }
////        }
//        // }
//        CustomToast.showToast((Activity) context, "All Data Deleted Successfully", 5);
//        // locationAdapter.notifyDataSetChanged();
//    }
//
//    void beforeSendEmail() {
//        procDialog = new ProgressDialog(this);
//        procDialog.setIndeterminate(true);
//        procDialog.setCancelable(false);
//        procDialog.setMessage("Uploading Database,Please wait...!");
//
//        procDialog.show();
//    }
//
//    void afterSendEmail() {
//        if ((procDialog != null) && (procDialog.isShowing())) {
//            try {
//                procDialog.dismiss();
//                CustomToast.showToast((Activity) context, msgBoard,
//                        Toast.LENGTH_SHORT);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, "AfterSendEmail:" + e.getLocalizedMessage());
//            }
//        }
//        // uploadFieldData();
//    }
//
//    private void syncAlert() {
////		   SharedPref.putBoolean("RETRACE", true);
//        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
//
//        alertDialogBuilder.setTitle("Changes to forms");
//        alertDialogBuilder.setMessage("Do You Want to download latest forms?");
//        // set positive button: Yes message
//        alertDialogBuilder.setPositiveButton(" YES ", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // go to a new activity of the app
//                if (CheckNetwork.isInternetAvailable(context)) {
//                    Intent metaIntent = new Intent(context, MetaSyncActivity.class);
////                metaIntent.putExtra("USER_NAME", getUsername());
////                metaIntent.putExtra("PASS", getPassword());
//                    startActivity(metaIntent);
//                    finish();
//                } else {
//                    Toast.makeText(context, GlobalStrings.network_alert, Toast.LENGTH_LONG).show();
//                }
//            }
//
//
//        });
//        // set negative button: No message
//        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // cancel the alert box and put a Toast to the user
//                dialog.cancel();
//
//            }
//        });
//        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
//        // show alert
//        alertDialog.show();
//    }
}

