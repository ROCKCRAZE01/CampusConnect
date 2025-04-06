package com.example.campusconnect;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfessorClubPagerAdapter extends FragmentStateAdapter {
    private int clubId, userId;

    public ProfessorClubPagerAdapter(@NonNull FragmentActivity fragmentActivity, int clubId, int userId) {
        super(fragmentActivity);
        this.clubId = clubId;
        this.userId = userId;
    }



    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ProfessorClubInfoFragment(clubId);
            case 1: return new ProfessorAddMemberFragment(clubId, userId);
            default: return new Fragment();
        }

    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
