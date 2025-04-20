package com.example.campusconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusconnect.DatabaseHelper;
import com.example.campusconnect.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UploadDocumentFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1;

    private EditText etTitle, etDescription;
    private Spinner spinnerDocumentType;
    private Button btnChooseFile, btnUpload;
    private TextView tvSelectedFile;

    private Uri selectedFileUri;
    private String selectedFileName;

    private String clubName;
    private int clubId;
    private DatabaseHelper dbHelper;

    public static UploadDocumentFragment newInstance(String clubName) {
        UploadDocumentFragment fragment = new UploadDocumentFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clubName = getArguments().getString("clubName");
        }
        dbHelper = new DatabaseHelper(requireContext());
        clubId = dbHelper.getClubIdByName(clubName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_document, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        etTitle = view.findViewById(R.id.etDocumentTitle);
        etDescription = view.findViewById(R.id.etDocumentDescription);
        spinnerDocumentType = view.findViewById(R.id.spinnerDocumentType);
        btnChooseFile = view.findViewById(R.id.btnChooseFile);
        btnUpload = view.findViewById(R.id.btnUploadDocument);
        tvSelectedFile = view.findViewById(R.id.tvSelectedFile);

        loadDocumentTypes();

        btnChooseFile.setOnClickListener(v -> chooseFile());

        btnUpload.setOnClickListener(v -> uploadDocument());
    }

    private void loadDocumentTypes() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT id AS _id, name FROM DocumentTypes", null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                new String[]{"name"},
                new int[]{android.R.id.text1},
                0
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocumentType.setAdapter(adapter);
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            selectedFileName = getFileName(selectedFileUri);
            tvSelectedFile.setText(selectedFileName);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        return result != null ? result : uri.getPath();
    }

    private void uploadDocument() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || selectedFileUri == null) {
            Toast.makeText(getContext(), "Please fill all fields and select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = (Cursor) spinnerDocumentType.getSelectedItem();
        if (cursor == null) {
            Toast.makeText(getContext(), "Invalid document type selection", Toast.LENGTH_SHORT).show();
            return;
        }
        int documentTypeId = cursor.getInt(cursor.getColumnIndex("_id"));

        SharedPreferences prefs = requireContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userID", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = saveFileToInternalStorage(selectedFileUri);
        if (filePath == null) {
            Toast.makeText(getContext(), "Failed to save file", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.insertDocument(title, desc, documentTypeId, clubId, userId, filePath);
        long documentId = dbHelper.getLastInsertedDocumentId(); // You'll implement this function

// Fetch the first approver from the approval flow
        Cursor flowCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT approver_user_id,step_number FROM DocumentApprovalFlow WHERE document_type_id = ? ORDER BY step_number ASC LIMIT 1",
                new String[]{String.valueOf(documentTypeId)}
        );

        if (flowCursor.moveToFirst()) {

            int approverId = flowCursor.getInt(flowCursor.getColumnIndex("approver_user_id"));
            int step_number = flowCursor.getInt(flowCursor.getColumnIndex("step_number"));

            // Insert into DocumentApprovals
            dbHelper.getWritableDatabase().execSQL(
                    "INSERT INTO DocumentApprovals (document_id, step_number, approver_id, status) VALUES (?, ?, ?, ?)",
                    new Object[]{documentId, step_number, approverId, "pending"}
            );
        }
        flowCursor.close();

        Toast.makeText(getContext(), "Document uploaded successfully", Toast.LENGTH_SHORT).show();

        etTitle.setText("");
        etDescription.setText("");
        tvSelectedFile.setText("No file selected");
    }

    private String saveFileToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File file = new File(requireContext().getFilesDir(), selectedFileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
