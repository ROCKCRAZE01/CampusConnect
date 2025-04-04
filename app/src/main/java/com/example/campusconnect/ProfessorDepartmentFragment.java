package com.example.campusconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfessorDepartmentFragment extends Fragment {
    Spinner spinnerDepartment;
    DatabaseHelper databaseHelper ;
    private int currentProfessorId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_professor_department,container,false);
        spinnerDepartment = view.findViewById(R.id.spinnerDepartment);
        databaseHelper =new DatabaseHelper(getContext());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int currentProfessorId = sharedPreferences.getInt("userID", -1);
        loadDepartmentsForProfessor(currentProfessorId);

        return view;
    }
    private void loadDepartmentsForProfessor(int userId) {
        SQLiteDatabase db = databaseHelper .getReadableDatabase();

        Set<String> departmentNames = new HashSet<>();
        String directorQuery = "SELECT d.department_id, d.name " +
                "FROM Departments d " +
                "INNER JOIN DepartmentDirectors dd ON d.department_id = dd.department_id " +
                "WHERE dd.user_id = ?";
        String memberQuery = "SELECT d.department_id, d.name " +
                "FROM Departments d " +
                "INNER JOIN DepartmentMembers m ON d.department_id = m.department_id " +
                "WHERE m.user_id = ?";

// 1. Execute directorQuery
        Cursor cursor1 = db.rawQuery(directorQuery, new String[]{String.valueOf(userId)});
        if (cursor1.moveToFirst()) {
            do {
                departmentNames.add(cursor1.getString(1)); // or store ID + name in custom object
            } while (cursor1.moveToNext());
        }
        cursor1.close();

// 2. Execute memberQuery
        Cursor cursor2 = db.rawQuery(memberQuery, new String[]{String.valueOf(userId)});
        if (cursor2.moveToFirst()) {
            do {
                departmentNames.add(cursor2.getString(1)); // prevents duplicate entries
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(departmentNames)
        );
        spinnerDepartment.setAdapter(adapter);
    }
}
