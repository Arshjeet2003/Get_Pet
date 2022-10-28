package com.example.android.getpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PetDetailsActivity extends AppCompatActivity {

    private TextView animal_details_et;
    private TextView breed_details_et;
    private TextView age_details_et;
    private TextView size_details_et;
    private TextView gender_details_et;
    private ImageView pic_details_et;
    private String imageUrl;
    private TextView chat;
    private String ownerName, ownerEmail, ownerPic, ownerKey;
    private String petKey;
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
        chat = findViewById(R.id.message_tv);

        Intent intent = getIntent();
        animal_details_et.setText(intent.getStringExtra("animal"));
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
    }
}