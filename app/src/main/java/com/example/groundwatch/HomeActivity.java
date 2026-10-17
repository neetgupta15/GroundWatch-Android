
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);

        btnFacilities = findViewById(R.id.btnFacilities);
        btnReportProblem = findViewById(R.id.btnReportProblem);
        btnMyReports = findViewById(R.id.btnMyReports);
        btnProfile = findViewById(R.id.btnProfile);

        SharedPreferences preferences =
                getSharedPreferences("GroundWatchPrefs", MODE_PRIVATE);

        String name = preferences.getString("userName", "");

        if (name != null && !name.isEmpty()) {
            tvWelcome.setText("Welcome, " + name + "!");
        } else {
            tvWelcome.setText("Welcome to GroundWatch");
        }

        // Open Sports Facilities screen
        btnFacilities.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    FacilitiesActivity.class
            );
            startActivity(intent);
        });

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
