package com.example.parentwithsubscription.features.contacts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.contacts.model.PhoneContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhoneContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTACT = 1;

    private List<PhoneContact> contactsList;
    private List<PhoneContact> filteredList;

    public PhoneContactsAdapter(List<PhoneContact> contactsList) {
        this.contactsList = sortAndGroupContacts(contactsList);
        this.filteredList = new ArrayList<>(this.contactsList);
    }

    private List<PhoneContact> sortAndGroupContacts(List<PhoneContact> contacts) {
        List<PhoneContact> sortedList = new ArrayList<>();
        Collections.sort(contacts, Comparator.comparing(PhoneContact::getName));
        char lastHeader = '\0';

        for (PhoneContact contact : contacts) {
            char firstChar = Character.toUpperCase(contact.getName().charAt(0));
            if (firstChar != lastHeader) {
                sortedList.add(new PhoneContact(String.valueOf(firstChar), true)); // Add header
                lastHeader = firstChar;
            }
            sortedList.add(contact); // Add contact
        }

        return sortedList;
    }


    public void filter(String query) {
        filteredList.clear();
        char lastAddedHeader = '\0'; // Keeps track of the last added header

        if (query.isEmpty()) {
            filteredList.addAll(contactsList);
        } else {
            for (PhoneContact contact : contactsList) {
                if (!contact.isHeader() && contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    char firstChar = Character.toUpperCase(contact.getName().charAt(0));

                    // Add the header only once for each alphabet
                    if (firstChar != lastAddedHeader) {
                        filteredList.add(new PhoneContact(String.valueOf(firstChar), true)); // Add header
                        lastAddedHeader = firstChar; // Update the last added header
                    }

                    filteredList.add(contact); // Add the contact
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return filteredList.get(position).isHeader() ? TYPE_HEADER : TYPE_CONTACT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                viewType == TYPE_HEADER ? R.layout.contact_header : R.layout.contact_item, parent, false);
        return viewType == TYPE_HEADER ? new HeaderViewHolder(view) : new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PhoneContact contact = filteredList.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerTitle.setText(contact.getName());
        } else {
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            contactViewHolder.contactName.setText(contact.getName());
            contactViewHolder.contactPhoneNumber.setText(contact.getPhoneNumber());
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactPhoneNumber;

        ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            contactPhoneNumber = itemView.findViewById(R.id.contactPhoneNumber);
        }
    }
}
