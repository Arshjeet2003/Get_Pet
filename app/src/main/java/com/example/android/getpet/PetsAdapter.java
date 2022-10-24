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

    @NonNull
    @Override
    public PetsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_holder,parent,false);
        return new PetsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetsHolder holder, int position) {

        holder.animal.setText(pets.get(position).getAnimal());
        holder.gender.setText(pets.get(position).getGender());
        holder.breed.setText(pets.get(position).getBreed());
        Glide.with(context).load(pets.get(position).getProfilePic()).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return pets.size();
    }


    class PetsHolder extends RecyclerView.ViewHolder{

        TextView animal;
        TextView gender;
        TextView breed;
        ImageView pic;

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
        }
    }

}
