package com.example.campusconnect;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusconnect.DatabaseHelper;
import com.example.campusconnect.R;

import java.util.List;

public class SuperAdminAnnouncementsFragment extends Fragment {
    private EditText etTitle, etDescription,etTargetId ;
    private Button btnCreateAnnouncement, btnShowForm, btnCancelAnnouncement;
    private LinearLayout announcementsContainer, formLayout;
    Spinner spinnerAudience, spinnerVisibleToRole;

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
        spinnerAudience = view.findViewById(R.id.spinnerTargetAudience);
        spinnerVisibleToRole = view.findViewById(R.id.spinnerVisibleToRole);
        etTargetId = view.findViewById(R.id.etTargetId);

        String[] audienceOptions = {"all", "all_professors", "all_students", "department", "subdepartment", "club"};
        String[] roleOptions = {"all","professor", "student", "admin", "dean"};

        spinnerAudience.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, audienceOptions));
        spinnerVisibleToRole.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, roleOptions));

        spinnerAudience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = audienceOptions[position];
                if (selected.equals("department") || selected.equals("subdepartment") || selected.equals("club")) {
                    etTargetId.setVisibility(View.VISIBLE);
                } else {
                    etTargetId.setVisibility(View.GONE);
                    etTargetId.setText("");
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
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
            String targetAudience = spinnerAudience.getSelectedItem().toString();
            String visibleToRole = spinnerVisibleToRole.getSelectedItem().toString();
            String targetIdText = etTargetId.getText().toString().trim();
            Integer targetId = targetIdText.isEmpty() ? null : Integer.parseInt(targetIdText);
            int superadminId = 1;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userID", -1);


            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                Toast.makeText(getContext(), "Please enter title and description", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean created = dbHelper.createAnnouncement(title, description, userId, targetAudience, visibleToRole, targetId);
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
        List<String> announcements = dbHelper.getAllAnnouncementsForSuperadmin();

        for (String a : announcements) {
            TextView tv = new TextView(getContext());
            tv.setText(a);
            tv.setPadding(10, 10, 10, 10);
            announcementsContainer.addView(tv);
        }
    }
}
