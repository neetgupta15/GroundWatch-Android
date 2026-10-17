package com.example.groundwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Session.require(this, false)) {
            return;
        }

        // Admin accounts use the admin dashboard.
        try (DBHelper db = new DBHelper(this)) {

            String role = db.getUserRole(
                    Session.email(this)
            );

            if ("admin".equals(role)) {

                startActivity(
                        new Intent(
                                this,
                                activity_admin_home.class
                        )
                );

                finish();
                return;
            }
        }

        setContentView(R.layout.activity_home);

        String name = getSharedPreferences(
                "GroundWatchPrefs",
                MODE_PRIVATE
        ).getString("userName", "User");

        TextView welcome = findViewById(R.id.tvWelcome);
        welcome.setText("Welcome, " + name + "!");

        findViewById(R.id.btnFacilities)
                .setOnClickListener(v -> startActivity(
                        new Intent(
                                this,
                                FacilitiesActivity.class
                        )
                ));

        findViewById(R.id.btnReportProblem)
                .setOnClickListener(v -> startActivity(
                        new Intent(
                                this,
                                ReportProblemActivity.class
                        )
                ));

        findViewById(R.id.btnProfile)
                .setOnClickListener(v -> startActivity(
                        new Intent(
                                this,
                                ProfileActivity.class
                        )
                ));
    }
}