package qnopy.com.qnopyandroid.ui.activity;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.fxn.pix.Pix;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.uiutils.BaseActivity;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.util.Util;

public class BaseMenuActivity extends BaseActivity implements CustomAlert.LocationServiceAlertListener {

    private static final String TAG = BaseMenuActivity.class.getSimpleName();
    ProgressDialog progDialog = null;

    public static final int REQUEST_CODE_PIX_IMAGE_PICKER = 1478;
    public static final int REQUEST_CODE_PIX_NOTE_IMAGE_PICKER = 1479;
    public static final int DRAWING_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    public static final int CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE = 104;
    public static final int SYNC_ACTIVITY_REQUEST_CODE = 1111;

    // camera constants
    public static final int REQUEST_CODE_MEDIA_PICKER = 1024;
    public static final int REQUEST_CODE_NOTE_MEDIA_PICKER = 7121;
    public static final int REQUEST_CODE_FORM_MASTER_MEDIA_PICKER = 7825;

    public static final int REQUEST_CODE_CARD_GALLERY_FOR_PHOTOS = 7995;
    public static final int REQUEST_CODE_BARCODE_SCANNER = 45024;

    Context CurrentContext;
    static double capturedLatitude = 0.0, capturedLongitude = 0.0;

    public File output_file = null;
    public String attachmentNamePrefix = null, currentLocationName;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_DRAWING = 3;
    public static final int MEDIA_TYPE_DOCUMENT = 4;
    public static final int MEDIA_TYPE_DB = 5;

    int userID;
    public boolean isSplitScreenEnabled = false;

    private FusedLocationProviderClient fusedLocationClient;

    private volatile CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    public String thumbFile;
    public String file1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.ENABLE_SPLIT_SCREEN);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                getLocation();
        } else
            getLocation();
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
/*
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        GlobalStrings.CURRENT_GPS_LOCATION = location;
                        lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        capturedLatitude = lastKnownLocation.latitude;
                        capturedLongitude = lastKnownLocation.longitude;
                    }
                });
*/

        //added new instance again in case if activity is resumed and cancellationToken is cancelled
        //then it gives crash as it cant be used if cancelled
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
                            GlobalStrings.CURRENT_GPS_LOCATION = location;
                            capturedLatitude = location.getLatitude();
                            capturedLongitude = location.getLongitude();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "\n\nonCreateOptionsMenu() Start Time:" + System.currentTimeMillis());

        currentLocationName = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.CURRENT_LOCATIONNAME);

        SharedPreferences prefs = getSharedPreferences("BADELFGPS", MODE_PRIVATE);
        String lat = prefs.getString("latitude", "");
        String lng = prefs.getString("longitude", "");

        Log.e("BaseMenuActivity", "onDataReceived: latitude " + lat + " ; longitude=" + lng);
        Log.e("BaseMenuActivity", "***********************************************************");

        if (lat.equals("") && lng.equals("")) {
            if (GlobalStrings.CURRENT_GPS_LOCATION != null) {
                capturedLatitude = GlobalStrings.CURRENT_GPS_LOCATION.getLatitude();
                capturedLongitude = GlobalStrings.CURRENT_GPS_LOCATION.getLongitude();
            }
        } else {
            capturedLatitude = Double.parseDouble(lat);
            capturedLongitude = Double.parseDouble(lng);
        }

        userID = Integer.parseInt(Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.USERID));

        Log.i(TAG, "onCreateOptionsMenu() End Time:" + System.currentTimeMillis());

        return true;
    }

    public void setAttachmentNamePrefix(String prefix) {
        attachmentNamePrefix = prefix;
        Log.i(TAG, "setAttachmentNamePrefix()  attachmentNamePrefix:" + attachmentNamePrefix);
    }

    public String getAttachmentNamePrefix() {
        return attachmentNamePrefix;
    }

    /**
     * Create a file Uri for saving an image or video
     */
    protected static Uri getOutputMediaFileUri(File op_file, Context context) {

        Uri photoURI;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider"
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

    public static File getOutputMediaFile(int type, String prefix, Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        Log.i(TAG, "getOutputMediaFile() IN time:" + System.currentTimeMillis());
        File mediaStorageDir = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            mediaStorageDir = getMediaStorageDirOS11Up(type, context);
        } else if (type == MEDIA_TYPE_IMAGE) {
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

        Log.i(TAG, "getOutputMediaFile() path:" + mediaStorageDir.getAbsolutePath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "getOutputMediaFile() failed to create directory");
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

        Log.i(TAG, "getOutputMediaFile() Path :" + mediaFile.getAbsolutePath());

        Log.i(TAG, "getOutputMediaFile() OUT time:" + System.currentTimeMillis());

        return mediaFile;
    }

    public static File getMediaStorageDirectory(int type) {
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

        String thumbDir = "";

        if (type != MEDIA_TYPE_DB && type != MEDIA_TYPE_DOCUMENT) {
            thumbDir = mediaStorageDir.getAbsolutePath() + File.separator + GlobalStrings.THUMBNAILS_DIR + File.separator;

            File thumbStorageDir = new File(thumbDir);

            if (!thumbStorageDir.exists())
                thumbStorageDir.mkdirs();
        }

        return mediaStorageDir;
    }

    public static File getMediaStorageDirOS11Up(int type, Context context) {
        File mediaStorageDir = null;

        String makeDir = Util.getMainBaseDirPath(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (type == MEDIA_TYPE_IMAGE) {
                makeDir = makeDir + GlobalStrings.IMAGE_STORAGE_DIR + File.separator;
            } else if (type == MEDIA_TYPE_DRAWING) {
                makeDir = makeDir + GlobalStrings.DRAWING_STORAGE_DIR + File.separator;
            } else if (type == MEDIA_TYPE_DOCUMENT) {
                makeDir = makeDir + GlobalStrings.DOCUMENT_STORAGE_DIR + File.separator;
            } else if (type == MEDIA_TYPE_DB) {
                makeDir = Util.getExternalDataDirPath(context) + File.separator
                        + GlobalStrings.DB_ZIP_STORAGE_DIR + File.separator;
            }

            mediaStorageDir = new File(makeDir);

            if (!mediaStorageDir.exists())
                mediaStorageDir.mkdirs();

            String thumbDir = "";

            if (type != MEDIA_TYPE_DB && type != MEDIA_TYPE_DOCUMENT) {
                thumbDir = makeDir + GlobalStrings.THUMBNAILS_DIR + File.separator;

                File thumbStorageDir = new File(thumbDir);

                if (!thumbStorageDir.exists())
                    thumbStorageDir.mkdirs();
            }
        }
        return mediaStorageDir;
    }

    public static File getDBZipStorageDir(Context context) {

        String makeDir = Util.getExternalDataDirPath(context) + File.separator
                + GlobalStrings.DB_ZIP_STORAGE_DIR + File.separator;
        File mediaStorageDir = new File(makeDir);

        if (!mediaStorageDir.exists())
            mediaStorageDir.mkdirs();

        return mediaStorageDir;
    }

    public void setRightNavigationEnabled(boolean status) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void invokeTextView(View v) {
    }

    public void handleLeftNav(View v) {
    }

    public void setLeftNavigation(View v) {

    }

    public void setRightNavigation(View v) {
    }

    public void handleCamera() {
    }

    public void handleDrawing() {
    }

    public void openGallery() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "OnActivityResult() BaseMenu code=" + requestCode);

        if (GlobalStrings.CURRENT_GPS_LOCATION == null) {
            getLocation();
        }

        Log.i(TAG, "Current watermark location(lat,longi):" + capturedLatitude + " , " + capturedLongitude);

        try {
            if ((requestCode == REQUEST_CODE_PIX_IMAGE_PICKER) && resultCode == RESULT_OK && data != null) {
                ArrayList<String> pathList = data.getStringArrayListExtra((Pix.IMAGE_RESULTS));
                if (pathList != null) {
                    if (pathList.size() > 0) {
                        Bitmap bitmap;
                        bitmap = Util.correctBitmapRotation(pathList.get(0));
                        Bitmap cropIMg = Util.cropToSquare(bitmap);
                        Util.saveBitmapToSDCard(cropIMg, output_file, GlobalStrings.COMPRESSION_RATE_100);
                        File file = new File(pathList.get(0));
                        /*if (file.exists()) {
                            file.delete();
                        }*/
                    }
                    addWaterMark();
                }
            } else if ((requestCode == REQUEST_CODE_MEDIA_PICKER)
                    && resultCode == RESULT_OK && data != null) {
                String path = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_PATH);
                String path1000 = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_1000_PATH);
                String pathThumb = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_THUMB_PATH);

                if (path != null) {
                    output_file = new File(path);
                    thumbFile = pathThumb;
                    file1000 = path1000;
                    addWaterMark();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in onActivityResult:" + e.getMessage());
        }
    }

    public void setAttachmentData(boolean openGallery, String fpId) {
        //overridden in formActivity
    }

    @SuppressLint("StaticFieldLeak")
    private void addWaterMark() {
        if (output_file != null) {
            try {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        showAlertProgress(getString(R.string.resizing_image));
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        compressImage(output_file.getPath());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String fn) {
                        afterSave();

                        Log.e(TAG, "onPostExecute() Image compression completed start to water mark ");

                        String filePath = output_file.getPath();

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
                        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                        // Shader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

                        String roundedLat = Util.RoundUpto_AfterDecimal(capturedLatitude, 6);
                        String roundedLongi = Util.RoundUpto_AfterDecimal(capturedLongitude, 6);
                        String waterMarkString = dateTime + "|Location:" + currentLocationName + "|Latitude:" + roundedLat +
                                " , Longitude:" + roundedLongi;

                        Util.WaterMarkPhoto(CurrentContext, filePath, waterMarkString);
                    }
                }.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void afterSave() {
        try {
            cancelAlertProgressWithMsg();
            Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaIntent.setData(getOutputMediaFileUri(output_file, CurrentContext));
            sendBroadcast(mediaIntent);
        } catch (Exception e) {
            Log.e("afterSave", "Image Resize Error:" + e.getMessage());

            Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaIntent.setData(getOutputMediaFileUri(output_file, CurrentContext));
            sendBroadcast(mediaIntent);
        }

        setAttachmentData(true, "");
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

        float imgRatio;
        float maxRatio;
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 916.0f;
        float maxWidth = 712.0f;

        try {
            imgRatio = actualWidth / actualHeight;
            maxRatio = maxWidth / maxHeight;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "compressImage() Error :" + e.getMessage());
            return imageUri;
        }

        String comp_image = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.IS_COMPRESS_IMAGE);

        if (comp_image == null) {
            GlobalStrings.COMPRESS_IMAGE = true;
        } else {
            GlobalStrings.COMPRESS_IMAGE = Boolean.parseBoolean(comp_image);
        }

//        if (GlobalStrings.COMPRESS_IMAGE) {
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
        //   }

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
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "compressImage() error in rotate image:" + e.getMessage());

        }
        FileOutputStream out = null;
        // String filename = getFilename();

        File outfile = new File(imageUri);
        try {
            out = new FileOutputStream(outfile);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "compressImage() error:" + e.getMessage());
        }

        Log.i(TAG, "compressImage() OUT time:" + System.currentTimeMillis());

        return imageUri;
    }


    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (output_file != null) {
            outState.putString("cameraImageUri", output_file.getPath());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            String path = savedInstanceState.getString("cameraImageUri");
            if (path != null && !path.isEmpty())
                output_file = new File(path);
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {

        Log.i(TAG, "copyFile() IN time:" + System.currentTimeMillis());

        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (source != null) {
            destination.transferFrom(source, 0, source.size());

            String filePath = sourceFile.getPath();
            if (!filePath.contains(GlobalStrings.IMAGE_STORAGE_DIR) && !filePath.contains(GlobalStrings.DRAWING_STORAGE_DIR)) {
                compressImage(destFile.getPath());

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
                String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                // hader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

                String roundedLat = Util.RoundUpto_AfterDecimal(capturedLatitude, 6);
                String roundedLongi = Util.RoundUpto_AfterDecimal(capturedLongitude, 6);
                String waterMarkString = dateTime + "|Location:" + currentLocationName + "|Latitude:" + roundedLat +
                        " , Longitude:" + roundedLongi;

                Util.WaterMarkPhoto(CurrentContext, destFile.getPath(), waterMarkString);
            }

            //compressImage(destFile.getPath());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
        Log.i(TAG, "copyFile() OUT time:" + System.currentTimeMillis());

    }

    private String getRealPathFromURI(Uri contentUri) {

        String res = contentUri.getPath();
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    @Override
    public void onLocationDeny() {
        //no use
    }

//    private String getRealPathFromURI(Uri contentUri) {
//        try {
//
//
//            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "getRealPathFromURI() error:" + e.getMessage());
//            return contentUri.toString();
//        }
//    }


}
