package com.example.android.getpet;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class PetDetailsActivity extends AppCompatActivity {

    private TextView animal_details_et;
    private TextView breed_details_et;
    private TextView age_details_et;
    private TextView size_details_et;
    private TextView gender_details_et;
    private TextView getLocation;
    private ImageView pic_details_et;
    private String imageUrl;
    private TextView chat;
    private String ownerName, ownerEmail, ownerPic, ownerKey, petLat, petLong;
    private String petKey ,petName;
    private String senderName, senderEmail, senderPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        animal_details_et = findViewById(R.id.animal_details_et);
        breed_details_et = findViewById(R.id.breed_details_et);
        age_details_et = findViewById(R.id.age_details_et);
        size_details_et = findViewById(R.id.size_details_et);
        gender_details_et = findViewById(R.id.gender_details_et);
        pic_details_et = findViewById(R.id.pic_details_iv);
        getLocation = findViewById(R.id.location_tv);
        chat = findViewById(R.id.message_tv);

        //Getting data from petList Activity.
        Intent intent = getIntent();
        petName = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_petName));
        animal_details_et.setText(petName);
        breed_details_et.setText(intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_breed)));
        age_details_et.setText(intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_age)));
        size_details_et.setText(intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_size)));
        gender_details_et.setText(intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_gender)));
        imageUrl = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_pic));
        ownerKey = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfileKey));
        ownerName = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerName));
        ownerEmail = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerEmail));
        ownerPic = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfilePic));
        petKey = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetKey));
        petLat = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLat));
        petLong = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLong));

        //Getting data about user from database.
        FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderName = snapshot.getValue(User.class).getName();
                senderEmail = snapshot.getValue(User.class).getEmail();
                senderPic = snapshot.getValue(User.class).getProfilePic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Using Glide library to put image of pet in imageView.
        if (imageUrl.isEmpty()) {
            Glide.with(getApplicationContext()).load(R.drawable.account_img).error(R.drawable.account_img)
                    .placeholder(R.drawable.account_img)
                    .into(pic_details_et);
        } else {
            Glide.with(getApplicationContext()).load(imageUrl).error(R.drawable.account_img)
                    .placeholder(R.drawable.account_img)
                    .into(pic_details_et);
        }

        //Chat option will only be available if it is someone else's pet not user's.
        if (ownerEmail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            chat.setVisibility(View.GONE);
        }

        //Sending data to ChatActivity.
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PetDetailsActivity.this, ChatActivity.class);
                intent1.putExtra("PetKey",petKey);
                intent1.putExtra("receiverName",ownerName);
                intent1.putExtra("receiverEmail",ownerEmail);
                intent1.putExtra("receiverProfilePic",ownerPic);
                intent1.putExtra("receiverKey",ownerKey);
                intent1.putExtra("senderName",senderName);
                intent1.putExtra("senderEmail",senderEmail);
                intent1.putExtra("senderPic",senderPic);
                intent1.putExtra("senderKey",FirebaseAuth.getInstance().getUid());
                startActivity(intent1);
            }
        });

        //Sending data to LocationActivity, Checking for permissions.
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Checking if we have permission for location.
                if(ContextCompat.checkSelfPermission(PetDetailsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                    sendIntentToLocationActivity();
                }
                else{
                    getPermissions();
                }
            }
        });
    }

    //Getting location permission using Dexter Api.
    private void getPermissions(){
        Dexter.withContext(PetDetailsActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        sendIntentToLocationActivity();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if(permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(PetDetailsActivity.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied." +
                                            "You need to allow the permission from settings")
                                    .setNegativeButton("Cancel",null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",getPackageName(),null));
                                        }
                                    }).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    //Sending data to LocationActivity
    private void sendIntentToLocationActivity(){
        Intent intent2 = new Intent(PetDetailsActivity.this,LocationActivity.class);
        intent2.putExtra("flagVal",true);
        intent2.putExtra(getResources().getString(R.string.LocationActivity_intent_latitudeData),petLat);
        intent2.putExtra(getResources().getString(R.string.LocationActivity_intent_longitudeData),petLong);
        intent2.putExtra(getResources().getString(R.string.LocationActivity_intent_PetNameData),petName);
        startActivity(intent2);
    }
}