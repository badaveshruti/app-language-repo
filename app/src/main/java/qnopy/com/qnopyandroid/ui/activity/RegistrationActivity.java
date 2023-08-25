package qnopy.com.qnopyandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.SUser;
import qnopy.com.qnopyandroid.responsemodel.RegistrationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.VerifyEmailResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 12/2/2017.
 */

public class RegistrationActivity extends ProgressDialogActivity {

    private static final String LOG = RegistrationActivity.class.getName();
    ProgressDialog progressDialog;

    Context context;
    EditText edtFirstname, edtLastname, edtemailid, edtcompanyname, edtpassword, edtconfrmpassword, edtphoneno, edtUsername;
    Button register;
    private boolean destroyed = false;
    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    String password;
    RegistrationResponseModel mRetRegisterResponse;
    Bundle extras;
    String pending = null;
    boolean isEmailNotVerified = false;
    String verified = null;
    VerifyEmailResponseModel mRetEmailVerify = null;
    ActionBar actionBar;
    String userGuid = null;
    String result = null;
    ProgressBar progressbar;
    public static Handler mHandler;
    Button s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registration);
        context = this;

        initi();

        extras = getIntent().getExtras();
        if (extras != null) {
            pending = extras.getString("Pending");
            verified = extras.getString("Verified");
            isEmailNotVerified = extras.getBoolean("isEmailNotVerified");
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Registration");
            actionBar.setDisplayHomeAsUpEnabled(true);
            // actionBar.setCustomView(cView);
        }

        if (pending != null && pending.equals("inactive")) {
            Util.setSharedPreferencesProperty(context, "Activation_Code", "");
        } else {
            Util.setSharedPreferencesProperty(context, "Activation_Code", "active");
        }

        if (verified != null && verified.equals("verified")) {
            Intent intent = new Intent(context, MetaSyncActivity.class);
            intent.putExtra("fromactivation", true);
            startActivity(intent);

        } else if (verified != null && verified.equals("notverified")) {
            LayoutInflater factory = LayoutInflater.from(context);
            final View DialogView = factory.inflate(R.layout.verifyemaildialog, null);
            final AlertDialog Dialog = new AlertDialog.Builder(context).create();
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.setView(DialogView);
            final Button s = DialogView.findViewById(R.id.ok);
            progressbar = DialogView.findViewById(R.id.progressbar);
            s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
                    String userGuid = Util.getSharedPreferencesProperty(context, username);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }

//                    if (CheckNetwork.isInternetAvailable(context)) {
//                        mRetEmailVerify = mAquaBlueService.verifyemailstatus(getResources().getString(R.string.prod_base_uri),
//                                getResources().getString(R.string.prod_verify_email),
//                                userGuid);
//
//                        if (mRetEmailVerify != null) {
//                            if (mRetEmailVerify.isSuccess()) {
//                                Util.setSharedPreferencesProperty(context, "Activation_Code", "");
//                                Intent intent = new Intent(context, MetaSyncActivity.class);
//                                intent.putExtra("fromactivation", true);
//                                startActivity(intent);
//                            }
//                        }
//                    }

                }
            });
            Dialog.show();
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNetwork.isInternetAvailable(context)) {

                    String firstname = edtFirstname.getText().toString().trim();
                    String lastname = edtLastname.getText().toString().trim();
                    String email = String.valueOf(edtemailid.getText().toString()).trim();
                    String pass = edtpassword.getText().toString().trim();
                    String pass1 = edtconfrmpassword.getText().toString().trim();
                    String compname = edtcompanyname.getText().toString().trim();
                    if (pass != null && pass1 != null && pass.equals(pass1)) {
                        password = pass;
                    }

                    if (!isValid(firstname)) {
                        edtFirstname.setError("Please enter first name");
                        edtFirstname.requestFocus();
                    } else if (!isValidEmail(email)) {
                        edtemailid.setError("Please enter correct Email ID");
                        edtemailid.requestFocus();
                    } else if (!isValid(pass)) {
                        edtpassword.setError("Please enter password");
                    } else if (!isValid(pass1)) {
                        edtconfrmpassword.setError("Please enter password");
                        edtconfrmpassword.requestFocus();
                    } else if (!isValid(password)) {
                        edtpassword.setError("Passwords don't match");
                        edtpassword.requestFocus();
                    }


//                        edtLastname.setError("Please enter last name");
//                        edtLastname.requestFocus();
//                    }
                    else {

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        builder.setTitle("Confirm your email")
                                .setMessage("We will send you a verification email. Is this (" + email + ") your correct email? ")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        new PostMessageTaskForRegistration().execute();
                                        mHandler = new Handler(Looper.getMainLooper()) {
                                            @Override
                                            public void handleMessage(Message message) {
                                                // This is where you do your work in the UI thread.
                                                // Your worker tells you in the message what to do.
                                                progressbar.setVisibility(View.GONE);
                                                s.setVisibility(View.VISIBLE);
                                            }
                                        };

                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        android.app.AlertDialog dia = builder.create();
                        dia.show();

                    }
//
//
//                    }
                } else {
                    Toast.makeText(context, getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateData(String firstname, String lastname, String email, String pass, String pass1) {
        if (!isValidEmail(email)) {
            edtemailid.setError("Please enter Email ID.");
            return false;
        }
        if (password == null) {
            // edtpassword.setError("Please enter Password");
            return false;

        }
        if (firstname.isEmpty()) {
//            edtFirstname.setError("Please enter first name");
            return false;

        }
        return !lastname.isEmpty();
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        // all touch events close the keyboard before they are processed except EditText instances.
        // if focus is an EditText we need to check, if the touchevent was inside the focus editTexts
        final View currentFocus = getCurrentFocus();
        try {
            if (!(currentFocus instanceof EditText) || !isTouchInsideView(ev, currentFocus)) {
                ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("dispatchTouch", "dispatchTouchEvent Error:" + e.getMessage());
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchInsideView(final MotionEvent ev, final View currentFocus) {
        final int[] loc = new int[2];
        currentFocus.getLocationOnScreen(loc);
        return ev.getRawX() > loc[0] && ev.getRawY() > loc[1] && ev.getRawX() < (loc[0] + currentFocus.getWidth())
                && ev.getRawY() < (loc[1] + currentFocus.getHeight());
    }

    @Override
    public void onBackPressed() {
        // MyController.getInstance().closeAllActivities();
        //Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_HOME);
        finish();
        Util.setSharedPreferencesProperty(context, "Activation_Code", "");
        // startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                Util.setSharedPreferencesProperty(context, "Activation_Code", "");
                return true;

            default:
                return true;
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValid(CharSequence target) {
        return target != null && !target.equals("");
    }

    private void initi() {
        edtFirstname = findViewById(R.id.edtFirstname);
        edtLastname = findViewById(R.id.edtLastname);
        edtemailid = findViewById(R.id.edtemailid);
        edtcompanyname = findViewById(R.id.edtcompanyname);
        edtpassword = findViewById(R.id.edtpassword);
        edtconfrmpassword = findViewById(R.id.edtconfrmpassword);
        //   edtphoneno = findViewById(R.id.edtphoneno);
        register = findViewById(R.id.register);
        //edtUsername = findViewById(R.id.edtUsername);

    }

    private class PostMessageTaskForRegistration extends AsyncTask<MediaType, Void, String> {

        @Override
        protected void onPreExecute() {

            // Init the progress dialog
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }// end of onPreExecute

        @Override
        protected String doInBackground(MediaType... mediaTypes) {

            String deviceID = DeviceInfo.getDeviceID(context);

            MultiValueMap<String, Object> files = new LinkedMultiValueMap<String, Object>();
            String response = null;
            SUser suser = new SUser();

            try {
                if (null != mAquaBlueService) {

                    String firstname = edtFirstname.getText().toString().trim();
                    String lastname = edtLastname.getText().toString().trim();
                    String companyName = edtcompanyname.getText().toString().trim();
                    String emailid = edtemailid.getText().toString().trim();
                    String mobileNumber = "0000000000";
                    String uname = edtemailid.getText().toString().trim();
                    String password1 = password;
//                    if (password.equals(password1)) {
//                        password1 = password;
//                    } else {
//                        edtconfrmpassword.setError("Password should be same");
//
//                    }

                    mRetRegisterResponse = mAquaBlueService.registrationwebservice(getResources().getString(R.string.prod_base_uri),
                            getResources().getString(R.string.prod_registration),
                            firstname, lastname, companyName, emailid, mobileNumber, uname, password1
                    );
                    if (null != mRetRegisterResponse) {
                        if (mRetRegisterResponse.isSuccess()) {
                            String responsecode = String.valueOf(mRetRegisterResponse.getResponseCode());
                            if (responsecode.equals("200")) {
                                suser = mRetRegisterResponse.getData();
                                response = "SUCCESS";
                                UserDataSource userDataSource = new UserDataSource(context);
                                userDataSource.truncateUserTable();
                            } else {
                                GlobalStrings.responseMessage = mRetRegisterResponse.getMessage();
                                response = mRetRegisterResponse.getResponseCode().toString();
                            }
                        } else {
                            GlobalStrings.responseMessage = mRetRegisterResponse.getMessage();
                            response = mRetRegisterResponse.getResponseCode().toString();
                        }
                    }

                } else {
                    response = "RETRY";
                }
            } catch (Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.e(LOG, "MetaDataSync doInBackground() Exception:" + e.getMessage());
                }

                return null;

            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            progressDialog.dismiss();

            if (res != null) {
                if (res.equals("SUCCESS")) {

                    saveUserData();

                    LayoutInflater factory = LayoutInflater.from(context);
                    final View DialogView = factory.inflate(R.layout.customdialog, null);
                    final AlertDialog Dialog = new AlertDialog.Builder(context).create();
                    Dialog.setCanceledOnTouchOutside(false);
                    Dialog.setView(DialogView);
                    s = DialogView.findViewById(R.id.btn_yes);
                    //   deleteDialogView.findViewById(R.id.btn_yes);

                    s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Dialog.dismiss();
                            Dialog.cancel();

                            // Intent applicationIntent = new Intent(context, MetaSyncActivity.class);
                            //applicationIntent.putExtra("fromactivation", true);

                            if (mRetRegisterResponse != null) {

                                String username = mRetRegisterResponse.getData().getUserName();
                                String userguid = mRetRegisterResponse.getData().getUserGuid();
                                String companyid = mRetRegisterResponse.getData().getCompanyId();
                                int userid = mRetRegisterResponse.getData().getUserId();
                                int userrole = mRetRegisterResponse.getData().getUserRole();

                                Util.setSharedPreferencesProperty(context, username, userguid);
                                Util.setSharedPreferencesProperty(context, GlobalStrings.USERNAME, username);
                                Util.setSharedPreferencesProperty(context, GlobalStrings.PASSWORD, password);
                                Util.setSharedPreferencesProperty(context, GlobalStrings.COMPANYID, companyid);
                                Util.setSharedPreferencesProperty(context, GlobalStrings.USERID, String.valueOf(userid));
                                Util.setSharedPreferencesProperty(context, GlobalStrings.USERROLE, userrole + "");
                                //    applicationIntent.putExtra("Password", password);

                                password = String.valueOf(Hex.encodeHex(DigestUtils.sha1(password)));

                                int status = 0;
                                status = mRetRegisterResponse.getData().getStatus();
                                if (status == 500) {
//                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                                    builder.setTitle("Email Verify")
//                                            .setCancelable(false)
//                                            .setMessage("An Email has sent at your registered email address.Please verify your email address.")
//                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                  finish();
////                                                    Intent intent = new Intent();
////                                                    intent.setAction(Intent.ACTION_MAIN);
////                                                    intent.putExtra("Verified",false);
////                                                    intent.addCategory(Intent.CATEGORY_HOME);
////                                                    startActivity(intent);
//                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                                        finishAffinity();
//                                                    }
//
//
//                                                }
//                                            });
//                                    android.app.AlertDialog dialog = builder.create();
//                                    dialog.show();

                                    LayoutInflater factory = LayoutInflater.from(context);
                                    final View DialogView = factory.inflate(R.layout.verifyemaildialog, null);
                                    final AlertDialog Dialog = new AlertDialog.Builder(context).create();
                                    Dialog.setCanceledOnTouchOutside(false);
                                    Dialog.setView(DialogView);
                                    s = DialogView.findViewById(R.id.ok);
                                    progressbar = DialogView.findViewById(R.id.progressbar);
                                    s.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            progressbar.setVisibility(View.VISIBLE);
                                            s.setVisibility(View.GONE);
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
//                                    Intent intent = new Intent(context, RegistrationActivity.class);
//                                    //intent.putExtra("Username",username);
//                                    intent.putExtra("Verified","verified");
//                                    intent.putExtra("Pending", "inactive");
//                                    startActivity(intent);
                                                                    Dialog.dismiss();
                                                                    Util.setSharedPreferencesProperty(context, "Activation_Code", "");
                                                                    Intent intent = new Intent(context, MetaSyncActivity.class);
                                                                    intent.putExtra("fromactivation", true);
                                                                    startActivity(intent);
                                                                    finish();

                                                                } else {
                                                                    //  progressbar.

//                                                                    progressbar.setVisibility(View.GONE);
//                                                                    s.setVisibility(View.VISIBLE);
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
                                                    } else {

                                                    }
                                                }
                                            }).start();

                                        }
                                    });
                                    Dialog.show();

                                }
                            }
                        }
                    });

                    //   finish();
                    Dialog.show();

                } else if (res.equals("RETRY")) {
                    Toast.makeText(context, "Registration Failed,Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Registration Failed,Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void saveUserData() {

        if (mRetRegisterResponse != null) {

            SUser sUser = null;
            UserDataSource userDataSource = new UserDataSource(context);
            sUser = new SUser();
            sUser.setFirstName(mRetRegisterResponse.getData().getFirstName());
            sUser.setLastName(mRetRegisterResponse.getData().getLastName());
            sUser.setPrimaryEmail(mRetRegisterResponse.getData().getPrimaryEmail());
            sUser.setPassword(mRetRegisterResponse.getData().getPassword());
            sUser.setConfirmPassword(mRetRegisterResponse.getData().getConfirmPassword());
            sUser.setCompanyName(mRetRegisterResponse.getData().getCompanyName());
            sUser.setUserRole(mRetRegisterResponse.getData().getUserRole());
            sUser.setUserGuid(mRetRegisterResponse.getData().getUserGuid());
            sUser.setCompanyId(mRetRegisterResponse.getData().getCompanyId());
            sUser.setUserName(mRetRegisterResponse.getData().getUserName());
            sUser.setStatus(mRetRegisterResponse.getData().getStatus());
            sUser.setUserId(mRetRegisterResponse.getData().getUserId());
            sUser.setToEmailList(mRetRegisterResponse.getData().getPrimaryEmail());
            sUser.setContactnumber(mRetRegisterResponse.getData().getContactnumber());
            sUser.setUserType(mRetRegisterResponse.getData().getUserType());
            Util.setSharedPreferencesProperty(context, GlobalStrings.USERTTYPE, mRetRegisterResponse.getData().getUserType());

            userDataSource.storeSelfSignUpUser(sUser);
        }

    }

}
