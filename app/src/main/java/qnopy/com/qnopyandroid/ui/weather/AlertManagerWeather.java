package qnopy.com.qnopyandroid.ui.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.responsemodel.WeatherResponse;
import qnopy.com.qnopyandroid.util.Util;

public class AlertManagerWeather {

    static void getWeatherForecastDetails(String latLngs, Context context,
                                          OnWeatherResponseListener listener) {

        String baseUrl = "http://api.weatherapi.com/v1/forecast.json" +
                "?key=" + GlobalStrings.WEATHER_API_KEY + "&q=" + latLngs + "";

        ProgressDialog progressDialog
                = showProgressDialog(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl, response -> {
            try {
                WeatherResponse weatherResponse = new Gson().fromJson(response, WeatherResponse.class);
                listener.onWeatherForecastResSuccess(weatherResponse);
                getHourlyWeatherDetails(latLngs, context, listener, progressDialog);
            } catch (JsonSyntaxException e) {
                listener.onWeatherResponseError("No matching location found.");
                Log.e("Weather request failed", e.getMessage());
                dismissProgressDialog(progressDialog);
            }
        }, error -> {
            listener.onWeatherResponseError("No matching location found.");
            dismissProgressDialog(progressDialog);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    private static void getHourlyWeatherDetails(String latLngs, Context context,
                                                OnWeatherResponseListener listener, ProgressDialog progressDialog) {

        String baseUrl = "http://api.weatherapi.com/v1/history.json" +
                "?key=" + GlobalStrings.WEATHER_API_KEY + "&q=" + latLngs + "&dt="
                + Util.getFormattedDateTime(System.currentTimeMillis(), GlobalStrings.DATE_FORMAT_YYYY_MM_DD);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl, response -> {
            try {
                WeatherResponse weatherResponse = new Gson().fromJson(response, WeatherResponse.class);
                listener.onWeatherHourlyResSuccess(weatherResponse);
            } catch (JsonSyntaxException e) {
                listener.onWeatherResponseError("No matching location found.");
                Log.e("Weather request failed", e.getMessage());
            }
            dismissProgressDialog(progressDialog);
        }, error -> {
            listener.onWeatherResponseError("No matching location found.");
            dismissProgressDialog(progressDialog);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    private static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Fetching weather data. Please wait...");
        progressDialog.show();
        return progressDialog;
    }

    public static void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public interface OnWeatherResponseListener {
        void onWeatherForecastResSuccess(WeatherResponse weatherResponse);

        void onWeatherHourlyResSuccess(WeatherResponse weatherResponse);

        void onWeatherResponseError(String error);
    }
}
