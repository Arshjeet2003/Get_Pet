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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class menuChats extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<DetailsOfChatRoom> chatRooms;
    private ProgressBar progressBar;
    private MenuChatsAdapter menuChatsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    MenuChatsAdapter.OnChatClickListener onChatClickListener;
    private TextView noChatData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_chats);

        recyclerView = findViewById(R.id.recycler_Chats);
        progressBar = findViewById(R.id.progressbar_Chats);
        swipeRefreshLayout = findViewById(R.id.chatswip);
        noChatData = findViewById(R.id.NoChats_tv);
        chatRooms = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        onChatClickListener = new MenuChatsAdapter.OnChatClickListener() {
            @Override
            public void onChatClicked(int position) {
                Intent intent1 = new Intent(menuChats.this, ChatActivity.class);
                intent1.putExtra("PetKey", chatRooms.get(position).getPetKey());
                intent1.putExtra("receiverName", chatRooms.get(position).getReceiverName());
                intent1.putExtra("receiverEmail", chatRooms.get(position).getReceiverEmail());
                intent1.putExtra("receiverProfilePic", chatRooms.get(position).getReceiverProfilePic());
                intent1.putExtra("receiverKey", chatRooms.get(position).getReceiverKey());
                intent1.putExtra("senderName",chatRooms.get(position).getSenderName());
                intent1.putExtra("senderEmail",chatRooms.get(position).getSenderEmail());
                intent1.putExtra("senderPic",chatRooms.get(position).getSenderProfilePic());
                intent1.putExtra("senderKey",chatRooms.get(position).getSenderKey());
                startActivity(intent1);
            }
        };
        getUsers();
    }

    private void getUsers(){
        chatRooms.clear();
        FirebaseDatabase.getInstance().getReference("chats/"+FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    chatRooms.add(dataSnapshot.getValue(DetailsOfChatRoom.class));
                }
                menuChatsAdapter = new MenuChatsAdapter(chatRooms,menuChats.this,onChatClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(menuChats.this));
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(menuChatsAdapter);
                if(menuChatsAdapter.getItemCount()==0){
                    noChatData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}