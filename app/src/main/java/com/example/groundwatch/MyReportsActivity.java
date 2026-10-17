
package com.example.groundwatch;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyReportsActivity extends AppCompatActivity {

    private ListView lvMyReports;
    private TextView tvNoReports;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        lvMyReports = findViewById(R.id.lvMyReports);
        tvNoReports = findViewById(R.id.tvNoReports);

        dbHelper = new DBHelper(this);

        loadReports();
    }

    private void loadReports() {

        String email = getSharedPreferences(
                "GroundWatchPrefs", MODE_PRIVATE
        ).getString("userEmail", "");

        ArrayList<String> reportList = new ArrayList<>();

        if (email == null || email.isEmpty()) {
            showMessage("Please log in again to view your reports.");
            return;
        }

        Cursor cursor = dbHelper.getReportsByUser(email);

        try {
            while (cursor.moveToNext()) {

                String facility = cursor.getString(1);
                String category = cursor.getString(2);
                String description = cursor.getString(3);
                String severity = cursor.getString(4);
                String status = cursor.getString(5);
                String date = cursor.getString(6);

                String report =
                        "Facility: " + facility +
                                "\nProblem: " + category +
                                "\nDescription: " + description +
                                "\nSeverity: " + severity +
                                "\nStatus: " + status +
                                "\nDate: " + date;

                reportList.add(report);
            }
        } finally {
            cursor.close();
        }

        if (reportList.isEmpty()) {
            showMessage("No reports submitted yet.");
        } else {
            tvNoReports.setVisibility(View.GONE);
            lvMyReports.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    reportList
            );

            lvMyReports.setAdapter(adapter);
        }
    }

    private void showMessage(String message) {
        tvNoReports.setText(message);
        tvNoReports.setVisibility(View.VISIBLE);
        lvMyReports.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }
}
