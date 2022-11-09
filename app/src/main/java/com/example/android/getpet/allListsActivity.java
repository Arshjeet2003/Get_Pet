package com.example.android.getpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class allListsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String senderName;
    private String senderEmail;
    private String senderPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_lists);

        getUserData();

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);

        VPadapter vPadapter = new VPadapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vPadapter.addFragment(new petList(),"Pets");
        vPadapter.addFragment(new menuChats(),"Chats");
        vPadapter.addFragment(new userPetList(),"My Pets");
        vPadapter.addFragment(new FavouritesActivity(),"Favourites");
        viewPager.setAdapter(vPadapter);

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
            startActivity(new Intent(allListsActivity.this,profile.class));
        }
        if(item.getItemId()==R.id.menu_item_globalChat){
            if(!senderName.isEmpty()) {
                Intent intent = new Intent(allListsActivity.this, GlobalChatActivity.class);
                intent.putExtra("sender_name", senderName);
                intent.putExtra("sender_email", senderEmail);
                intent.putExtra("sender_pic", senderPic);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserData(){
        try {
            //Getting data about user from database.
            FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }
}