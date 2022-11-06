package com.example.android.getpet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.example.android.getpet.databinding.ActivityLocationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener
 ,GoogleMap.OnCameraMoveListener ,GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener{

    private GoogleMap mMap;
    private ActivityLocationBinding binding;
    private TextView setmyLoc;
    private TextView locData;
    private ImageView locPin;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Double mLat;
    private Double mLong;
    private String locAdd;

    private String petName;
    private String animal;
    private String petBreed;
    private String petSize;
    private String petGender;
    private String petAge;
    private String petImage;

    boolean mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setmyLoc = findViewById(R.id.setloc_tv);
        locData = findViewById(R.id.locationData_tv);
        locPin = findViewById(R.id.mapPin);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //Getting data from the PetDetailsActivity
        Intent intent = getIntent();
        mFlag = intent.getBooleanExtra("flagVal",false);
        if(mFlag){
            mLat = Double.parseDouble(intent.getStringExtra(getResources().getString(R.string.LocationActivity_intent_latitudeData)));
            mLong = Double.parseDouble(intent.getStringExtra(getResources().getString(R.string.LocationActivity_intent_longitudeData)));
            petName = intent.getStringExtra(getResources().getString(R.string.LocationActivity_intent_PetNameData));
            setmyLoc.setVisibility(View.GONE);
        }
        else{
            locPin.setVisibility(View.VISIBLE);
            locData.setVisibility(View.VISIBLE);
            setmyLoc.setVisibility(View.VISIBLE);
            petName = intent.getStringExtra("animalName");
            animal = intent.getStringExtra("animal");
            petAge = intent.getStringExtra("breed");
            petSize = intent.getStringExtra("age");
            petGender = intent.getStringExtra("size");
            petBreed = intent.getStringExtra("gender");
            petImage = intent.getStringExtra("picture");
        }

        setmyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(LocationActivity.this,PetsEditorActivity.class);
                intent2.putExtra("locationData",true);
                intent2.putExtra("locationAdd",locAdd);
                intent2.putExtra("latitudeData",String.valueOf(mLat));
                intent2.putExtra("longitudeData",String.valueOf(mLong));
                intent2.putExtra("animalName_from_LocationActivity",petName);
                intent2.putExtra("animal_from_LocationActivity",animal);
                intent2.putExtra("breed_from_LocationActivity",petBreed);
                intent2.putExtra("age_from_LocationActivity",petAge);
                intent2.putExtra("size_from_LocationActivity",petSize);
                intent2.putExtra("gender_from_LocationActivity",petGender);
                intent2.putExtra("petPic_from_LocationActivity",petImage);
                startActivity(intent2);
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);

        //Setting up location of pet to be seen by user.
        if(mFlag){
            LatLng PetLatLng = new LatLng(mLat, mLong);
            mMap.addMarker(new MarkerOptions().position(PetLatLng).title(petName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(PetLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PetLatLng,18));
        }
        else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialize location
                    Location location = task.getResult();
                    if (location != null) {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                        //Initialize address List
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            mLat = addresses.get(0).getLatitude();
                            mLong = addresses.get(0).getLongitude();
                            LatLng currentLocationLatLng = new LatLng(mLat, mLong);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocationLatLng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 18));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
         Geocoder geocoder = new Geocoder(this, Locale.getDefault());
         List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            setAddress(addresses.get(0));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private void setAddress(Address address){
        String addressLine1=null,addressLine2 = null;
        if(address!=null){
            if(address.getAddressLine(0)!=null){
                addressLine1 = address.getAddressLine(0);
            }
            if(address.getAddressLine(1)!=null){
                addressLine2 = address.getAddressLine(1);
            }
            locAdd = addressLine1+addressLine2;
            locData.setText(locAdd);
        }
    }

    @Override
    public void onCameraIdle() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude,1);
            mLat = addresses.get(0).getLatitude();
            mLong = addresses.get(0).getLongitude();
            setAddress(addresses.get(0));
        } catch (IOException | IndexOutOfBoundsException e) {
            Toast.makeText(getApplicationContext(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    //Getting user's location.
    @SuppressLint("MissingPermission")
    private void getLocation() {

    }
}