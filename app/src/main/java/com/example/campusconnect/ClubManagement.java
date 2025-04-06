package com.example.campusconnect;



import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ClubManagement extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private StudentPageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_management);

        tabLayout = findViewById(R.id.tabLayoutStudent);
        viewPager = findViewById(R.id.viewPagerStudent);

        adapter = new StudentPageAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Announcements");
                        break;
                    case 1:
                        tab.setText("Clubs");
                        break;
                    case 2:
                        tab.setText("Profile");
                        break;
                    case 3:
                        tab.setText("Logout");
                        break;
                    default:
                        tab.setText("Unknown");
                        break;
                }
            }
        }).attach();
    }
}
