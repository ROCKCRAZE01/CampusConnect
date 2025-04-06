package com.example.campusconnect;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfessorClubInfoFragment extends Fragment {
    private int clubId;
    private DatabaseHelper dbHelper;

    public ProfessorClubInfoFragment(int clubId){
        this.clubId=clubId;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_club_info, container, false);
        TextView tvInfo = view.findViewById(R.id.tvClubInfo);
        dbHelper = new DatabaseHelper(getContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
           Cursor cursor = db.rawQuery("SELECT name, club_code FROM Clubs WHERE club_id = ?", new String[]{String.valueOf(clubId)});
            if (cursor.moveToFirst()) {
                tvInfo.setText("Club: " + cursor.getString(0) + "\nCode: " + cursor.getString(1));
            }
        cursor.close();
        return view;
    }
//    public View onCreateVIew(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//        View view = inflater.inflate(R.layout.fragment_professor_club_info, container, false);
//        TextView tvInfo = view.findViewById(R.id.tvClubInfo);
//        dbHelper = new DatabaseHelper(getContext());
//
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT name, club_code FROM Clubs WHERE club_id = ?", new String[]{String.valueOf(clubId)});
//        if (cursor.moveToFirst()) {
//            tvInfo.setText("Club: " + cursor.getString(0) + "\nCode: " + cursor.getString(1));
//        }
//        cursor.close();
//        return view;
//
//
//    }
}


//public class DepartmentInfoFragment extends Fragment {
//    private int deptId;
//    private DatabaseHelper dbHelper;
//
//    public DepartmentInfoFragment(int deptId) {
//        this.deptId = deptId;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_department_info, container, false);
//        TextView tvInfo = view.findViewById(R.id.tvDeptInfo);
//        dbHelper = new DatabaseHelper(getContext());
//
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT name, dept_code FROM Departments WHERE department_id = ?", new String[]{String.valueOf(deptId)});
//        if (cursor.moveToFirst()) {
//            tvInfo.setText("Department: " + cursor.getString(0) + "\nCode: " + cursor.getString(1));
//        }
//        cursor.close();
//        return view;
//    }
//}
