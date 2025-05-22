package com.example.parentwithsubscription.features.contacts.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.contacts.adapter.PhoneContactsAdapter;
import com.example.parentwithsubscription.features.contacts.model.PhoneContact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhoneContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchView;
    private PhoneContactsAdapter adapter;
    private List<PhoneContact> contactsList;
    private final String CONTACTS_URL = URIConstants.CONTACTS_URL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_phone_contacts, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = rootView.findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                if (adapter != null) {
                    adapter.filter(query);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        fetchContactsData();

        return rootView;
    }

    private void fetchContactsData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(CONTACTS_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to fetch contacts data", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && getActivity() != null) {
                    String contactsJsonString = response.body().string();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<PhoneContact>>() {}.getType();
                    contactsList = gson.fromJson(contactsJsonString, listType);

                    getActivity().runOnUiThread(() -> {
                        adapter = new PhoneContactsAdapter(contactsList);
                        recyclerView.setAdapter(adapter);
                    });
                } else if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Error fetching contacts", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
