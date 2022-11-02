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
    public void onBindViewHolder(PetsHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.animal.setText(pets.get(position).getAnimal());
        holder.gender.setText(pets.get(position).getGender());
        holder.breed.setText(pets.get(position).getBreed());

        //If the pet is favourite of the user then show the filled heart or else show empty heart.
        if(pets.get(position).getFav()){
            holder.favourites.setImageResource(R.drawable.filled_fav);
        }
        else{
            holder.favourites.setImageResource(R.drawable.outline_fav);
        }

        //flags array will help you determine if the user has clicked the favourites icon or not.
        boolean[] flags = new boolean[pets.size()];


        holder.favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if flags[] has false then it was not favourite of user but now has to be added.
                if(!flags[position]){
                    holder.favourites.setImageResource(R.drawable.filled_fav);
                    pets.get(position).setFav(true);

                    //Setting favourite as true in database.
                    FirebaseDatabase.getInstance().getReference("pet/"+pets.get(position).getKey())
                            .setValue(pets.get(position));

                    //Adding to the favourites list.
                    FirebaseDatabase.getInstance().getReference("userFav/"+ FirebaseAuth.getInstance().getUid()+"/"+pets.get(position).getPetKey())
                            .setValue(pets.get(position));
                    flags[position] = true;
                }
                //if flags[] has true then it was favourite of user but now has to be removed.
                else{

                    //Removing from favourites list.
                    FirebaseDatabase.getInstance().getReference("userFav/"+ FirebaseAuth.getInstance().getUid())
                            .child(pets.get(position).getPetKey()).removeValue();
                    holder.favourites.setImageResource(R.drawable.outline_fav);
                    pets.get(position).setFav(false);

                    //Setting favourite as false in database.
                    FirebaseDatabase.getInstance().getReference("pet/"+pets.get(position).getKey())
                            .setValue(pets.get(position));
                    flags[position] = false;
                    }
                }
            });

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

        TextView animal;
        TextView gender;
        TextView breed;
        ImageView pic;
        ImageView favourites;

        public PetsHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPetsClickListener.onPetsClicked(getAdapterPosition());
                }
            });

            animal = itemView.findViewById(R.id.animal);
            gender = itemView.findViewById(R.id.gender);
            breed = itemView.findViewById(R.id.breed);
            pic = itemView.findViewById(R.id.img_pet);
            favourites = itemView.findViewById(R.id.fav_empty);

        }
    }

}
