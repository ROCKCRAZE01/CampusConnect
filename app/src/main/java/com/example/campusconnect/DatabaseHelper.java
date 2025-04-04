package com.example.campusconnect;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_connect.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "Users";
    private static final String TABLE_DEPARTMENTS = "Departments";
    private static final String TABLE_DEPARTMENT_DIRECTORS = "Department_Directors";
    private static final String TABLE_ROLES = "Roles";
    private static final String TABLE_ROLE_PERMISSIONS = "Role_Permissions";
    private static final String TABLE_USER_ROLES = "User_Roles";
    private static final String TABLE_CLUBS = "Clubs";
    private static final String TABLE_ANNOUNCEMENTS = "Announcements";


    // Column Names - Users
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String COLUMN_BASE_ROLE = "baseRole";
    private static final String COLUMN_APPROVE_STATUS= "approve_status";

    // Column Names - Roles
    private static final String COLUMN_ROLE_ID = "role_id";
    private static final String COLUMN_ROLE_NAME = "name";
    private static final String COLUMN_DEPARTMENT_ID = "department_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL," +
                COLUMN_BASE_ROLE+ " TEXT, "+
                COLUMN_APPROVE_STATUS+ " INTEGER DEFAULT 0);" // 0 = Pending, 1 = Approved
        );

        // Create Departments Table
        db.execSQL("CREATE TABLE " + TABLE_DEPARTMENTS + " (" +
                "department_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "dept_code TEXT UNIQUE NOT NULL, "+
                "name TEXT UNIQUE NOT NULL);");

        // Create Department Directors Table (Users can be directors of multiple departments)
        db.execSQL("CREATE TABLE " + TABLE_DEPARTMENT_DIRECTORS + " (" +
                "department_id INTEGER, " +
                "user_id INTEGER, " +
                "FOREIGN KEY (department_id) REFERENCES " + TABLE_DEPARTMENTS + "(department_id), " +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(user_id), " +
                "PRIMARY KEY (department_id, user_id));");

        // Create Roles Table
        db.execSQL("CREATE TABLE " + TABLE_ROLES + " (" +
                "role_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "department_id INTEGER, " +
                "name TEXT NOT NULL, " +
                "FOREIGN KEY (department_id) REFERENCES " + TABLE_DEPARTMENTS + "(department_id));");

        // Create Role Permissions Table
        db.execSQL("CREATE TABLE " + TABLE_ROLE_PERMISSIONS + " (" +
                "permission_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "role_id INTEGER, " +
                "permission TEXT NOT NULL, " +
                "FOREIGN KEY (role_id) REFERENCES " + TABLE_ROLES + "(role_id));");

        // Create User Roles Table
        db.execSQL("CREATE TABLE " + TABLE_USER_ROLES + " (" +
                "user_id INTEGER, " +
                "role_id INTEGER, " +
                "department_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(user_id), " +
                "FOREIGN KEY (role_id) REFERENCES " + TABLE_ROLES + "(role_id), " +
                "FOREIGN KEY (department_id) REFERENCES " + TABLE_DEPARTMENTS + "(department_id), " +
                "PRIMARY KEY (user_id, role_id, department_id));");

        // Table - Announcements
        db.execSQL("CREATE TABLE " + TABLE_ANNOUNCEMENTS + " (" +
                "announcement_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "created_by INTEGER, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (created_by) REFERENCES Users(user_id));");

        db.execSQL("CREATE TABLE " + TABLE_CLUBS + " (" +
                "club_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "club_code TEXT UNIQUE NOT NULL, "+
                "name TEXT UNIQUE NOT NULL, " +
                "faculty_advisor INTEGER, " +
                "FOREIGN KEY (faculty_advisor) REFERENCES Users(user_id));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old tables if upgrading
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ROLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLE_PERMISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENT_DIRECTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENTS);
        onCreate(db);
    }





    // **Method to Register a New User**
    public boolean registerUser(String name, String email, String password, String baseRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_BASE_ROLE, baseRole);
//        values.put(COLUMN_APPROVE_STATUS, 0); // Pending approval

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // **Method to Assign a Director to a Department**
    public boolean assignDirector(int userId, int departmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("department_id", departmentId);

        long result = db.insert(TABLE_DEPARTMENT_DIRECTORS, null, values);
        db.close();
        return result != -1;
    }

    // **Method to Create a Custom Role Inside a Department**
    public boolean createRole(String roleName, int departmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", roleName);
        values.put("department_id", departmentId);

        long result = db.insert(TABLE_ROLES, null, values);
        db.close();
        return result != -1;
    }

    // **Method to Assign a Role to a User**
    public boolean assignUserRole(int userId, int roleId, int departmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("role_id", roleId);
        values.put("department_id", departmentId);

        long result = db.insert(TABLE_USER_ROLES, null, values);
        db.close();
        return result != -1;
    }

    // **Method to Get User Role**
    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT r.name FROM " + TABLE_USERS + " u " +
//                        "JOIN " + TABLE_USER_ROLES + " ur ON u.user_id = ur.user_id " +
//                        "JOIN " + TABLE_ROLES + " r ON ur.role_id = r.role_id " +
//                        "WHERE u.email = ? AND u.password = ?",
//                new String[]{email, password});
        Cursor cursor = db.rawQuery("SELECT baseRole FROM users WHERE email=? AND password=?", new String[]{email, password});

        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            db.close();
            return role;
        }
        cursor.close();
        db.close();
        return null;
    }

    public int getApproveStatus(String email) {
        int approveStatus = 0; // Default value if not found
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT approve_status FROM users WHERE email = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            approveStatus = cursor.getInt(0); // Extract approveStatus (0 or 1)
            cursor.close();
        }

        db.close();
        return approveStatus;
    }

    // Method to check if a user is a Professor
    public boolean isUserProfessor(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT baseRole FROM Users WHERE user_id = ?", new String[]{String.valueOf(userId)});
        boolean isProfessor = false;

        if (cursor.moveToFirst()) {
            String baseRole = cursor.getString(0);
            if ("Professor".equalsIgnoreCase(baseRole)) {
                isProfessor = true;
            }
        }

        cursor.close();
        db.close();
        return isProfessor;
    }

    // Method to create a department and assign director (only if baseRole = Professor)
    public boolean createDepartmentWithDirector(String deptName, String deptCode, int userId) {
        if (!isUserProfessor(userId)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues deptValues = new ContentValues();
            deptValues.put("dept_code", deptCode);
            deptValues.put("name", deptName);

            long deptId = db.insert(TABLE_DEPARTMENTS, null, deptValues);

            if (deptId == -1) {
                db.endTransaction();
                return false;
            }

            ContentValues directorValues = new ContentValues();
            directorValues.put("user_id", userId);
            directorValues.put("department_id", deptId);
            long result = db.insert(TABLE_DEPARTMENT_DIRECTORS, null, directorValues);

            if (result == -1) {
                db.endTransaction();
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    public boolean createClub(String clubName, int facultyAdvisorId) {
        if (!isUserProfessor(facultyAdvisorId)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", clubName);
        values.put("faculty_advisor", facultyAdvisorId);

        long result = db.insert(TABLE_CLUBS, null, values);
        db.close();
        return result != -1;
    }
    public boolean deleteDepartment(String deptCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Get the department_id based on deptCode
            Cursor cursor = db.rawQuery("SELECT department_id FROM Departments WHERE dept_code = ?", new String[]{deptCode});
            if (!cursor.moveToFirst()) {
                cursor.close();
                return false; // Department not found
            }

            int departmentId = cursor.getInt(0);
            cursor.close();

            // Delete from User_Roles
            db.delete("User_Roles", "department_id = ?", new String[]{String.valueOf(departmentId)});

            // Delete Role_Permissions linked to roles in this department
            db.execSQL("DELETE FROM Role_Permissions WHERE role_id IN (SELECT role_id FROM Roles WHERE department_id = ?)", new Object[]{departmentId});

            // Delete from Roles
            db.delete("Roles", "department_id = ?", new String[]{String.valueOf(departmentId)});

            // Delete from Department_Directors
            db.delete("Department_Directors", "department_id = ?", new String[]{String.valueOf(departmentId)});

            // Finally, delete from Departments
            int rowsAffected = db.delete("Departments", "department_id = ?", new String[]{String.valueOf(departmentId)});

            if (rowsAffected > 0) {
                db.setTransactionSuccessful();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }

    public boolean createAnnouncement(String title, String description, int createdBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("created_by", createdBy);

        long result = db.insert("Announcements", null, values);
        return result != -1;
    }

    public List<String> getAllAnnouncements() {
        List<String> announcements = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title, description, timestamp FROM Announcements ORDER BY timestamp DESC", null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String desc = cursor.getString(1);
                String time = cursor.getString(2);
                announcements.add("📢 " + title + "\n" + desc + "\n🕒 " + time);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return announcements;
    }



}

