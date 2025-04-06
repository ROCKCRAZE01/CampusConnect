package com.example.campusconnect;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StudentPageAdapter extends FragmentStateAdapter {

    public StudentPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AnnouncementsFragment_student();
            case 1:
                return new AnnouncementsFragment_student();
            case 2:
                return new AnnouncementsFragment_student();
            case 3:
                return new LogoutFragment();
            default:
                return new AnnouncementsFragment_student();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}