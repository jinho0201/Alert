package com.management.location.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.management.location.R;
import com.management.location.helper.DatabaseHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;


public class AlertService extends Service {


    WifiManager wifiManager;

    private LocationManager mLocationManager;
    private IntentReceiver mIntentReceiver;

    ArrayList mPendingIntentList;

    String intentKey = "Proximity";

    DatabaseHelper helper;
    SQLiteDatabase db;

    boolean isGPSEnabled = false;  //gps 이용 유무
    boolean isNetworkEnabled = false; // 네트워크 이용 유무
    LocationManager manager; // 위치 정보 프로바이더

    Double firstlat;
    Double firstlog;
    //현재 위치.


    Double lat; //위도
    Double log; //경도

    Double setLat;
    Double setLon;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("started", true);
        editor.commit();
        //preference 데이터에 시작여부를 저장.


        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE); //위치관리자는 시스템 서비스이므로 객체를 참조하기 위해서 getSystemService()메소드를 사용
        //시스템 서비스에서 와이파이 서비스 가져오기.

        //항상 서비스 켜짐.
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.icon1);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText("running");
        builder.setWhen(0);

        Notification noti = builder.build();

        //builder.addAction()

        startForeground(849, noti);
        //포어그라운드에서 서비스 동작 //상시 켜짐.


        // 위치 관리자 객체 참조
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mPendingIntentList = new ArrayList();


        //레지스터 등록

        //저장된 위치정보 가져오기.
        //구조는 helper - DatabaseHelper 참조

        helper = new DatabaseHelper(getApplicationContext(), DatabaseHelper.DATABASE, null, DatabaseHelper.VER);
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.DATABASE_TABLE, null);

        int count = cursor.getCount();


        for (int i = 0; i < count; i++) {

            cursor.moveToNext();

            String resid = cursor.getString(0);
            String name = cursor.getString(1);
            String lat = cursor.getString(2);
            String log = cursor.getString(3);

            //벨소리와 와이파이 온오프를 특정 영역에 따라서 자동 변경

            String bell = cursor.getString(4);
            String wifi = cursor.getString(5);


            //아래 정의 되어있는 함수.
            register(name, Integer.valueOf(resid), Double.valueOf(lat), Double.valueOf(log), bell, wifi, 80f, -1);
            // 5번째 파라미터 가 작동 거리. 현 = 80m ; 실 작동 결과 파라미터 값보다 더 안쪽으로 들어와야 작동. 넉넉히(범위가 너무 넓은경우 겹쳐지면 오동작 발생 가능)


        }


        helper.close();
        cursor.close();
        db.close();
        //db 닫음


        // 수신자 객체 생성하여 등록
        mIntentReceiver = new IntentReceiver(intentKey);
        registerReceiver(mIntentReceiver, mIntentReceiver.getFilter());

        Toast.makeText(getApplicationContext(), count + "개의 알람 등록", Toast.LENGTH_LONG).show();

        return super.onStartCommand(intent, flags, startId);
    }


    public void register(String name, int id, double latitude, double longitude, String bell, String wifi, float radius, long expiration) {

        //장소 경고를 등록하기 위한 인텐트.
        Intent proximityIntent = new Intent(intentKey);

        proximityIntent.putExtra("name", name);
        proximityIntent.putExtra("id", id);
        proximityIntent.putExtra("latitude", latitude);
        proximityIntent.putExtra("longitude", longitude);
        // 벨소리 설정과 와이파이 설정
        proximityIntent.putExtra("bell", bell);
        proximityIntent.putExtra("wifi", wifi);


        PendingIntent intent = PendingIntent.getBroadcast(this, id, proximityIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        //로케이션 메니저에 근접 경고 등록. 알림 기능
       mLocationManager.addProximityAlert(latitude, longitude, radius, expiration, intent);
        mPendingIntentList.add(intent);
        //리스트에 추가하며 리스트는 장소들이 저장된다.
    }

    //근접 경고를 해지하기 위한 함수
    private void unregister() {
        if (mPendingIntentList != null) {
            for (int i = 0; i < mPendingIntentList.size(); i++) {
                PendingIntent curIntent = (PendingIntent) mPendingIntentList.get(i);
                //해제
                mLocationManager.removeProximityAlert(curIntent);
                mPendingIntentList.remove(i);
            }
        }

        if (mIntentReceiver != null) {
            unregisterReceiver(mIntentReceiver);
            mIntentReceiver = null;
        }
    }


    private class IntentReceiver extends BroadcastReceiver {


        String name;
        String bell;
        String wifi;


        private String mExpectedAction;
        Context mContext;


        public IntentReceiver(String expectedAction) {
            mExpectedAction = expectedAction;
        }

        public IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter(mExpectedAction);
            return filter;
        }


        /**
         * 받았을 때 호출되는 메소드
         */
        public void onReceive(Context context, Intent intent) { //SMS를 받으면 onReceive() 메소드가 자동으로 호출
            //파라미터로 전달되는 intent 객체 안에 SMS 데이터가 들어 있음
            if (intent != null) {

                this.mContext = context;

                setLat = intent.getDoubleExtra("latitude", 0);
                setLon = intent.getDoubleExtra("longitude", 0);


                name = intent.getStringExtra("name");

                //벨소리 온오프
                bell = intent.getStringExtra("bell");

                //와이파이 온오프
                wifi = intent.getStringExtra("wifi");


                /*
                int id = intent.getIntExtra("id", 0);
                double latitude = intent.getDoubleExtra("latitude", 0.0D);
                double longitude = intent.getDoubleExtra("longitude", 0.0D);
                */

                boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
                //정보 가져오기.

                if (isEntering) {
                    // 영역에 들어가는경우.

                    //확인 장소가 집일경우 확인
                    if (name.equals("집")) {//집인 경우는 데이터 저장시 name이 '집'으로 저장됨.

                        SharedPreferences pref = getSharedPreferences("phone", MODE_PRIVATE);
                        if (!pref.getString("phone", "null").equals("null")) {
                            //null이 아니면

                            String text = "이용자가 집에 도착하였습니다.";

                            //문자보내기.
                            SmsManager mSmsManager = SmsManager.getDefault();
                            mSmsManager.sendTextMessage(pref.getString("phone", "null"), null, text, null, null);

                        }


                    }


                    Toast.makeText(context, name + " 알람영역에 들어왔습니다.", Toast.LENGTH_SHORT).show();





                    // Toast.makeText(context, name + " 으로 들어감" , Toast.LENGTH_LONG ).show();
                } else if (!isEntering) {
                    //영역에서 나오는 경우.

                    //확인 장소가 집일경우 확인
                    if (name.equals("집")) {

                        SharedPreferences pref = getSharedPreferences("phone", MODE_PRIVATE);
                        if (!pref.getString("phone", "null").equals("null")) {
                            //null이 아니면

                            String text = "이용자가 집에서 나왔습니다.";

                            //문자보내기.
                            SmsManager mSmsManager = SmsManager.getDefault();
                            mSmsManager.sendTextMessage(pref.getString("phone", "null"), null, text, null, null);

                        }


                    }

                                        // Toast.makeText(context, name + " 에서 벗어남" , Toast.LENGTH_LONG).show();
                }


            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("started", false);
        editor.commit();
        unregister();
    }


    //위치정보를 가져오는 함수.
    public void startThisService() {

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //위치 관리자 객체 참조
        //위치 관리자는 시스템 서비스이므로 객체를 참조하기 위해서 getSystemService()메소드 사용

        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000; //10초 지난뒤 위치정보 갱신
        float minDistance = 0; //내 위치에서 이동했을 경우 위치정보 업데이트

        try {

            if (isGPSEnabled) {
                // GPS를 이용한 위치 요청
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            }
            if (isNetworkEnabled) {
                // 네트워크를 이용한 위치 요청
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
            }
            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                lat = lastLocation.getLatitude();
                log = lastLocation.getLongitude();


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private class GPSListener implements LocationListener { //LocationListener를 구현하는 새로운 GPSListener 클래스 정의
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            log = location.getLongitude(); //위도와 경도좌표 확인

            firstlat = lat;
            firstlog = log;

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

}
