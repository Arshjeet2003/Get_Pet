package com.example.android.getpet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

//Adapter for Global Message Activity
public class GlobalMessageAdapter extends RecyclerView.Adapter<GlobalMessageAdapter.GlobalMessageHolder> {

    private ArrayList<MyMessage> messages;
    private Context context;
    private String senderEmail;

    public GlobalMessageAdapter(ArrayList<MyMessage> messages,String senderEmail,Context context) {
        this.messages = messages;
        this.context = context;
        this.senderEmail = senderEmail;
    }

    //Inflating message_holder xml file.
    @NonNull
    @Override
    public GlobalMessageAdapter.GlobalMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_holder,parent,false);
        return new GlobalMessageAdapter.GlobalMessageHolder(view);
    }

    //Binding data to view holder items.
    @Override
    public void onBindViewHolder(@NonNull GlobalMessageAdapter.GlobalMessageHolder holder, int position) {

        holder.txtmsg.setText(messages.get(position).getContent());

        ConstraintLayout constraintLayout = holder.Clayout;

        /*Using Constraints to stick the messages send by sender to right and stick the messages
          of the receiver to the left*/
        if(messages.get(position).getSenderEmail().equals(senderEmail)){
            Glide.with(context).load(messages.get(position).getSenderPic()).error(R.drawable.account_img).placeholder(R.drawable.account_img)
                    .into(holder.profImg);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.message_cardView,ConstraintSet.LEFT);
            constraintSet.clear(R.id.msg_tv,ConstraintSet.LEFT);
            constraintSet.connect(R.id.message_cardView,ConstraintSet.RIGHT,R.id.constraintView,ConstraintSet.RIGHT,0);
            constraintSet.connect(R.id.msg_tv,ConstraintSet.RIGHT,R.id.message_cardView,ConstraintSet.LEFT,0);
            constraintSet.applyTo(constraintLayout);
        }
        else{
            Glide.with(context).load(messages.get(position).getSenderPic()).error(R.drawable.account_img).placeholder(R.drawable.account_img)
                    .into(holder.profImg);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.message_cardView,ConstraintSet.RIGHT);
            constraintSet.clear(R.id.msg_tv,ConstraintSet.RIGHT);
            constraintSet.connect(R.id.message_cardView,ConstraintSet.LEFT,R.id.constraintView,ConstraintSet.LEFT,0);
            constraintSet.connect(R.id.msg_tv,ConstraintSet.LEFT,R.id.message_cardView,ConstraintSet.RIGHT,0);
            constraintSet.applyTo(constraintLayout);
        }
    }

    //Returns the number of items in the list.
    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Setting up holder.
    class GlobalMessageHolder extends RecyclerView.ViewHolder{

        ConstraintLayout Clayout;
        TextView txtmsg;
        ImageView profImg;

        public GlobalMessageHolder(View itemView){
            super(itemView);

            Clayout = itemView.findViewById(R.id.constraintView);
            txtmsg = itemView.findViewById(R.id.msg_tv);
            profImg = itemView.findViewById(R.id.small_profile_img);
        }
    }
}
