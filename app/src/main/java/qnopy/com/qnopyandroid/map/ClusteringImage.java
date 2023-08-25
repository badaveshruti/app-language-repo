package qnopy.com.qnopyandroid.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.clientmodel.clusterphoto;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

/**
 * Created by QNOPY on 10/30/2017.
 */

public class ClusteringImage extends ProgressDialogActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<clusterphoto>,
        ClusterManager.OnClusterInfoWindowClickListener<clusterphoto>,
        ClusterManager.OnClusterItemClickListener<clusterphoto>,
        ClusterManager.OnClusterItemInfoWindowClickListener<clusterphoto> {

    LatLng markerlt;
    private Random mRandom = new Random(1984);

    GoogleMap mMap;
    Bundle extras;
    Context mapcontext;
    String siteName, locName, locId = null;
    int eventId, mobappId, siteid, userid, setid = 0;
    ImageView imgview;
    FrameLayout frame1;
    ArrayList<GalleryItem> list = new ArrayList<>();
    ArrayList<clusterphoto> clusterlist = new ArrayList<>();
    private ImageView mImageView;
    private int mDimension;
    RadioGroup radiobtnview;
    ActionBar actionBar;
    TextView switch_listbtn;
    private ClusterManager<clusterphoto> mClusterManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmap);

        mapcontext = this;

        getExtraIntent();

        //  listmapbtn = (ImageButton) findViewById(R.id.listmapbtn);

        switch_listbtn = (TextView) findViewById(R.id.tolist);
        radiobtnview = (RadioGroup) findViewById(R.id.radiobtnview);

        // FrameLayout view = (FrameLayout)findViewById(R.id.framelayout);
        //   imgview= (ImageView) findViewById(R.id.imgview);


        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Map");
        }

        setUpMap();

        SiteDataSource sitedatasrc = new SiteDataSource(mapcontext);
        LocationDataSource ld = new LocationDataSource(mapcontext);
        if (siteid != 0) {
            siteName = sitedatasrc.getSiteNamefromID(siteid);
        }
        if (locId != null) {
            locName = ld.getLocationName(locId);
        }

        switch_listbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
            }
        });

        if (radiobtnview != null) {
            radiobtnview.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.gmapview) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    } else if (checkedId == R.id.gsatelliteview) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    }
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                //  Intent returnintent = new Intent();
                //returnintent.putExtra("LISTSTATUS", true);
                //   setResult(m,returnIntent);
//                startActivity(new Intent(context, CityDataActivity.class));
                finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap)).getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        if (mMap != null) {
            return;
        }
        mMap = map;

        AttachmentDataSource ads = new AttachmentDataSource(mapcontext);

        list = ads.getCardAttachmentDataListFromDB(AttachmentDataSource.DataForSync.DataNotSynced, siteid + "",
                eventId + "", locId + "", siteName, locName, mobappId + "");

        for (GalleryItem galleryImage : list) {
            clusterphoto clusterphoto = new clusterphoto();

            if (galleryImage.getLatitude() != 0 && galleryImage.getLongitude() > 0) {
                LatLng l = new LatLng(galleryImage.getLatitude(), galleryImage.getLongitude());

                String filepath = galleryImage.getFileLocation();

                if (filepath != null) {
                    final File file = new File(filepath);

                    Uri fileUri = Uri.fromFile(new File(filepath));

                    Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(file));

                    if (bitmap == null)
                        bitmap = uriToBitmap(fileUri);

                    BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

                    clusterphoto.setProfilePhoto(drawable);
                } else {
                    clusterphoto.setProfilePhoto(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_24dp));
                }
                clusterphoto.setmPosition(l);
                clusterlist.add(clusterphoto);
            }
        }

        startDemo();
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private void getExtraIntent() {
        extras = getIntent().getExtras();
        siteid = extras.getInt("SITEID");
        locId = extras.getString("LOCID");
        eventId = extras.getInt("EVENTID");
        mobappId = extras.getInt("MOBAPPID");
        setid = extras.getInt("SETID");
        userid = extras.getInt("USERID");
    }

    @Override
    public boolean onClusterItemClick(final clusterphoto clusterphoto) {

       /* mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.custom_windowlayout, null);

                // Set desired height and width
                v.setLayoutParams(new RelativeLayout.LayoutParams(1000, 500));

                ImageView id = (ImageView) v.findViewById(R.id.imgphoto);
                id.setImageDrawable(clusterphoto.getProfilePhoto());

                return v;
*/



               /*  LinearLayout infoView = new LinearLayout(mapcontext);
                LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                infoView.setOrientation(LinearLayout.HORIZONTAL);
                infoView.setLayoutParams(infoViewParams);

                LinearLayout subInfoView = new LinearLayout(mapcontext);
                LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                subInfoView.setOrientation(LinearLayout.VERTICAL);
                subInfoView.setLayoutParams(subInfoViewParams);

                ImageView subInfoLat = new ImageView(mapcontext);
                subInfoLat.setScaleType(ImageView.ScaleType.FIT_XY);
                subInfoLat.setImageDrawable(clusterphoto.getProfilePhoto());

                subInfoView.addView(subInfoLat);

                infoView.addView(subInfoView);


                return infoView;*/
        //   }


        //});
        return false;
    }


    @Override
    public void onClusterItemInfoWindowClick(clusterphoto galleryItem) {

    }

    @Override
    public void onClusterInfoWindowClick(Cluster<clusterphoto> cluster) {

    }


    private class galleryClusterRenderer extends DefaultClusterRenderer<clusterphoto> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public galleryClusterRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.photomap_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            // mImageView.setImageDrawable(getResources().getDrawable(R.drawable.qnopy_icon));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(clusterphoto item, MarkerOptions markerOptions) {

            Drawable d = item.getProfilePhoto();

            mClusterImageView.setImageDrawable(item.getProfilePhoto());
            mImageView.setImageDrawable(item.getProfilePhoto());
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.name);

          /*  File imagefile = new File(item.getFileLocation());
            if (imagefile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());

                mImageView.setImageBitmap(myBitmap);

                // mImageView.setImageResource(galleryItem.getFileLocation());
                Bitmap icon = mIconGenerator.makeIcon();
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(imagefile.getName());
            }*/
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<clusterphoto> cluster, MarkerOptions markerOptions) {
            GalleryItem galleryItem = null;
            clusterphoto clusterphoto = null;
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;


           /* for(int i=0;i<list.size();i++)
            {
                galleryItem=new galleryItem();
                galleryItem=list.get(i);

                LatLng latLng=new LatLng(galleryItem.getLatitude(),galleryItem.getLongitude());
                Bitmap myBitmap = BitmapFactory.decodeFile(galleryItem.getFileLocation());

                Drawable drawable = new BitmapDrawable(getResources(), myBitmap);

                drawable.setBounds(0, 0, width, height);

                clusterphoto.setProfilePhoto(drawable);
                clusterphoto.setmPosition(latLng);

            }*/


            for (clusterphoto p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4)
                    break;
                //  drawable.setBounds(0, 0, width, height);
                profilePhotos.add(p.getProfilePhoto());
            }
            for (int i = 0; i < profilePhotos.size(); i++) {
                mClusterImageView.setImageDrawable(profilePhotos.get(i));
            }

            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            // markerOptions.visible(true);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<clusterphoto> cluster) {
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<clusterphoto> cluster) {
        String firstName = cluster.getItems().iterator().next().name;
        //  Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            //  mMap.setLatLngBoundsForCameraTarget(bounds);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
            //  mMap.setMaxZoomPreference(70f);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void startDemo() {

        Location location = getLastKnownLocation();
        if (location != null) {
            //    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 5f));
        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                float maxzoom = 50f;
                if (cameraPosition.zoom > maxzoom) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(maxzoom));
                }
            }
        });

        mClusterManager = new ClusterManager<clusterphoto>(mapcontext, mMap);
        mClusterManager.setRenderer(new galleryClusterRenderer());
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        //  mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void addItems() {
        for (int i = 0; i < clusterlist.size(); i++) {
            clusterphoto clusterphoto = clusterlist.get(i);
            mClusterManager.addItem(clusterphoto);
        }
    }
}