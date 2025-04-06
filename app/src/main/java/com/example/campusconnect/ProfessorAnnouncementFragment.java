package com.example.campusconnect;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfessorAnnouncementFragment extends Fragment {
    private LinearLayout announcementsContainer;
    private DatabaseHelper databaseHelper;
    TextView temp;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_announcements, container, false);

        announcementsContainer = view.findViewById(R.id.announcementsContainer);
        databaseHelper = new DatabaseHelper(getContext());

        loadProfessorAnnouncements();

        return view;
    }


    private void loadProfessorAnnouncements() {
        announcementsContainer.removeAllViews();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title, content, created_at FROM Announcements WHERE target_audience IN (?, ?) ORDER BY created_at DESC",
                new String[]{"all", "all_professors"});

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String content = cursor.getString(1);
                String timestamp = cursor.getString(2);

                TextView textView = new TextView(getContext());
                textView.setText("Title: " + title + "\n" + content + "\nPosted on: " + timestamp);
                textView.setPadding(16, 16, 16, 16);
                textView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

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
