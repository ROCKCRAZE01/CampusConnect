package com.example.campusconnect;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DepartmentPagerAdapter extends FragmentStateAdapter {
    private int deptId, userId;

    public DepartmentPagerAdapter(@NonNull FragmentActivity fragmentActivity, int deptId, int userId) {
        super(fragmentActivity);
        this.deptId = deptId;
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new  DepartmentInfoFragment(deptId);
            case 1: return new CreateSubDepartmentFragment(deptId, userId);
            case 2: return new DepartmentAnnouncementFragment(deptId, "parent");
            // Add more tabs later
            default: return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Add more if needed later
    }
}

