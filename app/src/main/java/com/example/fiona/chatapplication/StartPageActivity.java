package com.example.fiona.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity {
    private Button already_have_account_button;
    private Button new_user_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
         already_have_account_button=(Button)findViewById(R.id.already_have_account_button);
         new_user_button=(Button)findViewById(R.id.new_user_button);

         already_have_account_button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                Intent login=new Intent(StartPageActivity.this,LoginActivity.class);
                startActivity(login);
             }
         });
         new_user_button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent register=new Intent(StartPageActivity.this,RegisterActivity.class);
                 startActivity(register);
             }
         });
    }
}
