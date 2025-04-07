package com.example.campusconnect.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.campusconnect.DatabaseHelper;
import com.example.campusconnect.R;
import com.example.campusconnect.adapters.AnnouncementAdapter;

import java.util.List;

public class ClubAnnouncementsFragment extends Fragment {

    private static final String ARG_CLUB_NAME = "club_name";

    private String clubName;
    private RecyclerView announcementRecycler;
    private Button addAnnouncementBtn;
    private List<String> announcementList;
    private AnnouncementAdapter adapter;

    private DatabaseHelper dbHelper;

    public static ClubAnnouncementsFragment newInstance(String clubName) {
        ClubAnnouncementsFragment fragment = new ClubAnnouncementsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLUB_NAME, clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clubName = getArguments().getString(ARG_CLUB_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_announcements, container, false);

        announcementRecycler = view.findViewById(R.id.announcementRecycler);
        addAnnouncementBtn = view.findViewById(R.id.addAnnouncementBtn);
        dbHelper = new DatabaseHelper(requireContext());

        SharedPreferences prefs = requireContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int studentId = prefs.getInt("userID", -1);

        // 🧠 Step 1: Check the student role in this club
        int clubId = dbHelper.getClubIdByName(clubName);
        String role = dbHelper.getStudentRoleInClub(studentId, clubId);


        // 🧠 Step 2: Show add button only for president/vice-president
        if ("President".equalsIgnoreCase(role) || "Vice President".equalsIgnoreCase(role)) {
            addAnnouncementBtn.setVisibility(View.VISIBLE);
        } else {
            addAnnouncementBtn.setVisibility(View.GONE);
        }

        addAnnouncementBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("New Announcement");

            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);

            EditText inputMessage = new EditText(requireContext());
            inputMessage.setHint("Enter your message here");
            layout.addView(inputMessage);

            builder.setView(layout);

            builder.setPositiveButton("Post", (dialog, which) -> {
                String message = inputMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    boolean success = dbHelper.addClubAnnouncement(clubName, message);
                    if (success) {
                        Toast.makeText(getContext(), "Announcement added!", Toast.LENGTH_SHORT).show();
                        // Optionally reload the list
                        announcementList.clear();
                        announcementList.addAll(dbHelper.getAnnouncementsForClub(clubName));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to add announcement", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Message can't be empty", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });


        // 🧠 Step 3: Load announcements
        announcementList = dbHelper.getAnnouncementsForClub(clubName);
        adapter = new AnnouncementAdapter(announcementList);
        announcementRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        announcementRecycler.setAdapter(adapter);

        if (announcementList.isEmpty()) {
            Toast.makeText(getContext(), "No announcements", Toast.LENGTH_SHORT).show();
        }


        return view;
    }
}
