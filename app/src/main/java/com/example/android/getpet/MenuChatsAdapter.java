package com.example.android.getpet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MenuChatsAdapter extends RecyclerView.Adapter<MenuChatsAdapter.MenuChatsHolder> {

    private ArrayList<DetailsOfChatRoom> chatRooms;
    private Context context;
    private MenuChatsAdapter.OnChatClickListener onChatClickListener;

    public MenuChatsAdapter(ArrayList<DetailsOfChatRoom> chatRooms, Context context, MenuChatsAdapter.OnChatClickListener onChatClickListener) {
        this.chatRooms = chatRooms;
        this.context = context;
        this.onChatClickListener = onChatClickListener;
    }

    interface OnChatClickListener{
        void onChatClicked(int position);
    }

    @NonNull
    @Override
    public MenuChatsAdapter.MenuChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_holder,parent,false);
        return new MenuChatsAdapter.MenuChatsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuChatsAdapter.MenuChatsHolder holder, int position) {

        holder.User_name.setText(chatRooms.get(position).getReceiverName());
        Glide.with(context).load(chatRooms.get(position).getReceiverProfilePic()).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(holder.User_pic);

    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }


    class MenuChatsHolder extends RecyclerView.ViewHolder{

        TextView User_name;
        ImageView User_pic;

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
        }
    }

}
