package com.example.campusconnect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfessorActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfessorPageAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        tabLayout = findViewById(R.id.tabLayoutProf);
        viewPager = findViewById(R.id.viewPagerProf);

        adapter = new ProfessorPageAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Announcements");
                        break;
                    case 1:
                        tab.setText("Departments");
                        break;
                    case 2:
                        tab.setText("Document Approvals");
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
