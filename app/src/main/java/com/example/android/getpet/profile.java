package com.example.android.getpet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    private Uri imagepath;   // global variable to store the image from gallery and then show it on profile photo icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // associating variables with views using corresponding id(s)
        logout = findViewById(R.id.logout_tv);
        uploadImage = findViewById(R.id.uploadImage_b);
        imgProfile = findViewById(R.id.profile_img);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

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
                Intent photoIntent = new Intent(Intent.ACTION_PICK);//we specify pick action for intent to take photo from gallery
                photoIntent.setType("image/*"); //specifying the type of intent (telling system to open gallery)
                startActivityForResult(photoIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null){   //get the image selected in gallery on icon
            imagepath=data.getData();//Store image file in form of Uri type data
            try {
                getImageInImageView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getImageInImageView() throws IOException {   //transform Uri to bitmap
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagepath);
            // transforming uri to bitmap
        }
        catch (IOException e){
            e.printStackTrace();
        }
        // surrounding the code with try catch block to handle exception (in case image not found!!!)
        imgProfile.setImageBitmap(bitmap);
    }

    private void UploadImage(){

        // Progress dialog to show the percentage of image uploaded while uploading image
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/"+ UUID.randomUUID().toString())
                .putFile(imagepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

            /* to upload photo to firebaseStorage and generating random Unique ID so that no two images have same ID
             .putFile associates the image having unique ID to our user */

            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //when uploading of photo is complete
                if(task.isSuccessful()) {        // if successfully uploaded then show image uploaded
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        //if task is successful download url of image from storage

                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {  //downloading takes time
                            if(task.isSuccessful()){ // if url downloaded successfully then call method having url as argment
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
                double progress = 100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                // Calculating the percentage of image uploaded

                progressDialog.setMessage(" Uploaded " + (int) progress + "%");
                //  setting up %age of image uploaded on progress dialogue
            }
        });
    }


    private void uploadProfilePicture(String url){
        /* After downloading image url from database we update it in realtime database by referencing using user UID(path).
         Path will be : user + / + Unique UID associated with user + / + profilePicture */
        FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser()
                .getUid() + "/profilePic").setValue(url);

    }
}