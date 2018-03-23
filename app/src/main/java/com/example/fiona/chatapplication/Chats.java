package com.example.fiona.chatapplication;

/**
 * Created by Fiona on 18-03-2018.
 */

public class Chats {
    private String User_status;

    public Chats(String user_status) {
        User_status = user_status;
    }

    public String getUser_status() {
        return User_status;
    }

    public void setUser_status(String user_status) {
        User_status = user_status;
    }

    public Chats(){

    }
}
