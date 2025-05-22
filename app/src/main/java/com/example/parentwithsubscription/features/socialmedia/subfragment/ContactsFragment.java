package com.example.parentwithsubscription.features.socialmedia.subfragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;

import com.example.parentwithsubscription.common.GlobalData;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.adapter.facebook.FacebookContactsAdapter;
import com.example.parentwithsubscription.features.socialmedia.adapter.instagram.InstagramContactsAdapter;
import com.example.parentwithsubscription.features.socialmedia.adapter.snapchat.SnapChatContactsAdapter;
import com.example.parentwithsubscription.features.socialmedia.adapter.whatsapp.WhatsAppContactsAdapter;
import com.example.parentwithsubscription.features.socialmedia.model.facebook.FacebookContact;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramContact;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramContactList;
import com.example.parentwithsubscription.features.socialmedia.model.snapchat.SnapChatContact;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppContacts;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContactsFragment extends Fragment {

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private RecyclerView contactsRecyclerView;
    private EditText searchBar, searchBarInstagram;
    private RecyclerView.Adapter adapter;
    private String logType ="contacts";

    private String SOCIAL_MEDIA_URL = URIConstants.SOCIAL_MEDIA_URL;

    public static ContactsFragment newInstance(String platform) {
        ContactsFragment fragment = new ContactsFragment();
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
        /*View rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
        contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);
        searchBar = rootView.findViewById(R.id.searchBar);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));*/
        View rootView;

        // Check if platform is Instagram
        if ("Instagram".equals(platform)) {
            rootView = inflater.inflate(R.layout.fragment_instagram_contacts, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.instagram_contacts_recycler_view);
            searchBarInstagram = rootView.findViewById(R.id.searchBar);  // Instagram-specific search bar
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);
            searchBar = rootView.findViewById(R.id.searchBar);
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

//        loadContacts();
        fetchDataFromApi(platform, logType);
        setupSearch();

        return rootView;
    }

    private void loadContacts() {
        Gson gson = new Gson();
        String jsonData;

        switch (platform) {
            case "WhatsApp":
            case "Telegram":
                jsonData = " [\n" +
                        "    {\"contact_name\": \"John Doe\", \"phone_number\": \"+123456789\"},\n" +
                        "    {\"contact_name\": \"Jane Smith\", \"phone_number\": \"+987654321\"},\n" +
                        "    {\"contact_name\": \"Alice Johnson\", \"phone_number\": \"+1122334455\"},\n" +
                        "    {\"contact_name\": \"Bob Brown\", \"phone_number\": \"+9988776655\"}\n" +
                        "  ]\n";
                loadWhatsAppContacts(gson, jsonData);
                break;
            case "Instagram":
                jsonData = "{\n" +
                        "    \"followers\": [\n" +
                        "      {\"user_id\": \"1234567890\", \"user_name\": \"john_doe\", \"full_name\": \"John Doe\"},\n" +
                        "      {\"user_id\": \"2345678901\", \"user_name\": \"jane_smith\", \"full_name\": \"Jane Smith\"},\n" +
                        "      {\"user_id\": \"3456789012\", \"user_name\": \"michael_brown\", \"full_name\": \"Michael Brown\"},\n" +
                        "      {\"user_id\": \"4567890123\", \"user_name\": \"emily_jones\", \"full_name\": \"Emily Jones\"},\n" +
                        "      {\"user_id\": \"5678901234\", \"user_name\": \"david_wilson\", \"full_name\": \"David Wilson\"},\n" +
                        "      {\"user_id\": \"6789012345\", \"user_name\": \"alice_white\", \"full_name\": \"Alice White\"}\n" +
                        "    ],\n" +
                        "    \"following\": [\n" +
                        "      {\"user_id\": \"6789012345\", \"user_name\": \"john_doe\", \"full_name\": \"John Doe\"},\n" +
                        "      {\"user_id\": \"7890123456\", \"user_name\": \"jane_smith\", \"full_name\": \"Jane Smith\"},\n" +
                        "      {\"user_id\": \"8901234567\", \"user_name\": \"michael_brown\", \"full_name\": \"Michael Brown\"},\n" +
                        "      {\"user_id\": \"9012345678\", \"user_name\": \"emily_jones\", \"full_name\": \"Emily Jones\"},\n" +
                        "      {\"user_id\": \"0123456789\", \"user_name\": \"david_wilson\", \"full_name\": \"David Wilson\"},\n" +
                        "      {\"user_id\": \"2345678901\", \"user_name\": \"mark_taylor\", \"full_name\": \"Mark Taylor\"}\n" +
                        "    ]\n" +
                        "}";
                loadInstagramContacts(gson, jsonData);
                break;
            case "Facebook":
            case "Twitter":
                jsonData =  "[\n" +
                        "      {\"user_id\": \"zuck123\", \"user_name\": \"Mark Zuckerberg\"},\n" +
                        "      {\"user_id\": \"sandy456\", \"user_name\": \"Sandy Roberts\"},\n" +
                        "      {\"user_id\": \"john789\", \"user_name\": \"John Doe\"},\n" +
                        "      {\"user_id\": \"lucy101\", \"user_name\": \"Lucy Smith\"},\n" +
                        "      {\"user_id\": \"anna202\", \"user_name\": \"Anna Johnson\"}\n" +
                        "  ]\n";
                loadFacebookContacts(gson, jsonData);
                break;
            case "Snapchat":
                jsonData = "[\n" +
                        "    {\"user_id\": \"user_12345\", \"user_name\": \"John Doe\"},\n" +
                        "    {\"user_id\": \"user_67890\", \"user_name\": \"Jane Smith\"},\n" +
                        "    {\"user_id\": \"user_13579\", \"user_name\": \"Mike Johnson\"},\n" +
                        "    {\"user_id\": \"user_24680\", \"user_name\": \"Anna Williams\"},\n" +
                        "    {\"user_id\": \"user_11223\", \"user_name\": \"Zoe Green\"}\n" +
                        "]\n";
                loadSnapChatContacts(gson, jsonData);
                break;
        }
    }

    private void fetchDataFromApi(String appName, String logType) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = SOCIAL_MEDIA_URL + "?log_type=" + logType + "&appname=" + appName + "&device_id=" + GlobalData.getDeviceId();
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
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    try {
                        final String responseData = response.body().string();
                        getActivity().runOnUiThread(() -> {
                            // Process the response based on the platform
                            if ("WhatsApp".equals(appName) || "Telegram".equals(appName)) {
                                loadWhatsAppContacts(gson, responseData);
                            } else if ("Instagram".equals(appName)) {
                                loadInstagramContacts(gson, responseData);
                            } else if ("Facebook".equals(appName) || "Twitter".equals(appName)) {
                                loadFacebookContacts(gson, responseData);
                            } else if ("Snapchat".equals(appName)) {
                                loadSnapChatContacts(gson, responseData);
                            }
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "API Request Failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void loadWhatsAppContacts(Gson gson, String jsonData) {
        Type listType = new TypeToken<List<WhatsAppContacts>>(){}.getType();
        List<WhatsAppContacts> contacts = gson.fromJson(jsonData, listType);
        adapter = new WhatsAppContactsAdapter(contacts);
        contactsRecyclerView.setAdapter((WhatsAppContactsAdapter) adapter);
    }

    private void loadInstagramContacts(Gson gson, String jsonData) {
        InstagramContactList contactList = gson.fromJson(jsonData, InstagramContactList.class);
        List<InstagramContact> allContacts = new ArrayList<>();

        Set<String> followersSet = new HashSet<>();
        for (InstagramContact follower : contactList.followers) {
            followersSet.add(follower.userName);
            allContacts.add(follower);
        }

        for (InstagramContact follow : contactList.following) {
            if (!followersSet.contains(follow.userName)) {
                allContacts.add(follow);
            }
        }

        adapter = new InstagramContactsAdapter(allContacts, contactList.followers, contactList.following);
        contactsRecyclerView.setAdapter((InstagramContactsAdapter) adapter);
    }

    private void loadFacebookContacts(Gson gson, String jsonData) {
        Type facebookContactsList = new TypeToken<List<FacebookContact>>() {}.getType();
        List<FacebookContact> facebookContacts = gson.fromJson(jsonData, facebookContactsList);
        adapter = new FacebookContactsAdapter(facebookContacts);
        contactsRecyclerView.setAdapter((FacebookContactsAdapter) adapter);
    }

    private void loadSnapChatContacts(Gson gson, String jsonData) {
        Type snapchatContactsList = new TypeToken<List<SnapChatContact>>() {}.getType();
        List<SnapChatContact> snapchatContacts = gson.fromJson(jsonData, snapchatContactsList);
        adapter = new SnapChatContactsAdapter(snapchatContacts);
        contactsRecyclerView.setAdapter((SnapChatContactsAdapter) adapter);
    }

    private void setupSearch() {
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim();
                    if (adapter instanceof WhatsAppContactsAdapter) {
                        ((WhatsAppContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof InstagramContactsAdapter) {
                        ((InstagramContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof FacebookContactsAdapter) {
                        ((FacebookContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof SnapChatContactsAdapter) {
                        ((SnapChatContactsAdapter) adapter).filter(query);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            searchBar.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchBar.getText().toString().trim();
                    if (adapter instanceof WhatsAppContactsAdapter) {
                        ((WhatsAppContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof InstagramContactsAdapter) {
                        ((InstagramContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof FacebookContactsAdapter) {
                        ((FacebookContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof SnapChatContactsAdapter) {
                        ((SnapChatContactsAdapter) adapter).filter(query);
                    }
                    return true;
                }
                return false;
            });
        }
    }
}






















/*
package com.example.parent.features.socialmedia.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.parent.R;
import com.example.parent.features.socialmedia.adapter.facebook.FacebookContactsAdapter;
import com.example.parent.features.socialmedia.adapter.instagram.InstagramContactsAdapter;
import com.example.parent.features.socialmedia.adapter.snapchat.SnapChatContactsAdapter;
import com.example.parent.features.socialmedia.adapter.whatsapp.WhatsAppContactsAdapter;
import com.example.parent.features.socialmedia.model.facebook.FacebookContact;
import com.example.parent.features.socialmedia.model.facebook.FacebookContactsWrapper;
import com.example.parent.features.socialmedia.model.instagram.InstagramContact;
import com.example.parent.features.socialmedia.model.instagram.InstagramContactList;
import com.example.parent.features.socialmedia.model.snapchat.SnapChatContact;
import com.example.parent.features.socialmedia.model.snapchat.SnapChatContactsWrapper;
import com.example.parent.features.socialmedia.model.whatsapp.WhatsAppContacts;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsFragment extends Fragment {

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private RecyclerView contactsRecyclerView;
    private WhatsAppContactsAdapter whatsAppAdapter;
    private InstagramContactsAdapter instagramContactsAdapter;
    private SnapChatContactsAdapter snapChatContactsAdapter;
    private FacebookContactsAdapter facebookContactsAdapter;
    private EditText searchBarWhatsApp, searchBarInstagram, searchBarFacebook, searchBarSnapChat;

    // Static method to create a new instance of ContactsFragment with platform info
    public static ContactsFragment newInstance(String platform) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the platform argument
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        // Handle the platform here and load the necessary data accordingly
        if ("WhatsApp".equals(platform)) {
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);
            searchBarWhatsApp = rootView.findViewById(R.id.searchBar);  // Reference to the search bar for WhatsApp
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadWhatsAppContacts();

            setupSearch(searchBarWhatsApp, whatsAppAdapter); // Setup search functionality for WhatsApp

        } else if ("Instagram".equals(platform)) {
            rootView = inflater.inflate(R.layout.fragment_instagram_contacts, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.instagram_contacts_recycler_view);
            searchBarInstagram = rootView.findViewById(R.id.searchBar);  // Reference to the search bar for Instagram
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadInstagramContacts();

            setupSearch(searchBarInstagram, instagramContactsAdapter); // Setup search functionality for Instagram

        } else if ("Facebook".equals(platform)) {
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);  // Change ID if needed
            searchBarFacebook = rootView.findViewById(R.id.searchBar);  // Reference to the search bar for Facebook
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadFacebookContacts();

            setupSearch(searchBarFacebook, facebookContactsAdapter); // Setup search functionality for Facebook

        } else if ("SnapChat".equals(platform)) {
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);
            searchBarSnapChat = rootView.findViewById(R.id.searchBar);  // Reference to the search bar for SnapChat
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadSnapChatContacts();

            setupSearch(searchBarSnapChat, snapChatContactsAdapter); // Setup search functionality for Snapchat
        } else if ("Telegram".equals(platform)) {
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);
            searchBarWhatsApp = rootView.findViewById(R.id.searchBar);  // Reference to the search bar for WhatsApp
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadTelegramContacts();

            setupSearch(searchBarWhatsApp, whatsAppAdapter); // Setup search functionality for WhatsApp

        } else if ("Twitter".equals(platform)) {
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);  // Change ID if needed
            searchBarFacebook = rootView.findViewById(R.id.searchBar);  // Reference to the search bar for Facebook
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadTwitterContacts();

            setupSearch(searchBarFacebook, facebookContactsAdapter); // Setup search functionality for Facebook

        } else {
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
        }

        return rootView;
    }

    // Method to set up the search functionality
    private void setupSearch(EditText searchBar, RecyclerView.Adapter adapter) {
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    String query = charSequence.toString().trim();

                    // Call the filter method in the adapter when the search query changes
                    if (adapter instanceof WhatsAppContactsAdapter) {
                        ((WhatsAppContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof InstagramContactsAdapter) {
                        ((InstagramContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof FacebookContactsAdapter) {
                        ((FacebookContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof SnapChatContactsAdapter) {
                        ((SnapChatContactsAdapter) adapter).filter(query);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            // Set OnEditorActionListener to handle the Enter key action (search)
            searchBar.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchBar.getText().toString().trim();
                    // Trigger the search action manually (if not done already by the TextWatcher)
                    if (adapter instanceof WhatsAppContactsAdapter) {
                        ((WhatsAppContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof InstagramContactsAdapter) {
                        ((InstagramContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof FacebookContactsAdapter) {
                        ((FacebookContactsAdapter) adapter).filter(query);
                    } else if (adapter instanceof SnapChatContactsAdapter) {
                        ((SnapChatContactsAdapter) adapter).filter(query);
                    }
                    return true;
                }
                return false;
            });
        }
    }

    // Example method to load WhatsApp contacts
    private void loadWhatsAppContacts() {
        String jsonData = "{\n" +
                "  \"contacts\": [\n" +
                "    {\"contact_name\": \"John Doe\", \"phone_number\": \"+123456789\"},\n" +
                "    {\"contact_name\": \"Jane Smith\", \"phone_number\": \"+987654321\"},\n" +
                "    {\"contact_name\": \"Alice Johnson\", \"phone_number\": \"+1122334455\"},\n" +
                "    {\"contact_name\": \"Bob Brown\", \"phone_number\": \"+9988776655\"}\n" +
                "  ]\n" +
                "}";
        Gson gson = new Gson();
        Type listType = new TypeToken<List<WhatsAppContacts>>(){}.getType();

        ContactsResponse response = gson.fromJson(jsonData, ContactsResponse.class);
        List<WhatsAppContacts> contactList = response.getContacts();

        whatsAppAdapter = new WhatsAppContactsAdapter(contactList);
        contactsRecyclerView.setAdapter(whatsAppAdapter);
        whatsAppAdapter.notifyDataSetChanged();
    }

    public class ContactsResponse {
        private List<WhatsAppContacts> contacts;

        public List<WhatsAppContacts> getContacts() {
            return contacts;
        }

        public void setContacts(List<WhatsAppContacts> contacts) {
            this.contacts = contacts;
        }
    }
    private void loadInstagramContacts() {
        String jsonData = "{\n" +
                "  \"contacts\": {\n" +
                "    \"followers\": [\n" +
                "      {\"user_id\": \"1234567890\", \"user_name\": \"john_doe\", \"full_name\": \"John Doe\"},\n" +
                "      {\"user_id\": \"2345678901\", \"user_name\": \"jane_smith\", \"full_name\": \"Jane Smith\"},\n" +
                "      {\"user_id\": \"3456789012\", \"user_name\": \"michael_brown\", \"full_name\": \"Michael Brown\"},\n" +
                "      {\"user_id\": \"4567890123\", \"user_name\": \"emily_jones\", \"full_name\": \"Emily Jones\"},\n" +
                "      {\"user_id\": \"5678901234\", \"user_name\": \"david_wilson\", \"full_name\": \"David Wilson\"},\n" +
                "      {\"user_id\": \"6789012345\", \"user_name\": \"alice_white\", \"full_name\": \"Alice White\"}\n" +
                "    ],\n" +
                "    \"following\": [\n" +
                "      {\"user_id\": \"6789012345\", \"user_name\": \"john_doe\", \"full_name\": \"John Doe\"},\n" +
                "      {\"user_id\": \"7890123456\", \"user_name\": \"jane_smith\", \"full_name\": \"Jane Smith\"},\n" +
                "      {\"user_id\": \"8901234567\", \"user_name\": \"michael_brown\", \"full_name\": \"Michael Brown\"},\n" +
                "      {\"user_id\": \"9012345678\", \"user_name\": \"emily_jones\", \"full_name\": \"Emily Jones\"},\n" +
                "      {\"user_id\": \"0123456789\", \"user_name\": \"david_wilson\", \"full_name\": \"David Wilson\"},\n" +
                "      {\"user_id\": \"2345678901\", \"user_name\": \"mark_taylor\", \"full_name\": \"Mark Taylor\"}\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        Gson gson = new Gson();
        InstagramContactList contactList = gson.fromJson(jsonData, InstagramContactList.class);

        Set<String> followersSet = new HashSet<>();
        Set<String> followingSet = new HashSet<>();

        for (InstagramContact follower : contactList.contacts.followers) {
            followersSet.add(follower.userName);
        }

        for (InstagramContact follow : contactList.contacts.following) {
            followingSet.add(follow.userName);
        }

        List<InstagramContact> allContacts = new ArrayList<>();

        for (InstagramContact follower : contactList.contacts.followers) {
            InstagramContact contact = new InstagramContact();
            contact.userName = follower.userName;
            contact.fullName = follower.fullName;
            allContacts.add(contact);
        }

        for (InstagramContact follow : contactList.contacts.following) {
            if (!followersSet.contains(follow.userName)) {
                InstagramContact contact = new InstagramContact();
                contact.userName = follow.userName;
                contact.fullName = follow.fullName;
                allContacts.add(contact);
            }
        }

        instagramContactsAdapter = new InstagramContactsAdapter(allContacts, contactList.contacts.followers, contactList.contacts.following);
        contactsRecyclerView.setAdapter(instagramContactsAdapter);
    }

    public void loadFacebookContacts() {
        String jsonData = "{\n" +
                "  \"contacts\": [\n" +
                "      {\"user_id\": \"zuck123\", \"user_name\": \"Mark Zuckerberg\"},\n" +
                "      {\"user_id\": \"sandy456\", \"user_name\": \"Sandy Roberts\"},\n" +
                "      {\"user_id\": \"john789\", \"user_name\": \"John Doe\"},\n" +
                "      {\"user_id\": \"lucy101\", \"user_name\": \"Lucy Smith\"},\n" +
                "      {\"user_id\": \"anna202\", \"user_name\": \"Anna Johnson\"}\n" +
                "  ]\n" +
                "}";
        Gson gson = new Gson();
        FacebookContactsWrapper wrapper = gson.fromJson(jsonData, FacebookContactsWrapper.class);

        List<FacebookContact> contactList = wrapper.getContacts();
        facebookContactsAdapter = new FacebookContactsAdapter(contactList);
        contactsRecyclerView.setAdapter(facebookContactsAdapter);
    }

    public void loadSnapChatContacts() {
        String jsonData = "{\n" +
                "\"contacts\": [\n" +
                "    {\"user_id\": \"user_12345\", \"user_name\": \"John Doe\"},\n" +
                "    {\"user_id\": \"user_67890\", \"user_name\": \"Jane Smith\"},\n" +
                "    {\"user_id\": \"user_13579\", \"user_name\": \"Mike Johnson\"},\n" +
                "    {\"user_id\": \"user_24680\", \"user_name\": \"Anna Williams\"},\n" +
                "    {\"user_id\": \"user_11223\", \"user_name\": \"Zoe Green\"}\n" +
                "]\n" +
                "}";
        Gson gson = new Gson();
        Type listType = new TypeToken<List<SnapChatContact>>(){}.getType();

        SnapChatContactsWrapper wrapper = gson.fromJson(jsonData, SnapChatContactsWrapper.class);

        snapChatContactsAdapter = new SnapChatContactsAdapter(wrapper.getSnapChatContacts());
        contactsRecyclerView.setAdapter(snapChatContactsAdapter);
    }

    public void loadTelegramContacts(){
        String jsonData = "{\n" +
                "  \"contacts\": [\n" +
                "    {\"contact_name\": \"John Doe\", \"phone_number\": \"+123456789\"},\n" +
                "    {\"contact_name\": \"Jane Smith\", \"phone_number\": \"+987654321\"},\n" +
                "    {\"contact_name\": \"Alice Johnson\", \"phone_number\": \"+1122334455\"},\n" +
                "    {\"contact_name\": \"Bob Brown\", \"phone_number\": \"+9988776655\"}\n" +
                "  ]\n" +
                "}";
        Gson gson = new Gson();
        Type listType = new TypeToken<List<WhatsAppContacts>>(){}.getType();

        ContactsResponse response = gson.fromJson(jsonData, ContactsResponse.class);
        List<WhatsAppContacts> contactList = response.getContacts();

        whatsAppAdapter = new WhatsAppContactsAdapter(contactList);
        contactsRecyclerView.setAdapter(whatsAppAdapter);
        whatsAppAdapter.notifyDataSetChanged();
    }

    private void loadTwitterContacts(){
        String jsonData = "{\n" +
                "  \"contacts\": [\n" +
                "      {\"user_id\": \"zuck123\", \"user_name\": \"Mark Zuckerberg\"},\n" +
                "      {\"user_id\": \"sandy456\", \"user_name\": \"Sandy Roberts\"},\n" +
                "      {\"user_id\": \"john789\", \"user_name\": \"John Doe\"},\n" +
                "      {\"user_id\": \"lucy101\", \"user_name\": \"Lucy Smith\"},\n" +
                "      {\"user_id\": \"anna202\", \"user_name\": \"Anna Johnson\"}\n" +
                "  ]\n" +
                "}";
        Gson gson = new Gson();
        FacebookContactsWrapper wrapper = gson.fromJson(jsonData, FacebookContactsWrapper.class);

        List<FacebookContact> contactList = wrapper.getContacts();
        facebookContactsAdapter = new FacebookContactsAdapter(contactList);
        contactsRecyclerView.setAdapter(facebookContactsAdapter);
    }
}
*/











/*package com.example.parent.features.socialmedia.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.socialmedia.adapter.instagram.InstagramContactsAdapter;
import com.example.parent.features.socialmedia.adapter.whatsapp.WhatsAppContactsAdapter;
import com.example.parent.features.socialmedia.model.instagram.InstagramContact;
import com.example.parent.features.socialmedia.model.instagram.InstagramContactList;
import com.example.parent.features.socialmedia.model.whatsapp.WhatsAppContacts;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsFragment extends Fragment {

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private RecyclerView contactsRecyclerView;
    private WhatsAppContactsAdapter whatsAppAdapter;
    private InstagramContactsAdapter instagramContactsAdapter;

    // Static method to create a new instance of ContactsFragment with platform info
    public static ContactsFragment newInstance(String platform) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the platform argument
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the base layout for the fragment
        View rootView;

        // Handle the platform here and load the necessary data accordingly
        if ("WhatsApp".equals(platform)) {
            // Inflate the layout for WhatsApp
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
            contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadWhatsAppContacts();
        } else if ("Instagram".equals(platform)) {
            // Inflate the layout for Instagram
            rootView = inflater.inflate(R.layout.fragment_instagram_contacts, container, false);

            contactsRecyclerView = rootView.findViewById(R.id.instagram_contacts_recycler_view);
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadInstagramContacts();
        } else {
            // Handle invalid platform case (default layout)
            rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
        }

        return rootView;
    }

    // Example method to load WhatsApp contacts
    private void loadWhatsAppContacts() {
        // Logic to load and display WhatsApp contacts
        // Example JSON data
        String jsonData = "{\n" +
                "  \"contacts\": [\n" +
                "    {\"contact_name\": \"John Doe\", \"phone_number\": \"+123456789\"},\n" +
                "    {\"contact_name\": \"Jane Smith\", \"phone_number\": \"+987654321\"},\n" +
                "    {\"contact_name\": \"Alice Johnson\", \"phone_number\": \"+1122334455\"},\n" +
                "    {\"contact_name\": \"Bob Brown\", \"phone_number\": \"+9988776655\"}\n" +
                "  ]\n" +
                "}";

        // Parse JSON using Gson
        Gson gson = new Gson();
        Type listType = new TypeToken<List<WhatsAppContacts>>(){}.getType();

        // Extract contacts list from the JSON data
        ContactsResponse response = gson.fromJson(jsonData, ContactsResponse.class);
        List<WhatsAppContacts> contactList = response.getContacts();

        Log.d("WhatsApp contacts", contactList.toString());
        // Set up the adapter for RecyclerView
        whatsAppAdapter = new WhatsAppContactsAdapter(contactList);
        contactsRecyclerView.setAdapter(whatsAppAdapter);
        whatsAppAdapter.notifyDataSetChanged();
    }

    // Model class to hold the JSON response
    public class ContactsResponse {
        private List<WhatsAppContacts> contacts;

        public List<WhatsAppContacts> getContacts() {
            return contacts;
        }

        public void setContacts(List<WhatsAppContacts> contacts) {
            this.contacts = contacts;
        }
    }

    // Example method to load Instagram contacts
    private void loadInstagramContacts() {
        // Logic to load and display Instagram contacts

        String jsonData = "{\n" +
                "  \"contacts\": {\n" +
                "    \"followers\": [\n" +
                "      {\"user_id\": \"1234567890\", \"user_name\": \"follower1\", \"full_name\": \"Follower One\"},\n" +
                "      {\"user_id\": \"2345678901\", \"user_name\": \"follower2\", \"full_name\": \"Follower Two\"},\n" +
                "      {\"user_id\": \"3456789012\", \"user_name\": \"follower3\", \"full_name\": \"Follower Three\"},\n" +
                "      {\"user_id\": \"4567890123\", \"user_name\": \"follower4\", \"full_name\": \"Follower Four\"},\n" +
                "      {\"user_id\": \"5678901234\", \"user_name\": \"follower5\", \"full_name\": \"Follower Five\"}\n" +
                "    ],\n" +
                "    \"following\": [\n" +
                "      {\"user_id\": \"6789012345\", \"user_name\": \"follower1\", \"full_name\": \"Following One\"},\n" +
                "      {\"user_id\": \"7890123456\", \"user_name\": \"follower2\", \"full_name\": \"Following Two\"},\n" +
                "      {\"user_id\": \"8901234567\", \"user_name\": \"follower3\", \"full_name\": \"Following Three\"},\n" +
                "      {\"user_id\": \"9012345678\", \"user_name\": \"following4\", \"full_name\": \"Following Four\"},\n" +
                "      {\"user_id\": \"0123456789\", \"user_name\": \"following5\", \"full_name\": \"Following Five\"}\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Get the list of followers and following from JSON
        Gson gson = new Gson();
        InstagramContactList contactList = gson.fromJson(jsonData, InstagramContactList.class);

        // Create sets for fast lookup (unique usernames)
        Set<String> followersSet = new HashSet<>();
        Set<String> followingSet = new HashSet<>();

        // Add all followers and following usernames to the sets
        for (InstagramContact follower : contactList.contacts.followers) {
            followersSet.add(follower.userName);
        }

        for (InstagramContact follow : contactList.contacts.following) {
            followingSet.add(follow.userName);
        }

        // Prepare a list of all unique usernames (union of followers and following)
        List<InstagramContact> allContacts = new ArrayList<>();

        // Add all unique followers to the list
        for (InstagramContact follower : contactList.contacts.followers) {
            InstagramContact contact = new InstagramContact();
            contact.userName = follower.userName;
            contact.fullName = follower.fullName;
            allContacts.add(contact);
        }

        // Add all unique following usernames to the list (only if not already in followers)
        for (InstagramContact follow : contactList.contacts.following) {
            if (!followersSet.contains(follow.userName)) {
                InstagramContact contact = new InstagramContact();
                contact.userName = follow.userName;
                contact.fullName = follow.fullName;
                allContacts.add(contact);
            }
        }

        // Set up the adapter with the combined list
        instagramContactsAdapter = new InstagramContactsAdapter(allContacts, contactList.contacts.followers, contactList.contacts.following);
        contactsRecyclerView.setAdapter(instagramContactsAdapter);
    }
}*/









/*
package com.example.singletablayoutwithdiffadaptersocialmedia;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.singletablayoutwithdiffadaptersocialmedia.instagram.InstagramContact;
import com.example.singletablayoutwithdiffadaptersocialmedia.instagram.InstagramContactList;
import com.example.singletablayoutwithdiffadaptersocialmedia.instagram.InstagramContactsAdapter;
import com.example.singletablayoutwithdiffadaptersocialmedia.whatsapp.WhatsAppContacts;
import com.example.singletablayoutwithdiffadaptersocialmedia.whatsapp.WhatsAppContactsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsFragment extends Fragment {

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private RecyclerView contactsRecyclerView, instagramContactsRecyclerView;
    private WhatsAppContactsAdapter whatsAppAdapter;
    private InstagramContactsAdapter instagramContactsAdapter;

    // Static method to create a new instance of ContactsFragment with platform info
    public static ContactsFragment newInstance(String platform) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the platform argument
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.contacts_fragment, container, false);

        // Initialize RecyclerView
        contactsRecyclerView = rootView.findViewById(R.id.whatsapp_contacts_recycler_view);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Handle the platform here and load the necessary data accordingly
        if ("WhatsApp".equals(platform)) {
            // Handle WhatsApp contacts
            // For example, you can call a method to load WhatsApp contacts here
            loadWhatsAppContacts();
        } else if ("Instagram".equals(platform)) {

           */
/* rootView = inflater.inflate(R.layout.fragment_instagram_contacts, container, false);

            instagramContactsRecyclerView = rootView.findViewById(R.id.instagramContactsRecyclerView);
            instagramContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



            // Set up the TabLayout and ViewPager for Followers and Following
            TabLayout tabLayout = rootView.findViewById(R.id.mainTabLayout);
            ViewPager viewPager = rootView.findViewById(R.id.contactsViewPager);

            // Create the adapter for the sub-tabs (Followers and Following)
            ContactsViewPagerAdapter adapter = new ContactsViewPagerAdapter(getChildFragmentManager());

            // Set the adapter to the ViewPager
            viewPager.setAdapter(adapter);

            // Link the TabLayout to the ViewPager
            tabLayout.setupWithViewPager(viewPager);

            // Handle Instagram contacts
            // Load Instagram-specific contacts if necessary*//*

            loadInstagramContacts();
        }

        return rootView;
    }

    // Example method to load WhatsApp contacts
    private void loadWhatsAppContacts() {
        // Logic to load and display WhatsApp contacts
        // Example JSON data
        String jsonData = "{\n" +
                "  \"contacts\": [\n" +
                "    {\"contact_name\": \"John Doe\", \"phone_number\": \"+123456789\"},\n" +
                "    {\"contact_name\": \"Jane Smith\", \"phone_number\": \"+987654321\"},\n" +
                "    {\"contact_name\": \"Alice Johnson\", \"phone_number\": \"+1122334455\"},\n" +
                "    {\"contact_name\": \"Bob Brown\", \"phone_number\": \"+9988776655\"}\n" +
                "  ]\n" +
                "}";

        // Parse JSON using Gson
        Gson gson = new Gson();
        Type listType = new TypeToken<List<WhatsAppContacts>>(){}.getType();

        // Extract contacts list from the JSON data
        ContactsResponse response = gson.fromJson(jsonData, ContactsResponse.class);
        List<WhatsAppContacts> contactList = response.getContacts();

        Log.d("Whats App contacts", contactList.toString());
        // Set up the adapter for RecyclerView
        whatsAppAdapter = new WhatsAppContactsAdapter(contactList);
        contactsRecyclerView.setAdapter(whatsAppAdapter);
        whatsAppAdapter.notifyDataSetChanged();
    }

    // Model class to hold the JSON response
    public class ContactsResponse {
        private List<WhatsAppContacts> contacts;

        public List<WhatsAppContacts> getContacts() {
            return contacts;
        }

        public void setContacts(List<WhatsAppContacts> contacts) {
            this.contacts = contacts;
        }
    }
    // Example method to load Instagram contacts
    private void loadInstagramContacts() {
        // Logic to load and display Instagram contacts

        String jsonData = "{\n" +
                "  \"contacts\": {\n" +
                "    \"followers\": [\n" +
                "      {\"user_id\": \"1234567890\", \"user_name\": \"follower1\", \"full_name\": \"Follower One\"},\n" +
                "      {\"user_id\": \"2345678901\", \"user_name\": \"follower2\", \"full_name\": \"Follower Two\"},\n" +
                "      {\"user_id\": \"3456789012\", \"user_name\": \"follower3\", \"full_name\": \"Follower Three\"},\n" +
                "      {\"user_id\": \"4567890123\", \"user_name\": \"follower4\", \"full_name\": \"Follower Four\"},\n" +
                "      {\"user_id\": \"5678901234\", \"user_name\": \"follower5\", \"full_name\": \"Follower Five\"}\n" +
                "    ],\n" +
                "    \"following\": [\n" +
                "      {\"user_id\": \"6789012345\", \"user_name\": \"follower1\", \"full_name\": \"Following One\"},\n" +
                "      {\"user_id\": \"7890123456\", \"user_name\": \"follower2\", \"full_name\": \"Following Two\"},\n" +
                "      {\"user_id\": \"8901234567\", \"user_name\": \"follower3\", \"full_name\": \"Following Three\"},\n" +
                "      {\"user_id\": \"9012345678\", \"user_name\": \"following4\", \"full_name\": \"Following Four\"},\n" +
                "      {\"user_id\": \"0123456789\", \"user_name\": \"following5\", \"full_name\": \"Following Five\"}\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Get the list of followers and following from JSON
        Gson gson = new Gson();
        InstagramContactList contactList = gson.fromJson(jsonData, InstagramContactList.class);


        // Create sets for fast lookup (unique usernames)
        Set<String> followersSet = new HashSet<>();
        Set<String> followingSet = new HashSet<>();

        // Add all followers and following usernames to the sets
        for (InstagramContact follower : contactList.contacts.followers) {
            followersSet.add(follower.userName);
        }

        for (InstagramContact follow : contactList.contacts.following) {
            followingSet.add(follow.userName);
        }

        // Prepare a list of all unique usernames (union of followers and following)
        List<InstagramContact> allContacts = new ArrayList<>();

        // Add all unique followers to the list
        for (InstagramContact follower : contactList.contacts.followers) {
            InstagramContact contact = new InstagramContact();
            contact.userName = follower.userName;
            contact.fullName = follower.fullName;
            allContacts.add(contact);
        }

        // Add all unique following usernames to the list (only if not already in followers)
        for (InstagramContact follow : contactList.contacts.following) {
            if (!followersSet.contains(follow.userName)) {
                InstagramContact contact = new InstagramContact();
                contact.userName = follow.userName;
                contact.fullName = follow.fullName;
                allContacts.add(contact);
            }
        }

        // Set up the adapter with the combined list
        instagramContactsAdapter = new InstagramContactsAdapter(allContacts, contactList.contacts.followers, contactList.contacts.following);
        contactsRecyclerView.setAdapter(instagramContactsAdapter);

    }
}
*/







/*
package com.example.singletablayoutwithdiffadaptersocialmedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class ContactsFragment extends Fragment {

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment (e.g., a RecyclerView for contacts)
//        return inflater.inflate(R.layout.fragment_contacts, container, false);
        return null;
    }
}
*/
