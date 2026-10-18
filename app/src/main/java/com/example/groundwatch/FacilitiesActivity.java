package com.example.groundwatch;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class FacilitiesActivity extends AppCompatActivity {

    private ListView lvFacilities;
    private TextView tvVenue;
    private TextView tvCount;
    private TextView tvEmpty;

    private DBHelper dbHelper;

    private final ArrayList<HashMap<String, String>> facilities =
            new ArrayList<>();

    private String[] site = {"", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Session.require(this, false)) {
            return;
        }

        setContentView(R.layout.activity_facilities);

        dbHelper = new DBHelper(this);

        lvFacilities = findViewById(R.id.lvFacilities);
        tvVenue = findViewById(R.id.tvFacilitiesVenue);
        tvCount = findViewById(R.id.tvFacilityCount);
        tvEmpty = findViewById(R.id.tvFacilitiesEmpty);

        lvFacilities.setEmptyView(tvEmpty);

        findViewById(R.id.btnFacilitiesBack)
                .setOnClickListener(v -> finish());

        lvFacilities.setOnItemClickListener(
                (parent, view, position, id) ->
                        showFacilityDetails(position)
        );

        applySystemBarPadding();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper != null && Session.require(this, false)) {
            loadFacilities();
        }
    }

    private void loadFacilities() {

        facilities.clear();

        tvEmpty.setText("No facilities have been added yet.");

        try {

            site = dbHelper.getSite();

            String venueDetails =
                    display(site[0])
                            + "\nType: " + display(site[1]);

            if (site[2] != null && !site[2].trim().isEmpty()) {
                venueDetails += "\n" + site[2];
            }

            tvVenue.setText(venueDetails);

            try (Cursor cursor = dbHelper.getAllFacilities()) {

                int nameColumn = cursor.getColumnIndexOrThrow("name");
                int sportColumn = cursor.getColumnIndexOrThrow("sport");
                int statusColumn = cursor.getColumnIndexOrThrow("status");

                while (cursor.moveToNext()) {

                    String name = display(
                            cursor.getString(nameColumn)
                    );

                    String sport = display(
                            cursor.getString(sportColumn)
                    );

                    String status = display(
                            cursor.getString(statusColumn)
                    );

                    HashMap<String, String> item = new HashMap<>();

                    item.put("name", name);
                    item.put("sport", sport);
                    item.put("status", status);
                    item.put("sportLabel", "Sport: " + sport);
                    item.put("statusLabel", "Availability: " + status);

                    facilities.add(item);
                }
            }

        } catch (SQLiteException exception) {

            facilities.clear();

            Log.e(
                    "FacilitiesActivity",
                    "Unable to load facilities",
                    exception
            );

            tvEmpty.setText(
                    "Unable to load facilities. Please reopen this screen."
            );
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                facilities,
                R.layout.item_facility,
                new String[]{
                        "name",
                        "sportLabel",
                        "statusLabel"
                },
                new int[]{
                        R.  id.tvFacilityName,
                        R.id.tvFacilitySport,
                        R.id.tvFacilityStatus
                }
        );

        lvFacilities.setAdapter(adapter);

        int count = facilities.size();

        tvCount.setText(
                count + (count == 1
                        ? " facility listed"
                        : " facilities listed")
        );
    }

    private void showFacilityDetails(int position) {

        if (position < 0 || position >= facilities.size()) {
            return;
        }

        HashMap<String, String> facility = facilities.get(position);

        String details =
                "Sport: " + facility.get("sport")
                        + "\nAvailability: " + facility.get("status")
                        + "\n\nVenue: " + display(site[0])
                        + "\nType: " + display(site[1])
                        + "\nAddress: " + display(site[2]);

        new AlertDialog.Builder(this)
                .setTitle(facility.get("name"))
                .setMessage(details)
                .setPositiveButton(
                        "Report a Problem",
                        (dialog, which) -> {

                            Intent intent = new Intent(
                                    FacilitiesActivity.this,
                                    ReportProblemActivity.class
                            );

                            intent.putExtra(
                                    "facility_name",
                                    facility.get("name")
                            );

                            startActivity(intent);
                        }
                )
                .setNeutralButton(
                        "Open Venue Map",
                        (dialog, which) ->
                                MapsHelper.openFacility(
                                        FacilitiesActivity.this
                                )
                )
                .setNegativeButton("Close", null)
                .show();
    }

    private void applySystemBarPadding() {

        View root = findViewById(R.id.facilitiesRoot);

        int left = root.getPaddingLeft();
        int top = root.getPaddingTop();
        int right = root.getPaddingRight();
        int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout()
                    );

                    view.setPadding(
                            left + insets.left,
                            top + insets.top,
                            right + insets.right,
                            bottom + insets.bottom
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(root);
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