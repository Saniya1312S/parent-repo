package com.example.parentwithsubscription.features.smslogs.adapter;/*
package com.example.parent.features.smslogs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.smslogs.model.SMSLog;

import java.util.List;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.MessageViewHolder> {

    private List<SMSLog> messageList;

    public SMSAdapter(List<SMSLog> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_log_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        SMSLog message = messageList.get(position);
        holder.senderName.setText(message.getSenderName());
        holder.messageText.setText(message.getMessageText());
        holder.messageTime.setText(message.getMessageTime());
        holder.senderImage.setImageResource(message.getSenderImage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText, messageTime;
        ImageView senderImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
            senderImage = itemView.findViewById(R.id.sender_image);
        }
    }
}

*/
