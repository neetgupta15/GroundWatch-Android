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

        if (!Session.require(this, true)) {
            return;
        }

        setContentView(R.layout.activity_my_reports);

        lvMyReports = findViewById(R.id.lvMyReports);
        tvNoReports = findViewById(R.id.tvNoReports);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper != null && Session.require(this, true)) {
            loadReports();
        }
    }

    private void loadReports() {

        ArrayList<String> reportList = new ArrayList<>();

        try (Cursor cursor =
                     dbHelper.getAllReportsForMaintenance()) {

            while (cursor.moveToNext()) {

                String report =
                        "Report #" + cursor.getInt(0)
                                + "\nReported by: "
                                + display(cursor.getString(1))
                                + "\nFacility: "
                                + display(cursor.getString(2))
                                + "\nProblem: "
                                + display(cursor.getString(3))
                                + "\nDescription: "
                                + display(cursor.getString(4))
                                + "\nSeverity: "
                                + display(cursor.getString(5))
                                + "\nStatus: "
                                + display(cursor.getString(6))
                                + "\nDate: "
                                + display(cursor.getString(7));

                reportList.add(report);
            }
        }

        lvMyReports.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        reportList
                )
        );

        if (reportList.isEmpty()) {
            tvNoReports.setText("No reports submitted yet.");
            tvNoReports.setVisibility(View.VISIBLE);
            lvMyReports.setVisibility(View.GONE);
        } else {
            tvNoReports.setVisibility(View.GONE);
            lvMyReports.setVisibility(View.VISIBLE);
        }
    }

    private String display(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided"
                : value;
    }

    @Override
    protected void onDestroy() {

        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }
}