package com.example.android.getpet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//Adapter for petList.
public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetsHolder> {

    private ArrayList<Pets> pets;
    private Context context;
    private OnPetsClickListener onPetsClickListener;

    public PetsAdapter(ArrayList<Pets> pets, Context context, OnPetsClickListener onPetsClickListener) {
        this.pets = pets;
        this.context = context;
        this.onPetsClickListener = onPetsClickListener;
    }

    interface OnPetsClickListener{
        void onPetsClicked(int position);
    }

    //Inflating pet_holder xml file.
    @Override
    public PetsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_holder,parent,false);
        return new PetsHolder(view);
    }

    //Binding data to view holder items.
    @Override
    public void onBindViewHolder(PetsHolder holder,int position) {

        holder.animalName.setText(pets.get(position).getAnimalName());
        holder.breed.setText(pets.get(position).getBreed());
        holder.loc.setText(pets.get(position).getLocation());

        //Using Glide library to put image of pet in imageView.
        Glide.with(context).load(pets.get(position).getProfilePic()).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(holder.pic);
    }

    //Returns the number of items in the list.
    @Override
    public int getItemCount() {
        return pets.size();
    }

    //Setting up holder.
    class PetsHolder extends RecyclerView.ViewHolder{

        TextView animalName;
        TextView breed;
        TextView loc;
        ImageView pic;

        public PetsHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPetsClickListener.onPetsClicked(getAdapterPosition());
                }
            });

            animalName = itemView.findViewById(R.id.animalName_PetList);
            breed = itemView.findViewById(R.id.breed_PetList);
            pic = itemView.findViewById(R.id.img_PetList);
            loc =itemView.findViewById(R.id.loc_PetList);

        }
    }

}
