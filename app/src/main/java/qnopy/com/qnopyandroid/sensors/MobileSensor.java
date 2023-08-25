package qnopy.com.qnopyandroid.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Yogendra on 01-Mar-17.
 */

public class MobileSensor implements SensorEventListener {
    private static final String TAG = "MobileSensor";
    Context mContext;
    float azimuth, roll, pitch;
    String azimuthInDegress = "N/A";

    boolean isAccelerometerAvailable, isMagnetoMeterAvailable;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mMagneticField;
    float[] mGravity;
    float[] mGeomagnetic;

    public MobileSensor(Context context) {
        this.mContext = context;
        initializeSensorService();


    }

    public boolean isAccelerometerAvailable() {
        return isAccelerometerAvailable;
    }

    public void setAccelerometerAvailable(boolean accelerometerAvailable) {
        isAccelerometerAvailable = accelerometerAvailable;
    }

    public boolean isMagnetoMeterAvailable() {
        return isMagnetoMeterAvailable;
    }

    public void setMagnetoMeterAvailable(boolean magnetoMeterAvailable) {
        isMagnetoMeterAvailable = magnetoMeterAvailable;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public String getAzimuthInDegress() {
        return azimuthInDegress;
    }

    public void setAzimuthInDegress(String azimuthInDegress) {
        this.azimuthInDegress = azimuthInDegress;
    }

    private void initializeSensorService() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Log.i(TAG, "Mobile sensor manager initialized");
    }


    public void unregisterSensorService() {
        mSensorManager.unregisterListener(this);
        Log.i(TAG, "unregisterSensorService() Unregister Sensor Manager");
    }

    public void registerSensorService() {
        isAccelerometerAvailable = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        isMagnetoMeterAvailable = mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_GAME);

        setAccelerometerAvailable(isAccelerometerAvailable);
        setMagnetoMeterAvailable(isMagnetoMeterAvailable);

        Log.i(TAG, "registerSensorService() Register Sensor Manager");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

//        Log.i(TAG, "Mobile sensor onSensorChanged()");

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                int azi = 0;
                float orientationData[] = new float[3];
                SensorManager.getOrientation(R, orientationData);
                azimuth = orientationData[0];
                pitch = orientationData[1];
                roll = orientationData[2];


                azi = (int) Math.toDegrees(azimuth);
                if (azi < 0) {
                    azi += 360;
                }
                azimuthInDegress = azi + "";

                char degree_symmbol = '\u00B0';

                String[] pole = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
                String dir = "N";

                //31-Jul-17 CALCULATE POLE
                for (int i = 0; i < pole.length; i++) {
                    if (azi < 45.0 / 2.0 + 45.0 * i) {
                        dir = pole[i];
                        break;
                    }
                }

                azimuthInDegress = azimuthInDegress + degree_symmbol + " " + dir;

//                Log.i(TAG, "Azimuth:" + azimuthInDegress);

                setAzimuth(azimuth);
                setRoll(roll);
                setPitch(pitch);
//                Log.i(TAG, "Azimuth in Degrees:" + azimuthInDegress);
                // now how to use previous 3 values to calculate orientation
            }
        }
        setAzimuthInDegress(azimuthInDegress);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
//        Log.i(TAG, "Mobile sensor onAccuracyChanged()");
    }
}
