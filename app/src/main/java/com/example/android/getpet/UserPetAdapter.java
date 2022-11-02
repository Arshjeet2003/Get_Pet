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

//Adapter for userPetList Activity.
public class UserPetAdapter extends RecyclerView.Adapter<UserPetAdapter.UserPetsHolder> {

    private ArrayList<Pets> userPets;
    private Context context;
    private OnUserPetsClickListener onUserPetsClickListener;

    public UserPetAdapter(ArrayList<Pets> userPets, Context context, UserPetAdapter.OnUserPetsClickListener onUserPetsClickListener) {
        this.userPets = userPets;
        this.context = context;
        this.onUserPetsClickListener = onUserPetsClickListener;
    }

    interface OnUserPetsClickListener{
        void onUserPetsClicked(int position);
    }

    //Inflating user_pet_holder xml file.
    @Override
    public UserPetAdapter.UserPetsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_pet_holder,parent,false);
        return new UserPetAdapter.UserPetsHolder(view);
    }

    //Binding data to view holder items.
    @Override
    public void onBindViewHolder(@NonNull UserPetAdapter.UserPetsHolder holder, int position) {

        holder.User_animal.setText(userPets.get(position).getAnimal());
        holder.User_gender.setText(userPets.get(position).getGender());
        holder.User_breed.setText(userPets.get(position).getBreed());
        Glide.with(context).load(userPets.get(position).getProfilePic()).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(holder.User_pic);

    }

    //Returns the number of items in the list.
    @Override
    public int getItemCount() {
        return userPets.size();
    }

    //Setting up holder.
    class UserPetsHolder extends RecyclerView.ViewHolder{

        TextView User_animal;
        TextView User_gender;
        TextView User_breed;
        ImageView User_pic;

        public UserPetsHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onUserPetsClickListener.onUserPetsClicked(getAdapterPosition());
                }
            });

            User_animal = itemView.findViewById(R.id.user_animal);
            User_gender = itemView.findViewById(R.id.user_gender);
            User_breed = itemView.findViewById(R.id.user_breed);
            User_pic = itemView.findViewById(R.id.user_img_pet);
        }
    }

}