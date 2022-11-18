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
import com.example.android.getpet.R;
import com.example.android.getpet.models.Pets;

import java.util.ArrayList;

//Adapter for petList.
public class PetsAdapter extends
        RecyclerView.Adapter<PetsAdapter.PetsHolder> implements Filterable{

    private ArrayList<Pets> petsListBackup;
    private ArrayList<Pets> pets;
    private Context context;
    private OnPetsClickListener onPetsClickListener;

    public PetsAdapter(ArrayList<Pets> pets, Context context, OnPetsClickListener onPetsClickListener){
        this.pets = pets;
        this.context = context;
        this.onPetsClickListener = onPetsClickListener;
        petsListBackup = new ArrayList<>(pets);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    public interface OnPetsClickListener{
        void onPetsClicked(int position);
    }


    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Pets> filteredPets = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredPets.addAll(petsListBackup);
            }
            else {
                for (Pets pet : petsListBackup){
                    if(pet.getBreed().toLowerCase().trim().contains(constraint.toString().toLowerCase())){
                        filteredPets.add(pet);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredPets;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pets.clear();
            pets.addAll((ArrayList<Pets>)results.values);
            notifyDataSetChanged();
        }
    };






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

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        //Using Glide library to put image of pet in imageView.
        Glide.with(context).load(pets.get(position).getProfilePic()).error(R.drawable.account_img)
                .placeholder(circularProgressDrawable).into(holder.pic);
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
