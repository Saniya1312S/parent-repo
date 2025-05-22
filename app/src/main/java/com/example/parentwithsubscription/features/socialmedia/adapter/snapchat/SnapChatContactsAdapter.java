package com.example.parentwithsubscription.features.socialmedia.adapter.snapchat;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.socialmedia.model.snapchat.SnapChatContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SnapChatContactsAdapter extends RecyclerView.Adapter<SnapChatContactsAdapter.ContactViewHolder> {

    private List<SnapChatContact> contactList;
    private List<SnapChatContact> filteredContactList;

    // Constructor
    public SnapChatContactsAdapter(List<SnapChatContact> contactList) {
        Log.d("SnapChat contacts in Adapter", contactList.toString());

        this.contactList = new ArrayList<>(contactList);
        this.filteredContactList = new ArrayList<>(contactList);
        sortContacts();  // Sort contacts alphabetically
    }

    // Sort contacts alphabetically by name
    private void sortContacts() {
        Collections.sort(filteredContactList, (contact1, contact2) -> contact1.getUserName().compareToIgnoreCase(contact2.getUserName()));
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each contact item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facbook_contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        SnapChatContact contact = filteredContactList.get(position);
        holder.contactName.setText(contact.getUserName());  // Display user name
        // If you want to display additional details, uncomment below
        // holder.phoneNumber.setText(contact.getPhoneNumber());  // Display phone number or other details if available
    }

    @Override
    public int getItemCount() {
        return filteredContactList.size();
    }

    // Method to filter the contact list based on a search query
    public void filter(String query) {
        Log.d("SnapChatContactsAdapter", "Filtering with query: " + query);
        filteredContactList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredContactList.addAll(contactList);  // Reset to original list
        } else {
            for (SnapChatContact contact : contactList) {
                if (contact.getUserName().toLowerCase().contains(query.toLowerCase())) {
                    filteredContactList.add(contact);
                }
            }
        }
        sortContacts();  // Re-sort after filtering
        notifyDataSetChanged();  // Notify the adapter to update the view
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.user_name);
        }
    }
}









/*
package com.example.singletablayoutwithdiffadaptersocialmedia.snapchat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singletablayoutwithdiffadaptersocialmedia.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SnapChatContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTACT = 1;

    private List<SnapChatFriend> contactsList;
    private List<SnapChatFriend> filteredList;

    public SnapChatContactsAdapter(List<SnapChatContact> contactsList) {
        this.contactsList = sortAndGroupContacts(contactsList);
        this.filteredList = new ArrayList<>(this.contactsList);
        Log.d("SnapChatContactsAdapter", "Adapter initialized with " + contactsList.size() + " contacts.");
    }

    // Sorting and grouping contacts by the first letter of the name
    private List<SnapChatFriend> sortAndGroupContacts(List<SnapChatContact> contacts) {
        List<SnapChatFriend> sortedList = new ArrayList<>();
        Collections.sort(contacts, Comparator.comparing(SnapChatContact::getUserName));
        char lastHeader = '\0';

        for (SnapChatContact contact : contacts) {
            char firstChar = Character.toUpperCase(contact.getUserName().charAt(0));
            if (firstChar != lastHeader) {
                sortedList.add(new SnapChatFriend(String.valueOf(firstChar), true)); // Add header
                lastHeader = firstChar;
            }
            sortedList.add(new SnapChatFriend(contact.getUserName())); // Add contact
        }

        Log.d("SnapChatContactsAdapter", "Sorted and grouped contacts: " + sortedList.size() + " items.");
        return sortedList;
    }

    // Filter method for search functionality
    public void filter(String query) {
        Log.d("SnapChatContactsAdapter", "Filtering with query: " + query);
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(contactsList);
        } else {
            char lastHeader = '\0';
            for (SnapChatFriend contact : contactsList) {
                if (contact.isHeader()) {
                    lastHeader = contact.getName().charAt(0);
                } else if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    if (!filteredList.isEmpty() && filteredList.get(filteredList.size() - 1).isHeader() &&
                            filteredList.get(filteredList.size() - 1).getName().charAt(0) == lastHeader) {
                        // Avoid duplicate headers
                    } else {
                        filteredList.add(new SnapChatFriend(String.valueOf(lastHeader), true)); // Add header
                    }
                    filteredList.add(contact);
                }
            }
        }
        notifyDataSetChanged();
        Log.d("SnapChatContactsAdapter", "Filtering completed. Filtered list size: " + filteredList.size());
    }

    @Override
    public int getItemViewType(int position) {
        return filteredList.get(position).isHeader() ? TYPE_HEADER : TYPE_CONTACT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                viewType == TYPE_HEADER ? R.layout.item_header : R.layout.item_friend, parent, false);
        return viewType == TYPE_HEADER ? new HeaderViewHolder(view) : new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SnapChatFriend contact = filteredList.get(position);

        Log.d("SnapChatContactsAdapter", "Binding view for position: " + position + " - " + contact.getName());

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerTitle.setText(contact.getName());
            Log.d("SnapChatContactsAdapter", "Header: " + contact.getName());
        } else {
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            contactViewHolder.contactName.setText(contact.getName());

            // Check if the divider should be shown
            if (position < filteredList.size() - 1) {
                SnapChatFriend nextContact = filteredList.get(position + 1);
                boolean shouldShowDivider = !nextContact.isHeader();  // Hide if next item is a header

                contactViewHolder.divider.setVisibility(shouldShowDivider ? View.VISIBLE : View.GONE);
            } else {
                contactViewHolder.divider.setVisibility(View.GONE); // Hide divider for the last contact
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // ViewHolder for the header
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }

    // ViewHolder for the contact
    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactEmoji;
        View divider; // Divider view

        ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.friendName);
            contactEmoji = itemView.findViewById(R.id.friendEmoji);
            divider = itemView.findViewById(R.id.divider); // Initialize the divider
        }
    }
}
*/








/*
package com.example.singletablayoutwithdiffadaptersocialmedia.snapchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singletablayoutwithdiffadaptersocialmedia.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SnapChatContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTACT = 1;

    private List<SnapChatFriend> contactsList;
    private List<SnapChatFriend> filteredList;

    public SnapChatContactsAdapter(List<SnapChatContact> contactsList) {
        this.contactsList = sortAndGroupContacts(contactsList);
        this.filteredList = new ArrayList<>(this.contactsList);
    }

    // Sorting and grouping contacts by the first letter of the name
    private List<SnapChatFriend> sortAndGroupContacts(List<SnapChatContact> contacts) {
        List<SnapChatFriend> sortedList = new ArrayList<>();
        Collections.sort(contacts, Comparator.comparing(SnapChatContact::getUserName));
        char lastHeader = '\0';

        for (SnapChatContact contact : contacts) {
            char firstChar = Character.toUpperCase(contact.getUserName().charAt(0));
            if (firstChar != lastHeader) {
                sortedList.add(new SnapChatFriend(String.valueOf(firstChar), true)); // Add header
                lastHeader = firstChar;
            }
            sortedList.add(new SnapChatFriend(contact.getUserName())); // Add contact
        }
        return sortedList;
    }

    // Filter method for search functionality
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(contactsList);
        } else {
            char lastHeader = '\0';
            for (SnapChatFriend contact : contactsList) {
                if (contact.isHeader()) {
                    lastHeader = contact.getName().charAt(0);
                } else if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    if (!filteredList.isEmpty() && filteredList.get(filteredList.size() - 1).isHeader() &&
                            filteredList.get(filteredList.size() - 1).getName().charAt(0) == lastHeader) {
                        // Avoid duplicate headers
                    } else {
                        filteredList.add(new SnapChatFriend(String.valueOf(lastHeader), true)); // Add header
                    }
                    filteredList.add(contact);
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
                viewType == TYPE_HEADER ? R.layout.item_header : R.layout.item_friend, parent, false);
        return viewType == TYPE_HEADER ? new HeaderViewHolder(view) : new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SnapChatFriend contact = filteredList.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerTitle.setText(contact.getName());
        } else {
            ((ContactViewHolder) holder).contactName.setText(contact.getName());
            // Set any other data such as emoji here if needed
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // ViewHolder for the header
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }

    // ViewHolder for the contact
    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactEmoji;
        ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.friendName);
//            contactEmoji = itemView.findViewById(R.id.contactEmoji); // If you want to show emoji
        }
    }
}
*/
