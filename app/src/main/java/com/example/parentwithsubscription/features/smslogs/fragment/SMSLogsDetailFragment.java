package com.example.parentwithsubscription.features.smslogs.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.smslogs.model.SMSLogsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SMSLogsDetailFragment extends Fragment {

    private TextView contactNameTextView, phoneNumberTextView;
    private RecyclerView smsLogsRecyclerView;
    private SMSLogsAdapter smsLogsAdapter;
    private List<SMSLogsDetail> smsLogsDetailList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sms_logs_detail, container, false);

        smsLogsRecyclerView = rootView.findViewById(R.id.sms_logs_recyclerview);
        contactNameTextView = rootView.findViewById(R.id.contact_name);
        phoneNumberTextView = rootView.findViewById(R.id.contact_phone_number);

        smsLogsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        if (args != null) {
            String contactName = args.getString("contact_name");
            String phoneNumber = args.getString("contact_phone_number");
            String smsJson = args.getString("sms_details");

            contactNameTextView.setText((contactName == null || contactName.isEmpty()) ? "Unknown" : contactName);
            phoneNumberTextView.setText(phoneNumber);

            Gson gson = new Gson();
            Type type = new TypeToken<List<SMSLogsDetail>>() {}.getType();
            smsLogsDetailList = gson.fromJson(smsJson, type);

            smsLogsAdapter = new SMSLogsAdapter(getContext(), smsLogsDetailList);
            smsLogsRecyclerView.setAdapter(smsLogsAdapter);
        }

        return rootView;
    }

    public static class SMSLogsAdapter extends RecyclerView.Adapter<SMSLogsAdapter.SMSLogsViewHolder> {

        private List<SMSLogsDetail> smsLogsDetailList;
        private final Context context;

        public SMSLogsAdapter(Context context, List<SMSLogsDetail> smsLogsDetailList) {
            this.context = context;
            this.smsLogsDetailList = smsLogsDetailList;
        }

        @Override
        public SMSLogsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.sms_logs_detail_item, parent, false);
            return new SMSLogsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SMSLogsViewHolder holder, int position) {
            SMSLogsDetail smsLogsDetail = smsLogsDetailList.get(position);

            String smsContentText = smsLogsDetail.getContent();
            holder.smsContent.setText(applyMessageStatus(smsContentText, smsLogsDetail.getSmsClassificationType()));

            try {
                long timestamp = Long.parseLong(smsLogsDetail.getTime());
                String formattedTime = URIConstants.formatTimestamp(timestamp);
                holder.smsDateTime.setText(formattedTime);
            } catch (NumberFormatException e) {
                holder.smsDateTime.setText(smsLogsDetail.getTime());
            }

            String smsType = smsLogsDetail.getSmsType().trim().toLowerCase();
            if ("sent".equalsIgnoreCase(smsType)) {
                holder.messageContainer.setGravity(Gravity.END);
                holder.smsContent.setBackgroundResource(R.drawable.sms_message_bubble_sent);
                holder.smsContent.setTextColor(context.getResources().getColor(R.color.white));
                holder.smsDateTime.setGravity(Gravity.END);
            } else {
                holder.messageContainer.setGravity(Gravity.START);
                holder.smsContent.setBackgroundResource(R.drawable.sms_message_bubble_received);
                holder.smsDateTime.setGravity(Gravity.START);
            }
        }

        @Override
        public int getItemCount() {
            return smsLogsDetailList.size();
        }

        private SpannableString applyMessageStatus(String content, String status) {
            SpannableString spannableMessage = new SpannableString(content);
            if ("spam".equalsIgnoreCase(status)) {
                content += " (Spam)";
                spannableMessage = new SpannableString(content);
                spannableMessage.setSpan(
                        new android.text.style.ForegroundColorSpan(context.getColor(R.color.red)),
                        content.length() - 6, content.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            } else if ("warning".equalsIgnoreCase(status)) {
                content += " (Warning)";
                spannableMessage = new SpannableString(content);
                spannableMessage.setSpan(
                        new android.text.style.ForegroundColorSpan(context.getColor(R.color.colorAccentDark)),
                        content.length() - 9, content.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            return spannableMessage;
        }

        public static class SMSLogsViewHolder extends RecyclerView.ViewHolder {

            TextView smsContent, smsDateTime;
            LinearLayout messageContainer;

            public SMSLogsViewHolder(View itemView) {
                super(itemView);
                smsContent = itemView.findViewById(R.id.sms_content);
                smsDateTime = itemView.findViewById(R.id.sms_date_time);
                messageContainer = itemView.findViewById(R.id.message_container);
            }
        }
    }
}
