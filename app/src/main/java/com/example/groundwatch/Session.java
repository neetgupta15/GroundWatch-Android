package com.example.groundwatch;

import android.app.Activity;
import android.content.Intent;

final class Session {

    static String email(Activity activity) {
        return activity.getSharedPreferences(
                "GroundWatchPrefs",
                Activity.MODE_PRIVATE
        ).getString("userEmail", "");
    }

    static boolean require(Activity activity, boolean adminOnly) {

        String role;

        try (DBHelper db = new DBHelper(activity)) {
            role = db.getUserRole(email(activity));
        }

        if (!"user".equals(role) && !"admin".equals(role)) {

            Intent intent = new Intent(
                    activity,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            activity.startActivity(intent);
            activity.finish();

            return false;
        }

        if (adminOnly && !"admin".equals(role)) {
            activity.finish();
            return false;
        }

        return true;
    }
}