package com.management.location;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.management.location.activitys.FunctionActivity;
import com.management.location.activitys.LocationActivity;
import com.management.location.helper.DatabaseHelper;
import com.management.location.service.AlertService;

public class ItemAddActivity extends Activity {

    RelativeLayout item1;

    Double lat, log;
    String name;

    String bell, wifi;

    int resid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);


        Button button = (Button) findViewById(R.id.button_add);

        item1 = (RelativeLayout) findViewById(R.id.item1);


        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                startActivityForResult(intent, 1001);


            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (lat == null || log == null) {
                    //위치 정보가 없을때

                    Toast.makeText(getApplicationContext(), "위치정보를 설정해 주세요.", Toast.LENGTH_SHORT).show();

                } else {
                    //위치 정보가 있을때


                    if (bell == null || wifi == null) {
                        //설정 정보가 없을떄


                        Toast.makeText(getApplicationContext(), "벨소리 모드 정보를 설정해 주세요.", Toast.LENGTH_SHORT).show();

                    } else {

                        if (name == null) {
                            Toast.makeText(getApplicationContext(), "제목을 설정해 주세요.", Toast.LENGTH_SHORT).show();

                        } else {
                            //모든 정보가 있을때
                            //db 에 저장과 동시에 location manager 도 동시에 등록한다.

                            resid = (int) (Math.random() * 10000);


                            //db저장
                            DatabaseHelper helper = new DatabaseHelper(getApplicationContext(), DatabaseHelper.DATABASE, null, DatabaseHelper.VER);
                            SQLiteDatabase db = helper.getWritableDatabase();

                            db.execSQL("insert into " + DatabaseHelper.DATABASE_TABLE + "(id,name,lat,log,bell,wifi) values ('" + resid + "','" + name + "','" + lat + "', '" + log + "', '" + bell + "', '" + wifi + "')");

                            helper.close();
                            db.close();

                            // 서비스 재실행(서비스는 db 에서 데이터를 불러와 등록하는 과정을 거침)
                            Intent intent = new Intent(getApplicationContext(), AlertService.class);
                            stopService(intent);
                            startService(intent);


                            finish();

                        }


                    }


                }


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                // 돌아오기전 엑티비티 확인.

                case 1001:
                    //선택한 위치 정보 담당.

                    this.lat = data.getDoubleExtra("lat", 0.0D);
                    this.log = data.getDoubleExtra("log", 0.0D);

                    //Log.d("back Test", "lat: " + lat + " log: " + log);

                    break;

                case 1003:
                    //벨소리 모드 담당.

                    this.bell = data.getStringExtra("bell");
                    this.wifi = data.getStringExtra("wifi");

                    // Log.d("back Test", "bell: " + bell + " wifi: " + wifi);
                    // 설정없음 = NOT
                    break;

                case 1004:
                    //제목 담당.

                    this.name = data.getStringExtra("name");

                    //Log.d("back Test", "name: " + name);
                    break;

                case 1005:
                    //앱 담당.



                    break;

                default:
                    break;


            }


        }

    }


}
