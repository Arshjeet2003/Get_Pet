package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PetsEditorActivity extends AppCompatActivity{

    private EditText animal_et;
    private EditText breed_et;
    private EditText age_et;
    private EditText size_et;
    private EditText gender_et;
    private ImageView pic_et;
    private TextView setLocation;
    private Uri imagePath;
    private String url;
    private Boolean booleanUpdate;
    private String mKey;
    private User userData;
    private String petKey;
    private String mLat;
    private String mLong;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_editor);

        animal_et = findViewById(R.id.animal_et);
        breed_et = findViewById(R.id.breed_et);
        age_et = findViewById(R.id.age_et);
        size_et = findViewById(R.id.size_et);
        gender_et = findViewById(R.id.gender_et);
        pic_et = findViewById(R.id.pic_iv);
        setLocation = findViewById(R.id.setLocation_tv);
        url = "";

        //Getting data from userPetList Activity.
        Intent intent = getIntent();
        booleanUpdate = intent.getBooleanExtra("User_data",false);
        mKey = intent.getStringExtra("User_key");
        petKey = UUID.randomUUID().toString();

        /*Checking if user wants to update pet's data or wants to add a new pet.
        If user wants to update the old data then set the old data into the textViews and imageViews.*/

        if(booleanUpdate){
            animal_et.setText(intent.getStringExtra("User_animal"));
            breed_et.setText(intent.getStringExtra("User_breed"));
            age_et.setText(intent.getStringExtra("User_age"));
            size_et.setText(intent.getStringExtra("User_size"));
            gender_et.setText(intent.getStringExtra("User_gender"));

            String UserPetImageUrl = intent.getStringExtra("User_pic");
            Glide.with(getApplicationContext()).load(UserPetImageUrl).error(R.drawable.account_img)
                    .placeholder(R.drawable.account_img)
                    .into(pic_et);
            url = UserPetImageUrl;
            petKey = intent.getStringExtra("petKey");
        }

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Select the picture from internal storage that you want to upload.
        pic_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });

        //Getting permission for user's location.
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
            }
        });

        getUserDetails();

    }


    //Getting user's location.
    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if(location!=null){
                    //Initialize geoCoder
                    Geocoder geocoder = new Geocoder(PetsEditorActivity.this, Locale.getDefault());
                    //Initialize address List
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        mLat = String.valueOf(addresses.get(0).getLatitude());
                        mLong = String.valueOf(addresses.get(0).getLongitude());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    //Getting mobile path for image you have uploaded.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            imagePath = data.getData();
            getImageInImageView();
        }
    }

    //Putting image in imageView and uploading to Firebase Storage.
    private void getImageInImageView(){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pic_et.setImageBitmap(bitmap);


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(" Uploading... ");
        progressDialog.show();

        //Put image in Firebase storage.
        FirebaseStorage.getInstance().getReference("petImages/"+ UUID.randomUUID().toString())
                .putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                //task.getResult().toString() Contains the url of the pet picture
                                url = task.getResult().toString();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            //Shows the progress of upload.
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progressDialog.setMessage(" Uploaded "+(int)progress+"%");
            }
        });
    }

    //Getting user details.
    private void getUserDetails(){
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                                userData = dataSnapshot.getValue(User.class);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Saving the new pet data into the database.
    private void savePetsData(){
        String key = UUID.randomUUID().toString();
        FirebaseDatabase.getInstance().getReference("user's_pet/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/pet/"+key)
                .setValue(new Pets(petKey,key, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                        ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic()
                ,mLat,mLong));

        FirebaseDatabase.getInstance().getReference("pet/"+key)
                .setValue(new Pets(petKey,key, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic(),
                        mLat,mLong));

    }

    //Updating the already created pet data.
    private void updatePetsData() {
        FirebaseDatabase.getInstance().getReference("user's_pet/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/pet/"+mKey)
                .setValue(new Pets(petKey,mKey, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                        ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic(),
                        mLat,mLong));

        FirebaseDatabase.getInstance().getReference("pet/"+mKey)
                .setValue(new Pets(petKey,mKey, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                        ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic(),
                        mLat,mLong));

    }

    //Inflating the menu options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.editor_activity_menu,menu);
        return true;
    }

    //Setting what happens when any menu item is clicked.
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_item_save){
            if(booleanUpdate){
                updatePetsData();
            }
            else{
                savePetsData();
            }
            startActivity(new Intent(PetsEditorActivity.this,petList.class));
        }
        return super.onOptionsItemSelected(item);
    }

    //Getting Location permissions using Dexter Api.
    private void getPermissions(){
        Dexter.withContext(PetsEditorActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getLocation();
                        Toast.makeText(getApplicationContext(),"Your Location has been set",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if(permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(PetsEditorActivity.this);
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
}