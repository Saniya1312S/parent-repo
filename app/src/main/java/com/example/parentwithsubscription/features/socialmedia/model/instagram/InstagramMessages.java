package com.example.parentwithsubscription.features.socialmedia.model.instagram;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstagramMessages {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("message_detail")
    private List<InstagramMessageDetails> instagramMessageDetails;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<InstagramMessageDetails> getInstagramMessageDetails() {
        return instagramMessageDetails;
    }

    public void setInstagramMessageDetails(List<InstagramMessageDetails> instagramMessageDetails) {
        this.instagramMessageDetails = instagramMessageDetails;
    }

    @Override
    public String toString() {
        return "InstagramMessages{" +
                "userId='" + userId + '\'' +
                ", instagramMessageDetails=" + instagramMessageDetails +
                '}';
    }
}
