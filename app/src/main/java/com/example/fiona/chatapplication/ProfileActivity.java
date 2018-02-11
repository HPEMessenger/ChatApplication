package com.example.fiona.chatapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private Button send_request,decline_request;
    private TextView profile_username,profile_status;
    private ImageView profileImage;
    private DatabaseReference UsersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String visit_user_id=getIntent().getExtras().get("User_visit_id").toString();
        send_request=findViewById(R.id.profile_visit_send_request_btn);
        decline_request=findViewById(R.id.profile_visit_decline_request_btn);
        profile_username=findViewById(R.id.profile_visit_username);
        profile_status=findViewById(R.id.profile_visit_status);
        profileImage=findViewById(R.id.profile_visit_user_image);
        UsersReference= FirebaseDatabase.getInstance().getReference().child("Users").child(visit_user_id);
        UsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("User_name").getValue().toString();
                String status=dataSnapshot.child("User_status").getValue().toString();
                String image=dataSnapshot.child("User_image").getValue().toString();
                profile_status.setText(status);
                profile_username.setText(name);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_image).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
