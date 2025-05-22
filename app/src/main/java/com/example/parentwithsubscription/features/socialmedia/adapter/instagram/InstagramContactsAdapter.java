package com.example.parentwithsubscription.features.socialmedia.adapter.instagram;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstagramContactsAdapter extends RecyclerView.Adapter<InstagramContactsAdapter.ContactViewHolder> {

    private List<InstagramContact> contactList;  // List to display contacts
    private List<InstagramContact> originalContactList;  // Backup list for restoring original contacts
    private Set<String> followersSet;  // Set to store followers' usernames
    private Set<String> followingSet;  // Set to store following usernames

    public InstagramContactsAdapter(List<InstagramContact> contactList, List<InstagramContact> followers, List<InstagramContact> following) {
        this.originalContactList = new ArrayList<>(contactList);  // Create a backup of the original list
        this.contactList = contactList;  // Store the current list
        this.followersSet = new HashSet<>();
        this.followingSet = new HashSet<>();

        // Populate the sets with followers and following usernames
        for (InstagramContact follower : followers) {
            followersSet.add(follower.userName);
        }

        for (InstagramContact follow : following) {
            followingSet.add(follow.userName);
        }

        sortContacts();  // Ensure the list is sorted when the adapter is first created
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each contact item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instagram_contact_details_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        InstagramContact contact = contactList.get(position);  // Get the contact at the current position
        holder.usernameTextView.setText(contact.userName);
        holder.fullnameTextView.setText(contact.fullName);

        // Check if the contact is a follower or following
        boolean isFollower = followersSet.contains(contact.userName);
        boolean isFollowing = followingSet.contains(contact.userName);

        // Set the images for follower and following status
        if (isFollower) {
            holder.followerTickImageView.setVisibility(View.VISIBLE);
            holder.followerTickImageView.setImageResource(R.drawable.ic_tick);  // Show a tick for follower
        } else {
            holder.followerTickImageView.setVisibility(View.VISIBLE);
            holder.followerTickImageView.setImageResource(R.drawable.ic_cross);  // Show a cross for not follower
        }

        if (isFollowing) {
            holder.followingTickImageView.setVisibility(View.VISIBLE);
            holder.followingTickImageView.setImageResource(R.drawable.ic_tick);  // Show a tick for following
        } else {
            holder.followingTickImageView.setVisibility(View.VISIBLE);
            holder.followingTickImageView.setImageResource(R.drawable.ic_cross);  // Show a cross for not following
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();  // Return the size of the filtered list
    }

    // Method to filter the contact list based on the search query
    public void filter(String query) {
        // If the query is empty, reset the list to the original list
        if (query == null || query.isEmpty()) {
            contactList = new ArrayList<>(originalContactList);
        } else {
            // Create a new list to store the filtered contacts
            List<InstagramContact> filteredList = new ArrayList<>();
            // Loop through the original list and check if the contact matches the query
            for (InstagramContact contact : originalContactList) {
                if (contact.userName.toLowerCase().contains(query.toLowerCase()) ||
                        contact.fullName.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(contact);
                }
            }
            // Update the contactList with the filtered list
            contactList = filteredList;
        }

        // Sort the contact list after filtering
        sortContacts();

        // Notify the adapter that the data has changed, so the RecyclerView will be updated
        notifyDataSetChanged();
    }

    // Method to sort the contact list alphabetically by user name
    private void sortContacts() {
        Collections.sort(contactList, new Comparator<InstagramContact>() {
            @Override
            public int compare(InstagramContact contact1, InstagramContact contact2) {
                return contact1.userName.compareToIgnoreCase(contact2.userName); // Sort by userName
            }
        });
    }

    // ViewHolder class to hold the views for each contact item
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, fullnameTextView;
        ImageView followerTickImageView, followingTickImageView;

        public ContactViewHolder(View itemView) {
            super(itemView);
            // Initialize the views for each item in the RecyclerView
            fullnameTextView = itemView.findViewById(R.id.fullnameTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            followerTickImageView = itemView.findViewById(R.id.followerTickImageView);
            followingTickImageView = itemView.findViewById(R.id.followingTickImageView);
        }
    }
}









/*
package com.example.parent.features.socialmedia.adapter.instagram;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.socialmedia.model.instagram.InstagramContact;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstagramContactsAdapter extends RecyclerView.Adapter<InstagramContactsAdapter.ContactViewHolder> {

    private List<InstagramContact> contactList;
    private Set<String> followersSet;
    private Set<String> followingSet;

    public InstagramContactsAdapter(List<InstagramContact> contactList, List<InstagramContact> followers, List<InstagramContact> following) {
        this.contactList = contactList;

        // Create sets for fast lookup
        followersSet = new HashSet<>();
        followingSet = new HashSet<>();

        // Populate the sets
        for (InstagramContact follower : followers) {
            followersSet.add(follower.userName);
        }

        for (InstagramContact follow : following) {
            followingSet.add(follow.userName);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instagram_contact_details_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Log.d("Instagram Contacts List", "Binding item at position " + position);  // Check if this log appears
        InstagramContact contact = contactList.get(position);
        holder.usernameTextView.setText(contact.userName);
        holder.fullnameTextView.setText(contact.fullName);

        Log.d("Full Name", "Full Name: " + contact.fullName);  // Log full name

        boolean isFollower = followersSet.contains(contact.userName);
        boolean isFollowing = followingSet.contains(contact.userName);

        // Log follower and following status
        Log.d("Follower Status", "Is Follower: " + isFollower);
        Log.d("Following Status", "Is Following: " + isFollowing);

        // Set the images as per follower/following status
        if (isFollower) {
            holder.followerTickImageView.setVisibility(View.VISIBLE);
            holder.followerTickImageView.setImageResource(R.drawable.ic_tick); // Tick icon for follower
        } else {
            holder.followerTickImageView.setVisibility(View.VISIBLE);
            holder.followerTickImageView.setImageResource(R.drawable.ic_cross); // Cross icon for not follower
        }

        if (isFollowing) {
            holder.followingTickImageView.setVisibility(View.VISIBLE);
            holder.followingTickImageView.setImageResource(R.drawable.ic_tick); // Tick icon for following
        } else {
            holder.followingTickImageView.setVisibility(View.VISIBLE);
            holder.followingTickImageView.setImageResource(R.drawable.ic_cross); // Cross icon for not following
        }
    }


    @Override
    public int getItemCount() {
        Log.d("Item Count", "Item count: " + contactList.size());
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, fullnameTextView;
        ImageView followerTickImageView;
        ImageView followingTickImageView;

        public ContactViewHolder(View itemView) {
            super(itemView);
            fullnameTextView = itemView.findViewById(R.id.fullnameTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            followerTickImageView = itemView.findViewById(R.id.followerTickImageView);
            followingTickImageView = itemView.findViewById(R.id.followingTickImageView);
        }
    }
}

*/





/*
package com.example.instagramcontactsui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Set<String> followersSet;
    private Set<String> followingSet;

    public ContactsAdapter(List<Contact> contactList, List<Contact> followers, List<Contact> following) {
        this.contactList = contactList;

        // Create sets for fast lookup
        followersSet = new HashSet<>();
        followingSet = new HashSet<>();

        // Populate the sets
        for (Contact follower : followers) {
            followersSet.add(follower.userName);
        }

        for (Contact follow : following) {
            followingSet.add(follow.userName);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.usernameTextView.setText(contact.userName);
        holder.fullnameTextView.setText(contact.fullName);

        Log.d("Full Name", "Full Name: "+ contact.fullName);

        boolean isFollower = followersSet.contains(contact.userName);
        boolean isFollowing = followingSet.contains(contact.userName);

        // Set icons based on follower and following status
        if (isFollower) {
            holder.followerTickImageView.setVisibility(View.VISIBLE);
            holder.followerTickImageView.setImageResource(R.drawable.ic_tick); // Tick icon for follower
        } else {
            holder.followerTickImageView.setVisibility(View.VISIBLE);
            holder.followerTickImageView.setImageResource(R.drawable.ic_cross); // Cross icon for not follower
        }

        if (isFollowing) {
            holder.followingTickImageView.setVisibility(View.VISIBLE);
            holder.followingTickImageView.setImageResource(R.drawable.ic_tick); // Tick icon for following
        } else {
            holder.followingTickImageView.setVisibility(View.VISIBLE);
            holder.followingTickImageView.setImageResource(R.drawable.ic_cross); // Cross icon for not following
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, fullnameTextView;
        ImageView followerTickImageView;
        ImageView followingTickImageView;

        public ContactViewHolder(View itemView) {
            super(itemView);
            fullnameTextView = itemView.findViewById(R.id.fullnameTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            followerTickImageView = itemView.findViewById(R.id.followerTickImageView);
            followingTickImageView = itemView.findViewById(R.id.followingTickImageView);
        }
    }
}
*/
