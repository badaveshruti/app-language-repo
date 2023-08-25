package qnopy.com.qnopyandroid.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.GridViewAdapter;
import qnopy.com.qnopyandroid.clientmodel.GridImageItem;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.db.AttachmentData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.fetchdraw.FetchDrawScreen;
import qnopy.com.qnopyandroid.sensors.MobileSensor;
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Saurabh on 21-Oct-15.
 */
public class NoteDialogBoxActivity extends BaseMenuActivity {
    Context mContext;
    AlertDialog.Builder builder;
    String item = "";
    private String appName = null;
    private int parentAppID = 0;
    private int curSetID = 0; // Indicates the currently loaded set in the form
    private int activeSetID = 0;
    private String locID = "0";
    private int compnyID = 0;
    private int currentFormNum = 0;
    public List<MobileApp> childAppList = null;
    List<EditText> allEds = new ArrayList<EditText>();
    String text = null;
    ArrayList<String> textvalueList = new ArrayList<>();
    //    ArrayList<String> DbList = new ArrayList<>();
    ArrayList<String> labelList = new ArrayList<>();
    EditText note;

    LinearLayout linear;

    String notes, siteName = null;//noteImage
    Bundle extras;
    int eventID, setID, paramID, siteID, appID, userID;
    String username, password, locationID;
    AttachmentData attachData = new AttachmentData();
    FieldDataSource fieldDataSource;
    String TAG = NoteDialogBoxActivity.class.getSimpleName();
    MobileSensor mSensorTracker;

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    // CountyResponseModel eventresponse;
    List<String> multinoteList = new ArrayList<>();
    List<String> strlist = new ArrayList<>();

    Map<String, String> map = new HashMap<String, String>();
    TextView emptyView, textcounter, errortxt;
    ActionBar actionBar;
    private Options pickerOptions;
    //29-Apr-17 Multi-Photos

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // this.requestWindowFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        //        showAsPopup(this);// TODO: 11-Dec-15
        setContentView(R.layout.note_dialogbox);
        CurrentContext = this;
        mContext = this;

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        currentLocationName = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.CURRENT_LOCATIONNAME);
        fieldDataSource = new FieldDataSource(mContext);

        mSensorTracker = new MobileSensor(mContext);

        note = findViewById(R.id.noteText);
        textcounter = findViewById(R.id.textViewCounter);
        errortxt = findViewById(R.id.textViewError);

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int res = 2000 - s.length();
                Log.i(TAG, "Total Character entered:" + s.length() + " result:" + res);
//                textcounter.setText("" +res);

                if (res <= 0) {
                    textcounter.setText("" + 0);
                    errortxt.setVisibility(View.VISIBLE);
                    errortxt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            errortxt.setVisibility(View.GONE);
                        }
                    }, 5000);
//                    note.setError(GlobalStrings.text_input_limit_alert);
                } else {
                    textcounter.setText("" + res);
                    errortxt.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        note.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (note.getRight() - note.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (!note.getText().toString().isEmpty()) {
                        note.setText("");
                        clearNote();
                    }
                    return true;
                }
            }
            return false;
        });


        linear = findViewById(R.id.linear);

        gridView = findViewById(R.id.gridView);
        emptyView = findViewById(R.id.emptyView);

        extras = getIntent().getExtras();
        eventID = extras.getInt("EVENT_ID");
        locationID = extras.getString("LOCATION_ID");
        appID = extras.getInt("APP_ID");
        siteID = extras.getInt("SITE_ID");
        paramID = extras.getInt("paramID");
        setID = extras.getInt("setID");
        siteName = extras.getString("SiteName");
        password = extras.getString("Password");
        username = extras.getString("UserName");
        userID = extras.getInt("UserID");

        Log.i(TAG, "Received Extras eventid:" + eventID + ",Loc:" + locationID + ",app:" + appID + ",site:" + siteID + ",param:" + paramID
                + ",set:" + setID + ",sitename:" + siteName + ",pass:" + password + ",username:" + username + ",userID:" + userID);

        setAttachmentNamePrefix(System.currentTimeMillis() + "");//(siteName + "_" + locSource.getLocationName(locationID) + "_").replace(" ", "_")

        MetaDataSource metaDataSource = new MetaDataSource(mContext);
        String multinote = metaDataSource.getMultinotes(appID, paramID);

        if (multinote != null) {
            //  List<String> multinoteList= Arrays.asList(multinote.split("|"));
            String[] value_split = multinote.split("\\|");

            multinoteList = Arrays.asList(value_split);
        }

        if (multinoteList.size() != 0) {
            for (int i = 0; i < multinoteList.size(); i++) {

                //note.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout linearview = new LinearLayout(mContext);

                linearview.setLayoutParams(params);
                linearview.setPadding(5, 3, 5, 3);
                linearview.setGravity(Gravity.CENTER);
                linearview.setWeightSum(2);
                linearview.setOrientation(LinearLayout.VERTICAL);
                TextView labeltext = new TextView(mContext);
                String text = multinoteList.get(i).trim();
                labeltext.setText(text + " :");
                labeltext.setTextSize(20);
                labeltext.setTypeface(Typeface.DEFAULT_BOLD);
                labelList.add(text);
//              labeltext.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                labeltext.setTextColor(mContext.getResources().getColor(R.color.qnopy_teal));
                labeltext.setPadding(10, 5, 10, 5);
                linearview.addView(labeltext);
                EditText edtlabel = new EditText(mContext);
//              edtlabel.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                edtlabel.setHint(getString(R.string.enter_text));
//              edtlabel.setLines(3);
                edtlabel.setTextSize(20);
                edtlabel.setTag(text);
                edtlabel.setSingleLine(true);
                edtlabel.setImeOptions(EditorInfo.IME_ACTION_DONE);
                edtlabel.setId(i);

                edtlabel.setBackgroundResource(R.drawable.data_entry_control_bg);
                edtlabel.setPadding(10, 8, 10, 8);

                // TODO: 13-Jun-17 SET MAX LIMIT 2000 TO COLLECT DATA
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(2000);
                edtlabel.setFilters(FilterArray);

                allEds.add(edtlabel);
                edtlabel.setTextColor(mContext.getResources().getColor(R.color.color_chooser_black));
                linearview.addView(edtlabel);
                linear.addView(linearview);

            }
        }

        setCurrentNotesFromDB(paramID);
        setAdapter();


//        notePhotoIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (noteImage != null && !noteImage.isEmpty())
//                    removeImage(noteImage);
//            }
//        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridImageItem item = (GridImageItem) parent.getItemAtPosition(position);

                Log.i(TAG, "Grid View File position(" + position + ") Clicked:" + item.getImage_path());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorTracker.registerSensorService();
        Log.i(TAG, "Sensor Status- ACCELEROMETER =" + mSensorTracker.isAccelerometerAvailable());
        Log.i(TAG, "Sensor Status- MAGNETOMETER =" + mSensorTracker.isMagnetoMeterAvailable());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorTracker.unregisterSensorService();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (output_file != null) {
            outState.putString("cameraImageUri", output_file.getPath());
            Log.i(TAG, "onSaveInstanceState file path :" + output_file.getPath());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "dispatchTouchEvent()");
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_actions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveNote();
                return true;

            //action cancel and home are merged so there is nothing added in action
            case R.id.action_cancel:

            case android.R.id.home:
                setResultForNote();
                finish();
                return true;

            case R.id.action_camera:
                openImagePicker();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResultForNote();
        super.onBackPressed();
    }

    public void setAdapter() {
        ArrayList<GridImageItem> items = getData();
        if (items.size() < 1) {
            gridView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            gridView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            gridAdapter = new GridViewAdapter(CurrentContext, R.layout.grid_item_layout, items);
            gridView.setAdapter(gridAdapter);
        }
    }

    public void setEmpty() {
        gridView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<GridImageItem> getData() {
        ArrayList<GridImageItem> imageItems = new ArrayList<>();
        AttachmentDataSource attchDataSource = new AttachmentDataSource(mContext);
        imageItems = attchDataSource.getNoteAttachment(eventID, paramID, setID, appID, locationID, siteID);
        Log.i(TAG, "List of images captured:" + imageItems.size());
        return imageItems;
    }

    public void setCurrentNotesFromDB(int fieldParameterID) {
        FieldDataSource fieldDataSource = new FieldDataSource(mContext);
        MetaDataSource metaDataSource = new MetaDataSource(mContext);

        notes = fieldDataSource.getParamLabelNotes(fieldParameterID, setID, appID, locationID, siteID);

        String mnote = metaDataSource.getMultinotes(appID, paramID);
        if (mnote != null) {
            if (notes != null && notes.contains("|")) {
                String[] value_split = notes.split("\\|");
                for (String s : value_split) {
                    if (s.contains(":")) {
                        String[] t = s.split("\\:");
                        if (t.length > 1) {
                            map.put(t[0], t[1].trim());
                        } else {
                            map.put(t[0], null);
                        }
                    }
                }

                for (String s : map.keySet()) {

                    if (s.equalsIgnoreCase("Note")) {
                        note.setText(map.get(s));
                    }
                    System.out.println(s + " is " + map.get(s));

                    String multinote = metaDataSource.getMultinotes(appID, paramID);

                    String[] arr = multinote.split("\\|");

                    strlist = Arrays.asList(arr);
                    for (int m = 0; m < strlist.size(); m++) {
                        String val = strlist.get(m).trim();
                        if (s.equalsIgnoreCase(val)) {
                            String value1 = map.get(s);
                            for (int j = 0; j < allEds.size(); j++) {
                                String tag = String.valueOf(allEds.get(j).getTag());
                                if (s.equalsIgnoreCase(tag)) {
                                    allEds.get(j).setText(value1);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            notes = fieldDataSource.getParamLabelNotes(fieldParameterID, setID, appID, locationID, siteID);
            note.setText(notes);

            //13-Jun-17 Set Counter
            if (notes != null) {
                int position = notes.length();
                note.setSelection(position > 2000 ? 2000 : position);
                int count = 2000 - notes.length();
                Log.i(TAG, "Total Character entered:" + notes.length() + " result:" + count);
//                textcounter.setText("" +res);

                if (count <= 0) {
                    textcounter.setText("" + 0);
                    errortxt.setVisibility(View.VISIBLE);
                    errortxt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            errortxt.setVisibility(View.GONE);
                        }
                    }, 5000);
//                    note.setError(GlobalStrings.text_input_limit_alert);
                } else {
                    textcounter.setText("" + count);
                    errortxt.setVisibility(View.GONE);
//                    note.setError(null);
//                    textcounter.setText("" + count);
                }
            }
        }
    }

    //changed name from handleCamera 8 May, 2020
    public void openImagePicker() {
        try {
            if (getAttachmentNamePrefix() == null) {
                Toast.makeText(this, "Name Prefix not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            output_file = getOutputMediaFile(MEDIA_TYPE_IMAGE, "p_"
                    + getAttachmentNamePrefix(), this);

            Intent mediaIntent = new Intent(this, MediaPickerActivity.class);
            mediaIntent.putExtra(GlobalStrings.IS_CAMERA, true);
            startActivityForResult(mediaIntent,
                    REQUEST_CODE_NOTE_MEDIA_PICKER);
          /*  initGalleryPicker(MEDIA_TYPE_IMAGE);
            Pix.start(this, pickerOptions);*/
            GlobalStrings.captureTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("NoteDialogBox", "openImagePicker Error:" + e.getMessage());
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
                        getMediaStorageDirectory(type).getAbsolutePath()
                );
    }

    public int getCurrentFormNum() {
        return currentFormNum;
    }

    void setCurrentFormNum(int num) {
        currentFormNum = num;
    }

    public int getCurrentAppID() {
        try {
            return childAppList.get(getCurrentFormNum()).getAppID();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // return currentAppID;
        return 0;
    }

    public String getLocID() {
        return locID;
    }

    public void setLocID(String locID) {
        this.locID = locID;
    }

    public int getCurSetID() {
        return curSetID;
    }

    public void setCurSetID(int curSetID) {
        this.curSetID = curSetID;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public int getSiteID() {
        return siteID;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    /**
     * @return the eventID
     */
    public int getEventID() {
        return eventID;

    }

    /**
     * @param eventID the eventID to set
     */
    void setEventID(int eventID) {
        this.eventID = eventID;
    }

    /**
     * @param siteID the siteID to set
     */
    void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    /**
     * @return the siteName
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * @param siteName the siteName to set
     */
    void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the appName
     */
    String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @param parentAppID the parentAppID to set
     */
    void setParentAppID(int parentAppID) {
        this.parentAppID = parentAppID;
    }

    public void handleDrawing() {
        Intent intent = new Intent(this,
                FetchDrawScreen.class);
        intent.putExtra("USER_NAME", username);
        intent.putExtra("PASS", password);
        intent.putExtra("EVENT_ID", eventID);
        intent.putExtra("LOC_ID", locationID);
        intent.putExtra("SITE_ID", siteID);
        intent.putExtra("USER_ID", userID);
        intent.putExtra("MOBILE_APP_ID", appID);
        intent.putExtra("SET_ID", setID);
        intent.putExtra("FILE_NAME_PREFIX", "d_" + getAttachmentNamePrefix()); // set

        intent.putExtra("SAVE_DIRECTORY", GlobalStrings.DRAWING_STORAGE_DIR);

        try {
            startActivityForResult(intent, DRAWING_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            Log.e("NoteDialogBox", "handleDrawing Error:" + e.getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GlobalStrings.COMPRESS_IMAGE = Boolean.parseBoolean(Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.IS_COMPRESS_IMAGE));

        try {
            System.out.println("NoteDialogBox Activity OnActivityResult code=" + requestCode);
            if ((requestCode == REQUEST_CODE_PIX_NOTE_IMAGE_PICKER)
                    && resultCode == RESULT_OK && data != null) {
                ArrayList<String> pathList = data.getStringArrayListExtra((Pix.IMAGE_RESULTS));
                if (pathList != null) {
                    if (pathList.size() > 0) {

                        if (!new File(pathList.get(0)).exists()) {
                            CustomToast.showToast((Activity) mContext,
                                    getString(R.string.unable_to_show_note_image), 5);
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
                    addWaterMarkToImage();
                }
            } else if ((requestCode == REQUEST_CODE_NOTE_MEDIA_PICKER)
                    && resultCode == RESULT_OK && data != null) {
                String path = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_PATH);
                String path1000 = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_1000_PATH);
                String pathThumb = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_THUMB_PATH);

                if (path != null) {
                    output_file = new File(path);
                    addWaterMarkToImage();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Set note Image error:" + e.getMessage());
            CustomToast.showToast((Activity) mContext,
                    getString(R.string.unable_to_show_note_image), 5);
        }
    }

    private void addWaterMarkToImage() {
        if ((output_file != null)) {
            try {

                compressImage(output_file.getPath());

                String filePath = output_file.getPath();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa", Locale.ENGLISH);
                String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                //Shader shader = new LinearGradient(0, 0, 100, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

                String roundedLat = Util.RoundUpto_AfterDecimal(capturedLatitude, 6);
                String roundedLongi = Util.RoundUpto_AfterDecimal(capturedLongitude, 6);
                String waterMarkString = dateTime + "|Location:" + currentLocationName + "|Latitude:" + roundedLat +
                        " , Longitude:" + roundedLongi;

                Util.WaterMarkPhoto(CurrentContext, filePath, waterMarkString);
                Log.i(TAG, "Image Attached:" + filePath);
                saveNotePhoto(filePath, "P", mSensorTracker.getAzimuthInDegress());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in Photo Capture:" + e.getMessage());
            }

            // openGallery();

        } else {
            CustomToast.showToast((Activity) mContext,
                    "Unable to attach the Picture", 10);
        }
    }


    public static void showAsPopup(Activity activity) {
        //To show activity as dialog and dim the background, you need to declare android:theme="@style/PopupTheme" on for the chosen activity on the manifest
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT; //fixed width
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        activity.getWindow().setAttributes(params);
    }

    void saveNote() {
        textvalueList = new ArrayList<>();

        try {
            String val = "";

            notes = note.getText().toString();

            if (notes.contains("|") || (notes.contains(":"))) {
                if (notes.contains("|")) {
                    notes = notes.replace("|", ",");
                }
                if (notes.contains(":")) {
                    notes = notes.replace(":", "-");
                }
            }
            String[] items = new String[allEds.size()];
            if (items.length > 0) {

                for (int i = 0; i < allEds.size(); i++) {

                    items[i] = allEds.get(i).getText().toString();
                    if (items[i].equals("")) {
                        items[i] = "  ";
                    }
                    if ((items[i].contains("|")) || (items[i]).contains(":")) {
                        String data = items[i];
                        if (data.contains("|")) {

                            data = data.replace("|", ",");
                        }
                        if (data.contains(":")) {
                            data = data.replace(":", "-");

                        }
                        items[i] = data;

                    }

                    textvalueList.add(items[i]);
                }

                val = "note:" + notes.concat("|");
                for (int j = 0; j < labelList.size(); j++) {
                    if (j < (labelList.size() - 1)) {
                        val += labelList.get(j).concat(":").concat(textvalueList.get(j)).concat("|");
                    } else {
                        val += labelList.get(j).concat(":").concat(textvalueList.get(j));
                    }
                }
                notes = val;//note:|action:|findings:
            }

//            if(notesValue!="") {
            fieldDataSource.updateNotesForLabel(eventID + "", paramID, setID,
                    locationID, notes, siteID, appID);
            AttachmentDataSource atd = new AttachmentDataSource(mContext);
            if (atd.isAttachNoteExists(eventID + "", locationID + "",
                    siteID + "", appID + "", setID + "",
                    paramID + "")) {
                atd.updateMultiNotes(siteID, eventID + "", notes, paramID + "",
                        locationID, appID + "", setID + "", "");//sending fileLoc as empty as we dont have loc here
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("NoteDialogBox", "Post Note Error:" + e.getMessage());
            Toast.makeText(mContext, getString(R.string.post_note_error) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setResultForNote();
        finish();
    }

    void clearNote() {
        textvalueList = new ArrayList<>();

        try {

            notes = note.getText().toString();

            fieldDataSource.updateNotesForLabel(eventID + "", paramID, setID, locationID, notes, siteID, appID);

            AttachmentDataSource atd = new AttachmentDataSource(mContext);
            if (atd.isAttachNoteExists(eventID + "", locationID + "",
                    siteID + "", appID + "", setID + "",
                    paramID + "")) {
                atd.updateMultiNotes(siteID, eventID + "", notes, paramID + "",
                        locationID, appID + "", setID + "", "");//sending fileLoc as empty as we dont have loc here
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.i("NoteDialogBox", "Post Note Error:" + e.getMessage());
            }
            Toast.makeText(mContext, getString(R.string.post_note_error) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setResultForNote();
    }

    private void setResultForNote() {
        AttachmentDataSource atd = new AttachmentDataSource(mContext);
        FieldDataSource fds = new FieldDataSource(mContext);

        boolean result = fds.isNoteTaken_Data(eventID, setID,
                locationID, siteID, appID,
                paramID);

        if (!result) {
            result = atd.isAttachNoteExists(eventID + "", locationID + "", siteID + "", appID + "", setID + "", paramID + "");
        }

        Log.i(TAG, "setResultForNote() result:" + result);

        Intent intent = new Intent();
        //  intent.putExtras(bundle);
        intent.putExtra("POSITION", paramID);
        intent.putExtra("IS_NOTE_VALUE", result);

        setResult(RESULT_OK, intent);
    }

    private boolean saveNotePhoto(String imagePath, String type, String azimuth) {
        attachData = new AttachmentData();

        attachData.setAttachementType(type);
        attachData.setAzimuth(azimuth);

        attachData.setEventID(eventID);
        attachData.setLocationID(locationID);
        //attachData.setAttachementType("P");

        attachData.setSiteId(siteID);
        attachData.setUserId(userID);
        attachData.setMobileAppId(appID);

        //09-Nov-15  Added timeTaken,extField2 and notes in attachment

        attachData.setTimeTaken(Long.parseLong(setID + ""));
        attachData.setNotes(notes);
        attachData.setExtField2("" + paramID);
        attachData.setFieldParameterID("" + paramID);

        attachData.setFileLocation(imagePath);

        attachData.setCreationDate(System.currentTimeMillis());
        Log.i(TAG, "Current note photo location(lat,longi):" + capturedLatitude + " , " + capturedLongitude);

        attachData.setLatitude(capturedLatitude);
        attachData.setLongitude(capturedLongitude);
        attachData.setDataSyncFlag(null);
        attachData.setEmailSentFlag(null);
        attachData.setSetId(setID);
        attachData.setAzimuth(mSensorTracker.getAzimuthInDegress());

        int ret = (int) new AttachmentDataSource(mContext).insertNoteAttachmentData(attachData);
        Log.i(TAG, "saveNotePhoto() result=" + ret);
//        Toast.makeText(mContext, "Photo Attached.", Toast.LENGTH_LONG).show();

        setAdapter();
        return ret > 0;
    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//            mGravity = event.values;
//        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//            mGeomagnetic = event.values;
//        if (mGravity != null && mGeomagnetic != null) {
//            float R[] = new float[9];
//            float I[] = new float[9];
//
//            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
//            if (success) {
//                float orientationData[] = new float[3];
//                SensorManager.getOrientation(R, orientationData);
//                azimuth = orientationData[0];
//                pitch = orientationData[1];
//                roll = orientationData[2];
//
//                azimuthInDegress = (int) Math.toDegrees(azimuth);
//                if (azimuthInDegress < 0) {
//                    azimuthInDegress += 360;
//                }
//                // now how to use previous 3 values to calculate orientation
//            }
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
}