package com.example.parentwithsubscription.features.contacts.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class PhoneContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhoneContactsAdapter adapter;
    private List<PhoneContact> contactsList;

    private String CONTACTS_URL = URIConstants.CONTACTS_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contacts);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch contacts data from the API
        fetchContactsData();

        // Set up the EditText search functionality
        EditText searchView = findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter contacts based on search query
                String query = charSequence.toString();
                if (adapter != null) {
                    adapter.filter(query);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void fetchContactsData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(CONTACTS_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., network error)
                runOnUiThread(() -> Toast.makeText(PhoneContactsActivity.this, "Failed to fetch contacts data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String contactsJsonString = response.body().string();

                    // Parse JSON string into PhoneContact objects
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<PhoneContact>>() {}.getType();
                    contactsList = gson.fromJson(contactsJsonString, listType);

                    // Update RecyclerView on the main thread
                    runOnUiThread(() -> {
                        // Initialize the adapter with the fetched contacts
                        adapter = new PhoneContactsAdapter(contactsList);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(PhoneContactsActivity.this, "Error fetching contacts", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}












/*
package com.example.parent.features.contacts.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.contacts.adapter.PhoneContactsAdapter;
import com.example.parent.features.contacts.model.PhoneContact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;

public class PhoneContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhoneContactsAdapter adapter;
    private List<PhoneContact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contacts);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        */
/*//*
/ Your contacts JSON string
        String contactsJsonString = "[\n" +
                "  {\"name\": \"John Doe\", \"phone_number\": \"+1234567890\"},\n" +
                "  {\"name\": \"Jane Smith\", \"phone_number\": \"+1987654321\"},\n" +
                "  {\"name\": \"Sam Wilson\", \"phone_number\": \"+1122334455\"},\n" +
                "  {\"name\": \"Emily Johnson\", \"phone_number\": \"+1555123456\"},\n" +
                "  {\"name\": \"Michael Brown\", \"phone_number\": \"+1444332211\"},\n" +
                "  {\"name\": \"Chris Davis\", \"phone_number\": \"+1777888999\"},\n" +
                "  {\"name\": \"Lisa Green\", \"phone_number\": \"+1230987654\"},\n" +
                "  {\"name\": \"David White\", \"phone_number\": \"+1777223344\"},\n" +
                "  {\"name\": \"Sophia Harris\", \"phone_number\": \"+1555666777\"},\n" +
                "  {\"name\": \"Daniel Martin\", \"phone_number\": \"+1999888777\"},\n" +
                "  {\"name\": \"Olivia Taylor\", \"phone_number\": \"+1888777666\"},\n" +
                "  {\"name\": \"James Wilson\", \"phone_number\": \"+1122446688\"},\n" +
                "  {\"name\": \"Alice Adams\", \"phone_number\": \"+1234455667\"},\n" +
                "  {\"name\": \"Andrew Anderson\", \"phone_number\": \"+1777665443\"},\n" +
                "  {\"name\": \"Amanda Allen\", \"phone_number\": \"+1333445566\"},\n" +
                "  {\"name\": \"Ava Armstrong\", \"phone_number\": \"+1777889933\"},\n" +
                "  {\"name\": \"Aaron Abbott\", \"phone_number\": \"+1444332219\"},\n" +
                "  {\"name\": \"Adam Avery\", \"phone_number\": \"+1222334455\"},\n" +
                "  {\"name\": \"April Atkinson\", \"phone_number\": \"+1987654329\"},\n" +
                "  {\"name\": \"Annie Ashford\", \"phone_number\": \"+1098765432\"},\n" +
                "  {\"name\": \"Arthur Allen\", \"phone_number\": \"+1122334458\"},\n" +
                "  {\"name\": \"Albert Austin\", \"phone_number\": \"+1222334450\"},\n" +
                "  {\"name\": \"Aidan Abbott\", \"phone_number\": \"+1155338899\"},\n" +
                "  {\"name\": \"Angela Adams\", \"phone_number\": \"+1546778899\"}\n" +
                "]";*//*


        OkHttpClient client = new OkHttpClient();

        // Replace with your actual URL
        String url = "http://192.168.0.113:5000/contacts/get_contacts_data?device_id=5551231010";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., network error)
                runOnUiThread(() -> Toast.makeText(PhoneContactsActivity.this, "Failed to fetch contacts data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

        // Parse JSON string into PhoneContact objects
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PhoneContact>>(){}.getType();
        contactsList = gson.fromJson(contactsJsonString, listType);

        // Initialize the adapter
        adapter = new PhoneContactsAdapter(contactsList);
        recyclerView.setAdapter(adapter);

        // Set up the EditText search functionality
        EditText searchView = findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter contacts based on search query
                String query = charSequence.toString();
                adapter.filter(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}
*/
