package com.example.fiona.chatapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                        (
                                Friends.class,
                                R.layout.all_users_display_layout,
                                FriendsViewHolder.class,
                                friendsReference

                        ) {
                    @Override
                    protected void populateViewHolder(FriendsViewHolder viewHolder, Friends model, int position) {
                        viewHolder.setDate(model.getDate());
                        String list_user_id = getRef(position).getKey();
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
    }
}
