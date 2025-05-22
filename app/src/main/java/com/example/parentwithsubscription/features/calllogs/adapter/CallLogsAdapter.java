package com.example.parentwithsubscription.features.calllogs.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.calllogs.listener.OnCallLogClickListener;
import com.example.parentwithsubscription.features.calllogs.model.CallLogs;

import java.util.List;

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.ContactViewHolder> {

    private List<CallLogs> contactList;
    private OnCallLogClickListener listener;
    private Context context;
    private RecyclerView recyclerView;  // Reference to RecyclerView

    public CallLogsAdapter(List<CallLogs> contactList, OnCallLogClickListener listener, Context context, RecyclerView recyclerView) {
        this.contactList = contactList;
        this.listener = listener;
        this.context = context;
        this.recyclerView = recyclerView;  // Initialize RecyclerView reference
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false);
        return new ContactViewHolder(view);
    }

    /*@Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        CallLogs contact = contactList.get(position);

        // Set the contact name with count
        String displayName = contact.getName() + " (" + contact.getCount() + ")";

        if (contact.getName() == null || contact.getName().trim().isEmpty() || contact.getName().equalsIgnoreCase("unknown")) {
            displayName = "Unknown Contact (" + contact.getCount() + ")";
            holder.nameTextView.setTextColor(Color.RED);
        } else {
            holder.nameTextView.setTextColor(Color.BLACK);
        }

        holder.nameTextView.setText(displayName);
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        // Set the switch state based on the blocked state of the contact
        holder.blockSwitch.setChecked(contact.isBlocked());

        // Set the block/unblock switch toggle listener
        holder.blockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the blocked state of the contact only if RecyclerView is not computing layout
            if (!isRecyclerViewComputingLayout()) {
                contact.setBlocked(isChecked);

                // Use post() to safely notify the adapter
                holder.itemView.post(() -> {
                    // Notify that the specific contact has changed
                    notifyItemChanged(position);

                    // Show a message based on the toggle state
                    String message = isChecked ? "Contact " + contact.getName() + " has been blocked."
                            : "Contact " + contact.getName() + " has been unblocked.";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(context, "Layout is being updated. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
*/
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        CallLogs contact = contactList.get(position);

        // Set the contact name with count
        String displayName = contact.getName() + " (" + contact.getCount() + ")";

        if (contact.getName() == null || contact.getName().trim().isEmpty() || contact.getName().equalsIgnoreCase("unknown")) {
            displayName = "Unknown Contact (" + contact.getCount() + ")";
            holder.nameTextView.setTextColor(Color.RED);
        } else {
            holder.nameTextView.setTextColor(Color.BLACK);
        }

        holder.nameTextView.setText(displayName);
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            // Adding a log statement to check if the listener is being triggered
            Log.d("CallLogsAdapter", "Item clicked: " + contact.getName());
            listener.onContactClick(contact);
        });

        // Set the block/unblock switch toggle listener (your existing code)
// Set the switch state based on the blocked state of the contact
        holder.blockSwitch.setChecked(contact.isBlocked());

        // Change the color of the switch depending on the state
        if (contact.isBlocked()) {
            holder.blockSwitch.setThumbTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
            holder.blockSwitch.setTrackTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
//            holder.blockedTextView.setText("Unblock"); // When blocked, show "Unblock"
        } /*else {
            holder.blockSwitch.setThumbTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
            holder.blockSwitch.setTrackTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
//            holder.blockedTextView.setText("Block?"); // When not blocked, show "Block"
        }*/

        // Set the block/unblock switch toggle listener
        holder.blockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the blocked state of the contact only if RecyclerView is not computing layout
            if (!isRecyclerViewComputingLayout()) {
                contact.setBlocked(isChecked);

                // Use post() to safely notify the adapter
                holder.itemView.post(() -> {
                    // Notify that the specific contact has changed
                    notifyItemChanged(position);

                    // Show a message based on the toggle state
                    String message = isChecked ? "Contact " + contact.getName() + " has been blocked."
                            : "Contact " + contact.getName() + " has been unblocked.";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(context, "Layout is being updated. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // Helper method to check if the RecyclerView is computing layout
    private boolean isRecyclerViewComputingLayout() {
        return recyclerView != null && recyclerView.isComputingLayout();  // Check if RecyclerView is in layout state
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneNumberTextView, blockedTextView;
        Switch blockSwitch;  // Reference to the Switch for blocking/unblocking

        public ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneNumberTextView = itemView.findViewById(R.id.contact_phone_number);
            blockSwitch = itemView.findViewById(R.id.contact_toggle); // Initialize the block switch
//            blockedTextView = itemView.findViewById(R.id.contact_blocked_text);
        }
    }
}



/*
package com.example.parent.features.calllogs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.calllogs.listener.OnCallLogClickListener;
import com.example.parent.features.calllogs.model.CallLogs;

import java.util.List;

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.ContactViewHolder> {

    private List<CallLogs> contactList;
    private OnCallLogClickListener listener;
    private Context context;  // Store context for PopupMenu creation

    public CallLogsAdapter(List<CallLogs> contactList, OnCallLogClickListener listener, Context context) {
        this.contactList = contactList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        CallLogs contact = contactList.get(position);

        // Set the contact name with count
        String displayName = contact.getName() + " (" + contact.getCount() + ")";

        // Prepend text if the name is empty or "unknown"
        if (contact.getName() == null || contact.getName().trim().isEmpty() || contact.getName().equalsIgnoreCase("unknown")) {
            displayName = "Unknown Contact (" + contact.getCount() + ")"; // Prepending "Unknown Contact"
            holder.nameTextView.setTextColor(Color.RED); // Set text color to red for unknown names
        } else {
            holder.nameTextView.setTextColor(Color.BLACK); // Set text color to black for valid names
        }

        // Set the final name with prepended text if needed
        holder.nameTextView.setText(displayName);

        // Set the phone number
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> listener.onContactClick(contact));

        // Set click listener for the three dots
        holder.threeDotsImageView.setOnClickListener(v -> {
            showPopupMenu(v, contact); // Show the popup menu when the three dots are clicked
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private void showPopupMenu(View view, CallLogs contact) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_contact_options); // Inflate your menu XML with block option

        // Set click listener for menu item
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_block) {
                blockContact(contact); // Call block method when block is selected
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void blockContact(CallLogs contact) {
        // Implement the block contact functionality here
        // For example, update contact status, or show a confirmation toast
        // You can add logic to block the contact or update a database
        listener.onContactBlock(contact); // Trigger a block action from the listener
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneNumberTextView;
        ImageView threeDotsImageView;  // Reference to the three dots ImageView

        public ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneNumberTextView = itemView.findViewById(R.id.contact_phone_number);
            threeDotsImageView = itemView.findViewById(R.id.ic_three_dots); // Initialize three dots ImageView
        }
    }
}
*/










/*
package com.example.contacticons;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private OnContactClickListener listener;

    public ContactAdapter(List<Contact> contactList, OnContactClickListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName()+ " (" + contact.getCount()+")");
        holder.phoneNumberTextView.setText(contact.getPhoneNumber()); // Display phone number

        holder.itemView.setOnClickListener(v -> listener.onContactClick(contact));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneNumberTextView, countTextView;

        public ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneNumberTextView = itemView.findViewById(R.id.contact_phone_number);
        }
    }
}
*/
