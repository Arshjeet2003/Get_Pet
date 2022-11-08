package com.example.android.getpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class allListsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_lists);

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
        return super.onOptionsItemSelected(item);
    }
}