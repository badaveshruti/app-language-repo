package qnopy.com.qnopyandroid.ui.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.clientmodel.CheckUserSSOResponse;
import qnopy.com.qnopyandroid.clientmodel.FetchUserDataRequest;
import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.customView.CustomButton;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.AppPreferenceDataSource;
import qnopy.com.qnopyandroid.db.DbAccess;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.SignInActivity;
import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.SUser;
import qnopy.com.qnopyandroid.responsemodel.LoginResponseModelV4;
import qnopy.com.qnopyandroid.responsemodel.PreferenceMappingModel;
import qnopy.com.qnopyandroid.responsemodel.PreferenceMappingResponse;
import qnopy.com.qnopyandroid.services.MyFirebaseMessagingService;
import qnopy.com.qnopyandroid.ui.activity.MetaSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.RegistrationActivity;
import qnopy.com.qnopyandroid.ui.login.model.FetchUserGuidResponse;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class LoginActivity extends CredentialsActivity implements OnTaskCompleted {
    public DbAccess datasource;
    public final int STATE_CHECK_LOGIN = 0;
    public final int STATE_CHANGE_PASSWORD = 1;

    private String username = null;
    private String passwd = null;
    private String passwdHash = null;
    String mUserAppType;
    int version = 0;
    EditText uname, pass;
    boolean gotoDownloadForm = false;
    List<PreferenceMappingModel> prefList = null;

    Context context;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final int LOCATION1_PERMISSION_REQUEST_CODE = 1;
    private static final int PHONE_STATE_PERMISSION_REQUEST_CODE = 2;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 3;
    private static final int NETWORK_PERMISSION_REQUEST_CODE = 4;
    private static final int WIFI_PERMISSION_REQUEST_CODE = 5;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 6;
    ProgressDialog procDialog;
    TextView signup;
    private CustomButton btnSignIn;
    private CustomTextView tvTermsNConditions;
    private TextInputLayout passwordLayout;
    private TextView tvForgotPassword;
    private Button btnSubmit;
    private ActivityResultLauncher<Intent> keycloakAuthTokenActivityLauncher;
    private AuthorizationService authService;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CheckUserSSOResponse ssoResponse;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ux);
        context = this;

        keycloakAuthTokenActivityLauncher
                = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        try {
                            AuthorizationResponse resp = AuthorizationResponse.fromIntent(result.getData());
                            AuthorizationException ex = AuthorizationException.fromIntent(result.getData());

                            if (resp != null) {
                                authService.performTokenRequest(resp.createTokenExchangeRequest(),
                                        (tokenResponse, error) -> {
                                            if (tokenResponse != null) {

                                                Util.setSharedPreferencesProperty(LoginActivity.this,
                                                        GlobalStrings.KEY_OPEN_ID_TOKEN_RESPONSE, tokenResponse.refreshToken);

                                                if (!CheckNetwork.isInternetAvailable(this)) {
                                                    Toast.makeText(context, getString(R.string.please_check_internet_connection),
                                                            Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                hitApiToFetchGUID(tokenResponse.accessToken);
                                            } else if (error != null) {
                                                Toast.makeText(context, error.errorDescription, Toast.LENGTH_LONG).show();
                                                enablePasswordLogin(true);
                                            } else
                                                enablePasswordLogin(true);
                                        });
                            } else {
                                if (ex != null && ex.errorDescription != null
                                        && !ex.errorDescription.toLowerCase(Locale.getDefault())
                                        .contains("user cancelled flow")) {
                                    Toast.makeText(context, ex.errorDescription, Toast.LENGTH_LONG).show();
                                    enablePasswordLogin(true);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        uname = findViewById(R.id.editText1);
        pass = findViewById(R.id.editText2);
        signup = findViewById(R.id.signup_tv);
        btnSignIn = findViewById(R.id.btnLogin);
        btnSubmit = findViewById(R.id.btnSubmit);

        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> SignInActivity.Companion.startActivity(context));

        passwordLayout = findViewById(R.id.etPasswordLayout);
        tvTermsNConditions = findViewById(R.id.tvTermsNConditions);
        tvTermsNConditions.setMovementMethod(LinkMovementMethod.getInstance());

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setTextColor(ContextCompat.getColor(this, R.color.login_text_grey));
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://app.qnopy.com/web5/en/web/forgot/password")
                        )
                );
            }
        });

/*        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }*/

        if (!checkPermission(Manifest.permission.READ_PHONE_STATE)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION_REQUEST_CODE);
            }
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClick();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfUserIsSSO();
            }
        });

        uname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uname.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        ActionBar actionBar = getActionBar();
//		actionBar.hide();
        if (actionBar != null) {
            actionBar.hide();
        }
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }

        deleteTempApkIfExists();
    }

    @Override
    public void onBackPressed() {

        if (btnSignIn.getVisibility() == View.VISIBLE) {
            enablePasswordLogin(false);
        } else
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  Toast.makeText(context, "Permission Granted, Now you can access location data.", Toast.LENGTH_LONG).show();
                } else {
                    //  Toast.makeText(context, "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();
                }

                if (!checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                            Manifest.permission.READ_PHONE_STATE)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION_REQUEST_CODE);
                    }
                }

            case PHONE_STATE_PERMISSION_REQUEST_CODE:

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    }
                    if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    }
                } else {
                    if (!checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, NETWORK_PERMISSION_REQUEST_CODE);
                        }
                    }
                }

            case STORAGE_PERMISSION_REQUEST_CODE:
                if (!checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, NETWORK_PERMISSION_REQUEST_CODE);
                    }
                }

            case NETWORK_PERMISSION_REQUEST_CODE:
                if (!checkPermission(Manifest.permission.ACCESS_WIFI_STATE)) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_WIFI_STATE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, WIFI_PERMISSION_REQUEST_CODE);
                    }
                }

            case WIFI_PERMISSION_REQUEST_CODE:
                if (!checkPermission(Manifest.permission.CAMERA)) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                }

            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(context, "Permission Granted, Now you can access Cam.", Toast.LENGTH_LONG).show();
                } else {
                    // Toast.makeText(context, "Permission Denied, You cannot access Cam.", Toast.LENGTH_LONG).show();
                }
        }
    }


    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected void clearPasswordView() {
        EditText pass = findViewById(R.id.editText2);
        pass.setText("");
    }

    private void checkIfUserIsSSO() {
        Util.hideKeyboard(this);

        String unameVal = uname.getText().toString();
        if (unameVal.length() < 1) {
            uname.setError(getString(R.string.enter_valid_username));
            uname.requestFocus();
        } else {
            setUsername(unameVal);

            if (CheckNetwork.isInternetAvailable(this))
                hitUserSSOApi(unameVal);
            else
                Toast.makeText(context, getString(R.string.please_check_internet_connection),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void hitApiToFetchGUID(String accessToken) {

        Executors.newSingleThreadExecutor()
                .execute(() -> {

                    handler.post(this::showAlertProgress);

                    String url = context.getString(R.string.base_url_guid)
                            + context.getString(R.string.url_get_user_guid);
                    StringRequest request = new StringRequest(Request.Method.GET, url, response -> {

                        handler.post(() -> {
                            cancelAlertProgress();
                            hitApiToFetchLoginDetails(response);
                        });
                    }, error -> handler.post(() -> {
                        cancelAlertProgress();
                        Toast.makeText(context,
                                objContext.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT).show();
                        enablePasswordLogin(true);
                    })) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> mapHeaders = new HashMap<>();
                            if (ssoResponse != null) {
                                mapHeaders.put("Authorization", "Bearer " + accessToken);
                                mapHeaders.put("companyid", ssoResponse.getCompanyId());
                                mapHeaders.put("realm", ssoResponse.getRealm());
                            }
                            return mapHeaders;
                        }
                    };

                    Volley.newRequestQueue(context).add(request);
                });
    }

    String guid = "";

    private void hitApiToFetchLoginDetails(String guidResponse) {

        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(context, getString(R.string.please_check_internet_connection),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        guid = guidResponse;

        //as api is removed from staging
/*        if (Util.isStagingUrl(this)) {
            FetchUserGuidResponse parsedRes
                    = new Gson().fromJson(guidResponse, FetchUserGuidResponse.class);

            if (parsedRes.isSuccess() && parsedRes.getData() != null) {
                guid = parsedRes.getData().getUserGuid();
                Util.setSharedPreferencesProperty(this, GlobalStrings.KEYCLOAK_GUID,
                        parsedRes.getData().getUserKeycloakGuid());
            } else {
                showToast(parsedRes.getError(), false);
                return;
            }
        }*/

        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    FetchUserDataRequest userDataRequest = new FetchUserDataRequest(guid);

                    JSONObject jsonReq = null;

                    try {
                        jsonReq = new JSONObject(new Gson().toJson(userDataRequest));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    handler.post(this::showAlertProgress);

                    String url = context.getString(R.string.prod_base_uri)
                            + context.getString(R.string.url_get_user_login_details);

                    JsonObjectRequest jsonObjectRequest
                            = new JsonObjectRequest(Request.Method.POST, url, jsonReq,
                            response -> handler.post(() -> {
                                cancelAlertProgress();
                                try {
                                    LoginResponseModelV4 loginResponse
                                            = new Gson().fromJson(response.toString(),
                                            LoginResponseModelV4.class);
                                    handleLoginResponse(loginResponse);
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context,
                                            objContext.getString(R.string.something_went_wrong),
                                            Toast.LENGTH_SHORT).show();
                                    enablePasswordLogin(true);
                                }
                            }), error -> {
                        cancelAlertProgress();
                        Toast.makeText(context,
                                objContext.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT).show();
                        enablePasswordLogin(true);
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            return getRequestHeaders(guid);
                        }
                    };

                    Volley.newRequestQueue(context).add(jsonObjectRequest);
                });
    }

    private void handleLoginResponse(LoginResponseModelV4 loginResponse) {
        this.usernameWs = getUsername();

        userResponse = loginResponse;

        if (userResponse.isSuccess()) {
            userList = userResponse.getData();
            if (userList.size() > 0) {
                for (SUser uItem : userList) {
                    if (uItem.getUserName() != null && !uItem.getUserName().isEmpty()) {
                        if (usernameWs.trim().equalsIgnoreCase(uItem.getUserName().trim())) {
                            result = "VALID";
                            user = uItem;

                            //12 Sep, 2022 (Sanket): As this keycloak login won't have any password so setting a dummy
                            //password. As prev dev has made few code related to password for db
                            user.setPassword("dummyKeyCloakPass");
                            if (getPasswd() != null) {
                                setPasswdHash(new String(Hex.encodeHex(DigestUtils.sha1(getPasswd()))));
                                setPasswd(getPasswdHash());
                            }

                            storeUserData();
                        } else {
                            UserDataSource userData = new UserDataSource(objContext);
                            userData.storeUser(uItem);
                        }
                    }
                }
            } else {
                result = "IN_VALID";
            }
        } else {

            GlobalStrings.responseMessage = userResponse.getMessage();
            if (userResponse.getResponseCode() == HttpStatus.LOCKED) {
                result = HttpStatus.LOCKED.toString();
            }
            if (userResponse.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                result = HttpStatus.NOT_ACCEPTABLE.toString();
            }
            if (userResponse.getResponseCode() == HttpStatus.NOT_FOUND) {
                result = HttpStatus.NOT_FOUND.toString();
            }
            if (userResponse.getResponseCode() == HttpStatus.BAD_REQUEST) {
                result = HttpStatus.BAD_REQUEST.toString();
            }
            if (userResponse.getResponseCode() == HttpStatus.UNAUTHORIZED) {
                result = HttpStatus.UNAUTHORIZED.toString();
            }
            if (userResponse.getResponseCode() == HttpStatus.EXPECTATION_FAILED) {
                result = HttpStatus.EXPECTATION_FAILED.toString();
            }
            if (userResponse.getResponseCode() == HttpStatus.CONFLICT) {

                result = HttpStatus.CONFLICT.toString();

                userList = userResponse.getData();
                if (userList.size() > 0) {
                    for (SUser uItem : userList) {
                        Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERNAME,
                                uItem.getUserName());
                        Util.setSharedPreferencesProperty(objContext,
                                GlobalStrings.USERID, uItem.getUserId() + "");
                        Util.setSharedPreferencesProperty(objContext, uItem.getUserName(),
                                uItem.getUserGuid());
                        Util.setSharedPreferencesProperty(objContext, GlobalStrings.COMPANYID,
                                uItem.getCompanyId());
                        Util.setSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD, passwordWs);
                        Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERROLE, uItem.getUserRole() + "");
                    }
                }
            }
        }

        onLoginResponse(result);
    }

    private Map<String, String> getRequestHeaders(String guid) {

        DeviceInfoModel deviceInfo = DeviceInfo.getDeviceInfo(context);
        String deviceToken = Util.getSharedPreferencesProperty(context,
                GlobalStrings.NOTIFICATION_REGISTRATION_ID);

        if (deviceToken == null || deviceToken.trim().isEmpty())
            new Thread(() -> MyFirebaseMessagingService
                    .generateFireBaseToken(context)).start();

        HashMap<String, String> mapHeaders = new HashMap<>();
        mapHeaders.put("user_guid", guid);
        mapHeaders.put("device_id", deviceInfo.getDeviceId());

        mapHeaders.put("device_token", deviceToken);
        mapHeaders.put("device_type", deviceInfo.getDeviceType());
        String appVersion = Util.getAppVersion(context);
        mapHeaders.put("service_provider", deviceInfo.getService_provider());
        mapHeaders.put("model_number", deviceInfo.getModel_number());
        mapHeaders.put("screen_resolution", deviceInfo.getScreen_resolution());
        mapHeaders.put("phone_number", deviceInfo.getPhone_number());
        mapHeaders.put("battery_percentage", deviceInfo.getBattery_percentage());
        mapHeaders.put("imei_no", deviceInfo.getImei_no());
        mapHeaders.put("os_version", deviceInfo.getOs_version());

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            mapHeaders.put("mac_address", deviceInfo.getMac_address());
            mapHeaders.put("ip_address", deviceInfo.getIp_address());
        }

        mapHeaders.put("device_name", deviceInfo.getDevice_name());
        mapHeaders.put("app_version", appVersion);
        return mapHeaders;
    }

    private void hitUserSSOApi(String userName) {
        Executors.newSingleThreadExecutor()
                .execute(() -> {

                    handler.post(this::showAlertProgress);

                    String url = context.getString(R.string.base_url_no_auth)
                            + context.getString(R.string.url_is_user_sso) + userName;
                    StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                        ssoResponse
                                = new Gson().fromJson(response, CheckUserSSOResponse.class);

                        handler.post(() -> {
                            cancelAlertProgress();
                            if (ssoResponse.isSso()) {
                                Util.setSharedPreferencesProperty(LoginActivity.this,
                                        GlobalStrings.KEY_SSO_RESPONSE, new Gson().toJson(ssoResponse));
                                fetchKeycloakOpenIdOAuthToken(ssoResponse, userName);
                            } else {
//                                showUnauthorisedSSOAlert();
                                enablePasswordLogin(true);
                            }
                        });
                    }, error -> handler.post(() -> {
                        cancelAlertProgress();
                        Toast.makeText(context,
                                objContext.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT).show();
                        enablePasswordLogin(true);
                    }));

                    if (CheckNetwork.isInternetAvailable(this))
                        Volley.newRequestQueue(context).add(request);
                    else {
                        handler.post(() -> {
                            Toast.makeText(context, getString(R.string.please_check_internet_connection),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void showUnauthorisedSSOAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Username doesn't exist");

        builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
            enablePasswordLogin(true);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void fetchKeycloakOpenIdOAuthToken(CheckUserSSOResponse ssoResponse, String username) {

        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(context, getString(R.string.please_check_internet_connection),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String baseEndPointUrl = ssoResponse.getRealmJson().getAuthServerUrl() + "realms/" + ssoResponse.getRealm()
                + "/protocol/openid-connect/";

        String authEndPoint = baseEndPointUrl + "auth?username=" + username;
        String tokenEndPoint = baseEndPointUrl + "token";

        AuthorizationServiceConfiguration serviceConfig =
                new AuthorizationServiceConfiguration(
                        Uri.parse(authEndPoint), // authorization endpoint
                        Uri.parse(tokenEndPoint)); // token endpoint

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        GlobalStrings.KEYCLOAK_CLIENT_ID, // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        GlobalStrings.KEYCLOAK_REDIRECT_URI); // the redirect URI to which the auth response is sent

        AuthorizationRequest authRequest = authRequestBuilder
                .setScopes("openid email")
                .build();

        authService = new AuthorizationService(this);
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        keycloakAuthTokenActivityLauncher.launch(authIntent);
    }

    private void enablePasswordLogin(boolean enable) {
        if (enable) {
            passwordLayout.setVisibility(View.VISIBLE);
            tvForgotPassword.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        } else {
            passwordLayout.setVisibility(View.GONE);
            tvForgotPassword.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
    }

    public void onSignInClick() {
        Util.hideKeyboard(this);

        String unameVal = uname.getText().toString();
//        unameVal = unameVal.replaceAll(" ", "");

        String password = pass.getText().toString();
        if (unameVal.length() < 1) {
            uname.setError(getString(R.string.enter_valid_username));
            uname.requestFocus();
        } else if (password.length() < 1) {
            pass.setError(getString(R.string.enter_password));
            pass.requestFocus();
        } else {
            setUsername(unameVal);
            setPasswd(password);
            loginProcess();
        }
    }

    public void loginProcess() {

        User user;
        UserDataSource userData = new UserDataSource(context);
        user = userData.getUser(getUsername());

        if ((user != null) && (getUsername().equals(user.getUserName()))) {
            setPasswdHash(new String(Hex.encodeHex(DigestUtils.sha1(getPasswd()))));//new String(Hex.encodeHex(DigestUtils.sha1(getPasswd())))  //hinkley //new String(Hex.encodeHex(DigestUtils.sha1(getPasswd())))
            if (getPasswdHash().equals(user.getUserPasswd())) {
                Toast.makeText(getApplicationContext(), R.string.login_successful, Toast.LENGTH_SHORT).show();

                setPasswd(getPasswdHash());
                Util.setSharedPreferencesProperty(context, GlobalStrings.USERNAME, getUsername());
                Util.setSharedPreferencesProperty(context, GlobalStrings.PASSWORD, getPasswdHash());
                Util.setSharedPreferencesProperty(context, GlobalStrings.USERID, user.getUserID() + "");
                Util.setSharedPreferencesProperty(context, GlobalStrings.COMPANYID, user.getCompanyID() + "");
                Util.setSharedPreferencesProperty(context, GlobalStrings.APP_TYPE, user.getNotes());
                Util.setSharedPreferencesProperty(context, user.getUserName(), user.getUserGuid());
                Util.setSharedPreferencesProperty(context, GlobalStrings.IS_SESSION_ACTIVE, "true");

                String deviceID = DeviceInfo.getDeviceID(context);

                initiateMetaSync();

            } else if ((user.getUserPasswd()) == null || user.getUserPasswd().isEmpty()) {
                //20-Jul-16 Username found but password is blank then delete that user

                UserDataSource userSource = new UserDataSource(context);
                userSource.deleteUser(user.getUserName());

                if (getPasswd() != null) {
                    setPasswdHash(new String(Hex.encodeHex(DigestUtils.sha1(getPasswd()))));
                    setPasswd(getPasswdHash());
                }

                requestForLogin(getUsername(), getPasswdHash());
            } else {
                requestForLogin(getUsername(), getPasswdHash());
            }

        } else {
            if (getPasswd() != null) {
                setPasswdHash(new String(Hex.encodeHex(DigestUtils.sha1(getPasswd()))));
                setPasswd(getPasswdHash());
            }
            requestForLogin(getUsername(), getPasswdHash());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        // all touch events close the keyboard before they are processed except EditText instances.
        // if focus is an EditText we need to check, if the touch event was inside the focus editTexts
        final View currentFocus = getCurrentFocus();
        try {
            if (currentFocus != null) {
                ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("dispatchTouch", "dispatchTouchEvent Error:" + e.getMessage());
        }
        return super.dispatchTouchEvent(ev);
    }

    public void launchRingDialog(Context context) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait ...", "Downloading image ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }


    void requestForLogin(String userName, String password) {
        //request for login
        invokeLoginWebService(userName, password, password);
    }

    void onLoginResponse(String result) {

        if (result == null || result.equals("RETRY")) {
            if (permission_denied) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        LoginActivity.this);

                alertDialogBuilder.setTitle(getString(R.string.permission_denied_upper_case));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder
                        .setMessage(getString(R.string.permission_denied_alert));
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                permission_denied = false;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:com.aqua.fieldbuddy"));
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                // Toast.makeText(getApplicationContext(), GlobalStrings.permission_denied_alert, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (result.equals("IN_VALID")) {
                CustomToast.showToast((Activity) context, getString(R.string.invalid_user_alert), 10);
            }

            if (result.equals("VALID")) {
                initiateMetaSync();
            }

            if (result.equals(HttpStatus.LOCKED.toString()) || result.equals(HttpStatus.NOT_FOUND.toString())
                    || result.equals(HttpStatus.NOT_ACCEPTABLE.toString()) || result.equals(HttpStatus.UNAUTHORIZED.toString())) {
                //27-12-2017 V9 UPDATE-REMOVED ACTIVATION
                // Util.setDeviceLOCKED((Activity) context, getUsername(), getPasswdHash());
                CustomAlert.showAlert(context, GlobalStrings.responseMessage, getString(R.string.alert));
            }

            if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                //Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                CustomAlert.showAlert(context, GlobalStrings.responseMessage, getString(R.string.alert));
            }
            if (result.equals(HttpStatus.EXPECTATION_FAILED.toString())) {
                CustomAlert.showAlert(context, GlobalStrings.responseMessage, getString(R.string.alert));
            }
            if (result.equals(HttpStatus.CONFLICT.toString())) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final androidx.appcompat.app.AlertDialog.Builder dialog
                                = new androidx.appcompat.app.AlertDialog.Builder(objContext);
                        dialog.setCancelable(false);
                        dialog.setTitle(R.string.device_alert);
                        dialog.setMessage(GlobalStrings.responseMessage
                                + getString(R.string.deactivate_old_device));
                        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int id) {

                                if (CheckNetwork.isInternetAvailable(objContext)) {
//
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String res = null;

                                            dialog.dismiss();
                                            mdeviceresponse = mAquaBlueService.updateDevice(getResources().getString(R.string.prod_base_uri),
                                                    getResources().getString(R.string.device_status_update));
                                            if (mdeviceresponse != null) {
                                                if (mdeviceresponse.isSuccess()) {
                                                    res = "VALID";
                                                    Log.i(TAG, "");
                                                    Intent metaIntent
                                                            = new Intent(getApplicationContext(),
                                                            MetaSyncActivity.class);
                                                    startActivity(metaIntent);
                                                    finish();
                                                } else {
                                                    GlobalStrings.responseMessage
                                                            = userResponse.getMessage();
                                                    if (mdeviceresponse.getResponseCode()
                                                            == HttpStatus.LOCKED) {
                                                        res = HttpStatus.LOCKED.toString();
                                                    }
                                                    if (mdeviceresponse.getResponseCode()
                                                            == HttpStatus.NOT_ACCEPTABLE) {
                                                        //   result[0] = HttpStatus.NOT_ACCEPTABLE.toString();
                                                        Util.setLogout(LoginActivity.this);
                                                    }
                                                    if (mdeviceresponse.getResponseCode()
                                                            == HttpStatus.NOT_FOUND) {
                                                        res = HttpStatus.NOT_FOUND.toString();
                                                    }
                                                    if (mdeviceresponse.getResponseCode()
                                                            == HttpStatus.BAD_REQUEST) {
                                                        res = HttpStatus.BAD_REQUEST.toString();
                                                    }
                                                    if (mdeviceresponse.getResponseCode()
                                                            == HttpStatus.UNAUTHORIZED) {
                                                        //  result[0] = HttpStatus.UNAUTHORIZED.toString();
                                                        Util.setLogout(LoginActivity.this);
                                                    }
                                                }
                                            }
                                        }
                                    }).start();
                                } else {
                                    CustomToast.showToast(LoginActivity.this,
                                            getString(R.string.bad_internet_connectivity), 10);
                                }
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERNAME,
                                        null);
                                Util.setSharedPreferencesProperty(objContext,
                                        GlobalStrings.USERID, null);
                                Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERNAME,
                                        null);
                                Util.setSharedPreferencesProperty(objContext, GlobalStrings.COMPANYID,
                                        null);
                                Util.setSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD,
                                        null);
                            }
                        });

                        final androidx.appcompat.app.AlertDialog alert = dialog.create();
                        alert.show();
                    }
                });
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void downloadAppPreferences(final boolean isDownloadForm) {

        if (CheckNetwork.isInternetAvailable(getApplicationContext())) {
            try {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        beforeDownload();

                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        PreferenceMappingModel model;
                        PreferenceMappingResponse pref_response;
                        String result = null;
                        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
                        String userGuid = Util.getSharedPreferencesProperty(context, username);

                        Log.i(TAG, "USerGuid For Preferences:" + userGuid);

                        int userId = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
                        int userCompany = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID));
                        prefList = new ArrayList<PreferenceMappingModel>();

                        if (null != mAquaBlueService) {
                            pref_response = mAquaBlueService.downloadAppPreferences(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_get_app_preferences), userGuid);
                            if (pref_response != null) {
                                if (pref_response.isSuccess()) {
                                    prefList = pref_response.getData();
                                    if (prefList.size() > 0) {

                                        AppPreferenceDataSource ds = new AppPreferenceDataSource(context);
                                        ds.truncateAppPreferenceMapping(userId);
                                        ds.storePreferenceMappingData(prefList, userId, userCompany);
                                        result = "SUCCESS";
                                    } else {
                                        Log.i(TAG, "downloadAppPreferences() No App preferences set for the user");
                                        result = "NO_PREF";
                                    }
                                } else {

                                    GlobalStrings.responseMessage = pref_response.getMessage();

                                    Log.e(TAG, "downloadAppPreferences() Error in download App preferences:" + GlobalStrings.responseMessage);
//                                    Toast.makeText(getApplicationContext(), "Unable to download app preferences.Please contact QNOPY Admin", Toast.LENGTH_LONG).show();

                                    if (pref_response.getResponseCode() == HttpStatus.LOCKED) {
                                        result = HttpStatus.LOCKED.toString();
                                    }
                                    if (pref_response.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                                        result = HttpStatus.NOT_ACCEPTABLE.toString();
                                    }
                                    if (pref_response.getResponseCode() == HttpStatus.NOT_FOUND) {
                                        result = HttpStatus.NOT_FOUND.toString();
                                    }
                                    if (pref_response.getResponseCode() == HttpStatus.BAD_REQUEST) {
                                        result = HttpStatus.BAD_REQUEST.toString();
                                    }
                                }

                            } else {
                                result = null;
                                Log.e(TAG, "downloadAppPreferences() error in download App preferences.");

                            }
                        }

                        return result;
                    }

                    @Override
                    protected void onPostExecute(String fn) {
                        afterDownload();

                        if (fn != null) {
                            if (fn.equalsIgnoreCase("SUCCESS")) {
                                Log.i(TAG, "App preferences downloaded successfully.");

                                if (isDownloadForm) {
                                    initiateMetaSync();
                                } else {
                                    invokeMainDrawerActivity();
                                }

                            } else if (fn.equalsIgnoreCase("NO_PREF")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.no_pref_set_for_user), Toast.LENGTH_LONG).show();
                                if (isDownloadForm) {
                                    initiateMetaSync();
                                } else {
                                    invokeMainDrawerActivity();
                                }
                            } else {
                                if (fn.equals(HttpStatus.LOCKED.toString()) || fn.equals(HttpStatus.NOT_FOUND.toString())) {
                                    //27-12-2017 V9 UPDATE-REMOVED ACTIVATION
                                    // Util.setDeviceLOCKED((Activity) context, getUsername(), getPasswdHash());
                                    CustomAlert.showAlert(context, GlobalStrings.responseMessage, getString(R.string.alert));
                                }
                            }
                        } else {
                            Log.e(TAG, "App Preferences download failed.");
                            Toast.makeText(getApplicationContext(), getString(R.string.unable_to_download_app_pref), Toast.LENGTH_LONG).show();

                            if (isDownloadForm) {
                                initiateMetaSync();
                            } else {
                                invokeMainDrawerActivity();
                            }
                        }
                    }
                }.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CustomToast.showToast((Activity) context,
                    getString(R.string.bad_internet_connectivity), 5);

            if (isDownloadForm) {
                initiateMetaSync();
            } else {
                invokeMainDrawerActivity();
            }
        }
    }

    public void initiateMetaSync() {

        //11-Sep-17 START ALARM SERVICE
        String userId;
        mUserAppType = Util.getSharedPreferencesProperty(context, GlobalStrings.USERAPPTYPE);
        userId = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        if (mUserAppType == null) {
            UserDataSource userDataSource = new UserDataSource(context);
            mUserAppType = userDataSource.getUserAppType(userId);
            Util.setSharedPreferencesProperty(context, GlobalStrings.USERAPPTYPE, mUserAppType);
        }

        if (mUserAppType != null) {
            ScreenReso.isLimitedUser = mUserAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_LIMITED);
            ScreenReso.isProjectUser = mUserAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_PROJECT);
            ScreenReso.isCalendarUser = mUserAppType.equalsIgnoreCase(GlobalStrings.APP_TYPE_CALENDAR);
        }

        Util.scheduleAlarm(getApplicationContext());
        Intent metaIntent = new Intent(getApplicationContext(), MetaSyncActivity.class);
        startActivity(metaIntent);
        finish();
    }

    private void invokeMainDrawerActivity() {
//        Intent applicationIntent = new Intent(context, MainDrawerActivity.class);
        Intent applicationIntent = new Intent(context, HomeScreenActivity.class);
        startActivity(applicationIntent);
        finish();
    }

    void beforeDownload() {
        procDialog = new ProgressDialog(context);
        procDialog.setIndeterminate(true);
        procDialog.setCancelable(false);
        procDialog.setMessage(getString(R.string.wait_message));
        procDialog.show();
    }

    void afterDownload() {
        if ((procDialog != null) && (procDialog.isShowing())) {
            try {
                procDialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "afterDownload:" + e.getLocalizedMessage());
            }
        }
        // uploadFieldData();
    }

    /**
     * @return the username
     */
    String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the passwd
     */
    String getPasswd() {
        return passwd;
    }

    /**
     * @param passwd the passwd to set
     */
    void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    /**
     * @return the passwdHash
     */
    String getPasswdHash() {
        return passwdHash;
    }

    /**
     * @param passwdHash the passwdHash to set
     */
    void setPasswdHash(String passwdHash) {
        this.passwdHash = passwdHash;
    }

    @Override
    public void onTaskCompleted() {
        invokeMainDrawerActivity();
    }

    @Override
    public void setGeneratedEventID(int id) {
    }

    @Override
    public void setGeneratedEventID(Object obj) {
    }

    void deleteTempApkIfExists() {
        File externalRootDir = Environment.getExternalStorageDirectory();
        File tempDir = new File(externalRootDir, ".FieldBuddy");
        File apkFile = new File(tempDir, GlobalStrings.tempApkName);
        if (apkFile.exists()) {
            apkFile.delete();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearPasswordView();
    }

    @Override
    public void onTaskCompleted(Object obj) {
    }
}
