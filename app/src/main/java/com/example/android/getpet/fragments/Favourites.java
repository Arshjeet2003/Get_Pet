package com.example.android.getpet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.getpet.activities.PetDetailsActivity;
import com.example.android.getpet.models.Pets;
import com.example.android.getpet.adapters.PetsAdapter;
import com.example.android.getpet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favourites extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Pets> pets;
    private ProgressBar progressBar;
    private PetsAdapter petsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    PetsAdapter.OnPetsClickListener onPetsClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_favourites,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getView().findViewById(R.id.fav_recycler);
        progressBar = getView().findViewById(R.id.fav_progressbar);
        swipeRefreshLayout = getView().findViewById(R.id.fav_swip);

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
                Intent intent = new Intent(getActivity(), PetDetailsActivity.class);
                intent.putExtra("keyData", pets.get(position).getKey());
                intent.putExtra("animal",pets.get(position).getAnimal());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerName), pets.get(position).getOwnerName());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerEmail), pets.get(position).getOwnerEmail());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfilePic), pets.get(position).getOwnerProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfileKey), pets.get(position).getOwnerKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_petName), pets.get(position).getAnimalName());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_breed), pets.get(position).getBreed());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_age), pets.get(position).getAge());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_size), pets.get(position).getSize());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_gender), pets.get(position).getGender());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_pic), pets.get(position).getProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetKey), pets.get(position).getKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLat), pets.get(position).getPetLat());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLong), pets.get(position).getPetLong());
                intent.putExtra("Address_Location",pets.get(position).getLocation());
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
        petsAdapter = new PetsAdapter(pets,getActivity(),onPetsClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(petsAdapter);

        //If no pets then show the noData textView.
        if(petsAdapter.getItemCount()==0){
            recyclerView.setBackgroundResource(R.drawable.no_fav_pets_back4);
        }
        else{
            recyclerView.setBackgroundResource(0);
        }
    }
}