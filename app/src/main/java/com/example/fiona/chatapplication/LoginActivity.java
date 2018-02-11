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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog loadDialog;
    private EditText login_email,login_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        mAuth=FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        loadDialog=new ProgressDialog(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button login_button = findViewById(R.id.login_button);
        login_email=findViewById(R.id.login_name);
        login_pass=findViewById(R.id.login_pass);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,pass;
                email=login_email.getText().toString();
                pass=login_pass.getText().toString();
                LoginUserAccount(email,pass);
            }


        });



    }
    private void LoginUserAccount(String email, String pass) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this,"Please Enter the E-mail",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(LoginActivity.this,"Please Enter the Password",Toast.LENGTH_LONG).show();
        }
        else {
            loadDialog.setTitle("Logging in");
            loadDialog.setMessage("Please wait...while we are verifying your credentials!");
            loadDialog.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent main=new Intent(LoginActivity.this,MainActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main);
                        finish();

                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Invalid Credentials",Toast.LENGTH_LONG).show();

                    }
                    loadDialog.dismiss();
                }
            });
        }
    }
}
