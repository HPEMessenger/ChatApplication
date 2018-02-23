package com.example.fiona.chatapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private Button send_request,decline_request;
    private TextView profile_username,profile_status;
    private ImageView profileImage;
    private DatabaseReference FriendRequestReference;
    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationReference;
    private String current_state;
    String sender_user_id,receiver_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        receiver_user_id=getIntent().getExtras().get("User_visit_id").toString();
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(receiver_user_id);
        FriendRequestReference=FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        FriendRequestReference.keepSynced(true);
        FriendsReference=FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReference.keepSynced(true);
        NotificationReference=FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationReference.keepSynced(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        sender_user_id= mAuth.getCurrentUser().getUid();
        send_request=findViewById(R.id.profile_visit_send_request_btn);
        decline_request=findViewById(R.id.profile_visit_decline_request_btn);
        profile_username=findViewById(R.id.profile_visit_username);
        profile_status=findViewById(R.id.profile_visit_status);
        profileImage=findViewById(R.id.profile_visit_user_image);
        current_state="not_friends";
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("User_name").getValue().toString();
                String status=dataSnapshot.child("User_status").getValue().toString();
                String image=dataSnapshot.child("User_image").getValue().toString();
                profile_status.setText(status);
                profile_username.setText(name);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_image).into(profileImage);
                FriendRequestReference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    if(dataSnapshot.hasChild(receiver_user_id)){
                                        String req_type=dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                                        if(req_type.equals("sent")){
                                            current_state="request_sent";
                                            send_request.setText("Cancel Friend Request");
                                            decline_request.setVisibility(View.INVISIBLE);
                                            decline_request.setEnabled(false);

                                        }
                                        if(req_type.equals("received")){
                                            current_state="request_received";
                                            send_request.setText("Accept Friend Request");
                                            decline_request.setVisibility(View.VISIBLE);
                                            decline_request.setEnabled(true);
                                            decline_request.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DeclineFriendRequest();
                                                }
                                            });

                                        }
                                    }
                                }
                                else{
                                    FriendsReference.child(sender_user_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.hasChild(receiver_user_id)){
                                                        current_state="friends";
                                                        send_request.setText("Unfriend ");
                                                        decline_request.setVisibility(View.INVISIBLE);
                                                        decline_request.setEnabled(false);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        decline_request.setVisibility(View.INVISIBLE);
        decline_request.setEnabled(false);
        if(!sender_user_id.equals(receiver_user_id)){
            send_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_request.setEnabled(false);
                    if(current_state.equals("not_friends")){
                        SendFriendRequestToAPerson();
                    }
                    if(current_state.equals("request_sent")){
                        CancelFriendRequest();

                    }
                    if(current_state.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(current_state.equals("friends")){
                        Unfriend();
                    }
                }
            });
        }
        else {
            send_request.setVisibility(View.INVISIBLE);
            decline_request.setVisibility(View.INVISIBLE);
        }

    }

    private void DeclineFriendRequest() {
        FriendRequestReference.child(sender_user_id)
                .child(receiver_user_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id)
                                    .child(sender_user_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                send_request.setEnabled(true);
                                                current_state="not_friends";
                                                send_request.setText("Send Friend Request");
                                                decline_request.setVisibility(View.INVISIBLE);
                                                decline_request.setEnabled(false);

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void Unfriend() {
        FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                send_request.setEnabled(true);
                                                current_state="not_friends";
                                                send_request.setText("SEND REQUEST");
                                                decline_request.setVisibility(View.INVISIBLE);
                                                decline_request.setEnabled(false);

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar callForDate= Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate= currentDate.format(callForDate.getTime());
        FriendsReference.child(sender_user_id)
                .child(receiver_user_id)
                .setValue(saveCurrentDate)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FriendsReference.child(receiver_user_id)
                        .child(sender_user_id)
                        .setValue(saveCurrentDate)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FriendRequestReference.child(sender_user_id)
                                        .child(receiver_user_id)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    FriendRequestReference.child(receiver_user_id)
                                                            .child(sender_user_id)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        send_request.setEnabled(true);
                                                                        current_state="friends";
                                                                        send_request.setText("Unfriend");
                                                                        decline_request.setVisibility(View.INVISIBLE);
                                                                        decline_request.setEnabled(false);
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

    private void CancelFriendRequest() {
        FriendRequestReference.child(sender_user_id)
                .child(receiver_user_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id)
                                    .child(sender_user_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   send_request.setEnabled(true);
                                                   current_state="not_friends";
                                                   send_request.setText("Send Friend Request");
                                                   decline_request.setVisibility(View.INVISIBLE);
                                                   decline_request.setEnabled(false);

                                               }
                                        }
                                    });

                        }
                    }
                });
    }

    private void SendFriendRequestToAPerson() {
        FriendRequestReference.child(sender_user_id)
                .child(receiver_user_id)
                .child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id)
                                    .child(sender_user_id)
                                    .child("request_type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                HashMap<String,String> notificationdata=new HashMap<String, String>();
                                                notificationdata.put("from",sender_user_id);
                                                notificationdata.put("type","request");
                                                NotificationReference.child(receiver_user_id).push().setValue(notificationdata)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    send_request.setEnabled(true);
                                                                    current_state="request_sent";
                                                                    send_request.setText("Cancel Friend Request");
                                                                    decline_request.setVisibility(View.INVISIBLE);
                                                                    decline_request.setEnabled(false);
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
