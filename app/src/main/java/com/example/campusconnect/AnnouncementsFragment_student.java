package com.example.campusconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class AnnouncementsFragment_student extends Fragment {
    private ListView announcementsList;
    private TextView emptyView;
    private DatabaseHelper databaseHelper;
    private int currentStudentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements_student, container, false);

        // Initialize views
        announcementsList = view.findViewById(R.id.announcementsList);
        emptyView = view.findViewById(R.id.emptyAnnouncementsView);
        announcementsList.setEmptyView(emptyView);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Retrieve logged-in student's ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        currentStudentId = sharedPreferences.getInt("userID", -1);
        databaseHelper.logAllAnnouncements();
        // Load announcements specific to the student
        loadAnnouncements();

        return view;
    }

    private void loadAnnouncements() {
        // Fetch announcements using the provided method from DatabaseHelper
        List<String> announcements = databaseHelper.getAllAnnouncementsForStudent(currentStudentId);
        Log.d("DEBUG", "Student ID: " + currentStudentId);
        Log.d("DEBUG", "Announcements found: " + announcements.size());



        if (announcements.isEmpty()) {
            emptyView.setText("No announcements available");
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    announcements
            );
            announcementsList.setAdapter(adapter);
        }
    }
}
