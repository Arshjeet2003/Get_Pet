package com.example.android.getpet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class profile extends AppCompatActivity {

    // variables for respective views
    private Button logout;
    private Button uploadImage;
    private ImageView imgProfile;
    private String mUrl;
    private Uri imagePath;   // global variable to store the image from gallery and then show it on profile photo icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // associating variables with views using corresponding id(s)
        logout = findViewById(R.id.logout_tv);
        uploadImage = findViewById(R.id.uploadImage_b);
        imgProfile = findViewById(R.id.profile_img);
        
        getDataForProfilePic();
        
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

        //Logout the user.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(profile.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                /* ".setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP"  to clear all existing activity during logout */
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We specify pick action for intent to take photo from gallery
                Intent photoIntent = new Intent(Intent.ACTION_PICK);

                //Specifying the type of intent (telling system to open gallery)
                photoIntent.setType("image/*");

                startActivityForResult(photoIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Get the image selected in gallery on icon
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){

            //Store image file in form of Uri type data
            imagePath=data.getData();
            try {
                getImageInImageView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Transform Uri (path) to Bitmap and put image in imageView.
    private void getImageInImageView() throws IOException {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //Surrounding the code with try catch block to handle exception (in case image not found!!!)
        imgProfile.setImageBitmap(bitmap);
    }

    private void UploadImage(){

        // Progress dialog to show the percentage of image uploaded while uploading image
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/"+ UUID.randomUUID().toString())
                .putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

            /*To upload photo to firebaseStorage and generating random Unique ID so that no two images have same ID
             .putFile associates the image having unique ID to our user */

            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //When uploading of photo is complete

                //If successfully uploaded then show image uploaded
                if(task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        //if task is successful download url of image from storage

                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {  //Downloading takes time

                            //If url downloaded successfully then call method having url as argument
                            if(task.isSuccessful()){
                                uploadProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                    Toast.makeText(profile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(profile.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss(); //on completion in either case dismiss process dialog

            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                //Calculating the percentage of image uploaded
                double progress = 100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount();

                //Setting up %age of image uploaded on progress dialogue
                progressDialog.setMessage(" Uploaded " + (int) progress + "%");
            }
        });
    }

    private void getDataForProfilePic() {
            FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 mUrl = snapshot.getValue(User.class).getProfilePic();
                Glide.with(getApplicationContext()).load(mUrl).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img)
                .into(imgProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    //Uploading profile picture of user.
    private void uploadProfilePicture(String url){
        /* After downloading image url from database we update it in realtime database by referencing using user UID(path).
         Path will be : user/Unique UID associated with user/profilePic */
        FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser()
                .getUid() + "/profilePic").setValue(url);

    }
}
