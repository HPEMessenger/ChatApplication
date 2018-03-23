package com.example.fiona.chatapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View myMainView;
    private RecyclerView myChatsList;
    private DatabaseReference friendsReference,usersReference;
    private FirebaseAuth mAuth;
    String online_user_id;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myMainView =inflater.inflate(R.layout.fragment_chat, container, false);
        myChatsList = (RecyclerView)myMainView.findViewById(R.id.chats_list);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);
        friendsReference = FirebaseDatabase.getInstance().getReference()
                .child("Friends")
                .child(online_user_id);
        friendsReference.keepSynced(true);
        myChatsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myChatsList.setLayoutManager(linearLayoutManager);


        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats,ChatFragment.ChatsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Chats, ChatFragment.ChatsViewHolder>
                        (
                                Chats.class,
                                R.layout.all_users_display_layout,
                                ChatFragment.ChatsViewHolder.class,
                                friendsReference

                        ) {
                    @Override
                    protected void populateViewHolder(final ChatFragment.ChatsViewHolder viewHolder, Chats model, int position) {
                        final String list_user_id = getRef(position).getKey();
                        usersReference.child(list_user_id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        final String User_name = dataSnapshot.child("User_name").getValue().toString();
                                        String thumb_image = dataSnapshot.child("User_thumb_image").getValue().toString();


                                        String user_status = dataSnapshot.child("User_status").getValue().toString();

                                        if(dataSnapshot.hasChild("online")){
                                            String online_status = (String) dataSnapshot.child("online").getValue().toString();
                                            viewHolder.setUserOnline(online_status);
                                        }
                                        viewHolder.setUsername(User_name);
                                        viewHolder.setThumbImage(thumb_image,getContext());
                                        viewHolder.setUserStatus(user_status);
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(dataSnapshot.child("online").exists()){
                                                    Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                                    chatIntent.putExtra("User_visit_id",list_user_id);
                                                    chatIntent.putExtra("User_name",User_name);
                                                    startActivity(chatIntent);
                                                }
                                                else {
                                                    usersReference.child(list_user_id).child("online")
                                                            .setValue(ServerValue.TIMESTAMP)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                                                    chatIntent.putExtra("User_visit_id",list_user_id);
                                                                    chatIntent.putExtra("User_name",User_name);
                                                                    startActivity(chatIntent);
                                                                }
                                                            });
                                                }

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                };
        myChatsList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setUsername(String User_name){
            TextView usernameDisplay =(TextView)mView.findViewById(R.id.all_users_username);
            usernameDisplay.setText(User_name);
        }

        public void setThumbImage(final String thumb_image,final Context ctx) {
            final CircleImageView User_thumb_image=(CircleImageView)mView.findViewById(R.id.all_users_profile_image);

            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(User_thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_image).into(User_thumb_image);

                }
            });
        }

        public void setUserOnline(String online_status) {
            ImageView online_status_view = (ImageView)mView.findViewById(R.id.online_status);
            if(online_status.equals("true")){
                online_status_view.setVisibility(View.VISIBLE);
            }
            else{
                online_status_view.setVisibility(View.VISIBLE);
            }
        }

        public void setUserStatus(String userStatus) {
            TextView UserStatus = mView.findViewById(R.id.all_users_status);
            UserStatus.setText(userStatus);
        }
    }
}
