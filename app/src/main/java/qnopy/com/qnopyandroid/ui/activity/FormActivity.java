package qnopy.com.qnopyandroid.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fxn.pix.Options;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.Event;
import qnopy.com.qnopyandroid.clientmodel.FieldData;
import qnopy.com.qnopyandroid.clientmodel.FieldParamInfo;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.clientmodel.MetaDataAttributes;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.customView.CustomBoldTextView;
import qnopy.com.qnopyandroid.customView.CustomEditText;
import qnopy.com.qnopyandroid.customView.CustomItalicTextView;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.AttachmentData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.DefaultValueDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataAttributesDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.fetchdraw.FetchDrawScreen;
import qnopy.com.qnopyandroid.requestmodel.CustomerSign;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.sensors.MobileSensor;
import qnopy.com.qnopyandroid.ui.forms.FormsAdapter;
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity;
import qnopy.com.qnopyandroid.uiutils.FormMaster;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;

public abstract class FormActivity extends BaseMenuActivity {

    public static final int REQUEST_CODE_FORM_REPORT = 1897;
    public static final int REQUEST_CODE_TASK = 1848;
    public static final int CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE = 104;
    public static final int CAPTURE_GPS_LOCATION_REQUEST_CODE = 173;
    public static final int AUTOCOMPLETE_REQUEST_CODE = 106;
    public static final int CAPTURE_NOTE_REQUEST_CODE = 107;
    private static final String TAG = "FormActivity";
    private static final int REQUEST_CODE_OS11UP_MEDIAPICKER = 1562;
    //    Context context;
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 17845;
    public static int eventIDChanged = 0;
    public static String locationIDChanged = "0";
    public static boolean IS_COC_LOCATION = false;
    static int currentFormColor = 0;
    static boolean fromMap = false;
    final byte NUMERIC = 1;
    final byte TEXT = 2;
    final Integer[] image = {R.color.colorPrimary};
    public String date = null;
    public String time = null;
    public long measurementTime = 0;
    public boolean isMandatoryfilled = false;
    public HashMap<String, FormMaster.DataHolder> mapObject;
    public List<MetaData> metaValues;
    public boolean doneClicked = false, showOneTimeAlert = true;
    public Window mWindow;
    public int reqFieldCount = 0;
    public boolean isSignature = false;
    public CustomBoldTextView tvNotifyUploadStatus;
    public CustomTextView tvCalculate;
    public Context appContext = null;
    public List<MobileApp> childAppList = null;
    public List<SSiteMobileApp> dispnamelist = null;
    public String deviceID;
    public MobileSensor mSensorTracker;
    public String notes = null;
    public String currCocID;
    protected int eventID = 0;
    protected int siteID = 0;
    protected String siteName = null;
    protected String username = null;
    protected String appName = null;
    protected String password = null;
    protected int userID = 0;
    protected int parentAppID = 0;
    protected String locID = "0";
    protected int compnyID = 0;
    protected double correctedLat = 0;
    protected double correctedLong = 0;
    protected List<FieldData> currentReading = null;
    protected String locName;
    protected String locDesc;

    FormMaster formMaster;
    FrameLayout tabTaskContentFrame;
    List<MetaData> metaDataList = new ArrayList<>();
    Bundle extras;
    FloatingActionButton fab_new_reading;
    FloatingActionButton fab_delete_current_reading;
    Button done;
    TextView alertBar;
    ProgressBar form_loading_bar;
    LinearLayout setInfo_container;
    SwipeRefreshLayout swipe_refresh;
    NestedScrollView nested_scroll_view;
    LinearLayout locationdetail_master_container;
    Event event = new Event();
    String sitePath = null;
    String siteImageName = null;
    List<GalleryItem> list = null;
    AlertDialog.Builder builder;
    //    public ListView listView = null;
    Gallery form_gallery;
    float density = 0;
    FloatingActionsMenu menuMultipleActions;
    EditText editText = null;
    String depthText = null; // for well log
    boolean isShowGallery = false;
    ImageButton setNavLeft, setNavRight;
    TextView setCount;
    int formDefault = 0;
    // sub forms related
    MobileAppDataSource mobileAppSource = null;
    int maxApps = 0;
    AlertDialog closedialog;
    PopulateLocationDetailsTask loadForm_Task;
    int LOAD_FORM = 0, LOAD_SET_DATA = 1;
    String mPermissionCamera = Manifest.permission.CAMERA;
    long startTime = 0;
    long endTime = 0;
    EditText notesInput = null;
    MetaData notesMetaData = null;
    private boolean invalidating = false;
    private double depth = 0; // used only for well log app
    private int curSetID = 0; // Indicates the currently loaded set in the form
    private int activeSetID = 0; // Indicates the latest uncommitted set
    private List<FieldData> previousReading1 = null, previousReading2 = null;
    private Options pickerOptions;
    private boolean isPopulatingFirstTime;
    private FieldDataSource fieldDataSource;
    private AttachmentDataSource attachDataSource;
    private int currentAppID = 0;
    private String currentAppType = null;
    private String currentAppName = null;
    private int currentFormNum = 0;
    RecyclerView rvForms;
    FormsAdapter formsAdapter;

    String jumpToFieldId;
    boolean isJumpToField;

    public String getJumpToFieldId() {
        return jumpToFieldId;
    }

    public void setJumpToFieldId(String jumpToFieldId) {
        this.jumpToFieldId = jumpToFieldId;
    }

    public boolean isJumpToField() {
        return isJumpToField;
    }

    public void setJumpToField(boolean jumpToField) {
        isJumpToField = jumpToField;
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldDataSource = new FieldDataSource(this);
        attachDataSource = new AttachmentDataSource(this);
    }

    public boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23)
            return checkSelfPermission(mPermissionCamera) == PackageManager.PERMISSION_GRANTED;
        else
            return true;
    }

    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkCameraPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[]{mPermissionCamera,
                        },
                        REQUEST_CODE_CAMERA_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();
            } else {
                AlertManager.showPermissionSettingsAlert("Qnopy require camera permission to capture Photos or scan Barcode/QRCode. " +
                        "Please accept permission manually from settings.", this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getExtrasFromIntent();
    }

    public void getExtrasFromIntent() {
        Log.i(TAG, "getExtrasFromIntent() IN time:" + System.currentTimeMillis());

        extras = getIntent().getExtras();

        setEventID(extras.getInt("EVENT_ID"));
        eventIDChanged = getEventID();
        setLocID(extras.getString("LOCATION_ID"));
        locationIDChanged = getLocID();
        setParentAppID(extras.getInt("APP_ID"));
        setSiteID(extras.getInt("SITE_ID"));
        setSiteName(extras.getString("SITE_NAME"));
        this.setUsername(Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.USERNAME));
        this.setPassword(Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.PASSWORD));
        // appName = extras.getString("APP_NAME");
        locName = extras.getString("LOCATION_NAME");
        locDesc = extras.getString("LOCATION_DESC");
        depthText = extras.getString("DEPTH", "0");
        if (extras.getString("COC_ID") != null) {
            currCocID = extras.getString("COC_ID");
        }

        formDefault = extras.getInt(GlobalStrings.FORM_DEFAULT, 0);

        if (extras.containsKey("JUMP_FROM_MAP")) {
            fromMap = true;
        }

        this.setDepth(Double.parseDouble(depthText)); // for well log app

        String coclocid = Util.getSharedPreferencesProperty(CurrentContext, getLocationID());

        if (coclocid != null) {
            IS_COC_LOCATION = (Util.getSharedPreferencesProperty(CurrentContext, getLocationID()).equals("1"));
        }

        Log.i(TAG, "getExtrasFromIntent() OUT time:" + System.currentTimeMillis());
        isPopulatingFirstTime = true;
        loadForm_Task = new PopulateLocationDetailsTask();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_FORM_NUM", getCurrentFormNum());
        Log.i(TAG, "onSaveInstanceState() store CurrentFormNum:" + getCurrentFormNum());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("CURRENT_FORM_NUM")) {
            setCurrentFormNum(savedInstanceState.getInt("CURRENT_FORM_NUM"));
            Log.i(TAG, "onRestoreInstanceState() re-store CurrentFormNum:" + getCurrentFormNum());
        }
    }

    public void setAppName() {

        density = ((ScreenReso) getApplication()).getDensity();
        locName = extras.getString("LOCATION_NAME");

        TextView textView = findViewById(R.id.locName);
        mobileAppSource = new MobileAppDataSource(CurrentContext);

        //20-Feb-17
        if (childAppList == null) {
            childAppList = mobileAppSource.getChildApps(getParentAppID(),
                    getSiteID(), getLocationID());
        }

        maxApps = childAppList.size();

        if (maxApps > 0) {
            Log.i(TAG, "setAppName() old currentAppID=" + getCurrentAppID() + ",currentAppName=" + getCurrentAppName());
            setCurrentAppID(childAppList.get(getCurrentFormNum()).getAppID());
            setCurrentAppName(childAppList.get(getCurrentFormNum())
                    .getAppName());
            Log.i(TAG, "setAppName() new currentAppID=" + getCurrentAppID() + ",currentAppName=" + getCurrentAppName());
        }

        if (locName != null) {
            Log.i(TAG, "setAppName() current location=" + locName);

            SiteMobileAppDataSource sm = new SiteMobileAppDataSource(CurrentContext);
            String dispname = sm.getMobileAppDisplayName(getCurrentAppID(), getSiteID());
            Log.i(TAG, "setAppName() DisplayName for site and mobile=" + dispname);

            setCurrentAppName(dispname);

            if (locDesc != null) {
                locName += " --- " + locDesc;
                //  locName += " --- " + dispname;
            }

            if (dispname == null) {
                dispname = sm.getMobileAppDispNameForSite(getCurrentAppID());
                Log.i(TAG, "setAppName() DisplayName for mobile =" + dispname);
            }

            appName = dispname;//getSiteName() + "\n" + locName +

            Log.i(TAG, "setAppName() Form header title=" + appName);

            textView.setText(appName);
            if (density >= 240) {
                //  textView.setTextSize(14);
                textView.setTextSize(16);
            } else if (density < 160) {
                textView.setTextSize(16);
            }
        }
        setFormHeaders();
    }

    public String getLocationID() {
        return getLocID();
    }

    public int getSiteID() {
        return siteID;
    }

    /**
     * @param siteID the siteID to set
     */
    void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    int getParentAppID() {
        return parentAppID;
    }

    /**
     * @param parentAppID the parentAppID to set
     */
    void setParentAppID(int parentAppID) {
        this.parentAppID = parentAppID;
    }

    int getMaxForms() {
        return maxApps;
    }

    public int getCurrentAppID() {
        return currentAppID;
    }

    /**
     * @param currentAppID the currentAppID to set
     */
    void setCurrentAppID(int currentAppID) {
        this.currentAppID = currentAppID;
    }

    int getAllowMultipleSets() {
        int allowMultiRes = 0;
        if (childAppList != null && childAppList.size() > 0) {
            allowMultiRes = childAppList.get(getCurrentFormNum()).getAllowMultipleSets();
        }
        Log.i(TAG, "getAllowMultipleSets() res =" + allowMultiRes);

        return allowMultiRes;
    }

    public String getExtField1() {
        return childAppList.get(getCurrentFormNum()).getExtField1();
    }

    String getExtField2() {
        try {
            return childAppList.get(getCurrentFormNum()).getExtField2();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getExtField2() Error:" + e.getMessage());
        }
        return "";
    }

    String getCurrentAppName() {
        //return childAppList.get(getCurrentFormNum()).getAppName();
        return this.currentAppName = childAppList.get(getCurrentFormNum()).getAppName();
    }

    /**
     * @param currentAppName the currentAppName to set
     */
    void setCurrentAppName(String currentAppName) {
        this.currentAppName = currentAppName;
    }

    public int getCurrentFormNum() {
        Log.i(TAG, "getCurrentFormNum() =" + currentFormNum);
        return currentFormNum;
    }

    void setCurrentFormNum(int num) {
        currentFormNum = num;
        Log.i(TAG, "set CurrentFormNum() =" + currentFormNum);
    }

    void incrCurrentFormNum() {
        setCurrentFormNum(getCurrentFormNum() + 1);
        Log.i(TAG, "incrCurrentFormNum() currentFormNum=" + getCurrentFormNum());

        //20-Feb-17
        setCurrentAppID(childAppList.get(getCurrentFormNum()).getAppID());

        Log.i(TAG, "incrCurrentFormNum() currentAppID=" + getCurrentAppID());
    }

    void decrCurrentFormNum() {
        setCurrentFormNum(getCurrentFormNum() - 1);
        Log.i(TAG, "decrCurrentFormNum() currentFormNum=" + getCurrentFormNum());
        //20-Feb-17
        setCurrentAppID(childAppList.get(getCurrentFormNum()).getAppID());
        Log.i(TAG, "decrCurrentFormNum() currentAppID=" + getCurrentAppID());
    }

    public void jumpToAnySet(int setId) {
        setCurSetID(setId);
        if (formMaster != null)
            formMaster.setCurrentSetID(getCurSetID());
        new PopulateLocationDetailsTask().execute(LOAD_SET_DATA);
    }

    public void jumpToAnyField() {

        isJumpToField = false;

        if (formsAdapter != null) {
            for (int pos = 0; pos < formsAdapter.getMetaDataList().size(); pos++) {
                MetaData metaData = formsAdapter.getMetaDataList().get(pos);
                if (metaData.getMetaParamID() == Integer.parseInt(jumpToFieldId)) {
                    rvForms.smoothScrollToPosition(pos);
                }
            }
        }

        if (formMaster != null) {
            if (locationdetail_master_container != null && formMaster.filteredMetaObjects.size() > 0) {

                MetaData metaDataFound = null;
                for (MetaData metaData : formMaster.filteredMetaObjects) {
                    if (metaData.getMetaParamID() == Integer.parseInt(jumpToFieldId)) {
                        metaDataFound = metaData;
                    }
                }

                try {

                    for (int i = 0; i < locationdetail_master_container.getChildCount(); i++) {
                        View rowView = locationdetail_master_container.getChildAt(i);

                        FormMaster.ViewHolder viewHolder
                                = (FormMaster.ViewHolder) metaDataFound.getForm_field_row().getTag();

                        if (rowView.getTag() != null) {//added null check to avoid null pointer as sometimes
                            // the row view doesn't get the row tag even though every row has tag set, don't know why.. but need to check
                            if (rowView.getTag().equals(viewHolder)) {
                                nested_scroll_view.smoothScrollTo(0, rowView.getTop());
                            }
                        }
                    }
                } catch (Resources.NotFoundException | IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void checkRequiredField(boolean isRightForm, View view) {
        //method callback is used in locationDetailActivity as it is overridden there
    }

    public void checkRequiredFieldAndNavigateForm(MobileApp app, int position) {
        //method callback is used in locationDetailActivity as it is overridden there
    }

    public void populateLocationDetail() {

        deviceID =
                Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.SESSION_DEVICEID);

        // DeviceInfo.getDeviceID(CurrentContext);
        if (deviceID == null || deviceID.isEmpty()) {
            deviceID = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.DEVICEID);
        }
        Log.i(TAG, "populateLocationDetail() Current DeviceID:" + deviceID);
        Log.i(TAG, "populateLocationDetail() Current setID :" + getCurSetID());
        isMandatoryfilled = fieldDataSource.isMandatoryFieldFilled(getCurrentAppID() + "", getEventID() + "", getLocationID());
        alertBar = findViewById(R.id.alert_bar);
        metaValues = getFormData();

        setLastSet();
        Log.i(TAG, "populateLocationDetail() size of metaValues = " + metaValues.size());

        if (metaValues.size() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                                    getString(R.string.location_data_not_available), Toast.LENGTH_SHORT)
                            .show();
                }
            });

            SharedPref.putInt("LOCATION_ID", 0);
            finish();
            return;
        }

        if (Util.isShowNewForms(this)) {
            formsAdapter = new FormsAdapter((LocationDetailActivity) CurrentContext,
                    FormActivity.this, new ArrayList<>(metaValues), getSiteID() + "", getLocationID(),
                    getEventID() + "", getParentAppID() + "");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rvForms.setAdapter(formsAdapter);
                    checkAndEnableNewReading();
                    showSetData();
                }
            });
        } else {
            refreshMasterForm();
        }
    }

    public void refreshMasterForm() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvForms.setVisibility(View.GONE);
                nested_scroll_view.setVisibility(View.VISIBLE);
                //stuff that updates ui
                try {

                    formMaster = new FormMaster(CurrentContext, FormActivity.this, metaValues,
                            getSiteID() + "", getLocationID(), getEventID() + "", getParentAppID() + "");

                    formMaster.setCurrentFormNumber(getCurrentFormNum());
                    formMaster.setCurrentSetID(getCurSetID());

                    metaDataList = formMaster.getFormMasterData();

                    attachViewToScroller(formMaster.form_meta_list);

                    currentReading = getCurrentReading1List();
                    previousReading1 = getPreviousReading1List();
                    previousReading2 = getPreviousReading2List();

//                    locaDetailAdapter = new LocationdetailAdapter(
//                            FormActivity.this, R.layout.adapter_locdetail, R.id.Field1, metaDataList
//                            , CurrentContext, formMaster);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "populateLocationDetail() runOnUiThread caught msg=" + e.getLocalizedMessage());
                }

//                getListView().setAdapter(locaDetailAdapter);

                checkAndEnableNewReading();
                showSetData();
                Log.e(TAG, "Populate forms completed " + (startTime - System.currentTimeMillis()) + "ms");
            }
        });
    }

    public void attachViewToScroller(MetaData metaData) {
        try {

            for (int i = 0; i < locationdetail_master_container.getChildCount(); i++) {
                View rowView = locationdetail_master_container.getChildAt(i);

                FormMaster.ViewHolder viewHolder
                        = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();

                if (rowView.getTag() != null) {//added null check to avoid null pointer as sometimes
                    // the row view doesn't get the row tag even though every row has tag set, don't know why.. but need to check
                    if (rowView.getTag().equals(viewHolder)) {

                        MetaDataAttributesDataSource source = new MetaDataAttributesDataSource(this);
                        MetaDataAttributes attributes = source.getMetaDataAttributes(siteID,
                                getCurrentAppID(), metaData.getMetaParamID());
                        boolean isFieldHidden = false;
                        try {
                            if (attributes != null && attributes.getHide() != null)
                                isFieldHidden = attributes.getHide() == 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (i != locationdetail_master_container.getChildCount() - 1) {
                            View divider = locationdetail_master_container.getChildAt(i + 1);

                            if ((metaData.isRowVisible || metaData.isVisible) && !isFieldHidden) {
                                divider.setVisibility(View.VISIBLE);
                            } else {
                                divider.setVisibility(View.GONE);
                            }
                        }

                        if ((metaData.isRowVisible || metaData.isVisible) && !isFieldHidden) {
                            rowView.setVisibility(View.VISIBLE);
                        } else {
                            rowView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Resources.NotFoundException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void attachViewToScroller(List<MetaData> metaDataList) {
        try {
            if (locationdetail_master_container != null && locationdetail_master_container.getChildCount() > 0) {
                locationdetail_master_container.removeAllViews();
            }

            locationdetail_master_container.requestFocus();
            nested_scroll_view.scrollTo(0, 0);
            nested_scroll_view.getParent().requestChildFocus(nested_scroll_view, nested_scroll_view);

            MetaDataAttributesDataSource source = new MetaDataAttributesDataSource(this);

            for (int i = 0; i < metaDataList.size(); i++) {
                MetaData metaData = metaDataList.get(i);

                MetaDataAttributes attributes = source.getMetaDataAttributes(siteID,
                        getCurrentAppID(), metaData.getMetaParamID());
                boolean isFieldHidden = false;
                try {
                    if (attributes != null && attributes.getHide() != null)
                        isFieldHidden = attributes.getHide() == 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (i == (metaDataList.size() - 1)) {
                    locationdetail_master_container.addView(metaData.getForm_field_row());

                    if ((!metaData.isParentField & !metaData.isChildField & metaData.isRowVisible
                            & !isFieldHidden)
                            | metaData.isParentField
                            | (metaData.isChildField & metaData.isVisible) & !isFieldHidden) {
                        metaData.getForm_field_row().setVisibility(View.VISIBLE);
                    } else {
                        metaData.getForm_field_row().setVisibility(View.GONE);
                    }
                } else {
                    locationdetail_master_container.addView(metaData.getForm_field_row());
                    View divider = new View(CurrentContext);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1);
                    lp.setMargins(16, 0, 16, 0);

                    divider.setLayoutParams(lp);
                    divider.setBackgroundColor(getResources().getColor(R.color.login_text_grey));
                    divider.setId(i);
                    locationdetail_master_container.addView(divider);

                    if ((!metaData.isParentField & !metaData.isChildField & metaData.isRowVisible
                            & !isFieldHidden)
                            | metaData.isParentField
                            | (metaData.isChildField & metaData.isVisible) & !isFieldHidden) {
                        metaData.getForm_field_row().setVisibility(View.VISIBLE);
                        divider.setVisibility(View.VISIBLE);
                    } else {
                        metaData.getForm_field_row().setVisibility(View.GONE);
                        divider.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Resources.NotFoundException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void showSetData() {

        Log.i(TAG, "showSetData() Allow_muliple_set status=true");
        setCount = findViewById(R.id.set_title1);

        setCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActiveSetID() > 1)
                    showPopupToJumpToSet();
            }
        });

        setNavLeft = findViewById(R.id.action_nav_left2);
        setNavRight = findViewById(R.id.action_nav_right2);

        setNavRight.setOnClickListener(this::setRightNavigation);
        setNavLeft.setOnClickListener(this::setLeftNavigation);

        setInfo_container = findViewById(R.id.setData);
        setInfo_container.setVisibility(View.VISIBLE);

        setNavLeft.setVisibility(View.VISIBLE);
        setNavRight.setVisibility(View.VISIBLE);
        String str = "Set " + getCurSetID() + " of "
                + getActiveSetID();
        setCount.setText(str);
        // TODO: 22-Feb-17

        if (activeSetID == 1) {
            setNavLeft.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_left_disabled));
            setNavRight.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_right_disabled));
        } else {
            if (curSetID == 1) {
                setNavLeft.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_left_disabled));
            }

            if (curSetID < activeSetID && curSetID > 1) {
                setNavLeft.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_left_enabled));
                setNavRight.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_right_enabled));
            }

            if (curSetID == activeSetID) {
                setNavRight.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_right_disabled));
            }
            if (curSetID > 1) {
                setNavLeft.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_left_enabled));
            }
            if (curSetID < activeSetID) {
                setNavRight.setBackground(ContextCompat.getDrawable(this, R.drawable.arrow_right_enabled));
            }
        }
    }

    private void showPopupToJumpToSet() {
        View view =
                LayoutInflater.from(this).inflate(R.layout.alert_show_set,
                        null, false);

        CustomEditText edtEnterSet = view.findViewById(R.id.edtEnterSet);
        CustomItalicTextView btnShowSet = view.findViewById(R.id.btnShowSet);
        CustomItalicTextView btnCancel = view.findViewById(R.id.btnCancel);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        edtEnterSet.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        btnShowSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(edtEnterSet.getWindowToken(), 0);
                if (edtEnterSet != null) {
                    if (!edtEnterSet.getText().toString().isEmpty()) {
                        int setId = Integer.parseInt(edtEnterSet.getText().toString());
                        if ((setId <= getActiveSetID()) && setId > 0 && setId != getCurSetID()) {
                            jumpToAnySet(setId);
                            alertDialog.cancel();
                        } else {
                            edtEnterSet.setFocusable(true);
                            edtEnterSet.setError(getString(R.string.enter_valid_set_no));
                        }
                    } else {
                        edtEnterSet.setFocusable(true);
                        edtEnterSet.setError(getString(R.string.enter_set_no));
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(edtEnterSet.getWindowToken(), 0);
                alertDialog.cancel();
            }
        });
    }

    public void setRefreshButtonVisibility(int isVisible) {
        tvCalculate.setVisibility(isVisible);
    }

    public void handleFormRight() { // Left to Right Navigation
        reqFieldCount = 0;
        showOneTimeAlert = true;

        if (getCurrentFormNum() > 0) {
            decrCurrentFormNum();
            setInvalidating();

            new PopulateLocationDetailsTask().execute(LOAD_FORM);
//          populateLocationDetail();

            resetInvalidating();

            setAppName();
            try {
                formMaster.setCurrentFormNumber(getCurrentFormNum());
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    public void handleForaddForm() {

        setInvalidating();
        new PopulateLocationDetailsTask().execute(LOAD_FORM);

        resetInvalidating();
        setAppName();
    }

    public void handleFormLeft() { // Right to left navigation
        reqFieldCount = 0;
        showOneTimeAlert = true;

        if (getCurrentFormNum() + 1 < getMaxForms()) {
            incrCurrentFormNum();
            setInvalidating();

            new PopulateLocationDetailsTask().execute(LOAD_FORM);

            resetInvalidating();

            setAppName();
            if (formMaster != null)
                formMaster.setCurrentFormNumber(getCurrentFormNum());
        }
    }

    public void onClickRightForm(View v) {

        if ((getCurrentFormNum() + 1) == getMaxForms()) {
            SharedPref.putInt("LOCATION_ID", 0);
            SharedPref.putInt("CURRENT_FORM_NUM", 0);
            this.finish();
        } else {
            handleFormLeft();
        }

        if (LocationDetailActivity.mobileReportFragment != null && Util.isTablet(this)
                && isSplitScreenEnabled) {
            LocationDetailActivity.mobileReportFragment.setReportDetails();
        }
        currCocID = null;//setting it as null as form is changed and it is handled only for the form where coc is picked
    }

    public void onClickLeftForm(View v) {

        if (getCurrentFormNum() == 0) {
            SharedPref.putInt("LOCATION_ID", 0);
            this.finish();
        } else {
            handleFormRight();
        }
        // showFormData();

        if (LocationDetailActivity.mobileReportFragment != null && Util.isTablet(this)
                && isSplitScreenEnabled) {
            LocationDetailActivity.mobileReportFragment.setReportDetails();
        }

        currCocID = null;//setting it as null as form is changed and it is handled only for the form where coc is picked
    }

    public void setFormHeaders() {

        LinearLayout siteName = findViewById(R.id.appName);
        ImageButton nav_left = findViewById(R.id.form_nav_left);
        ImageButton nav_right = findViewById(R.id.form_nav_right);

        nav_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRequiredField(true, view);
            }
        });

        nav_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRequiredField(false, view);
            }
        });

//      Button submit = (Button) findViewById(R.id.Done);
        if (siteName != null) {
            currentFormColor = 0;
            siteName.setBackgroundResource(image[currentFormColor]);

            nav_left.setBackgroundResource(R.drawable.arrow_prev_form);
            nav_right.setBackgroundResource(R.drawable.arrow_next_form);
            //submit.setBackgroundResource(submit_image[0]);
            // submit.setText("Submit");
        }

        Log.i(TAG, "setFormHeaders() OUT time=" + System.currentTimeMillis());
    }

    public List<MetaData> getFormData() {

        MetaDataSource metaData = new MetaDataSource(CurrentContext);
        Log.i(TAG, "getcontrolData() for CurrentAppID=" + getCurrentAppID() + ",SiteID=" + getSiteID() + ",locationID=" + getLocID());

/*        List<MetaData> metaLocID = metaData.getMetaData(getCurrentAppID(),
                getSiteID(), getLocID()); */

        List<MetaData> metaLocID = metaData.getMetaDataWithVisibleQueryOperands(getCurrentAppID(),
                getLocID());
        startTime = System.currentTimeMillis();
        Log.e(TAG, "Populate forms" + startTime);
        return new ArrayList<>(metaLocID);
    }

    public void loadFragment(Fragment fragment, int contentFrameId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(contentFrameId, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    public void checkAndEnableNewReading() {

        fab_new_reading = findViewById(R.id.action_new_set);
        fab_delete_current_reading = findViewById(R.id.action_delete_current_set);
        setInfo_container = findViewById(R.id.setData);

        if (getAllowMultipleSets() != 0) {
            showNewReading();
        } else {
            HideNewReading();
        }

        if (getCurSetID() > 1) {
            fab_delete_current_reading.setVisibility(View.VISIBLE);
        } else if (getCurSetID() == 1) {
            fab_delete_current_reading.setVisibility(View.GONE);
        }

        fab_delete_current_reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(CurrentContext);

                alertDialogBuilder1.setTitle(getString(R.string.delete_reading));
                alertDialogBuilder1.setMessage(getString(R.string.do_you_want_to_delete_reading));
                // set positive button: Yes message
                alertDialogBuilder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to a new activity of the app
                        int ret = fieldDataSource.deleteset(getLocationID(), getEventID(), getCurrentAppID(), getCurSetID(), getSiteID());
                        Log.i(TAG, "deleteset :" + ret);

                        ////1/25/2018  update extField 1 and set
                        fieldDataSource.updateset(getLocationID(), getEventID(), getCurrentAppID(), getSiteID(), getCurSetID());

                        ////1/25/2018  delete set from attachment
                        int cnt = attachDataSource.deleteAttachmentset(getLocationID(), getEventID(), getCurrentAppID(), getCurSetID(), getSiteID());

                        ////1/25/2018 update set in attachment
                        attachDataSource.updateAttachmentset(getLocationID(), getEventID(), getCurrentAppID(), getSiteID(), getCurSetID());

                        ////1/25/2018 delete set from sample map tag
                        SampleMapTagDataSource sampleMapTagDataSource = new SampleMapTagDataSource(CurrentContext);
                        int updatecnt = sampleMapTagDataSource.deletesetfromsamplemap(getLocationID(), getSiteID(),
                                getEventID(), getCurSetID(), getCurrentAppID());

                        sampleMapTagDataSource.updatesampletag(getLocationID(), getSiteID(), getEventID(), getCurSetID(), getCurrentAppID());

                        if (getCurSetID() > 1) {
                            setCurSetID(getCurSetID() - 1);
                        }
                        setActiveSetID(getActiveSetID() - 1);
                        if (formMaster != null)
                            formMaster.setCurrentSetID(getCurSetID());
                        showSetData();

                        menuMultipleActions.collapse();

                        new PopulateLocationDetailsTask().execute(LOAD_SET_DATA);
                        //06-04-2018 SCROLL TO 0th POSITION

                        locationdetail_master_container.requestFocus();
                        nested_scroll_view.scrollTo(0, nested_scroll_view.getTop());
                    }
                });
                // set negative button: No message
                alertDialogBuilder1.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog1 = alertDialogBuilder1.create();
                // show alert
                alertDialog1.show();
            }
        });

        fab_new_reading.setOnClickListener(view -> {
            menuMultipleActions.collapse();

            if (fieldDataSource.isValidToCreateNewSet(getCurrentAppID() + "",
                    getLocationID(), getSiteID() + "", getCurSetID() + "")) {
                if (checkForRequiredFields()) {
                    showRequiredFieldsAlertForSet();
                } else
                    onClickAddContinue();
            } else {
                Toast.makeText(appContext, getString(R.string.please_enter_data), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRequiredFieldsAlertForSet() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.incomplete_form));
        builder.setMessage(getString(R.string.missing_required_entries_do_you_want_leave));
        builder.setNegativeButton(R.string.proceed, (dialog, which) -> {
            onClickAddContinue();
        });
        builder.setPositiveButton(getString(R.string.go_back_lower_case), (dialog, which) -> {
            dialog.dismiss();
        });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showNewReading() {
        setInfo_container.setVisibility(View.VISIBLE);
        menuMultipleActions.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nested_scroll_view.getLayoutParams();
        if (Util.isTablet(this)) {
            layoutParams.setMargins(0, 0, 0, 100);
        } else {
            layoutParams.setMargins(0, 0, 0, 160);
        }
        nested_scroll_view.setLayoutParams(layoutParams);
        LocationDetailActivity.LocDetailActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //nested_scroll_view.setPadding(0, 0, 0, 160);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void HideNewReading() {
        menuMultipleActions.setVisibility(View.GONE);
        setInfo_container.setVisibility(View.GONE);

        //nested_scroll_view.setPadding(0, 0, 0, 0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nested_scroll_view.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        nested_scroll_view.setLayoutParams(layoutParams);
        LocationDetailActivity.LocDetailActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public boolean checkForRequiredFields() {
        FieldDataSource fieldDataSource = new FieldDataSource(this);
        ArrayList<String> fieldParamIds = fieldDataSource
                .getAllRequiredFieldParams(getCurrentAppID() + "", getLocationID(),
                        formMaster, formsAdapter);

        String fpIds = "()";
        if (fieldParamIds.size() > 0) {
            fpIds = Arrays.toString(fieldParamIds.toArray()).replace("[", "(");
            fpIds = fpIds.replace("]", ")");
        }

        int filledParamCount = fieldDataSource.getRequiredFieldDataFilledCount(getSiteID() + "",
                getCurrentAppID() + "", getLocationID() + "",
                getEventID() + "", getCurSetID() + "", fpIds);

        return fieldParamIds.size() != filledParamCount;
    }

    public void openGallery() {
        Intent i = new Intent(this,
                CardGalleryActivity.class);
        i.putExtra("SITE_ID", getSiteID());
        i.putExtra("EVENT_ID", getEventID());
        i.putExtra("LOC_ID", getLocID());
        i.putExtra("MOBILE_APP_ID", getCurrentAppID());
        i.putExtra("SET_ID", getCurSetID());
        i.putExtra("USER_ID", getUserID());
        i.putExtra("CURRENT_FORM_NUM", getCurrentFormNum());
        try {
            startActivity(i);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    /*
     * This function is implemented to handle a specific case. When the Next
     * Reading is clicked, onFocuschangeLisner of the text/numeric field below
     * is also being called and code under lost focus is called. This would set
     * the data of the particular text/numeric view back even after
     * clearMapObject/setMapObject is called. To handle this, a flag is set when
     * invalidation starts. This flag is checked in the lost focus part of the
     * code of onfocuschangelisner. Only side effect is that , it has to be
     * reset on having focus every time.
     */
    public void setInvalidating() {
        invalidating = true;
    }

    public void resetInvalidating() {
        setInvalidating(false);
    }

    public boolean getInvalidating() {
        return isInvalidating();
    }

    public void invalidateListView() {
        setInvalidating();
        if (!Util.isShowNewForms(this))
            clearMapObjects();

//        if (formMaster.getmAdapter() != null)
//            formMaster.getmAdapter().notifyDataSetChanged();
    }

    public void refreshScreen() {
//        if (locaDetailAdapter != null)
//            locaDetailAdapter.notifyDataSetChanged();

        if (reqFieldCount > 0 && showOneTimeAlert) {
            showRequiredFieldAlert();
        }
    }

    public void showRequiredFieldAlert() {

        //10-Apr-17 Multi Times Calling

        if (builder == null) {
            builder = new AlertDialog.Builder(appContext);
        }

        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.there_are_mandatory_fields_in_red_color));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                showOneTimeAlert = false;
                dialog.dismiss();
            }
        });

        if (closedialog != null && closedialog.isShowing()) {
            //Do Nothing
        } else {
            closedialog = builder.create();
            closedialog.show();
        }
    }

/*
    @Override
    protected Dialog onCreateDialog(final int id) {
        Dialog dialog = null;
        String MesgInvalid = getString(R.string.some_required_data_is_not_filled_or_valid);
        String MesgValid = getString(R.string.data_will_be_stored_for_location);
        String MesgAllNull = getString(R.string.no_changes_to_submit);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        switch (id) {
            case 10:
                builder.setMessage(MesgValid);
                break;
            case 11:
                builder.setMessage(MesgInvalid);
                break;
            case 12:
                builder.setMessage(MesgAllNull);
            default:
                break;
        }

        builder.setCancelable(false);

        if (id != 12) {
            builder.setPositiveButton(getString(R.string.proceed_to_submit_upper_case),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // to perform on ok
                            storeFieldData();
                            refreshScreen();
                        }
                    });
        }
        builder.setNegativeButton(getString(R.string.go_back_upper_case),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (doneClicked)
                            doneClicked = false;
                    }
                });
        AlertDialog alert = builder.create();
        dialog = alert;

        return dialog;
    }
*/

/*
    public void storeFieldData() {
        Log.i(TAG, "storeFieldData() IN time=" + System.currentTimeMillis());

        long time = System.currentTimeMillis();
        long creationDate = time;

        if (this.date == null) {
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy");
            this.date = ft.format(dNow);
        }
        if (this.time == null) {
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("hh:mm");
            this.time = ft.format(dNow);
        }
        Log.i(TAG, "storeFieldData() updateCreationDate for Date=" + this.date + " time=" + this.time + "," +
                "Current Set=" + getCurSetID() + ",EventID=" + getEventID() + ",Location=" + getLocationID() + ",Site=" + getSiteID() + ",CurrentAppID=" + getCurrentAppID() + "," +
                "CreationDate=" + creationDate);

        fieldDataSource.updateCreationDate(getEventID(), getCurSetID(),
                getLocationID(), getSiteID(), getCurrentAppID(), creationDate);

        if (measurementTime == 0 || measurementTime < 86400000) {
            Log.i(TAG, "storeFieldData() updateMeasurementTime for Date=" + this.date + " time=" + this.time + "," +
                    "Current Set=" + getCurSetID() + ",Location=" + getLocationID() + ",EventID=" + getEventID() + ",Site=" + getSiteID() + ",CurrentAppID=" + getCurrentAppID() + "," +
                    "CreationDate=" + creationDate);

            fieldDataSource.updateMeasurementTime(getEventID(), getCurSetID(),
                    getLocationID(), getSiteID(), getCurrentAppID(),
                    creationDate);
        }

        LocationDataSource locData = new LocationDataSource(CurrentContext);
        locData.setDataStoredFlag(getSiteID(), getLocID());
        String msg = getString(R.string.data_submitted);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text1 = layout.findViewById(R.id.text);
        text1.setText(msg);
        text1.setTextSize(20);
        Toast toast = new Toast(appContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);

        toast.setView(layout);
        toast.show();

        checkAndEnableNewReading();
    }
*/

    public List<FieldData> getBlankFieldData(List<MetaData> metaVals) {

        FormMaster.DataHolder dh;
        FieldData fieldData = null;
        MetaData metaData;
        List<FieldData> dataList = new ArrayList<>();
        EventDataSource eventDataSource = new EventDataSource(CurrentContext);
        event = eventDataSource.getEvent(getCurrentAppID(), getSiteID(),
                getUserID(), deviceID);

        for (int pos = 0; pos < metaVals.size(); pos++) {

            try {
                fieldData = new FieldData();


                metaData = metaVals.get(pos);
                Log.i(TAG, "getBlankFieldData() FieldParamID="
                        + metaData.getMetaParamID() + ",FieldParameterLabel="
                        + metaData.getMetaParamLabel());

                fieldData.setStringValue(null);

                fieldData.setFieldParameterID(metaData.getMetaParamID());
                fieldData.setFieldParameterLabel(metaData.getMetaParamLabel());

                ////7/1/17
                fieldData.setCreationDate(0);
                //  fieldData.setCreationDate(System.currentTimeMillis());
                fieldData.setLocationID(getLocID());
                fieldData.setEventID(getEventID());

                if (event != null) {
                    long date = event.getEventStartDateTime();
                    String dateString = Long.toString(date);
                    fieldData.setExtField2(dateString);
                    Log.i(TAG, "getBlankFieldData() extr :" + dateString);
                }

                fieldData.setUnits(metaData.DesiredUnits);
                fieldData.setCurSetID(this.getCurSetID());
                fieldData.setExtField4(Double.toString(this.getDepth()));
                fieldData.setSiteID(getSiteID());
                fieldData.setUserID(getUserID());
                fieldData.setMobileAppID(getCurrentAppID());
                fieldData.setDeviceId(deviceID);
                Log.i(TAG, "getBlankFieldData() LocationID="
                        + getLocID() + ",EventID=" + getEventID() + ",CursetID=" + this.getCurSetID() + ",Ext4=" + this.getDepth() + ",SiteID=" + getSiteID() +
                        ",UserID=" + getUserID() + ",MobileAppID=" + getCurrentAppID() + ",DeviceID=" + deviceID + ",Ext2=" + getExtField2());

                dataList.add(fieldData);

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "An Error Occurred-" + e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        Log.i(TAG, "getBlankFieldData() OUT time=" + System.currentTimeMillis());

        return dataList;
    }

    public double getDepth() {
        return this.depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public int getCurSetID() {
        return curSetID;
    }

    /**
     * @param curSetID the curSetID to set
     */
    protected void setCurSetID(int curSetID) {
        this.curSetID = curSetID;
    }

    public void resetCurSetID() {
        setCurSetID(0);
    }

    public void setLastSet() {
        Log.i(TAG, "setLastSet() In time:" + System.currentTimeMillis());
        int unsavedSetId = fieldDataSource.getWorkingFieldSetID(getLocID(),
                getSiteID(), getCurrentAppID());
        Log.i(TAG, "setLastSet() unsavedSetId:" + unsavedSetId);

        if (unsavedSetId == 0) {
            int lastSavedSetId = fieldDataSource.getLastSavedSetIDForOpenEvent(getEventID(),
                    getLocID(), getSiteID(), getCurrentAppID());
            Log.i(TAG, "setLastSet() lastSavedSetId:" + lastSavedSetId);

            if (lastSavedSetId == 0) {
                getNextSet();
                return;
            } else {
                setCurSetID(lastSavedSetId);
            }
        } else {
            setCurSetID(unsavedSetId);
        }

        setActiveSetID(getCurSetID());
        this.currentReading = fieldDataSource.getFieldDataListForSet(getEventID(), getCurSetID(),
                getLocID());

        Log.i(TAG, "setLastSet() OUT time:" + System.currentTimeMillis());
    }

    public void getNextSet() {
        setCurSetID(fieldDataSource.getLastSavedSetIDForOpenEvent(getEventID(), getLocID(),
                getSiteID(), getCurrentAppID()) + 1);
        List<FieldData> fieldData = getBlankFieldData(metaValues);
        fieldDataSource.insertFieldDataList(fieldData, getUserID(), deviceID);
        insertNotesField();
        setActiveSetID(getCurSetID());
        this.currentReading = fieldDataSource.getFieldDataListForSet(getEventID(), getCurSetID(),
                getLocID());
    }

    void insertNotesField() {
        Log.i(TAG, "insertNotesField() IN time:" + System.currentTimeMillis());

        MetaDataSource notesSource = new MetaDataSource(CurrentContext);

        // For the Notes if existing
        Log.i(TAG, "insertNotesField() call getMetaDataforNotes");

        List<MetaData> notesValues = notesSource.getMetaDataForNotes(
                getCurrentAppID(), getSiteID(), getLocID());
        if ((notesValues != null) && (notesValues.size() != 0)) {

            Log.i(TAG, "insertNotesField() NotesValues not null call getBlankFieldData");

            List<FieldData> notesData = getBlankFieldData(notesValues);

            Log.i(TAG, "insertNotesField() NotesValues not null call insertFieldDataList");

            fieldDataSource.insertFieldDataList(notesData, getUserID(), deviceID);
        }
        Log.i(TAG, "insertNotesField() OUT time:" + System.currentTimeMillis());
    }

    public List<FieldData> getCurrentReading1List() {

        Log.i(TAG, "getCurrentReading1List() ");

        if ((this.currentReading != null) && (this.currentReading.size() > 0)) {
            return this.currentReading;
        } else {
            return null;
        }
    }

    public List<FieldData> getPreviousReading1List() {
        if ((this.previousReading1 != null)
                && (this.previousReading1.size() > 0)) {
            return this.previousReading1;
        } else {
            return null;
        }
    }

    public List<FieldData> getPreviousReading2List() {

        if ((this.previousReading2 != null)
                && (this.previousReading2.size() > 0)) {
            return this.previousReading2;
        } else {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  if (requestCode != CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE || requestCode != AUTOCOMPLETE_REQUEST_CODE) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (requestCode == SYNC_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            boolean dataSynced = data.getBooleanExtra("SYNC_SUCCESS", false);
            Log.i(TAG, "Data Synced Success:" + dataSynced);
            String locationID = new LocationDataSource(appContext).getServerLocationID(getLocID());
            Log.i(TAG, "Client Location:" + getLocID() + " Server LocationID:" + locationID);
            if (locationID != null) {
                setLocID(locationID);
                if (formMaster != null)
                    formMaster.locationID = locationID;

                Util.setSharedPreferencesProperty(appContext, getEventID() + "", locationID);
                Util.setSharedPreferencesProperty(appContext, GlobalStrings.CURRENT_LOCATIONID, locationID);
            }
            refreshScreen();
        }

        if ((requestCode == GlobalStrings.REQUEST_CODE_WEATHER)
                && resultCode == RESULT_OK) {
            new PopulateLocationDetailsTask().execute(LOAD_SET_DATA);
        }

        if ((requestCode == REQUEST_CODE_TASK)
                && resultCode == RESULT_OK && data != null) {
            if (formsAdapter != null) {
                int fpId = data.getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);
                formsAdapter.updateTask(fpId);
            }
        }

        if ((requestCode == REQUEST_CODE_OS11UP_MEDIAPICKER && data != null)
                && resultCode == RESULT_OK) {
            File f = new File(output_file.getPath());
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    copyFile(new File(data.getData().getPath()), f);
                    setAttachmentData(true, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if ((requestCode == LocationDetailActivity.REQUEST_CODE_FORM_REPORT)
                && resultCode == RESULT_OK && data != null) {
            int set = data.getIntExtra(GlobalStrings.FORM_DETAILS, 1);
            jumpToAnySet(set);
        }

        if (requestCode == CAPTURE_GPS_LOCATION_REQUEST_CODE && resultCode == RESULT_OK
                && data != null) {
            MetaData metaData = data.getParcelableExtra(GlobalStrings.KEY_META_DATA);
            String location = data.getStringExtra(GlobalStrings.FETCHED_LOCATION);

            if (formsAdapter != null) {
                formsAdapter.updateGpsLocation(metaData.getMetaParamID(), location);
            }

            if (formMaster != null && formMaster.mapMetaObjects.containsKey(metaData.getMetaParamID())) {
                MetaData mMeta = formMaster.mapMetaObjects.get(metaData.getMetaParamID());
                FormMaster.DataHolder tempData = formMaster.fmapObject.get(mMeta.getMetaParamID() + "");

                FormMaster.ViewHolder viewHolder
                        = (FormMaster.ViewHolder) mMeta.getForm_field_row().getTag();

                tempData.value = location;
                formMaster.saveData_and_updateCreationDate(this, mMeta, location, curSetID);

                if (tempData.value != null && !tempData.value.isEmpty()) {
                    viewHolder.tvGpsCoordinates.setText(location);
                }
            }
        }

        //  }

        try {

            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE: {
                        // Bundle extras1 = getIntent().getExtras();

                        int fieldParameterID = data.getIntExtra("POSITION", 0);
                        String signaturevalue = data.getStringExtra("SIGNATURENAMES");

                        if (formsAdapter != null) {
                            formsAdapter.refreshSignatures(fieldParameterID);
                        }

                        if (formMaster != null && formMaster.mapMetaObjects.containsKey(fieldParameterID)) {
                            MetaData metaData = formMaster.mapMetaObjects.get(fieldParameterID);
                            if (metaData.getMetaParamID() == fieldParameterID) {
                                FormMaster.DataHolder tempData = formMaster.fmapObject.get(fieldParameterID + "");

                                FormMaster.ViewHolder viewHolder = new FormMaster.ViewHolder();
                                viewHolder = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();

                                AttachmentDataSource attachmentDataSource = new AttachmentDataSource(this);
                                ArrayList<CustomerSign> customerSigns
                                        = attachmentDataSource.getAttachmentListForSignature(eventID,
                                        siteID, metaData.getMetaParamID(), getLocationID(), userID,
                                        getCurrentAppID(), getCurSetID());

                                viewHolder.signatureAdapter.addSignatures(customerSigns);

                                /*tempData.value = signaturevalue;

                                if (tempData.value != null && !tempData.value.isEmpty()) {
                                    viewHolder.tvSignatureNames.removeAllViews();
                                    formMaster.addChipsToView(viewHolder.tvSignatureNames, tempData.value);
                                }*/
                            }
                        }

                        break;
                    }

                    case CAPTURE_NOTE_REQUEST_CODE: {
                        // Bundle extras1 = getIntent().getExtras();

                        int fieldParameterID = data.getIntExtra("POSITION", 0);
                        boolean isNotetaken = data.getBooleanExtra("IS_NOTE_VALUE", false);

                        if (formsAdapter != null) {
                            formsAdapter.updateNotes(fieldParameterID, isNotetaken);
                        }

                        if (formMaster != null && formMaster.mapMetaObjects.containsKey(fieldParameterID)) {
                            MetaData metaData = formMaster.mapMetaObjects.get(fieldParameterID);
                            if (metaData.getMetaParamID() == fieldParameterID) {
                                FormMaster.DataHolder tempData = formMaster.fmapObject.get(fieldParameterID + "");

                                FormMaster.ViewHolder viewHolder = new FormMaster.ViewHolder();
                                viewHolder = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();

                                FormMaster.DataHolder.isnote_taken = isNotetaken;

                                if (FormMaster.DataHolder.isnote_taken) {
                                    viewHolder.enableNotes.setImageResource(R.drawable.data_entry_note_blue_icon);
                                } else {
                                    viewHolder.enableNotes.setImageResource(R.drawable.data_entry_gray_note_icon);
                                }
                            }
                        }
                        break;
                    }

                    case AUTOCOMPLETE_REQUEST_CODE: {
                        // Bundle extras1 = getIntent().getExtras();

                        String position = data.getStringExtra("POSITION");
                        String selectedValues = data.getStringExtra("RESULT");
                        String inputtype = data.getStringExtra("INPUT_TYPE");
                        String old_selected_values = data.getStringExtra("OLD_VALUES");

                        if (data.getExtras().containsKey("AUTO_GENERATE")) {
                            boolean autoGen = data.getBooleanExtra("AUTO_GENERATE", false);
                            if (formMaster != null)
                                formMaster.AUTO_GENERATE = autoGen;
                            if (formsAdapter != null) {
                                formsAdapter.AUTO_GENERATE = autoGen;
                            }
                        }

                        if (inputtype.equalsIgnoreCase("AUTOSETGENERATOR")) {
                            if (formMaster != null)
                                formMaster.AUTO_SET_LAST_SELECTED_VALUES = old_selected_values;
                            if (formsAdapter != null)
                                formsAdapter.AUTO_SET_LAST_SELECTED_VALUES = old_selected_values;
                        }

                        if (inputtype.equalsIgnoreCase("MULTIMETHODS")) {
                            if (formMaster != null)
                                formMaster.AUTO_METHODS_LAST_SELECTED_VALUES = old_selected_values;
                            if (formsAdapter != null)
                                formsAdapter.AUTO_METHODS_LAST_SELECTED_VALUES = old_selected_values;
                        }

                        int fieldParameterID = Integer.parseInt(position);

                        if (formsAdapter != null) {
                            formsAdapter.updateAutoCompleteView(fieldParameterID, selectedValues);
                        }

                        if (formMaster != null && formMaster.mapMetaObjects.containsKey(fieldParameterID)) {
                            MetaData metaData = formMaster.mapMetaObjects.get(fieldParameterID);

                            if (metaData.getMetaParamID() == fieldParameterID) {
                                FormMaster.DataHolder tempData = formMaster.fmapObject.get(fieldParameterID + "");

                                FormMaster.ViewHolder viewHolder = new FormMaster.ViewHolder();
                                viewHolder = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();

                                tempData.value = selectedValues;

                                if (inputtype.equalsIgnoreCase("AUTOCOMPLETE")) {
                                    formMaster.updateAutoCompleteView(metaData, viewHolder.new_actv, selectedValues, inputtype,
                                            metaData.getMetaLovId(), tempData.getParentlovItemID(), null);
                                    if (tempData.value != null && tempData.getGoto_formID() > 0) {
                                        viewHolder.enableForms.setVisibility(View.VISIBLE);
                                    } else {
                                        //ll.removeView(viewHolder.enableNotes);
                                        viewHolder.enableForms.setVisibility(View.GONE);
                                    }
                                } else if (inputtype.equalsIgnoreCase("MULTIMETHODS") && currCocID != null
                                        && !currCocID.equalsIgnoreCase("0")) {

//                                formMaster.updateMultiMethodAutoCompleteView(metaData, viewHolder.new_actv, selectedValues, inputtype);
                                    formMaster.manageAutoMethods(metaData, currCocID, locID, selectedValues);
                                } else {

                                    formMaster.updateAutoCompleteView(metaData, viewHolder.new_actv, selectedValues, inputtype,
                                            metaData.getMetaLovId(), tempData.getParentlovItemID(), metaData.getNameValueMap());
                                    formMaster.makeFieldsVisible(metaData);

                                    if (metaData.getMetaInputType().equalsIgnoreCase("AUTOSETGENERATOR")) {

                                        String fieldoperands_expression = metaData.getFieldParameterOperands();
                                        if (fieldoperands_expression != null && !fieldoperands_expression.isEmpty() && fieldoperands_expression.contains("COPY")) {
                                            int mobAppID = Integer.parseInt(fieldoperands_expression.substring(fieldoperands_expression.indexOf("{") + 1,
                                                    fieldoperands_expression.lastIndexOf("}")));
                                            Log.i(TAG, "MobileAppID from COPY Expression:" + mobAppID);
                                            metaData.setFormID(mobAppID);
                                            tempData.setGoto_formID(mobAppID);
                                            formMaster.fmapObject.get(fieldParameterID + "").setGoto_formID(mobAppID);
                                        }
                                    }

                                    String extField2 = metaData.getExtField2();

                                    if (extField2 != null && !extField2.isEmpty()) {
                                        formMaster.setDataOnChanged(metaData.getMetaParamID());
                                    }

                                    if (tempData.value != null && tempData.getGoto_formID() > 0) {
                                        viewHolder.enableForms.setVisibility(View.VISIBLE);
                                    } else {
                                        //ll.removeView(viewHolder.enableNotes);
                                        viewHolder.enableForms.setVisibility(View.GONE);
                                    }
                                }
                                break;
                            }

                        }
                        break;
                    }
                }

                if (data != null) {
                    switch (requestCode) {
/*                        case REQUEST_CODE_MEDIA_PICKER: {
                            //know that watermark is added in BaseMenuActivity->onActResult for this result first and here file is updated
                            break;
                        }*/

                        case REQUEST_CODE_CARD_GALLERY_FOR_PHOTOS: {
                            if (formsAdapter != null) {
                                int fieldParamId = data.getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);
                                formsAdapter.refreshPhotos(fieldParamId);
                            }
                        }

                        case REQUEST_CODE_FORM_MASTER_MEDIA_PICKER: {
                            String path = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_PATH);
                            String path1000 = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_1000_PATH);
                            String pathThumb = data.getStringExtra(GlobalStrings.KEY_SELECTED_IMAGE_THUMB_PATH);

                            int fieldParamId = data.getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);
                            if (path != null) {
                                output_file = new File(path);

                                if (path1000 != null)
                                    file1000 = path1000;

                                if (pathThumb != null)
                                    thumbFile = pathThumb;

                                if (formsAdapter != null) {
                                    formsAdapter.updatePhotos(fieldParamId, path);
                                    setAttachmentData(false, fieldParamId + "");
                                }

                                if (formMaster != null && formMaster.mapMetaObjects.containsKey(fieldParamId)) {
                                    MetaData metaData = formMaster.mapMetaObjects.get(fieldParamId);
                                    if (metaData.getMetaParamID() == fieldParamId) {
                                        FormMaster.ViewHolder viewHolder
                                                = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();
                                        if (viewHolder.observedPhotosAdapter != null) {
                                            viewHolder.observedPhotosAdapter.addPhoto(path);
                                            setAttachmentData(false, metaData.getMetaParamID() + "");
                                        }
                                    }
                                }
                            }
                            break;
                        }

                        case REQUEST_CODE_BARCODE_SCANNER: {
                            int fpId = data.getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);
                            String scannedText = data.getStringExtra(GlobalStrings.QR_SCANNED_TEXT);

                            if (formsAdapter != null) {
                                formsAdapter.updateBarcode(fpId, scannedText);
                            }

                            if (formMaster != null && formMaster.mapMetaObjects.containsKey(fpId)) {
                                MetaData metaData = formMaster.mapMetaObjects.get(fpId);
                                FormMaster.ViewHolder viewHolder
                                        = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();

                                FormMaster.DataHolder tempData = formMaster.fmapObject.get(metaData.getMetaParamID() + "");

                                tempData.value = scannedText;
                                formMaster.saveData_and_updateCreationDate(this, metaData, scannedText, curSetID);

                                if (tempData.value != null && !tempData.value.isEmpty()) {
                                    viewHolder.tvBarCode.setText(scannedText);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onActivityResult Error:" + e.getLocalizedMessage());
        }
    }

    @Override
    public void setAttachmentData(boolean openGallery, String fpId) {
        if (!output_file.exists()) {
            Toast.makeText(appContext, "No file found", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = output_file.getPath();

        AttachmentData attachData = new AttachmentData();

        attachData.setEventID(getEventID());
        attachData.setLocationID(getLocID());
        attachData.setAttachementType("P");
        attachData.setFileLocation(filePath);
        attachData.setCreationDate(System.currentTimeMillis());

        if (!openGallery && !fpId.isEmpty()) {
            attachData.setFieldParameterID(fpId);
        }

        attachData.setLatitude(capturedLatitude);
        attachData.setLongitude(capturedLongitude);
        attachData.setDataSyncFlag(null);
        attachData.setEmailSentFlag(null);
        attachData.setTimeTaken(System.currentTimeMillis());
        attachData.setSiteId(getSiteID());
        attachData.setUserId(getUserID());
        attachData.setMobileAppId(getCurrentAppID());
        attachData.setSetId(getCurSetID());
//                    attachData.setExtField1(getCurSetID() + "");
        attachData.setAzimuth(mSensorTracker.getAzimuthInDegress());

        //added on 04/11/22 for attachment api changes
        attachData.setFile1000(file1000);
        attachData.setFileThumb(thumbFile);

        try {
            attachDataSource.insertAttachmentData(attachData, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (openGallery)
            openGallery();
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {

        final View currentFocus = getCurrentFocus();
        try {

            if (!(currentFocus instanceof EditText)
                    || !isTouchInsideView(ev, currentFocus)) {

                if (currentFocus != null) {
                    ((InputMethodManager) getApplicationContext().getSystemService(
                            INPUT_METHOD_SERVICE
                    )).hideSoftInputFromWindow(
                            currentFocus.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FormActivity", "Hide SoftKeyBoard Error:" + e.getMessage());
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * determine if the given motionevent is inside the given view.
     * as
     *
     * @param ev           the given view
     * @param currentFocus the motion event.
     * @return if the given motionevent is inside the given view
     */
    private boolean isTouchInsideView(final MotionEvent ev,
                                      final View currentFocus) {
        final int[] loc = new int[2];
        currentFocus.getLocationOnScreen(loc);
        return ev.getRawX() > loc[0] && ev.getRawY() > loc[1]
                && ev.getRawX() < (loc[0] + currentFocus.getWidth())
                && ev.getRawY() < (loc[1] + currentFocus.getHeight());
    }

    @Override
    public void setRightNavigation(View v) {
        if (getCurSetID() < getActiveSetID()) {
            setCurSetID(getCurSetID() + 1);
            if (formMaster != null)
                formMaster.setCurrentSetID(getCurSetID());
            new PopulateLocationDetailsTask().execute(LOAD_SET_DATA);
        }
    }

    @Override
    public void setLeftNavigation(View v) {
        if ((getCurSetID() - 1) >= 1) {
            setCurSetID(getCurSetID() - 1);

            if (formMaster != null)
                formMaster.setCurrentSetID(getCurSetID());

            new PopulateLocationDetailsTask().execute(LOAD_SET_DATA);
        }
    }

    public void onClickAddContinue() {
        deviceID = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.SESSION_DEVICEID);
        if (deviceID == null || deviceID.isEmpty()) {
            deviceID = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.DEVICEID);
        }
        Log.i(TAG, "Current DeviceID:" + deviceID);

        if (getActiveSetID() == getCurSetID()) { // latest reading
//            formMaster.isSampleDateOrTimeSet = false;//setting to false else it'll cause sample id gen issues
            getNextSet();
            if (formMaster != null)
                formMaster.setCurrentSetID(getCurSetID());
            checkAndEnableNewReading();

            new PopulateLocationDetailsTask().execute(LOAD_SET_DATA);

            if (!Util.isShowNewForms(this)) {
                //06-04-2018 SCROLL TO 0th POSITION
                locationdetail_master_container.requestFocus();
                nested_scroll_view.scrollTo(0, nested_scroll_view.getTop());
            }
        }
    }

    public void setNotesFromDB() {

        if (notesMetaData != null) {
            notes = fieldDataSource.getPreviousReading(getEventID(), getCurSetID(),
                    getLocID(), getSiteID(), getCurrentAppID(),
                    notesMetaData.getMetaParamID(), deviceID);

            if (notesInput != null) {
                notesInput.setText(notes);
            }
        }
    }

    @Override
    public void invokeTextView(View v) {

        MetaDataSource metaData = new MetaDataSource(CurrentContext);
        final List<MetaData> metaNotes = metaData.getMetaDataForNotes(
                getCurrentAppID(), getSiteID(), getLocID());

        if ((metaNotes == null) || (metaNotes.size() == 0)) {
            Toast.makeText(getApplicationContext(), getString(R.string.notes_are_not_enabled),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            notesMetaData = metaNotes.get(0);
        }

        AlertDialog.Builder editalert = new AlertDialog.Builder(this);

        editalert.setTitle(getString(R.string.enter_notes));
        // editalert.setMessage("here is the message");

        notesInput = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        notesInput.setLayoutParams(lp);
        notesInput.setText(notes);
        if (notes != null) {
            notesInput.setSelection(notes.length());
        }
        editalert.setView(notesInput);

        editalert.setPositiveButton(getString(R.string.post_upper_case),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String notesValue = null;
                        notes = notesInput.getText().toString();
                        /*
                         * if (notes.length() > 200) {
                         * System.out.println("gggg"+notes.substring(0, 199));
                         * notesValue = notes.substring(0, 198); } else {
                         * System.out.println("gggg"+notes); notesValue = notes;
                         * }
                         */
                        notesValue = notes;

                        fieldDataSource.updateValue(getEventID(),
                                notesMetaData.getMetaParamID(), getCurSetID(),
                                getLocationID(), notesValue, getSiteID(),
                                getCurrentAppID(), GlobalStrings.CURRENT_GPS_LOCATION,
                                deviceID, getUserID() + "");
                    }
                });

        editalert.show();
    }

    private void initGalleryPicker() {

        pickerOptions = Options.init()
                .setRequestCode(REQUEST_CODE_PIX_IMAGE_PICKER)
                .setCount(1)
                .setFrontfacing(false)
                .setExcludeVideos(true)
                .setVideoDurationLimitinSeconds(60)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath(
                        getMediaStorageDirectory(BaseMenuActivity.MEDIA_TYPE_IMAGE).getAbsolutePath()
                );
    }

    public void openImagePicker(boolean isCamera) {

        if (!Util.isLocationAvailable())
            getLocation();

        Log.i(TAG, "handleCamera() Start time:" + System.currentTimeMillis());
        SharedPref.setCamOrMap();
        SharedPref.putInt("CURRENT_FORM_NUM", getCurrentFormNum());
        SharedPref.putInt("CURRENT_SET", getCurSetID());

        try {
            if (getAttachmentNamePrefix() == null) {
                Toast.makeText(this, "Name Prefix not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            output_file = getOutputMediaFile(MEDIA_TYPE_IMAGE, "p_"
                    + getAttachmentNamePrefix(), this);

            if (!isCamera && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                ImagePicker.with(this)
                        // Crop Image(User can choose Aspect Ratio)
                        .crop(1f, 1f)
                        // User can only select image from Gallery
                        .galleryOnly()
                        .galleryMimeTypes( // no gif images at all
                                new String[]{
                                        "image/png",
                                        "image/jpg",
                                        "image/jpeg"
                                }
                        )
                        // Image resolution will be less than 1080 x 1920
                        .maxResultSize(1024, 1024)
                        // .saveDir(getExternalFilesDir(null)!!)
                        .start(REQUEST_CODE_OS11UP_MEDIAPICKER);
            } else {

                Intent mediaIntent = new Intent(this, MediaPickerActivity.class);
                mediaIntent.putExtra(GlobalStrings.IS_CAMERA, isCamera);

                startActivityForResult(mediaIntent,
                        REQUEST_CODE_MEDIA_PICKER);
            }

            /*            initGalleryPicker();
            Pix.start(this, pickerOptions);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleDrawing() {
        Log.i(TAG, "handleDrawing() Start time:" + System.currentTimeMillis());

        SharedPref.setCamOrMap();
        SharedPref.putInt("CURRENT_FORM_NUM", getCurrentFormNum());
        SharedPref.putInt("CURRENT_SET", getCurSetID());
        Intent intent = new Intent(this, FetchDrawScreen.class);
        intent.putExtra("USER_NAME", this.getUsername());
        intent.putExtra("PASS", this.getPassword());
        intent.putExtra("EVENT_ID", getEventID());
        intent.putExtra("LOC_ID", getLocID());
        intent.putExtra("SITE_ID", getSiteID());
        intent.putExtra("USER_ID", getUserID() + "");
        intent.putExtra("MOBILE_APP_ID", getCurrentAppID());
        intent.putExtra("SET_ID", getCurSetID());
        intent.putExtra("FILE_NAME_PREFIX", "d_" + getAttachmentNamePrefix());

        intent.putExtra("SAVE_DIRECTORY", GlobalStrings.DRAWING_STORAGE_DIR);

        try {
            startActivityForResult(intent, DRAWING_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "handleDrawing() Error To start Drawing App:" + e.getMessage());

        }

        Log.i(TAG, "handleDrawing() End time:" + System.currentTimeMillis());
    }

    public void showFormReport() {
        FieldDataSource fieldSource = new FieldDataSource(this);
        ArrayList<FieldParamInfo> paramLabelList = fieldSource.getParamLabelsForReport(getCurrentAppID());

        Intent reportIntent = new Intent(this, ReportView.class);
        reportIntent.putExtra("CURRENT_APP_ID", getCurrentAppID());
        reportIntent.putExtra("PARENT_APP_ID", 0);
        reportIntent.putExtra("SITE_ID", siteID);
        reportIntent.putExtra("SITE_NAME", siteName);
        reportIntent.putExtra("USER_NAME", username);
        reportIntent.putExtra("EVENT_ID", eventID);
        reportIntent.putExtra(GlobalStrings.KEY_LOCATION_ID, getLocationID());
        reportIntent.putExtra(GlobalStrings.FP_IDS_LIST, paramLabelList);
        startActivityForResult(reportIntent, REQUEST_CODE_FORM_REPORT);
    }

    public void showFormReportWithFieldSelection() {
        Intent repot = new Intent(this, ReportFieldsSelectionActivity.class);
        // repot.putExtra("APP_ID", getCurrentAppID());
        repot.putExtra("CURRENT_APP_ID", getCurrentAppID());
        repot.putExtra("PARENT_APP_ID", 0);
        repot.putExtra("SITE_ID", getSiteID());
        repot.putExtra("SITE_NAME", getSiteName());
        repot.putExtra("USER_NAME", getUsername());
        repot.putExtra("APP_NAME", getCurrentAppName());
        repot.putExtra("EVENT_ID", getEventID());
        repot.putExtra(GlobalStrings.KEY_LOCATION_ID, getLocationID());
        startActivity(repot);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    /**
     * @return the correctedLat
     */
    public double getCorrectedLat() {
        return correctedLat;
    }

    /**
     * @param correctedLat the correctedLat to set
     */
    void setCorrectedLat(double correctedLat) {
        this.correctedLat = correctedLat;
    }

    /**
     * @return the correctedLong
     */
    public double getCorrectedLong() {
        return correctedLong;
    }

    /**
     * @param correctedLong the correctedLong to set
     */
    void setCorrectedLong(double correctedLong) {
        this.correctedLong = correctedLong;
    }

    /**
     * @return the invalidating
     */
    boolean isInvalidating() {
        return invalidating;
    }

    /**
     * @param invalidating the invalidating to set
     */
    void setInvalidating(boolean invalidating) {
        this.invalidating = invalidating;
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

    public void setMapObject() {
        for (int i = 0; i < this.metaValues.size(); i++) {//this.metaValues
            setMapObject(metaValues.get(i), i);
        }
    }

    public void setMapObject(MetaData metaData, int pos) {
        String fpId = metaData.getMetaParamID() + "";

        FormMaster.DataHolder h1 = new FormMaster.DataHolder();

        // this.sethighlight();
        h1.imgStatus = FormMaster.indicatorStatus.ImageStatusInvisible;
        h1.value = fieldDataSource.getPreviousReading(getEventID(), this.getCurSetID(),
                this.getLocID(), this.getSiteID(), this.getCurrentAppID(),
                metaData.getMetaParamID(), deviceID);

        FormMaster.DataHolder.isnote_taken = fieldDataSource.isNoteTaken_Data(getEventID(), this.getCurSetID(),
                this.getLocID(), this.getSiteID(), this.getCurrentAppID(),
                metaData.getMetaParamID());

        if (!FormMaster.DataHolder.isnote_taken) {
            // TODO: 28-03-2018 CHECK IN ATTACHMENT
            FormMaster.DataHolder.isnote_taken = attachDataSource.isAttachNoteExists(getEventID() + "",
                    this.getLocID(), this.getSiteID() + "", this.getCurrentAppID() + "", this.getCurSetID() + "",
                    fpId);
        }

        h1.baseUntis = "";
        if (this.currentReading != null && this.currentReading.size() > pos) {
            h1.units = this.currentReading.get(pos).getUnits();
        }

        try {

            // if ((ObjContext.getCurSetID()-2) >=1) {
            if ((this.getCurSetID() - 2) >= 1) {
                h1.dhPrevReading1 = fieldDataSource.getPreviousReading(getEventID(),
                        this.getCurSetID() - 2, this.getLocID(),
                        this.getSiteID(), this.getCurrentAppID(),
                        metaData.getMetaParamID(), deviceID);
                DefaultValueDataSource defaultdatasrc = new DefaultValueDataSource(CurrentContext);

                h1.dhPrevReading11 = String.valueOf(defaultdatasrc.getdefvalue(this.getLocID(), this.getCurrentAppID()));

                if (h1.dhPrevReading1 == null) { // to be able to put NR in
                    // prevreadind

                    h1.dhPrevReading1 = "";
                }
            } else {
                h1.dhPrevReading1 = null;
            }

            // if ((this.getCurSetID()-1) >=1) {
            if ((this.getCurSetID() - 1) >= 1) {
                h1.dhPrevReading2 = fieldDataSource.getPreviousReading(getEventID(),
                        this.getCurSetID() - 1, this.getLocID(),
                        this.getSiteID(), this.getCurrentAppID(),
                        metaData.getMetaParamID(), deviceID);

                if (h1.dhPrevReading2 == null) { // to be able to put NR in
                    // prevreadind
                    h1.dhPrevReading2 = "";
                }
            } else {
                h1.dhPrevReading2 = null;
            }
            this.mapObject.put(fpId, h1);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setMapObject() Error:" + e.getMessage());
        }
    }

    public void clearMapObjects() {
        // this.mapObject.clear();
        for (int i = 0; i < this.metaValues.size(); i++) {
            String fpID = metaValues.get(i).getMetaParamID() + "";

            FormMaster.DataHolder h1 = this.mapObject.get(fpID);
            h1.imgStatus = FormMaster.indicatorStatus.ImageStatusInvisible;
            h1.value = fieldDataSource.getPreviousReading(getEventID(), this.getCurSetID(),
                    this.getLocID(), this.getSiteID(), this.getCurrentAppID(),
                    metaValues.get(i).getMetaParamID(), deviceID);

            FormMaster.DataHolder.isnote_taken = fieldDataSource.isNoteTaken_Data(getEventID(), this.getCurSetID(),
                    this.getLocID(), this.getSiteID(), this.getCurrentAppID(),
                    metaValues.get(i).getMetaParamID());

            if (!FormMaster.DataHolder.isnote_taken) {
                // TODO: 28-03-2018 CHECK IN ATTACHMENT
                FormMaster.DataHolder.isnote_taken = attachDataSource.isAttachNoteExists(getEventID() + "",
                        this.getLocID(), this.getSiteID() + "", this.getCurrentAppID() + "", this.getCurSetID() + "",
                        fpID);
            }
            h1.baseUntis = "";
            if (this.currentReading != null && currentReading.size() > i) {
                try {
                    h1.units = this.currentReading.get(i).getUnits();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "clearMapObjects() error:" + e.getMessage());
                }
            }
            try {
                // if ((ObjContext.getCurSetID()-2) >=1) {
                if ((this.getCurSetID() - 2) >= 1) {

                    h1.dhPrevReading1 = fieldDataSource.getPreviousReading(getEventID(),
                            this.getCurSetID() - 2, this.getLocID(),
                            this.getSiteID(), this.getCurrentAppID(),
                            this.metaValues.get(i).getMetaParamID(), deviceID);

                    if (h1.dhPrevReading1 == null) { // to be able to put NR in
                        // prev reading
                        h1.dhPrevReading1 = "";
                    }
                } else {
                    h1.dhPrevReading1 = null;
                }

                // if ((ObjContext.getCurSetID()-1) >=1) {
                if ((this.getCurSetID() - 1) >= 1) {
                    h1.dhPrevReading2 = fieldDataSource.getPreviousReading(getEventID(),
                            this.getCurSetID() - 1, this.getLocID(),
                            this.getSiteID(), this.getCurrentAppID(),
                            this.metaValues.get(i).getMetaParamID(), deviceID);

                    if (h1.dhPrevReading2 == null) { // to be able to put NR in
                        // prevreadind
                        h1.dhPrevReading2 = "";
                    }
                } else {
                    h1.dhPrevReading2 = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setNotesFromDB();
    }

    /**
     * @return the locID
     */
    public String getLocID() {
        return locID;
    }

    /**
     * @param locID the locID to set
     */
    void setLocID(String locID) {
        this.locID = locID;
    }

    /**
     * @return the activeSetID
     */
    private int getActiveSetID() {
        return activeSetID;
    }

    /**
     * @param activeSetID the activeSetID to set
     */
    protected void setActiveSetID(int activeSetID) {
        this.activeSetID = activeSetID;
    }

    /**
     * @param currentAppType the currentAppType to set
     */
    void setCurrentAppType(String currentAppType) {
        this.currentAppType = currentAppType;
    }

    public enum Validate {
        allNull, valid, invalid
    }

    protected class PopulateLocationDetailsTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress();
//            form_loading_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Integer... task) {
            if (task[0] == LOAD_SET_DATA) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invalidateListView();
                        showSetData();

                        if (!Util.isShowNewForms(FormActivity.this)) {
                            formMaster.setCurrentSetDataToForm();
                            formMaster.setDataOnChanged(0);//metaDataList
                        } else {
                            metaValues = getFormData();
                            formsAdapter = new FormsAdapter((LocationDetailActivity) CurrentContext,
                                    FormActivity.this, new ArrayList<>(metaValues), getSiteID() + "", getLocationID(),
                                    getEventID() + "", getParentAppID() + "");

                            rvForms.setAdapter(formsAdapter);
                        }
                    }
                });
            } else {
                populateLocationDetail();
            }
            return task[0];
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            //15-02-2018 LOAD FORM=0,LOAD_SET_DATA=1
            if (s == LOAD_FORM) {
//                getListView().setAnimation(
//                        AnimationUtils.loadAnimation(CurrentContext,
//                                R.anim.grow_from_top));
            } else {
                if (getAllowMultipleSets() != 0) {
                    menuMultipleActions.setVisibility(View.VISIBLE);

                    if (getActiveSetID() == getCurSetID()) {
                        fab_new_reading.setVisibility(View.VISIBLE);
                    } else {
                        fab_new_reading.setVisibility(View.GONE);
                    }

                    if (getCurSetID() <= getActiveSetID() && getActiveSetID() != 1) {
                        fab_delete_current_reading.setVisibility(View.VISIBLE);
                    } else if (getActiveSetID() == 1) {
                        fab_delete_current_reading.setVisibility(View.GONE);
                    }
                } else {
                    menuMultipleActions.setVisibility(View.GONE);
                }
            }

            //delay is to allow the form to be ready before adding any changes
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createSetOrUpdateDepthField();

                    if (isJumpToField)
                        jumpToAnyField();
                }
            }, 200);

            cancelAlertProgress();
//            form_loading_bar.setVisibility(View.GONE);

            String locInstruction = new LocationDataSource(FormActivity.this)
                    .getLocationInstruction(getLocationID(), siteID, currentAppID);

            if (locInstruction != null && !locInstruction.isEmpty()) {
                AlertManager.showNormalAlert("Location Instructions:",
                        locInstruction, "Okay", "", false,
                        FormActivity.this);
            }
        }
    }

    abstract void createSetOrUpdateDepthField();

    public class CustomComparator implements Comparator<GalleryItem> {

        @Override
        public int compare(GalleryItem lhs, GalleryItem rhs) {
            Log.i(TAG, "CustomComparator  compare() Start time:" + System.currentTimeMillis());

            if (lhs.getTxtDate() != null && rhs.getTxtDate() != null) {

                Long date1 = Long.parseLong(lhs.getTxtDate());
                Long date2 = Long.parseLong(rhs.getTxtDate());

                int count = date2.compareTo(date1);
                return count;
            }

            Log.i(TAG, "CustomComparator  compare() End time:" + System.currentTimeMillis());

            return 0;
        }
    }
}
