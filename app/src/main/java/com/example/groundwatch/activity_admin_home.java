package com.example.groundwatch;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class activity_admin_home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Session.require(this, true)) {
            return;
        }

        setContentView(R.layout.activity_admin_home);

        findViewById(R.id.btnReviewProblems)
                .setOnClickListener(v -> startActivity(
                        new Intent(
                                this,
                                MaintenanceActivity.class
                        )
                ));

        findViewById(R.id.btnAdminReports)
                .setOnClickListener(v -> startActivity(
                        new Intent(
                                this,
                                MyReportsActivity.class
                        )
                ));

        findViewById(R.id.btnAdminProfile)
                .setOnClickListener(v -> startActivity(
                        new Intent(
                                this,
                                ProfileActivity.class
                        )
                ));
    }
}