
package com.example.groundwatch;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MaintenanceActivity extends AppCompatActivity  {

    private ListView listMaintenance;
    private TextView tvEmptyMaintenance;
    private Button btnMaintenanceBack;

    private DBHelper dbHelper;
    private ArrayList<Integer> reportIds;
    private ArrayList<String> reportDetails;
    private ArrayAdapter<String> adapter;

    private final String[] statuses = {
            "Pending",
            "In Progress",
            "Completed"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Session.require(this, true)) {
            return;
        }
        setContentView(R.layout.activity_maintenance);

        listMaintenance = findViewById(R.id.listMaintenance);
        tvEmptyMaintenance = findViewById(R.id.tvEmptyMaintenance);
        btnMaintenanceBack = findViewById(R.id.btnMaintenanceBack);

        dbHelper = new DBHelper(this);

        reportIds = new ArrayList<>();
        reportDetails = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                reportDetails
        );

        listMaintenance.setAdapter(adapter);
        listMaintenance.setEmptyView(tvEmptyMaintenance);

        btnMaintenanceBack.setOnClickListener(v -> finish());

        listMaintenance.setOnItemClickListener(
                (parent, view, position, id) -> {

                    int reportId = reportIds.get(position);

                    new AlertDialog.Builder(this)
                            .setTitle("Report #" + reportId)
                            .setItems(
                                    new String[]{
                                            "Assign Severity",
                                            "Update Maintenance Status"
                                    },
                                    (dialog, which) -> {

                                        if (which == 0) {
                                            showSeverityDialog(reportId);
                                        } else {
                                            showStatusDialog(reportId);
                                        }
                                    }
                            )
                            .setNegativeButton("Cancel", null)
                            .show();
                }
        );
        loadReports();
    }

    private void showSeverityDialog(int reportId) {

        String[] levels = {
                "Low",
                "Medium",
                "High"
        };

        new AlertDialog.Builder(this)
                .setTitle("Assign Severity: Report #" + reportId)
                .setItems(levels, (dialog, which) -> {

                    boolean updated = dbHelper.updateReportSeverity(
                            Session.email(this),
                            reportId,
                            levels[which]
                    );

                    Toast.makeText(
                            this,
                            updated
                                    ? "Severity updated to " + levels[which]
                                    : "Unable to update severity",
                            Toast.LENGTH_SHORT
                    ).show();

                    if (updated) {
                        loadReports();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper != null
                && reportDetails != null
                && Session.require(this, true)) {
            loadReports();
        }
    }

    private void loadReports() {

        reportIds.clear();
        reportDetails.clear();

        Cursor cursor = null;

        try {
            cursor = dbHelper.getAllReportsForMaintenance();

            while (cursor.moveToNext()) {

                int reportId = cursor.getInt(0);
                String reporter = cursor.getString(1);
                String facility = cursor.getString(2);
                String category = cursor.getString(3);
                String description = cursor.getString(4);
                String severity = cursor.getString(5);
                String status = cursor.getString(6);
                String date = cursor.getString(7);

                reportIds.add(reportId);

                String details =
                        "Report ID: " + reportId +
                                "\nFacility: " + facility +
                                "\nCategory: " + category +
                                "\nDescription: " + safeText(description) +
                                "\nSeverity: " + safeText(severity) +
                                "\nStatus: " + safeText(status) +
                                "\nReported by: " + safeText(reporter) +
                                "\nDate: " + safeText(date) +
                                "\n\nTap to assign severity or update status";

                reportDetails.add(details);
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to load reports: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        adapter.notifyDataSetChanged();

        if (reportDetails.isEmpty()) {
            tvEmptyMaintenance.setVisibility(View.VISIBLE);
            tvEmptyMaintenance.setText(
                    "No reports available yet.\nSubmit a problem report first."
            );
        } else {
            tvEmptyMaintenance.setVisibility(View.GONE);
        }
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided"
                : value;
    }

    private void showStatusDialog(int reportId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Update Report #" + reportId);
        builder.setItems(statuses, (dialog, which) -> {

            String selectedStatus = statuses[which];

            boolean updated = dbHelper.updateReportStatus(
                    Session.email(this),
                    reportId,
                    selectedStatus
            );

            if (updated) {
                Toast.makeText(
                        this,
                        "Status updated to " + selectedStatus,
                        Toast.LENGTH_SHORT
                ).show();

                loadReports();

            } else {
                Toast.makeText(
                        this,
                        "Unable to update status",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }
}
