package com.example.parentwithsubscription.features.socialmedia.model.snapchat;

import com.google.gson.annotations.SerializedName;

public class SnapChatContact {

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("contact_name")
    private String user_name;

    public String getUserId() {
        return user_id;
    }

    public String getUserName() {
        return user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
