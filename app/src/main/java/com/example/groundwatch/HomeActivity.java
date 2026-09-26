package com.example.groundwatch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnFacilities;
    private Button btnReportProblem;
    private Button btnMyReports;
    private Button btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnFacilities = findViewById(R.id.btnFacilities);
        btnReportProblem = findViewById(R.id.btnReportProblem);
        btnMyReports = findViewById(R.id.btnMyReports);
        btnProfile = findViewById(R.id.btnProfile);

        btnFacilities.setOnClickListener(v ->
                Toast.makeText(
                        HomeActivity.this,
                        "Sports Facilities coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnReportProblem.setOnClickListener(v ->
                Toast.makeText(
                        HomeActivity.this,
                        "Report Problem coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnMyReports.setOnClickListener(v ->
                Toast.makeText(
                        HomeActivity.this,
                        "My Reports coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnProfile.setOnClickListener(v ->
                Toast.makeText(
                        HomeActivity.this,
                        "Profile coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}