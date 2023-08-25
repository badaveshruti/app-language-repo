package qnopy.com.qnopyandroid.ui.sitesProjectUser;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class SitesMapActivity extends ProgressDialogActivity implements OnMapReadyCallback, CustomAlert.LocationServiceAlertListener, GoogleMap.OnMarkerClickListener {

    private BadELFGPSTracker badElf;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private GoogleMap mMap;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handlerForUI = new Handler(Looper.getMainLooper());
    private HashMap<Marker, String> markersMap = new HashMap<>();
    private FloatingActionButton fabSatellite;
    private boolean isSatellite;
    private ArrayList<Site> listSite = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_task_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Back to Projects");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                addGoogleMap();
        } else
            addGoogleMap();

        fabSatellite = findViewById(R.id.fabSatellite);

        fabSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSatellite) {
                    isSatellite = false;
                    fabSatellite.setImageDrawable(VectorDrawableUtils
                            .getDrawable(SitesMapActivity.this, R.drawable.ic_satellite,
                                    R.color.black_faint));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    isSatellite = true;
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    fabSatellite.setImageDrawable(VectorDrawableUtils
                            .getDrawable(SitesMapActivity.this, R.drawable.ic_satellite,
                                    R.color.qnopy_splash));
                }
            }
        });
    }

    private void addGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        badElf = new BadELFGPSTracker(this);
        listSite = getSitesList();

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
    public void onStop() {
        badElf.disconnectTracker();
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel();
        super.onStop();
    }

    public ArrayList<Site> getSitesList() {
        int userid = Integer.parseInt(Util.getSharedPreferencesProperty(this,
                GlobalStrings.USERID));

        List<Site> list = new ArrayList<>();
        SiteDataSource siteData = new SiteDataSource(this);
        list = siteData.getAllSitesForUser(userid);

        Collection<Site> listSite = Collections2.filter(list, new Predicate<Site>() {
            @Override
            public boolean apply(Site site) {
                return site.getLatitude() != null && site.getLongitude() != null
                        && site.getLatitude() != 0 && site.getLongitude() != 0;
            }
        });

        return Lists.newArrayList(listSite);
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
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
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMarkerClickListener(this);
        addSitesToMap();
    }

    private void addSitesToMap() {
        executor.execute(() -> {

            handlerForUI.post(() -> {
                if (mMap != null)
                    mMap.clear();
            });

            for (Site site : listSite) {
                LatLng ll = new LatLng(site.getLatitude(), site.getLongitude());

                IconGenerator mBubbleFactory = new IconGenerator(this);

                mBubbleFactory.setStyle(IconGenerator.STYLE_GREEN);

                Bitmap iconBitmap = mBubbleFactory.makeIcon(site.getSiteName())
                        .copy(Bitmap.Config.ARGB_8888, true);

                handlerForUI.post(() -> {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .draggable(false).title(site.getSiteName())
                            .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).position(ll));
                    markersMap.put(marker, site.getSiteID() + "");
                });
            }

            handlerForUI.post(() -> {
                if (listSite.size() > 0) {
                    LatLng currentLoc = new LatLng(listSite.get(0).getLatitude(), listSite.get(0).getLongitude());
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 5);
                    mMap.animateCamera(zoom);
                }
            });
        });
    }

    @Override
    public void onLocationDeny() {
        //no use yet
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (markersMap.containsKey(marker)) {
            String siteId = markersMap.get(marker);
//            Intent intent = new Intent(this, MainDrawerActivity.class);
            Intent intent = new Intent(this, HomeScreenActivity.class);
            intent.putExtra(GlobalStrings.KEY_SITE_ID, siteId);
            startActivity(intent);
        }
        return true;
    }
}