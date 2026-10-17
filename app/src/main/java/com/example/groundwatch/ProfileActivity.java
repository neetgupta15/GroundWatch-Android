
package com.example.groundwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail;
    private Button btnBack, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnBack = findViewById(R.id.btnProfileBack);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs =
                getSharedPreferences("GroundWatchPrefs", MODE_PRIVATE);

        String name = prefs.getString("userName", "User");
        String email = prefs.getString("userEmail", "");

        tvName.setText(name);
        tvEmail.setText(email);

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            prefs.edit()
                    .remove("userName")
                    .remove("userEmail")
                    .apply();

            Toast.makeText(
                    ProfileActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
            ).show();

            android.content.Intent intent =
                    new android.content.Intent(
                            ProfileActivity.this,
                            MainActivity.class
                    );

            intent.setFlags(
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}
