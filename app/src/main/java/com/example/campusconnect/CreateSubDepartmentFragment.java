package com.example.campusconnect;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreateSubDepartmentFragment extends Fragment {
    private int deptId, userId;
    private DatabaseHelper dbHelper;

    public CreateSubDepartmentFragment(int deptId, int userId) {
        this.deptId = deptId;
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_sub_department, container, false);
        dbHelper = new DatabaseHelper(getContext());

        LinearLayout layout = view.findViewById(R.id.layoutCreateSubDept);
        EditText etName = view.findViewById(R.id.etSubDeptName);
        EditText etCode = view.findViewById(R.id.etSubDeptCode);
        EditText etHodId = view.findViewById(R.id.etHodId);
        EditText etMembers = view.findViewById(R.id.etMemberIds);
        Button btnCreate = view.findViewById(R.id.btnCreateSubDept);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM DepartmentDirectors WHERE user_id = ? AND department_id = ?", new String[]{String.valueOf(userId), String.valueOf(deptId)});
        if (cursor.moveToFirst()) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
        cursor.close();

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String code = etCode.getText().toString().trim();
            int hodId = Integer.parseInt(etHodId.getText().toString().trim());
            String[] memberIds = etMembers.getText().toString().split(",");

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("dept_code", code);
            values.put("parent_id", deptId);

            SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
            long subDeptId = writableDb.insert("Departments", null, values);

            ContentValues hodCV = new ContentValues();
            hodCV.put("department_id", subDeptId);
            hodCV.put("user_id", hodId);
            hodCV.put("role", "hod");
            writableDb.insert("DepartmentMembers", null, hodCV);

            for (String id : memberIds) {
                ContentValues memCV = new ContentValues();
                memCV.put("department_id", subDeptId);
                memCV.put("user_id", Integer.parseInt(id.trim()));
                memCV.put("role", "member");
                writableDb.insert("DepartmentMembers", null, memCV);
            }

            Toast.makeText(getContext(), "Sub-department created", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
