package com.example.fiona.chatapplication;

import android.app.VoiceInteractor;

/**
 * Created by Fiona on 23-03-2018.
 */

public class Requests {
    private String User_name,User_status,User_thumb_image;
    public Requests(){

    }
    public Requests(String user_name, String user_status, String user_thumb_image) {
        User_name = user_name;
        User_status = user_status;
        User_thumb_image = user_thumb_image;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getUser_status() {
        return User_status;
    }

    public void setUser_status(String user_status) {
        User_status = user_status;
    }

    public String getUser_thumb_image() {
        return User_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        User_thumb_image = user_thumb_image;
    }
}
