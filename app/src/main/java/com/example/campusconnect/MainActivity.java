package com.example.campusconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        String userEmail = getLoggedInUserEmail(); // Retrieve email from session
        if (userEmail != null) {
            String role = getUserRole(userEmail);
            if (role != null) {
                redirectToDashboard(role);
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        finish();
    }

    private String getLoggedInUserEmail() {
        // Retrieve user email from session (SharedPreferences or another method)
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", null);
    }

    private String getUserRole(String email) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT baseRole FROM users WHERE email=?", new String[]{email});

        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return role;
    }

    private void redirectToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "Superadmin":
                intent = new Intent(this, AdminDashboard.class);
                break;
            case "Student":
                intent = new Intent(this, DirectorDashboard.class);
                break;
            case "Professor":
                intent = new Intent(this, ClubManagement.class);
                break;
            default:
                intent = new Intent(this, MainDashboard.class);
                break;
        }
        startActivity(intent);
    }
}
