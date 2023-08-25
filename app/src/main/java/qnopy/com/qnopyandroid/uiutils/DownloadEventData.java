package qnopy.com.qnopyandroid.uiutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.EventData;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.requestmodel.FieldDataForEventDownload;
import qnopy.com.qnopyandroid.responsemodel.DownloadDataResponseModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.util.Util;

//this class is not in use anymore...only its listener is being used
public class DownloadEventData {

    private final DownloadEventDataListener mListener;
    private final int mEventId;
    private final String deviceId;
    private final String userGuid;
    private final Context mContext;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handlerForUI = new Handler(Looper.getMainLooper());
    private final EventData mEvent;
    private DownloadDataResponseModel mRetDownloadResponse;

    public DownloadEventData(Context context, DownloadEventDataListener listener, EventData event) {
        this.mContext = context;
        this.mListener = listener;
        this.mEventId = event.getEventID();
        this.mEvent = event;

        deviceId = Util.getSharedPreferencesProperty(mContext, GlobalStrings.SESSION_DEVICEID);
        String username = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERNAME);
        userGuid = Util.getSharedPreferencesProperty(mContext, username);
    }

    private void hitDownloadEventApi() {
        AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(mContext);
        long lastSyncDate = 1;

        executor.execute(() -> {
            handlerForUI.post(mListener::showDownloadEventProgress);
            String response = null;

            mRetDownloadResponse = mAquaBlueService.downloadActiveEventDataForUser(mContext.getString(R.string.prod_base_uri),
                    mContext.getString(R.string.prod_download_active_event_data), userGuid,
                    deviceId, mEventId, lastSyncDate);

            if (null != mRetDownloadResponse) {
                if (mRetDownloadResponse.isSuccess()) {
                    response = "SUCCESS";
                } else {
                    GlobalStrings.responseMessage = mRetDownloadResponse.getMessage();
                    response = mRetDownloadResponse.getResponseCode().toString();
                }
            } else {
                response = "RETRY";
            }

            String finalResponse = response;
            handlerForUI.post(() -> showResult(finalResponse));
        });
    }

    private void showResult(String result) {

        if (result != null) {
            if (result.equals("SUCCESS")) {
                saveEventData();
            } else if ((result.equals(HttpStatus.EXPECTATION_FAILED.toString())) ||
                    (result.equals(HttpStatus.CONFLICT.toString()))) {
                Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
            } else if (result.equals(HttpStatus.UNAUTHORIZED.toString())) {
                Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                Util.setLogout((Activity) mContext);
            } else if (result.equals(HttpStatus.NOT_FOUND.toString())) {
                Toast.makeText(mContext, GlobalStrings.responseMessage, Toast.LENGTH_LONG).show();
                Util.setLogout((Activity) mContext);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.download_data_fail_msg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveEventData() {
        if (mRetDownloadResponse != null) {
            executor.execute(() -> {
                FieldDataSource fielddatasource = new FieldDataSource(mContext);

                //TRUNCATE DATA FROM d_FieldData
                fielddatasource.deleteFieldDataforEvent(mEventId);

                List<FieldDataForEventDownload> mRetDownloadEventDataList = mRetDownloadResponse.getData();

                if (mRetDownloadEventDataList != null && mRetDownloadEventDataList.size() > 0) {
                    for (FieldDataForEventDownload fieldDataForEventDownload : mRetDownloadEventDataList) {
                        fielddatasource.insertFieldDataListforUser(fieldDataForEventDownload,
                                fieldDataForEventDownload.getUserId() + "");
                    }
                } else {
                    handlerForUI.post(() ->
                            Toast.makeText(mContext, "No data found for this event",
                                    Toast.LENGTH_SHORT).show());
                }

                handlerForUI.post(mListener::cancelDownloadEventProgress);
                mListener.showLocationScreen(mEvent);
            });
        }
    }

    public void showDownloadDataAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("There might be data available for this event on server, do you want to download?");
        builder.setPositiveButton("Yes", (dialog, which) -> hitDownloadEventApi());
        builder.setNegativeButton("No", (dialog, which) -> {
            mListener.showLocationScreen(mEvent);
            dialog.cancel();
        });
/*
        builder.setNeutralButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
*/

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setTextColor(ContextCompat.getColor(mContext, R.color.color_Gray));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
//        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setAllCaps(false);
    }

    public interface DownloadEventDataListener {
        void showDownloadEventProgress();

        void cancelDownloadEventProgress();

        void showLocationScreen(EventData event);
    }
}
