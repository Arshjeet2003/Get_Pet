package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class petList extends Fragment {

    private EditText filterBreed;
    private EditText filterAnimal;
    private EditText filterSize;
    private EditText filterAge;
    private EditText filterGender;
    private TextView okay;
    private ArrayList<Pets> filteredPetList;
    private RecyclerView recyclerView;
    private ArrayList<Pets> pets;
    private ProgressBar progressBar;
    private PetsAdapter petsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    PetsAdapter.OnPetsClickListener onPetsClickListener;
    private FloatingActionButton floatingActionButton;

    private String senderName;
    private String senderEmail;
    private String senderPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_pet_list,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        filterSize = getView().findViewById(R.id.srchSize);
        filterAnimal = getView().findViewById(R.id.srchanimal);
        filterAge = getView().findViewById(R.id.srchAge);
        filterBreed = getView().findViewById(R.id.srchBreed);
        filterGender = getView().findViewById(R.id.srchGender);
        okay = getView().findViewById(R.id.okay_tv);
        progressBar = getView().findViewById(R.id.progressbar);
        pets = new ArrayList<>();
        filteredPetList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.recycler);
        floatingActionButton = getView().findViewById(R.id.fab);
        swipeRefreshLayout = getView().findViewById(R.id.swip);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String breed_data = filterBreed.getText().toString();
                String animal_data = filterAnimal.getText().toString();
                String age_data = filterAge.getText().toString();
                String size_data = filterSize.getText().toString();
                String gender_data = filterGender.getText().toString();


                for(Pets pet : pets){
                    if(breed_data.equals(pet.getBreed())){
                        filteredPetList.add(pet);
                    }
                }

                for(Pets pet : pets){
                    if(animal_data.equals(pet.getAnimal())){
                        filteredPetList.add(pet);
                    }
                }

                for(Pets pet : pets){
                    if(age_data.equals(pet.getAge())){
                        filteredPetList.add(pet);
                    }
                }
                for(Pets pet : pets){
                    if(size_data.equals(pet.getSize())){
                        filteredPetList.add(pet);

                    }
                }
                for(Pets pet : pets){
                    if(gender_data.equals(pet.getGender())){
                        filteredPetList.add(pet);
                    }
                }

                petsAdapter = new PetsAdapter(filteredPetList,getActivity(),onPetsClickListener);
                pets.clear();
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(petsAdapter);
            }
        });

        //Loading pets again on refreshing.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPets();
                filteredPetList.clear();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getPets();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),PetsEditorActivity.class));
            }
        });

        //Sending data to PetDetailsActivity.
        onPetsClickListener = new PetsAdapter.OnPetsClickListener() {
            @Override
            public void onPetsClicked(int position) {
                Intent intent = new Intent(getActivity(),PetDetailsActivity.class);
                intent.putExtra("keyData",pets.get(position).getKey());
                intent.putExtra("animal",pets.get(position).getAnimal());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerName),pets.get(position).getOwnerName());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerEmail),pets.get(position).getOwnerEmail());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfilePic),pets.get(position).getOwnerProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfileKey),pets.get(position).getOwnerKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_petName),pets.get(position).getAnimalName());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_breed),pets.get(position).getBreed());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_age),pets.get(position).getAge());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_size),pets.get(position).getSize());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_gender),pets.get(position).getGender());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_pic),pets.get(position).getProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetKey),pets.get(position).getKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLat),pets.get(position).getPetLat());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLong),pets.get(position).getPetLong());
                intent.putExtra("Address_Location",pets.get(position).getLocation());
                startActivity(intent);
            }
        };


    }

    //Getting all the pet details from the database.
    private void getPets(){
        pets.clear();
        try {
            FirebaseDatabase.getInstance().getReference("pet").addListenerForSingleValueEvent(new ValueEventListener() {
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
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }
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
            recyclerView.setBackgroundResource(R.drawable.no_pets_back4);
        }
    }
}
