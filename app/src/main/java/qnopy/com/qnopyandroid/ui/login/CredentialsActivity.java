package qnopy.com.qnopyandroid.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.SUser;
import qnopy.com.qnopyandroid.responsemodel.DeviceUpdateResponseModel;
import qnopy.com.qnopyandroid.responsemodel.LoginResponseModelV4;
import qnopy.com.qnopyandroid.responsemodel.TicketResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;


/*
 * Activity to handle user registration process
 * Handles the network request in background using async task
 */


public class CredentialsActivity extends ProgressDialogActivity {

    private static final String LOG = LoginActivity.class.getName();
    int mStateMachine = 0;
    String strResponseBody = null;

    String usernameWs = null;
    String passwordWs = null;
    String oldpasswordWs = null;

    AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(this);
    Context objContext = CredentialsActivity.this;
    Boolean mRetVal = false;
    public static String mUserid = null;


    public final int STATE_CHECK_LOGIN = 0;
    public final int STATE_CHANGE_PASSWORD = 1;

    public static int mState = 0;

    SUser user = null;
    List<SUser> userList = null;
    LoginResponseModelV4 userResponse = null;
    TicketResponseModel ticketResponse = null;
    boolean permission_denied = false;
    DeviceUpdateResponseModel mdeviceresponse = null;
    String result = null;

    public void invokeLoginWebService(String userNameVar, String passWordVar, String OldPassWordVar) {
        this.usernameWs = userNameVar;
        this.passwordWs = passWordVar;
        this.oldpasswordWs = OldPassWordVar;
        this.mStateMachine = STATE_CHECK_LOGIN;

        if (CheckNetwork.isInternetAvailable(objContext)) {
            new LoginTask().execute();
        } else {
            CustomToast.showToast(this, getString(R.string.bad_internet_connectivity), 5);
        }
    }

    // Method to display the result
    private void showResult(String result) {
        Log.d(LOG, " showResult: result= " + result);

        cancelAlertProgress();
        this.onLoginResponse(result);
    }// end of showResult

    public void storeUserData() {
        if (user != null) {
            UserDataSource userData = new UserDataSource(objContext);
            userData.storeUser(user);
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERNAME, user.getUserName());
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.PASSWORD, user.getPassword());
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERID, user.getUserId() + "");
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.COMPANYID, user.getCompanyId() + "");
            Util.setSharedPreferencesProperty(objContext, user.getUserName(), user.getUserGuid());
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.APP_TYPE, user.getNotes());
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERTTYPE, user.getUserType());
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERROLE, String.valueOf(user.getUserRole()));
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.USERAPPTYPE, user.getUserAppType());
            Util.setSharedPreferencesProperty(objContext, GlobalStrings.IS_SESSION_ACTIVE, "true");

            String trialPeriod = user.getTrialStatus();
            boolean hasTrialPeriod = trialPeriod != null && !trialPeriod.isEmpty();

            if (user.getUserRole() == GlobalStrings.TRIAL_USER && hasTrialPeriod)
                Util.setSharedPreferencesProperty(objContext, GlobalStrings.TRIAL_PERIOD,
                        Long.parseLong(trialPeriod));
        }
    }

    void onLoginResponse(String loginState) {

    }

    // Handles sign-up request in back ground
    private class LoginTask extends AsyncTask<MediaType, Void, String> {

        @Override
        protected void onPreExecute() {

            Log.d(LOG, " onPreExecute: Populating the Person objects");
            // Init the progress dialog
            showAlertProgress();

        }// end of onPreExecute

        @SuppressLint("StringFormatInvalid")
        @Override
        protected String doInBackground(MediaType... params) {
            try {
                // Get handle to HTTP service
                switch (mStateMachine) {
                    case STATE_CHECK_LOGIN: // Register new user
                    {
                        Log.d(LOG, " doInBackground: Building HTTP POST Request");

                        if (null != mAquaBlueService) {
                            userResponse = mAquaBlueService.checkLogin(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_login, getApplicationContext()),
                                    usernameWs,
                                    passwordWs);

//                            ticketResponse = mAquaBlueService.getTicketNumber(getResources().getString(R.string.tech_base_uri),
//                                    getResources().getString(R.string.techSupport));
                            if (userResponse != null) {
                                if (userResponse.isSuccess()) {
                                    userList = userResponse.getData();
                                    if (userList.size() > 0) {
                                        for (SUser uItem : userList) {
                                            //               if (uItem.getPassword() != null && !uItem.getPassword().isEmpty()) {
                                            if (uItem.getUserName() != null && !uItem.getUserName().isEmpty()) {
                                                if (usernameWs.trim().equalsIgnoreCase(uItem.getUserName().trim())) {
                                                    result = "VALID";
                                                    user = uItem;
                                                    user.setPassword(passwordWs);
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
                                                String userguid = uItem.getUserGuid();
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
                            } else {
                                result = "RETRY";
                            }
                        }
                        // Return the response body to display to the user
                        return result;
                    }
                    case STATE_CHANGE_PASSWORD: // Update existing user
                    {
                        // Send request to Register
                        String response = null;
                        String result = null;
                        if (null != mAquaBlueService) {
                            result = mAquaBlueService.changePassword(getResources().getString(R.string.prod_base_uri),
                                    getResources().getString(R.string.prod_login),
                                    usernameWs,
                                    oldpasswordWs,
                                    passwordWs);
                        }

                        // Return the response
                        return result;
                    }
                    default:
                        break;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                Log.e(TAG, "Try catch error=" + e.getLocalizedMessage());
                permission_denied = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end of doInBackground

        @Override
        protected void onPostExecute(String result) {
            Log.d(LOG, " onPostExecute: Result = " + result);
            showResult(result);
        }// end of onPostExecute
    }// end of PostMessageTask


}// end of SignUpActivity
  










