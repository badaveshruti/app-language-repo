package qnopy.com.qnopyandroid.ui.activity;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class MapDragActivity extends ProgressDialogActivity implements OnMapReadyCallback,
        CustomAlert.LocationServiceAlertListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private MetaData metaData;
    private boolean isSatellite;
    private FloatingActionButton fabSatellite;
    private BadELFGPSTracker badElf;
    private volatile CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_drag);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.capture_location));

        fabSatellite = findViewById(R.id.fabSatellite);

        if (getIntent() != null) {
            metaData = getIntent().getParcelableExtra(GlobalStrings.KEY_META_DATA);
        }

        showToast(getString(R.string.drag_map_to_capture_coordinates), false);

        fabSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSatellite) {
                    isSatellite = false;
                    fabSatellite.setImageDrawable(VectorDrawableUtils
                            .getDrawable(MapDragActivity.this, R.drawable.ic_satellite,
                                    R.color.black_faint));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    isSatellite = true;
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    fabSatellite.setImageDrawable(VectorDrawableUtils
                            .getDrawable(MapDragActivity.this, R.drawable.ic_satellite,
                                    R.color.qnopy_splash));
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                addGoogleMap();
        } else
            addGoogleMap();
    }

    private void addGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_drag_act, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_capture_loc) {
            LatLng latlng = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

            String location = Util.round(latlng.latitude, 5) + "," + Util.round(latlng.longitude, 5);
            Intent intent = new Intent();

            if (metaData != null) {
                intent.putExtra(GlobalStrings.KEY_META_DATA, metaData);
                intent.putExtra(GlobalStrings.FETCHED_LOCATION, location);
            } else
                intent.putExtra(GlobalStrings.FETCHED_LOCATION, latlng);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        badElf = new BadELFGPSTracker(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                addGoogleMap();
            }
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
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel();
        super.onStop();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        requestCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {

/*        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15);
                        mMap.animateCamera(zoom);
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
                        animateCameraZoom(location.getLatitude(), location.getLongitude());

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

    private void animateCameraZoom(double latitude, double longitude) {
        LatLng currentLoc = new LatLng(latitude, longitude);
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15);
        mMap.animateCamera(zoom);
    }

    @Override
    public void onLocationDeny() {
        finish();
    }
}