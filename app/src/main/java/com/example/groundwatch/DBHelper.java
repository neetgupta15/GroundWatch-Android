
package com.example.groundwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GroundWatch.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Users table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "password TEXT NOT NULL, " +
                        "role TEXT DEFAULT 'user')"
        );

        // Sports facilities table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Facilities (" +
                        "facility_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "sport TEXT, " +
                        "latitude REAL, " +
                        "longitude REAL, " +
                        "status TEXT DEFAULT 'Available')"
        );

        // Problem reports table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Reports (" +
                        "report_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "facility_id INTEGER, " +
                        "category TEXT, " +
                        "description TEXT, " +
                        "image_path TEXT, " +
                        "latitude REAL, " +
                        "longitude REAL, " +
                        "severity TEXT, " +
                        "status TEXT DEFAULT 'Reported', " +
                        "date TEXT, " +
                        "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
                        "FOREIGN KEY(facility_id) REFERENCES Facilities(facility_id))"
        );

        // Maintenance tracking table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Maintenance (" +
                        "maintenance_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "report_id INTEGER, " +
                        "assigned_to TEXT, " +
                        "start_date TEXT, " +
                        "completion_date TEXT, " +
                        "status TEXT DEFAULT 'Pending', " +
                        "FOREIGN KEY(report_id) REFERENCES Reports(report_id))"
        );

        // Insert sample facilities only when the table is empty.
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Facilities", null
        );

        try {
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                insertFacility(db, "Cricket Ground", "Cricket");
                insertFacility(db, "Football Ground", "Football");
                insertFacility(db, "Basketball Court", "Basketball");
                insertFacility(db, "Badminton Court", "Badminton");
            }
        } finally {
            cursor.close();
        }
    }

    private void insertFacility(SQLiteDatabase db, String name, String sport) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("sport", sport);
        values.put("status", "Available");
        db.insert("Facilities", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Preserve existing user and report data.
    }

    // Register a new user.
    public boolean insertUser(String name, String email, String password) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("email", email.trim());
        values.put("password", password);
        values.put("role", "user");

        long result = db.insert("Users", null, values);
        return result != -1;
    }

    // Check login credentials.
    public boolean checkUser(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT user_id FROM Users WHERE email = ? AND password = ?",
                new String[]{email.trim(), password}
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    // Get a user's name using their email.
    public String getUserName(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT name FROM Users WHERE email = ?",
                new String[]{email.trim()}
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        } finally {
            cursor.close();
        }
    }

    // Submit a new problem report.
    public long insertReport(
            String userEmail,
            String facility,
            String category,
            String description,
            String severity,
            String date) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        db.beginTransaction();

        try {
            int userId = -1;
            int facilityId = -1;

            cursor = db.rawQuery(
                    "SELECT user_id FROM Users WHERE email = ?",
                    new String[]{userEmail.trim()}
            );

            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
            cursor.close();
            cursor = null;

            if (userId == -1) {
                return -1;
            }

            cursor = db.rawQuery(
                    "SELECT facility_id FROM Facilities WHERE name = ?",
                    new String[]{facility}
            );

            if (cursor.moveToFirst()) {
                facilityId = cursor.getInt(0);
            }
            cursor.close();
            cursor = null;

            if (facilityId == -1) {
                return -1;
            }

            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("facility_id", facilityId);
            values.put("category", category);
            values.put("description", description);
            values.put("severity", severity);
            values.put("status", "Reported");
            values.put("date", date);

            long reportId = db.insert("Reports", null, values);

            if (reportId != -1) {
                db.setTransactionSuccessful();
            }

            return reportId;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }
    }

    // Get reports submitted by a particular user.
    // The caller must close the returned Cursor.
    public Cursor getReportsByUser(String userEmail) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT Reports.report_id, " +
                        "Facilities.name, " +
                        "Reports.category, " +
                        "Reports.description, " +
                        "Reports.severity, " +
                        "Reports.status, " +
                        "Reports.date " +
                        "FROM Reports " +
                        "INNER JOIN Users ON Reports.user_id = Users.user_id " +
                        "INNER JOIN Facilities ON Reports.facility_id = Facilities.facility_id " +
                        "WHERE Users.email = ? " +
                        "ORDER BY Reports.report_id DESC",
                new String[]{userEmail.trim()}
        );
    }

    // Get all reports for the maintenance tracking screen.
    // Column order must match MaintenanceActivity.java.
    // The caller must close the returned Cursor.
    public Cursor getAllReportsForMaintenance() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT Reports.report_id, " +
                        "Users.name, " +
                        "Facilities.name, " +
                        "Reports.category, " +
                        "Reports.description, " +
                        "Reports.severity, " +
                        "Reports.status, " +
                        "Reports.date " +
                        "FROM Reports " +
                        "LEFT JOIN Users ON Reports.user_id = Users.user_id " +
                        "LEFT JOIN Facilities ON Reports.facility_id = Facilities.facility_id " +
                        "ORDER BY Reports.report_id DESC",
                null
        );
    }

    // Update a report's maintenance status.
    public boolean updateReportStatus(
            String email, int reportId,
            String newStatus
    ) {

        if (!"admin".equals(getUserRole(adminEmail))) {
            return false;
        }

        if (!"Pending".equals(newStatus)
                && !"In Progress".equals(newStatus)
                && !"Completed".equals(newStatus)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues reportValues = new ContentValues();
            reportValues.put("status", newStatus);

            int updatedRows = db.update(
                    "Reports",
                    reportValues,
                    "report_id = ?",
                    new String[]{String.valueOf(reportId)}
            );

            if (updatedRows == 0) {
                return false;
            }

            Cursor cursor = db.rawQuery(
                    "SELECT maintenance_id FROM Maintenance WHERE report_id = ?",
                    new String[]{String.valueOf(reportId)}
            );

            boolean maintenanceExists;
            try {
                maintenanceExists = cursor.moveToFirst();
            } finally {
                cursor.close();
            }

            ContentValues maintenanceValues = new ContentValues();
            maintenanceValues.put("status", newStatus);

            if ("In Progress".equals(newStatus)) {
                maintenanceValues.put("start_date", getCurrentDate());
            }

            if ("Completed".equals(newStatus)) {
                maintenanceValues.put("completion_date", getCurrentDate());
            }

            if (maintenanceExists) {
                db.update(
                        "Maintenance",
                        maintenanceValues,
                        "report_id = ?",
                        new String[]{String.valueOf(reportId)}
                );
            } else {
                maintenanceValues.put("report_id", reportId);
                db.insert("Maintenance", null, maintenanceValues);
            }

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            return false;

        } finally {
            db.endTransaction();
        }
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat format =
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        java.util.Locale.getDefault()
                );

        return format.format(new java.util.Date());
    }

    public String getUserRole(String email) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT role FROM Users WHERE email = ?",
                new String[]{email}
        )) {
            return cursor.moveToFirst()
                    ? cursor.getString(0)
                    : "";
        }
    }

    public boolean updateReportSeverity(
            String adminEmail,
            int reportId,
            String severity
    ) {

        if (!"admin".equals(getUserRole(adminEmail))) {
            return false;
        }

        if (!"Low".equals(severity)
                && !"Medium".equals(severity)
                && !"High".equals(severity)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("severity", severity);

        int updatedRows = getWritableDatabase().update(
                "Reports",
                values,
                "report_id = ?",
                new String[]{String.valueOf(reportId)}
        );

        return updatedRows == 1;
    }

    // Get the sport associated with a facility.
    private String getSportForFacility(SQLiteDatabase db, String facilityName) {

        Cursor cursor = db.rawQuery(
                "SELECT sport FROM Facilities WHERE name = ?",
                new String[]{facilityName}
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        } finally {
            cursor.close();
        }
    }

}
