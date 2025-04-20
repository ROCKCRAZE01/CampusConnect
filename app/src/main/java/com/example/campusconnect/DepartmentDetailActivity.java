package com.example.campusconnect;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class DepartmentDetailActivity extends AppCompatActivity {
    private int deptId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_detail);

        deptId = getIntent().getIntExtra("dept_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        DepartmentPagerAdapter adapter = new DepartmentPagerAdapter(this, deptId, userId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Info"); break;
                case 1: tab.setText("Create SubDept"); break;
                case 2: tab.setText("Announcements");break;
                // Future: tab.setText("Something Else");
            }
        }).attach();
    }
}
