package com.example.fiona.chatapplication;


import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendList;
    private DatabaseReference friendsReference,usersReference;
    private FirebaseAuth mAuth;
    String online_user_id;
    private View myMainView;
    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        myFriendList = (RecyclerView)myMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);
        friendsReference = FirebaseDatabase.getInstance().getReference()
                .child("Friends")
                .child(online_user_id);
        friendsReference.keepSynced(true);
        myFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                        (
                                Friends.class,
                                R.layout.all_users_display_layout,
                                FriendsViewHolder.class,
                                friendsReference

                        ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                        viewHolder.setDate(model.getDate());
                        String list_user_id = getRef(position).getKey();
                        usersReference.child(list_user_id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String User_name = dataSnapshot.child("User_name").getValue().toString();
                                        String thumb_image = dataSnapshot.child("User_thumb_image").getValue().toString();
                                        if(dataSnapshot.hasChild("online")){
                                            Boolean online_status = (boolean)dataSnapshot.child("online").getValue();
                                            viewHolder.setUserOnline(online_status);
                                        }
                                        viewHolder.setUsername(User_name);
                                        viewHolder.setThumbImage(thumb_image,getContext());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDate(String date) {
            TextView sinceFriendDate = (TextView)mView.findViewById(R.id.all_users_status);
            sinceFriendDate.setText(date);
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

        public void setUserOnline(Boolean online_status) {
            ImageView online_status_view = (ImageView)mView.findViewById(R.id.online_status);
            if(online_status==true){
                online_status_view.setVisibility(View.VISIBLE);
            }
            else{
                online_status_view.setVisibility(View.VISIBLE);
            }
        }
    }
}
