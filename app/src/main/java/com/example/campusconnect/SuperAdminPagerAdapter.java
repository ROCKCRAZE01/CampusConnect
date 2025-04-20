package com.example.campusconnect;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SuperAdminPagerAdapter extends FragmentStateAdapter {

    public SuperAdminPagerAdapter(@NonNull SuperAdminActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CreateDepartmentFragment();
            case 1:
                return new ApproveUserFragment(); // Placeholder for additional tab
            case 2:
                return new SuperAdminAnnouncementsFragment(); // Placeholder for additional tab
            case 3:
                return new DocumentApprovalFragment().newInstance();
            case 4:
                return new LogoutFragment();
            default:
                return new CreateDepartmentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Update this number when adding more tabs
    }
}
