package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.util.UUID;

public class PetsEditorActivity extends AppCompatActivity{

    private EditText animalName_et;
    private EditText animal_et;
    private EditText breed_et;
    private EditText age_et;
    private EditText size_et;
    private EditText gender_et;
    private ImageView pic_et;
    private TextView setLocation;
    private Uri imagePath;
    private String PetPicUrl;
    private Boolean booleanUpdate;
    private String mKey;
    private User userData;
    private String petKey;

    private Boolean booleanLocationData;
    private String mLat;
    private String mLong;
    private String LocAdd;
    private TextView petAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_editor);

        animalName_et = findViewById(R.id.animal_et);
        animal_et = findViewById(R.id.animal_);
        breed_et = findViewById(R.id.breed_et);
        age_et = findViewById(R.id.age_et);
        size_et = findViewById(R.id.size_et);
        gender_et = findViewById(R.id.gender_et);
        pic_et = findViewById(R.id.pic_iv);
        setLocation = findViewById(R.id.setLocation_tv);
        petAddress = findViewById(R.id.loc_tv);
        PetPicUrl = "";

        //Getting data from userPetList Activity.
        Intent intent = getIntent();
        booleanUpdate = intent.getBooleanExtra("User_data",false);
        petKey = UUID.randomUUID().toString();
        booleanLocationData = intent.getBooleanExtra("locationData",false);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(PetsEditorActivity.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if(booleanLocationData){
            LocAdd = intent.getStringExtra("locationAdd");
            mLat = intent.getStringExtra("latitudeData");
            mLong = intent.getStringExtra("longitudeData");
            animal_et.setText(intent.getStringExtra("animal_from_LocationActivity"));
            animalName_et.setText(intent.getStringExtra("animalName_from_LocationActivity"));
            breed_et.setText(intent.getStringExtra("breed_from_LocationActivity"));
            age_et.setText(intent.getStringExtra("age_from_LocationActivity"));
            size_et.setText(intent.getStringExtra("size_from_LocationActivity"));
            gender_et.setText(intent.getStringExtra("gender_from_LocationActivity"));
            PetPicUrl = intent.getStringExtra("petPic_from_LocationActivity");

            Glide.with(getApplicationContext()).load(PetPicUrl).error(R.drawable.account_img)
                    .placeholder(circularProgressDrawable)
                    .into(pic_et);

            mKey = intent.getStringExtra("key_from_LocationActivity");
            booleanUpdate = intent.getBooleanExtra("update_from_LocationActivity",false);
        }

        /*Checking if user wants to update pet's data or wants to add a new pet.
        If user wants to update the old data then set the old data into the textViews and imageViews.*/

        if(booleanUpdate  && !booleanLocationData){
            animal_et.setText(intent.getStringExtra("User_animal"));
            animalName_et.setText(intent.getStringExtra("User_animalName"));
            breed_et.setText(intent.getStringExtra("User_breed"));
            age_et.setText(intent.getStringExtra("User_age"));
            size_et.setText(intent.getStringExtra("User_size"));
            gender_et.setText(intent.getStringExtra("User_gender"));
            mLat = intent.getStringExtra("User_latitudeData");
            mLong = intent.getStringExtra("User_longitudeData");
            String UserPetImageUrl = intent.getStringExtra("User_pic");

            Glide.with(getApplicationContext()).load(UserPetImageUrl).error(R.drawable.account_img)
                    .placeholder(circularProgressDrawable)
                    .into(pic_et);

            PetPicUrl = UserPetImageUrl;
            mKey = intent.getStringExtra("User_key");
            LocAdd = intent.getStringExtra("Address_Loc");
        }

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
                Intent intent1 = new Intent(PetsEditorActivity.this,LocationActivity.class);
                intent1.putExtra("animalName",animalName_et.getText().toString());
                intent1.putExtra("animal",animal_et.getText().toString());
                intent1.putExtra("breed",breed_et.getText().toString());
                intent1.putExtra("age",age_et.getText().toString());
                intent1.putExtra("size",size_et.getText().toString());
                intent1.putExtra("gender",gender_et.getText().toString());
                intent1.putExtra("picture",PetPicUrl);
                intent1.putExtra("update_data",booleanUpdate);
                intent1.putExtra("key",mKey);
                startActivity(intent1);
            }
        });
        petAddress.setText(LocAdd);
        getUserDetails();
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
            uploadImage();
    }

    private void uploadImage(){
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
                                PetPicUrl = task.getResult().toString();
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
        try {
            FirebaseDatabase.getInstance().getReference("user's_pet/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/pet/" + key)
                    .setValue(new Pets(petKey, key, animalName_et.getText().toString(), animal_et.getText().toString(), breed_et.getText().toString(),
                            age_et.getText().toString(), size_et.getText().toString(), gender_et.getText().toString(), PetPicUrl
                            , FirebaseAuth.getInstance().getCurrentUser().getUid(), userData.getName(), userData.getEmail(), userData.getProfilePic()
                            , mLat, mLong,LocAdd));

            FirebaseDatabase.getInstance().getReference("pet/" + key)
                    .setValue(new Pets(petKey, key, animalName_et.getText().toString(), animal_et.getText().toString(), breed_et.getText().toString(),
                            age_et.getText().toString(), size_et.getText().toString(), gender_et.getText().toString(), PetPicUrl
                            , FirebaseAuth.getInstance().getCurrentUser().getUid(), userData.getName(), userData.getEmail(), userData.getProfilePic(),
                            mLat, mLong,LocAdd));
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "Your pet has been added.", Toast.LENGTH_SHORT).show();
    }

    //Updating the already created pet data.
    private void updatePetsData() {
        try {
            FirebaseDatabase.getInstance().getReference("user's_pet/" + FirebaseAuth.getInstance().getUid() + "/pet/" + mKey)
                    .setValue(new Pets(petKey, mKey, animalName_et.getText().toString(), animal_et.getText().toString(), breed_et.getText().toString(),
                            age_et.getText().toString(), size_et.getText().toString(), gender_et.getText().toString(), PetPicUrl
                            , FirebaseAuth.getInstance().getCurrentUser().getUid(), userData.getName(), userData.getEmail(), userData.getProfilePic(),
                            mLat, mLong,LocAdd));

            FirebaseDatabase.getInstance().getReference("pet/" + mKey)
                    .setValue(new Pets(petKey, mKey, animalName_et.getText().toString(), animal_et.getText().toString(), breed_et.getText().toString(),
                            age_et.getText().toString(), size_et.getText().toString(), gender_et.getText().toString(), PetPicUrl
                            , FirebaseAuth.getInstance().getCurrentUser().getUid(), userData.getName(), userData.getEmail(), userData.getProfilePic(),
                            mLat, mLong,LocAdd));
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
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
            if(booleanUpdate && checkData()){
                updatePetsData();
                startActivity(new Intent(PetsEditorActivity.this,allListsActivity.class));
            }
            else if(checkData()){
                savePetsData();
                startActivity(new Intent(PetsEditorActivity.this,allListsActivity.class));
            }
        }
        if(item.getItemId()==R.id.menu_item_del){
            if(booleanUpdate){
                deletePetsData();
            }
            else{
                startActivity(new Intent(PetsEditorActivity.this,allListsActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkData() {
        if(animalName_et.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter pet name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(animal_et.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter pet type.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(breed_et.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter pet breed.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(gender_et.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter pet gender.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(size_et.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter pet size.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(age_et.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter pet age.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(PetPicUrl.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please upload pet picture.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mLat.isEmpty() || mLong.isEmpty() || LocAdd.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please set pet location.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void deletePetsData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PetsEditorActivity.this);
        builder.setTitle("Delete Pet")
                .setIcon(R.drawable.delete_icon1)
                .setMessage("Do you want to delete this pet?")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference("user's_pet/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/pet")
                                .child(mKey).removeValue();
                        FirebaseDatabase.getInstance().getReference("pet")
                                .child(mKey).removeValue();
                        Toast.makeText(getApplicationContext(), "Pet Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PetsEditorActivity.this,allListsActivity.class));
                    }
                }).show();
    }

    //Getting Location permissions using Dexter Api.
    private void getPermissions(){
        Dexter.withContext(PetsEditorActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

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