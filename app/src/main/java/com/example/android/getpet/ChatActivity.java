package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText MessageInput;
    private TextView Chatting_with;
    private ProgressBar progressBar;
    private ImageView imgUser,sendingArrow;
    private MessageAdapter messageAdapter;

    private String senderKey;
    private String senderName;
    private String senderEmail;
    private String senderPic;

    private String receiverKey;
    private String receiverName;
    private String receiverEmail;
    private String receiverPic;

    private String petKey;
    private String mChatroomId;

    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_Messages);
        MessageInput = findViewById(R.id.Write_Chat);
        Chatting_with = findViewById(R.id.Chatting_With);
        progressBar = findViewById(R.id.message_progress);
        imgUser = findViewById(R.id.img_user_chat);
        sendingArrow = findViewById(R.id.imageSend);

        messages = new ArrayList<>();

        Intent intent = getIntent();
        petKey = intent.getStringExtra("PetKey");
        receiverKey = intent.getStringExtra("receiverKey");
        receiverName = intent.getStringExtra("receiverName");
        receiverEmail = intent.getStringExtra("receiverEmail");
        receiverPic = intent.getStringExtra("receiverProfilePic");
        senderKey = intent.getStringExtra("senderKey");
        senderName = intent.getStringExtra("senderName");
        senderEmail = intent.getStringExtra("senderEmail");
        senderPic = intent.getStringExtra("senderPic");

        Chatting_with.setText(receiverName);
        setUpChatRoom();

        sendingArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("messages/"+mChatroomId)
                        .push().setValue(new Message(senderEmail,
                        receiverEmail,MessageInput.getText().toString()));
                MessageInput.setText("");
            }
        });

        messageAdapter = new MessageAdapter(messages,senderPic,receiverPic,ChatActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Glide.with(ChatActivity.this).load(receiverPic).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(imgUser);

    }

    private void setUpChatRoom(){
        if(senderName.compareTo(receiverName)>=0){
            mChatroomId = receiverName+ petKey +senderName;
        }
        else{
            mChatroomId = senderName+ petKey +receiverName;
        }
        putDataOfChatRoom();
        attachMessageListener();
    }

    private void putDataOfChatRoom(){
        FirebaseDatabase.getInstance().getReference("chats/"+FirebaseAuth.getInstance().getUid()+"/"+mChatroomId)
                .setValue(new DetailsOfChatRoom(petKey,receiverKey,receiverName,receiverEmail,receiverPic,mChatroomId,
                        senderKey,senderName,senderEmail,senderPic));

        FirebaseDatabase.getInstance().getReference("chats/"+receiverKey+"/"+ mChatroomId)
                .setValue(new DetailsOfChatRoom(petKey,senderKey,senderName,senderEmail,senderPic,mChatroomId,
                receiverKey,receiverName,receiverEmail,receiverPic));
    }

    private void attachMessageListener(){
        FirebaseDatabase.getInstance().getReference("messages/"+mChatroomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}