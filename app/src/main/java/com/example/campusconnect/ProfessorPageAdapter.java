package com.example.campusconnect;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfessorPageAdapter extends FragmentStateAdapter {
    public ProfessorPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfessorAnnouncementFragment();
            case 1:
                return new LogoutFragment();
            default:
                return new CreateDepartmentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
