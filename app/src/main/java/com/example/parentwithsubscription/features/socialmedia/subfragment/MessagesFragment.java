package com.example.parentwithsubscription.features.socialmedia.subfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.GlobalData;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.adapter.instagram.InstagramMessagesAdapter;
import com.example.parentwithsubscription.features.socialmedia.adapter.whatsapp.WhatsAppMessagesAdapter;
import com.example.parentwithsubscription.features.socialmedia.fragment.facebook.FacebookMessageDetailsFragment;
import com.example.parentwithsubscription.features.socialmedia.fragment.instagram.InstagramMessageDetailsFragment;
import com.example.parentwithsubscription.features.socialmedia.fragment.snapchat.SnapChatMessageDetailsFragment;
import com.example.parentwithsubscription.features.socialmedia.fragment.telegram.TelegramMessageDetailsFragment;
import com.example.parentwithsubscription.features.socialmedia.fragment.twitter.TwitterMessageDetailsFragment;
import com.example.parentwithsubscription.features.socialmedia.fragment.whatsapp.WhatsAppMessageDetailsFragment;
import com.example.parentwithsubscription.features.socialmedia.listener.instagram.OnInstagramMessagesClickListener;
import com.example.parentwithsubscription.features.socialmedia.listener.whatsapp.OnWhatsAppMessagesClickListener;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramMessages;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppMessages;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessagesFragment extends Fragment {

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private RecyclerView messagesRecyclerView;
    private WhatsAppMessagesAdapter whatsAppMessagesAdapter;
    private InstagramMessagesAdapter instagramMessagesAdapter;
    private List<WhatsAppMessages> whatsAppMessagesList = new ArrayList<>();
    private List<InstagramMessages> instagramMessagesList = new ArrayList<>();
    private String logType = "messages";

    private String SOCIAL_MEDIA_URL = URIConstants.SOCIAL_MEDIA_URL;

    public static MessagesFragment newInstance(String platform) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages_fragment, container, false);
        messagesRecyclerView = rootView.findViewById(R.id.whatsapp_messages_recycler_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch messages from the API
        fetchMessagesFromApi(platform, logType);
        return rootView;
    }

    private void fetchMessagesFromApi(String platform, String logType) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = SOCIAL_MEDIA_URL + "?log_type=" + logType + "&appname=" + platform + "&device_id=" + GlobalData.getDeviceId();
        Log.d("API_URL", "Request URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "API Request Failed: " + e.getMessage(), e);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "API Request Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String responseData = response.body().string();
                        getActivity().runOnUiThread(() -> {
                            setupRecyclerView(responseData);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "API Request Failed", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void setupRecyclerView(String jsonData) {
        Gson gson = new Gson();

        if (platform.equals("WhatsApp") || platform.equals("Telegram")) {
            Type whatsAppMessagesListType = new TypeToken<List<WhatsAppMessages>>() {}.getType();
            whatsAppMessagesList = gson.fromJson(jsonData, whatsAppMessagesListType);

            whatsAppMessagesAdapter = new WhatsAppMessagesAdapter(whatsAppMessagesList,
                    message -> {
                        Fragment fragment;
                        if (platform.equals("WhatsApp")) {
                            fragment = WhatsAppMessageDetailsFragment.newInstance(
                                    message.getName(),
                                    message.getPhoneNumber(),
                                    new Gson().toJson(message.getWhatsAppMessagesDetails())
                            );
                        } else {
                            fragment = TelegramMessageDetailsFragment.newInstance(
                                    message.getName(),
                                    message.getPhoneNumber(),
                                    new Gson().toJson(message.getWhatsAppMessagesDetails())
                            );
                        }

                        // Replace fragment using the activity’s fragment manager
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
            messagesRecyclerView.setAdapter(whatsAppMessagesAdapter);

        }  else {
        Type instagramMessagesListType = new TypeToken<List<InstagramMessages>>() {}.getType();
        instagramMessagesList = gson.fromJson(jsonData, instagramMessagesListType);

        instagramMessagesAdapter = new InstagramMessagesAdapter(instagramMessagesList, message -> {
            Fragment fragment;

            switch (platform) {
                case "Instagram":
                    fragment = InstagramMessageDetailsFragment.newInstance(
                            message.getUserId(),
                            new Gson().toJson(message.getInstagramMessageDetails())
                    );
                    break;
                case "Facebook":
                    fragment = FacebookMessageDetailsFragment.newInstance(
                            message.getUserId(),
                            new Gson().toJson(message.getInstagramMessageDetails())
                    );
                    break;
                case "Snapchat":
                    fragment = SnapChatMessageDetailsFragment.newInstance(
                            message.getUserId(),
                            new Gson().toJson(message.getInstagramMessageDetails())
                    );
                    break;
                case "Twitter":
                    fragment = TwitterMessageDetailsFragment.newInstance(
                            message.getUserId(),
                            new Gson().toJson(message.getInstagramMessageDetails())
                    );
                    break;
                default:
                    return;
            }

            // Replace fragment using the activity’s fragment manager
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        messagesRecyclerView.setAdapter(instagramMessagesAdapter);
    }

}
}















/*
package com.example.parentwithsubscription.features.socialmedia.subfragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.GlobalData;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.activity.facebook.FacebookMessageDetailsActivity;
import com.example.parentwithsubscription.features.socialmedia.activity.instagram.InstagramMessageDetailsActivity;
import com.example.parentwithsubscription.features.socialmedia.activity.snapchat.SnapChatMessageDetailsActivity;
import com.example.parentwithsubscription.features.socialmedia.activity.telegram.TelegramMessageDetailsActivity;
import com.example.parentwithsubscription.features.socialmedia.activity.twitter.TwitterMessageDetailsActivity;
import com.example.parentwithsubscription.features.socialmedia.activity.whatsapp.WhatsAppMessageDetailsActivity;
import com.example.parentwithsubscription.features.socialmedia.adapter.instagram.InstagramMessagesAdapter;
import com.example.parentwithsubscription.features.socialmedia.adapter.whatsapp.WhatsAppMessagesAdapter;
import com.example.parentwithsubscription.features.socialmedia.listener.instagram.OnInstagramMessagesClickListener;
import com.example.parentwithsubscription.features.socialmedia.listener.whatsapp.OnWhatsAppMessagesClickListener;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramMessages;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppMessages;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessagesFragment extends Fragment {

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private RecyclerView messagesRecyclerView;
    private WhatsAppMessagesAdapter whatsAppMessagesAdapter;
    private InstagramMessagesAdapter instagramMessagesAdapter;
    private List<WhatsAppMessages> whatsAppMessagesList = new ArrayList<>();
    private List<InstagramMessages> instagramMessagesList = new ArrayList<>();
    private String logType = "messages";

    private String SOCIAL_MEDIA_URL = URIConstants.SOCIAL_MEDIA_URL;

    public static MessagesFragment newInstance(String platform) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages_fragment, container, false);
        messagesRecyclerView = rootView.findViewById(R.id.whatsapp_messages_recycler_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        fetchMessages();
        fetchMessagesFromApi(platform, logType);
        return rootView;
    }

    private void fetchMessages() {
        try {
            String jsonData = getJsonData();
            if (jsonData != null && !jsonData.isEmpty()) {
                setupRecyclerView(jsonData);
            }
        } catch (Exception e) {
            Log.e("MessagesFragment", "Error fetching messages: " + e.getMessage());
        }
    }

    private String getJsonData() {
        switch (platform) {
            case "WhatsApp":
            case "Telegram":
                return "["
                        + "{\"phone_number\": \"+917981214937\", \"name\": \"John Doe\", \"count\": \"5\", \"messages\": ["
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey! What's up? Did you get my message?\", \"time\": \"2024-12-10 10:00:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yeah, I saw it! Just been busy with work, you know how it is.\", \"time\": \"2024-12-10 10:05:00\", \"classification\": \"spam\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Totally. I feel that! Also, I got a reminder about that meeting tomorrow. Are you still on for 5 PM?\", \"time\": \"2024-12-10 10:10:00\", \"classification\": \"warning\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yes! I’ll be there, just need to finish up a couple of things first.\", \"time\": \"2024-12-10 10:12:00\", \"classification\": \"spam\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sounds good! Oh, and don't forget about the new promo I sent you for that online course. It's a great deal!\", \"time\": \"2024-12-10 10:15:00\", \"classification\": \"warning\"}"
                        + "]}, "
                        + "{\"phone_number\": \"9398221797\", \"name\": \"Jane Smith\", \"count\": \"4\", \"messages\": ["
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Haha, I almost missed it. Thanks for the heads-up. I'll check it out later.\", \"time\": \"2024-12-10 10:20:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"No problem! It's a really good deal, you should definitely look at it. Let me know if you need the link again.\", \"time\": \"2024-12-10 10:25:00\", \"classification\": \"spam\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Will do! Anyway, are we still meeting for coffee later today?\", \"time\": \"2024-12-10 10:30:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Absolutely! I’ll meet you at 3 PM. See you then!\", \"time\": \"2024-12-10 10:35:00\", \"classification\": \"ham\"}"
                        + "]}, "
                        + "{\"phone_number\": \"9876543210\", \"name\": \"Mike Johnson\", \"count\": \"3\", \"messages\": ["
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey, do you have a moment to chat? I need some advice on a project.\", \"time\": \"2024-12-10 11:00:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure! What's going on?\", \"time\": \"2024-12-10 11:05:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"I’m stuck on a presentation. Can you help me with the structure?\", \"time\": \"2024-12-10 11:10:00\", \"classification\": \"ham\"}"
                        + "]}, "
                        + "{\"phone_number\": \"+917032109876\", \"name\": \"Emily Brown\", \"count\": \"6\", \"messages\": ["
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, I got the files you sent. Thanks for that!\", \"time\": \"2024-12-10 12:00:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"No problem, glad they were useful!\", \"time\": \"2024-12-10 12:05:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Also, did you manage to finish that task I mentioned?\", \"time\": \"2024-12-10 12:10:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Almost there, just need a few more details from you.\", \"time\": \"2024-12-10 12:12:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Got it! I'll send you the details in a bit.\", \"time\": \"2024-12-10 12:15:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Perfect, looking forward to it!\", \"time\": \"2024-12-10 12:18:00\", \"classification\": \"ham\"}"
                        + "]}, "
                        + "{\"phone_number\": \"+919067890123\", \"name\": \"Chris Williams\", \"count\": \"2\", \"messages\": ["
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, let's catch up sometime soon!\", \"time\": \"2024-12-10 13:00:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Sounds good! Let's plan for next week.\", \"time\": \"2024-12-10 13:05:00\", \"classification\": \"ham\"}"
                        + "]}, "
                        + "{\"phone_number\": \"+918977665544\", \"name\": \"Olivia Harris\", \"count\": \"3\", \"messages\": ["
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey! Can you send me that document again? I lost it in the email thread.\", \"time\": \"2024-12-10 14:00:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure, I’ll send it over now.\", \"time\": \"2024-12-10 14:05:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Thanks! You're a lifesaver.\", \"time\": \"2024-12-10 14:10:00\", \"classification\": \"ham\"}"
                        + "]}, "
                        + "{\"phone_number\": \"+919234567890\", \"name\": \"Daniel Martinez\", \"count\": \"4\", \"messages\": ["
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, can you remind me when the meeting is tomorrow?\", \"time\": \"2024-12-10 15:00:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"The meeting's at 9 AM tomorrow, don’t forget!\", \"time\": \"2024-12-10 15:05:00\", \"classification\": \"warning\"}, "
                        + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Thanks! I'll be there on time.\", \"time\": \"2024-12-10 15:10:00\", \"classification\": \"ham\"}, "
                        + "{\"whatsapp_message_type\": \"received\", \"content\": \"Great! See you then.\", \"time\": \"2024-12-10 15:15:00\", \"classification\": \"ham\"}"
                        + "]}"
                        + "]";
            case "Instagram":
            case "Facebook":
            case "Snapchat":
            case "Twitter":
                return "[\n" +
                        "    {\n" +
                        "        \"user_id\": \"user_67890\",\n" +
                        "        \"message_details\": [\n" +
                        "            {\n" +
                        "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                        "                \"message_type\": \"received\",\n" +
                        "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                        "                \"message_type\": \"sent\",\n" +
                        "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                        "                \"message_type\": \"received\",\n" +
                        "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"user_id\": \"user_11111\",\n" +
                        "        \"message_details\": [\n" +
                        "            {\n" +
                        "                \"message\": \"Hello! Long time no see!\",\n" +
                        "                \"message_type\": \"sent\",\n" +
                        "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                        "                \"message_type\": \"received\",\n" +
                        "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                        "                \"message_type\": \"sent\",\n" +
                        "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                        "                \"message_type\": \"received\",\n" +
                        "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"user_id\": \"user_22222\",\n" +
                        "        \"message_details\": [\n" +
                        "            {\n" +
                        "                \"message\": \"Let's catch up soon!\",\n" +
                        "                \"message_type\": \"sent\",\n" +
                        "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                        "                \"message_type\": \"received\",\n" +
                        "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                        "                \"message_type\": \"sent\",\n" +
                        "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                        "                \"message_type\": \"received\",\n" +
                        "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                        "                \"classification\": \"ham\"\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    }\n" +
                        "]";
            default:
                return "";
        }
    }

    private void fetchMessagesFromApi(String platform, String logType) {
        // Use OkHttpClient to make the network request
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = SOCIAL_MEDIA_URL + "?log_type=" + logType + "&appname=" + platform + "&device_id=" + GlobalData.deviceId;
        Log.d("API_URL", "Request URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Log the error message for debugging purposes
                Log.e("API_ERROR", "API Request Failed: " + e.getMessage(), e);

                // Ensure UI updates happen on the main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Show a Toast message indicating the failure and error message
                        Toast.makeText(getContext(), "API Request Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String responseData = response.body().string();
                        getActivity().runOnUiThread(() -> {
                            setupRecyclerView(responseData); // Process the fetched response
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "API Request Failed", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void setupRecyclerView(String jsonData) {
        Gson gson = new Gson();

        if (platform.equals("WhatsApp") || platform.equals("Telegram")) {
            Type whatsAppMessagesListType = new TypeToken<List<WhatsAppMessages>>() {
            }.getType();
            whatsAppMessagesList = gson.fromJson(jsonData, whatsAppMessagesListType);

            whatsAppMessagesAdapter = new WhatsAppMessagesAdapter(whatsAppMessagesList,
                    new OnWhatsAppMessagesClickListener() {
                        @Override
                        public void onWhatsAppMessagesClick(WhatsAppMessages message) {
                            Intent intent;
                            if (platform.equals("WhatsApp")) {
                                intent = new Intent(getActivity(), WhatsAppMessageDetailsActivity.class);
                            } else {
                                intent = new Intent(getActivity(), TelegramMessageDetailsActivity.class);
                            }
                            intent.putExtra("contact_name", message.getName());
                            intent.putExtra("contact_phone_number", message.getPhoneNumber());
                            intent.putExtra("messages_details",
                                    new Gson().toJson(message.getWhatsAppMessagesDetails()));
                            startActivity(intent);
                        }
                    });
            messagesRecyclerView.setAdapter(whatsAppMessagesAdapter);
        } else {
            Type instagramMessagesListType = new TypeToken<List<InstagramMessages>>() {
            }.getType();
            instagramMessagesList = gson.fromJson(jsonData, instagramMessagesListType);

            instagramMessagesAdapter = new InstagramMessagesAdapter(instagramMessagesList,
                    new OnInstagramMessagesClickListener() {
                        @Override
                        public void onInstagramMessageClick(InstagramMessages message) {
                            Intent intent;
                            switch (platform) {
                                case "Instagram":
                                    intent = new Intent(getActivity(), InstagramMessageDetailsActivity.class);
                                    intent.putExtra("instagram_messages_details",
                                            new Gson().toJson(message.getInstagramMessageDetails()));
                                    break;
                                case "Facebook":
                                    intent = new Intent(getActivity(), FacebookMessageDetailsActivity.class);
                                    intent.putExtra("facebook_messages_details",
                                            new Gson().toJson(message.getInstagramMessageDetails()));
                                    break;
                                case "Snapchat":
                                    intent = new Intent(getActivity(), SnapChatMessageDetailsActivity.class);
                                    intent.putExtra("snapchat_messages_details",
                                            new Gson().toJson(message.getInstagramMessageDetails()));
                                    break;
                                case "Twitter":
                                    intent = new Intent(getActivity(), TwitterMessageDetailsActivity.class);
                                    intent.putExtra("twitter_messages_details",
                                            new Gson().toJson(message.getInstagramMessageDetails()));
                                    break;
                                default:
                                    return;
                            }
                            intent.putExtra("user_id", message.getUserId());
                            startActivity(intent);
                        }
                    });
            messagesRecyclerView.setAdapter(instagramMessagesAdapter);
        }
    }

    private String getWhatsAppJsonData() {
        return "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"John Doe\", \"count\": \"5\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey! What's up? Did you get my message?\", \"time\": \"2024-12-10 10:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yeah, I saw it! Just been busy with work, you know how it is.\", \"time\": \"2024-12-10 10:05:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Totally. I feel that! Also, I got a reminder about that meeting tomorrow. Are you still on for 5 PM?\", \"time\": \"2024-12-10 10:10:00\", \"classification\": \"warning\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yes! I’ll be there, just need to finish up a couple of things first.\", \"time\": \"2024-12-10 10:12:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sounds good! Oh, and don't forget about the new promo I sent you for that online course. It's a great deal!\", \"time\": \"2024-12-10 10:15:00\", \"classification\": \"warning\"}"
                + "]}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Jane Smith\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Haha, I almost missed it. Thanks for the heads-up. I'll check it out later.\", \"time\": \"2024-12-10 10:20:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"No problem! It's a really good deal, you should definitely look at it. Let me know if you need the link again.\", \"time\": \"2024-12-10 10:25:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Will do! Anyway, are we still meeting for coffee later today?\", \"time\": \"2024-12-10 10:30:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Absolutely! I’ll meet you at 3 PM. See you then!\", \"time\": \"2024-12-10 10:35:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"9876543210\", \"name\": \"Mike Johnson\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey, do you have a moment to chat? I need some advice on a project.\", \"time\": \"2024-12-10 11:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure! What's going on?\", \"time\": \"2024-12-10 11:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"I’m stuck on a presentation. Can you help me with the structure?\", \"time\": \"2024-12-10 11:10:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+917032109876\", \"name\": \"Emily Brown\", \"count\": \"6\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, I got the files you sent. Thanks for that!\", \"time\": \"2024-12-10 12:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"No problem, glad they were useful!\", \"time\": \"2024-12-10 12:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Also, did you manage to finish that task I mentioned?\", \"time\": \"2024-12-10 12:10:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Almost there, just need a few more details from you.\", \"time\": \"2024-12-10 12:12:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Got it! I'll send you the details in a bit.\", \"time\": \"2024-12-10 12:15:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Perfect, looking forward to it!\", \"time\": \"2024-12-10 12:18:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+919067890123\", \"name\": \"Chris Williams\", \"count\": \"2\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, let's catch up sometime soon!\", \"time\": \"2024-12-10 13:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Sounds good! Let's plan for next week.\", \"time\": \"2024-12-10 13:05:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+918977665544\", \"name\": \"Olivia Harris\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey! Can you send me that document again? I lost it in the email thread.\", \"time\": \"2024-12-10 14:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure, I’ll send it over now.\", \"time\": \"2024-12-10 14:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Thanks! You're a lifesaver.\", \"time\": \"2024-12-10 14:10:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+919234567890\", \"name\": \"Daniel Martinez\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, can you remind me when the meeting is tomorrow?\", \"time\": \"2024-12-10 15:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"The meeting's at 9 AM tomorrow, don’t forget!\", \"time\": \"2024-12-10 15:05:00\", \"classification\": \"warning\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Thanks! I'll be there on time.\", \"time\": \"2024-12-10 15:10:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Great! See you then.\", \"time\": \"2024-12-10 15:15:00\", \"classification\": \"ham\"}"
                + "]}"
                + "]";
    }

    private String getInstagramJsonData() {
        return "[\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! Long time no see!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
    }

}
*/





















/*
package com.example.parent.features.socialmedia.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.socialmedia.activity.facebook.FacebookMessageDetailsActivity;
import com.example.parent.features.socialmedia.activity.instagram.InstagramMessageDetailsActivity;
import com.example.parent.features.socialmedia.activity.snapchat.SnapChatMessageDetailsActivity;
import com.example.parent.features.socialmedia.activity.telegram.TelegramMessageDetailsActivity;
import com.example.parent.features.socialmedia.activity.twitter.TwitterMessageDetailsActivity;
import com.example.parent.features.socialmedia.activity.whatsapp.WhatsAppMessageDetailsActivity;
import com.example.parent.features.socialmedia.adapter.instagram.InstagramMessagesAdapter;
import com.example.parent.features.socialmedia.adapter.whatsapp.WhatsAppMessagesAdapter;
import com.example.parent.features.socialmedia.listener.instagram.OnInstagramMessagesClickListener;
import com.example.parent.features.socialmedia.listener.whatsapp.OnWhatsAppMessagesClickListener;
import com.example.parent.features.socialmedia.model.instagram.InstagramData;
import com.example.parent.features.socialmedia.model.instagram.InstagramMessages;
import com.example.parent.features.socialmedia.model.whatsapp.WhatsAppMessages;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment {

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private RecyclerView messagesRecyclerView;
    private WhatsAppMessagesAdapter whatsAppMessagesAdapter;
    private InstagramMessagesAdapter instagramMessagesAdapter;
    private List<WhatsAppMessages> whatsAppMessagesList = new ArrayList<>();
    private List<InstagramMessages> instagramMessagesList = new ArrayList<>();

    // Static method to create a new instance of MessagesFragment with platform info
    public static MessagesFragment newInstance(String platform) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);
        }
    }

    // Use the platform variable to load data accordingly
    // Example: If platform.equals("WhatsApp"), fetch WhatsApp-related messages, etc.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.messages_fragment, container, false);

        // Initialize RecyclerView
        messagesRecyclerView = rootView.findViewById(R.id.whatsapp_messages_recycler_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        // Fetch calls depending on the platform
        if (platform != null && platform.equals("WhatsApp")) {
            Log.d("Fragment", "Fragment created");
            fetchWhatsAppMessages();
        } else if (platform != null && platform.equals("Instagram")) {
            fetchInstagramMessages();
        } else if (platform != null && platform.equals("Facebook")) {
            fetchFacebookMessages();  // Implement this if needed for Facebook
        } else if (platform != null && platform.equals("SnapChat")) {
            fetchSnapChatMessages();  // Implement this if needed for SnapChat
        } else if (platform != null && platform.equals("Twitter")) {
            fetchTwitterMessages();  // Implement this if needed for Twitter
        } else if (platform != null && platform.equals("Telegram")) {
            fetchTelegramMessages();  // Implement this if needed for Telegram
        }

        return rootView;
    }


    private void fetchWhatsAppMessages() {
        // Simulating the JSON response for example purposes.
        Log.d("MessagesFragment", "WhatsApp Messages method");

        String jsonResponse = "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"John Doe\", \"count\": \"5\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey! What's up? Did you get my message?\", \"time\": \"2024-12-10 10:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yeah, I saw it! Just been busy with work, you know how it is.\", \"time\": \"2024-12-10 10:05:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Totally. I feel that! Also, I got a reminder about that meeting tomorrow. Are you still on for 5 PM?\", \"time\": \"2024-12-10 10:10:00\", \"classification\": \"warning\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yes! I’ll be there, just need to finish up a couple of things first.\", \"time\": \"2024-12-10 10:12:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sounds good! Oh, and don't forget about the new promo I sent you for that online course. It's a great deal!\", \"time\": \"2024-12-10 10:15:00\", \"classification\": \"warning\"}"
                + "]}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Jane Smith\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Haha, I almost missed it. Thanks for the heads-up. I'll check it out later.\", \"time\": \"2024-12-10 10:20:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"No problem! It's a really good deal, you should definitely look at it. Let me know if you need the link again.\", \"time\": \"2024-12-10 10:25:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Will do! Anyway, are we still meeting for coffee later today?\", \"time\": \"2024-12-10 10:30:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Absolutely! I’ll meet you at 3 PM. See you then!\", \"time\": \"2024-12-10 10:35:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"9876543210\", \"name\": \"Mike Johnson\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey, do you have a moment to chat? I need some advice on a project.\", \"time\": \"2024-12-10 11:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure! What's going on?\", \"time\": \"2024-12-10 11:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"I’m stuck on a presentation. Can you help me with the structure?\", \"time\": \"2024-12-10 11:10:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+917032109876\", \"name\": \"Emily Brown\", \"count\": \"6\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, I got the files you sent. Thanks for that!\", \"time\": \"2024-12-10 12:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"No problem, glad they were useful!\", \"time\": \"2024-12-10 12:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Also, did you manage to finish that task I mentioned?\", \"time\": \"2024-12-10 12:10:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Almost there, just need a few more details from you.\", \"time\": \"2024-12-10 12:12:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Got it! I'll send you the details in a bit.\", \"time\": \"2024-12-10 12:15:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Perfect, looking forward to it!\", \"time\": \"2024-12-10 12:18:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+919067890123\", \"name\": \"Chris Williams\", \"count\": \"2\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, let's catch up sometime soon!\", \"time\": \"2024-12-10 13:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Sounds good! Let's plan for next week.\", \"time\": \"2024-12-10 13:05:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+918977665544\", \"name\": \"Olivia Harris\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey! Can you send me that document again? I lost it in the email thread.\", \"time\": \"2024-12-10 14:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure, I’ll send it over now.\", \"time\": \"2024-12-10 14:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Thanks! You're a lifesaver.\", \"time\": \"2024-12-10 14:10:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+919234567890\", \"name\": \"Daniel Martinez\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, can you remind me when the meeting is tomorrow?\", \"time\": \"2024-12-10 15:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"The meeting's at 9 AM tomorrow, don’t forget!\", \"time\": \"2024-12-10 15:05:00\", \"classification\": \"warning\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Thanks! I'll be there on time.\", \"time\": \"2024-12-10 15:10:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Great! See you then.\", \"time\": \"2024-12-10 15:15:00\", \"classification\": \"ham\"}"
                + "]}"
                + "]";

        // Parse the JSON response using Gson
        Gson gson = new Gson();
        Type whatsAppMessagesListType = new TypeToken<List<WhatsAppMessages>>() {
        }.getType();
        whatsAppMessagesList = gson.fromJson(jsonResponse, whatsAppMessagesListType);

        // After parsing the JSON
        Log.d("MessagesFragment", "WhatsApp Messages List Size: " + whatsAppMessagesList.size());
        whatsAppMessagesAdapter = new WhatsAppMessagesAdapter(whatsAppMessagesList, new OnWhatsAppMessagesClickListener() {
            @Override
            public void onWhatsAppMessagesClick(WhatsAppMessages whatsAppMessages) {
                Intent intent = new Intent(getActivity(), WhatsAppMessageDetailsActivity.class);
                intent.putExtra("contact_name", whatsAppMessages.getName());
                intent.putExtra("contact_phone_number", whatsAppMessages.getPhoneNumber());
                intent.putExtra("whatsapp_messages_details", new Gson().toJson(whatsAppMessages.getWhatsAppMessagesDetails()));
                startActivity(intent);
            }
        });

        messagesRecyclerView.setAdapter(whatsAppMessagesAdapter);
    }


    private void fetchInstagramMessages(){
        String jsonData =  "[\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! Long time no see!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";

        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            Type instagramMessagesListType = new TypeToken<List<InstagramMessages>>() {
            }.getType();
            instagramMessagesList = gson.fromJson(jsonData, instagramMessagesListType);

            // Set the adapter for Instagram calls
            instagramMessagesAdapter = new InstagramMessagesAdapter(instagramMessagesList, new OnInstagramMessagesClickListener() {
                @Override
                public void onInstagramMessageClick(InstagramMessages instagramMessages) {
                    Intent intent = new Intent(getActivity(), InstagramMessageDetailsActivity.class);
                    intent.putExtra("user_id", instagramMessages.getUserId());
                    intent.putExtra("instagram_messages_details", new Gson().toJson(instagramMessages.getInstagramMessageDetails()));
                    startActivity(intent);
                }
            });
            messagesRecyclerView.setAdapter(instagramMessagesAdapter);
            Log.d("Instagram Messages Data ", "Instagram Messages: " + instagramMessagesList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchFacebookMessages() {
        String jsonData = "[\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! Long time no see!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            // Parse the call_log from the JSON string
            Type instagramMessagesListType = new TypeToken<List<InstagramMessages>>() {
            }.getType();
            instagramMessagesList = gson.fromJson(jsonData, instagramMessagesListType);

            // Set the adapter for Instagram calls
            instagramMessagesAdapter = new InstagramMessagesAdapter(instagramMessagesList, new OnInstagramMessagesClickListener() {
                @Override
                public void onInstagramMessageClick(InstagramMessages instagramMessages) {
                    Intent intent = new Intent(getActivity(), FacebookMessageDetailsActivity.class);
                    intent.putExtra("user_id", instagramMessages.getUserId());
                    intent.putExtra("facebook_messages_details", new Gson().toJson(instagramMessages.getInstagramMessageDetails()));
                    startActivity(intent);
                }
            });
            messagesRecyclerView.setAdapter(instagramMessagesAdapter);
            Log.d("Instagram Messages Data ", "Instagram Messages: " + instagramMessagesList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchSnapChatMessages() {
        String jsonDataSnapchat = "[\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! Long time no see!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            // Parse the call_log from the JSON string
            Type instagramMessagesListType = new TypeToken<List<InstagramMessages>>() {
            }.getType();
            instagramMessagesList = gson.fromJson(jsonDataSnapchat, instagramMessagesListType);
            // Set the adapter for Instagram calls
            instagramMessagesAdapter = new InstagramMessagesAdapter(instagramMessagesList, new OnInstagramMessagesClickListener() {
                @Override
                public void onInstagramMessageClick(InstagramMessages instagramMessages) {
                    Intent intent = new Intent(getActivity(), SnapChatMessageDetailsActivity.class);
                    intent.putExtra("user_id", instagramMessages.getUserId());
                    intent.putExtra("snapchat_messages_details", new Gson().toJson(instagramMessages.getInstagramMessageDetails()));
                    startActivity(intent);
                }
            });
            messagesRecyclerView.setAdapter(instagramMessagesAdapter);
            Log.d("Instagram Messages Data ", "Instagram Messages: " + instagramMessagesList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchTwitterMessages() {
        String jsonDataTwitter =  "[\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! Long time no see!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            // Parse the call_log from the JSON string
            Type instagramMessagesListType = new TypeToken<List<InstagramMessages>>() {
            }.getType();
            instagramMessagesList = gson.fromJson(jsonDataTwitter, instagramMessagesListType);

            // Set the adapter for Instagram calls
            instagramMessagesAdapter = new InstagramMessagesAdapter(instagramMessagesList, new OnInstagramMessagesClickListener() {
                @Override
                public void onInstagramMessageClick(InstagramMessages instagramMessages) {
                    Intent intent = new Intent(getActivity(), TwitterMessageDetailsActivity.class);
                    intent.putExtra("user_id", instagramMessages.getUserId());
                    intent.putExtra("twitter_messages_details", new Gson().toJson(instagramMessages.getInstagramMessageDetails()));
                    startActivity(intent);
                }
            });
            messagesRecyclerView.setAdapter(instagramMessagesAdapter);
            Log.d("Instagram Messages Data ", "Instagram Messages: " + instagramMessagesList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchTelegramMessages() {
        // Simulating the JSON response for example purposes.
        Log.d("MessagesFragment", "WhatsApp Messages method");

        String jsonResponse = "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"John Doe\", \"count\": \"5\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey! What's up? Did you get my message?\", \"time\": \"2024-12-10 10:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yeah, I saw it! Just been busy with work, you know how it is.\", \"time\": \"2024-12-10 10:05:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Totally. I feel that! Also, I got a reminder about that meeting tomorrow. Are you still on for 5 PM?\", \"time\": \"2024-12-10 10:10:00\", \"classification\": \"warning\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yes! I’ll be there, just need to finish up a couple of things first.\", \"time\": \"2024-12-10 10:12:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sounds good! Oh, and don't forget about the new promo I sent you for that online course. It's a great deal!\", \"time\": \"2024-12-10 10:15:00\", \"classification\": \"warning\"}"
                + "]}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Jane Smith\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Haha, I almost missed it. Thanks for the heads-up. I'll check it out later.\", \"time\": \"2024-12-10 10:20:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"No problem! It's a really good deal, you should definitely look at it. Let me know if you need the link again.\", \"time\": \"2024-12-10 10:25:00\", \"classification\": \"spam\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Will do! Anyway, are we still meeting for coffee later today?\", \"time\": \"2024-12-10 10:30:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Absolutely! I’ll meet you at 3 PM. See you then!\", \"time\": \"2024-12-10 10:35:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"9876543210\", \"name\": \"Mike Johnson\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey, do you have a moment to chat? I need some advice on a project.\", \"time\": \"2024-12-10 11:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure! What's going on?\", \"time\": \"2024-12-10 11:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"I’m stuck on a presentation. Can you help me with the structure?\", \"time\": \"2024-12-10 11:10:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+917032109876\", \"name\": \"Emily Brown\", \"count\": \"6\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, I got the files you sent. Thanks for that!\", \"time\": \"2024-12-10 12:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"No problem, glad they were useful!\", \"time\": \"2024-12-10 12:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Also, did you manage to finish that task I mentioned?\", \"time\": \"2024-12-10 12:10:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Almost there, just need a few more details from you.\", \"time\": \"2024-12-10 12:12:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Got it! I'll send you the details in a bit.\", \"time\": \"2024-12-10 12:15:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Perfect, looking forward to it!\", \"time\": \"2024-12-10 12:18:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+919067890123\", \"name\": \"Chris Williams\", \"count\": \"2\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, let's catch up sometime soon!\", \"time\": \"2024-12-10 13:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Sounds good! Let's plan for next week.\", \"time\": \"2024-12-10 13:05:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+918977665544\", \"name\": \"Olivia Harris\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey! Can you send me that document again? I lost it in the email thread.\", \"time\": \"2024-12-10 14:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure, I’ll send it over now.\", \"time\": \"2024-12-10 14:05:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Thanks! You're a lifesaver.\", \"time\": \"2024-12-10 14:10:00\", \"classification\": \"ham\"}"
                + "]}, "
                + "{\"phone_number\": \"+919234567890\", \"name\": \"Daniel Martinez\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, can you remind me when the meeting is tomorrow?\", \"time\": \"2024-12-10 15:00:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"The meeting's at 9 AM tomorrow, don’t forget!\", \"time\": \"2024-12-10 15:05:00\", \"classification\": \"warning\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Thanks! I'll be there on time.\", \"time\": \"2024-12-10 15:10:00\", \"classification\": \"ham\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Great! See you then.\", \"time\": \"2024-12-10 15:15:00\", \"classification\": \"ham\"}"
                + "]}"
                + "]";
        // Parse the JSON response using Gson
        Gson gson = new Gson();
        Type whatsAppMessagesListType = new TypeToken<List<WhatsAppMessages>>() {
        }.getType();
        whatsAppMessagesList = gson.fromJson(jsonResponse, whatsAppMessagesListType);

        // After parsing the JSON
        Log.d("MessagesFragment", "WhatsApp Messages List Size: " + whatsAppMessagesList.size());
        whatsAppMessagesAdapter = new WhatsAppMessagesAdapter(whatsAppMessagesList, new OnWhatsAppMessagesClickListener() {
            @Override
            public void onWhatsAppMessagesClick(WhatsAppMessages whatsAppMessages) {
                Intent intent = new Intent(getActivity(), TelegramMessageDetailsActivity.class);
                intent.putExtra("contact_name", whatsAppMessages.getName());
                intent.putExtra("contact_phone_number", whatsAppMessages.getPhoneNumber());
                intent.putExtra("telegram_messages_details", new Gson().toJson(whatsAppMessages.getWhatsAppMessagesDetails()));
                startActivity(intent);
            }
        });

        messagesRecyclerView.setAdapter(whatsAppMessagesAdapter);
    }
}*/
