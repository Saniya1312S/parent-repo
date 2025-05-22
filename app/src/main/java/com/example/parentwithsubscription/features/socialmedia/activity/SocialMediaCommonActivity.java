package com.example.parentwithsubscription.features.socialmedia.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.socialmedia.subfragment.CallsFragment;
import com.example.parentwithsubscription.features.socialmedia.subfragment.ContactsFragment;
import com.example.parentwithsubscription.features.socialmedia.subfragment.MessagesFragment;
import com.example.parentwithsubscription.features.socialmedia.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class SocialMediaCommonActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_social_media_layout); // Single layout for both platforms

        // Initialize the ViewPager and TabLayout
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Assuming platform is passed from MainActivity (or could be from a previous activity)
        String platform = getIntent().getStringExtra("PLATFORM"); // "WhatsApp" or "Instagram"

        // Set up the ViewPager with the selected platform
        setUpViewPager(platform);
    }

    private void setUpViewPager(String platform) {
        // Create an adapter for the ViewPager with the selected platform
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add the same fragments, but pass platform data to them
        adapter.addFragment(MessagesFragment.newInstance(platform), "Chats");
        adapter.addFragment(CallsFragment.newInstance(platform), "Calls");
        adapter.addFragment(ContactsFragment.newInstance(platform), "Contacts");

        // Set the adapter for the ViewPager
        viewPager.setAdapter(adapter);

        // Link the TabLayout to the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Set tab icons (same for both platforms)
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_messages);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_calls);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_contacts);
    }
}
