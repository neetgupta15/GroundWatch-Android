
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

        db.execSQL("CREATE TABLE Users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT DEFAULT 'user')");

        db.execSQL("CREATE TABLE Facilities (" +
                "facility_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "sport TEXT NOT NULL, " +
                "latitude REAL, longitude REAL, " +
                "status TEXT DEFAULT 'Available')");

        db.execSQL("CREATE TABLE Reports (" +
                "report_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, facility_id INTEGER, " +
                "category TEXT NOT NULL, description TEXT, " +
                "image_path TEXT, latitude REAL, longitude REAL, " +
                "severity TEXT, status TEXT DEFAULT 'Reported', " +
                "date TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY(facility_id) REFERENCES Facilities(facility_id))");

        db.execSQL("CREATE TABLE Maintenance (" +
                "maintenance_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "report_id INTEGER, assigned_to TEXT, " +
                "start_date TEXT, completion_date TEXT, " +
                "status TEXT DEFAULT 'Pending', " +
                "FOREIGN KEY(report_id) REFERENCES Reports(report_id))");
    }

    public boolean insertUser(String name, String email, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("role", "user");

        long result = db.insert("Users", null, values);
        db.close();

        return result != -1;
    }

    public boolean checkUser(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT user_id FROM Users WHERE email = ? AND password = ?",
                new String[]{email, password}
        );

        boolean exists = cursor.moveToFirst();

        cursor.close();
        db.close();

        return exists;
    }

    public String getUserName(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT name FROM Users WHERE email = ?",
                new String[]{email}
        );

        String name = "";

        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return name;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database upgrade logic will be added when required.
    }
}
