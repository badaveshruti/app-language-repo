package qnopy.com.qnopyandroid.ui.activity;

import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.REQUEST_CODE_NOTE_MEDIA_PICKER;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.REQUEST_CODE_PIX_NOTE_IMAGE_PICKER;
import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.CardAdapter;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.AttachmentData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.map.ClusteringImage;
import qnopy.com.qnopyandroid.sensors.MobileSensor;
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class CardGalleryActivity extends ProgressDialogActivity implements CustomAlert.LocationServiceAlertListener {

    private static final String TAG = "CardGallery";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    Context context;
    public List<String> thumbIds = null;
    String AttachmentNamePrefix = "";

    ProgressDialog procDialog = null;

    public File output_file = null;
    public String thumbFile;
    public String file1000;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_DRAWING = 3;

    private int eventID = 0;
    private String locID = "0";
    private int siteID = 0;
    private int userID = 0;
    private int mobileAppID = 0;
    private int setID = 1;
    private String siteName = "";
    private String locationName = "";
    Bundle extras;
    ActionBar actionBar;
    MobileSensor mSensorTracker;
    TextView switch_mapbtn;
    String filepath = null;
    private Options pickerOptions;
    private int fieldParamId = 0;
    private CustomTextView tvNoPhotoSaved;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private volatile CancellationTokenSource cancellationTokenSource;

    public String getAttachmentNamePrefix() {
        return AttachmentNamePrefix;
    }

    public void setAttachmentNamePrefix(String attachmentNamePrefix) {
        AttachmentNamePrefix = attachmentNamePrefix;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getLocID() {
        return locID;
    }

    public void setLocID(String locID) {
        this.locID = locID;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getMobileAppID() {
        return mobileAppID;
    }

    public void setMobileAppID(int mobileAppID) {
        this.mobileAppID = mobileAppID;
    }

    public int getSetID() {
        return setID;
    }

    public void setSetID(int setID) {
        this.setID = setID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_gallery);
        context = this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        switch_mapbtn = findViewById(R.id.switch_map_tv);
        recyclerView = findViewById(R.id.recyclerView);
        tvNoPhotoSaved = findViewById(R.id.tvNoPhotoSaved);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mSensorTracker = new MobileSensor(context);

        extras = getIntent().getExtras();
        try {
            setUserID(extras.getInt("USER_ID"));
            setSiteID(extras.getInt("SITE_ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setEventID(extras.getInt("EVENT_ID"));

        if (extras.getString("FILEPATH") != null) {
            filepath = extras.getString("FILEPATH");
        }

        String locationID = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONID);
        setLocID(locationID);
        setMobileAppID(extras.getInt("MOBILE_APP_ID"));
        if (extras.containsKey("SET_ID")) {
            setSetID(extras.getInt("SET_ID"));
        }

        if (extras.containsKey(GlobalStrings.KEY_FIELD_PARAM_ID)) {
            fieldParamId = extras.getInt(GlobalStrings.KEY_FIELD_PARAM_ID);
        }

        siteName = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME);
        locationName = new LocationDataSource(context).getLocationName(getLocID());

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        switch_mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photomapintent = new Intent(context, ClusteringImage.class);
                photomapintent.putExtra("SITEID", getSiteID());
                photomapintent.putExtra("EVENTID", getEventID());
                photomapintent.putExtra("LOCID", getLocID());
                photomapintent.putExtra("MOBAPPID", getMobileAppID());
                photomapintent.putExtra("SETID", getSetID());
                photomapintent.putExtra("USERID", getUserID());
                startActivity(photomapintent);

                overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cancellationTokenSource = new CancellationTokenSource();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
/*
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null)
                        lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                });
*/

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
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            result = "Location (success): " +
                                    location.getLatitude() +
                                    ", " +
                                    location.getLongitude();
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

    @Override
    protected void onStop() {
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel();
        super.onStop();
    }

    public ArrayList<GalleryItem> collectData() {
        ArrayList<GalleryItem> list = new ArrayList<>();
        AttachmentDataSource ads = new AttachmentDataSource(context);

        if (fieldParamId == 0) {
            list = ads.getCardAttachmentDataListFromDB(AttachmentDataSource.DataForSync.DataNotSynced, getSiteID() + "",
                    getEventID() + "", getLocID(), siteName, locationName, getMobileAppID() + "");
        } else {
            list = ads.getAttachmentForFieldParam(getSiteID() + "",
                    getEventID() + "", getLocID(), getMobileAppID() + "",
                    fieldParamId + "", getSetID(), siteName, locationName);
        }

        thumbIds = getImageUrl(list);
        //attachID = getAttachID();

        return list;
    }

    public void showData(ArrayList<GalleryItem> list) {
        Collections.sort(list, new CustomComparator());

        if (filepath != null) {
            adapter = new CardAdapter(context, list, filepath);
        } else {
            adapter = new CardAdapter(context, list);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public List<String> getImageUrl(ArrayList<GalleryItem> list) {

        List<String> tempThumbIds = new ArrayList<String>();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                GalleryItem attach = list.get(i);
                tempThumbIds.add(attach.getFileLocation());
                Log.i(TAG, "Image..." + tempThumbIds.get(i));
            }
        }

        return tempThumbIds;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater minflater = getMenuInflater();
        minflater.inflate(R.menu.menu_gallery, menu);
        AppPreferenceDataSource ds = new AppPreferenceDataSource(context);
        //KEY_PROJECT_FILE
        if (ds.isFeatureAvailable(GlobalStrings.KEY_PROJECT_FILE, getUserID())) {
            menu.findItem(R.id.filefolder).setVisible(true);
        } else {
            menu.findItem(R.id.filefolder).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.card_camera:
                setAttachmentNamePrefix(System.currentTimeMillis() + "");//siteName.replace(" ", "_") + "_" + (locationName).replace(" ", "_") + "_"
                handleCamera();
                break;

            case R.id.filefolder:

                List<FileFolderItem> list = new FileFolderDataSource(context).getHomeFileFolderItemList(getSiteID() + "");
                Log.i(TAG, "onOptionsItemSelected() File Folder List Item Count:" + list.size() + "");

                if (list.size() < 1) {
                    startActivity(new Intent(context, FileFolderSyncActivity.class));
                } else {
                    startActivity(new Intent(context, FileFolderMainActivity.class));
                }

                break;

            case android.R.id.home:
//                startActivity(new Intent(context, DashboardActivity.class));
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                getLocation();
        } else
            getLocation();

        mSensorTracker.registerSensorService();
        Log.i(TAG, "Sensor Status- ACCELEROMETER =" + mSensorTracker.isAccelerometerAvailable());
        Log.i(TAG, "Sensor Status- MAGNETOMETER =" + mSensorTracker.isMagnetoMeterAvailable());

        siteName = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME);
        locationName = new LocationDataSource(context).getLocationName(getLocID());
        ArrayList<GalleryItem> list = collectData();

        if (list == null || list.size() < 1) {
            recyclerView.setVisibility(View.GONE);
            tvNoPhotoSaved.setVisibility(View.VISIBLE);

/*            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
            this.finish();*/
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoPhotoSaved.setVisibility(View.GONE);
            showData(list);
        }
    }

    @Override
    protected void onPause() {
        mSensorTracker.unregisterSensorService();
        if (procDialog != null && procDialog.isShowing()) {
            procDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CODE_PIX_NOTE_IMAGE_PICKER && data != null) {
                    ArrayList<String> pathList = data.getStringArrayListExtra((Pix.IMAGE_RESULTS));
                    if (pathList != null) {
                        if (pathList.size() > 0) {

                            if (!new File(pathList.get(0)).exists()) {
                                CustomToast.showToast(this,
                                        getString(R.string.unable_to_show_image_or_may_be), 5);
                                return;
                            }

                            Bitmap bitmap;
                            bitmap = Util.correctBitmapRotation(pathList.get(0));
                            Bitmap cropIMg = Util.cropToSquare(bitmap);
                            Util.saveBitmapToSDCard(cropIMg, output_file,
                                    GlobalStrings.COMPRESSION_RATE_100);
                            /*File file = new File(pathList.get(0));
                            if (file.exists()) {
                                file.delete();
                            }*/
                        }
                        addWaterMark();
                    }
                } else if (requestCode == REQUEST_CODE_NOTE_MEDIA_PICKER && data != null) {
                    String path = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_PATH);
                    String path1000 = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_1000_PATH);
                    String pathThumb = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_THUMB_PATH);

                    if (path != null) {
                        output_file = new File(path);
                        file1000 = path1000;
                        thumbFile = pathThumb;

                        addWaterMark();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Card Camera Activity Result Error:" + e.getMessage());
            procDialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void addWaterMark() {
        if ((output_file != null)) {

            try {
                new AsyncTask<Void, Void, Object>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        procDialog = ProgressDialog.show(context, getString(R.string.resizing_image),
                                getString(R.string.please_wait), true);
                        procDialog.setCancelable(false);
                    }

                    @Override
                    protected Object doInBackground(Void... params) {
                        //  bmOptions.inSampleSize = 4;
                        // resizePic(fileUri.getPath());
                        compressImage(output_file.getPath());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object fn) {

                        double lat = 0.0, longi = 0.0;

                        if (currentLocation != null) {
                            lat = currentLocation.latitude;
                            longi = currentLocation.longitude;
                        }

                        String filePath = output_file.getPath();

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
                        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                        //            Shader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

                        String roundedLat = Util.RoundUpto_AfterDecimal(lat, 6);
                        String roundedLongi = Util.RoundUpto_AfterDecimal(longi, 6);
                        String waterMarkString = dateTime + "|Location:" + locationName + "|Latitude:" + roundedLat +
                                " , Longitude:" + roundedLongi;

                        Util.WaterMarkPhoto(context, filePath, waterMarkString);

                        AttachmentDataSource attachDataSource = new AttachmentDataSource(context);
                        AttachmentData attachData = new AttachmentData();

                        attachData.setEventID(getEventID());
                        attachData.setLocationID(getLocID());
                        attachData.setAttachementType("P");
                        attachData.setFileLocation(filePath);
                        attachData.setCreationDate(System.currentTimeMillis());
                        attachData.setLatitude(lat);
                        attachData.setLongitude(longi);
                        attachData.setDataSyncFlag(null);
                        attachData.setEmailSentFlag(null);
                        attachData.setTimeTaken((long) getSetID());

                        if (fieldParamId != 0) {
                            //means input type is PHOTOS
                            attachData.setFieldParameterID(fieldParamId + "");
                        }

                        attachData.setSiteId(getSiteID());
                        attachData.setUserId(getUserID());
                        attachData.setMobileAppId(getMobileAppID());
                        attachData.setSetId(getSetID());
                        attachData.setAzimuth(mSensorTracker.getAzimuthInDegress());

                        attachData.setFile1000(file1000);
                        attachData.setFileThumb(thumbFile);

//                                    Log.i(TAG, "Azimuth for photo " + attachData.getFileLocation() + " is :" + mSensorTracker.getAzimuthInDegress());
                        try {
                            attachDataSource.insertAttachmentData(attachData, false);

                            procDialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onActivityResult Error:" + e.getMessage());
                        }
                        //openGallery();
                        siteName = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME);
                        locationName = new LocationDataSource(context).getLocationName(getLocID());
                        ArrayList<GalleryItem> list = collectData();

                        if (list == null || list.size() < 1) {
                            recyclerView.setVisibility(View.GONE);
                            tvNoPhotoSaved.setVisibility(View.VISIBLE);

                            Toast.makeText(getApplicationContext(), getString(R.string.you_do_not_have_saved_photos),
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvNoPhotoSaved.setVisibility(View.GONE);
                            showData(list);
                        }
                    }
                }.execute();
//		    	     }.execute().get(10, TimeUnit.SECONDS)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (output_file != null) {
            outState.putString("FileURI", output_file.getPath());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("FileURI")) {
            String path = savedInstanceState.getString("FileURI");
            if (path != null && !path.isEmpty())
                output_file = new File(path);
        }
    }


    public void handleCamera() {

        try {
            if (getAttachmentNamePrefix() == null) {
                Toast.makeText(this, "Name Prefix not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            output_file = getOutputMediaFile(MEDIA_TYPE_IMAGE, "p_"
                    + getAttachmentNamePrefix());
            Intent mediaIntent = new Intent(this, MediaPickerActivity.class);
            mediaIntent.putExtra(GlobalStrings.IS_CAMERA, true);
            startActivityForResult(mediaIntent,
                    REQUEST_CODE_NOTE_MEDIA_PICKER);
          /*  initGalleryPicker(MEDIA_TYPE_IMAGE);
            Pix.start(this, pickerOptions);*/
            GlobalStrings.captureTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "handleCamera() error:" + e.getLocalizedMessage());
        }
    }

    private void initGalleryPicker(int type) {

        pickerOptions = Options.init()
                .setRequestCode(REQUEST_CODE_PIX_NOTE_IMAGE_PICKER)
                .setCount(1)
                .setFrontfacing(false)
                .setExcludeVideos(true)
                .setVideoDurationLimitinSeconds(60)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath(
                        BaseMenuActivity.getMediaStorageDirectory(type).getAbsolutePath()
                );
    }

    @Override
    public void onLocationDeny() {
        //no use
    }


    public class CustomComparator implements Comparator<GalleryItem> {

        @Override
        public int compare(GalleryItem lhs, GalleryItem rhs) {

            if (lhs.getTxtDate() != null && rhs.getTxtDate() != null) {

                Long date1 = Long.parseLong(lhs.getTxtDate());
                Long date2 = Long.parseLong(rhs.getTxtDate());

                int count = date2.compareTo(date1);
                return count;
            }
            return 0;
        }
    }

    protected static Uri getOutputMediaFileUri(File op_file, Context context) {

        Uri photoURI;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoURI = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider"
                    , op_file);
        } else {
            photoURI = Uri.fromFile(op_file);
        }

        Log.i(TAG, "Capture photo URI:" + photoURI);
        return photoURI;
    }

    /**
     * Create a File for saving an image or video
     */
    protected static File getOutputMediaFile(int type, String prefix) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = null;

        if (type == MEDIA_TYPE_IMAGE) {
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    GlobalStrings.IMAGE_STORAGE_DIR);
        } else if (type == MEDIA_TYPE_DRAWING) {
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    GlobalStrings.DRAWING_STORAGE_DIR);
        }
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist

        Log.i(TAG, "getOutputMediaFile path:" + mediaStorageDir.getAbsolutePath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + prefix + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else if (type == MEDIA_TYPE_DRAWING) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + prefix);
        } else {
            return null;
        }

        Log.i(TAG, "getOutputMediaFile Path:" + mediaFile.getAbsolutePath());
        return mediaFile;
    }


    public String compressImage(String imageUri) {


        Log.i(TAG, "compressImage() IN time:" + System.currentTimeMillis());

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = null;//= BitmapFactory.decodeFile(filePath,options);

        try {
//            InputStream in = getContentResolver().openInputStream(
//                    Uri.parse(filePath));
            bmp = BitmapFactory.decodeFile(imageUri, options);//BitmapFactory.decodeStream(in, null, options);
        } catch (Exception e) {
            // do something
            e.printStackTrace();
            Log.e(TAG, "compressImage() error in Compress image:" + e.getMessage());
            return imageUri;

        }


        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 916.0f;
        float maxWidth = 712.0f;
        float imgRatio;
        float maxRatio;

        try {
            imgRatio = actualWidth / actualHeight;
            maxRatio = maxWidth / maxHeight;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "compressImage() Error:" + e.getMessage());
            return imageUri;

        }


        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = Util.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imageUri, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            Log.i(TAG, "compressImage() error in decode file:" + exception.getMessage());
            return imageUri;
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            Log.i(TAG, "compressImage() error in createBitmap file:" + exception.getMessage());

        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(imageUri);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

//            //06-Apr-17 Add Water mark
//            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
//            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
////            Shader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
//            String waterMarkString=dateTime+"|Location:"+locationName+"|Latitude:"+lat+" , Longitude:"+longi;
//            Point pont=new Point();
//            pont.x=20;
//            pont.y=(int) (scaledBitmap.getHeight()- 60);
//            scaledBitmap=Util.CreateWaterMark(context,scaledBitmap,waterMarkString,pont, Color.RED,100,15,false);
//

//            Log.i(TAG,"Width/height  x="+actualWidth+" y="+actualHeight);
//            Log.i(TAG,"Point x="+pont.x+" y="+pont.y);
//            Log.i(TAG, "compressImage() Water mark=" + dateTime);


        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        // String filename = getFilename();

        File outfile = new File(imageUri);
        try {
            out = new FileOutputStream(outfile);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return imageUri;

    }
}
