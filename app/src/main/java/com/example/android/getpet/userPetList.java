package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class userPetList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Pets> User_pets;
    private ProgressBar progressBar;
    private UserPetAdapter mUserPetsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    UserPetAdapter.OnUserPetsClickListener onUserPetsClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pet_list);

        progressBar = findViewById(R.id.User_progressbar);
        User_pets = new ArrayList<>();
        recyclerView = findViewById(R.id.User_recycler);
        swipeRefreshLayout = findViewById(R.id.User_swip);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserPets();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        onUserPetsClickListener = new UserPetAdapter.OnUserPetsClickListener() {
            @Override
            public void onUserPetsClicked(int position) {
                Intent intent = new Intent(userPetList.this,PetsEditorActivity.class);
                intent.putExtra("User_data",true);
                intent.putExtra("User_key", User_pets.get(position).getKey());
                intent.putExtra("User_animal",User_pets.get(position).getAnimal());
                intent.putExtra("User_breed",User_pets.get(position).getBreed());
                intent.putExtra("User_age",User_pets.get(position).getAge());
                intent.putExtra("User_size",User_pets.get(position).getSize());
                intent.putExtra("User_gender",User_pets.get(position).getGender());
                intent.putExtra("User_pic",User_pets.get(position).getProfilePic());
                intent.putExtra("PetKey",User_pets.get(position).getPetKey());
                startActivity(intent);
            }
        };
        getUserPets();
    }

    private void getUserPets(){
        User_pets.clear();
        FirebaseDatabase.getInstance().getReference("PetsOfUsers/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/pet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User_pets.add(dataSnapshot.getValue(Pets.class));
                }
                mUserPetsAdapter = new UserPetAdapter(User_pets,userPetList.this,onUserPetsClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(userPetList.this));
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(mUserPetsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}