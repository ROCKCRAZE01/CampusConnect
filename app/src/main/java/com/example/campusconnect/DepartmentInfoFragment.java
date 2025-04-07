package com.example.campusconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class DepartmentInfoFragment extends Fragment {
    private int deptId;
    private DatabaseHelper dbHelper;
    private String departmentLevel;
    private TextView tvDeptInfo;
    private Button btnShowAddMemberForm, btnConfirmAddMember, btnCancelAddMember;
    private EditText etAddUserId;
    private Spinner spinnerAddRole;
    private LinearLayout addMemberFormLayout;
    public DepartmentInfoFragment(int deptId) {
        this.deptId = deptId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_department_info, container, false);
        TextView tvInfo = view.findViewById(R.id.tvDeptInfo);
        dbHelper = new DatabaseHelper(getContext());

        tvDeptInfo = view.findViewById(R.id.tvDeptInfo);
        btnShowAddMemberForm = view.findViewById(R.id.btnShowAddMemberForm);
        btnConfirmAddMember = view.findViewById(R.id.btnConfirmAddMember);
        btnCancelAddMember = view.findViewById(R.id.btnCancelAddMember);
        etAddUserId = view.findViewById(R.id.etAddUserId);
        spinnerAddRole = view.findViewById(R.id.spinnerAddRole);
        addMemberFormLayout = view.findViewById(R.id.addMemberFormLayout);

//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT name, dept_code FROM Departments WHERE department_id = ?", new String[]{String.valueOf(deptId)});
//        if (cursor.moveToFirst()) {
//            tvInfo.setText("Department: " + cursor.getString(0) + "\nCode: " + cursor.getString(1));
//        }
//        cursor.close();

        loadDepartmentInfo();
        setupAddMemberUI();

        return view;
    }

    private void loadDepartmentInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, dept_code, parent_id FROM Departments WHERE department_id = ?",
                new String[]{String.valueOf(deptId)});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String code = cursor.getString(1);
            boolean isParent = cursor.isNull(2);
            departmentLevel = isParent ? "parent" : "sub";

            tvDeptInfo.setText("Department: " + name + "\nCode: " + code);
        }
        cursor.close();
    }

    private void setupAddMemberUI() {
        SharedPreferences prefs = getContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("userID", -1);
        boolean isAllowed = false;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if ("parent".equals(departmentLevel)) {
            Cursor c = db.rawQuery("SELECT * FROM DepartmentDirectors WHERE department_id=? AND user_id=?",
                    new String[]{String.valueOf(deptId), String.valueOf(currentUserId)});
            if (c.moveToFirst()) isAllowed = true;
            c.close();
        } else {
            Cursor c = db.rawQuery("SELECT * FROM DepartmentMembers WHERE department_id=? AND user_id=? AND role='hod'",
                    new String[]{String.valueOf(deptId), String.valueOf(currentUserId)});
            if (c.moveToFirst()) isAllowed = true;
            c.close();
        }

        if (isAllowed) {
            btnShowAddMemberForm.setVisibility(View.VISIBLE);
            String[] roles = {"member", "hod", "coordinator"};
            spinnerAddRole.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, roles));
        }

        btnShowAddMemberForm.setOnClickListener(v -> {
            addMemberFormLayout.setVisibility(View.VISIBLE);
        });

        btnCancelAddMember.setOnClickListener(v -> {
            addMemberFormLayout.setVisibility(View.GONE);
            etAddUserId.setText("");
            spinnerAddRole.setSelection(0);
        });

        btnConfirmAddMember.setOnClickListener(v -> addMember());
    }

    private void addMember() {
        String userIdStr = etAddUserId.getText().toString().trim();
        String selectedRole = spinnerAddRole.getSelectedItem().toString();

        if (TextUtils.isEmpty(userIdStr)) {
            Toast.makeText(getContext(), "Enter User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = Integer.parseInt(userIdStr);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Add to this department
        db.execSQL("INSERT OR REPLACE INTO DepartmentMembers (department_id, user_id, role) VALUES (?, ?, ?)",
                new Object[]{deptId, userId, selectedRole});

        // If subdepartment, also add to parent if not present
        if ("sub".equals(departmentLevel)) {
            Cursor cursor = db.rawQuery("SELECT parent_id FROM Departments WHERE department_id=?",
                    new String[]{String.valueOf(deptId)});
            if (cursor.moveToFirst()) {
                int parentId = cursor.getInt(0);
                Cursor check = db.rawQuery("SELECT * FROM DepartmentMembers WHERE department_id=? AND user_id=?",
                        new String[]{String.valueOf(parentId), String.valueOf(userId)});
                if (!check.moveToFirst()) {
                    db.execSQL("INSERT INTO DepartmentMembers (department_id, user_id, role) VALUES (?, ?, ?)",
                            new Object[]{parentId, userId, "member"});
                }
                check.close();
            }
            cursor.close();
        }

        Toast.makeText(getContext(), "Member added successfully", Toast.LENGTH_SHORT).show();

        // Reset form
        etAddUserId.setText("");
        spinnerAddRole.setSelection(0);
        addMemberFormLayout.setVisibility(View.GONE);
    }
}
