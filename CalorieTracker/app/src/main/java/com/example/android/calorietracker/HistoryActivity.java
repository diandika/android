package com.example.android.calorietracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


/**
 * Created by user on 2/22/2018.
 */

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_history);

        TableLayout t=(TableLayout) findViewById(R.id.table_);
        TableRow tr1;
        TextView c1;
        TextView c2;
        TextView c3;
        SQLiteDatabase database = openOrCreateDatabase("android_db.db",MODE_PRIVATE, null);
        database.execSQL("create table if not exists sampletable2(time text,distance text,calory text)");
        database.execSQL("delete from sampletable2");
        database.execSQL("insert into sampletable2 values ('17/02/1997','20m','1024kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/1998','25m','1048kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/1999','26m','1049kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/2000','27m','1055kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/2001','28m','10342kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/2002','29m','1042kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/2003','30m','1432kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/2004','31m','1233344kkal')");
        database.execSQL("insert into sampletable2 values ('17/02/2005','32m','154kkal')");

        Cursor cursor= database.rawQuery("select * from sampletable2", null);
        if(cursor.moveToFirst()){
            do {
                tr1= new TableRow(this);
                c1= new TextView(this);
                c2= new TextView(this);
                c2.setGravity(Gravity.CENTER_HORIZONTAL);
                c3= new TextView(this);

                String time= cursor.getString(0);
                String distance= cursor.getString(1);
                String calory= cursor.getString(2);
                c1.setText(time);
                c2.setText(distance);
                c3.setText(calory);

                tr1.addView(c1);
                tr1.addView(c2);
                tr1.addView(c3);
                t.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            } while(cursor.moveToNext());
        }
        database.close();
    }
}
