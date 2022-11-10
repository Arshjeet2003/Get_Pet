package com.example.android.getpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class menuChats extends Fragment {

    private SearchView searchPetOwner;
    private RecyclerView recyclerView;
    private ArrayList<DetailsOfChatRoom> chatRooms;
    private ProgressBar progressBar;
    private MenuChatsAdapter menuChatsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    MenuChatsAdapter.OnChatClickListener onChatClickListener;

    //Inflating the layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_menu_chats,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchPetOwner = getView().findViewById(R.id.searchChat_sv);
        recyclerView = getView().findViewById(R.id.recycler_Chats);
        progressBar = getView().findViewById(R.id.progressbar_Chats);
        swipeRefreshLayout = getView().findViewById(R.id.chatswip);
        chatRooms = new ArrayList<>();

        // Dynamic searching of usernames in Chats tab.
        searchPetOwner.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                //Using the filter from menuChatsAdapter to filter through the list.
                menuChatsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Calling getUsers() again on refreshing so that data can be updated in recycler view.
                getUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Sending data to ChatActivity
        onChatClickListener = new MenuChatsAdapter.OnChatClickListener() {
            @Override
            public void onChatClicked(int position) {
                Intent intent1 = new Intent(getActivity(), ChatActivity.class);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_petKey), chatRooms.get(position).getPetKey());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverName), chatRooms.get(position).getReceiverName());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverEmail), chatRooms.get(position).getReceiverEmail());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverPic), chatRooms.get(position).getReceiverProfilePic());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverKey), chatRooms.get(position).getReceiverKey());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderName),chatRooms.get(position).getSenderName());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderEmail),chatRooms.get(position).getSenderEmail());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderPic),chatRooms.get(position).getSenderProfilePic());
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderKey ),chatRooms.get(position).getSenderKey());
                startActivity(intent1);
            }
        };

        //Getting data of users in the arraylist and setting up the recycler view.
        getUsers();
    }

    //Getting all the chats of the user.
    private void getUsers(){
        chatRooms.clear(); //Clearing the arraylist first so that data does not keep getting added.
        try {
            //Fetching data from the database
            FirebaseDatabase.getInstance().getReference("chats/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        //Adding data to the arraylist.
                        chatRooms.add(dataSnapshot.getValue(DetailsOfChatRoom.class));
                    }

                    //Initializing adapter with the data from firebase.
                    menuChatsAdapter = new MenuChatsAdapter(chatRooms, getActivity(), onChatClickListener);

                    //A LayoutManager is responsible for measuring and positioning item views within a RecyclerView as well as determining the policy for when to recycle item views that are no longer visible to the user.
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    //Removing the progress bar after getting data.
                    progressBar.setVisibility(View.GONE);

                    //Making recycler view visible.
                    recyclerView.setVisibility(View.VISIBLE);

                    //Setting the recycler view.
                    recyclerView.setAdapter(menuChatsAdapter);

                    //Changing the background based on if the recycler view has any items or not.
                    if (menuChatsAdapter.getItemCount() == 0){
                        recyclerView.setBackgroundResource(R.drawable.no_chats_back4);
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
        catch (Exception e){ //Catching exceptions that might have occurred.
            e.printStackTrace();
            Toast.makeText(getActivity(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
