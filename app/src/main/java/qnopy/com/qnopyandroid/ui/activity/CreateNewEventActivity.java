package qnopy.com.qnopyandroid.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
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
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.EventResponseData;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.locations.LocationActivity;
import qnopy.com.qnopyandroid.ui.splitLocationAndMap.SplitLocationAndMapActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

@AndroidEntryPoint
public class CreateNewEventActivity extends ProgressDialogActivity
        implements View.OnClickListener {

    private TextView tvStartDate;
    private Calendar date;
    private TextView tvEndDate;
    private String username;
    private String guid;
    private int appId;
    private int siteId;
    private String siteName;
    private int userId;
    private final String TAG = "CreateEventActivity";
    private EditText edtEventName;
    private long startDateMillis = 0;
    private long endDateMillis = 0;
    private String formName;
    private long eventStartDate = 0L;
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;

    @Inject
    SiteDataSource siteDataSource;
    @Inject
    LocationDataSource locationDataSource;
    @Inject
    MobileAppDataSource mobileAppSource;
    @Inject
    FormSitesDataSource formSitesDataSource;
    private boolean isAppTypeNoLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        username = Util.getSharedPreferencesProperty(this, GlobalStrings.USERNAME);
        guid = Util.getSharedPreferencesProperty(this, username);
        setUpUi();

        GlobalStrings.currentContext = this;

        boolean mGpsPermissionStatus = checkWriteExternalPermission();
        Log.e("gpsPermissionStatus", "onCreate: " + mGpsPermissionStatus);

        try {
            if (mGpsPermissionStatus) {
                mLatitude = GlobalStrings.CURRENT_GPS_LOCATION.getLatitude();
                mLongitude = GlobalStrings.CURRENT_GPS_LOCATION.getLongitude();
            } else {
                mLatitude = 0.00;
                mLongitude = 0.00;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLatitude = 0.00;
            mLongitude = 0.00;
        }
    }

    private void setUpUi() {

        if (getIntent() != null) {
            appId = getIntent().getIntExtra(GlobalStrings.CURRENT_APPID, 0);
            siteId = getIntent().getIntExtra(GlobalStrings.CURRENT_SITEID, 0);
            siteName = getIntent().getStringExtra(GlobalStrings.CURRENT_SITENAME);
            formName = getIntent().getStringExtra(GlobalStrings.FORM_NAME);
            userId = getIntent().getIntExtra(GlobalStrings.USERID, 0);
            eventStartDate = getIntent().getLongExtra(GlobalStrings.EVENT_STAR_DATE, 0);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.start_new_form));

        isAppTypeNoLoc = formSitesDataSource.isAppTypeNoLoc(appId + "", siteId + "");

        TextView tvSiteName = findViewById(R.id.tvSiteName);
        tvSiteName.setText(getString(R.string.site) + " " + siteName);
        TextView tvFormName = findViewById(R.id.tvFormName);
        tvFormName.setText(getString(R.string.form) + " " + formName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvSelectEndDate);
        edtEventName = findViewById(R.id.edtEventName);
        TextView btnCreateEvent = findViewById(R.id.tvCreateEvent);

        if (eventStartDate > 0) {
            tvStartDate.setText(Util.getFormattedDateFromMilliS(eventStartDate,
                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN_12HR));
            startDateMillis = eventStartDate;
        }

        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnCreateEvent.setOnClickListener(this);
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void showDateTimePicker(final TextView tvDate) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(CreateNewEventActivity.this,
                        (view1, hourOfDay, minute) -> {
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            date.set(Calendar.MINUTE, minute);

                            if (tvDate == tvStartDate)
                                startDateMillis = date.getTimeInMillis();
                            else
                                endDateMillis = date.getTimeInMillis();

                            SimpleDateFormat sdf
                                    = new SimpleDateFormat(GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN_12HR,
                                    Locale.US);
                            tvDate.setText(sdf.format(date.getTime()));
                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE),
                        false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DATE)).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvStartDate:
                showDateTimePicker(tvStartDate);
                break;
            case R.id.tvSelectEndDate:
                showDateTimePicker(tvEndDate);
                break;
            case R.id.tvCreateEvent:
                if (tvStartDate.getText().toString().isEmpty()) {
                    CustomToast.showToast(this, getString(R.string.please_select_start_date), Toast.LENGTH_SHORT);
                    return;
                } else if (!tvEndDate.getText().toString().isEmpty()) {
                    if (startDateMillis > endDateMillis) {
                        CustomToast.showToast(this, getString(R.string.please_enter_valid_date), Toast.LENGTH_SHORT);
                        return;
                    }
                }

                if (CheckNetwork.isInternetAvailable(this)) {
                    startEvent();
                } else {
                    showAlertProgress();
                    updateAlertProgressMsg(getString(R.string.creating_event));

                    int eventId = createOfflineEvent();

                    cancelAlertProgress();
                    startLocationAct(appId, eventId);
                }
                break;
        }
    }

    private void showStartDateAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.info));
        builder.setMessage(R.string.you_are_starting_a_new_field_event_provide_start_date);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startEvent() {
        DEvent event = new DEvent();
        event.setSiteId(siteId);
        event.setMobileAppId(appId);
        event.setUserId(userId);
        event.setDeviceId(DeviceInfo.getDeviceID(this));
        event.setUserName(username);

        String eventName = edtEventName.getText().toString();
        if (eventName.isEmpty())
            event.setEventName(null);
        else
            event.setEventName(eventName);

        event.setEventDate(startDateMillis);
        event.setEventStartDate(startDateMillis);
        event.setEventEndDate(endDateMillis);

        AsyncEventCreate eventHandler = new AsyncEventCreate(event, this);
        eventHandler.execute();
    }

    public int createOfflineEvent() {
        String generatedBy = "C";
        boolean isEventIdExists = false;
        EventDataSource eventData = new EventDataSource(this);

        int eventID = 0;
        long currntTime = System.currentTimeMillis();
        Log.i(TAG, "genClientEventID() Event Timestamp :" + currntTime);
        eventID = (int) currntTime;
        Log.i(TAG, "genClientEventID() Client Event :" + eventID);

        isEventIdExists = eventData.isEventIdExists(eventID);
        while (isEventIdExists) {
            eventID = (int) System.currentTimeMillis();
            isEventIdExists = eventData.isEventIdExists(eventID);
        }

        if (eventID > 0) {
            eventID = -(eventID);
        }
        System.out.println("event offline " + eventID);

        eventData.insertEventId(eventID, generatedBy, appId, siteId,
                userId, mLatitude, mLongitude, DeviceInfo.getDeviceID(this),
                startDateMillis, endDateMillis, edtEventName.getText().toString());
        return eventID;
    }

    class AsyncEventCreate extends AsyncTask<Void, Void, EventResponseModel> {

        private final DEvent event;
        private final AquaBlueServiceImpl mAquaBlueService;
        private Context mContext;

        AsyncEventCreate(DEvent event, Context context) {
            this.event = event;
            this.mContext = context;
            this.mAquaBlueService = new AquaBlueServiceImpl(mContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            startProgressDialog();
            showAlertProgress();
            updateAlertProgressMsg(getString(R.string.creating_event));
        }

        @Override
        protected EventResponseModel doInBackground(Void... voids) {

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("deviceId", DeviceInfo.getDeviceID(mContext));
                jsonObject.put("siteId", event.getSiteId());
                jsonObject.put("mobileAppId", event.getMobileAppId());
                jsonObject.put("userGuid", guid);
                jsonObject.put("userId", event.getUserId());
                jsonObject.put("eventDate", event.getEventDate());
                jsonObject.put("eventStartDate", event.getEventStartDate());
                jsonObject.put("createEventFlag", 1);
                jsonObject.put("latitude", mLatitude);
                jsonObject.put("longitude", mLongitude);
                jsonObject.put("eventName", event.getEventName());
                jsonObject.put("eventEndDate", event.getEventDate());

            } catch (JSONException e1) {
                e1.printStackTrace();
                Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
                return null;
            }

            return mAquaBlueService.generateEventIDFromServer(mContext.getResources()
                            .getString(R.string.prod_base_uri),
                    mContext.getResources().getString(R.string.prod_event_create), event,
                    username,
                    jsonObject);
        }

        @Override
        protected void onPostExecute(EventResponseModel eventResponse) {
            super.onPostExecute(eventResponse);
//            dismissEventProgressDialog();
            cancelAlertProgress();
            if (eventResponse != null) {
                if (eventResponse.isSuccess()) {
                    insertEventDataToDb(eventResponse.getData());
                } else {
                    CustomToast.showToast(CreateNewEventActivity.this,
                            getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                }
            }
        }
    }

    private void insertEventDataToDb(EventResponseData data) {

        DEvent event = new DEvent();
        event.setEventId(data.getEventId());
        event.setMobileAppId(data.getMobileAppId());
        event.setSiteId(data.getSiteId());
        event.setUserId(data.getCreatedBy());
        event.setCreatedBy(data.getCreatedBy());
        event.setLatitude(data.getLatitude());
        event.setLongitude(data.getLongitude());
        event.setDeviceId(data.getDeviceId());
        event.setEventDate(data.getEventDate());
        event.setEventStartDate(data.getEventStartDate());
        event.setEventEndDate(data.getEventEndDate());
        event.setEventName(data.getEventName());
        event.setEventUserName(data.getEventUserName());

        String generatedBy = "S";
        EventDataSource eventData = new EventDataSource(this);
        eventData.insertEventData(event, generatedBy);

        startLocationAct(event.getMobileAppId(), event.getEventId());
    }

    private void startLocationAct(int appId, int eventId) {

        if (isAppTypeNoLoc) {
            showFormDetailsScreen(eventId);
            return;
        }

        Util.setSharedPreferencesProperty(
                this,
                GlobalStrings.CURRENT_SITEID, siteId + ""
        );

        Intent locationIntent = new Intent(this, LocationActivity.class);

        boolean isSplitScreenEnabled = Util.getSharedPrefBoolProperty(this,
                GlobalStrings.ENABLE_SPLIT_SCREEN);

        if (Util.isTablet(this) && isSplitScreenEnabled)
            locationIntent = new Intent(this, SplitLocationAndMapActivity.class);

        locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        locationIntent.putExtra("APP_ID", appId);
        locationIntent.putExtra("EVENT_ID", eventId);
        locationIntent.putExtra("fromaddsite", false);
        locationIntent.putExtra(GlobalStrings.IS_FROM_CREATE_EVENT_SCREEN, true);
        startActivity(locationIntent);

        if (SiteActivity.siteActivity != null)
            SiteActivity.siteActivity.finish();

        if (ApplicationActivity.applicationActivity != null)
            ApplicationActivity.applicationActivity.finish();

        finish();
    }

    //20/10/22 if the siteType is no_loc then the event will show the default location form directly w\o location screen
    //the default location taken is currently in lowest id and very first
    private void showFormDetailsScreen(int eventId) {

        int serverEventId = eventId;

        ArrayList<Location> locations = locationDataSource.getDefaultLocation(siteId, appId);

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
                Log.e(TAG, "Error in parsing Shared preferences for userID:" + e.getMessage());

                UserDataSource userData = new UserDataSource(this);
                User newUser = userData.getUser(username);
                if (newUser != null) {
                    userId = newUser.getUserID() + "";
                }
            }

            if (eventId < 0) {
                serverEventId = new EventDataSource(this).getServerEventID(eventId + "");
            }

            String dispAppName = new SiteMobileAppDataSource(this)
                    .getMobileAppDisplayNameRollIntoApp(appId, siteId);

            Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_APPNAME, dispAppName);

            Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_LOCATIONID, locationId);
            Util.setSharedPreferencesProperty(this, GlobalStrings.CURRENT_LOCATIONNAME, locName);
            Util.setSharedPreferencesProperty(this, GlobalStrings.SESSION_USERID, userId);
            Util.setSharedPreferencesProperty(this, GlobalStrings.SESSION_DEVICEID, deviceId);

            List<MobileApp> childAppList = mobileAppSource.getChildApps(appId, siteId, locationId);

            int maxApps = childAppList.size();

            if (maxApps == 0) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.no_forms_for_this_location), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent locationDetailIntent = new Intent(this,
                    LocationDetailActivity.class);

            locationDetailIntent.putExtra("EVENT_ID", serverEventId);
            locationDetailIntent.putExtra("LOCATION_ID", locationId);
            locationDetailIntent.putExtra("APP_ID", appId);
            locationDetailIntent.putExtra("SITE_ID", siteId);
            locationDetailIntent.putExtra("SITE_NAME", siteName);
            locationDetailIntent.putExtra("APP_NAME", dispAppName);
            String cocId = null;
            locationDetailIntent.putExtra("COC_ID", cocId);

            locationDetailIntent.putExtra("LOCATION_NAME", locName);
            locationDetailIntent.putExtra("LOCATION_DESC", location.getLocationDesc() == null ? "" : location.getLocationDesc());
            locationDetailIntent.putExtra(GlobalStrings.FORM_DEFAULT, location.getFormDefault());

            try {
                startActivity(locationDetailIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "no_loc site event creation Error in Redirecting to Details Form:" + e.getMessage());
                Toast.makeText(this, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
