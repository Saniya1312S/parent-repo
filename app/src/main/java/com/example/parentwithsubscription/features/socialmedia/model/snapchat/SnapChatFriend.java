package com.example.parentwithsubscription.features.socialmedia.model.snapchat;

public class SnapChatFriend {

    private String name;
    private boolean isHeader;

    public SnapChatFriend(String name) {
        this.name = name;
        this.isHeader = false;
    }

    public SnapChatFriend(String name, boolean isHeader) {
        this.name = name;
        this.isHeader = isHeader;
    }

    public String getName() {
        return name;
    }

    public boolean isHeader() {
        return isHeader;
    }
}
