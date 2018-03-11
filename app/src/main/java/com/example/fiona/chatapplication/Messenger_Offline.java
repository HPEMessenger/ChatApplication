package com.example.fiona.chatapplication;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Fiona on 12-02-2018.
 */

public class Messenger_Offline extends Application{
    private DatabaseReference UserDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //load offline picture
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            String online_user_id = mAuth.getCurrentUser().getUid();
            UserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(online_user_id);
            UserDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserDatabaseReference.child("online").onDisconnect().setValue(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
