package qnopy.com.qnopyandroid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import qnopy.com.qnopyandroid.GlobalStrings;


/**
 * Created by Yogendra on 28-Jun-16.
 */
public class BatteryStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        GlobalStrings.BATTERY_LEVEL=level+"";
    }
}
