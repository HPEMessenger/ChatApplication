package com.example.fiona.chatapplication;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fiona on 17-03-2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> userMesagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDatabaseReference;
    public MessageAdapter(List<Messages> userMesagesList){
        this.userMesagesList = userMesagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_layout_of_user,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        String message_sender_id = mAuth.getCurrentUser().getUid();
        Messages messages= userMesagesList.get(position);
        String from_user_id = messages.getFrom();
        String fromMessageType = messages.getType();
        UsersDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(from_user_id);
        UsersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("User_name").getValue().toString();
                String userImage = dataSnapshot.child("User_thumb_image").getValue().toString();
                Picasso.with(holder.userProfileImage.getContext())
                        .load(userImage)
                        .placeholder(R.drawable.default_image)
                        .into(holder.userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(fromMessageType.equals("text")){
            holder.messagePicture.setVisibility(View.INVISIBLE);
            if(from_user_id.equals(message_sender_id)){
                holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setGravity(Gravity.RIGHT);
            }
            else {
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setGravity(Gravity.LEFT);
            }
            holder.messageText.setText(messages.getMessage());
        }
        else{
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.messageText.setPadding(0,0,0,0);
            Picasso.with(holder.userProfileImage.getContext())
                    .load(messages.getMessage())
                    .placeholder(R.drawable.default_image)
                    .into(holder.messagePicture);
        }


    }

    @Override
    public int getItemCount() {

        return userMesagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView userProfileImage;
        public ImageView messagePicture;
        public MessageViewHolder(View view){
            super(view);
            messageText = (TextView)view.findViewById(R.id.message_text);
            messagePicture = (ImageView)view.findViewById(R.id.messgae_image_view);
            userProfileImage = (CircleImageView)view.findViewById(R.id.messages_profile_image);

        }

    }
}
