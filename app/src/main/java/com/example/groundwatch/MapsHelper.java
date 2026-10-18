package com.example.groundwatch;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public final class MapsHelper {

    private MapsHelper() {
    }

    public static void openFacility(Activity activity) {

        String[] site;

        try (DBHelper db = new DBHelper(activity)) {
            site = db.getSite();
        }

        String facilityName = site[0].trim();
        String address = site[2].trim();

        if (address.isEmpty()) {

            Toast.makeText(
                    activity,
                    "Ask the admin to save the facility address first.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String search = facilityName.isEmpty()
                ? address
                : facilityName + ", " + address;

        Uri mapUrl = Uri.parse(
                        "https://www.google.com/maps/search/"
                ).buildUpon()
                .appendQueryParameter("api", "1")
                .appendQueryParameter("query", search)
                .build();

        // Prefer the Google Maps app.
        Intent mapsIntent = new Intent(
                Intent.ACTION_VIEW,
                mapUrl
        );

        mapsIntent.setPackage("com.google.android.apps.maps");

        try {
            activity.startActivity(mapsIntent);

        } catch (ActivityNotFoundException exception) {

            // Open the URL in a browser if Maps is unavailable.
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    mapUrl
            );

            try {
                activity.startActivity(browserIntent);

            } catch (ActivityNotFoundException unavailable) {

                Toast.makeText(
                        activity,
                        "Install Google Maps or a browser to open the map.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}