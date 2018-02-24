package com.example.fiona.chatapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendList;
    private DatabaseReference friendsReference;
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
        friendsReference = FirebaseDatabase.getInstance().getReference()
                .child("Friends")
                .child(online_user_id);
        myFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> 
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        public FriendsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
