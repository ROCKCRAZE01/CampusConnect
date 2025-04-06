package com.example.campusconnect;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfessorClubDetailActivity extends AppCompatActivity {

    private int clubId, userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_club_detail);

        clubId = getIntent().getIntExtra("club_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        TabLayout tabLayout = findViewById(R.id.tabLayout_prof_club);
        ViewPager2 viewPager = findViewById(R.id.viewPager_prof_club);
        ProfessorClubPagerAdapter adapter = new ProfessorClubPagerAdapter(this, clubId, userId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Info"); break;
                case 1: tab.setText("Add Member"); break;
                // Future: tab.setText("Something Else");
            }
        }).attach();
    }

}
