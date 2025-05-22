package com.example.parentwithsubscription.features.socialmedia.fragment.facebook;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramMessageDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FacebookMessageDetailsFragment extends Fragment {

    private TextView userIdTextView;
    private RecyclerView facebookMessagesRecyclerView;
    private FacebookMessageDetailsAdapter facebookMessagesAdapter;
    private List<InstagramMessageDetails> facebookMessageDetailsList;

    private String userId;
    private String facebookMessagesJson;

    public static FacebookMessageDetailsFragment newInstance(String userId, String facebookMessagesJson) {
        FacebookMessageDetailsFragment fragment = new FacebookMessageDetailsFragment();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        args.putString("facebook_messages_details", facebookMessagesJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.instagram_message_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userIdTextView = view.findViewById(R.id.user_id);
        facebookMessagesRecyclerView = view.findViewById(R.id.instagram_messages_recyclerview);
        facebookMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
            facebookMessagesJson = getArguments().getString("facebook_messages_details");
        }

        userIdTextView.setText(userId == null || userId.isEmpty() ? "Unknown" : userId);

        Gson gson = new Gson();
        Type type = new TypeToken<List<InstagramMessageDetails>>() {}.getType();
        facebookMessageDetailsList = gson.fromJson(facebookMessagesJson, type);

        facebookMessagesAdapter = new FacebookMessageDetailsAdapter(facebookMessageDetailsList);
        facebookMessagesRecyclerView.setAdapter(facebookMessagesAdapter);
    }

    public static class FacebookMessageDetailsAdapter extends RecyclerView.Adapter<FacebookMessageDetailsAdapter.FacebookMessageViewHolder> {

        private final List<InstagramMessageDetails> facebookMessageDetailsList;

        public FacebookMessageDetailsAdapter(List<InstagramMessageDetails> facebookMessageDetailsList) {
            this.facebookMessageDetailsList = facebookMessageDetailsList;
        }

        @NonNull
        @Override
        public FacebookMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_detail_item, parent, false);
            return new FacebookMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FacebookMessageViewHolder holder, int position) {
            InstagramMessageDetails message = facebookMessageDetailsList.get(position);

            String smsContentText = message.getContent();
            String messageStatus = message.getClassification();

            try {
                long timestamp = Long.parseLong(message.getTime());
                String formattedTime = URIConstants.formatTimestamp(timestamp);
                holder.timestamp.setText(formattedTime);
                Log.d("Time formatted", formattedTime);
            } catch (NumberFormatException e) {
                holder.timestamp.setText(message.getTime());
            }

            SpannableString spannableMessage = new SpannableString(smsContentText);

            if ("spam".equalsIgnoreCase(messageStatus)) {
                smsContentText = smsContentText + " (Spam)";
                spannableMessage = new SpannableString(smsContentText);
                int spamStartIndex = smsContentText.length() - 6;
                int spamEndIndex = smsContentText.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), spamStartIndex, spamEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("warning".equalsIgnoreCase(messageStatus)) {
                smsContentText = smsContentText + " (Warning)";
                spannableMessage = new SpannableString(smsContentText);
                int warningStartIndex = smsContentText.length() - 9;
                int warningEndIndex = smsContentText.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")), warningStartIndex, warningEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            Log.d("Spam Ham", spannableMessage.toString());
            holder.messageText.setText(spannableMessage);

            if ("sent".equalsIgnoreCase(message.getMessageType())) {
                holder.messageContainer.setGravity(Gravity.END);
                holder.messageText.setBackgroundResource(R.drawable.facebook_message_bubble_sent);
                holder.messageText.setTextColor(Color.WHITE);
                holder.timestamp.setGravity(Gravity.END);
            } else {
                holder.messageContainer.setGravity(Gravity.START);
                holder.messageText.setBackgroundResource(R.drawable.instagram_message_bubble_received);
                holder.timestamp.setGravity(Gravity.START);
            }
        }

        @Override
        public int getItemCount() {
            return facebookMessageDetailsList.size();
        }

        public static class FacebookMessageViewHolder extends RecyclerView.ViewHolder {
            TextView messageText, timestamp;
            LinearLayout messageContainer;

            public FacebookMessageViewHolder(@NonNull View itemView) {
                super(itemView);
                messageText = itemView.findViewById(R.id.whatsapp_message_content);
                timestamp = itemView.findViewById(R.id.whatsapp_message_date_time);
                messageContainer = itemView.findViewById(R.id.whatsapp_message_container);
            }
        }
    }
}
