package com.example.fiona.chatapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId;
    private String messageReceiverName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageReceiverId=getIntent().getExtras().get("User_visit_id").toString();
        messageReceiverName=getIntent().getExtras().get("User_name").toString();
        Toast.makeText(ChatActivity.this,messageReceiverId,Toast.LENGTH_SHORT);
        Toast.makeText(ChatActivity.this,messageReceiverId,Toast.LENGTH_SHORT);

    }
}
