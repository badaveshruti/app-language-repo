package qnopy.com.qnopyandroid.ui.calendarUser;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.SyncStatusDataSource;
import qnopy.com.qnopyandroid.responsemodel.SubmittalModel;
import qnopy.com.qnopyandroid.responsemodel.SubmittalResponseCollector;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.uiutils.CustomAlert;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.Util;

public class DownloadEventListTask extends AsyncTask<MediaType, Void, String> {

    private final AlertDialog progressBar;
    private final AquaBlueServiceImpl mAquaBlueService;
    private final String username;
    private final String userGuid;
    private final String password;
    private final int userID;
    private final String deviceID;
    private final SyncStatusDataSource syncStatusDb;
    private String lastSyncDate;
    private AppCompatActivity mContext;
    private OnEventDownloadListener mListener;

    public DownloadEventListTask(AppCompatActivity context, OnEventDownloadListener listener) {
        this.mContext = context;
        this.mListener = listener;

//        progressBar = AlertManager.showProgressBar(context, context.getString(R.string.downloading_events));
        progressBar = AlertManager.showQnopyProgressBar(context, context.getString(R.string.downloading_events));
        mAquaBlueService = new AquaBlueServiceImpl(context);
        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(context, username);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        deviceID = DeviceInfo.getDeviceID(context);
        syncStatusDb = new SyncStatusDataSource(context);
        lastSyncDate = syncStatusDb.getLastSyncDate(userID, GlobalStrings.SYNC_DATE_TYPE_EVENT);
    }

    //Although the message parameter doesn't have any use but it helps to show default loading text
    public DownloadEventListTask(AppCompatActivity context, OnEventDownloadListener listener,
                                 String message) {
        this.mContext = context;
        this.mListener = listener;

//        progressBar = AlertManager.showProgressBar(context, false);
        progressBar = AlertManager.showQnopyProgressBar(context, context.getString(R.string.loading));
        mAquaBlueService = new AquaBlueServiceImpl(context);
        username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(context, username);
        password = Util.getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        userID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.USERID));
        deviceID = DeviceInfo.getDeviceID(context);
        syncStatusDb = new SyncStatusDataSource(context);
        lastSyncDate = syncStatusDb.getLastSyncDate(userID, GlobalStrings.SYNC_DATE_TYPE_EVENT);
    }

    public void showAlertProgress() {
        if (progressBar != null) progressBar.show();
    }

    public void cancelAlertProgress() {
        if (progressBar != null && progressBar.isShowing())
            progressBar.cancel();
    }

    @Override
    protected void onPreExecute() {
        showAlertProgress();
    }

    @Override
    protected String doInBackground(MediaType... params) {

        SubmittalResponseCollector mResponse = new SubmittalResponseCollector();
        List<SubmittalModel> mSubmittalList = new ArrayList<>();
        String response;
        try {
            if (null != mAquaBlueService) {
                lastSyncDate = syncStatusDb.getLastSyncDate(userID, GlobalStrings.SYNC_DATE_TYPE_EVENT);

                mResponse = mAquaBlueService.getEventList(mContext.getResources().getString(R.string.prod_base_uri),
                        mContext.getResources().getString(R.string.download_eventlist),
                        userID + "", lastSyncDate);

                if (null != mResponse) {
                    Log.e("CallsCheck", "doInBackground: event list response called");
                    if (!mResponse.isSuccess()) {
                        String code = mResponse.getResponseCode().toString();
                        if (code.equals("401")) {
                            if (mResponse.getMessage() != null && !mResponse.getMessage().isEmpty())
                                GlobalStrings.responseMessage = mResponse.getMessage();
                            else
                                GlobalStrings.responseMessage = mContext.getString(R.string.your_device_disabled);

                            response = "DEVICE-DISABLE";
                        } else if (code.equals("417")) {
                            response = "USER-SUSPENDED";
                        } else {
                            response = "FALSE";
                            Log.e("Agreement", "doInBackground: mResponse.isSuccess() is--- "
                                    + mResponse.isSuccess() + " msg " + mResponse.getMessage() + " code " + mResponse.getResponseCode());
                        }
                    } else if (mResponse.isSuccess()) {
                        response = "SUCCESS";
                        Log.e("Agreement", "doInBackground: mResponse.isSuccess() is--- " + mResponse.isSuccess());
                        String newSyncTime_server = mResponse.getData().getLastSyncDate() + "";
                        Log.i("Download", "All submittal last sync time:" + newSyncTime_server);

                        if (mResponse.getData().getEventList().size() > 0) {

                            mSubmittalList = mResponse.getData().getEventList();
                            //INSERT EVENT LIST
                            EventDataSource eds = new EventDataSource(mContext);
                            eds.saveSubmittalsList(mSubmittalList, userID);

                            //Update LastSyncDate in d_sync_status table
                            syncStatusDb.insertLastSyncDate(userID, Long.valueOf(newSyncTime_server),
                                    GlobalStrings.SYNC_DATE_TYPE_EVENT);
                        }
                    } else {
                        GlobalStrings.responseMessage = mResponse.getMessage();
                        response = mResponse.getResponseCode().toString();
                        if (mResponse.getResponseCode() == HttpStatus.LOCKED) {
                            response = HttpStatus.LOCKED.toString();
                        }
                        if (mResponse.getResponseCode() == HttpStatus.NOT_ACCEPTABLE) {
                            response = HttpStatus.NOT_ACCEPTABLE.toString();
                        }
                        if (mResponse.getResponseCode() == HttpStatus.NOT_FOUND) {
                            response = HttpStatus.NOT_FOUND.toString();
                        }
                        if (mResponse.getResponseCode() == HttpStatus.BAD_REQUEST) {
                            response = HttpStatus.BAD_REQUEST.toString();
                        }
                    }
                } else {
                    response = "RETRY";
                }
            } else {
                response = "RETRY";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Download event", "All submittals doInBackground() Exception:" + e.getMessage());
            return null;
        }

        return response;
    }// end ofdoInBackground

    @Override
    protected void onPostExecute(String result) {
        Log.d("Download event", " onPostExecute: Result = " + result);
        cancelAlertProgress();
        showResult(result);
    }// end of onPostExecute

    private void showResult(String result) {
        Log.d("Download event", " All Submittals sync result= " + result);
        if (result != null) {
            if (result.equals("FALSE")) {
//                showAgreement();
                mListener.onEventDownloadFailed();
            } else if (result.equals("SUCCESS")) {
                mListener.onEventDownloadSuccess();
            } else if (result.equals("USER-SUSPENDED")) {
                String msg = mContext.getString(R.string.your_acc_was_suspended);
                CustomAlert.showAlert(mContext, msg, "Alert");
                mListener.onEventDownloadFailed();
            } else if (result.equals("DEVICE-DISABLE")) {
                String msg = GlobalStrings.responseMessage;
                CustomAlert.showUnAuthAlert(mContext, msg, mContext.getString(R.string.alert));
                mListener.onEventDownloadFailed();
            } else if (result.equals(HttpStatus.NOT_ACCEPTABLE.toString())) {
                Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                mListener.onEventDownloadFailed();
            } else if (result.equals("RETRY")) {
                Toast.makeText(mContext, mContext.getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
                mListener.onEventDownloadFailed();
            } else if (result.equals(HttpStatus.LOCKED.toString())
                    || result.equals(HttpStatus.NOT_FOUND.toString()) || result.equals("2000")) {
                Util.setDeviceNOT_ACTIVATED((Activity) mContext, username, password);
                mListener.onEventDownloadFailed();
            } else if (result.equals(HttpStatus.BAD_REQUEST.toString())) {
                Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_SHORT).show();
                mListener.onEventDownloadFailed();
            }
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.unable_to_connect_to_server), Toast.LENGTH_SHORT).show();
            mListener.onEventDownloadFailed();
        }

    }// end of showResult

    public interface OnEventDownloadListener {
        void onEventDownloadSuccess();

        void onEventDownloadFailed();
    }
}
