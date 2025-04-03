package com.example.campusconnect;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ApproveUserFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private ListView lvPendingUsers;
    private SimpleCursorAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_approve_user, container, false); // Replace with your layout file name (fragment_approve_user.xml)
        dbHelper = new DatabaseHelper(getContext());
        lvPendingUsers = view.findViewById(R.id.lvPendingUsers);

        loadPendingUsers();
        return view;
    }
    private void loadPendingUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id AS _id, name, email, baseRole FROM Users WHERE approve_status = 0", null);

        adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_expandable_list_item_2,
                cursor,
                new String[]{"email", "baseRole"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );
        lvPendingUsers.setAdapter(adapter);

        lvPendingUsers.setOnItemClickListener((parent, view, position, id) -> showApprovalDialog((int) id));
    }

    private void showApprovalDialog(int userId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_approve_reject, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        Button btnApprove = dialogView.findViewById(R.id.btnApprove);
        Button btnReject = dialogView.findViewById(R.id.btnReject);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        btnApprove.setOnClickListener(v -> {
            approveUser(userId);
            dialog.dismiss();
        });

        btnReject.setOnClickListener(v -> {
            rejectUser(userId);
            dialog.dismiss();
        });
    }

    private void approveUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("approve_status", 1);
        db.update("Users", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        Toast.makeText(getContext(), "User Approved!", Toast.LENGTH_SHORT).show();
        loadPendingUsers();
    }

    private void rejectUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Users", "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        Toast.makeText(getContext(), "User Rejected!", Toast.LENGTH_SHORT).show();
        loadPendingUsers();
    }
}
