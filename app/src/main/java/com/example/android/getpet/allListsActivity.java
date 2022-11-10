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
import com.example.android.getpet.SendNotification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class allListsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String senderName;
    private String senderEmail;
    private String senderPic;
    private String senderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_lists);

        UpdateToken();
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
            if(!senderName.isEmpty()) {
                Intent intent = new Intent(allListsActivity.this, profile.class);
                intent.putExtra("update_from_allList",true);
                intent.putExtra("sender_name", senderName);
                intent.putExtra("sender_pic", senderPic);
                intent.putExtra("sender_number",senderNumber);
                startActivity(intent);
            }
        }
        if(item.getItemId()==R.id.menu_item_globalChat){
            if(!senderName.isEmpty()) {
                Intent intent = new Intent(allListsActivity.this, GlobalChatActivity.class);
                intent.putExtra("sender_name_toGlobal", senderName);
                intent.putExtra("sender_email_toGlobal",senderEmail);
                intent.putExtra("sender_pic_toGlobal", senderPic);
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
                    senderNumber = snapshot.getValue(User.class).getNumber();
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

    private void UpdateToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(allListsActivity.this, "new token failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String token = task.getResult();
                        updateToken(token);
                    }
                });
    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getUid()).setValue(token1);
    }
}