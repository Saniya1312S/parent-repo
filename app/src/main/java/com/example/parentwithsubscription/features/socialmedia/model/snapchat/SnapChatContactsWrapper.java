package com.example.parentwithsubscription.features.socialmedia.model.snapchat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SnapChatContactsWrapper {
    @SerializedName("contacts")
    private List<SnapChatContact> snapChatContacts;

    public List<SnapChatContact> getSnapChatContacts() {
        return snapChatContacts;
    }

    public void setSnapChatContacts(List<SnapChatContact> snapChatContacts) {
        this.snapChatContacts = snapChatContacts;
    }
}
