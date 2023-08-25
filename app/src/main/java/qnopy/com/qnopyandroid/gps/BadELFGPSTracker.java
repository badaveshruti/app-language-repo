package qnopy.com.qnopyandroid.gps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bad_elf.badelfgps.BadElfDevice;
import com.bad_elf.badelfgps.BadElfGpsConnection;
import com.bad_elf.badelfgps.BadElfGpsConnectionObserver;
import com.bad_elf.badelfgps.BadElfService;

import java.nio.channels.AlreadyConnectedException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;

public class BadELFGPSTracker implements BadElfGpsConnectionObserver {

    Context mContext;
    List<BadElfDevice> mBadElfDevices = new ArrayList<>();
    BadElfDevice mBadElfDeviceModelClass;
    public BadElfGpsConnection mBadElfConnection;

    private static final String LOG_TAG = "NMEAParserAbhi";
    private TextUtils.SimpleStringSplitter splitter;

    public BadELFGPSTracker(Context context) {
        mContext = context;

        try {
            mBadElfDevices = BadElfDevice.getPairedBadElfDevices(mContext);
            if (mBadElfDevices.size() > 0) {
                mBadElfDeviceModelClass = mBadElfDevices.get(0);
                // Toast.makeText(context, "INSIDE BadELFGPSActivity NAME IS - "+mBadElfDeviceModelClass.toString(), Toast.LENGTH_LONG).show();
                mBadElfConnection = new BadElfGpsConnection(this, mContext);
            } else {
                //Toast.makeText(context, "Device not paired - "+mBadElfDeviceModelClass.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (RuntimeException e) {
            // Errors: Bluetooth not enabled or no paired Bad Elf Devices
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReady() {
        updateGui(mBadElfConnection.getState());

       /* mBadElfConnection.setBadElfDevice(mBadElfDeviceModelClass);
        Log.e("DeviceStatus", "onReady: "+mBadElfConnection.getState());
        String deviceStatus = "CONNECTED";
        if (deviceStatus.equals(mBadElfConnection.getState())){
            Log.e("DeviceStatus", "onReady: "+mBadElfConnection.getState());
        }else {
            mBadElfConnection.connect();
        }*/
    }

    @Override
    public void onStateChanged(BadElfService.State newState) {
        updateGui(newState);
    }

    private void updateGui(final BadElfService.State newState) {
        final boolean isConnected = (newState == BadElfService.State.CONNECTED);
        final boolean isIdle = (newState == BadElfService.State.IDLE);
        final boolean isDisConnected = (newState == BadElfService.State.DISCONNECTING);

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (isIdle) {
                    // Toast.makeText(mContext, "IDLE", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = mContext.getSharedPreferences("BADELFGPS", mContext.MODE_PRIVATE).edit();
                    editor.clear().commit();
                    try {
                        mBadElfConnection.setBadElfDevice(mBadElfDeviceModelClass);
                        mBadElfConnection.connect();
                    } catch (AlreadyConnectedException a) {
                        //mBadElfConnection.disconnect();
                        //new BadELFGPSTracker(mContext);

                    } catch (IllegalStateException i) {
                        i.printStackTrace();
                        Log.e("badELFexception", "run: " + i);
                    }
                }
                if (isConnected) {
                    GlobalStrings.mDeviceConnectedStatusFlag = 1;
                    GlobalStrings.mDeviceConnectedName = mBadElfDeviceModelClass.toString();
                    Log.e("flagName", "BadELFGPSTracker-  FLAG- " + GlobalStrings.mDeviceConnectedStatusFlag + " Name- " + GlobalStrings.mDeviceConnectedName);
                    Toast.makeText(mContext, " " + mBadElfDeviceModelClass.toString() + " CONNECTED", Toast.LENGTH_SHORT).show();
                } else {
                    GlobalStrings.mDeviceConnectedStatusFlag = 0;
                }

                if (isDisConnected) {
                    GlobalStrings.mDeviceConnectedStatusFlag = 0;
                    Toast.makeText(mContext, "" + mBadElfDeviceModelClass.toString() + " DISCONNECTED", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void disconnectTracker() {
        try {
            if (mBadElfConnection != null && (mBadElfConnection.getState()
                    == BadElfService.State.CONNECTED)) {
                mBadElfConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataReceived(byte[] data) {

        Log.e("BadELFGPSActivity", "run: " + new String(data, Charset.forName("ISO-8859-1")));

        String dataParse = new String(data, Charset.forName("ISO-8859-1"));

        if (dataParse.startsWith("$GPRMC")) {
            String[] strValues = dataParse.split(",");
            double latitude = Double.parseDouble(strValues[3]);

            double temp1 = latitude;
            double temp2 = Math.floor(temp1 / 100);
            double temp3 = (temp1 / 100 - temp2) / 0.6;
            latitude = (temp2 + temp3);
            if (strValues[4].charAt(0) == 'S') {
                Log.e("southWest", "onDataReceived: SSSS");
                latitude = -latitude;
            }
            double longitude = Double.parseDouble(strValues[5]);
            double temp11 = longitude;
            double temp22 = Math.floor(temp11 / 100);
            double temp33 = (temp11 / 100 - temp22) / 0.6;
            longitude = (temp22 + temp33);
            if (strValues[6].charAt(0) == 'W') {
                Log.e("southWest", "onDataReceived: WWWW");
                longitude = -longitude;
            }

            SharedPreferences.Editor editor = mContext.getSharedPreferences("BADELFGPS", mContext.MODE_PRIVATE).edit();
            editor.clear().commit();
            editor.putString("latitude", String.valueOf(latitude));
            editor.putString("longitude", String.valueOf(longitude));
            editor.apply();

            SharedPreferences prefs = mContext.getSharedPreferences("BADELFGPS", mContext.MODE_PRIVATE);
            String lat = prefs.getString("latitude", "");
            String lng = prefs.getString("longitude", "");

            Log.e("latitudeLongitude", "onDataReceived: latitude " + lat + " ; longitude=" + lng);
            Log.e("latitudeLongitude", "-----------------------------------------------------------------");
        }

        /*BasicNMEAHandler basicNMEAHandler = new BasicNMEAHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onRMC(long date, long time, double latitude, double longitude, float speed, float direction) {

                Log.e("LATLNG", "onRMC: BadELFGPSActivityy LATITUDE- "+latitude+" LONGITUDE- "+longitude );
            }

            @Override
            public void onGGA(long time, double latitude, double longitude, float altitude, FixQuality quality, int satellites, float hdop) {

                Log.e("LATLNG", "onGGA: BadELFGPSActivityy LATITUDE- "+latitude+" LONGITUDE- "+longitude);
            }

            @Override
            public void onGSV(int satellites, int index, int prn, float elevation, float azimuth, int snr) {


            }

            @Override
            public void onGSA(FixType type, Set<Integer> prns, float pdop, float hdop, float vdop) {

            }

            @Override
            public void onUnrecognized(String sentence) {

            }

            @Override
            public void onBadChecksum(int expected, int actual) {

            }

            @Override
            public void onException(Exception e) {

            }

            @Override
            public void onFinished() {

            }
        };

        BasicNMEAParser basicNMEAParser = new BasicNMEAParser(basicNMEAHandler);
        basicNMEAParser.parse(new String(data, Charset.forName("ISO-8859-1")));*/
    }

}
