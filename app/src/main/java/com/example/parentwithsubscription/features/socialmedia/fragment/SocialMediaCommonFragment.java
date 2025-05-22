package com.example.parentwithsubscription.features.socialmedia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.socialmedia.adapter.ViewPagerAdapter;
import com.example.parentwithsubscription.features.socialmedia.subfragment.CallsFragment;
import com.example.parentwithsubscription.features.socialmedia.subfragment.ContactsFragment;
import com.example.parentwithsubscription.features.socialmedia.subfragment.MessagesFragment;
import com.google.android.material.tabs.TabLayout;

public class SocialMediaCommonFragment extends Fragment {

    private String platform;

    public static SocialMediaCommonFragment newInstance(String platform) {
        SocialMediaCommonFragment fragment = new SocialMediaCommonFragment();
        Bundle args = new Bundle();
        args.putString("PLATFORM", platform);
        fragment.setArguments(args);
        return fragment;
    }

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_common_social_media_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            platform = getArguments().getString("PLATFORM");
        }

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        setUpViewPager(platform);
    }

    private void setUpViewPager(String platform) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(MessagesFragment.newInstance(platform), "Chats");
        adapter.addFragment(CallsFragment.newInstance(platform), "Calls");
        adapter.addFragment(ContactsFragment.newInstance(platform), "Contacts");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_messages);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_calls);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_contacts);
    }
}
