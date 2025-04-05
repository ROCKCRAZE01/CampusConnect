package com.example.campusconnect;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PrfessorDepartmentPagerAdapter extends FragmentStateAdapter {
    int deptId, userId;

    public PrfessorDepartmentPagerAdapter(@NonNull FragmentActivity fragmentActivity, int deptId, int userId) {
        super(fragmentActivity);
        this.deptId = deptId;
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new CreateSubDepartmentFragment(deptId, userId);
        }
        return new Fragment(); // placeholder for other tabs
    }

    @Override
    public int getItemCount() {
        return 1; // Add more when additional tabs are implemented
    }
}
