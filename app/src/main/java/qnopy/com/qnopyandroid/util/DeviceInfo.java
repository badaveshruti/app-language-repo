package qnopy.com.qnopyandroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;

public class DeviceInfo {

    static String TAG = "DeviceInfo";

    public static String getDeviceID(Context context) {
        String deviceID = "";
        deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID);

        if (deviceID != null && !deviceID.isEmpty()) {
            return deviceID;
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deviceID = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
/*                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephony.getDeviceId() != null) {
                    deviceID = mTelephony.getDeviceId();
                } else {
                    deviceID = Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }*/

                deviceID = getMacAddr();
            }

            Util.setSharedPreferencesProperty(context, GlobalStrings.DEVICEID, deviceID);
            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, deviceID);
        }

        Log.i(TAG, "DeviceInfo WIFI DEVICE_ID stored in session:-" + deviceID);
        return deviceID;
    }

/*    public static String getDeviceID(Context context) {
        String deviceID = null;
        // deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//
//            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            deviceID = wm.getConnectionInfo().getMacAddress();
//            Log.i(TAG, "DeviceInfo WIFI DEVICE_ID:-" + deviceID);
//
//
//        if (deviceID != null && deviceID.contains(":")) {
//            deviceID = deviceID.replace(':', '-');
//            Util.setSharedPreferencesProperty(context, GlobalStrings.DEVICEID, deviceID);
//            Log.i(TAG, "DeviceInfo WIFI DEVICE_ID stored in session:-" + deviceID);
//
//        }

        deviceID = getMacAddr();
        if (deviceID != null && !deviceID.isEmpty()) {
            Util.setSharedPreferencesProperty(context, GlobalStrings.DEVICEID, deviceID);
            Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, deviceID);
        } else {
            //10-May-17 SET DEVICE ID FROM SESSION
            deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.DEVICEID);
//
//            if (deviceID==null || deviceID.isEmpty()){
//
//                //STATIC MAC ADDRESS CREATED BY QNOPY FOR SIMULATOR
//                deviceID="02-02-00-00-00-00";
//                Util.setSharedPreferencesProperty(context, GlobalStrings.DEVICEID, deviceID);
//                Util.setSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID, deviceID);
//            }

        }

        Log.i(TAG, "DeviceInfo WIFI DEVICE_ID stored in session:-" + deviceID);

        return deviceID;
    }*/

    // TODO: 05-May-17 UPDATED MARSHMALLOW MAC-ADDRESS FILTER
    public static String getMacAddr() {
        //Android M
        String macAddress = "";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String digit = Integer.toHexString(b & 0xFF);

                    if (digit.length() == 1) {
                        digit = 0 + digit;//IF SINGLE DIGIT THEN APPEND 0(ZERO) BEFORE DIGIT
                    }
                    res1.append(digit.toUpperCase() + "-");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                macAddress = res1.toString();
                return macAddress;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macAddress;
    }

    public static String getIMEInumber(Context context) {
        Log.i(TAG, "\n\ngetIMEInumber() IN time:-" + System.currentTimeMillis());
        String IMEI = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        IMEI = tm.getDeviceId();
        IMEI = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("getIMEInumber()", "IMEI Number:" + IMEI);

        Log.i(TAG, "getIMEInumber() OUT time:-" + System.currentTimeMillis());

        return IMEI;
    }

    public static DeviceInfoModel getDeviceInfo(Context context) {
        Log.i(TAG, "\n\ngetDeviceInfo() IN time:-" + System.currentTimeMillis());

        DeviceInfoModel info = new DeviceInfoModel();
        String username = Util.getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        String userGuid = Util.getSharedPreferencesProperty(context, username);

        Log.i(TAG, "getDeviceInfo() session user:-" + username + ",UserGUID:" + userGuid);

        DisplayMetrics metrics = new DisplayMetrics();
//        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
//        info.setScreen_resolution("" + width + " x " + height);

        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        info.setIp_address(ip);
        info.setMac_address(getDeviceID(context));
        info.setImei_no(getIMEInumber(context));
        //      info.setBattery_percentage(GlobalStrings.BATTERY_LEVEL);

        info.setDevice_name(Build.BRAND + " " + Build.DEVICE);
//        info.setDeviceId("70-BB-E9-2B-E2-9A");
        info.setDeviceId(getDeviceID(context));

        info.setUser_guid(userGuid);
        String deviceType = context.getResources().getString(R.string.screen_type);
        info.setDeviceType("ANDROID");
//        if (isTabletDevice(context)) {
//            info.setDeviceType("ANDROID");
//
//        }
        info.setModel_number(Build.MODEL);
        info.setOs_version("Build v" + Build.VERSION.RELEASE + " and Android OS" + Build.VERSION.SDK_INT);

        Log.i(TAG, "getDeviceInfo() OUT time:-" + System.currentTimeMillis());

        return info;
    }

    public static boolean isTabletDevice(Context activityContext) {
        // Verifies if the Generalized Size of the device is XLARGE to be
        // considered a Tablet

        Log.i(TAG, "isTabletDevice() IN time:" + System.currentTimeMillis());
        boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE);

        // If XLarge, checks if the Generalized Density is at least MDPI
        // (160dpi)
        if (xlarge) {
            DisplayMetrics metrics = new DisplayMetrics();
            Activity activity = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,+
            // DENSITY_TV=213, DENSITY_XHIGH=320
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == DisplayMetrics.DENSITY_TV
                    || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

                // Yes, this is a tablet!
                return true;
            }
        }

        Log.i(TAG, "isTabletDevice() OUT time:" + System.currentTimeMillis());
        // No, this is not a tablet!
        return false;
    }

}