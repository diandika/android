package com.example.android.calorietracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TrackerGuest extends AppCompatActivity implements SensorEventListener{
    private TextView distanceView;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_guest);

        distanceView = (TextView) findViewById(R.id.distance_value);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        distance = 0;
        accelerometer = mSensorManager.getDefaultSensor((Sensor.TYPE_ACCELEROMETER));
        //mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (savedInstanceState!=null){
            distanceView.setText(savedInstanceState.getString("distance_value"));
            distance = savedInstanceState.getFloat("distance_float");

            if (distance > 1000) {
                TextView km = (TextView) findViewById(R.id.distance_unit);
                km.setText("km");
            }

            int step = (int) (distance / 0.8);
            float calorie = (float) (step / 33);
            TextView calCount = (TextView) findViewById(R.id.calorie_value);
            DecimalFormat df = new DecimalFormat("#.##");
            String calShow = df.format(calorie);
            calCount.setText(calShow);

            boolean startTrack = savedInstanceState.getBoolean("started");
            if (startTrack){
                mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                Button startButton = (Button) findViewById(R.id.button_start);
                Button stopButton = (Button) findViewById(R.id.button_stop);
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }

    public void trackStart(View view) {
        mSensorManager.registerListener((SensorEventListener) this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Button startButton = (Button) findViewById(R.id.button_start);
        Button stopButton = (Button) findViewById(R.id.button_stop);
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    private float Vo = 0;
    private long to = System.currentTimeMillis();

    private void getAccelerometer(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        float result = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        DecimalFormat df = new DecimalFormat("#.##");
        if (Vo >= 0) {
            long tn = System.currentTimeMillis();
            if (result > 1.3) {
                df.setRoundingMode(RoundingMode.HALF_UP);
                float dt = (float) ((tn - to) / 1000);
                float Vt = (float) (Vo + result * 0.1);
                distance = (float) (distance + Vo * dt + 0.5 * result * 0.01);
                float distanceShow = distance;
                if (distance > 1000) {
                    distanceShow = distance / 1000;
                    TextView km = (TextView) findViewById(R.id.distance_unit);
                    km.setText("km");
                }
                String jarak = df.format(distanceShow);
                distanceView.setText(jarak);
                Vo = Vt;
                to = tn;
            }
        }
        int step = (int) (distance / 0.8);
        float calorie = (float) (step / 33);
        TextView calCount = (TextView) findViewById(R.id.calorie_value);
        String calShow = df.format(calorie);
        calCount.setText(calShow);
    }

    public void trackStop(View view) {
        mSensorManager.unregisterListener((SensorEventListener) this);
        Button startButton = (Button) findViewById(R.id.button_start);
        Button stopButton = (Button) findViewById(R.id.button_stop);
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("distance_value",distanceView.getText().toString());
        outState.putFloat("distance_float",distance);
        TextView startButton = (TextView) findViewById(R.id.button_start);
        if (startButton.getVisibility()==View.VISIBLE){
            outState.putBoolean("started",false);
        }else{
            outState.putBoolean("started",true);
        }
    }
}
