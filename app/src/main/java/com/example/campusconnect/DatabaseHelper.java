package com.example.campusconnect;

import com.example.campusconnect.models.ChatMessage;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_connect.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "Users";
    private static final String TABLE_DEPARTMENTS = "Departments";
    private static final String TABLE_DEPARTMENT_DIRECTORS = "DepartmentDirectors";
    private static final String TABLE_CLUB_FACULTY_ADVISORS = "Club_Faculty_Advisors";
    private static final String TABLE_ROLES = "Roles";
    private static final String TABLE_ROLE_PERMISSIONS = "Role_Permissions";
    private static final String TABLE_USER_ROLES = "User_Roles";
    private static final String TABLE_CLUBS = "Clubs";
    private static final String TABLE_ANNOUNCEMENTS = "Announcements";
    private static final String TABLE_DEPARTMENT_MEMBERS = "DepartmentMembers";
    private static final String TABLE_CLUB_MEMBERS = "ClubMembers";
    private static final String TABLE_CLUB_ANNOUNCEMENTS = "ClubAnnouncements";

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

    // Table - Club Announcements
    private static final String COLUMN_CLUB_ANNOUNCEMENT_ID = "id";
    private static final String COLUMN_CLUB_ANNOUNCEMENT_CLUB_NAME = "club_name";
    private static final String COLUMN_CLUB_ANNOUNCEMENT_MESSAGE = "message";
    private static final String COLUMN_CLUB_ANNOUNCEMENT_TIMESTAMP = "timestamp";

    // Table - Club Chat Messages
    private static final String TABLE_CLUB_CHAT_MESSAGES = "ClubChatMessages";

    private static final String COLUMN_CLUB_CHAT_ID = "id";
    private static final String COLUMN_CLUB_CHAT_CLUB_ID = "club_id";
    private static final String COLUMN_CLUB_CHAT_SENDER_ID = "sender_id";
    private static final String COLUMN_CLUB_CHAT_MESSAGE = "message";
    private static final String COLUMN_CLUB_CHAT_TIMESTAMP = "timestamp";


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
                "parent_id INTEGER, "+
                "name TEXT UNIQUE NOT NULL, "+
                "FOREIGN KEY (parent_id) REFERENCES " + TABLE_DEPARTMENTS + "(department_id));");

        db.execSQL("CREATE TABLE  " + TABLE_DEPARTMENT_MEMBERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "department_id INTEGER, " +
                "user_id INTEGER, " +
                "role TEXT, " + // e.g., "member", "hod", "coordinator"
                "FOREIGN KEY (department_id) REFERENCES " + TABLE_DEPARTMENTS + "(department_id), " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id));");


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
//        db.execSQL("CREATE TABLE " + TABLE_ANNOUNCEMENTS + " (" +
//                "announcement_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "title TEXT NOT NULL, " +
//                "description TEXT NOT NULL, " +
//                "created_by INTEGER, " +
//                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
//                "FOREIGN KEY (created_by) REFERENCES Users(user_id));");

        db.execSQL("CREATE TABLE "+  TABLE_ANNOUNCEMENTS +" (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "created_by_role TEXT NOT NULL, " +
                "created_by_id INTEGER NOT NULL, " +
                "target_audience TEXT NOT NULL, " +
                "target_id INTEGER, " +
                "visible_to_role TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (created_by_id) REFERENCES Users(user_id));");


        db.execSQL("CREATE TABLE " + TABLE_CLUBS + " (" +
                "club_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "club_code TEXT UNIQUE NOT NULL, "+
                "name TEXT UNIQUE NOT NULL ); " );

        db.execSQL("CREATE TABLE " + TABLE_CLUB_MEMBERS + " (" +
                "club_id INTEGER, " +
                "user_id INTEGER, " +
                "role TEXT NOT NULL, " +
                "PRIMARY KEY (club_id, user_id), " +
                "FOREIGN KEY (club_id) REFERENCES Clubs(club_id), " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id));");


        db.execSQL("CREATE TABLE " + TABLE_CLUB_FACULTY_ADVISORS + " (" +
                "club_id INTEGER, " +
                "user_id INTEGER, " +
                "FOREIGN KEY (club_id) REFERENCES " + TABLE_CLUBS + "(club_id), " +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(user_id), " +
                "PRIMARY KEY (club_id, user_id));");

        db.execSQL("CREATE TABLE IF NOT EXISTS ClubAnnouncements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "club_name TEXT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        String CREATE_CLUB_CHAT_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CLUB_CHAT_MESSAGES + " (" +
                COLUMN_CLUB_CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CLUB_CHAT_CLUB_ID + " INTEGER NOT NULL, " +
                COLUMN_CLUB_CHAT_SENDER_ID + " INTEGER NOT NULL, " +
                COLUMN_CLUB_CHAT_MESSAGE + " TEXT NOT NULL, " +
                COLUMN_CLUB_CHAT_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (" + COLUMN_CLUB_CHAT_CLUB_ID + ") REFERENCES Clubs(club_id), " +
                "FOREIGN KEY (" + COLUMN_CLUB_CHAT_SENDER_ID + ") REFERENCES Users(user_id)" +
                ");";

        db.execSQL(CREATE_CLUB_CHAT_MESSAGES_TABLE);



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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUB_FACULTY_ADVISORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENT_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUB_MEMBERS);

        onCreate(db);
    }




    public boolean isUserDirector(int userId, int departmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM DepartmentDirectors WHERE user_id = ? AND department_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(departmentId)}
        );

        boolean isDirector = cursor.moveToFirst();
        cursor.close();
        return isDirector;
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
    public int getUserID(String email) {
        int userId = -1; // Default value if not found
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM users WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return userId;
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
    public boolean createDepartmentWithDirector(String deptName, String deptCode, int userId, int parentId) {
        if (!isUserProfessor(userId)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
//        db.execSQL("CREATE TABLE " + TABLE_DEPARTMENTS + " (" +
//                "department_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "dept_code TEXT UNIQUE NOT NULL, "+
//                "parent_id INTEGER, "+
//                "name TEXT UNIQUE NOT NULL, "+
//                "FOREIGN KEY (parent_id) REFERENCES " + TABLE_DEPARTMENTS + "(department_id));");

        try {
            ContentValues deptValues = new ContentValues();
            deptValues.put("dept_code", deptCode);
            deptValues.put("name", deptName);
            deptValues.put("parent_id", parentId!=0?parentId:null); // Set parent_id if provided, otherwise null (for top-level departments)");

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
    public boolean createClub(String clubName, String clubCode ,int facultyAdvisorId) {
        if (!isUserProfessor(facultyAdvisorId)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try{
            ContentValues values = new ContentValues();
            values.put("name", clubName);
            values.put("club_code", clubCode);
            long result = db.insert(TABLE_CLUBS, null, values);
            if (result == -1) {
                db.endTransaction();
                return false;
            }


            ContentValues advisorValues = new ContentValues();
            advisorValues.put("user_id", facultyAdvisorId);
            advisorValues.put("club_id", result);
            long advisorResult = db.insert(TABLE_CLUB_FACULTY_ADVISORS, null, advisorValues);
            if (advisorResult == -1) {
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

//    public boolean createAnnouncement(String title, String description, int createdBy) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("title", title);
//        values.put("description", description);
//        values.put("created_by", createdBy);
//
//        long result = db.insert("Announcements", null, values);
//        return result != -1;
//    }
    public boolean createAnnouncement(String title, String description, int createdBy,
                                      String targetAudience, String visibleToRole, Integer targetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", description);

        String role=getBaseRoleByUserId(createdBy);
        values.put("created_by_role", role);
        values.put("created_by_id", createdBy);
        values.put("target_audience", targetAudience);
        values.put("visible_to_role", visibleToRole);

        values.put("target_id", targetId);


        long result = db.insert("Announcements", null, values);
        return result != -1;
    }
//    db.execSQL("CREATE TABLE "+  TABLE_ANNOUNCEMENTS +" (" +
//            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//            "title TEXT NOT NULL, " +
//            "content TEXT NOT NULL, " +
//            "created_by_role TEXT NOT NULL, " +
//            "created_by_id INTEGER NOT NULL, " +
//            "target_audience TEXT NOT NULL, " +
//            "target_id INTEGER, " +
//            "visible_to_role TEXT, " +
//            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
//            "FOREIGN KEY (created_by_id) REFERENCES Users(user_id));");


    public String getBaseRoleByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String baseRole = null;

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_BASE_ROLE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            baseRole = cursor.getString(0);
            cursor.close();
        }

        return baseRole;
    }

    public List<String> getAllAnnouncements(int userId, String userRole, int departmentId, int subDepartmentId, List<Integer> clubIds) {
        List<String> announcements = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Base query
        String query = "SELECT title, content, created_at FROM Announcements WHERE visible_to_role = ? AND (";

        List<String> selectionArgsList = new ArrayList<>();
        selectionArgsList.add(userRole); // For visible_to_role match

        // Add filters for target_audience
        query += "target_audience = 'all' OR ";
        query += "target_audience = 'all_" + userRole + "s' OR ";

        // Department-specific filter
        query += "(target_audience = 'department' AND target_id = ?) OR ";
        selectionArgsList.add(String.valueOf(departmentId));

        // Subdepartment-specific filter
        query += "(target_audience = 'subdepartment' AND target_id = ?) OR ";
        selectionArgsList.add(String.valueOf(subDepartmentId));

        // Club-specific filter
        if (clubIds != null && !clubIds.isEmpty()) {
            StringBuilder clubConditions = new StringBuilder();
            for (int i = 0; i < clubIds.size(); i++) {
                if (i > 0) clubConditions.append(" OR ");
                clubConditions.append("(target_audience = 'club' AND target_id = ?)");
                selectionArgsList.add(String.valueOf(clubIds.get(i)));
            }
            query += clubConditions.toString();
        } else {
            // To avoid dangling OR at the end if no clubs
            query = query.substring(0, query.length() - 4); // Remove last " OR "
        }

        query += ") ORDER BY created_at DESC";

        Cursor cursor = db.rawQuery(query, selectionArgsList.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String content = cursor.getString(1);
                String timestamp = cursor.getString(2);
                announcements.add("📢 " + title + "\n" + content + "\n🕒 " + timestamp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return announcements;
    }


    public List<String> getAllAnnouncementsForSuperadmin() {
        List<String> announcements = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Superadmin sees all announcements
        String query = "SELECT title, content, created_at FROM Announcements ORDER BY created_at DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String content = cursor.getString(1);
                String timestamp = cursor.getString(2);
                announcements.add("📢 " + title + "\n" + content + "\n🕒 " + timestamp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return announcements;
    }
    public List<String> getAllAnnouncementsForStudent(int studentId) {
        List<String> announcements = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
// Step 1: Get student's club IDs
        List<Integer> studentClubIds = new ArrayList<>();
        Cursor clubCursor = db.rawQuery(
                "SELECT club_id FROM " + TABLE_CLUB_MEMBERS + " WHERE user_id = ?", new String[]{String.valueOf(studentId)}
        );
        if (clubCursor.moveToFirst()) {
            do {
                studentClubIds.add(clubCursor.getInt(0));
            } while (clubCursor.moveToNext());
        }
        clubCursor.close();


        // Step 2: Prepare query for all + students + student's clubs
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT title, content, created_at FROM Announcements ")
                .append("WHERE target_audience IN ('all', 'students', 'all_students') ");

        if (!studentClubIds.isEmpty()) {
            queryBuilder.append("OR (target_audience = 'club' AND target_id IN (")
                    .append(makePlaceholders(studentClubIds.size()))
                    .append(")) ");
        }

        queryBuilder.append("ORDER BY created_at DESC");

        // Step 3: Arguments for club IDs
        String[] args = new String[studentClubIds.size()];
        for (int i = 0; i < studentClubIds.size(); i++) {
            args[i] = String.valueOf(studentClubIds.get(i));
        }

        Cursor cursor = db.rawQuery(queryBuilder.toString(), args);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String content = cursor.getString(1);
                String timestamp = cursor.getString(2);
                announcements.add("📢 " + title + "\n" + content + "\n🕒 " + timestamp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return announcements;
    }

    // Helper for (?, ?, ?, ...) placeholders
    private String makePlaceholders(int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder("?");
        for (int i = 1; i < count; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }
    public void logAllAnnouncements() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ANNOUNCEMENTS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String createdByRole = cursor.getString(cursor.getColumnIndexOrThrow("created_by_role"));
                int createdById = cursor.getInt(cursor.getColumnIndexOrThrow("created_by_id"));
                String targetAudience = cursor.getString(cursor.getColumnIndexOrThrow("target_audience"));
                int targetId = cursor.getInt(cursor.getColumnIndexOrThrow("target_id"));
                String visibleToRole = cursor.getString(cursor.getColumnIndexOrThrow("visible_to_role"));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

                Log.d("ANNOUNCEMENT_ROW", "ID: " + id +
                        ", Title: " + title +
                        ", Content: " + content +
                        ", CreatedByRole: " + createdByRole +
                        ", CreatedById: " + createdById +
                        ", Audience: " + targetAudience +
                        ", TargetID: " + targetId +
                        ", VisibleToRole: " + visibleToRole +
                        ", CreatedAt: " + createdAt);
            } while (cursor.moveToNext());
        } else {
            Log.d("ANNOUNCEMENT_ROW", "No announcements found.");
        }

        cursor.close();
    }
    public List<String> getClubsForStudent(int userId) {
        List<String> clubs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT c.name FROM " + TABLE_CLUBS + " c " +
                        "INNER JOIN " + TABLE_CLUB_MEMBERS + " cm ON c.club_id = cm.club_id " +
                        "WHERE cm.user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            do {
                clubs.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return clubs;
    }
    public String getClubInfo(String clubName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String info = "";

        Cursor cursor = db.rawQuery("SELECT club_code FROM Clubs WHERE name = ?", new String[]{clubName});

        if (cursor.moveToFirst()) {
            String code = cursor.getString(cursor.getColumnIndexOrThrow("club_code"));
            info = "Club Name: " + clubName + "\nClub Code: " + code;
        } else {
            info = "No info found for club: " + clubName;
        }

        cursor.close();
        return info;
    }
    public List<String> getClubFacultyAdvisors(String clubName) {
        List<String> advisors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT u.name FROM Users u " +
                        "JOIN Club_Faculty_Advisors cfa ON u.user_id = cfa.user_id " +
                        "JOIN Clubs c ON c.club_id = cfa.club_id " +
                        "WHERE c.name = ?", new String[]{clubName});

        while (cursor.moveToNext()) {
            advisors.add(cursor.getString(0));
        }

        cursor.close();
        return advisors;
    }
    public List<String> getClubMembersWithRoles(String clubName) {
        List<String> members = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT u.name, cm.role FROM Users u " +
                        "JOIN ClubMembers cm ON u.user_id = cm.user_id " +
                        "JOIN Clubs c ON c.club_id = cm.club_id " +
                        "WHERE c.name = ?", new String[]{clubName});

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String role = cursor.getString(1);
            members.add(name + " (" + role + ")");
        }

        cursor.close();
        return members;
    }

    public int getClubIdByName(String clubName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int clubId = -1;

        Cursor cursor = db.rawQuery(
                "SELECT club_id FROM " + TABLE_CLUBS + " WHERE name = ?",
                new String[]{clubName}
        );

        if (cursor.moveToFirst()) {
            clubId = cursor.getInt(0);
        }

        cursor.close();
        return clubId;
    }

    public boolean addClubAnnouncement(String clubName, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLUB_ANNOUNCEMENT_CLUB_NAME, clubName);
        values.put(COLUMN_CLUB_ANNOUNCEMENT_MESSAGE, message);

        long result = db.insert(TABLE_CLUB_ANNOUNCEMENTS, null, values);
        return result != -1; // returns true if insertion was successful
    }


    // Returns the role of the student in the given club
    public String getStudentRoleInClub(int userId, int clubId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = "";

        Cursor cursor = db.rawQuery(
                "SELECT role FROM " + TABLE_CLUB_MEMBERS + " WHERE user_id = ? AND club_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(clubId)}
        );

        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }

        cursor.close();
        return role;
    }

    // Returns list of announcements for a club
    public List<String> getAnnouncementsForClub(String clubName) {
        List<String> announcements = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT message FROM ClubAnnouncements WHERE club_name = ? ORDER BY timestamp DESC",
                new String[]{clubName}
        );

        while (cursor.moveToNext()) {
            announcements.add(cursor.getString(0));
        }

        cursor.close();
        return announcements;
    }

    public void insertClubChatMessage(String clubName, int senderId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();

        int clubId = getClubIdByName(clubName);
        if (clubId != -1) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CLUB_CHAT_CLUB_ID, clubId);
            values.put(COLUMN_CLUB_CHAT_SENDER_ID, senderId);
            values.put(COLUMN_CLUB_CHAT_MESSAGE, message);

            db.insert(TABLE_CLUB_CHAT_MESSAGES, null, values);
        }
    }
    public List<ChatMessage> getClubChatMessages(String clubName) {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT Users.name, ClubChatMessages.message " +
                "FROM ClubChatMessages " +
                "JOIN Users ON ClubChatMessages.sender_id = Users.user_id " +
                "JOIN Clubs ON ClubChatMessages.club_id = Clubs.club_id " +
                "WHERE Clubs.name = ? " +
                "ORDER BY ClubChatMessages.timestamp ASC";

        Cursor cursor = db.rawQuery(query, new String[]{clubName});

        if (cursor.moveToFirst()) {
            do {
                String senderName = cursor.getString(0);
                String message = cursor.getString(1);
                messages.add(new ChatMessage(senderName, message));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }
    public List<String> getClubsForWhichUserIsFacultyAdvisor(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> clubNames = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT name FROM " + TABLE_CLUBS + " WHERE club_id IN " +
                        "(SELECT club_id FROM " + TABLE_CLUB_FACULTY_ADVISORS + " WHERE user_id = ?)",
                new String[]{String.valueOf(userId)}
        );

        while (cursor.moveToNext()) {
            clubNames.add(cursor.getString(0));
        }

        cursor.close();
        return clubNames;
    }
    public void insertProfessorAnnouncement(String title, String content, int professorId, String createdByRole, String targetAudience, int clubId,String Clubname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();



        // Insert into the appropriate table based on the target audience
        if ("club".equals(targetAudience)) {
            // Insert into ClubAnnouncements table
            values.put("club_name", Clubname);  // Assuming title is the club name in case of club-specific announcements
            values.put("message", content);  // Assuming content is the message of the announcement
            db.insert("ClubAnnouncements", null, values);
        } else {
            values.put("title", title);  // Title of the announcement
            values.put("content", content);  // Content of the announcement
            values.put("created_by_role", createdByRole);  // Role of the creator (Professor)
            values.put("created_by_id", professorId);  // Professor's ID
            values.put("target_audience", targetAudience);  // Target audience (either "all" or "club")
            values.put("target_id", clubId);  // If it's a club announcement, store club ID, otherwise NULL
            values.put("created_at", System.currentTimeMillis());  // Timestamp for the creation time
            // Insert into Announcements table (for all audience)
            db.insert("Announcements", null, values);
        }

        db.close();
    }













}

