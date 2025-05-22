package com.example.parentwithsubscription.features.socialmedia.adapter.facebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.socialmedia.model.facebook.FacebookContact;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FacebookContactsAdapter extends RecyclerView.Adapter<FacebookContactsAdapter.ContactViewHolder> {

    private List<FacebookContact> contactList;  // This is the list currently being displayed
    private List<FacebookContact> originalContactList;  // Backup list for restoring original contacts

    public FacebookContactsAdapter(List<FacebookContact> contactList) {
        this.originalContactList = new ArrayList<>(contactList);  // Create a backup of the original list
        this.contactList = contactList;  // Store the current list
        sortContacts();  // Ensure the list is sorted when the adapter is first created
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom item layout (facebook_contact_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facbook_contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        FacebookContact contact = contactList.get(position);

        // Bind the user's name
        holder.userNameTextView.setText(contact.getUserName());

        // For the profile picture, you can load an image from a URL or a placeholder
        // Uncomment the code below and integrate Picasso or another image loading library if you want to load images
        // Example: Picasso.get().load("url_to_profile_picture").into(holder.profilePictureImageView);
    }

    @Override
    public int getItemCount() {
        return contactList.size();  // Return the size of the filtered list
    }

    // Method to filter the contact list based on the search query
    public void filter(String query) {
        if (query == null || query.isEmpty()) {
            // If the query is empty, reset to the original list and sort alphabetically
            contactList.clear();
            contactList.addAll(originalContactList);
        } else {
            List<FacebookContact> filteredList = new ArrayList<>();
            for (FacebookContact contact : originalContactList) {
                if (contact.getUserName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(contact);
                }
            }
            contactList.clear();
            contactList.addAll(filteredList);
        }
        sortContacts();  // Always sort after updating the list
        notifyDataSetChanged();  // Notify the adapter about data changes
    }

    // Method to sort the contact list alphabetically by user name
    private void sortContacts() {
        Collections.sort(contactList, new Comparator<FacebookContact>() {
            @Override
            public int compare(FacebookContact contact1, FacebookContact contact2) {
                return contact1.getUserName().compareToIgnoreCase(contact2.getUserName());
            }
        });
    }

    // ViewHolder to hold the contact's UI elements
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView profilePictureImageView;  // For profile picture
        TextView userNameTextView;  // For user's name

        public ContactViewHolder(View itemView) {
            super(itemView);

            // Find views from the layout
            profilePictureImageView = itemView.findViewById(R.id.profile_picture);
            userNameTextView = itemView.findViewById(R.id.user_name);
        }
    }
}










/*
package com.example.singletablayoutwithdiffadaptersocialmedia.facebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.singletablayoutwithdiffadaptersocialmedia.R;
import com.google.android.material.imageview.ShapeableImageView;
import androidx.recyclerview.widget.RecyclerView;
//import com.squareup.picasso.Picasso; // Picasso for image loading

import java.util.List;

public class FacebookContactsAdapter extends RecyclerView.Adapter<FacebookContactsAdapter.ContactViewHolder> {

    private List<FacebookContact> contactList;

    public FacebookContactsAdapter(List<FacebookContact> contactList) {
        this.contactList = contactList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom item layout (facebook_contact_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facbook_contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        FacebookContact contact = contactList.get(position);

        // Bind the user's name
        holder.userNameTextView.setText(contact.getUserName());

        // For the profile picture, you can load an image from a URL or a placeholder
        // Here we use Picasso (make sure to include Picasso in your dependencies)
        // Example: Picasso.get().load("url_to_profile_picture").into(holder.profilePictureImageView);
//        Picasso.get().load("https://example.com/profile_pictures/" + contact.getUserId() + ".jpg")
//                .placeholder(R.drawable.ic_placeholder) // Placeholder while image loads
//                .into(holder.profilePictureImageView);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // ViewHolder to hold the contact's UI elements
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView profilePictureImageView;
        TextView userNameTextView;

        public ContactViewHolder(View itemView) {
            super(itemView);

            // Find views from the layout
//            profilePictureImageView = itemView.findViewById(R.id.profile_picture);
            userNameTextView = itemView.findViewById(R.id.user_name);
        }
    }
}
*/
