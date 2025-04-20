package com.example.campusconnect;




import static androidx.core.app.PendingIntentCompat.getActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerText;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseHelper=new DatabaseHelper(this);
        db=databaseHelper.getWritableDatabase();
//        insertStaticDocumentData();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        databaseHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> loginUser());
        registerText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        int userID = databaseHelper.getUserID(email);
        String baseRole = databaseHelper.getUserRole(email, password);
        SharedPreferences sharedPreferences = getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userID", userID);
        editor.apply();


        int approveStatus = databaseHelper.getApproveStatus(email);
        approveStatus=1;
        if (baseRole != null && approveStatus == 1) {
            navigateBasedOnRole(baseRole);
        } else if(approveStatus == 0){
            Toast.makeText(this, "Your account is pending approval", Toast.LENGTH_SHORT).show();
        } else  {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
    private void insertStaticDocumentData() {
        try {
            // Insert Document Types (if not already inserted)
            insertDocumentType(1, "PERM Time Extension");
            insertDocumentType(2, "Event Budget");
            insertDocumentType(3, "Resource Request");

            // Insert Approval Flow for "PERM Time Extension"
            insertApprovalFlow(1, 5, 1);
            insertApprovalFlow(1, 6, 2);
            insertApprovalFlow(1, 2, 3);


            Toast.makeText(this, "Document types and approval flow inserted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error inserting data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void insertDocumentType(int id, String name) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        db.insertWithOnConflict("DocumentTypes", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertApprovalFlow(int documentTypeId, int approverUserId, int level) {
        ContentValues values = new ContentValues();
        values.put("document_type_id", documentTypeId);
        values.put("approver_user_id", approverUserId);
        values.put("step_number", level); // or "level" depending on your schema
        db.insertWithOnConflict("DocumentApprovalFlow", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        switch (role) {
            case "Superadmin":
                intent = new Intent(this, SuperAdminActivity.class);
                break;
            case "Professor":
                intent = new Intent(this, ProfessorActivity.class);
                break;
            case "Student":
                intent = new Intent(this, ClubManagement.class);
                break;
            default:
                intent = new Intent(this, MainDashboard.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
