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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Pets> pets;
    private ProgressBar progressBar;
    private PetsAdapter petsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    PetsAdapter.OnPetsClickListener onPetsClickListener;
    private TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerView = findViewById(R.id.fav_recycler);
        progressBar = findViewById(R.id.fav_progressbar);
        swipeRefreshLayout = findViewById(R.id.fav_swip);
        noData = findViewById(R.id.fav_NoData);

        pets = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFavPets();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getFavPets();

        //Sending data to PetDetailsActivity.
        onPetsClickListener = new PetsAdapter.OnPetsClickListener() {
            @Override
            public void onPetsClicked(int position) {
                Intent intent = new Intent(FavouritesActivity.this, PetDetailsActivity.class);
                intent.putExtra("keyData", pets.get(position).getKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerName), pets.get(position).getOwnerName());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerEmail), pets.get(position).getOwnerEmail());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfilePic), pets.get(position).getOwnerProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfileKey), pets.get(position).getOwnerKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_petName), pets.get(position).getAnimal());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_breed), pets.get(position).getBreed());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_age), pets.get(position).getAge());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_size), pets.get(position).getSize());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_gender), pets.get(position).getGender());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_pic), pets.get(position).getProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetKey), pets.get(position).getKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLat), pets.get(position).getPetLat());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLong), pets.get(position).getPetLong());
                startActivity(intent);
            }
        };

    }

    //Getting favourite pet of user from database.
    private void getFavPets() {
        pets.clear();
        FirebaseDatabase.getInstance().getReference("favourites/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    pets.add(dataSnapshot.getValue(Pets.class));
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Setting up the adapter to show the list of pets in the arraylist.
    private void setAdapter(){
        petsAdapter = new PetsAdapter(pets,FavouritesActivity.this,onPetsClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavouritesActivity.this));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(petsAdapter);

        //If no pets then show the noData textView.
        if(petsAdapter.getItemCount()==0){
            noData.setVisibility(View.VISIBLE);
        }
        else{
            noData.setVisibility(View.GONE);
        }
    }
}