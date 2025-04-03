package com.example.campusconnect;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

}

