package qnopy.com.qnopyandroid.ui.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.responsemodel.WeatherResponse;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class WeatherActivity extends ProgressDialogActivity implements
        AlertManagerWeather.OnWeatherResponseListener {

    private static final int DRAWABLE_RIGHT = 2;
    private static final int DRAWABLE_LEFT = 0;
    private WeatherResponse forecastResponse = null;
    private WeatherResponse hourlyForecastResponse;
    private EditText edtSearch;
    private ConstraintLayout layoutWeatherData;
    private RecyclerView rvWeatherData;
    private TextView tvLocationName;
    private TextView tvDate;
    private TextView tvCurrentCondition;
    private TextView tvTimeSunrise;
    private TextView tvTimeSunset;
    private TextView tvTempHigh;
    private TextView tvTempLow;
    private String userID;
    private String deviceID;
    private ArrayList<MetaData> metaDataList;
    private int setId;
    private String eventId;
    private String locationId;
    private String siteID;
    private TextView btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Get Weather");
        userID = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID);
        deviceID = Util.getSharedPreferencesProperty(this, GlobalStrings.SESSION_DEVICEID);

        if (getIntent() != null) {
            metaDataList = getIntent().getParcelableArrayListExtra(GlobalStrings.KEY_META_DATA);
            setId = getIntent().getIntExtra(GlobalStrings.KEY_SET_ID, 0);
            eventId = getIntent().getStringExtra(GlobalStrings.KEY_EVENT_ID);
            locationId = getIntent().getStringExtra(GlobalStrings.KEY_LOCATION_ID);
            siteID = getIntent().getStringExtra(GlobalStrings.KEY_SITE_ID);
        }

        setUpUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpUI() {
        edtSearch = findViewById(R.id.edtSearchLoc);
        btnGo = findViewById(R.id.btnGo);
        layoutWeatherData = findViewById(R.id.layoutWeather);
        rvWeatherData = findViewById(R.id.rvForecastHourly);
        rvWeatherData.setLayoutManager(new LinearLayoutManager(this));
        tvLocationName = findViewById(R.id.tvLocationName);
        tvDate = findViewById(R.id.tvDate);
        tvCurrentCondition = findViewById(R.id.tvCurrentCondition);
        tvTimeSunrise = findViewById(R.id.tvTimeSunrise);
        tvTimeSunset = findViewById(R.id.tvTimeSunset);
        tvTempHigh = findViewById(R.id.tvTempHigh);
        tvTempLow = findViewById(R.id.tvTempLow);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnGo.setBackground(ContextCompat.getDrawable(this, R.drawable.ripple_effect_white_circle));
        }

        btnGo.setOnClickListener(v -> getWeatherDetails());

        edtSearch.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() <=
                        (edtSearch.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    //on location button click
                    if (GlobalStrings.CURRENT_GPS_LOCATION != null) {
                        String address = getAddress(GlobalStrings.CURRENT_GPS_LOCATION.getLatitude(),
                                GlobalStrings.CURRENT_GPS_LOCATION.getLongitude());
                        edtSearch.setText(address);
                        getWeatherDetails();
                    }
                }
            }
            return false;
        });

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getWeatherDetails();
                return true;
            }
            return false;
        });
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getSubLocality() != null)
                    result.append(address.getSubLocality()).append(", ");
                if (address.getLocality() != null)
                    result.append(address.getLocality()).append(", ");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return result.toString();
    }

    private void getWeatherDetails() {
        Util.hideKeyboard(this);
        if (CheckNetwork.isInternetAvailable(WeatherActivity.this)) {
            if (!edtSearch.getText().toString().trim().isEmpty()) {
                AlertManagerWeather.getWeatherForecastDetails(edtSearch.getText().toString().trim(),
                        WeatherActivity.this,
                        WeatherActivity.this);
            }
        } else {
            Toast.makeText(WeatherActivity.this, "No internet connection.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException | IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return null;
        }

        return p1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit_task, menu);
        MenuItem item = menu.findItem(R.id.menu_save_task);
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(),
                0);
        item.setTitle(spanString);
        item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.menu_save_task) {
            if (forecastResponse != null) {
                showProgressDialog("Saving..");
                new Handler().postDelayed(() -> saveWeatherData(forecastResponse), 500);
            } else if (!CheckNetwork.isInternetAvailable(this)) {
                String forecastValue = "";
                String searchString = edtSearch.getText().toString().trim();
                int parentParamId = 0;
                if (!searchString.isEmpty()) {
                    for (MetaData metaData :
                            metaDataList) {

                        if ("Get Weather".equalsIgnoreCase(metaData.getMetaParamLabel())
                                || "Current condition".equalsIgnoreCase(metaData.getMetaParamLabel())
                                || "Weather".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                            forecastValue = searchString;
                            parentParamId = metaData.getMetaParamID();
                        } else if (parentParamId == metaData.getParentParameterId()) {
                            forecastValue = null;
                        }

                        saveDataAndUpdateCreationDate(this, metaData, forecastValue, setId);
                    }
                }

                setResult(RESULT_OK);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWeatherForecastResSuccess(WeatherResponse weatherResponse) {
        forecastResponse = weatherResponse;
        String locationName = weatherResponse.getLocation().getName() + ", "
                + weatherResponse.getLocation().getRegion();
        tvLocationName.setText(locationName);

        String date = Util.getFormattedDateTime(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_YYYY_MM_DD);
        tvDate.setText(date);

        try {
            if (weatherResponse.getForecast().getForecastday() != null) {
                tvCurrentCondition.setText(weatherResponse.getForecast().getForecastday().get(0)
                        .getDay().getCondition().getText());

                String sunriseTime = weatherResponse.getForecast().getForecastday().get(0)
                        .getAstro().getSunrise();
                tvTimeSunrise.setText(sunriseTime);
                String sunsetTime = weatherResponse.getForecast().getForecastday().get(0)
                        .getAstro().getSunset();
                tvTimeSunset.setText(sunsetTime);

                String tempHigh = "High " + weatherResponse.getForecast().getForecastday().get(0)
                        .getDay().getMaxtemp_c() + " °C";
                String tempLow = "Low " + weatherResponse.getForecast().getForecastday().get(0)
                        .getDay().getMintemp_c() + " °C";
                tvTempHigh.setText(tempHigh);
                tvTempLow.setText(tempLow);
            } else {
                tvCurrentCondition.setText("NA");
                tvTimeSunrise.setText("NA");
                tvTimeSunset.setText("NA");
                tvTempHigh.setText("NA");
                tvTempLow.setText("NA");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvCurrentCondition.setText("NA");
            tvTimeSunrise.setText("NA");
            tvTimeSunset.setText("NA");
            tvTempHigh.setText("NA");
            tvTempLow.setText("NA");
        }
    }

    @Override
    public void onWeatherHourlyResSuccess(WeatherResponse weatherResponse) {
        Util.hideKeyboard(this);
        hourlyForecastResponse = weatherResponse;
        layoutWeatherData.setVisibility(View.VISIBLE);
        HourlyForecastAdapter adapter = new HourlyForecastAdapter(weatherResponse.getForecast()
                .getForecastday().get(0).getHour(), WeatherActivity.this);
        rvWeatherData.setAdapter(adapter);
    }

    @Override
    public void onWeatherResponseError(String error) {
        layoutWeatherData.setVisibility(View.INVISIBLE);
        forecastResponse = null;
        hourlyForecastResponse = null;
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void saveWeatherData(WeatherResponse weatherResponse) {
        if (weatherResponse == null)
            return;

        for (MetaData metaData :
                metaDataList) {
            String forecastValue = "";
            if ("Get Weather".equalsIgnoreCase(metaData.getMetaParamLabel())
                    || "Current condition".equalsIgnoreCase(metaData.getMetaParamLabel())
                    || "Weather".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getLocation().getName();
            } else if ("Region".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getLocation().getRegion();
            } else if ("Country".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getLocation().getCountry();
            } else if ("Latitude".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getLocation().getLat();
            } else if ("Longitude".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getLocation().getLon();
            } else if ("Time zone".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getLocation().getTz_id();
            } else if ("Local time epoch".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getLocation().getLocaltime();
            } else if ("Last updated epoch".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getLast_updated();
            } else if ("Temperature °C".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getTemp_c();
            } else if ("Temperature °F".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getTemp_f();
            } else if ("Current condition".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getCondition().getText();
            } else if ("Wind mph".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getWind_mph();
            } else if ("Wind kph".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getWind_kph();
            } else if ("Wind degree".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getWind_degree();
            } else if ("Wind direction".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getWind_dir();
            } else if ("Pressure mb".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getPressure_mb();
            } else if ("Pressure inches".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getPressure_in();
            } else if ("Precipitation mm".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getPrecip_mm();
            } else if ("Precipitation inches".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getPrecip_in();
            } else if ("Humidity".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getHumidity();
            } else if ("Cloud".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getCloud();
            } else if ("Feels like °C".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getFeelslike_c();
            } else if ("Feels like °F".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getFeelslike_f();
            } else if ("Visibility Km".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getVis_km();
            } else if ("Visibility miles".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getVis_miles();
            } else if ("UV".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getUv();
            } else if ("Gust mph".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getGust_mph();
            } else if ("Gust kph".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getCurrent().getGust_kph();
            } else if ("Max temperature °C".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getMaxtemp_c();
            } else if ("Max temperature °F".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                //added two spaces before F as the param label also has spaces
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getMaxtemp_f();
            } else if ("Min temperature °C".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getMintemp_c();
            } else if ("Min temperature °F".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                //added two spaces before F as the param label also has spaces
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getMintemp_f();
            } else if ("Avg temperature °C".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                //added two spaces before F as the param label also has spaces
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getAvgtemp_c();
            } else if ("Avg temperature °F".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                //added two spaces before F as the param label also has spaces
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getAvgtemp_f();
            } else if ("Max wind speed mph".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getMaxwind_mph();
            } else if ("Max wind speed kph".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getMaxwind_kph();
            } else if ("Total precipitation mm".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getTotalprecip_mm();
            } else if ("Total precipitation inches".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getTotalprecip_in();
            } else if ("Avg Humidity".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getAvghumidity();
            } else if ("Avg visibility km".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getAvgvis_km();
            } else if ("Avg visibility miles".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getAvgvis_miles();
            } else if ("Daily will it rain".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getDaily_will_it_rain();
            } else if ("Daily chance of rain".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getDaily_chance_of_rain();
            } else if ("Daily will it snow".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getDaily_will_it_snow();
            } else if ("Daily chance of snow".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getDaily_chance_of_snow();
            } else if ("Forecast condition".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getCondition().getText();
            } else if ("Forecast UV".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getDay().getUv();
            } else if ("Sunrise".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getAstro().getSunrise();
            } else if ("Sunset".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getAstro().getSunset();
            } else if ("Moonrise".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getAstro().getMoonrise();
            } else if ("Moonset".equalsIgnoreCase(metaData.getMetaParamLabel())) {
                forecastValue = weatherResponse.getForecast().getForecastday()
                        .get(0).getAstro().getMoonset();
            }

            if (metaData.getMetaInputType().equalsIgnoreCase("TIME")
                    && !metaData.getMetaParamLabel().equalsIgnoreCase("TIME")) {
                forecastValue = Util.get24hrFormatTime(forecastValue, Locale.ENGLISH);
            }

            if (metaData.getMetaParamID() != 25 && metaData.getMetaParamID() != 15)
                saveDataAndUpdateCreationDate(this, metaData, forecastValue, setId);
        }

        dismissProgressDialog();
        setResult(RESULT_OK);
        finish();
    }

    private void saveDataAndUpdateCreationDate(Context context, MetaData metaData,
                                               String value, int currentSetID) {

        FieldDataSource fieldDataSource = new FieldDataSource(context);

        fieldDataSource.updateValue(Integer.parseInt(eventId), metaData.getMetaParamID(), currentSetID,
                locationId, value, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                GlobalStrings.CURRENT_GPS_LOCATION,
                deviceID, userID + "");

        long creationDate = System.currentTimeMillis();

        String oldcreationdate = fieldDataSource.getCreationDateForMobileApp(metaData.getCurrentFormID(),
                Integer.parseInt(eventId), Integer.parseInt(siteID), locationId, Integer.parseInt(userID), currentSetID);

        if (oldcreationdate != null) {
            fieldDataSource.updateCreationDate(Integer.parseInt(eventId), currentSetID,
                    locationId, Integer.parseInt(siteID), metaData.getCurrentFormID(), Long.parseLong(oldcreationdate));
        } else {
            fieldDataSource.updateCreationDate(Integer.parseInt(eventId), currentSetID,
                    locationId, Integer.parseInt(siteID), metaData.getCurrentFormID(), creationDate);
        }

        fieldDataSource.updateMeasurementTime(Integer.parseInt(eventId), currentSetID,
                locationId, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                creationDate);
    }
}
