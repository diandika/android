package com.example.android.calorietracker;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SetTargetActivity extends AppCompatActivity {

    private static final String LOG_TAG = SetTargetActivity.class.getSimpleName();
    private EditText text1,text2;
    private Spinner spinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_target);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void saveTarget(View view) {
        Log.d(LOG_TAG, "Save Target to Firebase!");
        //Get Data From view
        text1 = (EditText) findViewById(R.id.input_kalori);
        text2 = (EditText) findViewById(R.id.input_jarak);
        spinner1 = (Spinner) findViewById(R.id.input_waktu);
        if ((text1.getText().toString().matches(""))||(text2.getText().toString().matches(""))){
            Toast.makeText(this, "Please input something", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(LOG_TAG,text1.getText().toString());
        Log.d(LOG_TAG,text2.getText().toString());
        Log.d(LOG_TAG,spinner1.getSelectedItem().toString());

        int kalori = Integer.parseInt(text1.getText().toString());
        int jarak = Integer.parseInt(text2.getText().toString());
        int waktu;

        if (spinner1.getSelectedItem().toString().matches("Harian")){
            waktu = 1;
        }else {
            waktu = 2;
        }
        //Save Data to SQLite database
        SQLiteDatabase database = openOrCreateDatabase("android_db.db",MODE_PRIVATE, null);
        database.execSQL("create table if not exists target(kalori int,jarak int,waktu int)");
        database.execSQL("delete from target");
        String query = "insert into target values (" + kalori + "," + jarak + "," + waktu + ")";
        Log.d(LOG_TAG,query);
        database.execSQL(query);
        database.close();


        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        text1.setText("");
        text2.setText("");
    }
}
