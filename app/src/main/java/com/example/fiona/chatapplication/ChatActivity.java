package com.example.fiona.chatapplication;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId;
    private String messageReceiverName;

    private Toolbar chatToolbar;
    private TextView UsernameTitle,UserLastSeen;
    private CircleImageView userChatProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageReceiverId=getIntent().getExtras().get("User_visit_id").toString();
        messageReceiverName=getIntent().getExtras().get("User_name").toString();
        chatToolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        UsernameTitle = (TextView)findViewById(R.id.custom_profile_name);
        UserLastSeen =(TextView)findViewById(R.id.custom_User_Last_seen);
        userChatProfileImage = (CircleImageView)findViewById(R.id.custom_Profile_Image);
        UsernameTitle.setText(messageReceiverId);
    }
}
