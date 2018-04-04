package com.example.fiona.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView display_name,display_status;
    private Button change_profile_image,change_profile_status;
    private final static int Gallery_Pick=1;
    private ProgressBar progressName;
    private DatabaseReference getDatabaseReference;
    private StorageReference storeprofileImage,thumbImageReference;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    Bitmap thumb_bitmap=null;
/*
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth=FirebaseAuth.getInstance();
        String user_id=mAuth.getCurrentUser().getUid();
        getDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        getDatabaseReference.keepSynced(true);
        storeprofileImage= FirebaseStorage.getInstance().getReference().child("Profile_images");
        profile_image=(CircleImageView)findViewById(R.id.profile_image);
        display_name=(TextView)findViewById(R.id.username);
        display_status=(TextView)findViewById(R.id.profile_status);
        progressName = findViewById(R.id.progressBar2);
        progressName.animate();
        thumbImageReference=FirebaseStorage.getInstance().getReference().child("Thumb_image");
        change_profile_image=(Button)findViewById(R.id.profile_picture_button);
        change_profile_status=(Button)findViewById(R.id.profile_status_button);
        change_profile_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_val = display_status.getText().toString();
                Intent st = new Intent(SettingsActivity.this, StatusActivity.class);
                st.putExtra("status_val", status_val);
                startActivity(st);
            }
        });
        getDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("User_name").getValue().toString();
                String status=dataSnapshot.child("User_status").getValue().toString();
                final String image=dataSnapshot.child("User_image").getValue().toString();
                String thumb_image=dataSnapshot.child("User_thumb_image").getValue().toString();
                display_status.setText(status);
                display_name.setText(name);
                if(!image.equals("default_image")) {
                    Picasso.with(SettingsActivity.this)
                            .load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_image)
                            .into(profile_image, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_image).into(profile_image);

                                }
                            });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        change_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), Gallery_Pick);



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick&& resultCode==RESULT_OK){
            Uri ImageUri=data.getData();
            CropImage.activity(ImageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();
                File thumb_filePathUri=new File(resultUri.getPath());

                String user_id=mAuth.getCurrentUser().getUid();
                try {
                    thumb_bitmap=new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);
                }
                catch (IOException e){
                    e.printStackTrace();

                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final byte[] thumb_byte=byteArrayOutputStream.toByteArray();
                StorageReference filepath=storeprofileImage.child(user_id+".jpg");
                final StorageReference thumb_filepath=thumbImageReference.child(user_id+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                      @Override
                                                                      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                          if(task.isSuccessful()){
                                                                              Toast.makeText(SettingsActivity.this,"Profile Picture Updated",Toast.LENGTH_LONG);
                                                                              final String downloadUrl=task.getResult().getDownloadUrl().toString();
                                                                                 UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                                                                                 uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                                     @Override
                                                                                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                                                                         String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();
                                                                                         if(thumb_task.isSuccessful()){
                                                                                             Map update_user_data=new HashMap();
                                                                                             update_user_data.put("User_image",downloadUrl);
                                                                                             update_user_data.put("User_thumb_image",thumb_download_url);
                                                                                             getDatabaseReference.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                 @Override
                                                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                                                     Toast.makeText(SettingsActivity.this,"Image Uplaoded Successfully",Toast.LENGTH_LONG);

                                                                                                     mProgressDialog.dismiss();

                                                                                                 }
                                                                                             });
                                                                                         }
                                                                                            }
                                                                                 });

                                                                          }
                                                                          else{
                                                                              Toast.makeText(SettingsActivity.this,"Error Occured",Toast.LENGTH_LONG);
                                                                                mProgressDialog.dismiss();
                                                                                  }
                                                                      }
                                                                  }
                );
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
}
