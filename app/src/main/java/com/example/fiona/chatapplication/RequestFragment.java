package com.example.fiona.chatapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {

    private RecyclerView myRequestList;
    private View myMainView;
    private DatabaseReference friendRequestReference,usersReference,FriendsDatabaseReference,FriendsRequestDatabaseReference;
    private FirebaseAuth mAuth;
    String online_user_id;
    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_request, container, false);
        myRequestList = (RecyclerView)myMainView.findViewById(R.id.request_list);
        myRequestList.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid().toString();
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(online_user_id);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsRequestDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestList.setLayoutManager(linearLayoutManager);

        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests,RequestFragment.RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestFragment.RequestViewHolder>
                (
                        Requests.class,
                        R.layout.friend_request_all_users_layout,
                        RequestFragment.RequestViewHolder.class,
                        friendRequestReference
                ) {
                @Override
                protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position) {
                    final String list_users_id = getRef(position).getKey();
                    DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();
                    get_type_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String request_type = dataSnapshot.getValue().toString();
                                if(request_type.equals("received")){
                                    usersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            final String User_name = dataSnapshot.child("User_name").getValue().toString();
                                            final String thumb_image = dataSnapshot.child("User_thumb_image").getValue().toString();
                                            final String user_status = dataSnapshot.child("User_status").getValue().toString();
                                            viewHolder.setUserName(User_name);
                                            viewHolder.setThumbImage(thumb_image,getContext());
                                            viewHolder.setUserStatus(user_status);
                                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CharSequence options[] = new CharSequence[]{
                                                            "Accept Friend Request",
                                                            "Cancel Friend Request"
                                                    };
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                    builder.setTitle("Friend Request Options");
                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int position) {
                                                            if(position==0){
                                                                Calendar callForDate= Calendar.getInstance();
                                                                SimpleDateFormat currentDate= new SimpleDateFormat("dd-MMMM-yyyy");
                                                                final String saveCurrentDate= currentDate.format(callForDate.getTime());
                                                                FriendsDatabaseReference.child(online_user_id)
                                                                        .child(list_users_id)
                                                                        .child("date")
                                                                        .setValue(saveCurrentDate)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                FriendsDatabaseReference.child(list_users_id)
                                                                                        .child(online_user_id)
                                                                                        .child("date")
                                                                                        .setValue(saveCurrentDate)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                FriendsRequestDatabaseReference.child(online_user_id)
                                                                                                        .child(list_users_id)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(Task<Void> task) {
                                                                                                                if(task.isSuccessful()){
                                                                                                                    FriendsRequestDatabaseReference.child(list_users_id)
                                                                                                                            .child(online_user_id)
                                                                                                                            .removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(Task<Void> task) {
                                                                                                                                    if(task.isSuccessful()){
                                                                                                                                        Toast.makeText(getContext(),"Friend Request Accepted",Toast.LENGTH_LONG).show();
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });

                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });

                                                                            }
                                                                        });

                                                            }
                                                            if(position==1){
                                                                FriendsRequestDatabaseReference.child(online_user_id)
                                                                        .child(list_users_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    FriendsRequestDatabaseReference.child(list_users_id)
                                                                                            .child(online_user_id)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(Task<Void> task) {
                                                                                                    if(task.isSuccessful()){

                                                                                                        Toast.makeText(getContext(),"Friend Request Cancelled",Toast.LENGTH_LONG).show();

                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else if(request_type.equals("sent")){
                                    Button request_send_btn = viewHolder.mView.findViewById(R.id.request_accept_buton);
                                    request_send_btn.setText("Request Sent");
                                    viewHolder.mView.findViewById(R.id.request_decline_button).setVisibility(View.INVISIBLE);
                                    usersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final String User_name = dataSnapshot.child("User_name").getValue().toString();
                                            final String thumb_image = dataSnapshot.child("User_thumb_image").getValue().toString();
                                            final String user_status = dataSnapshot.child("User_status").getValue().toString();
                                            viewHolder.setUserName(User_name);
                                            viewHolder.setThumbImage(thumb_image,getContext());
                                            viewHolder.setUserStatus(user_status);
                                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CharSequence options[] = new CharSequence[]{
                                                            "Cancel Friend Request"
                                                    };
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                    builder.setTitle("Friend Request Sent");
                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int position) {

                                                            if(position==0){
                                                                FriendsRequestDatabaseReference.child(online_user_id)
                                                                        .child(list_users_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    FriendsRequestDatabaseReference.child(list_users_id)
                                                                                            .child(online_user_id)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(Task<Void> task) {
                                                                                                    if(task.isSuccessful()){

                                                                                                        Toast.makeText(getContext(),"Friend Request Cancelled",Toast.LENGTH_LONG);

                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            };
        myRequestList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserName(String user_name) {
            TextView userNameDisplay = (TextView)mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(user_name);
        }

        public void setThumbImage(final String thumb_image, final Context ctx) {
            final CircleImageView User_thumb_image=(CircleImageView)mView.findViewById(R.id.request_profile_image);

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

        public void setUserStatus(String user_status) {
            TextView status = mView.findViewById(R.id.request_profile_status);
            status.setText(user_status);
        }
    }
}
