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

        Intent intent = getIntent();
        petName = intent.getStringExtra("animal");
        animal_details_et.setText(petName);
        breed_details_et.setText(intent.getStringExtra("breed"));
        age_details_et.setText(intent.getStringExtra("age"));
        size_details_et.setText(intent.getStringExtra("size"));
        gender_details_et.setText(intent.getStringExtra("gender"));
        imageUrl = intent.getStringExtra("pic");
        ownerKey = intent.getStringExtra("OwnerProfileKey");
        ownerName = intent.getStringExtra("OwnerName");
        ownerEmail = intent.getStringExtra("OwnerEmail");
        ownerPic = intent.getStringExtra("OwnerProfilePic");
        petKey = intent.getStringExtra("PetKey");
        petLat = intent.getStringExtra("PetLat");
        petLong = intent.getStringExtra("PetLong");

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

        if (imageUrl.isEmpty()) {
            Glide.with(getApplicationContext()).load(R.drawable.account_img).error(R.drawable.account_img)
                    .placeholder(R.drawable.account_img)
                    .into(pic_details_et);
        } else {
            Glide.with(getApplicationContext()).load(imageUrl).error(R.drawable.account_img)
                    .placeholder(R.drawable.account_img)
                    .into(pic_details_et);
        }

        if (ownerEmail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            chat.setVisibility(View.GONE);
        }

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

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void sendIntentToLocationActivity(){
        Intent intent2 = new Intent(PetDetailsActivity.this,LocationActivity.class);
        intent2.putExtra("flagVal",true);
        intent2.putExtra("petLat",petLat);
        intent2.putExtra("petLong",petLong);
        intent2.putExtra("petName",petName);
        startActivity(intent2);
    }
}