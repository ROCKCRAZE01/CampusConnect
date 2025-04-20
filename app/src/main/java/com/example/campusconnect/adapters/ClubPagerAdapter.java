package com.example.campusconnect.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.campusconnect.fragments.ClubInfoFragment;
import com.example.campusconnect.fragments.ClubAnnouncementsFragment;
import com.example.campusconnect.fragments.ClubChatFragment;
import com.example.campusconnect.fragments.UploadDocumentFragment;

public class ClubPagerAdapter extends FragmentStateAdapter {

    private final String clubName;

    public ClubPagerAdapter(@NonNull FragmentActivity fragmentActivity, String clubName) {
        super(fragmentActivity);
        this.clubName = clubName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return ClubInfoFragment.newInstance(clubName);
            case 1: return ClubAnnouncementsFragment.newInstance(clubName);
            case 2: return ClubChatFragment.newInstance(clubName);
            case 3: return UploadDocumentFragment.newInstance(clubName);
            default: return new ClubInfoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
