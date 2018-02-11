package com.example.fiona.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog loadDialog;
    private DatabaseReference databaseReference;
    private EditText register_name,register_email,register_pass;
    Button signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();

        toolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true );
        register_name=(EditText)findViewById(R.id.register_name);
        register_email=(EditText)findViewById(R.id.register_email);
        register_pass=(EditText)findViewById(R.id.register_password);
        signin=(Button)findViewById(R.id.signin);
        loadDialog=new ProgressDialog(this);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,email,pass;
                name=register_name.getText().toString();
                email=register_email.getText().toString();
                pass=register_pass.getText().toString();

                RegisterAccount(name,email,pass);
            }
        });
    }

    private void RegisterAccount(final String name, String email, String pass) {
        if(TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this,"Please Enter the Name",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"Please Enter the E-mail",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(RegisterActivity.this,"Please Enter the Password",Toast.LENGTH_LONG).show();
        }
        else {
            loadDialog.setTitle("Creating new Account");
            loadDialog.setMessage("Please wait...while we are creating a new account for you!");
            loadDialog.show();
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful()){
                         String user_id=mAuth.getCurrentUser().getUid();
                         databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                         databaseReference.child("User_name").setValue(name);
                         databaseReference.child("User_status").setValue("Hey There! I am using Messenger!");
                         databaseReference.child("User_image").setValue("default_image");
                         databaseReference.child("User_thumb_image").setValue("default_image").addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     Intent main=new Intent(RegisterActivity.this,MainActivity.class);
                                     main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                     startActivity(main);
                                     finish();
                                 }
                             }
                         });



                     }
                     else {
                         Toast.makeText(RegisterActivity.this,"Error Occured! Please Try Again",Toast.LENGTH_LONG).show();
                     }
                     loadDialog.dismiss();
                }
            });

        }
    }
}
