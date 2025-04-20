package com.example.campusconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
    private LinearLayout timelineContainer;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_document, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etTitle = view.findViewById(R.id.etDocumentTitle);
        etDescription = view.findViewById(R.id.etDocumentDescription);
        spinnerDocumentType = view.findViewById(R.id.spinnerDocumentType);
        btnChooseFile = view.findViewById(R.id.btnChooseFile);
        btnUpload = view.findViewById(R.id.btnUploadDocument);
        tvSelectedFile = view.findViewById(R.id.tvSelectedFile);
        timelineContainer = view.findViewById(R.id.timelineLayout);
        timelineContainer.removeAllViews(); // Prevent duplicate steps for a document
        loadAllDocuments();

        loadDocumentTypes();

        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnUpload.setOnClickListener(v -> uploadDocument());
    }

    private void loadAllDocuments() {
        timelineContainer.removeAllViews(); // Clear old views if refreshing

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor docCursor = db.rawQuery(
                "SELECT id, title, description FROM Documents WHERE club_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(clubId)}
        );

        while (docCursor.moveToNext()) {
            int documentId = docCursor.getInt(docCursor.getColumnIndex("id"));
            String title = docCursor.getString(docCursor.getColumnIndex("title"));
            String desc = docCursor.getString(docCursor.getColumnIndex("description"));

            showTimelineSteps(timelineContainer, documentId, clubId, title, desc);
        }

        docCursor.close();
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
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        return uri.getLastPathSegment();
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
            Toast.makeText(getContext(), "Invalid document type selected", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "File saving failed", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.insertDocument(title, desc, documentTypeId, clubId, userId, filePath);
        long documentId = dbHelper.getLastInsertedDocumentId();

        Cursor flowCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT approver_user_id, step_number FROM DocumentApprovalFlow WHERE document_type_id = ? ORDER BY step_number ASC LIMIT 1",
                new String[]{String.valueOf(documentTypeId)}
        );

        if (flowCursor.moveToFirst()) {
            int approverId = flowCursor.getInt(flowCursor.getColumnIndex("approver_user_id"));
            int stepNumber = flowCursor.getInt(flowCursor.getColumnIndex("step_number"));

            dbHelper.getWritableDatabase().execSQL(
                    "INSERT INTO DocumentApprovals (document_id, step_number, approver_id, status) VALUES (?, ?, ?, ?)",
                    new Object[]{documentId, stepNumber, approverId, "pending"}
            );
        }
        flowCursor.close();

        Toast.makeText(getContext(), "Document uploaded successfully", Toast.LENGTH_SHORT).show();
        resetForm();
        showTimelineSteps(timelineContainer, (int) documentId, clubId, title, desc);

    }

    private String saveFileToInternalStorage(Uri uri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(new File(requireContext().getFilesDir(), selectedFileName))) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return new File(requireContext().getFilesDir(), selectedFileName).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showTimelineSteps(LinearLayout parentLayout, int documentId, int clubId, String title, String description) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Modified query to ensure you're only getting steps for this document
        Cursor stepsCursor = db.rawQuery(
                "SELECT daf.step_number, da.status " +
                        "  FROM DocumentApprovalFlow daf " +
                        "  JOIN Documents d " +
                        "    ON d.document_type_id = daf.document_type_id " +
                        "  LEFT JOIN DocumentApprovals da " +
                        "    ON da.step_number = daf.step_number " +
                        "   AND da.document_id = d.id " +
                        " WHERE d.id = ? " +
                        "   AND d.club_id = ? " +
                        " ORDER BY daf.step_number ASC",
                new String[]{ String.valueOf(documentId), String.valueOf(clubId) }
        );

        View docView = LayoutInflater.from(getContext()).inflate(R.layout.layout_for_document, parentLayout, false);
        ((TextView) docView.findViewById(R.id.documentTitle)).setText(title);
        ((TextView) docView.findViewById(R.id.documentDescription)).setText(description);

        LinearLayout timelineLayout = docView.findViewById(R.id.timelineLayout);
        timelineLayout.removeAllViews(); // 🧼 Clear any old steps

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int stepCount = stepsCursor.getCount();
        for (int i = 0; stepsCursor.moveToNext(); i++) {
            int stepNum = stepsCursor.getInt(stepsCursor.getColumnIndex("step_number"));
            String status = stepsCursor.getString(stepsCursor.getColumnIndex("status"));

            // Inflate and add the step
            View stepView = inflater.inflate(R.layout.timeline_step, timelineLayout, false);
            View lineLeft = stepView.findViewById(R.id.lineLeft);
            View lineRight = stepView.findViewById(R.id.lineRight);
            View circle = stepView.findViewById(R.id.stepCircle);

            if (i == 0) lineLeft.setVisibility(View.GONE);
            if (i == stepCount - 1) lineRight.setVisibility(View.GONE);

            // Apply styling based on status
            if ("approved".equalsIgnoreCase(status)) {
                circle.setBackgroundResource(R.drawable.circle_green);
                lineLeft.setBackgroundColor(Color.parseColor("#4CAF50"));
                lineRight.setBackgroundColor(Color.parseColor("#4CAF50"));
            } else if ("rejected".equalsIgnoreCase(status)) {
                circle.setBackgroundResource(R.drawable.circle_red);
                lineLeft.setBackgroundColor(Color.parseColor("#F44336"));
                lineRight.setBackgroundColor(Color.parseColor("#F44336"));
            } else {
                circle.setBackgroundResource(R.drawable.circle_gray);
            }

            timelineLayout.addView(stepView);
        }

        stepsCursor.close();
        parentLayout.addView(docView);
    }


    private void resetForm() {
        etTitle.setText("");
        etDescription.setText("");
        tvSelectedFile.setText("No file selected");
        selectedFileUri = null;
        selectedFileName = null;
    }
}
