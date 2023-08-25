package qnopy.com.qnopyandroid.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.db.CreationOfTable;
import qnopy.com.qnopyandroid.db.DbAccess;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.PreferenceMappingModel;
import qnopy.com.qnopyandroid.responsemodel.VerifyEmailResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.services.BatteryStatusReceiver;
import qnopy.com.qnopyandroid.services.MyFirebaseMessagingService;
import qnopy.com.qnopyandroid.ui.login.LoginActivity;
import qnopy.com.qnopyandroid.ui.sitesProjectUser.AllSitesActivity;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Saurabh on 08-Dec-15.
 */

public class SplashScreenActivity extends ProgressDialogActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    Context context;
    Thread mSplashWait;
    ImageView iv3;
    // verifyemail verifyemail = new verifyemail();
    String result = null;

    TextView versiontxt;
    Animation scaleupanim;
    Animation scaledownanim;
    private AnimationDrawable loadingViewAnim = null;
    private int updateFlag = 0;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    BatteryStatusReceiver batteryStatusReceiver = new BatteryStatusReceiver();

    String username, pass, compID, userGuid, deviceID, userrole, userAppType, userId;
    List<PreferenceMappingModel> prefList = null;
    ProgressDialog procDialog;
    VerifyEmailResponseModel mRetEmailVerify = null;
    ProgressBar progressbar;
    public static Handler mHandler;
    Button s, login;

    public static Activity mSplashScreenActivity;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

/*        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/
        context = this;
        GlobalStrings.currentContext = this;
        mSplashScreenActivity = this;

        GlobalStrings.responseMessage = getString(R.string.unauthorized_user_account);

        FirebaseApp.initializeApp(context);
        //16-05-2018 REGISTER APP FOR PUSH NOTIFICATIONS

        versiontxt = findViewById(R.id.versiontextView);
        versiontxt.setText("v " + Util.getAppVersionName(context));
        iv3 = findViewById(R.id.imageView3);

        //07-Aug-16

        ((ScreenReso) getApplication()).getScreenReso(context);

        //09-Dec-15 Grow and Shrink animation
        scaleupanim = AnimationUtils.loadAnimation(context, R.anim.fadein);
        scaledownanim = AnimationUtils.loadAnimation(context, R.anim.fadeout);

        scaleupanim.setDuration(1500);
        scaledownanim.setDuration(1500);

        iv3.setAnimation(scaleupanim);
        scaleupanim.start();

        scaleupanim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv3.setAnimation(scaledownanim);
                scaledownanim.start();
            }
        });

        scaledownanim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv3.setAnimation(scaleupanim);
                scaleupanim.start();
            }
        });

        new Thread(() -> MyFirebaseMessagingService
                .generateFireBaseToken(SplashScreenActivity.this)).start();

        // 20-Jan-16
        initializeDatabaseSchema(context);

        //9-May-17  UPDATE NEW DEVICE-ID
        updateDeviceID(context);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NotNull Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
                progressbar.setVisibility(View.GONE);
                s.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
            }
        };

        if (Util.isAlreadyLogin(context)) {

            //11-Sep-17 START ALARM SERVICE
            Util.scheduleAlarm(context);
            try {
                username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
                userGuid = Util.getSharedPreferencesProperty(context, username);
                pass = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
                compID = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID);
                // userrole= Util.getSharedPreferencesProperty(context, GlobalStrings.USERROLE);
                userAppType = Util.getSharedPreferencesProperty(context, GlobalStrings.USERAPPTYPE);
                userId = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);

                if (userAppType != null) {
                    ScreenReso.isLimitedUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_LIMITED);
                    ScreenReso.isProjectUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_PROJECT);
                    ScreenReso.isCalendarUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_CALENDAR)
                            || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_MOBILE_2_POINT_0);
                }

                if (userAppType == null
                        || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_LIMITED)
                        || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_STANDARD)
                        || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_CALENDAR)
                        || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_PROJECT)
                        || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_MOBILE_2_POINT_0)) {
                    UserDataSource userDataSource = new UserDataSource(context);
                    userAppType = userDataSource.getUserAppType(userId);
                    Util.setSharedPreferencesProperty(context, GlobalStrings.USERAPPTYPE, userAppType);
                    Log.e("UserAppType", "onCreate: within splash screen activity" + userAppType);
                }

                deviceID = DeviceInfo.getDeviceID(context);
                Log.e("Qnopy", "DeviceId: " + deviceID);

                Util.setSharedPreferencesProperty(context, GlobalStrings.IS_SESSION_ACTIVE, "true");

                if (compID == null || compID.isEmpty()) {
                    UserDataSource ob = new UserDataSource(context);
                    compID = ob.getUserCompanyID(username, pass);
                    Log.i("alreadyLogin", "User CompanyID:" + compID);
                    Util.setSharedPreferencesProperty(context, GlobalStrings.COMPANYID, compID);

                    if (userAppType == null || userAppType.equals("")
                            || userAppType.equals("writer") || userAppType.equals("qNopy")
                            || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_LIMITED)
                            || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_STANDARD)
                            || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_CALENDAR)
                            || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_PROJECT)
                            || userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_MOBILE_2_POINT_0)) {
                        //initializeDatabaseSchema(context);
                        Intent metaIntent = new Intent(getApplicationContext(), MetaSyncActivity.class);
                        startActivity(metaIntent);
                        finish();
                    }
                } else {
                    Log.i(TAG, "Slow Internet Connection.So,App Update Skipped");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            invokeDashboard();
                        }
                    }, 4000);
                    //19-Jun-17
                    // downloadAppPreferences(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in Database Initiation :" + e.getMessage());
            }
        } else {
            //removed on 15 sep, 22 - no welcome screen onwards have kept code for future in case we need it again or remove it totally if sir says
/*            boolean isFirstTime = (Util.getSharedPreferencesProperty(context, GlobalStrings.IS_FIRST_TIME_LAUNCH) == null);
            if (isFirstTime) {
                launchWelcomeScreen();
            } else {
                launchLoginScreen();
            }*/
            launchLoginScreen();
        }
    }

    private void launchWelcomeScreen() {
        Util.setSharedPreferencesProperty(context, GlobalStrings.IS_FIRST_TIME_LAUNCH, "false");
        startActivity(new Intent(context, WelcomeActivity.class));
        finish();
    }

    private void launchLoginScreen() {

        String createproject = Util.getSharedPreferencesProperty(context, "CreateProjectActivity");
        String activation = Util.getSharedPreferencesProperty(context, "Activation_Code");
        String rollappForm = Util.getSharedPreferencesProperty(context, "RollAppFormActivity");

        // verifyemail verifyemail=new verifyemail();
        // verifyemail.setUserMailId(username);

        if (activation != null && activation.equals("active")) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Do network action in this function

                    if (userGuid != null) {
                        if (CheckNetwork.isInternetAvailable(context)) {
                            mRetEmailVerify = mAquaBlueService.verifyemailstatus(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_verify_email),
                                    userGuid);

                            if (mRetEmailVerify != null) {
                                if (mRetEmailVerify.isSuccess()) {
                                    Util.setSharedPreferencesProperty(context, "Activation_Code", "");
                                    Intent intent = new Intent(context, MetaSyncActivity.class);
                                    intent.putExtra("fromactivation", true);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    GlobalStrings.responseMessage = mRetEmailVerify.getMessage();
                                    if (mRetEmailVerify.getResponseCode() == HttpStatus.LOCKED) {
                                        result = HttpStatus.LOCKED.toString();
                                    }
                                    if (mRetEmailVerify.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                                        result = HttpStatus.NOT_ACCEPTABLE.toString();
                                    } else {
                                        //Intent intent = new Intent(context, RegistrationActivity.class);
                                        //intent.putExtra("Username",username);
                                        //intent.putExtra("Verified","notverified");
                                        //intent.putExtra("Pending", "inactive");
                                        //startActivity(intent);
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                LayoutInflater factory = LayoutInflater.from(context);
                                                final View DialogView = factory.inflate(R.layout.verifyemailalert, null);
                                                final androidx.appcompat.app.AlertDialog Dialog
                                                        = new androidx.appcompat.app.AlertDialog.Builder(context).create();
                                                Dialog.setCanceledOnTouchOutside(false);
                                                Dialog.setView(DialogView);
                                                final Button s = DialogView.findViewById(R.id.ok);
                                                final Button login = DialogView.findViewById(R.id.btnlogin);
                                                s.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
                                                        String userGuid = Util.getSharedPreferencesProperty(context, username);

                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                            finishAffinity();
                                                        }

                                                    }
                                                });
                                                Dialog.show();

                                                login.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent
                                                                = new Intent(SplashScreenActivity.this,
                                                                LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                        });

                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Intent intent = new Intent(context, RegistrationActivity.class);
                        intent.putExtra("Username", username);
                        //  intent.putExtra("Verified", "notverified");
                        //intent.putExtra("Pending", "inactive");
                        startActivity(intent);
                    }
                }
            }).start();

//            Intent intent=new Intent(context, RegistrationActivity.class);
//            intent.putExtra("Username",username);
//            intent.putExtra("Password",pass);
//            intent.putExtra("Pending","inactive");
//            startActivity(intent);
        } else if (rollappForm != null && rollappForm.equals("active")) {
            Intent intent = new Intent(context, RollAppFormActivity.class);
            intent.putExtra("Pending", "inactive");
            startActivity(intent);

        } else if (createproject != null && createproject.equals("active")) {
            Intent intent = new Intent(context, RollAppFormActivity.class);
            intent.putExtra("Pending", "inactive");
            startActivity(intent);
        } else {
            startActivity(new Intent(context, LoginActivity.class));
        }
//        overridePendingTransition(R.anim.right_to_left,
//                R.anim.left_to_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void invokeDashboard() {
        //initializeDatabaseSchema(context);
        String createproject = Util.getSharedPreferencesProperty(context, "CreateProjectActivity");
        String activation = Util.getSharedPreferencesProperty(context, "Activation_Code");
        String rollappForm = Util.getSharedPreferencesProperty(context, "RollAppFormActivity");
        //   verifyemail.setUserMailId(username);
        if (activation != null && activation.equals("active")) {

            // Do network action in this function
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Do network action in this function

                    if (userGuid != null) {
                        if (CheckNetwork.isInternetAvailable(context)) {
                            mRetEmailVerify = mAquaBlueService.verifyemailstatus(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_verify_email),
                                    userGuid);

                            if (mRetEmailVerify != null) {
                                if (mRetEmailVerify.isSuccess()) {

                                    Util.setSharedPreferencesProperty(context, "Activation_Code", "");
                                    Intent intent = new Intent(context, MetaSyncActivity.class);
                                    intent.putExtra("fromactivation", true);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    GlobalStrings.responseMessage = mRetEmailVerify.getMessage();
                                    if (mRetEmailVerify.getResponseCode() == HttpStatus.LOCKED) {
                                        result = HttpStatus.LOCKED.toString();
                                    }
                                    if (mRetEmailVerify.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                                        result = HttpStatus.NOT_ACCEPTABLE.toString();
                                    } else {
                                        //Intent intent = new Intent(context, RegistrationActivity.class);
                                        //intent.putExtra("Username",username);
                                        //intent.putExtra("Verified","notverified");
                                        //intent.putExtra("Pending", "inactive");
                                        //startActivity(intent);
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                LayoutInflater factory = LayoutInflater.from(context);
                                                final View DialogView = factory.inflate(R.layout.verifyemailalert, null);
                                                final androidx.appcompat.app.AlertDialog Dialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
                                                Dialog.setCanceledOnTouchOutside(false);
                                                Dialog.setView(DialogView);
                                                s = DialogView.findViewById(R.id.ok);
                                                progressbar = DialogView.findViewById(R.id.progressbar);
                                                login = DialogView.findViewById(R.id.btnlogin);

                                                s.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        progressbar.setVisibility(View.VISIBLE);
                                                        s.setVisibility(View.GONE);
                                                        login.setVisibility(View.GONE);
                                                        String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
                                                        userGuid = Util.getSharedPreferencesProperty(context, username);

                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                // Do network action in this function

                                                                if (userGuid != null) {
                                                                    if (CheckNetwork.isInternetAvailable(context)) {
                                                                        mRetEmailVerify = mAquaBlueService.verifyemailstatus(getResources().getString(R.string.prod_base_uri),
                                                                                getResources().getString(R.string.prod_verify_email),
                                                                                userGuid);

                                                                        if (mRetEmailVerify != null) {
                                                                            if (mRetEmailVerify.isSuccess()) {
                                                                                Dialog.dismiss();
                                                                                Util.setSharedPreferencesProperty(context, "Activation_Code", "");
                                                                                Intent intent = new Intent(context, MetaSyncActivity.class);
                                                                                intent.putExtra("fromactivation", true);
                                                                                startActivity(intent);
                                                                                finish();

                                                                            } else {
                                                                                Util.msgHandler("", mHandler);
                                                                                GlobalStrings.responseMessage = mRetEmailVerify.getMessage();
                                                                                if (mRetEmailVerify.getResponseCode() == HttpStatus.LOCKED) {
                                                                                    result = HttpStatus.LOCKED.toString();
                                                                                }
                                                                                if (mRetEmailVerify.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                                                                                    result = HttpStatus.NOT_ACCEPTABLE.toString();
                                                                                } else {
                                                                                    Dialog.show();
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            }
                                                        }).start();
                                                    }
                                                });

                                                login.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                                        Util.setSharedPreferencesProperty(context, "Activation_Code", "");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                                Dialog.show();
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Intent intent = new Intent(context, RegistrationActivity.class);
                        intent.putExtra("Username", username);
                        //  intent.putExtra("Verified", "notverified");
                        //intent.putExtra("Pending", "inactive");
                        startActivity(intent);
                    }
                }
            }).start();

        } else if (rollappForm != null && rollappForm.equals("active")) {
            Intent intent = new Intent(context, RollAppFormActivity.class);
            intent.putExtra("Pending", "inactive");
            startActivity(intent);

        } else if (createproject != null && createproject.equals("active")) {
            Intent intent = new Intent(context, RollAppFormActivity.class);
            intent.putExtra("Pending", "inactive");
            startActivity(intent);
        } else {

//            Intent applicationIntent = new Intent(context, MainDrawerActivity.class);
            Intent applicationIntent = new Intent(context, HomeScreenActivity.class);

            if (ScreenReso.isProjectUser || ScreenReso.isLimitedUser)
                applicationIntent = new Intent(context, AllSitesActivity.class);

            startActivity(applicationIntent);
            finish();
        }
//        overridePendingTransition(R.anim.right_to_left,
//                R.anim.left_to_right);
        // finish();

    }

    private void updateDeviceID(Context context) {
        String deviceID = DeviceInfo.getDeviceID(context);
        if (deviceID == null || deviceID.isEmpty()) {
            deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.DEVICEID);
        }

        Log.i(TAG, "updateDeviceID() deviceID to Update:" + deviceID);
        new FieldDataSource(context).updateFieldDataDeviceID(deviceID);
        new EventDataSource(context).updateEventDeviceID(deviceID);
    }

    private boolean initializeDatabaseSchema(Context context) {

        try {
            DbAccess.getInstance(context);
            //  DbAccess.getInstance(context);
            DbAccess.getInstance(context).open();

            CreationOfTable schema = new CreationOfTable(context);

            schema.createTableSiteMobileApp();

            //20-Jan-16
            schema.createTable_s_project_folder_IfNotExist();
            schema.createTable_s_LocationFormPercentage_IfNotExist();
            schema.createTable_s_project_file_IfNotExist();
            schema.createTable_s_file_permission_IfNotExist();
            schema.createTable_temp_project_file_IfNotExist();
            schema.createTable_sync_status_IfNotExist();
            schema.createTable_d_fielddata_conflict_IfNotExist();
            schema.createTable_d_field_data_temp_IfNotExist();
            schema.createTable_d_SampleMapTag_IfNotExist();
            schema.createTable_s_DefaultValues_IfNotExist();

            //7/3/2018
            schema.createTable_s_LocationAttribute_IfNotExist();

            //29-03-2018
            schema.createTable_cm_coc_details_IfNotExist();
            schema.createTable_cm_methods_IfNotExist();
            schema.createTable_cm_coc_master_IfNotExist();

            //17-Jun-17
            schema.createTable_s_AppPreferences_IfNotExist();
            schema.createTable_task_attributes_IfNotExist();

//            schema.createTableEvents();
            schema.createTable_temp_project_folder_IfNotExist();
            schema.alterTable_s_project_file("download_status", "BOOLEAN", "FALSE");
            schema.alterTable_s_project_file("folder_id", "INT (11)", null);
            schema.alterTable_temp_project_file();
            schema.alterTable_metaSync();
            schema.alterTable_s_Location();
            schema.alterTable_d_fielddata();
            schema.alterTable_d_fielddata_temp();
            schema.alterTable_s_MobileApp();
            schema.alterTable_s_EventTable();
            schema.alterTable_s_Default_Values();
            schema.alterTable_s_sitemobileapp();
            schema.alterTable_s_SiteUserRole();
            schema.alterTable_d_Attachment();
            schema.rename_s_user();
            schema.alterTable_s_User();
            schema.alterTable_s_Site();

            //31-Jul-17 WORK_ORDER ALTERATION
            if (schema.isTableExists(DbAccess.TABLE_WORK_ORDER_TASK_OLD)) {
                //if (schema.renameTable(DbAccess.TABLE_WORK_ORDER_TASK_OLD, DbAccess.TABLE_WORK_ORDER_TASK_TEMP)) {
                schema.createTable_s_work_order_task_IfNotExist();
                schema.moveOld_work_order_data();
//                } else
//                    Log.e(TAG, "initializeDatabaseSchema() unable to rename table:" + DbAccess.TABLE_WORK_ORDER_TASK_OLD);
            } else {
                schema.createTable_s_work_order_task_IfNotExist();
            }

            //28-Feb-17

            schema.createTableLocFormStatus_IfNotExist();

            //23-05-2018 NOTIFICATION TABLE
            schema.createTable_notifications_IfNotExist();
            schema.alterTable_notifications();
            String[] indexTables = {"LocFormStatus", "d_FieldData", "s_MetaData", "s_SiteMobileApp"};
            //  String[] indexNames = {"indx_LocFormStatus", "indx_d_FieldData", "indx_s_MetaData", "indx_s_SiteMobileApp"};
            for (String table : indexTables) {
                switch (table) {
                    case "LocFormStatus":
                        schema.createIndex_IfNotExist("indx_LocFormStatus", "LocationID", table);
                        break;
                    case "d_FieldData":
                        schema.createIndex_IfNotExist("indx_d_FieldData", "LocationID,EventID,MobileAppID,ExtField1", table);
                        break;
                    case "s_MetaData":
                        schema.createIndex_IfNotExist("indx_s_MetaData", "MobileAppID", table);
                        break;
                    case "s_SiteMobileApp":
                        schema.createIndex_IfNotExist("indx_s_SiteMobileApp", "SiteID,roll_into_app_id,MobileAppID", table);
                        break;
                }
            }

            //CONSTRUCTION APP DATABASE
            schema.createTable_SimpleNoteData_Timestamp_IfNotExist();
            schema.createTable_PostData_IfNotExist();
            schema.createTable_MediaData_IfNotExist();
            schema.createTable_CTagData_IfNotExist();
            schema.createTable_RTagData_IfNotExist();
            //schema.createTable_Construction_AttachmentMedia_IfNotExist();

            //TASK LIST DATABASE

            schema.createTableTaskDataListIFNotExists();
            schema.createTableTaskAttachmentListIfNotExists();
            schema.createTableTaskCommentListIFNotExists();

            schema.createTableTaskDetailsIfNotExist();
            schema.alterTaskTableColumns();
            schema.createTableTaskAttachmentsIfNotExist();
            schema.createTableTaskCommentsIfNotExist();
            schema.alterTaskCommentsTableColumns();
            schema.createTableTaskUsersIfNotExist();

            schema.createTableMetaDataAttributesIFNotExists();

            schema.createTableLocationProfilePicIfNotExists();
            schema.createTableCopiedFormTemplateIfNotExist();
            schema.createTableTempLogs();
            schema.createTableDownloadDataSyncStatus();
            schema.createTableEventLocations();
            schema.createTableSiteFormFields();
            schema.createTableFormSites();//added on 30 March, 22
            schema.alterTableAddNewCols();
            schema.addColumns();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Database Initiation:" + e.getMessage());
            return false;
        }
        return true;
    }
}
