
package com.example.groundwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportProblemActivity extends AppCompatActivity {

    private Spinner spFacility;
    private Spinner spCategory;
    private Spinner spSeverity;
    private EditText etDescription;
    private Button btnSubmitReport;

    private DBHelper dbHelper;

    private final String[] facilities = {
            "Cricket Ground",
            "Football Ground",
            "Basketball Court",
            "Badminton Court"
    };

    private final String[] categories = {
            "Select Problem Category",
            "Damaged Surface",
            "Broken Equipment",
            "Poor Lighting",
            "Water Logging",
            "Cleanliness Issue",
            "Other"
    };

    private final String[] severities = {
            "Select Severity",
            "Low",
            "Medium",
            "High"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        dbHelper = new DBHelper(this);

        spFacility = findViewById(R.id.spFacility);
        spCategory = findViewById(R.id.spCategory);
        spSeverity = findViewById(R.id.spSeverity);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        setSpinnerAdapter(spFacility, facilities);
        setSpinnerAdapter(spCategory, categories);
        setSpinnerAdapter(spSeverity, severities);

        btnSubmitReport.setOnClickListener(v -> submitReport());
    }

    private void setSpinnerAdapter(Spinner spinner, String[] items) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void submitReport() {

        String facility = spFacility.getSelectedItem().toString();
        String category = spCategory.getSelectedItem().toString();
        String severity = spSeverity.getSelectedItem().toString();
        String description = etDescription.getText().toString().trim();

        if (category.equals("Select Problem Category")) {
            Toast.makeText(this, "Please select a problem category",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (severity.equals("Select Severity")) {
            Toast.makeText(this, "Please select severity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Please describe the problem");
            etDescription.requestFocus();
            return;
        }

        SharedPreferences preferences =
                getSharedPreferences("GroundWatchPrefs", MODE_PRIVATE);

        String userEmail = preferences.getString("userEmail", "");

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Please log in again",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
        ).format(new Date());

        long result = dbHelper.insertReport(
                userEmail,
                facility,
                category,
                description,
                severity,
                currentDate
        );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Report submitted successfully!",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to save report. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
