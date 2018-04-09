package com.example.fiona.chatapplication;

/**
 * Created by Fiona on 23-03-2018.
 */

public class Requestnew {
    private String User_name,user_status,thumb_image;
    public Requestnew(){

    }
    public Requestnew(String User_name, String thumb_image, String user_status) {
        User_name = User_name;
        thumb_image = thumb_image;
        user_status = user_status;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
