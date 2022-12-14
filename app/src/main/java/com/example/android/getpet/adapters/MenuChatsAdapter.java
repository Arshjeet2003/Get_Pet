package com.example.android.getpet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.android.getpet.models.DetailsOfChatRoom;
import com.example.android.getpet.R;

import java.util.ArrayList;

//Adapter for menuChats Activity.
public class MenuChatsAdapter extends
        RecyclerView.Adapter<MenuChatsAdapter.MenuChatsHolder> implements Filterable {

    private ArrayList<DetailsOfChatRoom> chatRoomsBackup;
    private ArrayList<DetailsOfChatRoom> chatRooms;
    private Context context;
    private MenuChatsAdapter.OnChatClickListener onChatClickListener;

    public MenuChatsAdapter(ArrayList<DetailsOfChatRoom> chatRooms, Context context,
                            MenuChatsAdapter.OnChatClickListener onChatClickListener) {
        this.chatRooms = chatRooms;
        this.context = context;
        this.onChatClickListener = onChatClickListener;
        chatRoomsBackup = new ArrayList<>(chatRooms);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public interface OnChatClickListener{
        void onChatClicked(int position);
    }

    //Inflating users_holder xml file.
    @NonNull
    @Override
    public MenuChatsAdapter.MenuChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_holder,parent,false);
        return new MenuChatsAdapter.MenuChatsHolder(view);
    }

    //Binding data to view holder items.
    @Override
    public void onBindViewHolder(@NonNull MenuChatsAdapter.MenuChatsHolder holder, int position) {

        holder.User_name.setText(chatRooms.get(position).getReceiverName());
        holder.Pet_name.setText(chatRooms.get(position).getPetName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Glide.with(context).load(chatRooms.get(position).getReceiverProfilePic())
                .error(R.drawable.account_img)
                .placeholder(circularProgressDrawable).into(holder.User_pic);

    }

    //Returns the number of items in the list.
    @Override
    public int getItemCount() {
        return chatRooms.size();
    }


    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DetailsOfChatRoom> filteredChatRooms = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredChatRooms.addAll(chatRoomsBackup);
            }
            else {
                for (DetailsOfChatRoom detailsOfChatRoom : chatRoomsBackup){
                    if(detailsOfChatRoom.getReceiverName()
                            .toLowerCase().trim()
                            .contains(constraint.toString().toLowerCase())){
                        filteredChatRooms.add(detailsOfChatRoom);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredChatRooms;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chatRooms.clear();
            chatRooms.addAll((ArrayList<DetailsOfChatRoom>)results.values);
            notifyDataSetChanged();
        }
    };


    //Setting up holder.
    class MenuChatsHolder extends RecyclerView.ViewHolder{

        TextView User_name;
        ImageView User_pic;
        TextView Pet_name;

        public MenuChatsHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChatClickListener.onChatClicked(getAdapterPosition());
                }
            });

            User_name = itemView.findViewById(R.id.username);
            User_pic = itemView.findViewById(R.id.img_user);
            Pet_name = itemView.findViewById(R.id.petName_userHolder);
        }
    }

}
