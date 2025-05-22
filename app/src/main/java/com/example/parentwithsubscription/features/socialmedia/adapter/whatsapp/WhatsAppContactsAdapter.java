package com.example.parentwithsubscription.features.socialmedia.adapter.whatsapp;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppContacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhatsAppContactsAdapter extends RecyclerView.Adapter<WhatsAppContactsAdapter.ContactViewHolder> {

    private List<WhatsAppContacts> contactList;
    private List<WhatsAppContacts> filteredContactList;

    public WhatsAppContactsAdapter(List<WhatsAppContacts> contactList) {
        Log.d("Whats App contacts in Adapter", contactList.toString());

        this.contactList = new ArrayList<>(contactList);
        this.filteredContactList = new ArrayList<>(contactList);
        sortContacts();  // Sort contacts alphabetically
    }

    // Sort contacts alphabetically by name
    private void sortContacts() {
        Collections.sort(filteredContactList, (contact1, contact2) -> contact1.getContactName().compareToIgnoreCase(contact2.getContactName()));
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each contact item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_contact_details, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        WhatsAppContacts contact = filteredContactList.get(position);
        holder.contactName.setText(contact.getContactName());
        holder.phoneNumber.setText(contact.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return filteredContactList.size();
    }

    // Method to filter the contact list based on a search query
    public void filter(String query) {
        Log.d("WhatsAppContactsAdapter", "Filtering with query: " + query);
        filteredContactList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredContactList.addAll(contactList);  // If query is empty, show all contacts
        } else {
            for (WhatsAppContacts contact : contactList) {
                if (contact.getContactName().toLowerCase().contains(query.toLowerCase())) {
                    filteredContactList.add(contact);
                }
            }
        }
        sortContacts();  // Re-sort after filtering
        notifyDataSetChanged();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView phoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            phoneNumber = itemView.findViewById(R.id.contact_phone_number);
        }
    }
}












/*
package com.example.parent.features.socialmedia.adapter.whatsapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.parent.R;
import com.example.parent.features.socialmedia.model.whatsapp.WhatsAppContacts;

import java.util.List;

public class WhatsAppContactsAdapter extends RecyclerView.Adapter<WhatsAppContactsAdapter.ContactViewHolder> {

    private List<WhatsAppContacts> contactList;

    public WhatsAppContactsAdapter(List<WhatsAppContacts> contactList) {

        Log.d("Whats App contacts in Adapter", contactList.toString());

        this.contactList = contactList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each contact item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_contact_details, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        WhatsAppContacts contact = contactList.get(position);
        holder.contactName.setText(contact.getContactName());
        holder.phoneNumber.setText(contact.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView phoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            phoneNumber = itemView.findViewById(R.id.contact_phone_number);
        }
    }
}
*/
