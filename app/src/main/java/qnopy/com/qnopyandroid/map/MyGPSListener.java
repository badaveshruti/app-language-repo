package qnopy.com.qnopyandroid.map;

import android.location.GpsStatus;
import android.location.Location;

@SuppressWarnings("unused")
public class MyGPSListener extends MapActivity implements GpsStatus.Listener {

    public Location mLastLocation;
    MapActivity map;

    public boolean firstFix = false;
    public boolean gpsStarted = false;
    public boolean gpsStopped = false;

    @Override
    public void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
            firstFix = true;
            //Toast.makeText(MyGPSListener.this, "Gps got fixed", Toast.LENGTH_SHORT).show();
            System.out.println("gps fixed");
        }
        if (event == GpsStatus.GPS_EVENT_STARTED) {
//    		Toast.makeText(getBaseContext(), "Gps not fixed", Toast.LENGTH_SHORT).show();
            gpsStarted = true;
            System.out.println("gps not fixed");
        }
        if (event == GpsStatus.GPS_EVENT_STOPPED) {
//    		Toast.makeText(getBaseContext(), "Gps not fixed", Toast.LENGTH_SHORT).show();
            gpsStopped = true;
            System.out.println("gps not fixed");
        }
    }
}