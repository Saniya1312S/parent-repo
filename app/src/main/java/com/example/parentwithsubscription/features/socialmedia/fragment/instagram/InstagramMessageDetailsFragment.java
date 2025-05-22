package com.example.parentwithsubscription.features.socialmedia.fragment.instagram;

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

public class InstagramMessageDetailsFragment extends Fragment {

    private TextView userIdTextView;
    private RecyclerView instagramMessagesRecyclerView;
    private InstagramMessageDetailsAdapter instagramMessagesAdapter;
    private List<InstagramMessageDetails> instagramMessageDetailsList;

    private String userId;
    private String instagramMessagesJson;

    public static InstagramMessageDetailsFragment newInstance(String userId, String instagramMessagesJson) {
        InstagramMessageDetailsFragment fragment = new InstagramMessageDetailsFragment();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        args.putString("instagram_messages_details", instagramMessagesJson);
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
        instagramMessagesRecyclerView = view.findViewById(R.id.instagram_messages_recyclerview);
        instagramMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
            instagramMessagesJson = getArguments().getString("instagram_messages_details");
        }

        userIdTextView.setText(userId == null || userId.isEmpty() ? "Unknown" : userId);

        Gson gson = new Gson();
        Type type = new TypeToken<List<InstagramMessageDetails>>() {}.getType();
        instagramMessageDetailsList = gson.fromJson(instagramMessagesJson, type);

        instagramMessagesAdapter = new InstagramMessageDetailsAdapter(instagramMessageDetailsList);
        instagramMessagesRecyclerView.setAdapter(instagramMessagesAdapter);
    }

    public static class InstagramMessageDetailsAdapter extends RecyclerView.Adapter<InstagramMessageDetailsAdapter.InstagramMessageViewHolder> {

        private final List<InstagramMessageDetails> instagramMessageDetailsList;

        public InstagramMessageDetailsAdapter(List<InstagramMessageDetails> instagramMessageDetailsList) {
            this.instagramMessageDetailsList = instagramMessageDetailsList;
        }

        @NonNull
        @Override
        public InstagramMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_detail_item, parent, false);
            return new InstagramMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InstagramMessageViewHolder holder, int position) {
            InstagramMessageDetails message = instagramMessageDetailsList.get(position);

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
                smsContentText += " (Spam)";
                spannableMessage = new SpannableString(smsContentText);
                int start = smsContentText.length() - 6;
                spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), start, smsContentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("warning".equalsIgnoreCase(messageStatus)) {
                smsContentText += " (Warning)";
                spannableMessage = new SpannableString(smsContentText);
                int start = smsContentText.length() - 9;
                spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")), start, smsContentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            holder.messageText.setText(spannableMessage);

            if ("sent".equalsIgnoreCase(message.getMessageType())) {
                holder.messageContainer.setGravity(Gravity.END);
                holder.messageText.setBackgroundResource(R.drawable.instagram_message_bubble_sent);
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
            return instagramMessageDetailsList.size();
        }

        public static class InstagramMessageViewHolder extends RecyclerView.ViewHolder {

            TextView messageText, timestamp;
            LinearLayout messageContainer;

            public InstagramMessageViewHolder(@NonNull View itemView) {
                super(itemView);
                messageText = itemView.findViewById(R.id.whatsapp_message_content);
                timestamp = itemView.findViewById(R.id.whatsapp_message_date_time);
                messageContainer = itemView.findViewById(R.id.whatsapp_message_container);
            }
        }
    }
}
