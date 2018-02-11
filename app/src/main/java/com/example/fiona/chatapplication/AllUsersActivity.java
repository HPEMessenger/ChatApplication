package com.example.fiona.chatapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.fiona.chatapplication.R.layout.all_users_display_layout;

public class AllUsersActivity extends AppCompatActivity {
    private RecyclerView all_user_list;
    private Toolbar mToolbar;
    private DatabaseReference allUsersDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
         mToolbar = (Toolbar) findViewById(R.id.all_users_app_bar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        allUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        all_user_list = (RecyclerView)findViewById(R.id.all_users_list);
        all_user_list.setHasFixedSize(true);
        all_user_list.setLayoutManager(new LinearLayoutManager(this));

    }
   @Override
    public void onStart() {
       super.onStart();

       FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(
               AllUsers.class,
               R.layout.all_users_display_layout,
               AllUsersViewHolder.class,
               allUsersDatabaseReference
       )
       {

           @Override
           protected void populateViewHolder(AllUsersViewHolder holder, AllUsers model, int position) {
               holder.setUser_name(model.getUser_name());
               holder.setUser_status(model.getUser_status());
               holder.setUser_thumb_image(getApplicationContext(),model.getUser_thumb_image());

           }

       };
       all_user_list.setAdapter(firebaseRecyclerAdapter);

   }


    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private TextView User_name,User_status;
        private CircleImageView User_thumb_image;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }


         void setUser_name(String User_name) {
            this.User_name = mView.findViewById(R.id.all_users_username);

            this.User_name.setText(User_name);
        }
         void setUser_status(String User_status) {
             this.User_status =  mView.findViewById(R.id.all_users_status);

             this.User_status.setText(User_status);
        }



         void setUser_thumb_image(Context ctx,String User_thumb_image) {
             this.User_thumb_image=mView.findViewById(R.id.all_users_profile_image);

             Picasso.with(ctx).load(User_thumb_image).placeholder(R.drawable.default_image).into(this.User_thumb_image);
        }

    }
}
