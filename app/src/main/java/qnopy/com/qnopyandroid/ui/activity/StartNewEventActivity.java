package qnopy.com.qnopyandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.adapter.ActiveEventsAdapter;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FormSitesDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uiutils.DividerItemDecoration;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

@AndroidEntryPoint
public class StartNewEventActivity extends ProgressDialogActivity
        implements ActiveEventsAdapter.OnEventItemClickListener {

    private RecyclerView rvActiveEvents;
    private ArrayList<DEvent> eventList = new ArrayList<>();
    private ActiveEventsAdapter adapter;
    private int appId;
    private int siteId;
    private String siteName;
    private int userId;
    private TextView btnStartNewEvent;
    private String username;
    private String guid;
    private final String TAG = "StartEventActivity";
    private ProgressDialog progressDialog;
    private String formName;

    @Inject
    SiteDataSource siteDataSource;
    @Inject
    FormSitesDataSource formSitesDataSource;
    @Inject
    LocationDataSource locationDataSource;
    @Inject
    MobileAppDataSource mobileAppDataSource;
    private boolean isSiteTypeDemo;
    private boolean isAppTypeNoLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_new_event);
        username = Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME);
        guid = Util.getSharedPreferencesProperty(this, username);
        setUpUi();
    }

    private void setUpUi() {

        if (getIntent() != null) {
            appId = getIntent().getIntExtra(GlobalStrings.CURRENT_APPID, 0);
            siteId = getIntent().getIntExtra(GlobalStrings.CURRENT_SITEID, 0);
            siteName = getIntent().getStringExtra(GlobalStrings.CURRENT_SITENAME);
            userId = getIntent().getIntExtra(GlobalStrings.USERID, 0);
            formName = getIntent().getStringExtra(GlobalStrings.FORM_NAME);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(siteName);

        rvActiveEvents = findViewById(R.id.rvActiveEvents);
        btnStartNewEvent = findViewById(R.id.tvStartNewEvent);

        isSiteTypeDemo = siteDataSource.isSiteTypeDemo(siteId);
        isAppTypeNoLoc = formSitesDataSource.isAppTypeNoLoc(appId + "", siteId + "");

        if (ScreenReso.isLimitedUser || isSiteTypeDemo)
            btnStartNewEvent.setVisibility(View.GONE);

        btnStartNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartNewEventActivity.this,
                        CreateNewEventActivity.class);
                intent.putExtra(GlobalStrings.CURRENT_APPID, appId);
                intent.putExtra(GlobalStrings.CURRENT_SITEID, siteId);
                intent.putExtra(GlobalStrings.FORM_NAME, formName);
                intent.putExtra(GlobalStrings.CURRENT_SITENAME, siteName);
                intent.putExtra(GlobalStrings.USERID, userId);
                startActivity(intent);
            }
        });

        rvActiveEvents.setLayoutManager(new LinearLayoutManager(this));
        rvActiveEvents.setHasFixedSize(true);
        rvActiveEvents.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        rvActiveEvents.setItemAnimator(new DefaultItemAnimator());

        EventDataSource eventDataSource = new EventDataSource(this);
        eventList.addAll(eventDataSource.getSiteEvents(appId, siteId));
        adapter = new ActiveEventsAdapter(eventList, this, this);
        rvActiveEvents.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEventItemClicked(DEvent event) {

        if (isAppTypeNoLoc) {
            showFormDetailsScreen(event);
            return;
        }


        Intent locationIntent = new Intent(this, LocationActivity.class);

        Util.setSharedPreferencesProperty(StartNewEventActivity.this,
                GlobalStrings.CURRENT_SITEID, siteId);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(this) && isSplitScreenEnabled)
            locationIntent = new Intent(this, SplitLocationAndMapActivity.class);

        locationIntent.putExtra("APP_ID", event.getMobileAppId());
        locationIntent.putExtra("EVENT_ID", event.getEventId());
        locationIntent.putExtra("fromaddsite", false);

        if (SiteActivity.siteActivity != null)
            SiteActivity.siteActivity.finish();

        if (ApplicationActivity.applicationActivity != null)
            ApplicationActivity.applicationActivity.finish();

        startActivity(locationIntent);
        finish();
    }

    //20/10/22 if the siteType is no_loc then the event will show the default location form directly w\o location screen
    //the default location taken is currently in lowest id and very first
    private void showFormDetailsScreen(DEvent event) {
        var serverEventId = event.getEventId();

        ArrayList<Location> locations =
                locationDataSource.getDefaultLocation(siteId, appId);

        if (locations.size() >= 1) {
            Location location = locations.get(0);
            String locName = location.getLocationName();
            String locationId = location.getLocationID();
            String deviceId = DeviceInfo.getDeviceID(this);
            String userId = "0";

            try {
                userId = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HomeFragment", "Error in parsing Shared preferences for userID: " + e.getMessage());
                UserDataSource userData = new UserDataSource(this);
                String username =
                        Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME);

                User newUser = userData.getUser(username);
                if (newUser != null) {
                    userId = newUser.getUserID() + "";
                }
            }

            if (serverEventId < 0) {
                serverEventId =
                        new EventDataSource(this).getServerEventID(serverEventId.toString());
            }
            String displayAppName = new SiteMobileAppDataSource(this)
                    .getMobileAppDisplayNameRollIntoApp(appId, siteId);
            Util.setSharedPreferencesProperty(
                    this,
                    GlobalStrings.CURRENT_APPNAME,
                    displayAppName
            );
            Util.setSharedPreferencesProperty(
                    this,
                    GlobalStrings.CURRENT_LOCATIONID,
                    locationId
            );
            Util.setSharedPreferencesProperty(
                    this,
                    GlobalStrings.CURRENT_LOCATIONNAME,
                    locName
            );
            Util.setSharedPreferencesProperty(
                    this,
                    GlobalStrings.SESSION_USERID,
                    userId
            );
            Util.setSharedPreferencesProperty(
                    this,
                    GlobalStrings.SESSION_DEVICEID,
                    deviceId
            );
            List<MobileApp> childAppList =
                    mobileAppDataSource.getChildApps(event.getMobileAppId(), event.getSiteId(), locationId);
            int maxApps = childAppList.size();
            if (maxApps == 0) {
                Toast.makeText(
                        this,
                        getString(R.string.no_forms_for_this_location), Toast.LENGTH_SHORT
                ).show();
                return;
            }
            Intent locationDetailIntent = new Intent(
                    this,
                    LocationDetailActivity.class
            );
            locationDetailIntent.putExtra("EVENT_ID", serverEventId);
            locationDetailIntent.putExtra("LOCATION_ID", locationId);
            locationDetailIntent.putExtra("APP_ID", event.getMobileAppId());
            locationDetailIntent.putExtra("SITE_ID", event.getSiteId());
            locationDetailIntent.putExtra(
                    "SITE_NAME",
                    siteDataSource.getSiteNamefromID(event.getSiteId())
            );
            locationDetailIntent.putExtra("APP_NAME", displayAppName);
            locationDetailIntent.putExtra("COC_ID", "0");
            locationDetailIntent.putExtra("LOCATION_NAME", locName);

            if (location.getLocationDesc() == null) locationDetailIntent.putExtra(
                    "LOCATION_DESC", "");
            else
                locationDetailIntent.putExtra(
                        "LOCATION_DESC", location.getLocationDesc());

            locationDetailIntent.putExtra(GlobalStrings.FORM_DEFAULT, location.getFormDefault());

            try {
                startActivity(locationDetailIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(
                        "HomeFragment",
                        "no_loc site event creation Error in Redirecting to Details Form:" + e.getMessage()
                );
                Toast.makeText(
                        this,
                        getString(R.string.unable_to_connect_to_server),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
