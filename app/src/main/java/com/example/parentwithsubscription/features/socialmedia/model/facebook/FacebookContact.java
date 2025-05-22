package com.example.parentwithsubscription.features.socialmedia.model.facebook;

public class FacebookContact {
    private String user_id;
    private String user_name;

    public FacebookContact(String user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }

    public String getUserId() {
        return user_id;
    }

    public String getUserName() {
        return user_name;
    }
}

