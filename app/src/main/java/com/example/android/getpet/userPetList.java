package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class userPetList extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Pets> User_pets;
    private ProgressBar progressBar;
    private UserPetAdapter mUserPetsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    UserPetAdapter.OnUserPetsClickListener onUserPetsClickListener;

    // inflating the activity_user_pet_list
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_user_pet_list,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // associating each variable with corresponding view
        progressBar = getView().findViewById(R.id.User_progressbar);
        User_pets = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.User_recycler);
        swipeRefreshLayout = getView().findViewById(R.id.User_swip);

        //Refresh to show new data.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserPets();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Sending data to PetsEditorActivity on clicking the specifie pet holder and opening PetsEditorActivity
        onUserPetsClickListener = new UserPetAdapter.OnUserPetsClickListener() {
            @Override
            public void onUserPetsClicked(int position) {
                Intent intent = new Intent(getActivity(),PetsEditorActivity.class);
                intent.putExtra("User_data",true);
                intent.putExtra("User_key", User_pets.get(position).getKey());
                intent.putExtra("User_animalName",User_pets.get(position).getAnimalName());
                intent.putExtra("User_animal",User_pets.get(position).getAnimal());
                intent.putExtra("User_breed",User_pets.get(position).getBreed());
                intent.putExtra("User_age",User_pets.get(position).getAge());
                intent.putExtra("User_size",User_pets.get(position).getSize());
                intent.putExtra("User_gender",User_pets.get(position).getGender());
                intent.putExtra("User_desc",User_pets.get(position).getDescription());
                intent.putExtra("User_pic",User_pets.get(position).getProfilePic());
                intent.putExtra("PetKey",User_pets.get(position).getPetKey());
                intent.putExtra("Address_Loc",User_pets.get(position).getLocation());
                intent.putExtra("User_latitudeData",User_pets.get(position).getPetLat());
                intent.putExtra("User_longitudeData",User_pets.get(position).getPetLong());
                startActivity(intent);
            }
        };
        getUserPets();
    }

    //Getting user's pets' data only.
    private void getUserPets(){
        User_pets.clear();
        try {
            FirebaseDatabase.getInstance().getReference("user's_pet/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/pet").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    
                    // getting user's pet's data from database and storing them in User_pets arraylist

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User_pets.add(dataSnapshot.getValue(Pets.class));
                    }
                    
                    // setting up User_pets in PetsAdapter
                    mUserPetsAdapter = new UserPetAdapter(User_pets, getActivity(), onUserPetsClickListener);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mUserPetsAdapter);

                    // if no pets present then set no pets present background
                    if(mUserPetsAdapter.getItemCount()==0){
                        recyclerView.setBackgroundResource(R.drawable.no_mypets_back4);
                    }
                    else{
                        recyclerView.setBackgroundResource(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
