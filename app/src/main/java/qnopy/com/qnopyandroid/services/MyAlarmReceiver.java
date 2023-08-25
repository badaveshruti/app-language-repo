package qnopy.com.qnopyandroid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Yogendra on 23-Jun-16.
 */
public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "qnopy.com.qnopyandroid.services.alarm";
    private static final String TAG = "MyAlarmReceiver";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {

        if (CheckNetwork.isInternetAvailable(context)) {
            Log.i(TAG, "Internet available.Started data sync service");
            if (context == null) {
                return;
            }

            Intent i = new Intent(context, DataSyncService.class);

            String bg_service = Util.getSharedPreferencesProperty(context, GlobalStrings.BG_SERVICE);
            Boolean isBG_SERVICE = false;

            if (bg_service == null) {
                isBG_SERVICE = true;
            } else {
                isBG_SERVICE = Boolean.parseBoolean(bg_service);
            }

            if (isBG_SERVICE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // TODO: 25-Sep-17 REMOVED BACKGROUND SERVICE IN ANDROID O.WE NEED TO START IN FOREGROUND
                    //   context.startForegroundService(i);

                    // TODO: 04-01-2018 NO BACKGROUND SERVICE FOR OREO OS
                } else {
                    context.startService(i);
                }
            } else {
                context.stopService(i);

            }
        }
    }
}
