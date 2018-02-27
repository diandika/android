package com.example.android.calorietracker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

public class TrackerActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "";
    private TextView distanceView;
    private SensorManager mSensorManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String LOG = "TRACKER: ";
    private Sensor accelerometer;
    private float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        distanceView = (TextView) findViewById(R.id.distance_value);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        distance = 0;
        accelerometer = mSensorManager.getDefaultSensor((Sensor.TYPE_ACCELEROMETER));
        //mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(LOG, "registered accelerometer");



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

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.set_target) {
            Intent intent = new Intent(this, SetTargetActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void trackStart(View view) {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

    public void shareToTwitter(View view) {
        TextView distance = (TextView) findViewById(R.id.distance_value);
        TextView distanceUnit = (TextView) findViewById(R.id.distance_unit);
        TextView calorie = (TextView) findViewById(R.id.calorie_value);
        TextView calorieUnit = (TextView) findViewById(R.id.calorie_unit);
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        String tweetMsg = ("Tubes-1: via com.diandika.android.calorietracker;\n"
                + distance.getText().toString() + " " + distanceUnit.getText().toString() + "\n"
                + calorie.getText().toString() + " " + calorieUnit.getText().toString());
        tweetIntent.putExtra(Intent.EXTRA_TEXT, (tweetMsg));
        tweetIntent.setType("text/plain");

        PackageManager mPackageManager = getPackageManager();
        List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            String message = "check tubes1: " + distance.getText().toString() + distanceUnit.getText().toString();
            i.putExtra(Intent.EXTRA_TEXT, message);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.twitter.com/intent/tweet?text=" + urlEncode(message)));
            Toast.makeText(this, "Twitter app not found", Toast.LENGTH_LONG).show();
        }
    }

    private String urlEncode(String message) {
        try {
            return URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }

    public void shareToFb(View view) {
        String packagename = "com.facebook.katana";
        String fullUrl = "https://www.facebook.com/sharer.php?u=..";

        Intent fbIntent = getPackageManager().getLaunchIntentForPackage(packagename);
        if (fbIntent == null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(fullUrl));
            startActivity(i);
        } else {
            TextView distance = (TextView) findViewById(R.id.distance_value);
            TextView distanceUnit = (TextView) findViewById(R.id.distance_unit);
            TextView calorie = (TextView) findViewById(R.id.calorie_value);
            TextView calorieUnit = (TextView) findViewById(R.id.calorie_unit);
            Intent shareintent = new Intent(Intent.ACTION_SEND);
            shareintent.setClassName(packagename, "com.facebook.katana.ShareLinkActivity");
            String fbMsg = ("Tubes-1: via com.diandika.android.calorietracker;\n"
                    + distance.getText().toString() + " " + distanceUnit.getText().toString() + "\n"
                    + calorie.getText().toString() + " " + calorieUnit.getText().toString());
            shareintent.putExtra(Intent.EXTRA_TEXT, fbMsg);
            startActivity(shareintent);
        }
    }

    public void trackStop(View view) {
        mSensorManager.unregisterListener(this);
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
