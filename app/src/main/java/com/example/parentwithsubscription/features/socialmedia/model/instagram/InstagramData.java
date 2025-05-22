package com.example.parentwithsubscription.features.socialmedia.model.instagram;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstagramData {

    @SerializedName("call_log")
    private List<InstagramCalls> instagramCalls;

    @SerializedName("message_log")
    private List<InstagramMessages> instagramMessages;

    public List<InstagramCalls> getInstagramCalls() {
        return instagramCalls;
    }

    public void setInstagramCalls(List<InstagramCalls> instagramCalls) {
        this.instagramCalls = instagramCalls;
    }

    public List<InstagramMessages> getInstagramMessages() {
        return instagramMessages;
    }

    public void setInstagramMessages(List<InstagramMessages> instagramMessages) {
        this.instagramMessages = instagramMessages;
    }
}
