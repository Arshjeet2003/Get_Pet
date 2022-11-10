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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class petList extends Fragment{

    private ArrayList<Pets> pets;

    private SearchView searchBreed;
    private Spinner spinner;
    private TextView mFilterMale;
    private TextView mFilterFemale;
    private TextView mFilterDogs;
    private TextView mFilterCats;

    private ArrayList<Pets> mSortByAge;
    private ArrayList<Pets> mSortBySize;
    private ArrayList<Pets> filteredDogsList;
    private ArrayList<Pets> filteredCatsList;
    private ArrayList<Pets> filteredMaleList;
    private ArrayList<Pets> filteredFemaleList;
    private String filtersUsed[] = {"Sort By", "Size" , "Age"};

    private RecyclerView recyclerView;

    private ProgressBar progressBar;
    private PetsAdapter petsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    PetsAdapter.OnPetsClickListener onPetsClickListener;
    private FloatingActionButton floatingActionButton;

    private boolean dogFilterUpdate;
    private boolean catFilterUpdate;
    private boolean maleFilterUpdate;
    private boolean femaleFilterUpdate;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_pet_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFilterMale = getView().findViewById(R.id.filterMale_tv);
        mFilterFemale = getView().findViewById(R.id.filterFemale_tv);
        mFilterCats = getView().findViewById(R.id.filterCat_tv);
        mFilterDogs = getView().findViewById(R.id.filterDog_tv);
        spinner = getView().findViewById(R.id.spinner1);
        searchBreed = getView().findViewById(R.id.srchBreed);

        filteredDogsList = new ArrayList<>();
        filteredCatsList = new ArrayList<>();
        filteredMaleList = new ArrayList<>();
        filteredFemaleList = new ArrayList<>();
        mSortByAge = new ArrayList<>();
        mSortBySize = new ArrayList<>();
        pets = new ArrayList<>();

        dogFilterUpdate=true;
        catFilterUpdate=true;
        maleFilterUpdate=true;
        femaleFilterUpdate=true;

        progressBar = getView().findViewById(R.id.progressbar);
        recyclerView = getView().findViewById(R.id.recycler);
        floatingActionButton = getView().findViewById(R.id.fab);
        swipeRefreshLayout = getView().findViewById(R.id.swip);

        searchBreed.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                petsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, filtersUsed);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 1){
                    mSortBySize.clear();
                    mSortBySize.addAll(pets);
                    Collections.sort(mSortBySize, new Comparator<Pets>() {
                        @Override
                        public int compare(Pets o1, Pets o2) {
                            return o1.getSize().compareTo(o2.getSize());
                        }
                    });
                    setAdapter(mSortBySize);
                }

                if(position == 2){
                    mSortByAge.clear();
                    mSortByAge.addAll(pets);
                    Collections.sort(mSortByAge, new Comparator<Pets>() {
                        @Override
                        public int compare(Pets o1, Pets o2) {
                            return o1.getAge().compareTo(o2.getAge());
                        }
                    });
                    setAdapter(mSortByAge);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mFilterDogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dogFilterUpdate){
                    readyForFilter();
                    mFilterDogs.setBackgroundResource(R.drawable.round);
                    for(Pets pet : pets){
                        if(pet.getAnimal().toLowerCase().trim().equals("dog")){
                            filteredDogsList.add(pet);
                        }
                    }
                    setAdapter(filteredDogsList);
                    dogFilterUpdate=false;
                    if(filteredDogsList.size()==0){
                        recyclerView.setBackgroundResource(R.drawable.no_pets_back4);
                    }
                    else{
                        recyclerView.setBackgroundResource(0);
                    }
                }
                else{
                    readyForFilter();
                    mFilterCats.setBackgroundResource(0);
                    mFilterDogs.setBackgroundResource(0);
                    mFilterFemale.setBackgroundResource(0);
                    mFilterMale.setBackgroundResource(0);
                    spinner.setSelection(0);
                    dogFilterUpdate=true;
                    catFilterUpdate=true;
                    maleFilterUpdate=true;
                    femaleFilterUpdate=true;
                    setAdapter(pets);
                }
            }
        });

        mFilterCats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(catFilterUpdate){
                    readyForFilter();
                    mFilterCats.setBackgroundResource(R.drawable.round);
                    for(Pets pet : pets){
                        if(pet.getAnimal().toLowerCase().trim().equals("cat")){
                            filteredCatsList.add(pet);
                        }
                    }
                    setAdapter(filteredCatsList);
                    if(filteredCatsList.size()==0){
                        recyclerView.setBackgroundResource(R.drawable.no_pets_back4);
                    }
                    else{
                        recyclerView.setBackgroundResource(0);
                    }
                    catFilterUpdate=false;
                }
                else{
                    readyForFilter();
                    mFilterCats.setBackgroundResource(0);
                    mFilterDogs.setBackgroundResource(0);
                    mFilterFemale.setBackgroundResource(0);
                    mFilterMale.setBackgroundResource(0);
                    dogFilterUpdate=true;
                    catFilterUpdate=true;
                    maleFilterUpdate=true;
                    femaleFilterUpdate=true;
                    setAdapter(pets);
                }
            }
        });

        mFilterMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maleFilterUpdate){
                    readyForFilter();
                    mFilterMale.setBackgroundResource(R.drawable.round);
                    for(Pets pet : pets){
                        if(pet.getGender().toLowerCase().trim().equals("male")){
                            filteredMaleList.add(pet);
                        }
                        else{
                            recyclerView.setBackgroundResource(0);
                        }
                    }
                    setAdapter(filteredMaleList);
                    if(filteredMaleList.size()==0){
                        recyclerView.setBackgroundResource(R.drawable.no_pets_back4);
                    }
                    else{
                        recyclerView.setBackgroundResource(0);
                    }
                    maleFilterUpdate=false;
                }
                else{
                    readyForFilter();
                    spinner.setSelection(0);
                    maleFilterUpdate=true;
                    dogFilterUpdate=true;
                    catFilterUpdate=true;
                    femaleFilterUpdate=true;
                    setAdapter(pets);
                }
            }
        });

        mFilterFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(femaleFilterUpdate){
                    readyForFilter();
                    mFilterFemale.setBackgroundResource(R.drawable.round);
                    for(Pets pet : pets){
                        if(pet.getGender().toLowerCase().trim().equals("female")){
                            filteredFemaleList.add(pet);
                        }
                    }
                    if(filteredFemaleList.size()==0){
                        recyclerView.setBackgroundResource(R.drawable.no_pets_back4);
                    }
                    else{
                        recyclerView.setBackgroundResource(0);
                    }
                    setAdapter(filteredFemaleList);
                    femaleFilterUpdate=false;
                }
                else{
                    readyForFilter();
                    spinner.setSelection(0);
                    femaleFilterUpdate=true;
                    dogFilterUpdate=true;
                    catFilterUpdate=true;
                    maleFilterUpdate=true;
                    setAdapter(pets);
                }
            }
        });

        //Loading pets again on refreshing.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readyForFilter();

                spinner.setSelection(0);
                dogFilterUpdate=true;
                catFilterUpdate=true;
                maleFilterUpdate=true;
                femaleFilterUpdate=true;
                getPets();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getPets();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PetsEditorActivity.class));
            }
        });

        //Sending data to PetDetailsActivity.
        onPetsClickListener = new PetsAdapter.OnPetsClickListener() {
            @Override
            public void onPetsClicked(int position) {
                Intent intent = new Intent(getActivity(), PetDetailsActivity.class);
                intent.putExtra("keyData", pets.get(position).getKey());
                intent.putExtra("animal", pets.get(position).getAnimal());
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
                intent.putExtra("Address_Location", pets.get(position).getLocation());
                intent.putExtra("pet_description", pets.get(position).getDescription());
                startActivity(intent);
            }
        };


    }

    //Getting all the pet details from the database.
    private void getPets() {
        pets.clear();
        try {
            FirebaseDatabase.getInstance().getReference("pet").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        pets.add(dataSnapshot.getValue(Pets.class));
                    }
                    setAdapter(pets);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    //Setting up the adapter to show the list of pets in the arraylist.
    private void setAdapter(ArrayList<Pets> arrayList) {
        petsAdapter = new PetsAdapter(arrayList, getActivity(), onPetsClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(petsAdapter);
        petsAdapter.notifyDataSetChanged();

        //If no pets then show the noData textView.
        if (petsAdapter.getItemCount() == 0) {
            recyclerView.setBackgroundResource(R.drawable.no_pets_back4);
        }
        else{
            recyclerView.setBackgroundResource(0);
        }
    }

    private void readyForFilter(){
        mFilterCats.setBackgroundResource(0);
        mFilterMale.setBackgroundResource(0);
        mFilterFemale.setBackgroundResource(0);
        mFilterDogs.setBackgroundResource(0);
        filteredCatsList.clear();
        filteredDogsList.clear();
        filteredFemaleList.clear();
        filteredMaleList.clear();
    }
}
