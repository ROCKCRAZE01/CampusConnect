package com.example.campusconnect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.example.campusconnect.DatabaseHelper;
import com.example.campusconnect.DocumentApprovalFragment;
import com.example.campusconnect.R;

import java.io.File;


public class DocumentApprovalAdapter extends CursorAdapter {

    private Context context;
    private DatabaseHelper dbHelper;
    private int userId;
    private DocumentActionListener actionListener; //new added
    public DocumentApprovalAdapter(Context context, Cursor cursor, int userId,DocumentActionListener listener) {
        super(context, cursor, 0);
        this.userId=userId;
        this.context = context;

        this.dbHelper = new DatabaseHelper(context);
        this.actionListener = listener;//new added
    }
    public interface DocumentActionListener {
        void onDocumentAction();
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_document_approval, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = view.findViewById(R.id.tvDocumentTitle);
        TextView tvDescription = view.findViewById(R.id.tvDocumentDescription);
        Button btnDownload = view.findViewById(R.id.btnDownload);
        Button btnApprove = view.findViewById(R.id.btnApprove);
        Button btnReject = view.findViewById(R.id.btnReject);

        String title = cursor.getString(cursor.getColumnIndex("title"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        String filePath = cursor.getString(cursor.getColumnIndex("file_path"));
        int documentId = cursor.getInt(cursor.getColumnIndex("_id"));

        tvTitle.setText(title);
        tvDescription.setText(description);

        btnDownload.setOnClickListener(v -> downloadDocument(filePath));

        btnApprove.setOnClickListener(v -> updateApprovalStatus(documentId, "Approved"));
        btnReject.setOnClickListener(v -> updateApprovalStatus(documentId, "Rejected"));
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getLong(cursor.getColumnIndex("_id")); // Use "id" column instead of "_id"
    }

//    private void downloadDocument(String filePath) {
//        File file = new File(filePath);
//        Uri uri = Uri.fromFile(file);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "application/pdf"); // Assuming PDF for simplicity
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        context.startActivity(intent); // Use the stored context to start the activity
//    }
private void downloadDocument(String filePath) {
    File file = new File(filePath);
    Uri uri = FileProvider.getUriForFile(
            context,
            "com.example.campusconnect.fileprovider", // Must match authority in manifest
            file
    );

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(uri, "application/pdf");
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

    context.startActivity(intent);
}


//    private void updateApprovalStatus(int documentId, String status) {
//        // Update the DocumentApprovals table with the approval status
//        ContentValues values = new ContentValues();
//        values.put("status", status);
//        dbHelper.getWritableDatabase().update("DocumentApprovals", values, "document_id = ? AND approver_id = ?",
//                new String[]{String.valueOf(documentId), String.valueOf(userId)});
//
//        // Check if the document is completely approved or rejected and update the document status
//        if (status.equals("Approved")) {
//            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
//                    "SELECT COUNT(*) FROM DocumentApprovals WHERE document_id = ? AND status = 'Pending'",
//                    new String[]{String.valueOf(documentId)});
//            cursor.moveToFirst();
//            int pendingApprovals = cursor.getInt(0);
//            cursor.close();
//
//            if (pendingApprovals == 0) {
//                ContentValues documentValues = new ContentValues();
//                documentValues.put("status", "Approved");
//                dbHelper.getWritableDatabase().update("Documents", documentValues, "id = ?",
//                        new String[]{String.valueOf(documentId)});
//            }
//        } else if (status.equals("Rejected")) {
//            ContentValues documentValues = new ContentValues();
//            documentValues.put("status", "Rejected");
//            dbHelper.getWritableDatabase().update("Documents", documentValues, "id = ?",
//                    new String[]{String.valueOf(documentId)});
//        }
//    }
private void updateApprovalStatus(int documentId, String status) {
    // Step 1: Update current user's approval status
    ContentValues values = new ContentValues();
    values.put("status", status);
    dbHelper.getWritableDatabase().update("DocumentApprovals", values,
            "document_id = ? AND approver_id = ?",
            new String[]{String.valueOf(documentId), String.valueOf(userId)});

    if (status.equals("Approved")) {
        // Step 2: Get the current step number of this approver
        Cursor stepCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT step_number FROM DocumentApprovals WHERE document_id = ? AND approver_id = ?",
                new String[]{String.valueOf(documentId), String.valueOf(userId)});
        if (!stepCursor.moveToFirst()) {
            stepCursor.close();
            return; // Safety check
        }
        int currentStep = stepCursor.getInt(0);
        int nextStep=currentStep+1;

        Cursor docType =dbHelper.getReadableDatabase().rawQuery("SELECT document_type_id FROM Documents WHERE id = ?",
                new String[]{String.valueOf(documentId)});
        docType.moveToFirst();
        int docTypeId=docType.getInt(0);
        docType.close();
        Cursor maxStepCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT MAX(step_number) FROM DocumentApprovalFlow WHERE document_type_id = ?",
                new String[]{String.valueOf(docTypeId)});
        maxStepCursor.moveToFirst();
        int maxStep =maxStepCursor.getInt(0);
        maxStepCursor.close();
        if(nextStep>maxStep) {
            ContentValues docValues = new ContentValues();
            docValues.put("status", "Approved");
            dbHelper.getWritableDatabase().update("Documents", docValues,
                    "id = ?", new String[]{String.valueOf(documentId)});
        } else{
            Cursor nextStepUserId=dbHelper.getReadableDatabase().rawQuery(
                    "SELECT approver_user_id FROM DocumentApprovalFlow WHERE document_type_id = ? AND step_number = ?",
                    new String[]{String.valueOf(docTypeId), String.valueOf(nextStep)});
            nextStepUserId.moveToFirst();

            int nextStepUserIdValue=nextStepUserId.getInt(0);
            ContentValues nextStepValues = new ContentValues();
            nextStepValues.put("status", "pending");
            nextStepValues.put("document_id", documentId);
            nextStepValues.put("step_number", nextStep);
            nextStepValues.put("approver_id", nextStepUserIdValue);
            dbHelper.getWritableDatabase().insert("DocumentApprovals", null, nextStepValues);
        }



        stepCursor.close();

//        // Step 3: Check if all users in this step have approved
//        Cursor pendingCursor = dbHelper.getReadableDatabase().rawQuery(
//                "SELECT COUNT(*) FROM DocumentApprovals WHERE document_id = ? AND step_number = ? AND status != 'Approved'",
//                new String[]{String.valueOf(documentId), String.valueOf(currentStep)});
//        pendingCursor.moveToFirst();
//        int notApprovedCount = pendingCursor.getInt(0);
//        pendingCursor.close();
//
//        if (notApprovedCount == 0) {
//            // Step 4: All in current step approved. Activate next step
//            Cursor nextStepCursor = dbHelper.getReadableDatabase().rawQuery(
//                    "SELECT DISTINCT step_number FROM DocumentApprovals WHERE document_id = ? AND step_number > ? ORDER BY step_number ASC LIMIT 1",
//                    new String[]{String.valueOf(documentId), String.valueOf(currentStep)});
//
//            if (nextStepCursor.moveToFirst()) {
//                int nextStep = nextStepCursor.getInt(0);
//                nextStepCursor.close();
//
//                // Step 5: Set next step users' status from Inactive to Pending
//                ContentValues nextStepValues = new ContentValues();
//                nextStepValues.put("status", "Pending");
//                dbHelper.getWritableDatabase().update("DocumentApprovals", nextStepValues,
//                        "document_id = ? AND step_number = ? AND status = 'Inactive'",
//                        new String[]{String.valueOf(documentId), String.valueOf(nextStep)});
//            } else {
//                nextStepCursor.close();
//
//                // Step 6: No next step → mark document as Approved
//                ContentValues docValues = new ContentValues();
//                docValues.put("status", "Approved");
//                dbHelper.getWritableDatabase().update("Documents", docValues,
//                        "id = ?", new String[]{String.valueOf(documentId)});
//            }
//        }
    } else if (status.equals("Rejected")) {
        // Immediately reject the document
        ContentValues docValues = new ContentValues();
        docValues.put("status", "Rejected");
        dbHelper.getWritableDatabase().update("Documents", docValues,
                "id = ?", new String[]{String.valueOf(documentId)});
    }
    if (actionListener != null) {
        actionListener.onDocumentAction();
    }

}

}