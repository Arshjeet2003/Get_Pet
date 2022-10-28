package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.UUID;

public class PetsEditorActivity extends AppCompatActivity {

    private EditText animal_et;
    private EditText breed_et;
    private EditText age_et;
    private EditText size_et;
    private EditText gender_et;
    private ImageView pic_et;
    private Uri imagePath;
    private String url;
    private Boolean booleanUpdate;
    private String mKey;
    private User userData;
    private String petKey;

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
        url = "";

        Intent intent = getIntent();
        booleanUpdate = intent.getBooleanExtra("User_data",false);
        mKey = intent.getStringExtra("User_key");
        petKey = UUID.randomUUID().toString();

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

        pic_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });

        getUserDetails();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            imagePath = data.getData();
            getImageInImageView();
        }
    }

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
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progressDialog.setMessage(" Uploaded "+(int)progress+"%");
            }
        });
    }

    private void getUserDetails(){
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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

    private void savePetsData(){
        String key = UUID.randomUUID().toString();
        FirebaseDatabase.getInstance().getReference("PetsOfUsers/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/pet/"+key)
                .setValue(new Pets(petKey,key, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                        ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic()));

        FirebaseDatabase.getInstance().getReference("pet/"+key)
                .setValue(new Pets(petKey,key, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic()));

    }

    private void updatePetsData() {
        FirebaseDatabase.getInstance().getReference("PetsOfUsers/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/pet/"+mKey)
                .setValue(new Pets(petKey,mKey, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                        ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic()));

        FirebaseDatabase.getInstance().getReference("pet/"+mKey)
                .setValue(new Pets(petKey,mKey, animal_et.getText().toString(),breed_et.getText().toString(),
                        age_et.getText().toString(),size_et.getText().toString(),gender_et.getText().toString(),url
                        ,FirebaseAuth.getInstance().getCurrentUser().getUid(),userData.getName(),userData.getEmail(),userData.getProfilePic()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.save_menu,menu);
        return true;
    }

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
}