package com.example.parentwithsubscription.features.socialmedia.model.instagram;

import java.util.List;

public class InstagramContactList {
        public List<InstagramContact> followers;
        public List<InstagramContact> following;

    @Override
    public String toString() {
        return "InstagramContactList{" +
                "followers=" + followers +
                ", following=" + following +
                '}';
    }
}
