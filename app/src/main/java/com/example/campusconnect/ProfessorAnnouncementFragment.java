package com.example.campusconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ProfessorAnnouncementFragment extends Fragment {
    private LinearLayout announcementsContainer;
    private DatabaseHelper databaseHelper;
    TextView temp;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_announcements, container, false);

        announcementsContainer = view.findViewById(R.id.announcementsContainer);
        databaseHelper = new DatabaseHelper(getContext());

        // 🔽 Add your button logic here
        Button makeAnnouncementBtn = view.findViewById(R.id.makeAnnouncementBtn);
        SharedPreferences prefs = requireContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userID", -1);

        List<String> advisedClubs = databaseHelper.getClubsForWhichUserIsFacultyAdvisor(userId);

        if (!advisedClubs.isEmpty()) {
            makeAnnouncementBtn.setVisibility(View.VISIBLE);
            makeAnnouncementBtn.setOnClickListener(v -> showAnnouncementDialog(advisedClubs, userId)); // You define this function
        }

        loadProfessorAnnouncements();

        return view;
    }



    private void showAnnouncementDialog(List<String> clubList, int professorId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create Announcement");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 10);

        Spinner clubSpinner = new Spinner(requireContext());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, clubList);
        clubSpinner.setAdapter(spinnerAdapter);
        layout.addView(clubSpinner);

        RadioGroup audienceGroup = new RadioGroup(requireContext());
        RadioButton toClub = new RadioButton(requireContext());
        toClub.setText("Only Club Members");
        audienceGroup.addView(toClub);
        RadioButton toAll = new RadioButton(requireContext());
        toAll.setText("Public to All Students");
        audienceGroup.addView(toAll);
        layout.addView(audienceGroup);

        EditText titleInput = new EditText(requireContext());
        titleInput.setHint("Title");
        layout.addView(titleInput);

        EditText contentInput = new EditText(requireContext());
        contentInput.setHint("Content");
        layout.addView(contentInput);

        builder.setView(layout);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String content = contentInput.getText().toString().trim();
            String clubName = clubSpinner.getSelectedItem().toString();
            String targetAudience = (audienceGroup.getCheckedRadioButtonId() == toAll.getId()) ? "all" : "club";
            int clubId = databaseHelper.getClubIdByName(clubName); // Get the club ID based on the selected club name

            if (!title.isEmpty() && !content.isEmpty()) {
                // Insert the announcement into the appropriate table
                databaseHelper.insertProfessorAnnouncement(title, content, professorId, "Faculty", targetAudience, clubId,clubName);
                loadProfessorAnnouncements();  // Refresh the list of announcements
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadProfessorAnnouncements() {
        announcementsContainer.removeAllViews();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        // Query for both general and club-specific announcements
        Cursor cursor = db.rawQuery("SELECT title, content, created_at, target_audience FROM Announcements ORDER BY created_at DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String content = cursor.getString(1);
                String timestamp = cursor.getString(2);
                String targetAudience = cursor.getString(3);

                TextView textView = new TextView(getContext());
                textView.setText("Title: " + title + "\n" + content + "\nPosted on: " + timestamp);
                textView.setPadding(16, 16, 16, 16);
                textView.setTextSize(27);
                textView.setBackgroundResource(android.R.drawable.alert_dark_frame);

                announcementsContainer.addView(textView);

                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        2
                ));
                divider.setBackgroundColor(0xFFCCCCCC);
                announcementsContainer.addView(divider);

            } while (cursor.moveToNext());
        } else {
            TextView emptyMsg = new TextView(getContext());
            emptyMsg.setText("No announcements available.");
            emptyMsg.setPadding(16, 16, 16, 16);
            announcementsContainer.addView(emptyMsg);
        }

        cursor.close();
    }

}
