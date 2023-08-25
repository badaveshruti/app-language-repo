package qnopy.com.qnopyandroid.ui.activity;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.map.GetNearbyPlacesData;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class MapForSiteActivity extends ProgressDialogActivity implements
        OnMapReadyCallback, GoogleMap.InfoWindowAdapter, CustomAlert.LocationServiceAlertListener {

    private static final String TAG = "MapForSiteActivity";
    Location mLastLocation, mCurrentLocation;
    LocationManager mLocationManager;
    GoogleMap googleMap;
    private int PROXIMITY_RADIUS = 30000;
    static String operation = "nearby";
    static String prev_context = null;

    protected double latitude, longitude;
    protected boolean gps_enabled = false, network_enabled = false;
    Context mContext;
    Marker mCurrLocationMarker;
    TextView currentAddressTV;
    Bundle extras;
    Button nearBy_btn;
    LinearLayout nearby_container_ll;
    private FusedLocationProviderClient fusedLocationClient;
    private MarkerOptions curMarkerOptions;
    private volatile CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_for_site);

        mContext = this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);//(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.qnopy_icon);
        }

        extras = getIntent().getExtras();

        currentAddressTV = findViewById(R.id.site_address);
        nearBy_btn = findViewById(R.id.nearby_hospitals_btn);
        nearby_container_ll = findViewById(R.id.nearby_container);
        nearBy_btn.setEnabled(false);
        nearBy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetwork.isInternetAvailable(mContext)) {
                    getNearByHospitals();
                } else {
                    Toast.makeText(mContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                setUpUi();
        } else
            setUpUi();
    }

    @Override
    protected void onStop() {
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel();
        super.onStop();
    }

    private void setUpUi() {

        MapsInitializer.initialize(getApplicationContext());
        addGoogleMap();

        final String username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
        boolean SHOW_ALERT = Util.getSharedPreferencesProperty(mContext,
                GlobalStrings.SHOW_HOSPITAL_ALERT_FOR_FIRSTTIME + username) == null;

        if (SHOW_ALERT) {

            AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
            builder2.setCancelable(false);

            builder2.setTitle(getString(R.string.alert))
                    .setMessage(getString(R.string.hospital_alert_msg_2))
                    .setNeutralButton(getString(R.string.accept_upper_case), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog23, int which) {
                            Util.setSharedPreferencesProperty(mContext,
                                    GlobalStrings.SHOW_HOSPITAL_ALERT_FOR_FIRSTTIME
                                            + username, "ACCEPTED BY USER " + username);
                            dialog23.dismiss();
                        }
                    });

            final AlertDialog dialog2 = builder2.create();

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setCancelable(false);
            builder.setTitle(getString(R.string.alert))

                    .setMessage(getString(R.string.hospital_alert_msg_1))
                    .setNeutralButton(getString(R.string.accept_upper_case), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog2.show();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void addGoogleMap() {
        SupportMapFragment mFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mFragment != null) {
            mFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpUi();
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("MissingPermission")
    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Location wasn't found, check the next most accurate place for the current location
            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                // Finds a provider that matches the criteria
                String provider = lm.getBestProvider(criteria, true);
                // Use the provider to get the last known location
                myLocation = lm.getLastKnownLocation(provider);
            }
        }

        return myLocation;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "No Address found";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Cannot get Address!");
        }
        return strAdd;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap = gMap;

        //Initialize Google Play Services
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        requestCurrentLocation();

        if (extras != null) {
            prev_context = extras.getString("PREV_CONTEXT");
            if (extras.containsKey("OPERATION")) {
                operation = extras.getString("OPERATION");

                if (operation.equals("nearby")) {
                    setNearBy(View.VISIBLE);
                } else {
                    setNearBy(View.GONE);
                }
            }
        }

        //04-Feb-17
        googleMap.setInfoWindowAdapter(this);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude));
                startActivity(intent);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {

/*        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastLocation = location;
                        animateCameraZoom(location.getLatitude(), location.getLongitude());
                    }
                });*/

        cancellationTokenSource = new CancellationTokenSource();

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

                        try {
                            animateCameraZoom(location.getLatitude(), location.getLongitude());
                            nearBy_btn.setEnabled(true);

                            result = "Location (success): " +
                                    location.getLatitude() +
                                    ", " +
                                    location.getLongitude();
                        } catch (Exception e) {
                            e.printStackTrace();
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

    private void animateCameraZoom(double latitude, double longitude) {
        googleMap.clear();
        LatLng currentLoc = new LatLng(latitude, longitude);
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15);
        googleMap.animateCamera(zoom);

        this.latitude = currentLoc.latitude;
        this.longitude = currentLoc.longitude;

        curMarkerOptions = new MarkerOptions();

        curMarkerOptions.position(currentLoc);
        curMarkerOptions.title(getCompleteAddressString(latitude, longitude));
        curMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = googleMap.addMarker(curMarkerOptions);
    }

    private void getNearByHospitals() {

        Log.d("onClick", "Get Nearest Hospitals");
        String Hospital = "hospital";

        googleMap.clear();
        if (curMarkerOptions != null)
            googleMap.addMarker(curMarkerOptions);

        String url = getUrl(latitude, longitude, Hospital);
        LatLng myCuerrentLocation = new LatLng(latitude, longitude);
        Object[] DataTransfer = new Object[3];
        DataTransfer[0] = googleMap;
        DataTransfer[1] = url;
        DataTransfer[2] = myCuerrentLocation;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(mContext);
        getNearbyPlacesData.execute(DataTransfer);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyDdu_kcciyOxu_osApFyEmFwqFizoQQFkE");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    private void setNearBy(int visiblity) {
        nearby_container_ll.setVisibility(visiblity);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
        //return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        View infoWindow = null;
        String addr = marker.getTitle();
        if (addr == null || addr.isEmpty()) {

            AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
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
        return infoWindow;

    }

    private View prepareInfoView(final Marker marker) {
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(MapForSiteActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);


        LinearLayout subInfoView = new LinearLayout(MapForSiteActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoLat = new TextView(MapForSiteActivity.this);
        subInfoLat.setText(marker.getTitle());
        subInfoLat.setTextSize(15);
        Button navigate_btn = new Button(MapForSiteActivity.this);
        navigate_btn.setText("NAVIGATE");
        navigate_btn.setTextColor(getResources().getColor(R.color.white));
        navigate_btn.setBackgroundColor(getResources().getColor(R.color.qnopy_teal));

        subInfoView.addView(subInfoLat);
        subInfoView.addView(navigate_btn);

        infoView.addView(subInfoView);

        return infoView;
    }

    @Override
    public void onLocationDeny() {
        finish();
    }
}
