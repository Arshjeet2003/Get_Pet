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

        //Loading pets again on refreshing.
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

        //Sending data to PetDetailsActivity.
        onPetsClickListener = new PetsAdapter.OnPetsClickListener() {
            @Override
            public void onPetsClicked(int position) {
                Intent intent = new Intent(petList.this,PetDetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerName),pets.get(position).getOwnerName());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerEmail),pets.get(position).getOwnerEmail());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfilePic),pets.get(position).getOwnerProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfileKey),pets.get(position).getOwnerKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_petName),pets.get(position).getAnimal());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_breed),pets.get(position).getBreed());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_age),pets.get(position).getAge());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_size),pets.get(position).getSize());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_gender),pets.get(position).getGender());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_pic),pets.get(position).getProfilePic());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetKey),pets.get(position).getKey());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLat),pets.get(position).getPetLat());
                intent.putExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLong),pets.get(position).getPetLong());
                startActivity(intent);
            }
        };
    }

    //Inflating menu options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.petlist_menu,menu);
        return true;
    }

    //Setting what happens when any menu item is clicked.
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
//            Intent intent = new Intent(petList.this,GlobalChatActivity.class);
//            intent.putExtra("sender_name",senderName);
//            intent.putExtra("sender_email",senderEmail);
//            intent.putExtra("sender_pic",senderPic);
//            startActivity(intent);
        }
        if(item.getItemId()==R.id.menu_item_fav){
            showFavPets();
        }
        return super.onOptionsItemSelected(item);
    }

    //Showing favourites pets of the user. Getting data of user's favourite pets from database.
    private void showFavPets() {
        pets.clear();
        FirebaseDatabase.getInstance().getReference("userFav/"+FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    //Getting all the pet details from the database.
    private void getPets(){
        pets.clear();
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

    //Getting User details from the database.
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

    //Setting up the adapter to show the list of pets in the arraylist.
    private void setAdapter(){
        petsAdapter = new PetsAdapter(pets,petList.this,onPetsClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(petList.this));
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