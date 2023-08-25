package qnopy.com.qnopyandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.ui.login.LoginActivity;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.uiutils.SendDBTask;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class AgreementActivity extends ProgressDialogActivity {


    TextView mTextViewAgreement;
    Button mButtontTextAccept, mButtonCancel;
    String text = "";
    String formId = "", eventID = "", siteID = "";
    String url;
    RequestQueue requestQueue;
    String username = null;
    String userGuid = null;
    String password = null;
    String deviceID;
    int userId;
    public static boolean mAcceptStatus;
    String mKey;
    public static Activity AgreementActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agreement_textfragment);
        AgreementActivity = this;

        mTextViewAgreement = findViewById(R.id.textViewAgreement);
        mButtontTextAccept = findViewById(R.id.acceptTxtButton);
        mButtonCancel = findViewById(R.id.cancelTxtButton);

        username = Util.getSharedPreferencesProperty(AgreementActivity.this, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(AgreementActivity.this, username);
        userId = Integer.parseInt(Util.getSharedPreferencesProperty(AgreementActivity.this, GlobalStrings.USERID));
        siteID = Util.getSharedPreferencesProperty(AgreementActivity.this, GlobalStrings.CURRENT_SITEID);
        deviceID = DeviceInfo.getDeviceID(AgreementActivity.this);
        Log.e("abhishek", "onCreate: AGREEMENT ACTIVITY -- " + userId + "------------" + userGuid + "-----------------------" + username);
        url = getResources().getString(R.string.prod_base_uri).concat(getResources().getString(R.string.accept_License));
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        Intent intent = getIntent();
        mKey = intent.getStringExtra("input");

        try {
            InputStream inputStream = getAssets().open("EndUserAgreement.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            text = new String(buffer);
            mTextViewAgreement.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mButtontTextAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abhishek", "onResponse: " + response);
                        afterAccept(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abhishek", "onErrorResponse: " + error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_guid", userGuid);
                        params.put("device_id", deviceID);
                        params.put("user_id", String.valueOf(userId));
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogout(AgreementActivity);
            }
        });
    }

    public String getSharedPreferencesProperty(Activity activity, String propertyName) {
        final SharedPreferences prefs = activity.getSharedPreferences(AgreementActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getString(propertyName, null);
    }

    public void setSharedPreferencesProperty(Activity context, String propertyName, String propertyValue) {
        final SharedPreferences prefs = context.getSharedPreferences(AgreementActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(propertyName, propertyValue);
        editor.apply();
    }

    private void setLogout(Activity agreementActivity) {
        String key_userguid = getSharedPreferencesProperty(agreementActivity, GlobalStrings.USERNAME);
        setSharedPreferencesProperty(agreementActivity, key_userguid, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.USERID, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.USERNAME, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.PASSWORD, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.COMPANYID, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.CURRENT_APPID, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.CURRENT_SITEID, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.CURRENT_SITENAME, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.CURRENT_LOCATIONID, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.CURRENT_LOCATIONNAME, "");
        setSharedPreferencesProperty(agreementActivity, GlobalStrings.IS_SESSION_ACTIVE, "false");

        Intent intent = new Intent(new Intent(AgreementActivity.this, LoginActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void afterAccept(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String responseCode = jsonObject.getString("responseCode");
            String message = jsonObject.getString("message");
            if (responseCode.equals("OK")) {

                if (mKey.equals("metasync")) {
                    Log.e("Agreement", "afterAccept: mKey is ----------" + mKey);
                    MetaSyncActivity.mFlag = 1;
                    Intent intent = new Intent(AgreementActivity.this, MetaSyncActivity.class);
                    //intent.putExtra("onAccept", "true");
                    startActivity(intent);
                    finish();
                }
                if (mKey.equals("dashboard")) {
                    Log.e("Agreement", "afterAccept: mKey is ----------" + mKey);
//                    Intent intent = new Intent(AgreementActivity.this, MainDrawerActivity.class);
                    Intent intent = new Intent(AgreementActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (mKey.equals("mobilereport")) {
                    Log.e("Agreement", "afterAccept: mKey is ----------" + mKey);
                    String userId = "", formId = "", eventID = "", siteID = "";
                    SharedPreferences prefs = getSharedPreferences("PDF_REPORT_PARAMETERS", MODE_PRIVATE);
                    userId = prefs.getString("USER_ID", "");
                    formId = prefs.getString("FORM_ID", "");
                    siteID = prefs.getString("SITE_ID", "");
                    eventID = prefs.getString("EVENT_ID", "");

                    Intent intent = new Intent(AgreementActivity.this, MobileReportRequiredActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("FORM_ID", formId);
                    intent.putExtra("SITE_ID", siteID);
                    intent.putExtra("EVENT_ID", eventID);
                    intent.putExtra("call", "AgreementActivity");
                    startActivity(intent);
                    finish();
                }
                if (mKey.equals("datasyncact")) {
                    Log.e("Agreement", "afterAccept: mKey is ----------" + mKey);
//                    Intent intent = new Intent(AgreementActivity.this, MainDrawerActivity.class);
                    Intent intent = new Intent(AgreementActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (mKey.equals("sendDB")) {
                    Log.e("Agreement", "afterAccept: mKey is ----------" + mKey);
                    if (CheckNetwork.isInternetAvailable(AgreementActivity.this)) {
                        new SendDBTask(AgreementActivity.this).execute();
                    } else {
                        CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), 10);
                    }
                    AgreementActivity.finish();
                }
                if (mKey.equals("filefolder")) {
                    Log.e("Agreement", "afterAccept: mKey is ----------" + mKey);
                    Intent intent = new Intent(AgreementActivity.this, FileFolderSyncActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
