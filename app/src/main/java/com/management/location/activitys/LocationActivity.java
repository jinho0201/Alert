package com.management.location.activitys;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.management.location.R;
import com.management.location.adapter.findItem;
import com.management.location.adapter.findViewAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    //장소를 선택하는 액티비티.

    Double lat;
    Double log;

    Double firstlat;
    Double firstlog;
    //현재 위치.


    boolean isGPSEnabled = false;  //gps 이용 유무
    boolean isNetworkEnabled = false; // 네트워크 이용 유무
    LocationManager manager; // 위치 정보 프로바이더
    private GoogleMap mMap;

    boolean firstMark = false;

    findViewAdapter adapter;

    Button find;
    EditText input;
    ListView list;

    Button setMyPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        startThisService();


        setMyPlace = (Button) findViewById(R.id.set_place);
        setMyPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(firstlat != null && firstlog != null){

                    setPlace(firstlat,firstlog);
                    Toast.makeText(getApplicationContext(), "알람 지점이 선택되었습니다.", Toast.LENGTH_SHORT).show();

                }


            }
        });



        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
                intent.putExtra("lat", lat);
                intent.putExtra("log", log);

                backData(intent);


            }
        });

        input = (EditText) findViewById(R.id.input);



        find = (Button) findViewById(R.id.find);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                invaliate(input.getText().toString());

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //지도가 보이면 호출.
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        //map 클릭 리스너

    }


    private void setPlace(Double lat, Double log) {


        if(lat != null && log != null){

            //마커 표시.
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title("선택 위치")).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, log), 17));

        }


    }



    @Override
    public void onMapClick(LatLng latLng) {
        //맵이 클릭될대 호출.

        this.lat = latLng.latitude;
        this.log = latLng.longitude;

        Toast.makeText(getApplicationContext(), "알람 지점이 선택되었습니다.", Toast.LENGTH_SHORT).show();

        setPlace(lat, log);

    }


    private void backData(Intent intent) {

        this.setResult(RESULT_OK, intent);
        finish();

    }


    public void startThisService(){


        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        try {

            if(isGPSEnabled) {
                // GPS를 이용한 위치 요청
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            }
            if(isNetworkEnabled) {
                // 네트워크를 이용한 위치 요청
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
            }
            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                 lat = lastLocation.getLatitude();
                 log = lastLocation.getLongitude();



            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }


    }


    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
             lat = location.getLatitude();
             log = location.getLongitude();

            firstlat = lat;
            firstlog = log;

           if(!firstMark){
               //첫 마크가 없을경우

                   setPlace(lat,log);
                   //자신의 위치를 표시

               firstMark = true;
               //검색후 마크 이동을 방지.
           }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }


    private void invaliate(String inputText){

        List<Address> addressList = null;
        ArrayList<findItem> itemK= new ArrayList<>();

        Geocoder gc = new Geocoder(this , Locale. KOREAN );
        try {
            addressList = gc.getFromLocationName(inputText, 10);

            if (addressList != null) {
               //addressList.size();
                for (int i = 0; i < addressList.size(); i++) {
                    Address outAddr = addressList.get(i);
                    int addrCount = outAddr.getMaxAddressLineIndex() + 1;
                    StringBuffer outAddrStr = new StringBuffer();
                    for (int k = 0; k < addrCount; k++) {
                        outAddrStr.append(outAddr.getAddressLine(k));
                    }

                    itemK.add(new findItem(outAddrStr.toString(),""+outAddr.getLatitude(),""+outAddr.getLongitude()));
                    Log.d("item ", ""+outAddrStr.toString() );

                }


                adapter = new findViewAdapter(getApplicationContext(),itemK);


            }

        }catch (Exception e){

            e.printStackTrace();

        }


        list = (ListView) findViewById(R.id.list_view);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                TextView slat = (TextView) view.findViewById(R.id.lat);
                TextView slog = (TextView) view.findViewById(R.id.log);

                Double Lat = Double.valueOf(slat.getText().toString());
                Double Log = Double.valueOf(slog.getText().toString());


                lat = Lat;
                log = Log;

                setPlace(lat,log);
                Toast.makeText(getApplicationContext(), "알람 지점이 선택되었습니다.", Toast.LENGTH_SHORT).show();


            }
        });


    }
}
