
package com.example.groundwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.Cursor;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportProblemActivity extends AppCompatActivity {

    private Spinner spFacility;
    private Spinner spCategory;
    private EditText etDescription;
    private Button btnSubmitReport;

    private DBHelper dbHelper;



    private final String[] categories = {
            "Select Problem Category",
            "Damaged Surface",
            "Broken Equipment",
            "Poor Lighting",
            "Water Logging",
            "Cleanliness Issue",
            "Other"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Session.require(this, false)) {
            return;
        }
        setContentView(R.layout.activity_report_problem);

        dbHelper = new DBHelper(this);

        spFacility = findViewById(R.id.spFacility);
        spCategory = findViewById(R.id.spCategory);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        ArrayList<String> facilityNames = new ArrayList<>();

        try (Cursor cursor = dbHelper.getAllFacilities()) {

            while (cursor.moveToNext()) {
                facilityNames.add(cursor.getString(1));
            }
        }

        setSpinnerAdapter(
                spFacility,
                facilityNames.toArray(new String[0])
        );

        String selectedFacility = getIntent().getStringExtra(
                "facility_name"
        );

        if (selectedFacility != null) {

            int position = facilityNames.indexOf(selectedFacility);

            if (position >= 0) {
                spFacility.setSelection(position);
            }
        }

        btnSubmitReport.setEnabled(!facilityNames.isEmpty());
        setSpinnerAdapter(spCategory, categories);

        if (spFacility.getSelectedItem() == null) {

            Toast.makeText(
                    this,
                    "No facilities are available for reporting.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

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
        String severity = "Unassigned";
        String description = etDescription.getText().toString().trim();

        if (category.equals("Select Problem Category")) {
            Toast.makeText(this, "Please select a problem category",
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
