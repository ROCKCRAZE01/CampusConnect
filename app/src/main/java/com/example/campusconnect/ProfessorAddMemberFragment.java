package com.example.campusconnect;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfessorAddMemberFragment extends Fragment {
    private int clubId, userId;
    private EditText editTextUserId;
    private Spinner spinnerRole;
    private Button buttonAddMember;
    private DatabaseHelper databaseHelper;

    public ProfessorAddMemberFragment(int clubId, int userId) {
        this.clubId = clubId;
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_add_member, container, false);

        editTextUserId = view.findViewById(R.id.editTextUserId);
        spinnerRole = view.findViewById(R.id.spinnerRole);
        buttonAddMember = view.findViewById(R.id.buttonAddMember);
        databaseHelper = new DatabaseHelper(getContext());

        String[] roles = {"Member", "Vice President", "President"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, roles);
        spinnerRole.setAdapter(roleAdapter);

        buttonAddMember.setOnClickListener(v -> addMember());

        return view;
    }

    private void addMember() {
        String userIdStr = editTextUserId.getText().toString().trim();
        String selectedRole = spinnerRole.getSelectedItem().toString();

        if (userIdStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int memberUserId;
        try {
            memberUserId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Check if user has 'Student' base role
        Cursor cursor = db.rawQuery("SELECT baseRole FROM Users WHERE user_id = ?", new String[]{String.valueOf(memberUserId)});
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            if (!"Student".equalsIgnoreCase(role)) {
                Toast.makeText(getContext(), "Only users with 'Student' role can be added to clubs", Toast.LENGTH_LONG).show();
                cursor.close();
                return;
            }
        } else {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        cursor.close();

        db = databaseHelper.getWritableDatabase();
        try {
            db.execSQL("INSERT OR REPLACE INTO ClubMembers (club_id, user_id, role) VALUES (?, ?, ?)",
                    new Object[]{clubId, memberUserId, selectedRole});

            Toast.makeText(getContext(), "Member added successfully", Toast.LENGTH_SHORT).show();
            editTextUserId.setText("");
            spinnerRole.setSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error adding member", Toast.LENGTH_SHORT).show();
        }
    }

}
