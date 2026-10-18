package com.example.groundwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "GroundWatchDB";

    private static final String DATABASE_NAME = "GroundWatch.db";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ---------------------------------------------------------
    // DATABASE CREATION
    // ---------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "password TEXT NOT NULL, " +
                        "role TEXT DEFAULT 'user')"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Facilities (" +
                        "facility_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "sport TEXT, " +
                        "latitude REAL, " +
                        "longitude REAL, " +
                        "status TEXT DEFAULT 'Available')"
        );

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
                        "FOREIGN KEY(facility_id) " +
                        "REFERENCES Facilities(facility_id))"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Maintenance (" +
                        "maintenance_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "report_id INTEGER, " +
                        "assigned_to TEXT, " +
                        "start_date TEXT, " +
                        "completion_date TEXT, " +
                        "status TEXT DEFAULT 'Pending', " +
                        "FOREIGN KEY(report_id) " +
                        "REFERENCES Reports(report_id))"
        );

        createSiteTable(db);
        ensureDefaultFacilities(db);
    }

    // ---------------------------------------------------------
    // DATABASE UPGRADES
    // ---------------------------------------------------------

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        if (oldVersion < 2) {
            createSiteTable(db);
        }

        if (oldVersion < 3) {
            ensureDefaultFacilities(db);
        }
    }

    // ---------------------------------------------------------
    // DEFAULT FACILITIES
    // ---------------------------------------------------------

    private void ensureDefaultFacilities(SQLiteDatabase db) {

        String[][] facilities = {
                {"Cricket Ground", "Cricket"},
                {"Football Ground", "Football"},
                {"Basketball Court", "Basketball"},
                {"Badminton Court", "Badminton"}
        };

        for (String[] facility : facilities) {

            boolean exists;

            try (Cursor cursor = db.rawQuery(
                    "SELECT facility_id FROM Facilities " +
                            "WHERE name = ? LIMIT 1",
                    new String[]{facility[0]}
            )) {
                exists = cursor.moveToFirst();
            }

            if (!exists) {

                ContentValues values = new ContentValues();

                values.put("name", facility[0]);
                values.put("sport", facility[1]);
                values.put("status", "Available");

                db.insertOrThrow(
                        "Facilities",
                        null,
                        values
                );
            }
        }
    }

    // ---------------------------------------------------------
    // USER REGISTRATION
    // ---------------------------------------------------------

    public boolean insertUser(
            String name,
            String email,
            String password
    ) {

        if (isBlank(name)
                || isBlank(email)
                || password == null
                || password.length() < 6) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put("name", name.trim());
        values.put("email", email.trim());
        values.put("password", password);
        values.put("role", "user");

        try {
            return getWritableDatabase().insertOrThrow(
                    "Users",
                    null,
                    values
            ) != -1;

        } catch (SQLiteException exception) {

            Log.e(
                    TAG,
                    "Unable to register user",
                    exception
            );

            return false;
        }
    }

    // ---------------------------------------------------------
    // LOGIN
    // ---------------------------------------------------------

    public boolean checkUser(
            String email,
            String password
    ) {

        if (isBlank(email) || password == null) {
            return false;
        }

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT user_id FROM Users " +
                        "WHERE email = ? AND password = ?",
                new String[]{
                        email.trim(),
                        password
                }
        )) {
            return cursor.moveToFirst();
        }
    }

    public String getUserName(String email) {

        if (isBlank(email)) {
            return "";
        }

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name FROM Users WHERE email = ?",
                new String[]{email.trim()}
        )) {
            return cursor.moveToFirst()
                    ? cursor.getString(0)
                    : "";
        }
    }

    public String getUserRole(String email) {

        if (isBlank(email)) {
            return "";
        }

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT role FROM Users WHERE email = ?",
                new String[]{email.trim()}
        )) {
            return cursor.moveToFirst()
                    ? cursor.getString(0)
                    : "";
        }
    }

    // ---------------------------------------------------------
    // SUBMIT A REPORT
    // ---------------------------------------------------------

    /*
     * The severity parameter is retained for compatibility with
     * the existing ReportProblemActivity.
     *
     * New reports always start as Unassigned.
     * Only an admin can assign their severity later.
     */
    public long insertReport(
            String userEmail,
            String facility,
            String category,
            String description,
            String severity,
            String date
    ) {

        if (isBlank(userEmail)
                || isBlank(facility)
                || isBlank(category)
                || isBlank(description)) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            int userId;
            int facilityId;

            try (Cursor cursor = db.rawQuery(
                    "SELECT user_id FROM Users WHERE email = ?",
                    new String[]{userEmail.trim()}
            )) {

                if (!cursor.moveToFirst()) {
                    return -1;
                }

                userId = cursor.getInt(0);
            }

            try (Cursor cursor = db.rawQuery(
                    "SELECT facility_id FROM Facilities " +
                            "WHERE name = ? LIMIT 1",
                    new String[]{facility.trim()}
            )) {

                if (!cursor.moveToFirst()) {
                    return -1;
                }

                facilityId = cursor.getInt(0);
            }

            ContentValues values = new ContentValues();

            values.put("user_id", userId);
            values.put("facility_id", facilityId);
            values.put("category", category.trim());
            values.put("description", description.trim());
            values.put("severity", "Unassigned");
            values.put("status", "Reported");

            values.put(
                    "date",
                    isBlank(date) ? getCurrentDate() : date
            );

            long reportId = db.insertOrThrow(
                    "Reports",
                    null,
                    values
            );

            db.setTransactionSuccessful();

            return reportId;

        } catch (SQLiteException exception) {

            Log.e(
                    TAG,
                    "Unable to submit report",
                    exception
            );

            return -1;

        } finally {
            db.endTransaction();
        }
    }

    // ---------------------------------------------------------
    // REPORT QUERIES
    // Caller must close the returned Cursor.
    // ---------------------------------------------------------

    public Cursor getReportsByUser(String userEmail) {

        String email = userEmail == null
                ? ""
                : userEmail.trim();

        return getReadableDatabase().rawQuery(
                "SELECT " +
                        "Reports.report_id, " +
                        "Facilities.name, " +
                        "Reports.category, " +
                        "Reports.description, " +
                        "Reports.severity, " +
                        "Reports.status, " +
                        "Reports.date " +
                        "FROM Reports " +
                        "INNER JOIN Users " +
                        "ON Reports.user_id = Users.user_id " +
                        "INNER JOIN Facilities " +
                        "ON Reports.facility_id = Facilities.facility_id " +
                        "WHERE Users.email = ? " +
                        "ORDER BY Reports.report_id DESC",
                new String[]{email}
        );
    }

    /*
     * Column order used by MaintenanceActivity and MyReportsActivity:
     *
     * 0: report ID
     * 1: reporter name
     * 2: facility name
     * 3: category
     * 4: description
     * 5: severity
     * 6: status
     * 7: date
     *
     * These screens must retain their Session admin checks.
     */
    public Cursor getAllReportsForMaintenance() {

        return getReadableDatabase().rawQuery(
                "SELECT " +
                        "Reports.report_id, " +
                        "Users.name, " +
                        "Facilities.name, " +
                        "Reports.category, " +
                        "Reports.description, " +
                        "Reports.severity, " +
                        "Reports.status, " +
                        "Reports.date " +
                        "FROM Reports " +
                        "LEFT JOIN Users " +
                        "ON Reports.user_id = Users.user_id " +
                        "LEFT JOIN Facilities " +
                        "ON Reports.facility_id = Facilities.facility_id " +
                        "ORDER BY Reports.report_id DESC",
                null
        );
    }

    // ---------------------------------------------------------
    // ADMIN: ASSIGN SEVERITY
    // ---------------------------------------------------------

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

        try {

            int updatedRows = getWritableDatabase().update(
                    "Reports",
                    values,
                    "report_id = ?",
                    new String[]{String.valueOf(reportId)}
            );

            return updatedRows == 1;

        } catch (SQLiteException exception) {

            Log.e(
                    TAG,
                    "Unable to update report severity",
                    exception
            );

            return false;
        }
    }

    // ---------------------------------------------------------
    // ADMIN: UPDATE MAINTENANCE STATUS
    // ---------------------------------------------------------

    public boolean updateReportStatus(
            String adminEmail,
            int reportId,
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

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            boolean maintenanceExists;

            String previousStatus = null;
            String previousStartDate = null;
            String previousCompletionDate = null;

            try (Cursor cursor = db.rawQuery(
                    "SELECT status, start_date, completion_date " +
                            "FROM Maintenance WHERE report_id = ?",
                    new String[]{String.valueOf(reportId)}
            )) {

                maintenanceExists = cursor.moveToFirst();

                if (maintenanceExists) {
                    previousStatus = cursor.getString(0);
                    previousStartDate = cursor.getString(1);
                    previousCompletionDate = cursor.getString(2);
                }
            }

            ContentValues reportValues = new ContentValues();
            reportValues.put("status", newStatus);

            int reportRows = db.update(
                    "Reports",
                    reportValues,
                    "report_id = ?",
                    new String[]{String.valueOf(reportId)}
            );

            if (reportRows != 1) {
                return false;
            }

            ContentValues maintenanceValues = new ContentValues();
            maintenanceValues.put("status", newStatus);

            String now = getCurrentDate();

            if ("Pending".equals(newStatus)) {

                maintenanceValues.putNull("start_date");
                maintenanceValues.putNull("completion_date");

            } else if ("In Progress".equals(newStatus)) {

                boolean keepStartDate =
                        "In Progress".equals(previousStatus)
                                && !isBlank(previousStartDate);

                maintenanceValues.put(
                        "start_date",
                        keepStartDate ? previousStartDate : now
                );

                maintenanceValues.putNull("completion_date");

            } else {

                if (isBlank(previousStartDate)) {
                    maintenanceValues.putNull("start_date");
                } else {
                    maintenanceValues.put(
                            "start_date",
                            previousStartDate
                    );
                }

                boolean keepCompletionDate =
                        "Completed".equals(previousStatus)
                                && !isBlank(previousCompletionDate);

                maintenanceValues.put(
                        "completion_date",
                        keepCompletionDate
                                ? previousCompletionDate
                                : now
                );
            }

            if (maintenanceExists) {

                int maintenanceRows = db.update(
                        "Maintenance",
                        maintenanceValues,
                        "report_id = ?",
                        new String[]{String.valueOf(reportId)}
                );

                if (maintenanceRows != 1) {
                    return false;
                }

            } else {

                maintenanceValues.put("report_id", reportId);

                long maintenanceId = db.insertOrThrow(
                        "Maintenance",
                        null,
                        maintenanceValues
                );

                if (maintenanceId == -1) {
                    return false;
                }
            }

            db.setTransactionSuccessful();

            return true;

        } catch (SQLiteException exception) {

            Log.e(
                    TAG,
                    "Unable to update maintenance for report " + reportId,
                    exception
            );

            return false;

        } finally {
            db.endTransaction();
        }
    }

    // ---------------------------------------------------------
    // FACILITY NAME, TYPE AND ADDRESS
    // ---------------------------------------------------------

    private void createSiteTable(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Site (" +
                        "id INTEGER PRIMARY KEY CHECK(id = 1), " +
                        "name TEXT NOT NULL, " +
                        "type TEXT NOT NULL, " +
                        "address TEXT NOT NULL)"
        );

        db.execSQL(
                "INSERT OR IGNORE INTO Site " +
                        "(id, name, type, address) VALUES " +
                        "(1, 'NMIMS Shirpur Sports Facility', " +
                        "'Private', '')"
        );
    }

    public String[] getSite() {

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name, type, address FROM Site WHERE id = 1",
                null
        )) {

            if (cursor.moveToFirst()) {

                return new String[]{
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)
                };
            }
        }

        return new String[]{"", "Private", ""};
    }

    public boolean updateSite(
            String adminEmail,
            String name,
            String type,
            String address
    ) {

        if (!"admin".equals(getUserRole(adminEmail))) {
            return false;
        }

        if (isBlank(name) || isBlank(address)) {
            return false;
        }

        if (!"Public".equals(type) && !"Private".equals(type)) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put("name", name.trim());
        values.put("type", type);
        values.put("address", address.trim());

        try {

            int updatedRows = getWritableDatabase().update(
                    "Site",
                    values,
                    "id = 1",
                    null
            );

            return updatedRows == 1;

        } catch (SQLiteException exception) {

            Log.e(
                    TAG,
                    "Unable to update facility details",
                    exception
            );

            return false;
        }
    }

    // ---------------------------------------------------------
    // UTILITY METHODS
    // ---------------------------------------------------------

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String getCurrentDate() {

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
        );

        return format.format(new Date());
    }
}