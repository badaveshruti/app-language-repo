package qnopy.com.qnopyandroid.ui.activity;

import static qnopy.com.qnopyandroid.util.Util.delete_All_Log;
import static qnopy.com.qnopyandroid.util.Util.isTablet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.AppNavigationDrawer;
import qnopy.com.qnopyandroid.adapter.ChildAppsAdapter;
import qnopy.com.qnopyandroid.adapter.CopiedFormsAdapter;
import qnopy.com.qnopyandroid.clientmodel.ChangeEventModel;
import qnopy.com.qnopyandroid.clientmodel.CopiedTemplate;
import qnopy.com.qnopyandroid.clientmodel.CopyTemplateModel;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.CopiedFormTemplatesDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.FileFolderDataSource;
import qnopy.com.qnopyandroid.db.FormSitesDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.TempLogsDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.soilLogReport.DepthFieldData;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.soilLogReport.SoilLogReportActivity;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.AlertButtonOnClick;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.photogallery.AsyncTask;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp;
import qnopy.com.qnopyandroid.requestmodel.WorkOrderTask;
import qnopy.com.qnopyandroid.sensors.MobileSensor;
import qnopy.com.qnopyandroid.ui.fragment.ChangeEventBottomSheetFragment;
import qnopy.com.qnopyandroid.ui.fragment.FormDetailsFragment;
import qnopy.com.qnopyandroid.ui.fragment.MobileReportFragment;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.FormMaster;
import qnopy.com.qnopyandroid.uiutils.SendDBTask;
import qnopy.com.qnopyandroid.util.SharedPref;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class LocationDetailActivity extends FormActivity implements OnTaskCompleted,
        ChangeEventBottomSheetFragment.SheetEventClickListener {

    private static final String TAG = "LocationDetailActivity";
    public static PopupWindow popup;

    private float mTotalWidth; // pixels
    private float mPercentLeft = 50; // percent of screen
    private float mMinimumWidth = 100; // percent of screen
    private static final String EXTRA_CURRENT_INDEX = "MySplitPaneActivity.EXTRA_CURRENT_INDEX";
    private static final String EXTRA_PERCENT_LEFT = "MySplitPaneActivity.EXTRA_PERCENT_LEFT";
    private static final String EXTRA_MINIMUM_WIDTH_DIP = "MySplitPaneActivity.EXTRA_MINIMUM_WIDTH_DIP";
    private FrameLayout mLeftPane;
    private FrameLayout mRightPane;

    private LinearLayout coordinatorLayout;
    private Snackbar snackbar;
    String msgBoard;
    boolean delete = false;
    public int last_position = -1;
    public static Activity LocDetailActivity;
    Context mmcontext;

    public static final int ADD_FORMFIELD = 11;
    List<SSiteMobileApp> sitemobAppList = null;
    boolean isCompressed = false;
    public TextView mandatoryalerttxt, saved_photos_count_tv;
    public List<WorkOrderTask> workOrderTaskList = null;
    AlertDialog.Builder alertBuilder;

    LinearLayout sync_conatiner, camera_container, gallery_container, photos_count_container, nav_cantainer_ll;
    ImageView syncdata_ib, camera_ib, gallery_ib, drawing_ib;
    TextView sync_tv, camera_tv, gallery_tv, drawing_tv;
    public TextView title_text, subtitle_text = null;
    ImageButton showForm_btn;

    public String title, subtitle;
    Toolbar toolbar;
    private AppBarLayout mAppBarLayout;
    public ActionBar actionBar;
    private FormDetailsFragment formDetailsFrag;
    private LinearLayout llSplitPane;
    private RelativeLayout llFormDetails;
    public static MobileReportFragment mobileReportFragment;
    private BadELFGPSTracker badElf;
    private ConstraintLayout layoutSearchString;
    private EditText edtSearchString;
    private ImageView ivSearchUp;
    private ImageView ivSearchDown;
    private ImageView ivCancelSearch;
    private ArrayList<Integer> searchedValues = new ArrayList<>();
    private int searchCount = 0;
    private TextView tvSearchResultsCount;

    private RecyclerView.LayoutManager layoutManagerChildApps;
    private RecyclerView.SmoothScroller smoothScroller;
    private ChildAppsAdapter childAppsAdapter;
    private ActivityResultLauncher<Intent> soilReportActivityLauncher;
    private boolean isCreateNewSetForDepth;
    private DepthFieldData depthFieldData;
    private int depthFieldId = 0;
    private boolean useDepthData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_detail_ux);

        soilReportActivityLauncher
                = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        depthFieldData = (DepthFieldData) result.getData()
                                .getSerializableExtra(GlobalStrings.FORM_DETAILS);
                        depthFieldId = result.getData()
                                .getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);

                        int position = 0;
                        for (int i = 0; i < childAppList.size(); i++) {
                            MobileApp app = childAppList.get(i);
                            if (app.getAppID() == depthFieldData.getTabId())
                                position = i;
                        }

                        FieldDataSource fieldDataSource = new FieldDataSource(this);
                        int maxSet = fieldDataSource.getmaxSetID_MobileApp(getLocationID(),
                                getEventID() + "", depthFieldData.getTabId() + "");
                        String curDepthValue = fieldDataSource.getStringValueFromId(getEventID(), getLocationID(),
                                depthFieldData.getTabId(), maxSet,
                                depthFieldId + "");

                        //check the cur depth and the
                        //intended depth to fill matches, if matches then leave else
                        //check if cur depth is empty, if empty then fill the cur depth value
                        //if cur depth and intended depth don't match then create new set
                        //note for matching value you non empty string
                        if (curDepthValue != null
                                && Integer.parseInt(curDepthValue)
                                == depthFieldData.getDepthValue()) {
                            useDepthData = false;
                        } else if (curDepthValue != null
                                && Integer.parseInt(curDepthValue)
                                != depthFieldData.getDepthValue()) {
                            isCreateNewSetForDepth = true;
                            useDepthData = true;
                        } else if (curDepthValue == null || curDepthValue.isEmpty()) {
                            isCreateNewSetForDepth = false;
                            useDepthData = true;
                        }

                        if (getCurrentFormNum() != position) {
                            jumpToChildApp(position);
                        } else if (isCreateNewSetForDepth) {
                            createSetOrUpdateDepthField();
                        }
                    }
                });

        llSplitPane = findViewById(R.id.activity_split_pane);
        llFormDetails = findViewById(R.id.ll_form_details);

        if (Util.isTablet(this) && isSplitScreenEnabled) {
            formDetailsFrag = new FormDetailsFragment();
            loadFragment(formDetailsFrag, R.id.content_split_right_pane);
            llSplitPane.setVisibility(View.VISIBLE);
            llFormDetails.setVisibility(View.GONE);
        } else {
            llSplitPane.setVisibility(View.GONE);
            llFormDetails.setVisibility(View.VISIBLE);
        }

        GlobalStrings.currentContext = this;
        LocDetailActivity = this;
        mmcontext = this;
        CurrentContext = this;
        appContext = this;

        init();

        mAppBarLayout = findViewById(R.id.mAppBarLayout);
        toolbar = findViewById(R.id.location_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.entry_form_done);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.entry_form_done);
        }

        SharedPref.globalContext = getApplicationContext();

        title = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.CURRENT_APPNAME);
        subtitle = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.CURRENT_LOCATIONNAME);

        nav_cantainer_ll = toolbar.findViewById(R.id.form_nav_ll);
        title_text = toolbar.findViewById(R.id.form_title);
        subtitle_text = toolbar.findViewById(R.id.sub_title_tv);
        showForm_btn = toolbar.findViewById(R.id.show_form);
        tvCalculate = findViewById(R.id.tvCalculate);

        setupSearchUI();

        setUpClickListeners();

        title_text.setText(title);
        title_text.setSelected(true);
        subtitle_text.setSelected(true);
        subtitle_text.setText(subtitle);

        mSensorTracker = new MobileSensor(CurrentContext);

        try {

            getExtrasFromIntent();

            SharedPref.globalContext = getApplicationContext();

            String userID = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.SESSION_USERID);

            if (getSiteID() == 0) {
                setSiteID(Integer.parseInt(Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.CURRENT_SITEID)));
            }

            compnyID = Integer.parseInt(Util.getSharedPreferencesProperty(mmcontext, GlobalStrings.COMPANYID));

            setUserID(Integer.parseInt(userID));
            setUsername(Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.USERNAME));
            setAttachmentNamePrefix(System.currentTimeMillis() + "");//getSiteName().replace(" ", "_") + "_" + (locName).replace(" ", "_") + "_"
            deviceID = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.SESSION_DEVICEID);//DeviceInfo.getDeviceID(CurrentContext);
            if (deviceID == null || deviceID.isEmpty()) {
                deviceID = Util.getSharedPreferencesProperty(CurrentContext, GlobalStrings.DEVICEID);
            }

            try {
                compnyID = Integer.parseInt(Util.getSharedPreferencesProperty(mmcontext, GlobalStrings.COMPANYID));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate() Error in get compnyID from session:" + e.getMessage());
                compnyID = 0;
            }

            mapObject = new HashMap<>();

            //sub form related
            Log.i(TAG, "onCreate() Retrace LocDet " + SharedPref.getRetracing());

            mobileAppSource = new MobileAppDataSource(CurrentContext);

            Log.i(TAG, "onCreate()  getChild apps list for UserID:" + getUserID()
                    + " ParentAppID:" + getParentAppID() + " SiteID:" + getSiteID() + " CompnyID:" + compnyID);

            childAppList = mobileAppSource.getChildApps(getParentAppID(), getSiteID(), getLocationID());
            maxApps = childAppList.size();

            if (maxApps > 0) {
                setCurrentAppID(childAppList.get(getCurrentFormNum()).getAppID());
                setUpChildAppsRecycler();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_forms_found), Toast.LENGTH_SHORT).show();
                //   childAppList=mobileAppSource.getChildAppForSite(getUserID(),getParentAppID(),getSiteID(),compnyID);
                finish();
            }

            setAppName();

            if (Util.isTablet(this) && isSplitScreenEnabled) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInfo_container = formDetailsFrag.setInfo_container;
                        setNavLeft = formDetailsFrag.setNavLeft;
                        setNavRight = formDetailsFrag.setNavRight;
                        setCount = formDetailsFrag.setCount;
                        fab_new_reading = formDetailsFrag.fab_new_reading;
                        fab_delete_current_reading = formDetailsFrag.fab_delete_current_reading;

                        nested_scroll_view = formDetailsFrag.nested_scroll_view;
                        locationdetail_master_container = formDetailsFrag.locationDetail_master_container;
                        form_loading_bar = formDetailsFrag.form_loading_bar;
                        menuMultipleActions = formDetailsFrag.menuMultipleActions;
                        rvForms = formDetailsFrag.rvForms;
                        tvCalculate = formDetailsFrag.tvCalculate;
                        new PopulateLocationDetailsTask().execute(LOAD_FORM);
                        mobileReportFragment = new MobileReportFragment();
                        loadFragment(mobileReportFragment, R.id.content_split_left_pane);
                    }
                }, 200);
            } else {
                new PopulateLocationDetailsTask().execute(LOAD_FORM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate() Captured error in form Load:" + e.getMessage());
            finish();
        }

        setImageDividerPane();
    }

    private void setUpClickListeners() {
        nav_cantainer_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appNavigation(view);
            }
        });
        title_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appNavigation(view);
            }
        });
        showForm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appNavigation(view);
            }
        });

        tvCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (formMaster != null) {
                    new populateLocationDetail_Task().execute(LOAD_SET_DATA);
                }*/
                new PopulateLocationDetailsTask().execute(LOAD_SET_DATA);
            }
        });

        drawing_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDrawing();
            }
        });

        drawing_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDrawing();
            }
        });

        camera_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorTracker.registerSensorService();
                Log.i(TAG, "Sensor Status- ACCELEROMETER =" + mSensorTracker.isAccelerometerAvailable());
                Log.i(TAG, "Sensor Status- MAGNETOMETER =" + mSensorTracker.isMagnetoMeterAvailable());
                if (!mSensorTracker.isAccelerometerAvailable() || !mSensorTracker.isMagnetoMeterAvailable()) {
                    mSensorTracker.unregisterSensorService();
                    Log.i(TAG, "Un-Register Sensor Service ");
                }

                openImagePicker(true);
            }
        });

        camera_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorTracker.registerSensorService();
                Log.i(TAG, "Sensor Status- ACCELEROMETER =" + mSensorTracker.isAccelerometerAvailable());
                Log.i(TAG, "Sensor Status- MAGNETOMETER =" + mSensorTracker.isMagnetoMeterAvailable());
                if (mSensorTracker.isAccelerometerAvailable() && mSensorTracker.isMagnetoMeterAvailable()) {

                } else {
                    mSensorTracker.unregisterSensorService();
                    Log.i(TAG, "Un-Register Sensor Service ");

                }
                openImagePicker(true);
            }
        });

        gallery_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker(false);

                //refer line 1264 of qnopy.com.qnopyandroid.fetchdraw.fetchDrawScreen.class
            }
        });

        gallery_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker(false);
            }
        });

        syncdata_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncData();
            }
        });

        sync_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncData();
            }
        });

        photos_count_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        saved_photos_count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void setUpChildAppsRecycler() {
        RecyclerView rvChildApps = findViewById(R.id.rvChildApps);
        layoutManagerChildApps = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        rvChildApps.setLayoutManager(layoutManagerChildApps);
        rvChildApps.setItemAnimator(new DefaultItemAnimator());

        //this delay is just to make the title at center first and then start showing form
        childAppsAdapter = new ChildAppsAdapter(childAppList, this, new ChildAppsAdapter.OnChildAppClickedListener() {
            @Override
            public void onChildAppSelected(MobileApp app, int position) {
                checkRequiredFieldAndNavigateForm(app, position);
            }
        });

        rvChildApps.setAdapter(childAppsAdapter);

        smoothScroller = new CenterScroller(rvChildApps.getContext());

        smoothScroller.setTargetPosition(0);
        layoutManagerChildApps.startSmoothScroll(smoothScroller);
    }

    public static class CenterScroller extends LinearSmoothScroller {

        float MILLISECONDS_PER_INCH = 120f;

        public CenterScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return MILLISECONDS_PER_INCH / displayMetrics.xdpi;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSearchUI() {
        layoutSearchString = findViewById(R.id.layoutSearchString);
        edtSearchString = findViewById(R.id.edtSearchString);
        edtSearchString.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchValueInformData(v.getText().toString());
                return true;
            }
            return false;
        });

        edtSearchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    clearSearchStringValues(false);
                }
            }
        });

        edtSearchString.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtSearchString.getRight()
                        - edtSearchString.getCompoundDrawables()[DRAWABLE_RIGHT
                        ].getBounds().width())) {
                    clearSearchStringValues(true);
//                    Util.showKeyboard(LocationDetailActivity.this, edtSearchString);//as drawable clicked keyboards gets hide so showing again
                    return false;
                }
            }
            return false;
        });

/*        edtSearchString.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Util.showKeyboard(LocationDetailActivity.this, edtSearchString);
                }
            }
        });*/

        tvSearchResultsCount = findViewById(R.id.tvResultsCount);
        ivSearchUp = findViewById(R.id.ivSearchUp);
        ivSearchUp.setImageDrawable(VectorDrawableUtils.getDrawable(this,
                R.drawable.collapse_arrow, R.color.white));
        ivSearchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCount--;
                if (searchedValues.isEmpty() || (searchCount < 0
                        || searchCount >= searchedValues.size())) {
                    searchCount = 0;
                }

                try {
                    if (!searchedValues.isEmpty())
                        nested_scroll_view.scrollTo(0,
                                searchedValues.get(searchCount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ivSearchDown = findViewById(R.id.ivSearchDown);
        ivSearchDown.setImageDrawable(VectorDrawableUtils.getDrawable(this,
                R.drawable.expand_arrow, R.color.white));
        ivSearchDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCount++;
                if (searchedValues.isEmpty() || (searchCount < 0
                        || searchCount >= searchedValues.size())) {
                    searchCount = searchedValues.size() - 1;
                }

                try {
                    if (!searchedValues.isEmpty())
                        nested_scroll_view.scrollTo(0,
                                searchedValues.get(searchCount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ivCancelSearch = findViewById(R.id.ivCancelSearch);
        ivCancelSearch.setImageDrawable(VectorDrawableUtils.getDrawable(this,
                R.drawable.ic_cancel_black_32dp, R.color.white));

        ivCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.VISIBLE);
                edtSearchString.setText("");
                layoutSearchString.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImageDividerPane() {
        // get screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mTotalWidth = size.x;

        // convert minimum width from dip to percent
        mMinimumWidth = convertDipToPercent(100);

        // get left and right pane and set weights
        mLeftPane = (FrameLayout) findViewById(R.id.content_split_left_pane);
        mRightPane = (FrameLayout) findViewById(R.id.content_split_right_pane);
        RelativeLayout divider = findViewById(R.id.split_pane_divider);
        divider.setOnTouchListener(new DividerTouchListener());
    }

    @Override
    public void checkRequiredField(boolean isRightForm, View view) {
        super.checkRequiredField(isRightForm, view);
        clearSearchStringValues(true);

        if (checkForRequiredFields()) {
            showMissingRequiredFieldsAlert(true, isRightForm, view);
        } else {
            if (isRightForm) {
                onClickRightForm(view);
            } else {
                onClickLeftForm(view);
            }
        }
    }

    @Override
    public void checkRequiredFieldAndNavigateForm(MobileApp app, int position) {
        super.checkRequiredFieldAndNavigateForm(app, position);
        clearSearchStringValues(true);

        if (checkForRequiredFields()) {
            showMissingRequiredFieldsAlert(app, position);
        } else {
            navigateToFormClicked(app, position);
        }
    }

    private void navigateToFormClicked(MobileApp app, int position) {
        reqFieldCount = 0;
        showOneTimeAlert = true;

        smoothScroller.setTargetPosition(position);
        layoutManagerChildApps.startSmoothScroll(smoothScroller);
        if (childAppsAdapter != null) {
            childAppsAdapter.setItemSelected(position);
        }

        setCurrentFormNum(position);
        setCurrentAppID(app.getAppID());
        setCurrentAppName(app.getAppName());

        //this delay is just to make the title at center first and then start showing form
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PopulateLocationDetailsTask().execute(LOAD_FORM);
            }
        }, 500);

        if (formMaster != null)
            formMaster.setCurrentFormNumber(getCurrentFormNum());

        if (LocationDetailActivity.mobileReportFragment != null && Util.isTablet(this)
                && isSplitScreenEnabled) {
            LocationDetailActivity.mobileReportFragment.setReportDetails();
        }
        currCocID = null;//setting it as null as form is changed and it is handled only for the form where coc is picked
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        //14-Feb-17
        //setActionBarTitle();
        // showSetData();

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.location_detail_menu, menu);
//        menu.findItem(R.id.user).setTitle("Hi  " + getUsername() + "!");

        String comp_image = Util.getSharedPreferencesProperty(mmcontext, GlobalStrings.IS_COMPRESS_IMAGE);

        if (comp_image == null) {
            isCompressed = true;
        } else {
            isCompressed = Boolean.parseBoolean(comp_image);
        }

        Util.setSharedPreferencesProperty(CurrentContext, GlobalStrings.IS_COMPRESS_IMAGE, String.valueOf(isCompressed));

//      isShowGallery = Boolean.parseBoolean(Util.getSharedPreferencesProperty(mmcontext, GlobalStrings.IS_SHOW_GALLERY));

        if (new FormSitesDataSource(this).isAppTypeSoilLog(getParentAppID() + "",
                getSiteID() + ""))
            menu.findItem(R.id.reportSoilLog).setVisible(true);

        menu.findItem(R.id.enable_compression).setChecked(isCompressed);
        menu.findItem(R.id.enable_compression).setVisible(false);
//      menu.findItem(R.id.enable_form_gallery).setChecked(isShowGallery);

        if (Util.isShowNewForms(this))
            menu.findItem(R.id.menu_search_string).setVisible(false);

        AppPreferenceDataSource ds = new AppPreferenceDataSource(CurrentContext);

//        if (IS_COC_LOCATION) {
//            menu.findItem(R.id.taskorder).setVisible(true);
//        } else {
//            menu.findItem(R.id.taskorder).setVisible(false);
//        }

        //KEY_PROJECT_FILE
        menu.findItem(R.id.filefolder)
                .setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_PROJECT_FILE,
                        getUserID()));

        //KEY_EMERGENCY
        menu.findItem(R.id.nearby)
                .setVisible(ds.isFeatureAvailable(GlobalStrings.KEY_EMERGENCY,
                        getUserID()));

        //KEY_PHOTO_RESOLUTION
/*        if (ds.isFeatureAvailable(GlobalStrings.KEY_PHOTO_RESOLUTION, getUserID())) {
            menu.findItem(R.id.enable_compression).setVisible(false);
        } else {
            menu.findItem(R.id.enable_compression).setVisible(false);
        }*/

        // KEY_DOWNLOAD_DATA
/*
        if (ds.isFeatureAvailable(GlobalStrings.KEY_DOWNLOAD_DATA, getUserID())) {
            menu.findItem(R.id.download_your_owndata).setVisible(true);
        } else {
            menu.findItem(R.id.download_your_owndata).setVisible(false);
        }
*/
        return true;
    }

    public void showSoilLogReport() {
        SoilLogReportActivity.Companion.startSoilLogActivity(this, childAppList,
                getSiteID() + "", getLocID(), getEventID() + "",
                soilReportActivityLauncher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final LocationDetailActivity context = LocationDetailActivity.this;

        Log.i(TAG, "onOptionsItemSelected() Item Selected:" + item.getTitle());
        switch (item.getItemId()) {

            case R.id.enable_compression:

                isCompressed = !item.isChecked();
                item.setChecked(isCompressed);
                Util.setSharedPreferencesProperty(context, GlobalStrings.IS_COMPRESS_IMAGE, String.valueOf(isCompressed));
                GlobalStrings.COMPRESS_IMAGE = isCompressed;
                Log.d(TAG, "\n onOptionsItemSelected() menu selected : Enable Photo Compression:" + isCompressed + ",Time:" + System.currentTimeMillis());

                return true;

            case R.id.app_preferences:
                Intent pref_intent = new Intent(context, AppPreferencesActivity.class);
                startActivity(pref_intent);
                return true;

            case R.id.taskorder:
//                appNavigationForTask();
                return true;

            case R.id.report:
//                showFormReportWithFieldSelection();
                showFormReport();
                return true;

            case R.id.reportSoilLog:
                showSoilLogReport();
                return true;

            case R.id.nearby:
                Log.d(TAG, "\n onOptionsItemSelected() menu selected : NearBy Hospitals ,Start Time:" + System.currentTimeMillis());

                if (CheckNetwork.isInternetAvailable(context)) {

                    Intent mapIntent = new Intent(context, MapForSiteActivity.class);
                    mapIntent.putExtra("PREV_CONTEXT", "LocationDetail");
                    mapIntent.putExtra("OPERATION", "nearby");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }

                Log.d(TAG, "\n onOptionsItemSelected() menu selected : NearBy Hospitals ,End Time:" + System.currentTimeMillis());

                return true;

            case android.R.id.home:
                if (checkForRequiredFields()) {
                    //commented on 13 Dec, 21 for new child apps list
/*                    //the 2nd param as false/true doesn't matter unless 1st param is true
                    // 2nd and 3rd param depends on 1st param if true
                    showMissingRequiredFieldsAlert(false, false, null);*/

                    showMissingRequiredFieldsAlert(null, -1);
                } else {
                    if (last_position > -1) {
                        jumpToChildApp(last_position);
                        last_position = -1;
                    } else {
                        finish();
                    }
                }
                return true;

            case R.id.filefolder:

                List<FileFolderItem> list = new FileFolderDataSource(context).getHomeFileFolderItemList(getSiteID() + "");
                Log.i(TAG, "onOptionsItemSelected() File Folder List Item Count:" + list.size() + "");

                if (list.size() < 1) {
                    startActivity(new Intent(context, FileFolderSyncActivity.class));
                } else {
                    startActivity(new Intent(context, FileFolderMainActivity.class));
                }

                return true;

            case R.id.download_your_owndata:

                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(context);

                alertDialogBuilder1.setTitle(getString(R.string.downloadYourData));
                alertDialogBuilder1.setMessage(getString(R.string.dow_you_want_dwnld_own_data));
                // set positive button: Yes message
                alertDialogBuilder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to a new activity of the app
                        if (CheckNetwork.isInternetAvailable(context)) {
                            Intent downloadintent = new Intent(context, DownloadYourOwnDataActivity.class);
                            downloadintent.putExtra("SITEID", getSiteID());
                            downloadintent.putExtra("EVENTID", getEventID());
                            downloadintent.putExtra("PARENTAPPID", getParentAppID());
                            downloadintent.putExtra("SITEID", siteID);
                            startActivity(downloadintent);
                            finish();
                        } else {
                            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                        }
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
                return true;

            case R.id.menu_change_event:
                ChangeEventBottomSheetFragment fragment
                        = ChangeEventBottomSheetFragment.newInstance(getSiteID() + "",
                        "1", getEventID() + "");
                fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
                return true;

            case R.id.menu_copy_form_data:
                showCopyFormAlert();
                return true;

            case R.id.menu_paste_form_data:
                pasteFormData();
                return true;

            case R.id.menu_search_string:
                toolbar.setVisibility(View.GONE);
                layoutSearchString.setVisibility(View.VISIBLE);

                //delayed intentionally as keyboard wont show up immediately when we set its visibility
                new Handler().postDelayed(()
                        -> {
                    edtSearchString.requestFocus();
                    Util.showKeyboard(LocationDetailActivity.this, edtSearchString);
                }, 100);
                return true;
            default:
                return false;
        }
    }

    @Override
    void createSetOrUpdateDepthField() {
        if (useDepthData) {
            useDepthData = false;

            if (isCreateNewSetForDepth) {
                //means current set has depth value already and we need to create new set with depth update
                isCreateNewSetForDepth = false;
                useDepthData = true;//setting to true again so that when new set is created this
                //function will be called again and else part will run to update depth value
                fab_new_reading.performClick();
            } else {
                MetaData metaData = new MetaData();
                metaData.setCurrentFormID(depthFieldData.getTabId());
                metaData.setMetaParamID(depthFieldId);
                metaData.setMetaInputType("");
                metaData.setCurrentReading(depthFieldData.getDepthValue() + "");//will be used for faster forms

                //update cur set depth field
                if (formMaster != null) {
                    formMaster.saveData_and_updateCreationDate(this, metaData,
                            depthFieldData.getDepthValue() + "", getCurSetID());
                } else if (formsAdapter != null) {
                    formsAdapter.getFormOperations().saveDataAndUpdateCreationDate(metaData);
                }

                tvCalculate.performClick();//refreshing set so that we can see updated depth field
            }
        }
    }

    class AsyncSearchValue extends AsyncTask<Void, Void, ArrayList<Integer>> {

        private String searchValue;

        public AsyncSearchValue(String searchValue) {
            this.searchValue = searchValue;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress();
        }

        @Override
        protected ArrayList<Integer> doInBackground(Void... voids) {
            Collection<MetaData> list = Collections2.filter(formMaster.metaObjects, new Predicate<MetaData>() {
                @Override
                public boolean apply(MetaData metaData) {
                    boolean hasData = false;
                    try {
                        FormMaster.DataHolder tempData = formMaster.getFmapObject()
                                .get(metaData.getMetaParamID() + "");

                        hasData = tempData.value != null && !tempData.value.isEmpty()
                                && tempData.value.trim().toLowerCase()
                                .contains(searchValue.trim().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    boolean isVisible = false;
                    try {
                        isVisible = Objects.requireNonNull(formMaster.getMapMetaObjects()
                                .get(metaData.getMetaParamID())).isRowVisible;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return (metaData.getMetaParamLabel().toLowerCase().trim()
                            .contains(searchValue.trim().toLowerCase()) || hasData) && isVisible;
                }
            });

            ArrayList<MetaData> metaList = Lists.newArrayList(list);

            if (metaList.size() > 0) {
                for (int i = 0; i < locationdetail_master_container.getChildCount(); i++) {
                    View rowView = locationdetail_master_container.getChildAt(i);

                    for (MetaData metaData : metaList) {
                        FormMaster.ViewHolder viewHolder
                                = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();
                        if (rowView != null && rowView.getTag() != null) {
                            try {
                                if (rowView.getTag().equals(viewHolder)) {
                                    searchedValues.add((int) rowView.getY());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            return searchedValues;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> metaYList) {
            super.onPostExecute(metaYList);

            if (metaYList.size() > 0) {
                nested_scroll_view.scrollTo(0, metaYList.get(0));
                tvSearchResultsCount.setText(metaYList.size() + " results");
                ivSearchUp.setVisibility(View.VISIBLE);
                ivSearchDown.setVisibility(View.VISIBLE);
            } else {
                tvSearchResultsCount.setText("0 results");
                ivSearchUp.setVisibility(View.INVISIBLE);
                ivSearchDown.setVisibility(View.INVISIBLE);
            }

            cancelAlertProgress();
        }
    }

    private void searchValueInformData(String searchValue) {
        if (formMaster != null && !searchValue.trim().isEmpty()) {
            searchCount = 0;
            searchedValues.clear();
            new AsyncSearchValue(searchValue).execute();
        } else if (Util.isShowNewForms(this) && !searchValue.trim().isEmpty()) {

        }
    }

    public void clearSearchStringValues(boolean isSetTextEmpty) {
        searchCount = 0;
        if (isSetTextEmpty)
            edtSearchString.setText("");
        searchedValues.clear();
        tvSearchResultsCount.setText("0 results");
        ivSearchUp.setVisibility(View.INVISIBLE);
        ivSearchDown.setVisibility(View.INVISIBLE);
    }

    private void showMissingRequiredFieldsAlert(boolean isNextSetOrForm, boolean isRightForm, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.incomplete_form));
        builder.setMessage(getString(R.string.missing_required_entries_do_you_want_leave));
        builder.setNegativeButton(R.string.proceed, (dialog, which) -> {
            if (isNextSetOrForm) {
                if (isRightForm) {
                    onClickRightForm(view);
                } else {
                    onClickLeftForm(view);
                }
            } else {
                if (last_position > -1) {
                    jumpToChildApp(last_position);
                    last_position = -1;
                } else {
                    finish();
                }
            }
            dialog.dismiss();
        });
        builder.setPositiveButton(getString(R.string.go_back_lower_case), (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showMissingRequiredFieldsAlert(MobileApp app, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.incomplete_form));
        builder.setMessage(getString(R.string.missing_required_entries_do_you_want_leave));
        builder.setNegativeButton(R.string.proceed, (dialog, which) -> {
            if (app != null) {
                navigateToFormClicked(app, position);
                dialog.dismiss();
            } else finish();
        });
        builder.setPositiveButton(getString(R.string.go_back_lower_case), (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCopyFormAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.copy_data));
        builder.setMessage(getString(R.string.copy_entries_for_current_form));
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            FieldDataSource fieldDataSource = new FieldDataSource(LocationDetailActivity.this);
            CopyTemplateModel templateModel =
                    fieldDataSource.copyFieldData(getCurrentAppID() + "",
                            getLocationID(), getEventID());

            if (templateModel.getCopiedData().size() > 0) {

                String fileName = siteName + "_" + currentLocationName + "_"
                        + Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                        GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN_24HR);
                String jsonString = new Gson().toJson(templateModel);

                CopiedFormTemplatesDataSource copyForm = new CopiedFormTemplatesDataSource(this);
                if (copyForm.insertCopyFormTemplate(new CopiedTemplate(fileName, jsonString)))
                    showToast(getString(R.string.form_data_copied), true);
            } else {
                showToast(getString(R.string.no_data_to_copy), true);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void pasteFormData() {

        FieldDataSource fieldDataSource = new FieldDataSource(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_copied_forms_list,
                null, false);

        RecyclerView rvCopiedFiles = view.findViewById(R.id.rvCopiedForms);
        ImageView ivClose = view.findViewById(R.id.ivCloseTemplate);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        rvCopiedFiles.setHasFixedSize(true);

        CopiedFormTemplatesDataSource copyFormSource = new CopiedFormTemplatesDataSource(this);
        ArrayList<CopiedTemplate> listTemplates = copyFormSource.getAllCopiedForms();

        if (listTemplates.size() == 0) {
            showToast(getString(R.string.no_templates_found), true);
            return;
        } else {
            alertDialog.show();
        }

        ivClose.setOnClickListener(v -> alertDialog.dismiss());

        CopiedFormsAdapter adapter = new CopiedFormsAdapter(listTemplates, template -> {
            CopyTemplateModel templateModel = new Gson()
                    .fromJson(template.getCopiedForm(), CopyTemplateModel.class);
            if (templateModel != null) {
                for (CopyTemplateModel.CopyData copyData :
                        templateModel.getCopiedData()) {
                    fieldDataSource.pasteFormData(copyData, getLocationID(), getEventID(),
                            getUserID(), deviceID, siteID);
                }
                alertDialog.dismiss();
                new PopulateLocationDetailsTask().execute(LOAD_FORM);
            }
        });

        rvCopiedFiles.setAdapter(adapter);
    }

    void init() {
        rvForms = findViewById(R.id.rvForms);
/*        rvForms.addItemDecoration(new DividerItemDecoration(this,
                LinearLayout.VERTICAL));*/

        tvNotifyUploadStatus = findViewById(R.id.tvNotifyUploadStatus);
        coordinatorLayout = findViewById(R.id.linearLayout1);
        menuMultipleActions = findViewById(R.id.multiple_actions);
//        action_copydata = (FloatingActionButton) findViewById(R.id.action_copydata);
//        action_undo = (FloatingActionButton) findViewById(R.id.action_undo);
//      menuMultipleActions.setVisibility(View.GONE);

        drawing_ib = findViewById(R.id.drawing_ib);
        gallery_ib = findViewById(R.id.gallery_ib);
        camera_ib = findViewById(R.id.camera_ib);
        syncdata_ib = findViewById(R.id.syncdata_ib);

        sync_tv = findViewById(R.id.syncdata_tv);
        camera_tv = findViewById(R.id.camera_tv);
        gallery_tv = findViewById(R.id.gallery_tv);
        drawing_tv = findViewById(R.id.draw_tv);

        photos_count_container = findViewById(R.id.saved_photos_ll);
        saved_photos_count_tv = findViewById(R.id.saved_photos_count_tv);
        form_loading_bar = findViewById(R.id.loading_pb_vertical);

//        swipe_refresh=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        nested_scroll_view = findViewById(R.id.nestedScrollerid);
        locationdetail_master_container = findViewById(R.id.LocDetailList);

        locationdetail_master_container.requestFocus();
        nested_scroll_view.getParent().requestChildFocus(nested_scroll_view, nested_scroll_view);
        nested_scroll_view.fullScroll(View.FOCUS_UP);
        nested_scroll_view.scrollTo(0, 0);

//        setListView((ListView) findViewById(R.id.LocDetailList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorTracker.registerSensorService();

        if (eventIDChanged != 0 && getEventID() != eventIDChanged) {
            setEventID(eventIDChanged);

            if (formMaster != null)
                formMaster.eventID = eventIDChanged + "";

            //refreshing form
            formMaster.setDataOnChanged(0);

            //loading report frag again as ids have changed
            if (Util.isTablet(this) && isSplitScreenEnabled) {
                mobileReportFragment = new MobileReportFragment();
                loadFragment(mobileReportFragment, R.id.content_split_left_pane);
            }
        }

        if (!locationIDChanged.equals("0") && !locationIDChanged.equals(getLocationID())) {
            setLocID(locationIDChanged);

            if (formMaster != null)
                formMaster.locationID = locationIDChanged;

            //refreshing form
            formMaster.setDataOnChanged(0);

            //loading report frag again as ids have changed
            if (Util.isTablet(this) && isSplitScreenEnabled) {
                mobileReportFragment = new MobileReportFragment();
                loadFragment(mobileReportFragment, R.id.content_split_left_pane);
            }
        }

        badElf = new BadELFGPSTracker(LocationDetailActivity.this);

        Log.i(TAG, "onResume() Refresh-Screen called.");

        // refreshScreen();
        GlobalStrings.CAPTURE_LOG = Boolean.parseBoolean(Util.getSharedPreferencesProperty(appContext, GlobalStrings.IS_CAPTURE_LOG));

        AttachmentDataSource ads = new AttachmentDataSource(appContext);

        int count = ads.getFormAttachmentCount(getSiteID() + "",
                getEventID() + "", getLocID(), getCurrentAppID() + "");
        saved_photos_count_tv.setText(count + "");

        if (formMaster != null)
            if (formMaster.filteredMetaObjects != null) {
                for (MetaData mMeta : formMaster.filteredMetaObjects) {
                    if (mMeta.getMetaInputType().equalsIgnoreCase("PHOTOS")) {
                        formMaster.setDataOnChanged(0);
                        break;
                    }
                }
            }
    }

    @Override
    protected void onPause() {
        mSensorTracker.unregisterSensorService();
        if (badElf != null)
            badElf.disconnectTracker();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (badElf != null)
            badElf.disconnectTracker();
        super.onStop();
    }

    public void appNavigation(View v) {
        try {
            Log.i(TAG, "appNavigation() eventID:" + getEventID());
            FieldDataSource obj = new FieldDataSource(CurrentContext);
            Long millis = System.currentTimeMillis();

            obj.truncateLocFormStatus();
            // 21-Feb-17 Make it as a feature
            obj.insertChildAppStatus(getEventID());

            LayoutInflater mInflater = getLayoutInflater();

            View layout = mInflater.inflate(R.layout.app_menu_drawer, null);

            ListView listView = layout.findViewById(R.id.nav_drawer);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int columnWidth = (dm.widthPixels) / 3;

            popup = new PopupWindow(LocationDetailActivity.this);
            popup.setContentView(layout);
            popup.setWidth(columnWidth * 2);
            popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

            Log.i(TAG, "appNavigation() child App List count:" + childAppList.size());

            AppNavigationDrawer adapter = new AppNavigationDrawer(
                    LocationDetailActivity.this, R.layout.app_drawer_adapter,
                    R.id.app_label, childAppList, getCurrentAppID());
            listView.setAdapter(adapter);

            //20-Feb-17 HANDLED IN ADAPTER ON-CLICK
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dismissPopup(position);
                }
            });

            int loc[] = new int[2];
//            appIcon.getLocationOnScreen(loc);
            int x = loc[0] - 12;
            int y = loc[1] + 38;

            popup.setOutsideTouchable(true);
            popup.setTouchable(true);
            popup.setTouchInterceptor(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // close the Popup when clicking outside
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popup.dismiss();
                    }
                    return false;
                }
            });
            popup.showAtLocation(layout, Gravity.NO_GRAVITY, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //click listener for appNavigation touch
    public void dismissPopup(int position) {
        popup.dismiss();

        if (getCurrentFormNum() != position) {
            jumpToChildApp(position);
        }
    }

    public void jumpToChildApp(int position) {
        reqFieldCount = 0;
        showOneTimeAlert = true;

        try {
            smoothScroller.setTargetPosition(position);
            layoutManagerChildApps.startSmoothScroll(smoothScroller);

            childAppsAdapter.setItemSelected(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCurrentFormNum(position);
        //20-Feb-17 UPDATED APP_ID TO CURRENT_APP_ID
        setCurrentAppID(childAppList.get(position).getAppID());
        Log.i(TAG, "jumpToChildApp() populate form details for mobileAppID=" + getCurrentAppID() + "\n");
        setInvalidating();
        new PopulateLocationDetailsTask().execute(LOAD_FORM);
        resetInvalidating();

        setAppName();

        if (LocationDetailActivity.mobileReportFragment != null
                && Util.isTablet(this) && isSplitScreenEnabled) {
            LocationDetailActivity.mobileReportFragment.setReportDetails();
        }
        currCocID = null;//setting it as null as form is changed and it is handled only for the form where coc is picked
    }

    @Override
    public void onBackPressed() {

        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        } else if (last_position > -1) {
            jumpToChildApp(last_position);
            last_position = -1;
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
            finish();
        }
    }

    private void metaSyncAlert() {
        Log.i(TAG, "metaSyncAlert() IN time:" + System.currentTimeMillis());

//		   SharedPref.putBoolean("RETRACE", true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CurrentContext);

        alertDialogBuilder.setTitle(getString(R.string.changes_to_forms));
        alertDialogBuilder.setMessage(getString(R.string.download_latest_forms));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                if (CheckNetwork.isInternetAvailable(CurrentContext)) {
                    Intent metaIntent = new Intent(CurrentContext, MetaSyncActivity.class);
                    startActivity(metaIntent);
                    finish();
                } else {
                    Toast.makeText(CurrentContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            }


        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == ADD_FORMFIELD && resultCode == RESULT_OK) {
                boolean add = data.getBooleanExtra("Success", false);
                if (add) {
                    if (CheckNetwork.isInternetAvailable(appContext)) {
                        refreshScreen();
                        handleForaddForm();
                    }
                } else {
                    Toast.makeText(appContext, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == SYNC_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
                if (isTablet(this) && isSplitScreenEnabled) {
                    mobileReportFragment = new MobileReportFragment();
                    loadFragment(mobileReportFragment, R.id.content_split_left_pane);
                }
            }
        }
    }

    void SyncData() {
        if (CheckNetwork.isInternetAvailable(appContext)) {
            EventDataSource eventData = new EventDataSource(appContext);

            ArrayList<DEvent> eventList = eventData
                    .getClientGeneratedEventIDs(appContext);

            int count = eventList.size();

            if (count > 0) {
                EventIDGeneratorTask eventHandler =
                        new EventIDGeneratorTask(LocationDetailActivity.this, null, username,
                                password, true);

                eventHandler.execute();
            } else {
                uploadFieldData();
            }
        } else {
            Toast.makeText(appContext,
                    getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
        }
    }

    public void uploadFieldData() {

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(this);

        LocationDataSource LDSource = new LocationDataSource(appContext);
        FieldDataSource fieldSource = new FieldDataSource(appContext);
        AttachmentDataSource attachDataSource = new AttachmentDataSource(appContext);

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        LDSource.checkAndUpdateClientLocationInFieldData();
        LDSource.checkAndUpdateClientLocationInAttachmentData();

        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("EventId: " + getEventID() + ", LocationId: "
                + getLocationID() + ", Site Id: " + getSiteID() + " AppId: " + getCurrentAppID());
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
        logDetails.setScreenName("Form Screen");
        logDetails.setDetails("Has field data before checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());

        tempLogsDataSource.insertTempLogs(logDetails);

        boolean isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();//24-Mar-17
        boolean isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        boolean isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(appContext);

        boolean isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        logDetails.setDetails("Has field data upon checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());
        tempLogsDataSource.insertTempLogs(logDetails);

        logDetails.setDetails("CHECKING DATA TO SYNC - " + " Has locations:" + isLocationsAvailableToSync
                + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync);
        tempLogsDataSource.insertTempLogs(logDetails);

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync
                && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
//            showResyncDataAlert();
            Toast.makeText(appContext, getString(R.string.no_data_to_sync), Toast.LENGTH_LONG).show();
        } else {
            Intent dataUpload = new Intent(appContext, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", getUsername());
            dataUpload.putExtra("PASS", getPassword());
            dataUpload.putExtra("EVENT_ID", getEventID());
            startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
        }
    }

    private void showResyncDataAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.resync_data));
        builder.setMessage(getString(R.string.no_new_data_to_sync));
        builder.setPositiveButton(getString(R.string.resync), (dialog, i) -> {
            dialog.cancel();
            resyncData();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, i) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void resyncData() {
        Intent dataUpload = new Intent(appContext, DataSyncActivity.class);
        dataUpload.putExtra("USER_NAME", getUsername());
        dataUpload.putExtra("PASS", getPassword());
        dataUpload.putExtra("EVENT_ID", getEventID());
        dataUpload.putExtra(GlobalStrings.IS_RESYNC_DATA, true);
        startActivityForResult(dataUpload, SYNC_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onTaskCompleted(Object obj) {
        if (obj != null && obj instanceof String) {

            String result = (String) obj;

            if (result.equals("SUCCESS")) {
                int serverEventID = getEventID();

                if (getEventID() < 0) {
                    serverEventID = new EventDataSource(appContext).getServerEventID(getEventID() + "");
                    setEventID(serverEventID);
                    setGeneratedEventID(serverEventID);
                    LocationActivity.serverGenEventID = serverEventID;
                    LocationActivity.eventID = serverEventID;
                }

                uploadFieldData();
            } else {
                Toast.makeText(appContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(appContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskCompleted() {

    }

    @Override
    public void setGeneratedEventID(int id) {
        setEventID(id);
        LocationActivity.serverGenEventID = id;
        LocationActivity.eventID = id;

    }

    @Override
    public void setGeneratedEventID(Object obj) {

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = this.getCurrentFocus();
            Log.i(TAG, "Touched");

            if (menuMultipleActions.isExpanded()) {

                Rect outRect = new Rect();
                menuMultipleActions.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    menuMultipleActions.collapse();
            }

            try {
                if (v != null) {
                    ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onSheetEventClicked(ChangeEventModel changeEventModel) {

        Util.setSharedPreferencesProperty(this, GlobalStrings.IS_LOCATION_SWITCHED, true);
        Util.setSharedPreferencesProperty(this, GlobalStrings.SWITCHED_APPID, changeEventModel.getMobileAppID());
        Util.setSharedPreferencesProperty(this, GlobalStrings.SWITCHED_SITE_ID, changeEventModel.getSiteID());
        Util.setSharedPreferencesProperty(this, GlobalStrings.SWITCHED_SITENAME, changeEventModel.getSiteName());
        Util.setSharedPreferencesProperty(this, GlobalStrings.SWITCHED_EVENT_ID, changeEventModel.getEventID());

        Util.setSharedPreferencesProperty(this,
                GlobalStrings.CURRENT_APPNAME, changeEventModel.getDisplayName());

        Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_LOCATIONID,
                changeEventModel.getLocationID() + "");
        Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_LOCATIONNAME, changeEventModel.getLocation());
        Util.setSharedPreferencesProperty(this,
                changeEventModel.getEventID() + "", changeEventModel.getLocationID() + "");

        Intent intent = new Intent(this, LocationDetailActivity.class);
        intent.putExtra("EVENT_ID", changeEventModel.getEventID());
        intent.putExtra("LOCATION_ID", changeEventModel.getLocationID() + "");
        intent.putExtra("APP_ID", changeEventModel.getMobileAppID());
        intent.putExtra("SITE_ID", changeEventModel.getSiteID());
        intent.putExtra("SITE_NAME", changeEventModel.getSiteName());
        intent.putExtra("LOCATION_NAME", changeEventModel.getLocation());
        intent.putExtra("APP_NAME", changeEventModel.getDisplayName());
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    /**
     * custom TouchListener
     * note:
     * since I want to return the global coordinate of the touch event, I need to use getRawX(.), not getX(.)
     */
    private class DividerTouchListener implements OnTouchListener {

        @SuppressLint({"ClickableViewAccessibility", "MissingPermission"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                case MotionEvent.ACTION_CANCEL:
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(40);
                    break;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    rebuildView(event.getRawX());
                    break;
            }
            return true;
        }
    }

    /**
     * rebuilds entire view by forcing layout pass on root view
     *
     * @param draggedToX
     */
    private void rebuildView(float draggedToX) {
        // reset weights
        mPercentLeft = computeNewPercentLeft(draggedToX);
        setWeights(mPercentLeft);

        // save to extras
        getIntent().putExtra(EXTRA_PERCENT_LEFT, mPercentLeft);

        // force layout pass
        ViewGroup viewGroup = findViewById(R.id.activity_split_pane);
        viewGroup.requestLayout();
    }

    /**
     * Computes the new percent left based on draggedToX from the touch listener
     */
    private float computeNewPercentLeft(float draggedToX) {
        return (100 - (100 * (mTotalWidth - draggedToX) / mTotalWidth));
    }

    /**
     * converts dip dimension to percentage of the screen
     *
     * @param dip
     * @return
     */
    private float convertDipToPercent(int dip) {
        return (dip / mTotalWidth) * 100;
    }

    /**
     * sets the layout weights of the left and right panes
     *
     * @param percentLeft
     */
    private void setWeights(float percentLeft) {
        Log.d("TAG", "minimum width = " + mMinimumWidth);

        float percentRight = 100 - percentLeft;

        // if left side too small, resize
        if (percentLeft < mMinimumWidth) {
            percentLeft = mMinimumWidth;
            percentRight = 100 - percentLeft;
        }

        // if right side too small, resize
        if (percentRight < mMinimumWidth) {
            percentRight = mMinimumWidth;
            percentLeft = 100 - percentRight;
        }

        // set weights
        mLeftPane.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, percentLeft));
        mRightPane.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, percentRight));
    }
}
