
package com.example.groundwatch;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FacilitiesActivity extends AppCompatActivity {

    private ListView lvFacilities;

    private String[] facilityNames = {
            "Cricket Ground",
            "Football Ground",
            "Basketball Court",
            "Badminton Court"
    };

    private String[] facilitySports = {
            "Cricket",
            "Football",
            "Basketball",
            "Badminton"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);

        lvFacilities = findViewById(R.id.lvFacilities);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                facilityNames
        );

        lvFacilities.setAdapter(adapter);

        lvFacilities.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        String message = facilityNames[position]
                                + " selected. Sport: "
                                + facilitySports[position];

                        Toast.makeText(
                                FacilitiesActivity.this,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}
