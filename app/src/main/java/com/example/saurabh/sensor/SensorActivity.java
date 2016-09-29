package com.example.saurabh.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sManager;
    private TextView azm, ptch, rol;

    float Rot[]=null; //for gravity rotational data
    float I[]=null; //for magnetic rotational data
    float accels[]=new float[3];
    float mags[]=new float[3];
    float[] values = new float[3];

    float azimuth;
    float pitch;
    float roll;

    static int ACCE_FILTER_DATA_MIN_TIME = 1000; // 1000ms
    long lastSaved = System.currentTimeMillis();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            /*register the sensor listener to listen to the gyroscope sensor, use the
            callbacks defined in this class, and gather the sensor information as quick
            as possible*/
        azm = (TextView)findViewById(R.id.azimuth);
        ptch = (TextView)findViewById(R.id.pitch);
        rol= (TextView)findViewById(R.id.roll);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accels = event.values.clone();
                    break;
            }

            if (mags != null && accels != null) {
                Rot = new float[9];
                I = new float[9];
                SensorManager.getRotationMatrix(Rot, I, accels, mags);
                // Correct if screen is in Landscape

                float[] outR = new float[9];
                SensorManager.remapCoordinateSystem(Rot, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
                SensorManager.getOrientation(outR, values);

                //Sensor values displayed at a sample of 1000 msec
                if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {
                    lastSaved = System.currentTimeMillis();
                    azimuth = values[0] * 57.2957795f; //looks like we don't need this one
                    //azm.setText((int) azimuth);
                    azm.setText("Azimuth: " + azimuth);
                    //Log.d("Sensor", "Azimuth: "+ azimuth);
                    pitch = values[1] * 57.2957795f;
                    //Log.d("Sensor", "Pitch: "+ pitch);
                    ptch.setText("Pitch: " + pitch);
                    roll = values[2] * 57.2957795f;
                    //Log.d("Sensor", "Roll: "+ roll);
                    rol.setText("Roll: " + roll);
                    mags = null; //retrigger the loop when things are repopulated
                    accels = null; ////retrigger the loop when things are repopulated
                }
            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
    }
}
