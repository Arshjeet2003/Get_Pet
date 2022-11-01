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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GlobalChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText MessageInput;
    private ProgressBar progressBar;
    private ImageView sendingArrow;
    private GlobalMessageAdapter globalMessageAdapter;

    private String senderName;
    private String senderEmail;
    private String senderPic;

    private ArrayList<MyMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.gb_recycler_Messages);
        MessageInput = findViewById(R.id.gb_Write_Chat);
        progressBar = findViewById(R.id.gb_message_progress);
        sendingArrow = findViewById(R.id.gb_imageSend);

        messages = new ArrayList<>();

//      initializeGlobalChat();

        Intent intent = getIntent();
        senderName = intent.getStringExtra("sender_name");
        senderEmail = intent.getStringExtra("sender_email");
        senderPic = intent.getStringExtra("sender_pic");


        attachMessageListener();

        sendingArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("globalMessages/")
                        .push().setValue(new MyMessage(senderName,
                        senderEmail,senderPic,MessageInput.getText().toString()));
                MessageInput.setText("");
            }
        });

        globalMessageAdapter = new GlobalMessageAdapter(messages,senderEmail,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(globalMessageAdapter);
    }

    private void attachMessageListener(){
        FirebaseDatabase.getInstance().getReference("globalMessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(MyMessage.class));
                }
                Toast.makeText(getApplicationContext(),"going to take data from mess",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), messages.get(0).getContent(),Toast.LENGTH_SHORT).show();
                globalMessageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeGlobalChat(){
        FirebaseDatabase.getInstance().getReference("globalMessages/")
                .push().setValue(new MyMessage("Developer",
                "developer@gmail.com","","Hello! This is Pet community..."));
    }
}