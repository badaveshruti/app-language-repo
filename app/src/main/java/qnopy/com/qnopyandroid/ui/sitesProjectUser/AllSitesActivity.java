package qnopy.com.qnopyandroid.ui.sitesProjectUser;

import static androidx.core.content.FileProvider.getUriForFile;
import static qnopy.com.qnopyandroid.ui.activity.MainDrawerActivity.copyDataBase;
import static qnopy.com.qnopyandroid.util.Util.delete_All_Log;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.NotificationsDataSource;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.db.TempLogsDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.gps.BadELFGPSTracker;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.CSVDataModel;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity;
import qnopy.com.qnopyandroid.ui.activity.DataSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.HelpActivity;
import qnopy.com.qnopyandroid.ui.activity.MapForSiteActivity;
import qnopy.com.qnopyandroid.ui.activity.MetaSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.NotificationActivity;
import qnopy.com.qnopyandroid.ui.activity.SiteActivity;
import qnopy.com.qnopyandroid.ui.fragment.SiteFragment;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.BadgeDrawerArrowDrawable;
import qnopy.com.qnopyandroid.uiutils.BadgeDrawerToggle;
import qnopy.com.qnopyandroid.uiutils.EventIDGeneratorTask;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.uiutils.SendDBTask;
import qnopy.com.qnopyandroid.util.CSVUtil;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class AllSitesActivity extends ProgressDialogActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SiteFragment.OnSiteClickListener, OnTaskCompleted {

    private ActionBar actionbar;
    private String username;
    private String userID;
    private DrawerLayout drawer;
    private BadgeDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Menu navigationMenu;
    private FrameLayout redCircle;
    private TextView counter_view;
    private BadgeDrawerArrowDrawable badgeDrawable;
    private CheckBox checkView;
    private CheckBox backg_sync_checkview;
    private CheckBox splitScreenCheckbox;
    private int REQUEST_CODE_COPY_DB = 148;
    private boolean isForceDownload;
    private AquaBlueServiceImpl mAquaBlueService;
    private String password;
    private String companyID;
    private BadELFGPSTracker badElf;
    private MyBroadCastReceiver myBroadCastReceiver;
    private CheckBox fasterFormsCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sites);
        myBroadCastReceiver = new MyBroadCastReceiver();

        String userAppType = Util.getSharedPreferencesProperty(this, GlobalStrings.USERAPPTYPE);

        if (userAppType != null) {
            ScreenReso.isLimitedUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_LIMITED);
            ScreenReso.isProjectUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_PROJECT);
            ScreenReso.isCalendarUser = userAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_CALENDAR);
        }

        setToolbar();

        username = Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME);
        userID = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID);
        mAquaBlueService = new AquaBlueServiceImpl(this);
        password = Util.getSharedPreferencesProperty(this, GlobalStrings.PASSWORD);
        companyID = Util.getSharedPreferencesProperty(this, GlobalStrings.COMPANYID);

        setNavigationDrawer();

        setSplitScreenCheckbox();

        setFasterFormsCheckbox();

        setCaptureSignatureAndBgService();

        UserDataSource userDataSource = new UserDataSource(this);
        int userRole = userDataSource.getUserRole(username);

        if (userRole == 1 || userRole == 2 || userRole == 5) {
            MenuItem itemDashBoard = navigationMenu.findItem(R.id.nav_reset_app);
            itemDashBoard.setVisible(true);
        }

        loadFragment(new SiteFragment());
    }

    private void setFasterFormsCheckbox() {

        boolean isFasterFormsEnabled = Util.isShowNewForms(this);

        MenuItem checkItem = navigationMenu.findItem(R.id.menu_faster_forms);
//        String title = getString(R.string.faster_forms);
        String title = "New Faster Forms<font color='#F44336'><i> (beta)</i></font>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            checkItem.setTitle(Html.fromHtml(title,
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            checkItem.setTitle(Html.fromHtml(title));
        }

        fasterFormsCheckbox = (CheckBox) checkItem.getActionView();
        fasterFormsCheckbox.setChecked(isFasterFormsEnabled);

        fasterFormsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Util.setSharedPreferencesProperty(AllSitesActivity.this, GlobalStrings.IS_SHOW_FASTER_FORMS,
                        isChecked);
            }
        });
    }

    /**
     * MyBroadCastReceiver is responsible to receive broadCast from register action
     */
    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Log.d(TAG, "onReceive() called");
                Toast.makeText(context, "Notification Received.", Toast.LENGTH_LONG).show();
                initializeCountDrawer();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerMyReceiver();
        initializeCountDrawer();

        SharedPreferences.Editor editor = getSharedPreferences("BADELFGPS", MODE_PRIVATE).edit();
        editor.clear().apply();

        badElf = new BadELFGPSTracker(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBroadCastReceiver);
        // make sure to unregister your receiver after finishing of this activity
        badElf.disconnectTracker();
    }

    @Override
    protected void onStop() {
        badElf.disconnectTracker();
        super.onStop();
    }

    /**
     * This method is responsible to register an action to BroadCastReceiver
     */
    private void registerMyReceiver() {

        try {
            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(GlobalStrings.BROADCAST_ACTION);
            registerReceiver(myBroadCastReceiver, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setSplitScreenCheckbox() {
        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        MenuItem checkItem = navigationMenu.findItem(R.id.menu_enable_split_screen);

        if (!Util.isTablet(this))
            checkItem.setVisible(false);

        splitScreenCheckbox = (CheckBox) checkItem.getActionView();
        splitScreenCheckbox.setChecked(isSplitScreenEnabled);

        splitScreenCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Util.setSharedPreferencesProperty(AllSitesActivity.this,
                        GlobalStrings.ENABLE_SPLIT_SCREEN, isChecked);
            }
        });
    }

    private void setCaptureSignatureAndBgService() {

        String capture = Util.getSharedPreferencesProperty(this,
                GlobalStrings.CAPTURE_SIGNATURE);
        String bg_service = Util.getSharedPreferencesProperty(this,
                GlobalStrings.BG_SERVICE);

        boolean CAPTURE, BG_SERVICE;
        if (capture == null) {
            CAPTURE = false;
        } else {
            CAPTURE = Boolean.parseBoolean(capture);
        }

        if (bg_service == null) {
            BG_SERVICE = true;
        } else {
            BG_SERVICE = Boolean.parseBoolean(bg_service);
        }

        Util.setSharedPreferencesProperty(this, GlobalStrings.CAPTURE_SIGNATURE,
                String.valueOf(CAPTURE));


        MenuItem checkItem = navigationMenu.findItem(R.id.enable_signature);
        checkView = (CheckBox) checkItem.getActionView();
        checkView.setChecked(CAPTURE);

        checkView.setOnCheckedChangeListener((compoundButton, isChecked) ->
                Util.setSharedPreferencesProperty(AllSitesActivity.this,
                        GlobalStrings.CAPTURE_SIGNATURE, String.valueOf(isChecked)));

        checkItem = navigationMenu.findItem(R.id.enable_background_sync_service);

        backg_sync_checkview = (CheckBox) checkItem.getActionView();
        backg_sync_checkview.setChecked(BG_SERVICE);
        backg_sync_checkview.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Util.setSharedPreferencesProperty(AllSitesActivity.this,
                    GlobalStrings.BG_SERVICE, String.valueOf(isChecked));
            if (isChecked) {
                Util.scheduleAlarm(getApplicationContext());
            } else {
                Util.stopAlarm(getApplicationContext());
            }
        });
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Projects");

        actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setElevation(0);
        }
    }

    private void setNavigationDrawer() {
        drawer = findViewById(R.id.drawer_layout_main);

        toggle = new BadgeDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        badgeDrawable = new BadgeDrawerArrowDrawable(actionbar.getThemedContext());
        toggle.setDrawerArrowDrawable(badgeDrawable);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navViewSites);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.header_user_name);
        nav_user.setText(username);

        navigationMenu = navigationView.getMenu();
        navigationMenu.findItem(R.id.nav_app_version).setTitle(getString(R.string.version)
                + Util.getAppVersionName(this));

        MenuItem navItem = navigationMenu.findItem(R.id.nav_notification);
        FrameLayout rootView = (FrameLayout) navItem.getActionView();

        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        counter_view = rootView.findViewById(R.id.view_alert_count_textview);

        initializeCountDrawer();

        navigationView.setNavigationItemSelectedListener(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                if (GlobalStrings.mDeviceConnectedStatusFlag == 1) {
                    navigationView.getMenu().findItem(R.id.nav_badelfcnnectionstatus).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_badelfcnnectionstatus)
                            .setTitle("" + GlobalStrings.mDeviceConnectedName + " CONNECTED.");
                }
                if (GlobalStrings.mDeviceConnectedStatusFlag == 0) {
                    navigationView.getMenu().findItem(R.id.nav_badelfcnnectionstatus).setVisible(false);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        int userrole = 0;
        UserDataSource userDataSource = new UserDataSource(this);

        String u_role = Util.getSharedPreferencesProperty(this, GlobalStrings.USERROLE);
        if (u_role != null) {
            if (!u_role.isEmpty()) {
                userrole = Integer.parseInt(u_role);
            }
        } else {
            int userrole1 = userDataSource.getUserRolefromID(Integer.parseInt(userID));
            if (userrole1 != 0) {
                Util.setSharedPreferencesProperty(this, GlobalStrings.USERROLE, String.valueOf(userrole1));
                userrole = userrole1;
            }
        }

        if (userrole != 0) {
            navigationMenu.findItem(R.id.assign_project)
                    .setVisible(userrole == GlobalStrings.SUPER_ADMIN
                            || userrole == GlobalStrings.CLIENT_ADMIN ||
                            userrole == GlobalStrings.PROJECT_MANAGER);
        } else {
            navigationMenu.findItem(R.id.assign_project).setVisible(false);
        }

        navigationMenu.findItem(R.id.enable_background_sync_service)
                .setVisible(Build.VERSION.SDK_INT < Build.VERSION_CODES.O);

        if (!BuildConfig.DEBUG) {
            navigationMenu.findItem(R.id.nav_copy_db).setVisible(false);
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.load_fragment, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    private void initializeCountDrawer() {
        if (counter_view != null) {
            int[] count = new NotificationsDataSource(this)
                    .getNotificationCount(Integer.parseInt(userID));

            if (count[1] > 0) {
                redCircle.setVisibility(View.VISIBLE);
                counter_view.setText(count[1] > 99 ? "99+" : count[1] + "");
                toggle.setBadgeText(count[1] > 99 ? "99+" : count[1] + "");
                badgeDrawable.setEnabled(true);

                runAnimation();
            } else {
                redCircle.setVisibility(View.GONE);
                badgeDrawable.setEnabled(false);
            }
        }
    }

    private void runAnimation() {
        Animation alert_anim = AnimationUtils.loadAnimation(this, R.anim.notification_alert);
        alert_anim.reset();
        redCircle.clearAnimation();
        redCircle.startAnimation(alert_anim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COPY_DB) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                assert uri != null;
                try {
                    copyDataBase(uri, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                syncTasks();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_notification:
                Intent notificationIntent = new Intent(this, NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.nav_hospital:
                if (CheckNetwork.isInternetAvailable(this)) {
                    Intent mapIntent = new Intent(this, MapForSiteActivity.class);
                    mapIntent.putExtra("PREV_CONTEXT", "LocationDetail");
                    mapIntent.putExtra("OPERATION", "nearby");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_reset_app:
                FieldDataSource fd = new FieldDataSource(this);
                AttachmentDataSource attachDataSource = new AttachmentDataSource(this);

                if (fd.isFieldDataAvailableToSync() || attachDataSource.attachmentsAvailableToSync()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.warning))
                            .setMessage(R.string.there_is_unsync_data)
                            .setPositiveButton(R.string.erase, (dialog, which) -> alertForDeletingData())
                            .setNegativeButton(R.string.cancel_upper_case, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    alertForDeletingData();
                }
                break;

            case R.id.nav_update_app:
                //21-Mar-16  Link to Update app from Play Store
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.aqua.fieldbuddy"));
                startActivity(intent);
                break;

            case R.id.assign_project:
                Intent intent2 = new Intent(this, SiteActivity.class);
                intent2.putExtra("FromAssignProject", true);
                startActivity(intent2);
                break;

            case R.id.enable_signature:

                boolean CAPTURE = !checkView.isChecked();
                Util.setSharedPreferencesProperty(this, GlobalStrings.CAPTURE_SIGNATURE, String.valueOf(CAPTURE));
                checkView.setChecked(CAPTURE);
                break;

            case R.id.menu_enable_split_screen:

                boolean isSplitScreenEnabled = !splitScreenCheckbox.isChecked();
                Util.setSharedPreferencesProperty(this, GlobalStrings.ENABLE_SPLIT_SCREEN, isSplitScreenEnabled);
                splitScreenCheckbox.setChecked(isSplitScreenEnabled);
                break;

            case R.id.enable_background_sync_service:

                boolean BG_SERVICE = !backg_sync_checkview.isChecked();
                Util.setSharedPreferencesProperty(this, GlobalStrings.BG_SERVICE, String.valueOf(BG_SERVICE));
                backg_sync_checkview.setChecked(BG_SERVICE);
                break;

            case R.id.nav_copy_db:
                Intent intentChooser = new Intent(Intent.ACTION_GET_CONTENT);
                intentChooser.setType("*/*");
                intentChooser.addCategory(Intent.CATEGORY_DEFAULT);

                try {
                    startActivityForResult(
                            Intent.createChooser(intentChooser, getString(R.string.select_db_file_to_copy)),
                            REQUEST_CODE_COPY_DB);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(this, getString(R.string.install_file_mgr),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_download_forms:
                isForceDownload = true;
                downloadForms();
                break;

            case R.id.nav_contact_support:
                showContactSupportAlert();
                break;

            case R.id.nav_signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.sign_out))
                        .setMessage(getString(R.string.are_you_sure_to_sign_out))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAllData();
//                                Util.setLogout(MainDrawerActivity.this);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null);
                AlertDialog dia = builder.create();
                dia.show();
                break;

            case R.id.nav_send_db:
                if (CheckNetwork.isInternetAvailable(this)) {
                    new SendDBTask(this).execute();
                } else {
                    CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), 10);
                }
                break;

            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;

            case R.id.export_all_data_csv:
                FieldDataSource fieldDataSource = new FieldDataSource(this);
                ArrayList<CSVDataModel> list = fieldDataSource.getDataForCSV("");

                if (list.size() > 0) {
                    String csvData = CSVUtil.toCSV(list, ',', true);
                    exportCSVFile(csvData);
                } else {
                    CustomToast.showToast(this, getString(R.string.no_data_to_export), Toast.LENGTH_SHORT);
                }

                break;

            case R.id.export_today_csv_data:
                FieldDataSource fieldDataSource1 = new FieldDataSource(this);
                ArrayList<CSVDataModel> listTodaysData = fieldDataSource1.
                        getDataForCSV(Util.getFormattedDate(System.currentTimeMillis()));

                if (listTodaysData.size() > 0) {
                    String csvData = CSVUtil.toCSV(listTodaysData, ',', true);
                    exportCSVFile(csvData);
                } else {
                    CustomToast.showToast(this, getString(R.string.no_data_to_export), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.menu_faster_forms:

                boolean isFasterForms = !fasterFormsCheckbox.isChecked();
                Util.setSharedPreferencesProperty(AllSitesActivity.this,
                        GlobalStrings.IS_SHOW_FASTER_FORMS, isFasterForms);
                fasterFormsCheckbox.setChecked(isFasterForms);
                break;
            default:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void showContactSupportAlert() {
        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle(R.string.contact_support);
        builder.setMessage("\n" + getString(R.string.email) + ":" + GlobalStrings.SUPPORT_EMAIL
                + "\n" + getString(R.string.phone) + ":" + GlobalStrings.SUPPORT_PHONE);
        boolean isTabletSize = getResources().getBoolean(R.bool.isTablet);

        if (!isTabletSize) {
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

            builder.setPositiveButton(getString(R.string.call), (dialog, which) -> {
                dialog.cancel();

                String mobileNumber = GlobalStrings.SUPPORT_PHONE;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                intent.setData(Uri.parse("tel: " + mobileNumber)); // Data with intent respective action on intent
                startActivity(intent);
            });
        } else {
            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.cancel());
        }

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void exportCSVFile(String csvData) {
        try {
            String fileName = "qnopy_sheet_" + System.currentTimeMillis() + ".csv";
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(csvData.getBytes());
            outputStream.close();

            File fileLocation = new File(getFilesDir(), fileName);
            Uri contentUri = getUriForFile(getApplicationContext(), "com.aqua.fieldbuddy.provider", fileLocation);

            Intent csvIntent = new Intent(Intent.ACTION_VIEW);
            csvIntent.setDataAndType(contentUri, "text/csv");
            csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            csvIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager manager = getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(csvIntent, 0);
            if (infos.size() > 0) {
                startActivity(Intent.createChooser(csvIntent, getString(R.string.choose_app_to_open)));
            } else {
                CustomToast.showToast(this,
                        getString(R.string.you_may_not_have_proper_app), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void alertForDeletingData() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_erase_data,
                null, false);
        alertDialogBuilder.setView(view);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        TextView btnErase = view.findViewById(R.id.tvErase);
        EditText edtErase = view.findViewById(R.id.edtErase);

        edtErase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equalsIgnoreCase("Erase")) {
                    btnErase.setVisibility(View.VISIBLE);
                } else {
                    btnErase.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnErase.setOnClickListener(v -> {
            deleteAllData();
            alertDialog.cancel();
        });
    }

    public void deleteAllData() {
        MetaDataSource md = new MetaDataSource(this);
        md.resetAppData();
        Util.setSharedPreferencesProperty(this, GlobalStrings.IS_CAPTURE_LOG, String.valueOf(false));
        Util.setSharedPreferencesProperty(this, GlobalStrings.ENABLE_SPLIT_SCREEN, false);

        //Setting boolean as false in case if person has login and logout to some
        //other user without closing app
        ScreenReso.isLimitedUser = false;
        ScreenReso.isCalendarUser = false;
        ScreenReso.isProjectUser = false;

        delete_All_Log();
        Util.setLogout(this);
    }

    public void downloadForms() {

        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
            return;
        }

        //1. Check any offline events, if there is any create events and update event ids then
        //2. Upload event data, coc data, location data in case there is any then
        //3. Check any task data to upload task data.
        EventDataSource eventDbSource = new EventDataSource(this);
        ArrayList<DEvent> eventList = eventDbSource
                .getClientGeneratedEventIDs(this);

        if (eventList.size() > 0) {
            new EventIDGeneratorTask(this, null,
                    username, password, true, this).execute();
        } else {
            //checking if any field data to upload then call download forms and later events will
            //be fetched as we'll be clearing tables to let submittals fragment know that it
            //should download events
            //then sync tasks
            uploadFieldData();
        }
    }

    private void callMetaSync() {
        Intent metaIntent = new Intent(getApplicationContext(), MetaSyncActivity.class);
        metaIntent.putExtra(GlobalStrings.FROM_DASHBOARD, isForceDownload);
        isForceDownload = false;//reset
        startActivity(metaIntent);
//        finish();
    }

    public void uploadFieldData() {

        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(this);

        LocationDataSource LDSource = new LocationDataSource(this);
        FieldDataSource fieldSource = new FieldDataSource(this);
        AttachmentDataSource attachDataSource = new AttachmentDataSource(this);

        //12-May-17 CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        LDSource.checkAndUpdateClientLocationInFieldData();
        LDSource.checkAndUpdateClientLocationInAttachmentData();

        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));
        logDetails.setScreenName("Event Dashboard Screen");
        logDetails.setDetails("Has field data before checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());

        tempLogsDataSource.insertTempLogs(logDetails);

        boolean isLocationsAvailableToSync = LDSource.isOfflineLocationsAvailable();
        boolean isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        boolean isAttachmentsAvailableToSync = attachDataSource.attachmentsAvailableToSync();
        CocMasterDataSource cocDataSource = new CocMasterDataSource(this);

        boolean isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        logDetails.setDetails("Has field data upon checking old strings? Rows: " + fieldSource.collectDataForSyncUpload().size());
        tempLogsDataSource.insertTempLogs(logDetails);

        logDetails.setDetails("CHECKING DATA TO SYNC - " + " Has locations:" + isLocationsAvailableToSync
                + " Has COC: " + isCoCAvailableToSync + " Has field Data: "
                + isFieldDataAvailableToSync + " Has attachments: " + isAttachmentsAvailableToSync);
        tempLogsDataSource.insertTempLogs(logDetails);

        if (!isLocationsAvailableToSync && !isCoCAvailableToSync && !isFieldDataAvailableToSync && !isAttachmentsAvailableToSync) {
            syncTasks();
        } else {

            Intent dataUpload = new Intent(this, DataSyncActivity.class);
            dataUpload.putExtra("USER_NAME", username);
            dataUpload.putExtra("PASS", password);
            dataUpload.putExtra("EVENT_ID", 0); //this id is used to close
            //the event which we don't require here
            startActivityForResult(dataUpload, BaseMenuActivity.SYNC_ACTIVITY_REQUEST_CODE);
        }
    }

    private void syncTasks() {

        if (!CheckNetwork.isInternetAvailable(this)) {
            CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), Toast.LENGTH_SHORT);
            return;
        }

        final TaskDataResponse.Data taskDataRequest = new TaskDataResponse.Data();

        final TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(this);
        final TaskCommentsDataSource commentsDataSource = new TaskCommentsDataSource(this);
        TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(this);
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments("");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks("");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments("");

        if (commentList.size() == 0 && dataList.size() == 0 && attachmentList.size() == 0) {
            callMetaSync();
            return;
        }

        taskDataRequest.setTaskDataList(dataList);
        taskDataRequest.setCommentList(commentList);

        String baseUrl = this.getString(R.string.prod_base_uri)
                + this.getString(R.string.prod_user_task_sync_data);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(taskDataRequest));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showAlertProgress();
        updateAlertProgressMsg(getString(R.string.syncing_tasks_please_wait));
//        showProgressDialog(getString(R.string.syncing_tasks_please_wait));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, baseUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                TaskDataResponse syncRes = new Gson().fromJson(response.toString(), TaskDataResponse.class);

                if (syncRes.getData().getCommentList().size() > 0) {
                    for (TaskDataResponse.CommentList comment : syncRes.getData().getCommentList()) {
                        commentsDataSource.updateIdAndSyncFlag(comment.getTaskCommentId() + "",
                                comment.getTaskId() + "",
                                comment.getClientCommentId() + "");
                    }
                }

                if (syncRes.getData().getTaskDataList().size() > 0) {
                    for (TaskDataResponse.TaskDataList details : syncRes.getData().getTaskDataList()) {
                        taskDetailsDataSource.updateSyncFlagAndId(details.getTaskId() + "",
                                details.getClientTaskId() + "");
                        attachmentsDataSource.updateTaskId(details.getTaskId() + "",
                                details.getClientTaskId() + "");
                    }
                }

                ArrayList<TaskDataResponse.AttachmentList> attachmentList
                        = attachmentsDataSource.getAllUnSyncAttachments("");

                if (attachmentList.size() > 0) {
                    syncTaskAttachments(attachmentList);
                } else {
//                    dismissProgressDialog();
                    cancelAlertProgress();
                    callMetaSync();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                dismissProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                DeviceInfoModel ob = DeviceInfo.getDeviceInfo(AllSitesActivity.this);
                String deviceToken = Util.getSharedPreferencesProperty(AllSitesActivity.this,
                        GlobalStrings.NOTIFICATION_REGISTRATION_ID);
                String uID = Util.getSharedPreferencesProperty(AllSitesActivity.this,
                        GlobalStrings.USERID);

                Map<String, String> paramsHeader = new HashMap<String, String>();
                paramsHeader.put("user_guid", ob.getUser_guid());
                paramsHeader.put("device_id", ob.getDeviceId());
                paramsHeader.put("user_id", uID);
                paramsHeader.put("device_token", deviceToken);
                paramsHeader.put("Content-Type", "application/json");
                return paramsHeader;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonObjectRequest);
    }

    private void syncTaskAttachments(ArrayList<TaskDataResponse.AttachmentList> list) {

        for (TaskDataResponse.AttachmentList attachment : list) {
            File imagePath = new File(attachment.getFileName());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new Gson().toJson(attachment));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SyncMedia(this, jsonObject, imagePath.getAbsolutePath(), list.size()).execute();
        }
    }

    int countMediaSync = 0;

    private class SyncMedia extends AsyncTask<MediaType, Void, String> {

        //        File mFile;
        MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
        AttachmentTaskResponseModel resultModel = null;
        Context mContext;
        JSONObject mJsonObjectMediaData;
        String mPath;
        int mMediaCount;

        SyncMedia(Context context, JSONObject jsonObjectMediaData, String path, int mediaCount) {
            mContext = context;
            mJsonObjectMediaData = jsonObjectMediaData;
            mPath = path;
            mMediaCount = mediaCount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(MediaType... mediaTypes) {
            String response = null;

            File file = new File(mPath);
            try {
                if (file.exists()) {
                    files.add("files", new FileSystemResource(file));
                }
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
            files.add("media", mJsonObjectMediaData.toString());

            resultModel = mAquaBlueService.TaskMediaUpload(getResources().getString(R.string.prod_base_uri),
                    getResources().getString(R.string.prod_user_task_attachment_sync),
                    files);
            if (resultModel != null) {
                if (resultModel.isSuccess()) {
                    response = "SUCCESS";
                } else {
                    response = "FALSE";
                }
            } else {
                Log.e("imageUpload", "doInBackground: fails to upload image attachment");
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                countMediaSync++;
                if (s.equals("FALSE")) {
                    Log.e("imageUpload", "onPostExecute: fails to upload image attachment");
                } else if (s.equals("SUCCESS")) {
                    Log.e("imageUpload", "onPostExecute: image attachment upload success" + resultModel.getData().getTaskId());

                    TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(mContext);
                    attachmentsDataSource.updateDataSyncFlag(resultModel.getData().getTaskId(),
                            resultModel.getData().getFileName(),
                            resultModel.getData().getClientTaskAttachmentId() == null
                                    ? resultModel.getData().getTaskAttachmentId()
                                    : resultModel.getData().getClientTaskAttachmentId(),
                            resultModel.getData().getTaskAttachmentId());
                }
            }

            if (mMediaCount == countMediaSync) {
                cancelAlertProgress();
                callMetaSync();
            }
        }
    }

    @Override
    public void onTaskCompleted(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                String result = (String) obj;
                if (result.equals("SUCCESS")) {
                    uploadFieldData();
                } else {
                    Toast.makeText(this, getString(R.string.unable_to_connect_to_server),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onTaskCompleted() {
        //no use
    }

    @Override
    public void setGeneratedEventID(int id) {
        //no use
    }

    @Override
    public void setGeneratedEventID(Object obj) {
        //no use
    }

    @Override
    public void onSiteClicked(Site item) {
//        Intent intent = new Intent(this, MainDrawerActivity.class);
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.putExtra(GlobalStrings.KEY_SITE_ID, item.getSiteID() + "");
        intent.putExtra(GlobalStrings.KEY_SITE_NAME, item.getSiteName());
        startActivity(intent);
    }
}