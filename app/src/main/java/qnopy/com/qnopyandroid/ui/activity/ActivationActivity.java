package qnopy.com.qnopyandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpStatus;

import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.requestmodel.SUser;
import qnopy.com.qnopyandroid.responsemodel.ActivationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.ActivationResponseModelV4;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class ActivationActivity extends ProgressDialogActivity {

    private static final String TAG = "ActivationActivity";
    EditText uNametxt, activationCodetxt;
    String userName, code, deviceID, password;
    Button activate;
    Context context;
    ProgressDialog progressdialog;
    TextView header;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        progressdialog = new ProgressDialog(this);
        uNametxt = findViewById(R.id.unametxt);
        activationCodetxt = findViewById(R.id.activationtxt);
        activate = findViewById(R.id.activatebutton);
        header = findViewById(R.id.textView5);
        activationCodetxt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(activationCodetxt, InputMethodManager.SHOW_IMPLICIT);

        String boldString = getString(R.string.please_enter_an_activation_code_sent_to_your_email);
        Spanned result;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(boldString, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(boldString);
        }
        header.setText(result);
        context = this;

        Bundle extras = getIntent().getExtras();
        userName = extras.getString("Username");
        password = extras.getString("Password");
        uNametxt.setText(userName);

//        Util.setOverflowButtonColor(ActivationActivity.this, Color.BLACK);

        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userName = uNametxt.getText().toString();
                code = activationCodetxt.getText().toString();
                deviceID = DeviceInfo.getDeviceID(context);

//                if (userName.isEmpty()) {
//                    uNametxt.setError("Enter Username");
//                    uNametxt.requestFocus();
//                } else
                if (code.isEmpty()) {
                    activationCodetxt.setError(getString(R.string.enter_activation_code));
                    activationCodetxt.requestFocus();
                } else {
                    new postActivationTask(context, userName, password, code, deviceID).execute();
                }
            }
        });
    }

    private class postActivationTask extends AsyncTask<Void, Void, Object> {
        String username = null;
        String activationCode = null;
        String devID = null;
        String pswd = null;
        Context mContext;

        postActivationTask(Context context, String username, String pswd, String activationCode, String deviceID) {
            this.username = username;
            this.activationCode = activationCode;
            this.devID = deviceID;
            this.pswd = pswd;
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressdialog.setIndeterminate(true);
            progressdialog.setMessage(getString(R.string.please_wait));
            progressdialog.show();
        }

        @Override
        protected Object doInBackground(Void... params) {
            ActivationResponseModel respModel = null;
            ActivationResponseModelV4 respModelv4 = null;

            String resp = null;
            try {
                // Get handle to HTTP service

                Log.d(TAG, " postActivationTask  username:" + username + " ,ActivationCode: " + activationCode + ",DeviceID: " + devID);

//                respModelv4 = mAquaBlueService.ActivateDeviceV4(GlobalStrings.Local_Base_URL,
//                        getResources().getString(R.string.prod_provision_device), userName, password, activationCode);

                respModelv4 = mAquaBlueService.ActivateDeviceV4(getResources().getString(R.string.prod_base_uri),
                        getResources().getString(R.string.prod_provision_device), userName, password, activationCode);

                if (respModelv4 != null) {
                    GlobalStrings.responseMessage = respModelv4.getMessage();

                    if (respModelv4.isSuccess()) {
                        String result = respModelv4.getResponseCode().toString();
                        if (result.equals(HttpStatus.CONFLICT.toString())) {

                            resp = HttpStatus.CONFLICT.toString();

                        } else {

                            List<SUser> userList = respModelv4.getData();
                            if (userList.size() > 0) {
                                for (SUser user : userList) {
                                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                                        storeUserData(user);
                                    } else {
                                        UserDataSource userData = new UserDataSource(context);
                                        userData.storeUser(user);
                                    }
                                }
                            }
                            resp = "SUCCESS";
                        }
                    } else {
                        resp = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error=" + e.getLocalizedMessage());
            }

            return resp;
        }

        @Override
        protected void onPostExecute(Object s) {
            progressdialog.dismiss();
            if (s != null) {
                if (s.equals(HttpStatus.CONFLICT.toString())) {
                    Toast.makeText(getApplicationContext(), GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                } else if (s.equals("SUCCESS")) {
                    initiateMetasync();
                    Util.scheduleAlarm(getApplicationContext());
                    finish();
                }
            } else {
                Toast.makeText(mContext, getString(R.string.unable_to_connect_to_server), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void initiateMetasync() {
        Intent metaIntent = new Intent(getApplicationContext(), MetaSyncActivity.class);
        startActivity(metaIntent);
    }

    private void storeUserData(SUser user) {
        if (user != null) {
            UserDataSource userData = new UserDataSource(context);
            userData.storeUser(user);
            Util.setSharedPreferencesProperty(context, GlobalStrings.USERNAME, user.getUserName());
            Util.setSharedPreferencesProperty(context, GlobalStrings.PASSWORD, user.getPassword());
            Util.setSharedPreferencesProperty(context, GlobalStrings.USERID, user.getUserId() + "");
            Util.setSharedPreferencesProperty(context, GlobalStrings.COMPANYID, user.getCompanyId() + "");
            Util.setSharedPreferencesProperty(context, user.getUserName(), user.getUserGuid());
            //Crittercism.setUsername(user.getUserName());//13-Dec-15
        }
    }
}
