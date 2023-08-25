package qnopy.com.qnopyandroid.fetchdraw;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.db.AttachmentData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.map.MapActivity;
import qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity;
import qnopy.com.qnopyandroid.ui.activity.CardGalleryActivity;
import qnopy.com.qnopyandroid.ui.activity.FileFolderMainActivity;
import qnopy.com.qnopyandroid.ui.activity.FileFolderSyncActivity;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;

public class FetchDrawScreen extends ProgressDialogActivity implements OnClickListener,
        OnMyLocationButtonClickListener, CustomAlert.LocationServiceAlertListener {
    private static final String TAG = "FetchDrawScreen";
    ActionBar actionBar;
    private ImageButton camera, open, rotate, draw, circle, square, drawnew,
            undo, save, zoom, map, object, actions, action_zoombtn;
    private Spinner tag, line, objects, shapes;
    private ImageView red, blue1, blue2, green, black, white, magenta, yellow;
    private Bitmap rotateBitmap;
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private final int REQUEST_GALLERY = 1;
    private static int SCREEN_CAPTURE_ACTIVITY_REQUEST_CODE = 2;
    static File output_file;
    DrawingView drawingview;
    static FrameLayout framelayout;
    DBHandler mDbHandler;
    //    static String URIBitmap = "";
    int lastColor = 0xFFFF0000;
    boolean textSpinnerFirstTimeSelected = false;
    boolean lineSpinnerFirstTimeSelected = false;
    boolean objectSpinnerFirstTimeSelected = false;
    boolean shapesSpinnerFirstTimeSelected = false;
    boolean actionSpinnerFirstTimeSelected = false;
    boolean newNgpsSpinnerFirstTimeSelected = false;
    private Point p;
    int compressQuality = 100;
    int[] shapes_img_id = new int[]{R.mipmap.draw, R.mipmap.circle,
            R.mipmap.square};
    int[] text_img_id = new int[]{R.mipmap.tagtext1, R.mipmap.tagtext2,
            R.mipmap.tagtext3};

    int[] object_img_id = new int[]{R.mipmap.tree_white,
            R.mipmap.lift_white, R.mipmap.sump_white};

    int[] line_img_id = new int[]{R.mipmap.line1, R.mipmap.line2,
            R.mipmap.line3};
    ImageView bgImg;
    int imgWidth, imgHeight;
    private int eventID = 0;
    private String locID = "0";
    private int siteID = 0;
    private int userID = 0;
    private int mobileAppID = 0;
    private String filePrefix = null;
    private String saveDirectory = null;
    private int setID = 0;
    private int objectResourceID = 0;
    // private boolean saved = true;
    private String notes, FileToSave = null;
    Context mContext;
    private LinkedList<String> mDrawingsToScan = new LinkedList<String>();
    protected MediaScannerConnection mMediaScannerConnection;
    private String mPendingShareFile, username;
    private int mCurrentView;
    static ArrayList mViewsArray;
    private int mViewsCount;
    private FetchDrawScreen mStyle;
    Bundle extras;
    boolean isDrawPhoto = false, waterMark = true, Add_waterMark = false;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    volatile CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

//    MobileSensor mSensorTracker;

    public FetchDrawScreen() {
        mCurrentView = 0;
        mViewsCount = 0;
        mViewsArray = new ArrayList();
    }

    private MediaScannerConnectionClient mMediaScannerClient = new MediaScannerConnectionClient() {
        @Override
        public void onMediaScannerConnected() {
            scanNext();
        }

        private void scanNext() {
            synchronized (mDrawingsToScan) {
                if (mDrawingsToScan.isEmpty()) {
                    mMediaScannerConnection.disconnect();
                    return;
                }
                String fn = mDrawingsToScan.removeFirst();
                mMediaScannerConnection.scanFile(fn, "image/png");
            }
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            synchronized (mDrawingsToScan) {
                if (path.equals(mPendingShareFile)) {
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("image/png");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    // startActivity(Intent.createChooser(sendIntent,
                    // "Send drawing to:"));
                    mPendingShareFile = null;
                }
                scanNext();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fetchdraw_screen);
        mContext = this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mMediaScannerConnection = new MediaScannerConnection(
                FetchDrawScreen.this, mMediaScannerClient);

//        mSensorTracker = new MobileSensor(mContext);
        username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
        extras = getIntent().getExtras();
        if (extras != null) {
            setEventID(extras.getInt("EVENT_ID"));
            setLocID(extras.getString("LOC_ID"));
            int uid = Integer.parseInt(extras.getString("USER_ID"));
            setUserID(uid);
            setMobileAppID(extras.getInt("MOBILE_APP_ID"));
            setSiteID(extras.getInt("SITE_ID"));
            setFilePrefix(extras.getString("FILE_NAME_PREFIX"));
            setSaveDirectory(extras.getString("SAVE_DIRECTORY"));
            setSetID(extras.getInt("SET_ID"));
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        SharedPref.globalContext = getApplicationContext();
        // Custom ActionBar
//        ActionBar actionBar = getSupportActionBar();
        View mActionBarView = getLayoutInflater().inflate(
                R.layout.header_layout, null);
        actionBar = getSupportActionBar();
        mStyle = this;

        final Drawable upArrow = ContextCompat.getDrawable(mContext, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setCustomView(mActionBarView);
            actionBar.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.actionbar_teal)));
            actionBar.setCustomView(mActionBarView);

            Toolbar parent = (Toolbar) mActionBarView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }

        Util.setOverflowButtonColor(FetchDrawScreen.this, Color.WHITE);

        mDbHandler = new DBHandler(FetchDrawScreen.this);

        camera = (ImageButton) mActionBarView.findViewById(R.id.camera);
        map = (ImageButton) mActionBarView.findViewById(R.id.newNgps);
        open = (ImageButton) mActionBarView.findViewById(R.id.open);
        actions = (ImageButton) mActionBarView.findViewById(R.id.actions);
        action_zoombtn = (ImageButton) mActionBarView.findViewById(R.id.action_zoom);
        object = (ImageButton) mActionBarView.findViewById(R.id.object);
        save = (ImageButton) mActionBarView.findViewById(R.id.save);
        undo = (ImageButton) mActionBarView.findViewById(R.id.undo);

        framelayout = (FrameLayout) findViewById(R.id.frame);
        red = (ImageView) findViewById(R.id.red);
        blue1 = (ImageView) findViewById(R.id.blue1);
        blue2 = (ImageView) findViewById(R.id.blue2);
        green = (ImageView) findViewById(R.id.green);
        white = (ImageView) findViewById(R.id.white);
        black = (ImageView) findViewById(R.id.black);
        magenta = (ImageView) findViewById(R.id.magenta);
        yellow = (ImageView) findViewById(R.id.yellow);
        objects = (Spinner) findViewById(R.id.objects);
        objects.setAdapter(new TextAdapter(FetchDrawScreen.this, object_img_id));
        tag = (Spinner) findViewById(R.id.tag);
        tag.setAdapter(new TextAdapter(FetchDrawScreen.this, text_img_id));
        line = (Spinner) findViewById(R.id.line);
        bgImg = (ImageView) findViewById(R.id.bg_img);
        shapes = (Spinner) findViewById(R.id.shapes);
        shapes.setAdapter(new TextAdapter(FetchDrawScreen.this, shapes_img_id));
        line.setAdapter(new TextAdapter(FetchDrawScreen.this, line_img_id));
        red.setOnClickListener(this);
        blue1.setOnClickListener(this);
        blue2.setOnClickListener(this);
        green.setOnClickListener(this);
        white.setOnClickListener(this);
        black.setOnClickListener(this);
        magenta.setOnClickListener(this);
        yellow.setOnClickListener(this);
        camera.setOnClickListener(cameraClick);
        map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapClick();
            }
        });

        undo.setOnClickListener(undoClick);
        object.setOnClickListener(objectClick);
        open.setOnClickListener(gallery);
        actions.setOnClickListener(actionRedoClick);
        action_zoombtn.setOnClickListener(actionZoomClick);
        save.setOnClickListener(saveClick);
        shapes.setOnItemSelectedListener(shapesSpinner);
        objects.setOnItemSelectedListener(drawObjectSpinner);
        tag.setOnItemSelectedListener(drawTextSpinner);
        line.setOnItemSelectedListener(drawLineSpinner);

        drawingview = (DrawingView) findViewById(R.id.canvas);
        drawingview.setImage(bgImg);
        drawingview.mCurrentShape = DrawingView.DRAW;
        if (extras != null && extras.containsKey("DRAW_PHOTO")) {
            isDrawPhoto = true;
            setImageToPicture(getSaveDirectory());
            SendImageToView();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                CustomAlert.showLocationPermissionAlert(this, this);
            } else
                getLocation();
        } else
            getLocation();
    }

    // Check SharedPreferences for temporary Saved Bitmap

    public int getmCurrentView() {
        return mCurrentView;
    }

    public void setmCurrentView(int i) {
        mCurrentView = i;
    }

    private void getPreviousImage() {
        SharedPreferences prefs = getSharedPrefrences();
        boolean flag = prefs.getBoolean("previous", false);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
        if (flag) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                    FetchDrawScreen.this);
            mBuilder.setTitle(getString(R.string.previous_edited_image));
            mBuilder.setMessage(getString(R.string.previous_edited_image_is_not_saved));
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(getString(R.string.yes),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Get Stored Bitmap from Database
                            Cursor cursor = mDbHandler.getDatas();
                            if (cursor != null && cursor.getCount() > 0) {
                                if (cursor.moveToFirst()) {
                                    do {

                                        byte[] image = cursor.getBlob(cursor
                                                .getColumnIndexOrThrow(DBHandler.IMAGE_BITMAP));

                                        if (image == null) {
                                            Toast.makeText(mContext, "Unable to process " +
                                                            "image please try again",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Bitmap bmp = BitmapFactory
                                                .decodeByteArray(image, 0,
                                                        image.length);

                                        String filename, baseDir, thumbDir = "";
                                        File folder;
                                        if (!isDrawPhoto) {

                                            filename = getFilePrefix()
                                                    + System
                                                    .currentTimeMillis() + ".png";

        /*                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                                baseDir
                                                        = BaseMenuActivity.getMediaStorageDirOS11Up(BaseMenuActivity
                                                                .MEDIA_TYPE_DRAWING,
                                                        FetchDrawScreen.this).getAbsolutePath();
                                                folder = new File(baseDir);
                                            } else {
                                                baseDir = Environment
                                                        .getExternalStoragePublicDirectory(
                                                                Environment.DIRECTORY_PICTURES)
                                                        .getAbsolutePath();

                                                folder = new File(baseDir,
                                                        getSaveDirectory());
                                            }*/

                                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                                baseDir
                                                        = BaseMenuActivity.getMediaStorageDirOS11Up(BaseMenuActivity
                                                        .MEDIA_TYPE_DRAWING, FetchDrawScreen.this).getAbsolutePath();
                                                folder = new File(baseDir);
                                                thumbDir = baseDir + File.separator + GlobalStrings.THUMBNAILS_DIR
                                                        + File.separator;
                                            } else {
                                                baseDir = Environment
                                                        .getExternalStoragePublicDirectory(
                                                                Environment.DIRECTORY_PICTURES)
                                                        .getAbsolutePath();
                                                folder = new File(baseDir, getSaveDirectory());

                                                thumbDir = Util.createThumbDir(folder.getAbsolutePath());
                                            }
                                        } else {
                                            String dir = getSaveDirectory();
                                            String actualPath = dir.substring(0, dir.lastIndexOf("/"));
                                            baseDir = actualPath;
                                            filename = dir.substring(dir.lastIndexOf("/") + 1);

                                            folder = new File(baseDir);
                                        }

                                        // File folder = new File(baseDir,
                                        // "Fetch Draw");

                                        try {
                                            folder.mkdir();
                                            if (folder.exists()) {
                                                File file = new File(folder,
                                                        filename);
                                                file.createNewFile();
                                                OutputStream os = null;
                                                os = new FileOutputStream(file);
                                                boolean saveImage = bmp
                                                        .compress(
                                                                CompressFormat.PNG,
                                                                100, os);

                                                //thumbnail file
                                                String thumbFileName = filename + GlobalStrings.THUMBNAIL_EXTENSION;
                                                File dirThumbDest = new File(thumbDir, thumbFileName);

                                                if (saveImage) {
                                                    FileToSave = file.getAbsolutePath();
                                                    Toast.makeText(
                                                                    FetchDrawScreen.this,
                                                                    getString(R.string.image_is_saved),
                                                                    Toast.LENGTH_SHORT)
                                                            .show();
                                                    if (isDrawPhoto) {
                                                        AttachmentDataSource attachDataSource = new AttachmentDataSource(mContext);
                                                        if (attachDataSource.isFileAlreadyExist(FileToSave)) {
                                                            attachDataSource.updateModificationDate(FileToSave, null, null);

                                                        }
                                                    } else {
                                                        attachmentDataInsertion(FileToSave, dirThumbDest.getAbsolutePath());
                                                    }
//                                                    attachmentDataInsertion(file
//                                                            .getAbsolutePath());
                                                } else {
                                                    Toast.makeText(
                                                                    FetchDrawScreen.this,
                                                                    getString(R.string.failed_to_save_image),
                                                                    Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (Error er) {
                                            er.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } while (cursor.moveToNext());
                                }
                            }
                            mDbHandler.deleteTableContent();
                            dialog.dismiss();
                            finish(); // onbackpressed
                        }
                    });

            mBuilder.setNegativeButton(getString(R.string.no),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDbHandler.deleteTableContent();
                            dialog.dismiss();
                            finish(); // onbackpressed
                        }
                    });
            AlertDialog mAlertDialog = mBuilder.create();
            mAlertDialog.show();
        } else {
            finish();
        }
    }

    // Getting location from screen for displaying popup window at correct place
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int[] location = new int[2];
        p = new Point();
        framelayout.getLocationOnScreen(location);
        p.y = location[1];
        save.getLocationOnScreen(location);
        p.x = location[0];
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
                        try {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            result = "Location (success): " +
                                    location.getLatitude() +
                                    ", " +
                                    location.getLongitude();
                        } catch (Exception e) {
                            e.printStackTrace();
                            currentLocation = new LatLng(0.0, 0.0);
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
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CustomAlert.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                getLocation();
            }
        }
    }

    // Clear Memory
    @Override
    protected void onStop() {
        try {
            System.out.println("Before Memory clear");
            System.gc();
            System.out.println("After Memory Clear");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        cancellationTokenSource.cancel();
        super.onStop();
    }

    // save temporary images to Database and set flag in SharedPreferences
    public void checkForUnsaved() {
        final SharedPreferences prefs = getSharedPrefrences();
        Editor edit = prefs.edit();
        boolean flag = false;
        if (drawingview._allStrokes.size() > 0 || rotateBitmap != null) {
            Bitmap cs = null;
            framelayout.setDrawingCacheEnabled(false);
            framelayout.setDrawingCacheEnabled(true);
            framelayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
            framelayout.buildDrawingCache();
            cs = framelayout.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cs.compress(CompressFormat.PNG, 100, baos);
            byte[] buf = baos.toByteArray();
            if (!SharedPref.isSaved()) {
                flag = mDbHandler.updateTable(buf);
            }
            framelayout.setDrawingCacheEnabled(false);
            cs.recycle();
            edit.putBoolean("previous", flag);
        } else {
            edit.putBoolean("previous", flag);
        }
        edit.commit();
    }

    // get single instance for SharedPreferences

    private SharedPreferences getSharedPrefrences() {
        return getSharedPreferences(FetchDrawScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public void zoomClick() {
        drawingview.mCurrentShape = DrawingView.ZOOM;
    }


    AdapterView.OnItemSelectedListener drawTextSpinner = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (textSpinnerFirstTimeSelected) {
                switch (position) {
                    case 0:
                        drawingview.textSize = 20;
                        break;
                    case 1:
                        drawingview.textSize = 40;
                        break;
                    case 2:
                        drawingview.textSize = 60;
                        break;
                }
                drawingview.mCurrentShape = DrawingView.TAG;
                getTagText();
            } else {
                textSpinnerFirstTimeSelected = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    };
    AdapterView.OnItemSelectedListener newNgpsSpinner = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (newNgpsSpinnerFirstTimeSelected) {
                switch (position) {
                    case 0:
                        mapClick();
                        break;
                    case 1:
                        newdrawClick();
                        break;

                }
            } else {
                newNgpsSpinnerFirstTimeSelected = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    // Get Text from user to place on view

    public void getTagText() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                FetchDrawScreen.this);
        final EditText mEditText = new EditText(FetchDrawScreen.this);
        mBuilder.setTitle(getString(R.string.enter_tag_text))
                .setMessage(getString(R.string.enter_text))
                .setCancelable(true).setView(mEditText)
                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog mAlertDialog = mBuilder.create();
        mAlertDialog.show();
        Button okButton = mAlertDialog
                .getButton(DialogInterface.BUTTON_NEUTRAL);
        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mEditText.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_tag_text),
                            Toast.LENGTH_SHORT).show();
                } else {
                    DrawingView.TEXT_DRAW = true;
                    String temp_text = mEditText.getText().toString();
//                    TouchView obj1 = new TouchView(mContext, mStyle,temp_text, mViewsCount, 1.0F);
//                    obj1.setClickable(true);
//                    obj1.setmSelected(true);
//                    mViewsArray.add(obj1);
//                    framelayout.addView((View) mViewsArray.get(mViewsCount));
//                    obj1.invalidate();
//                    mViewsCount = mViewsCount + 1;
//                    Log.i(TAG,"View Count:"+mViewsCount);
                    if (temp_text.equalsIgnoreCase(drawingview.textValue)) {
                        drawingview.checkText = false;
                    } else {
                        drawingview.checkText = true;
                        drawingview.textValue = temp_text;
                        drawingview.drawTextFirst();
                        SharedPref.resetSaved();
                    }
                    mAlertDialog.dismiss();
                }
            }
        });
    }

    // place an object

    AdapterView.OnItemSelectedListener drawObjectSpinner = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (objectSpinnerFirstTimeSelected) {
                switch (position) {
                    case 0:
                        objectResourceID = R.mipmap.tree;
                        break;
                    case 1:
                        objectResourceID = R.mipmap.lift;
                        break;
                    case 2:
                        objectResourceID = R.mipmap.sump;
                        break;
                }
                drawingview.mCurrentShape = DrawingView.OBJECT;
                getObject();

            } else {
                objectSpinnerFirstTimeSelected = true;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    AdapterView.OnItemSelectedListener shapesSpinner = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (shapesSpinnerFirstTimeSelected) {
                switch (position) {
                    case 0:
                        drawingview.mCurrentShape = DrawingView.DRAW;
                        break;
                    case 1:
                        drawingview.mCurrentShape = DrawingView.CIRCLE;
                        break;
                    case 2:
                        drawingview.mCurrentShape = DrawingView.SQUARE;
                        break;
                }

            } else {
                shapesSpinnerFirstTimeSelected = true;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    AdapterView.OnItemSelectedListener actionSpinner = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (actionSpinnerFirstTimeSelected) {
                switch (position) {
                    case 0:
                        rotateClick();
                        break;
                    case 1:
                        zoomClick();
                        break;
                }
                // view.setOnClickListener(null);
            } else {
                actionSpinnerFirstTimeSelected = true;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    OnClickListener objectClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            drawingview.mCurrentShape = DrawingView.OBJECT;
            getObject();
        }
    };

    OnClickListener actionRedoClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            rotateClick();
        }
    };

    OnClickListener actionZoomClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            zoomClick();
        }
    };

    public void getObject() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                objectResourceID);
        drawingview.object = bitmap;
        drawingview.drawObjectFirst();
        bitmap = null;
        drawingview.invalidate();
        SharedPref.resetSaved();
    }

    // save View as .png format image
    OnClickListener saveClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onSaveClick();
        }
    };

    private void onSaveClick() {

        if (drawingview._allStrokes.size() > 0 || rotateBitmap != null) {

            // addNotes();

            boolean flag = SaveBitmap();
            if (isDrawPhoto) {
                finish();////20-Aug-16 back to card gallery
            } else if (flag) {
                if (p != null) {
                    // clearAll();
                    // SharedPref.setSaved();
                    // drawingview.mCurrentShape = 0;
                    drawingview.imageCanvas = null;
                    drawingview.invalidate();

                    Intent intent = new Intent(FetchDrawScreen.this, CardGalleryActivity.class);
                    intent.putExtra("SITE_ID", getSiteID());
                    intent.putExtra("EVENT_ID", getEventID());
                    intent.putExtra("LOC_ID", getLocID());
                    intent.putExtra("MOBILE_APP_ID", getMobileAppID());
                    intent.putExtra("SET_ID", getSetID());
                    intent.putExtra("USER_ID", getUserID());

                    try {
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Undo Operation
    OnClickListener undoClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (drawingview._allStrokes.size() > 0) {
                drawingview.onUndo();
                SharedPref.resetSaved();
            } else {
                rotateAnticlockwise();
            }
        }
    };

    private void rotateAnticlockwise() {
        try {
            bgImg.setImageBitmap(rotateBitmap = AndroiUtils.rotateImage(rotateBitmap, -90));
        } catch (NullPointerException n) {
            n.printStackTrace();
            Toast.makeText(mContext, getString(R.string.cancas_is_blank_nothing_to_rotate), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Camera Work

    OnClickListener cameraClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (isDrawPhoto) {//24-Feb-17 PHOTO IS OPENED IN EDIT MODE
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        FetchDrawScreen.this);
                mBuilder.setCancelable(false)
                        .setTitle(getString(R.string.alert))
                        .setMessage(getString(R.string.current_photo_will_get_removed))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //  SaveBitmap();
//                                clearAll();
//                                drawingview
//                                        .resetBGScalingAndTranslation();
                                Add_waterMark = true;
                                camera();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog mAlertDialog = mBuilder.create();
                mAlertDialog.show();
            } else if ((drawingview._allStrokes.size() > 0 || rotateBitmap != null)
                    && !SharedPref.isSaved()) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        FetchDrawScreen.this);
                mBuilder.setCancelable(false)
                        .setTitle(getString(R.string.save_picture))
                        .setMessage(getString(R.string.save_changes_made_to_the_image))
                        .setNegativeButton(getString(R.string.save),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        SaveBitmap();
//                                        clearAll();
//                                        drawingview
//                                                .resetBGScalingAndTranslation();
                                        camera();
                                    }
                                })
                        .setNeutralButton(getString(R.string.discard),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        clearAll();
                                        dialog.dismiss();
                                        drawingview
                                                .resetBGScalingAndTranslation();
                                        camera();
                                    }
                                })

                        .setPositiveButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });

                AlertDialog mAlertDialog = mBuilder.create();
                mAlertDialog.show();
            } else {
                clearAll();
                camera();
            }

        }
    };

    public void mapClick() {
        if (isDrawPhoto) {//24-Feb-17 PHOTO IS OPENED IN EDIT MODE
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                    FetchDrawScreen.this);
            mBuilder.setCancelable(false)
                    .setTitle(getString(R.string.alert))
                    .setMessage(getString(R.string.current_will_be_removed_if_u_proceed))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // SaveBitmap();
//                                clearAll();
//                                drawingview
//                                        .resetBGScalingAndTranslation();

                            Add_waterMark = true;

                            map();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog mAlertDialog = mBuilder.create();
            mAlertDialog.show();
        } else if ((drawingview._allStrokes.size() > 0 || rotateBitmap != null)
                && !SharedPref.isSaved()) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                    FetchDrawScreen.this);
            mBuilder.setCancelable(false)
                    .setTitle(getString(R.string.save_picture))
                    .setMessage(getString(R.string.save_changes_made_to_the_image))
                    .setNegativeButton(getString(R.string.save),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    SaveBitmap();
//                                    clearAll();
//                                    drawingview.resetBGScalingAndTranslation();
                                    // camera();
                                    map();
                                }
                            })
                    .setNeutralButton(getString(R.string.discard),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    clearAll();
                                    dialog.dismiss();
                                    drawingview.resetBGScalingAndTranslation();
                                    // camera();
                                    map();
                                }
                            })

                    .setPositiveButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });

            AlertDialog mAlertDialog = mBuilder.create();
            mAlertDialog.show();
        } else {
            clearAll();
            map();
            // camera();
        }
    }

    // Takes to map Activity

    private void map() {
        /*
         * try { if(bgImg != null) { bgImg.setImageBitmap(null); }
         * drawingview.setVisibility(View.GONE); if(googleMap == null) {
         * googleMap = ((MapFragment)
         * getFragmentManager().findFragmentById(R.id.mapfrag)).getMap(); }
         * setListeners(); } catch (Exception e) { e.printStackTrace(); }
         */

        Intent mapIntent = new Intent(getApplicationContext(),
                MapActivity.class);
        mapIntent.putExtra("ENABLE_SCREEN_CAPTURE", true);
        mapIntent.putExtra("FILE_NAME_PREFIX", getFilePrefix());
        mapIntent.putExtra("PREV_CONTEXT", "Draw");
        mapIntent.putExtra("OPERATION", -1);
        startActivityForResult(mapIntent, SCREEN_CAPTURE_ACTIVITY_REQUEST_CODE);

    }

    // Takes to Camera Activity

    private void camera() {
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create file in DCIM Folder
            output_file = new File(Environment.getExternalStorageDirectory()
                    + "/DCIM/", "image" + new Date().getTime() + ".png");

            Uri outputUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                outputUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider"
                        , output_file);
            } else {
                outputUri = Uri.fromFile(output_file);

                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, outputUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

//            outputUri = Uri.fromFile(file);
            // set the image file name
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            Toast.makeText(this, getString(R.string.camera_feature_not_available),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Gallery works go here

    OnClickListener gallery = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (isDrawPhoto) {//24-Feb-17 PHOTO IS OPENED IN EDIT MODE
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        FetchDrawScreen.this);
                mBuilder.setCancelable(false)
                        .setTitle(getString(R.string.alert))
                        .setMessage(getString(R.string.photo_will_be_removed_if_u_proceed))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //  SaveBitmap();
//                                clearAll();
//                                drawingview
//                                        .resetBGScalingAndTranslation();

                                Add_waterMark = true;
                                gallery();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Add_waterMark = false;
                            }
                        });

                AlertDialog mAlertDialog = mBuilder.create();
                mAlertDialog.show();
            } else if ((drawingview._allStrokes.size() > 0 || rotateBitmap != null)
                    && !SharedPref.isSaved()) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        FetchDrawScreen.this);
                mBuilder.setCancelable(false)
                        .setTitle(getString(R.string.save_picture))
                        .setMessage(getString(R.string.save_changes_made_to_the_image_before_loading))
                        .setNegativeButton(getString(R.string.save),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // dialog.dismiss();
                                        SaveBitmap();
//                                        clearAll();
//                                        drawingview
//                                                .resetBGScalingAndTranslation();
                                        gallery();
                                    }
                                })
                        .setNeutralButton(getString(R.string.discard),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        clearAll();
                                        dialog.dismiss();
                                        drawingview
                                                .resetBGScalingAndTranslation();
                                        gallery();
                                    }
                                })
                        .setPositiveButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog mAlertDialog = mBuilder.create();
                mAlertDialog.show();
            } else {
                clearAll();
                gallery();
            }
        }
    };

    // Takes to Gallery Activity

    private void gallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
    }

    // Clear all works and begin as new work

    public void newdrawClick() {

        if (!SharedPref.isSaved()) {

            if ((drawingview._allStrokes.size() > 0 || rotateBitmap != null)) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        FetchDrawScreen.this);
                mBuilder.setCancelable(false)
                        .setTitle(getString(R.string.save_picture))
                        .setMessage(getString(R.string.save_changes_made_to_the_image))
                        .setNegativeButton(getString(R.string.save),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        SaveBitmap();
                                        clearAll();
                                        drawingview
                                                .resetBGScalingAndTranslation();
                                    }
                                })
                        .setNeutralButton(getString(R.string.discard),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        clearAll();
                                        dialog.dismiss();
                                        drawingview
                                                .resetBGScalingAndTranslation();
                                    }
                                })
                        .setPositiveButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog mAlertDialog = mBuilder.create();
                mAlertDialog.show();
            } else {
                clearAll();
            }
        } else {
            clearAll();
        }

    }

    // clear all draw values and clear view image

    protected void clearAll() {
        drawingview._allStrokes.clear();
        drawingview.mCurrentShape = 0;
        drawingview.invalidate();
        rotateBitmap = null;
        bgImg.setImageBitmap(null);
        drawingview.object = null;
    }

    // Get the Image Bounds inside imageview

    public int[] getBitmapPositionInsideImageView() {
        int[] ret = new int[4];

        if (bgImg == null || bgImg.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        bgImg.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio
        // maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and
        // getWidth/getHeight)
        final Drawable d = bgImg.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = bgImg.getWidth();
        int imgViewH = bgImg.getHeight();

        int top = (int) (imgViewH - actH) / 2;
        int left = (int) (imgViewW - actW) / 2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }

    // Rotate the view

    public void rotateClick() {
        if (drawingview._allStrokes.size() > 0 && rotateBitmap != null) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                    FetchDrawScreen.this);
            mBuilder.setCancelable(false)
                    .setTitle(getString(R.string.rotate))
                    .setMessage(getString(R.string.once_you_rotate_the_image_changes_made))
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setPositiveButton(getString(R.string.rotate),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Bitmap cs = null;

                                    framelayout.setDrawingCacheEnabled(false);
                                    drawingview.resetBGScalingAndTranslation();
                                    framelayout.setDrawingCacheEnabled(true);
                                    framelayout.measure(MeasureSpec
                                                    .makeMeasureSpec(
                                                            drawingview.canvasWidth,
                                                            MeasureSpec.EXACTLY),
                                            MeasureSpec.makeMeasureSpec(
                                                    drawingview.canvasHeight,
                                                    MeasureSpec.EXACTLY));
                                    framelayout.layout(0, 0,
                                            framelayout.getMeasuredWidth(),
                                            framelayout.getMeasuredHeight());
                                    framelayout
                                            .setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
                                    framelayout.buildDrawingCache();
                                    int[] ret = getBitmapPositionInsideImageView();
                                    cs = Bitmap.createBitmap(
                                            framelayout.getDrawingCache(),
                                            ret[0], ret[1], ret[2], ret[3]);
                                    framelayout.setDrawingCacheEnabled(false);
                                    clearAll();
                                    drawingview.invalidate();
                                    bgImg.setImageBitmap(rotateBitmap = AndroiUtils
                                            .rotateImage(cs, 90));

                                }
                            });
            AlertDialog mAlertDialog = mBuilder.create();
            mAlertDialog.show();
        } else if (rotateBitmap != null) {
            bgImg.setImageBitmap(rotateBitmap = AndroiUtils.rotateImage(
                    rotateBitmap, 90));
        }
    }

    // Line Spinner work

    AdapterView.OnItemSelectedListener drawLineSpinner = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (lineSpinnerFirstTimeSelected) {
                switch (position) {
                    case 0:
                        drawingview.mStrokeWidth = 5;
                        break;
                    case 1:
                        drawingview.mStrokeWidth = 10;
                        break;
                    case 2:
                        drawingview.mStrokeWidth = 15;
                        break;
                }
                drawingview.mCurrentShape = DrawingView.LINE;
            } else {
                lineSpinnerFirstTimeSelected = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    // Free draw

    OnClickListener drawClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            drawingview.mCurrentShape = DrawingView.DRAW;
        }
    };

    // Circle draw

    OnClickListener circleClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            drawingview.mCurrentShape = DrawingView.CIRCLE;
        }
    };

    // Square draw
    OnClickListener squareClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            drawingview.mCurrentShape = DrawingView.SQUARE;
        }
    };

    // private GoogleMap googleMap;

    // Set color

    public void colorChanged(int color) {
        lastColor = color;
        drawingview.colorChanged(lastColor);
    }

    // After picture received from camera or gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            clearAll();
            drawingview
                    .resetBGScalingAndTranslation();

            setImageToPicture(output_file.getPath());
            if (isDrawPhoto) {
                SaveBitmap();

            }
        } else if (requestCode == REQUEST_GALLERY
                && resultCode == Activity.RESULT_OK) {
            clearAll();
            drawingview
                    .resetBGScalingAndTranslation();
            Uri photoUri = data.getData();
            String photoPath = photoUri.getPath();

            if (photoUri != null) {
                Cursor query = getContentResolver().query(photoUri,
                        new String[]{ImageColumns.DATA}, null, null, null);
                if (query != null && query.moveToFirst()) {
                    int columnIndex = query.getColumnIndex(ImageColumns.DATA);
                    String path = query.getString(columnIndex);
                    // URIBitmap = path;

                    if (path.contains("QnopyPictures") || path.contains("QnopyDrawings")) {
                        waterMark = false;
                        //07-Apr-17 Disable water-marking if already water marked image
                    } else {
                        waterMark = true;
                    }

                    setImageToPicture(path);
                } else {
//                    URIBitmap = photoPath;

                    if (photoPath.contains("QnopyPictures") || photoPath.contains("QnopyDrawings")) {
                        waterMark = false;
                        //07-Apr-17 Disable water-marking if already water marked image
                    } else {
                        waterMark = true;
                    }

                    setImageToPicture(photoPath);
                }
                if (isDrawPhoto) {
                    SaveBitmap();
                }
            }
        } else if (requestCode == SCREEN_CAPTURE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {

            clearAll();
            drawingview
                    .resetBGScalingAndTranslation();

            File file = (File) data.getSerializableExtra("FILE_NAME");
            if (file != null) {
                setImageToPicture(file.getAbsolutePath());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        SendImageToView();

        if (isDrawPhoto) {
            SaveBitmap();
        }
    }

    // Make Rotation of Image

    private int imageRotation(String fileName) {
        try {
            ExifInterface exif = new ExifInterface(fileName);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;

            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            } else
                return 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // send image to drawing background

    private void SendImageToView() {
        if (rotateBitmap != null) {
            imgWidth = rotateBitmap.getWidth();
            imgHeight = rotateBitmap.getHeight();

            bgImg.setImageBitmap(rotateBitmap);
        }
    }

    // work for image out of memory

    public void setImageToPicture(String path) {
        try {

            Bitmap currentImage1 = null;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            currentImage1 = BitmapFactory.decodeFile(path, o2);
            Matrix csmatrix12 = new Matrix();
            int rotation = imageRotation(path);
            csmatrix12.preRotate(rotation);
            currentImage1 = Bitmap.createBitmap(currentImage1, 0, 0,
                    currentImage1.getWidth(), currentImage1.getHeight(),
                    csmatrix12, true);
            rotateBitmap = currentImage1;

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "setImageToPicture Error :" + ex.getMessage());
        }
    }

    // saving the view work as .png image

    private boolean SaveBitmap() {
        boolean flag = false;
        if (!SharedPref.isSaved()) {
            if (drawingview._allStrokes.size() > 0) {
                // addNotes();

                Bitmap cs = null;
                long filename = System.currentTimeMillis();
                framelayout.setDrawingCacheEnabled(false);
                framelayout.setDrawingCacheEnabled(true);
                framelayout
                        .setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
                framelayout.buildDrawingCache();
                try {
                    cs = framelayout.getDrawingCache();

                    String baseDir = "";
                    File folder = null;
                    String filename1 = getFilePrefix() + filename + ".png";
                    String thumbDir = "";

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        baseDir
                                = BaseMenuActivity.getMediaStorageDirOS11Up(BaseMenuActivity
                                .MEDIA_TYPE_DRAWING, this).getAbsolutePath();
                        folder = new File(baseDir);
                        thumbDir = baseDir + File.separator + GlobalStrings.THUMBNAILS_DIR
                                + File.separator;
                    } else {
                        baseDir = Environment
                                .getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES)
                                .getAbsolutePath();
                        folder = new File(baseDir, getSaveDirectory());

                        thumbDir = Util.createThumbDir(folder.getAbsolutePath());
                    }

                    if (isDrawPhoto) {
                        String dir = getSaveDirectory();
                        String actualPath = dir.substring(0, dir.lastIndexOf("/"));
                        baseDir = actualPath;
                        filename1 = dir.substring(dir.lastIndexOf("/") + 1);

                        folder = new File(baseDir);
                    }

                    folder.mkdir();
                    if (folder.exists()) {
                        File file = new File(folder, filename1);
                        file.createNewFile();
                        String fn = file.toString();
                        OutputStream os = null;
                        os = new FileOutputStream(file);
                        flag = cs.compress(CompressFormat.PNG, 100, os);

                        //thumbnail file
                        String thumbFileName = filename + GlobalStrings.THUMBNAIL_EXTENSION;
                        File dirThumbDest = new File(thumbDir, thumbFileName);

                        if (flag) {

                            SharedPref.setSaved();
                            FileToSave = file.getAbsolutePath();
                            if (isDrawPhoto) {
                                AttachmentDataSource attachDataSource = new AttachmentDataSource(mContext);
                                if (Add_waterMark) {

                                    double latitude = 0.0;
                                    double longitude = 0.0;

                                    if (currentLocation != null) {
                                        latitude = currentLocation.latitude;
                                        longitude = currentLocation.longitude;
                                    }

                                    updateAttachmentWithWatermark(FileToSave, latitude,
                                            longitude, dirThumbDest.getAbsolutePath());
                                    Add_waterMark = false;
                                }

                                attachDataSource.updateModificationDate(FileToSave, null, null);
                            } else {
                                attachmentDataInsertion(FileToSave, dirThumbDest.getAbsolutePath());
                            }
                            // drawingview.mCurrentShape = 0;
                            mDbHandler.deleteTableContent();

                            if (fn != null) {
                                synchronized (mDrawingsToScan) {
                                    mDrawingsToScan.add(fn);
                                    mPendingShareFile = fn;
                                    if (!mMediaScannerConnection.isConnected()) {
                                        // mMediaScannerConnection.connect();

                                        //07-Jul-17  throws exception (FetchDrawScreen has leaked ServiceConnection android.media.MediaScannerConnection@42451218 that was originally bound here)
                                        Uri contentUri = Uri.fromFile(file);
                                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        mediaScanIntent.setData(contentUri);
                                        sendBroadcast(mediaScanIntent);
                                        // will
                                        // scan
                                        // the
                                        // files
                                        // and
                                        // share
                                        // them
                                    }
                                }
                            }

                        }
                    }
                    cs.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Error er) {
                    er.printStackTrace();
                } catch (Exception e) {
                    System.out.println();
                }
                framelayout.setDrawingCacheEnabled(false);
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_changes_to_save), Toast.LENGTH_LONG).show();
        }
        return flag;
    }

    void updateAttachmentWithWatermark(String fileName, double lati, double longi, String dirThumbDest) {
        String currentLocationName = Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_LOCATIONNAME);
        double capturedLatitude = lati;
        double capturedLongitude = longi;


        Bitmap scaledBitmap = BitmapFactory.decodeFile(fileName);

        //06-Apr-17 Add Water mark
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
//            Shader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

        String roundedLat = Util.RoundUpto_AfterDecimal(capturedLatitude, 6);
        String roundedLongi = Util.RoundUpto_AfterDecimal(capturedLongitude, 6);
        String waterMarkString = dateTime + "|Location:" + currentLocationName + "|Latitude:" + roundedLat +
                " , Longitude:" + roundedLongi;
        int y_start_position = 0;
        int height = scaledBitmap.getHeight();
        if ((int) height < 550) {
            y_start_position = (int) (height - 40);

        } else {
            y_start_position = (int) (height - (height * 0.045));
        }

        Point pont = new Point();
        pont.x = 20;
        pont.y = y_start_position;//1% of total height is reduced and is a start point

        ScreenReso application = new ScreenReso();
        application.getScreenReso(mContext);
        int density = (int) application.getDensity();

        int textSize = 0;

        if (density >= 240) {
            if ((int) height < 500) {
                textSize = 8;
            } else {
                textSize = 15;
            }
        } else {
            textSize = 15;
        }


        Log.i(TAG, "Water mark text size:" + textSize);
        //height < 500 ? (int) (height * 0.020) * density : (int) (height * 0.011) * density
        scaledBitmap = CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.WHITE, 100, textSize, false);


//        scaledBitmap = CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.RED, 100,(int) (scaledBitmap.getHeight()*0.025), false);
        // scaledBitmap = CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.RED, 100, height < 500 ? (int) (height * 0.020) * density : (int) (height * 0.011) * density, false);


        FileOutputStream out = null;
        // String filename = getFilename();

        File outfile = new File(fileName);
        try {
            out = new FileOutputStream(outfile);
            scaledBitmap.compress(CompressFormat.PNG, 100, out);

            Bitmap thumbBmp = Util.getResizedBitmap(scaledBitmap, 160, 160);
            Util.saveBitmapToSDCard(thumbBmp, new File(dirThumbDest), GlobalStrings.COMPRESSION_RATE_100);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "() error:" + e.getMessage());
        }
    }

    void attachmentDataInsertion(String fileName, String thumbPath) {
        String currentLocationName = Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_LOCATIONNAME);
        double capturedLatitude = 0.0;
        double capturedLongitude = 0.0;

        if (currentLocation != null) {
            capturedLatitude = currentLocation.latitude;
            capturedLongitude = currentLocation.longitude;
        }

        if (waterMark) {
            Bitmap scaledBitmap = BitmapFactory.decodeFile(fileName);

            //06-Apr-17 Add Water mark
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
//            Shader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

            String roundedLat = Util.RoundUpto_AfterDecimal(capturedLatitude, 6);
            String roundedLongi = Util.RoundUpto_AfterDecimal(capturedLongitude, 6);
            String waterMarkString = dateTime + "|Location:" + currentLocationName + "|Latitude:" + roundedLat +
                    " , Longitude:" + roundedLongi;

//            Point pont = new Point();
//            pont.x = 20;
//            pont.y = (int) (scaledBitmap.getHeight() - 100);
//            scaledBitmap = CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.RED, 100, 25, false);

            int y_start_position = 0;
            int height = scaledBitmap.getHeight();
            if ((int) height < 500) {
                y_start_position = (int) (height - 40);

            } else {
                y_start_position = (int) (height - (height * 0.045));
            }

            Point pont = new Point();
            pont.x = 20;
            pont.y = y_start_position;//1% of total height is reduced and is a start point
            Log.i(TAG, "attachmentDataInsertion() Water mark y_start_position:" + y_start_position);

//            int density=(int) mContext.getResources().getDisplayMetrics().density;

//        scaledBitmap = CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.RED, 100,(int) (scaledBitmap.getHeight()*0.025), false);

            ScreenReso application = new ScreenReso();
            application.getScreenReso(mContext);
            int density = (int) application.getDensity();

            int textSize = 0;

            if (density >= 240) {
                if ((int) height < 500) {
                    textSize = 8;
                } else {
                    textSize = 15;
                }
            } else {
                textSize = 15;
            }

            Log.i(TAG, "attachmentDataInsertion() Water mark text size:" + textSize);
            //height < 500 ? (int) (height * 0.020) * density : (int) (height * 0.011) * density
            scaledBitmap = CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.WHITE, 100, textSize, false);

            FileOutputStream out = null;
            // String filename = getFilename();

            File outfile = new File(fileName);
            try {

                out = new FileOutputStream(outfile);
                scaledBitmap.compress(CompressFormat.PNG, 100, out);
                Bitmap thumbBmp = Util.getResizedBitmap(scaledBitmap, 160, 160);
                Util.saveBitmapToSDCard(thumbBmp, new File(thumbPath), GlobalStrings.COMPRESSION_RATE_100);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "() error:" + e.getMessage());
            }
        }

        AttachmentData attachData = new AttachmentData();
        AttachmentDataSource attachDataSource = new AttachmentDataSource(mContext);
        attachData.setEventID(getEventID());
        attachData.setLocationID(getLocID());
        attachData.setAttachementType("D");
        attachData.setFileLocation(fileName);
        attachData.setCreationDate(System.currentTimeMillis());
        attachData.setLatitude(capturedLatitude);
        attachData.setLongitude(capturedLongitude);
        attachData.setDataSyncFlag(null);
        attachData.setEmailSentFlag(null);
        attachData.setSiteId(getSiteID());
        attachData.setUserId(getUserID());
        attachData.setMobileAppId(getMobileAppID());
        attachData.setTimeTaken(System.currentTimeMillis());
        attachData.setSetId(getSetID());

        attachData.setFile1000(fileName);
        attachData.setFileThumb(thumbPath);

//        attachData.setAzimuth(mSensorTracker.getAzimuthInDegress()+"");
        if (notes != null) {
            attachData.setNotes(notes);
        }
        try {
            attachDataSource.insertAttachmentData(attachData, false);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, " Error in attachmentDataInsertion :" + e.getMessage());
        }
    }

    // on Selecting color
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.red:
                drawingview.colorChanged(Color.parseColor("#ff0000"));
                break;
            case R.id.blue1:
                drawingview.colorChanged(Color.parseColor("#00ffff"));
                break;
            case R.id.blue2:
                drawingview.colorChanged(Color.parseColor("#0000ff"));
                break;
            case R.id.green:
                drawingview.colorChanged(Color.parseColor("#00ff00"));
                break;
            case R.id.black:
                drawingview.colorChanged(Color.parseColor("#000000"));
                break;
            case R.id.white:
                drawingview.colorChanged(Color.parseColor("#ffffff"));
                break;
            case R.id.magenta:
                drawingview.colorChanged(Color.parseColor("#ff00ff"));
                break;
            case R.id.yellow:
                drawingview.colorChanged(Color.parseColor("#ffff00"));
                break;
        }
    }

    private Bitmap CreateWaterMark(Context context, Bitmap src, String watermark, Point location, int color, int alpha, int textsize, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        String[] values = watermark.split("\\|");
        Canvas canvas = new Canvas(result);

        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(src, 0, 0, null);

//        Paint paint = new Paint();
//        paint.setColor(color);
////        paint.setAlpha(alpha);
//        paint.setTextSize(size);
//        paint.setAntiAlias(true);
//        paint.setUnderlineText(underline);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawText(values[0], location.x, location.y, paint);
//        canvas.drawText(values[1], location.x, location.y + size+5, paint);
//        canvas.drawText(values[2], location.x, location.y + size+size+10, paint);

        Log.i(TAG, "Water-Mark : " + values[0] + "\n" + values[1] + "\n" + values[2]);

        //method 2
        TextView textView = new TextView(context);
        textView.layout(0, 0, w, h); //text box size 300px x 500px


        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        textView.setTextColor(color);
        textView.setBackgroundResource(R.drawable.textview_shader_bg);
        textView.setPadding(20, 5, 5, 5);
//        textView.setShadowLayer(5, 2, 2, Color.BLACK); //text shadow
        if (values[0] == null || values[0].isEmpty()) {
            values[0] = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME);
        }
        textView.setText(values[0] + "\n" + values[1] + "  " + values[2]);
        textView.setDrawingCacheEnabled(true);

        try {
            canvas.drawBitmap(textView.getDrawingCache(), 0, location.y, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHandler.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_drawing, menu);
/*
        menu.findItem(R.id.user).setTitle("Hi " + username + "!");
        if (!mContext.getResources().getString(R.string.screen_type).equals("Phone")) {
            menu.findItem(R.id.save_drawing).setVisible(false);
        } else {
            menu.findItem(R.id.save_drawing).setVisible(true);
        }

        AppPreferenceDataSource ds = new AppPreferenceDataSource(mContext);
        //KEY_PROJECT_FILE
        if (ds.isFeatureAvailable(GlobalStrings.KEY_PROJECT_FILE, getUserID())) {
            menu.findItem(R.id.filefolder).setVisible(true);
        } else {
            menu.findItem(R.id.filefolder).setVisible(false);
        }
*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                if (SharedPref.isSaved()) {
                    sendResultIntent();
                    finish();
                } else if (!SharedPref.isSaved()) {
                    checkForUnsaved();
                    getPreviousImage();
                } else {
                    finish();
                }
                break;

            case R.id.filefolder:

                List<FileFolderItem> list = new FileFolderDataSource(mContext).getHomeFileFolderItemList(siteID + "");

                if (list.size() < 1) {
                    startActivity(new Intent(mContext, FileFolderSyncActivity.class));
                } else {
                    startActivity(new Intent(mContext, FileFolderMainActivity.class));
                }

                break;

            case R.id.save_drawing:
                onSaveClick();
                break;
        }
        return true;
    }

    int getEventID() {
        return eventID;
    }

    void setEventID(int eventID) {
        this.eventID = eventID;
    }

    String getLocID() {
        return locID;
    }

    void setLocID(String locID) {
        this.locID = locID;
    }

    int getSiteID() {
        return siteID;
    }

    void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    int getUserID() {
        return userID;
    }

    void setUserID(int userID) {
        this.userID = userID;
    }

    int getMobileAppID() {
        return mobileAppID;
    }

    void setMobileAppID(int mobileAppID) {
        this.mobileAppID = mobileAppID;
    }

    String getFilePrefix() {
        return filePrefix;
    }

    void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    String getSaveDirectory() {
        return saveDirectory;
    }

    void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    int getSetID() {
        return setID;
    }

    void setSetID(int setID) {
        this.setID = setID;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (SharedPref.isSaved()) {
            sendResultIntent();
            finish();
        } else if (!SharedPref.isSaved()) {
            checkForUnsaved();
            getPreviousImage();
        } else {
            finish();
        }
    }

    public void sendResultIntent() {
        Intent output = new Intent();
        output.putExtra("SAVED_FILE_NAME", FileToSave);
        System.out.println("gggg" + "Draw savedFileName=" + FileToSave);
        setResult(RESULT_OK, output);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // if(googleMap != null)
        // {
        // if(lt != null) {
        // // lt = new LatLng(mLocationClient.getLastLocation().getLatitude(),
        // mLocationClient.getLastLocation().getLongitude());
        // CameraPosition currentLocation = new
        // CameraPosition.Builder().target(lt).zoom(19f).bearing(350).build();
        //
        // googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentLocation));
        // }
        // }
        // Return false so that we don't consume the event and the default
        // behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onLocationDeny() {
        //no use
    }
}
