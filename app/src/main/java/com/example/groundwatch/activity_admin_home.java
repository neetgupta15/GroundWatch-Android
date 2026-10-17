package com.example.groundwatch;

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class activity_admin_home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Session.require(this, true)) {
            return;
        }

        setContentView(R.layout.activity_admin_home);
        findViewById(R.id.btnEditAddress)
                .setOnClickListener(v -> editSite());

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

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFinishing() && Session.require(this, true)) {
            showSite();
        }
    }

    private void showSite() {

        try (DBHelper db = new DBHelper(this)) {

            String[] site = db.getSite();

            String address = site[2].isEmpty()
                    ? "Use Edit Facility and Address to add the address."
                    : site[2];

            TextView addressView = findViewById(
                    R.id.tvAdminAddress
            );

            addressView.setText(
                    site[0]
                            + "\nType: " + site[1]
                            + "\nAddress: " + address
            );
        }
    }

    private void editSite() {

        String[] site;

        try (DBHelper db = new DBHelper(this)) {
            site = db.getSite();
        }

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (
                24 * getResources().getDisplayMetrics().density
        );

        form.setPadding(padding, padding, padding, 0);

        // Facility name
        EditText nameInput = new EditText(this);

        nameInput.setHint("Facility name");
        nameInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        nameInput.setText(site[0]);

        // Public or private
        TextView typeLabel = new TextView(this);
        typeLabel.setText("Facility Type");

        Spinner typeInput = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Public", "Private"}
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        typeInput.setAdapter(adapter);

        typeInput.setSelection(
                "Public".equals(site[1]) ? 0 : 1
        );

        // Address
        EditText addressInput = new EditText(this);

        addressInput.setHint("Address, city and postal code");
        addressInput.setMinLines(2);

        addressInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        addressInput.setText(site[2]);

        form.addView(nameInput);
        form.addView(typeLabel);
        form.addView(typeInput);
        form.addView(addressInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Facility Details")
                .setView(form)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(unused ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                            String name = nameInput.getText()
                                    .toString().trim();

                            String address = addressInput.getText()
                                    .toString().trim();

                            String type = typeInput
                                    .getSelectedItem().toString();

                            if (name.isEmpty()) {
                                nameInput.setError("Enter the facility name");
                                nameInput.requestFocus();
                                return;
                            }

                            if (address.isEmpty()) {
                                addressInput.setError("Enter the facility address");
                                addressInput.requestFocus();
                                return;
                            }

                            try (DBHelper db = new DBHelper(this)) {

                                boolean saved = db.updateSite(
                                        Session.email(this),
                                        name,
                                        type,
                                        address
                                );

                                if (saved) {

                                    showSite();
                                    dialog.dismiss();

                                    Toast.makeText(
                                            this,
                                            "Facility details saved",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "Unable to save facility details",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        })
        );

        dialog.show();
    }
}