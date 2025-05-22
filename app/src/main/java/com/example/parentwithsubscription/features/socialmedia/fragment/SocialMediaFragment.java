package com.example.parentwithsubscription.features.socialmedia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.R;

public class SocialMediaFragment extends Fragment {

    private LinearLayout whatsAppCard, instagramCard, facebookCard, snapChatCard, twitterCard, telegramCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_social_media, container, false);

        // Initialize the LinearLayouts
        whatsAppCard = rootView.findViewById(R.id.whatsapp_card);
        instagramCard = rootView.findViewById(R.id.instagram_card);
        facebookCard = rootView.findViewById(R.id.facebook_card);
        snapChatCard = rootView.findViewById(R.id.snapchat_card);
        twitterCard = rootView.findViewById(R.id.twitter_card);
        telegramCard = rootView.findViewById(R.id.telegram_card);

        // Set OnClickListeners for each card
        setCardClickListener(whatsAppCard, "WhatsApp");
        setCardClickListener(instagramCard, "Instagram");
        setCardClickListener(facebookCard, "Facebook");
        setCardClickListener(snapChatCard, "Snapchat");
        setCardClickListener(twitterCard, "Twitter");
        setCardClickListener(telegramCard, "Telegram");

        return rootView;
    }

    private void setCardClickListener(LinearLayout card, String platform) {
        card.setOnClickListener(v -> {
            // Create a new instance of SocialMediaCommonFragment
            SocialMediaCommonFragment fragment = new SocialMediaCommonFragment();

            // Pass the platform name as an argument
            Bundle args = new Bundle();
            args.putString("PLATFORM", platform);
            fragment.setArguments(args);

            // Replace the current fragment with the new one
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
