package com.example.campusconnect;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusconnect.DatabaseHelper;
import com.example.campusconnect.R;

import java.util.List;

public class SuperAdminAnnouncementsFragment extends Fragment {
    private EditText etTitle, etDescription;
    private Button btnCreateAnnouncement, btnShowForm, btnCancelAnnouncement;
    private LinearLayout announcementsContainer, formLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_superadmin_announcements, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        btnCreateAnnouncement = view.findViewById(R.id.btnCreateAnnouncement);
        btnCancelAnnouncement= view.findViewById(R.id.btnCancelAnnouncement);
        btnShowForm = view.findViewById(R.id.btnShowForm);
        announcementsContainer = view.findViewById(R.id.announcementsContainer);
        formLayout = view.findViewById(R.id.formLayout);



        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        displayAnnouncements(dbHelper);

        // Show form on button click
        btnShowForm.setOnClickListener(v -> {
            formLayout.setVisibility(View.VISIBLE);
            btnShowForm.setVisibility(View.GONE);
        });
        btnCancelAnnouncement.setOnClickListener(v -> {
            formLayout.setVisibility(View.GONE);
            btnShowForm.setVisibility(View.VISIBLE);
        });
        btnCreateAnnouncement.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            int superadminId = 1;

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                Toast.makeText(getContext(), "Please enter title and description", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean created = dbHelper.createAnnouncement(title, description, superadminId);
            if (created) {
                Toast.makeText(getContext(), "Announcement created", Toast.LENGTH_SHORT).show();
                etTitle.setText("");
                etDescription.setText("");
                formLayout.setVisibility(View.GONE);
                btnShowForm.setVisibility(View.VISIBLE);
                displayAnnouncements(dbHelper);
            } else {
                Toast.makeText(getContext(), "Failed to create announcement", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void displayAnnouncements(DatabaseHelper dbHelper) {
        announcementsContainer.removeAllViews();
        List<String> announcements = dbHelper.getAllAnnouncements();

        for (String a : announcements) {
            TextView tv = new TextView(getContext());
            tv.setText(a);
            tv.setPadding(10, 10, 10, 10);
            announcementsContainer.addView(tv);
        }
    }
}
