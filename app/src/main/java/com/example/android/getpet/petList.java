package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class petList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Pets> pets;
    private ProgressBar progressBar;
    private PetsAdapter petsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    PetsAdapter.OnPetsClickListener onPetsClickListener;
    private FloatingActionButton floatingActionButton;
    private TextView noData;

    private String senderName;
    private String senderEmail;
    private String senderPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);

        progressBar = findViewById(R.id.progressbar);
        pets = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        floatingActionButton = findViewById(R.id.fab);
        swipeRefreshLayout = findViewById(R.id.swip);
        noData = findViewById(R.id.Nodata_tv);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPets();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getPets();
        getUserData();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(petList.this,PetsEditorActivity.class));
            }
        });

        onPetsClickListener = new PetsAdapter.OnPetsClickListener() {
            @Override
            public void onPetsClicked(int position) {
                Intent intent = new Intent(petList.this,PetDetailsActivity.class);
                intent.putExtra("OwnerName",pets.get(position).getOwnerName());
                intent.putExtra("OwnerEmail",pets.get(position).getOwnerEmail());
                intent.putExtra("OwnerProfilePic",pets.get(position).getOwnerProfilePic());
                intent.putExtra("OwnerProfileKey",pets.get(position).getOwnerKey());
                intent.putExtra("animal",pets.get(position).getAnimal());
                intent.putExtra("breed",pets.get(position).getBreed());
                intent.putExtra("age",pets.get(position).getAge());
                intent.putExtra("size",pets.get(position).getSize());
                intent.putExtra("gender",pets.get(position).getGender());
                intent.putExtra("pic",pets.get(position).getProfilePic());
                intent.putExtra("PetKey",pets.get(position).getKey());
                intent.putExtra("PetLat",pets.get(position).getPetLat());
                intent.putExtra("PetLong",pets.get(position).getPetLong());
                startActivity(intent);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_item_profile){
            startActivity(new Intent(petList.this,profile.class));
        }
        if(item.getItemId()==R.id.menu_item_mypetlist){
            startActivity(new Intent(petList.this,userPetList.class));
        }
        if(item.getItemId()==R.id.menu_item_chats){
            startActivity(new Intent(petList.this,menuChats.class));
        }
        if(item.getItemId()==R.id.menu_item_globalChat){
            Intent intent = new Intent(petList.this,GlobalChatActivity.class);
            intent.putExtra("sender_name",senderName);
            intent.putExtra("sender_email",senderEmail);
            intent.putExtra("sender_pic",senderPic);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPets(){
        pets.clear();
        FirebaseDatabase.getInstance().getReference("pet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    pets.add(dataSnapshot.getValue(Pets.class));
                }
                petsAdapter = new PetsAdapter(pets,petList.this,onPetsClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(petList.this));
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(petsAdapter);
                if(petsAdapter.getItemCount()==0){
                    noData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData() {

        FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                senderName = snapshot.getValue(User.class).getName();
                senderEmail = snapshot.getValue(User.class).getEmail();
                senderPic = snapshot.getValue(User.class).getProfilePic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}