
package com.example.groundwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnFacilities;
    private Button btnReportProblem;
    private Button btnMyReports;
    private Button btnProfile;
    private Button btnMaintenance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Connect Java variables to XML views.
        tvWelcome = findViewById(R.id.tvWelcome);
        btnFacilities = findViewById(R.id.btnFacilities);
        btnReportProblem = findViewById(R.id.btnReportProblem);
        btnMyReports = findViewById(R.id.btnMyReports);
        btnProfile = findViewById(R.id.btnProfile);
        btnMaintenance = findViewById(R.id.btnMaintenance);

        // Display the logged-in user's name.
        SharedPreferences prefs =
                getSharedPreferences("GroundWatchPrefs", MODE_PRIVATE);

        String userName = prefs.getString("userName", "User");
        tvWelcome.setText("Welcome, " + userName + "!");

        // Open Facilities.
        btnFacilities.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    FacilitiesActivity.class
            );
            startActivity(intent);
        });

        // Open Report Problem.
        btnReportProblem.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    ReportProblemActivity.class
            );
            startActivity(intent);
        });

        // Open My Reports.
        btnMyReports.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    MyReportsActivity.class
            );
            startActivity(intent);
        });

        // Open Profile.
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    ProfileActivity.class
            );
            startActivity(intent);
        });

        // Open Maintenance Tracking.
        btnMaintenance.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    MaintenanceActivity.class
            );
            startActivity(intent);
        });
    }
}
