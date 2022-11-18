package com.example.android.getpet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.android.getpet.R;
import com.example.android.getpet.models.Pets;

import java.util.ArrayList;

//Adapter for userPetList Activity.
public class UserPetAdapter extends RecyclerView.Adapter<UserPetAdapter.UserPetsHolder> {

    private ArrayList<Pets> userPets;
    private Context context;
    private OnUserPetsClickListener onUserPetsClickListener;

    public UserPetAdapter(ArrayList<Pets> userPets, Context context,
                          UserPetAdapter.OnUserPetsClickListener onUserPetsClickListener) {
        this.userPets = userPets;
        this.context = context;
        this.onUserPetsClickListener = onUserPetsClickListener;
    }

    public interface OnUserPetsClickListener{
        void onUserPetsClicked(int position);
    }

    //Inflating user_pet_holder xml file.
    @Override
    public UserPetAdapter.UserPetsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_holder,parent,false);
        return new UserPetAdapter.UserPetsHolder(view);
    }

    //Binding data to view holder items.
    @Override
    public void onBindViewHolder(@NonNull UserPetAdapter.UserPetsHolder holder, int position) {

        holder.User_animal.setText(userPets.get(position).getAnimalName());
        holder.User_breed.setText(userPets.get(position).getBreed());
        holder.User_location.setText(userPets.get(position).getLocation());

        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Glide.with(context).load(userPets.get(position).getProfilePic())
                .error(R.drawable.account_img)
                .placeholder(circularProgressDrawable)
                .into(holder.User_pic);

    }

    //Returns the number of items in the list.
    @Override
    public int getItemCount() {
        return userPets.size();
    }

    //Setting up holder.
    class UserPetsHolder extends RecyclerView.ViewHolder{

        TextView User_animal;
        TextView User_breed;
        TextView User_location;
        ImageView User_pic;

        public UserPetsHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onUserPetsClickListener.onUserPetsClicked(getAdapterPosition());
                }
            });

            User_animal = itemView.findViewById(R.id.animalName_PetList);
            User_breed = itemView.findViewById(R.id.breed_PetList);
            User_location = itemView.findViewById(R.id.loc_PetList);
            User_pic = itemView.findViewById(R.id.img_PetList);
        }
    }

}