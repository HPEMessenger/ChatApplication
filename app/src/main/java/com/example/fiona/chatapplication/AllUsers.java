package com.example.fiona.chatapplication;

import android.content.Context;

/**
 * Created by Fiona on 03-02-2018.
 */

public class AllUsers {
    private String User_name;
    private String User_status;
    private String User_image;
    private String User_thumb_image;
    public AllUsers(){

    }
    public AllUsers(String User_name, String User_status, String User_image,String User_thumb_image) {
        this.User_name = User_name;
        this.User_status = User_status;
        this.User_image = User_image;
        this.User_thumb_image = User_thumb_image;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String User_name) {
        this.User_name = User_name;
    }

    public String getUser_status() {
        return User_status;
    }

    public void setUser_status(String User_status) {
        this.User_status = User_status;
    }

    public String getUser_image() {
        return User_image;
    }

    public void setUser_image(Context ctx,String User_image) {
        this.User_image = User_image;
    }



    public String getUser_thumb_image() {
        return User_thumb_image;
    }

    public void setUser_thumb_image(String User_thumb_image) {
        this.User_thumb_image = User_thumb_image;
    }
}
