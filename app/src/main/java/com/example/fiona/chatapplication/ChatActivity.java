package com.example.fiona.chatapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId;
    private String messageReceiverName;
    private static int Gallery_Pick =1;
    private Toolbar chatToolbar;
    private TextView UsernameTitle,UserLastSeen;
    private CircleImageView userChatProfileImage;
    private ImageButton SendMessageButton,SelectImageButton;
    private RecyclerView userMessageslist;
    private EditText InputMessageText;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private StorageReference MessageImageStorageRef;
    private String messageSenderId;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private ProgressDialog LoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        messageSenderId=mAuth.getCurrentUser().getUid();
        messageReceiverId=getIntent().getExtras().get("User_visit_id").toString();
        messageReceiverName=getIntent().getExtras().get("User_name").toString();
        MessageImageStorageRef = FirebaseStorage.getInstance().getReference().child("Messages_Pictures");
        chatToolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        LoadingBar = new ProgressDialog(this);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        UsernameTitle = findViewById(R.id.custom_profile_name);
        UserLastSeen =findViewById(R.id.custom_User_Last_seen);
        userChatProfileImage = findViewById(R.id.custom_Profile_Image);
        SendMessageButton = findViewById(R.id.send_message_btn);
        SelectImageButton = findViewById(R.id.select_image);
        InputMessageText=findViewById(R.id.input_message);
        messageAdapter = new MessageAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageslist.setHasFixedSize(true);
        userMessageslist.setLayoutManager(linearLayoutManager);
        userMessageslist.setAdapter(messageAdapter);
        FetchMessage();
        userMessageslist = findViewById(R.id.messages_list_of_users);
        UsernameTitle.setText(messageReceiverName);
        rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userThumb = dataSnapshot.child("User_thumb_image").getValue().toString();
                Picasso.with(ChatActivity.this).load(userThumb)
                        .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(userChatProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ChatActivity.this).load(userThumb).placeholder(R.drawable.default_image).into(userChatProfileImage);

                    }
                });
                if(online.equals("true")){
                    UserLastSeen.setText("Online");
                }
                else{
                    LastSeenTime getTime = new LastSeenTime();
                    long last_seen = Long.parseLong(online);
                    String lastSeenDisplayTime = getTime.getTimeAgo(last_seen, getApplicationContext());
                    UserLastSeen.setText(lastSeenDisplayTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });
        SelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick&& resultCode==RESULT_OK){
            LoadingBar.setTitle("Sending Image");
            LoadingBar.setMessage("Please wait while the picture is being sent!");
            LoadingBar.show();
            Uri ImageUri=data.getData();
            final String message_sender_ref = "Messages /"+messageSenderId + "/" + messageReceiverId;
            final String message_receiver_ref = "Messages /"+messageReceiverId + "/" + messageSenderId;
            DatabaseReference user_mesage_key = rootRef.child("Messages")
                    .child(messageSenderId)
                    .child(messageReceiverId).push();
            final String message_push_id = user_mesage_key.getKey();
            StorageReference filePath = MessageImageStorageRef.child(message_push_id+ ".jpg");
            filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        final String downloadUrl=task.getResult().getDownloadUrl().toString();
                        Map messageTextBody = new HashMap();
                        messageTextBody.put("message",downloadUrl);
                        messageTextBody.put("seen",false);
                        messageTextBody.put("type","image");
                        messageTextBody.put("time", ServerValue.TIMESTAMP);
                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
                        messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBody);
                        rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null){
                                    Log.d("Chat_Log",databaseError.getMessage().toString());
                                }
                                InputMessageText.setText("null");
                                LoadingBar.dismiss();
                            }
                        });
                        Toast.makeText(ChatActivity.this,"Picture Sent Successfully",Toast.LENGTH_LONG).show();
                        LoadingBar.dismiss();
                    }
                    else{
                        Toast.makeText(ChatActivity.this,"Picture Not Sent",Toast.LENGTH_LONG).show();
                        LoadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void FetchMessage() {
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String messageText = InputMessageText.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(ChatActivity.this,"Please wrtie your message",Toast.LENGTH_SHORT);
        }
        else{
            String message_sender_ref = "Messages /"+messageSenderId + "/" + messageReceiverId;
            String message_receiver_ref = "Messages /"+messageReceiverId + "/" + messageSenderId;
            DatabaseReference user_mesage_key = rootRef.child("Messages")
                    .child(messageSenderId)
                    .child(messageReceiverId).push();
            String message_push_id = user_mesage_key.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("seen",false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
            messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBody);
            rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null){
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }
                    InputMessageText.setText("");
                }
            });
        }
    }
}
