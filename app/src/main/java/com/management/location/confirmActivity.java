package com.management.location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class confirmActivity extends FragmentActivity implements OnMapReadyCallback{


    GoogleMap mMap;

    Double lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con);


        lat = getIntent().getDoubleExtra("lat",0);
        lon = getIntent().getDoubleExtra("lon",0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);







    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //지도가 보이면 호출.
        mMap = googleMap;

        setPlace(lat,lon);

    }


    private void setPlace(Double lat, Double log) {


        if(lat != null && log != null){

            //마커 표시.
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title("선택 위치")).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, log), 17));

        }


    }

}
