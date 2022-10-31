package com.example.android.getpet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.example.android.getpet.databinding.ActivityLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityLocationBinding binding;
    private TextView setmyLoc;


    private Double mLat;
    private Double mLong;
    private String petName;

    boolean mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setmyLoc = findViewById(R.id.setloc_tv);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        mFlag = intent.getBooleanExtra("flagVal",false);
        if(mFlag){
            mLat = Double.parseDouble(intent.getStringExtra("petLat"));
            mLong = Double.parseDouble(intent.getStringExtra("petLong"));
            petName = intent.getStringExtra("petName");
        }
        else{
            setmyLoc.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(mFlag){
            LatLng PetLatLng = new LatLng(mLat, mLong);
            mMap.addMarker(new MarkerOptions().position(PetLatLng).title(petName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(PetLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PetLatLng,18));
        }
    }
}