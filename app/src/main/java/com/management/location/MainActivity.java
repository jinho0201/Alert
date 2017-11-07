package com.management.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.management.location.activitys.addNumberActivity;
import com.management.location.adapter.ListViewAdapter;
import com.management.location.adapter.item;
import com.management.location.helper.DatabaseHelper;
import com.management.location.service.AlertService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private LocationManager mLocationManager;

    ListView listview, listviewHouse;
    Button button, messageBtn;
    TextView Text;
    List<item> mItems = new ArrayList<item>();
    List<item> mItemsHouse = new ArrayList<item>();

    DatabaseHelper helper;
    SQLiteDatabase db;

    String intentKey = "Proximity";

    boolean serviceStarted;

    @Override
    protected void onResume() {
        super.onResume();

        mItemsHouse.clear();
        mItems.clear();
        invalidate();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //아이템들을 등록하는 엑테비티. 데이터들을 한번에 저장한다.

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        serviceStarted = pref.getBoolean("started", false);


        if (!serviceStarted) {
            Intent intent = new Intent(getApplicationContext(), AlertService.class);
            startService(intent);
        }


        listview = (ListView) findViewById(R.id.listview);
        listviewHouse = (ListView) findViewById(R.id.listview_house);
        button = (Button) findViewById(R.id.button_add);
        messageBtn = (Button) findViewById(R.id.button_message);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ItemAddActivity.class);
                startActivity(intent);


            }
        });


        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), addNumberActivity.class);
                startActivity(intent);


            }
        });



        listview.setOnItemClickListener(new listClickListener());
        listviewHouse.setOnItemClickListener(new listClickListener());


    }

    private void invalidate() {

        if (mItems.size() > 0 || mItemsHouse.size() > 0)
            mItems.clear();
        mItemsHouse.clear();

        helper = new DatabaseHelper(getApplicationContext(), DatabaseHelper.DATABASE, null, DatabaseHelper.VER);
        db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.DATABASE_TABLE, null);

        int count = cursor.getCount();


        for (int i = 0; i < count; i++) {

            cursor.moveToNext();

            String name = cursor.getString(1);
            String bell = cursor.getString(4);
            String wifi = cursor.getString(5);


            // 데이터를 넣은후. List<> 에 추가 -  List<> 는 어뎁터에 이용.(리스트 뷰에 보여질 데이터들)
            item mItem = new item(name, bell, wifi);

            if (name.equals("집")) {
                //집일경우는 따른 리스트에 등록한다.

                mItemsHouse.add(mItem);

            } else {

                mItems.add(mItem);
            }


        }


        helper.close();
        cursor.close();
        db.close();

        listview.setAdapter(new ListViewAdapter(getApplicationContext(), mItems));
        listviewHouse.setAdapter(new ListViewAdapter(getApplicationContext(), mItemsHouse));

        // 서비스 재실행(서비스는 db 에서 데이터를 불러와 등록하는 과정을 거침)
        Intent intent = new Intent(getApplicationContext(), AlertService.class);
        stopService(intent);
        startService(intent);


    }

    private class listClickListener implements AdapterView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Text = (TextView) view.findViewById(R.id.name);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("알림 설정")
                    .setCancelable(true)
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {


                            String name = Text.getText().toString();

                            helper = new DatabaseHelper(getApplicationContext(), DatabaseHelper.DATABASE, null, DatabaseHelper.VER);
                            db = helper.getWritableDatabase();

                            Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.DATABASE_TABLE + " where name = '" + name + "'", null);


                            cursor.moveToNext();

                            String residss = cursor.getString(0);
                            String namess = cursor.getString(1);
                            String latss = cursor.getString(2);
                            String logss = cursor.getString(3);

                            String bellss = cursor.getString(4);
                            String wifiss = cursor.getString(5);

                            //페키지 정보 삭제.
                            SharedPreferences preferences = getSharedPreferences(namess, MODE_PRIVATE);
                            preferences.edit().remove("package").commit();


                            Intent proximityIntent = new Intent(intentKey);

                            proximityIntent.putExtra("name", namess);
                            proximityIntent.putExtra("id", residss);
                            proximityIntent.putExtra("latitude", latss);
                            proximityIntent.putExtra("longitude", logss);
                            proximityIntent.putExtra("bell", bellss);
                            proximityIntent.putExtra("wifi", wifiss);


                            PendingIntent intent = PendingIntent.getBroadcast(getApplicationContext(), Integer.valueOf(residss), proximityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                            mLocationManager.removeProximityAlert(intent);

                            db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE + " where name = '" + name + "'");


                            db.close();


                            Toast.makeText(getApplicationContext(), name + "  삭제 완료.", Toast.LENGTH_SHORT).show();

                            invalidate();

                            Text = null;


                        }
                    })
                    .setNegativeButton("위치확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String name = Text.getText().toString();

                            helper = new DatabaseHelper(getApplicationContext(), DatabaseHelper.DATABASE, null, DatabaseHelper.VER);
                            db = helper.getWritableDatabase();
                            Cursor cursor = db.rawQuery("select lat,log from " + DatabaseHelper.DATABASE_TABLE + " where name = '" + name + "'", null);
                            cursor.moveToNext();


                            Double lat = Double.valueOf(cursor.getString(0));
                            Double lon = Double.valueOf(cursor.getString(1));


                            cursor.close();
                            db.close();

                            Intent intent = new Intent(getApplicationContext(), confirmActivity.class);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lon);


                            startActivity(intent);


                        }
                    })
                    .setNeutralButton("수정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            String name = Text.getText().toString();




                        }
                    });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        }
    }
}
