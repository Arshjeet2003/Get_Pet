package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PetDetailsActivity extends AppCompatActivity {

    private TextView animal_details_et;
    private TextView breed_details_et;
    private TextView age_details_et;
    private TextView size_details_et;
    private TextView gender_details_et;
    private ImageView pic_details_et;
    private String imageUrl;

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

        Intent intent = getIntent();
        animal_details_et.setText(intent.getStringExtra("animal"));
        breed_details_et.setText(intent.getStringExtra("breed"));
        age_details_et.setText(intent.getStringExtra("age"));
        size_details_et.setText(intent.getStringExtra("size"));
        gender_details_et.setText(intent.getStringExtra("gender"));
        imageUrl = intent.getStringExtra("pic");

        if(imageUrl.isEmpty()){
            Glide.with(getApplicationContext()).load(R.drawable.account_img).error(R.drawable.account_img)
                    .placeholder(R.drawable.account_img)
                    .into(pic_details_et);
        }
        else{
            Glide.with(getApplicationContext()).load(imageUrl).error(R.drawable.account_img)
                    .placeholder(R.drawable.account_img)
                    .into(pic_details_et);
        }
    }
}