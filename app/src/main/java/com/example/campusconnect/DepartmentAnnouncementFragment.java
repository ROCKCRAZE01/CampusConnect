package com.example.campusconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DepartmentAnnouncementFragment extends Fragment {
    EditText etTitle, etContent;
    Button btnPost;
    LinearLayout container;
    int departmentId;
    String departmentLevel;
    DatabaseHelper dbHelper;

    public DepartmentAnnouncementFragment(int departmentId, String departmentLevel) {
        this.departmentId = departmentId;
        this.departmentLevel = departmentLevel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container_,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_department_announcement, container_, false);

        etTitle = view.findViewById(R.id.etDeptAnnTitle);
        etContent = view.findViewById(R.id.etDeptAnnContent);
        btnPost = view.findViewById(R.id.btnPostDeptAnnouncement);
        container = view.findViewById(R.id.deptAnnouncementsContainer);
        dbHelper = new DatabaseHelper(getContext());

        btnPost.setOnClickListener(v -> postAnnouncement());
        determineDepartmentLevel();
        loadAnnouncements();

        return view;
    }
    private void determineDepartmentLevel() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT parent_id FROM Departments WHERE department_id = ?", new String[]{String.valueOf(departmentId)});
        if (cursor.moveToFirst()) {
            int parentId = cursor.getInt(cursor.getColumnIndexOrThrow("parent_id"));
            if (cursor.isNull(cursor.getColumnIndex("parent_id"))) {
                departmentLevel = "parent";
            } else {
                departmentLevel = "child";
            }
        }
        cursor.close();
    }
    private void postAnnouncement() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        SharedPreferences prefs = getContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userID", -1);

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean isAllowed = false;

        if (departmentLevel.equals("parent")) {
            Cursor c = db.rawQuery("SELECT * FROM DepartmentDirectors WHERE department_id=? AND user_id=?",
                    new String[]{String.valueOf(departmentId), String.valueOf(userId)});
            if (c.moveToFirst()) isAllowed = true;
            c.close();
        } else {
            Cursor c = db.rawQuery("SELECT * FROM DepartmentMembers WHERE department_id=? AND user_id=? AND role='hod'",
                    new String[]{String.valueOf(departmentId), String.valueOf(userId)});
            if (c.moveToFirst()) isAllowed = true;
            c.close();
        }

        if (!isAllowed) {
            Toast.makeText(getContext(), "You are not authorized", Toast.LENGTH_SHORT).show();
            return;
        }

        db.execSQL("INSERT INTO DepartmentAnnouncements (department_id, title, message, created_by_user_id) " +
                        "VALUES (?, ?, ?, ?)",
                new Object[]{departmentId, title, content, userId});

        Toast.makeText(getContext(), "Announcement posted", Toast.LENGTH_SHORT).show();
        etTitle.setText("");
        etContent.setText("");
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        container.removeAllViews();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query;
        String[] args;
        if (departmentLevel.equals("parent")) {
            query = "SELECT * FROM DepartmentAnnouncements WHERE department_id IN (" +
                    "SELECT department_id FROM Departments WHERE department_id=? OR parent_id=?) " +
                    "ORDER BY timestamp DESC";
            args = new String[]{String.valueOf(departmentId), String.valueOf(departmentId)};
        } else {
            query = "SELECT * FROM DepartmentAnnouncements WHERE department_id=? ORDER BY timestamp DESC";
            args = new String[]{String.valueOf(departmentId)};
        }

        Cursor cursor = db.rawQuery(query, args);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String message = cursor.getString(cursor.getColumnIndex("message"));
            String time = cursor.getString(cursor.getColumnIndex("timestamp"));

            TextView tv = new TextView(getContext());
            tv.setText("• " + title + ":\n" + message + "\n(" + time + ")");
            tv.setPadding(5, 10, 5, 10);
            container.addView(tv);
        }
        cursor.close();
    }
}
