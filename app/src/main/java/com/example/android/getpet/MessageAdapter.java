package com.example.android.getpet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    public MessageAdapter(ArrayList<Message> messages, String senderImg, String receiverImg, Context context) {
        this.messages = messages;
        this.senderImg = senderImg;
        this.receiverImg = receiverImg;
        this.context = context;
    }

    private ArrayList<Message> messages;
    private String senderImg;
    private String receiverImg;
    private Context context;

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_holder,parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {

        holder.txtmsg.setText(messages.get(position).getContent());

        ConstraintLayout constraintLayout = holder.Clayout;

        if(messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            Glide.with(context).load(senderImg).error(R.drawable.account_img).placeholder(R.drawable.account_img)
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
            Glide.with(context).load(receiverImg).error(R.drawable.account_img).placeholder(R.drawable.account_img)
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

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder{

        ConstraintLayout Clayout;
        TextView txtmsg;
        ImageView profImg;

        public MessageHolder(View itemView){
            super(itemView);

            Clayout = itemView.findViewById(R.id.constraintView);
            txtmsg = itemView.findViewById(R.id.msg_tv);
            profImg = itemView.findViewById(R.id.small_profile_img);
        }
    }
}