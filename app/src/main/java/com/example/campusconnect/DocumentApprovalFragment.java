package com.example.campusconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.campusconnect.adapters.DocumentApprovalAdapter;

public class DocumentApprovalFragment extends Fragment implements DocumentApprovalAdapter.DocumentActionListener {

    private ListView lvDocuments;
    private DocumentApprovalAdapter adapter;
    private DatabaseHelper dbHelper;
    private int userId;

    public static DocumentApprovalFragment newInstance() {
        return new DocumentApprovalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int studentId = sharedPreferences.getInt("userID", -1);
        userId=studentId;
        Log.d("DocumentApprovalFragment", "User ID: " + userId);
//        userId = requireContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE).getInt("userID", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_approval, container, false);
        lvDocuments = view.findViewById(R.id.lvDocumentApprovals);
        loadDocumentsForApproval();
        return view;
    }

    private void loadDocumentsForApproval() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT D.id As _id, D.title, D.description, D.file_path " +
                        "FROM Documents D " +
                        "JOIN DocumentApprovals DA ON D.id = DA.document_id " +
                        "WHERE DA.approver_id = ? AND DA.status = 'pending'",
                new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow("file_path"));

                Log.d("DocumentApprovalDebug", "Document ID: " + id +
                        ", Title: " + title +
                        ", Description: " + description +
                        ", FilePath: " + filePath);
            } while (cursor.moveToNext());
            // 🔁 Reset the cursor back to the beginning before passing to adapter
            cursor.moveToFirst();
        } else {
            Log.d("DocumentApprovalDebug", "No documents found for approval for userId: " + userId);
        }
        adapter = new DocumentApprovalAdapter(getContext(), cursor,userId,this);
        lvDocuments.setAdapter(adapter);

    }
    public void onDocumentAction() {
        loadDocumentsForApproval(); // Refresh after approval/rejection
    }
}
