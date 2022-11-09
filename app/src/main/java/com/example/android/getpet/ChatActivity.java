package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
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

    private String petName;

    private String petKey;
    private String mChatroomId;

    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_Messages);
        MessageInput = findViewById(R.id.Write_Chat);
        Chatting_with = findViewById(R.id.Chatting_With);
        progressBar = findViewById(R.id.message_progress);
        imgUser = findViewById(R.id.img_user_chat);
        sendingArrow = findViewById(R.id.imageSend);

        messages = new ArrayList<>();

        //Getting data from PetDetailsActivity
        Intent intent = getIntent();
        petKey = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_petKey));
        receiverKey = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_receiverKey));
        receiverName = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_receiverName));
        receiverEmail = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_receiverEmail));
        receiverPic = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_receiverPic));
        senderKey = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_senderKey));
        senderName = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_senderName));
        senderEmail = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_senderEmail));
        senderPic = intent.getStringExtra(getResources().getString(R.string.ChatActivity_intent_senderPic));
        petName = intent.getStringExtra("petName_ChatActivity");

        Chatting_with.setText(receiverName);
        setUpChatRoom();

        //Inserting Message objects in database.
        sendingArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("messages/"+mChatroomId)
                        .push().setValue(new Message(senderEmail,
                        receiverEmail,MessageInput.getText().toString()));
                MessageInput.setText("");
            }
        });

        //Setting up message adapter
        messageAdapter = new MessageAdapter(messages,senderPic,receiverPic,ChatActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        //Using Glide library to put image of receiver in imageView
        Glide.with(ChatActivity.this).load(receiverPic).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(imgUser);

    }

    //Setting up chatroom for each chat between User and Pet Owner
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

    /*Putting all the data in chats node in database so that data can be retrieved easily in the chats of the user.
    When user key is used in database reference, sender is user.
    When receiver key is used in database reference, sender is pet owner.*/

    private void putDataOfChatRoom(){
        FirebaseDatabase.getInstance().getReference("chats/"+FirebaseAuth.getInstance().getUid()+"/"+mChatroomId)
                .setValue(new DetailsOfChatRoom(petKey,receiverKey,receiverName,receiverEmail,receiverPic,mChatroomId,
                        senderKey,senderName,senderEmail,senderPic,petName));

        FirebaseDatabase.getInstance().getReference("chats/"+receiverKey+"/"+ mChatroomId)
                .setValue(new DetailsOfChatRoom(petKey,senderKey,senderName,senderEmail,senderPic,mChatroomId,
                        receiverKey,receiverName,receiverEmail,receiverPic,petName));
    }

    //Retrieving messages from database, adding in messages arraylist.
    private void attachMessageListener(){
        FirebaseDatabase.getInstance().getReference("messages/"+mChatroomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }
                //Notifying message adapter about changes.
                messageAdapter.notifyDataSetChanged();

                //Scrolling recycler view to last message.
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}