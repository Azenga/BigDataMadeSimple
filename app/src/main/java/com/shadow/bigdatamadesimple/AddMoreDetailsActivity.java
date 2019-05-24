package com.shadow.bigdatamadesimple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddMoreDetailsActivity extends AppCompatActivity {

    private static final String TAG = "AddMoreDetailsActivity";

    private Spinner areaOfSpecificsSpinner, jobTypeSpinner, countriesToWorkSpinner;
    private Switch workRemotelySwitch;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_details);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        initComponents();
    }

    private void initComponents() {
        areaOfSpecificsSpinner = findViewById(R.id.area_of_specifics_spinner);
        ArrayAdapter<CharSequence> areaOfSpecificsAdapter = ArrayAdapter.createFromResource(this, R.array.area_of_specifics_spinner, R.layout.custom_spinner_item);
        areaOfSpecificsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaOfSpecificsSpinner.setAdapter(areaOfSpecificsAdapter);

        jobTypeSpinner = findViewById(R.id.job_type_spinner);
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(this, R.array.available_job_types, R.layout.custom_spinner_item);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobTypeSpinner.setAdapter(jobTypeAdapter);

        setUpCountrySpinner();

        workRemotelySwitch = findViewById(R.id.work_remotely_switch);
        workRemotelySwitch.setChecked(false);

        Button saveMoreBtn = findViewById(R.id.save_more_btn);
        saveMoreBtn.setOnClickListener(view -> saveMoreToFirebase());
    }

    private void saveMoreToFirebase() {
        Map<String, Object> moreHashmap = new HashMap<>();

        moreHashmap.put("specifics", String.valueOf(areaOfSpecificsSpinner.getSelectedItem()));
        moreHashmap.put("jobType", String.valueOf(jobTypeSpinner.getSelectedItem()));
        moreHashmap.put("countries", new String[]{String.valueOf(countriesToWorkSpinner.getSelectedItem())});
        moreHashmap.put("workRemotely", workRemotelySwitch.isChecked());

        String uid = mAuth.getCurrentUser().getUid();

        mDb.document("analysts/" + uid)
                .set(moreHashmap)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                Toast.makeText(this, "Your Preferences Added", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();

                            } else {

                                Log.w(TAG, "saveMoreToFirebase: ", task.getException());

                            }
                        }
                );
    }

    private void setUpCountrySpinner() {
        Locale[] locales = Locale.getAvailableLocales();

        List<String> countries = new ArrayList<>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();

            if ((country.trim().length() > 0) && !countries.contains(country.trim())) {
                countries.add(country.trim());
            }
        }

        Collections.sort(countries);
        countriesToWorkSpinner = findViewById(R.id.countries_to_work_in_spinner);
        ArrayAdapter<String> countriesToWorkAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, countries);
        countriesToWorkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countriesToWorkSpinner.setAdapter(countriesToWorkAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
