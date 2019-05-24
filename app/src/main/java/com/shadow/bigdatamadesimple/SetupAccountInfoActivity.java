package com.shadow.bigdatamadesimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.bigdatamadesimple.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SetupAccountInfoActivity extends AppCompatActivity {
    private static final String TAG = "SetupAccountInfoActivit";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    private String mGroup = null;

    //Widgets
    private TextInputEditText nameTIET, phoneTIET, streetTIET, poboxTIET, cityTIET, stateTIET, zipcodeTIET, websiteTIET;
    private Spinner countrySpinner;
    private ProgressDialog progressDialog;

    private Button gotoAddMoreDetailsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account_info);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Account");
        }

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mGroup = getIntent().getStringExtra(RegisterActivity.USER_GROUP);

        initComponents();

        if (mGroup.equalsIgnoreCase("company")) gotoAddMoreDetailsBtn.setVisibility(View.GONE);

        gotoAddMoreDetailsBtn.setOnClickListener(view -> startActivity(new Intent(this, AddMoreDetailsActivity.class)));

    }

    private void initComponents() {
        nameTIET = findViewById(R.id.name_txt);
        phoneTIET = findViewById(R.id.phone_txt);
        streetTIET = findViewById(R.id.street_txt);
        poboxTIET = findViewById(R.id.po_box_txt);
        cityTIET = findViewById(R.id.city_txt);
        stateTIET = findViewById(R.id.state_txt);
        zipcodeTIET = findViewById(R.id.zipcode_txt);
        websiteTIET = findViewById(R.id.website);

        gotoAddMoreDetailsBtn = findViewById(R.id.goto_add_more_details_btn);

        progressDialog = new ProgressDialog(this);

        countrySpinner = findViewById(R.id.country_spinner);

        Locale[] locales = Locale.getAvailableLocales();

        List<String> countries = new ArrayList<>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();

            if ((country.trim().length() > 0) && !countries.contains(country.trim())) {
                countries.add(country.trim());
            }
        }

        Collections.sort(countries);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_save:
                saveProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveProfile() {
        String name = String.valueOf(nameTIET.getText());
        String contact = String.valueOf(phoneTIET.getText());
        String street = String.valueOf(streetTIET.getText());
        String pobox = String.valueOf(poboxTIET.getText());
        String city = String.valueOf(cityTIET.getText());
        String state = String.valueOf(stateTIET.getText());
        String zipcode = String.valueOf(zipcodeTIET.getText());
        String country = String.valueOf(countrySpinner.getSelectedItem());
        String website = String.valueOf(websiteTIET.getText());

        if (TextUtils.isEmpty(name)) {
            nameTIET.setError("Name required");
            nameTIET.requestFocus();
            return;
        } else if (TextUtils.isEmpty(contact)) {
            phoneTIET.setError("Contact required");
            phoneTIET.requestFocus();
            return;
        }

        User user = new User(name, contact, street, pobox, city, state, zipcode, country, website, mGroup);

        String uid = mAuth.getCurrentUser().getUid();


        progressDialog.setTitle("Updating Account Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mDb.document("users/" + uid)
                .set(user)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(this, AddProfileImageActivity.class);
                                intent.putExtra(RegisterActivity.USER_GROUP, mGroup);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(this, "Operation Failed", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "saveProfile: Failed", task.getException());
                            }

                            progressDialog.dismiss();
                        }
                );
    }

    private void updateFields(User user) {
        if (user.getFullName() != null) nameTIET.setText(user.getFullName());
        if (user.getContact() != null) phoneTIET.setText(user.getContact());
        if (user.getStreet() != null) streetTIET.setText(user.getStreet());
        if (user.getPobox() != null) poboxTIET.setText(user.getPobox());
        if (user.getCity() != null) cityTIET.setText(user.getCity());
        if (user.getState() != null) stateTIET.setText(user.getState());
        if (user.getZipcode() != null) zipcodeTIET.setText(user.getZipcode());
        if (user.getWebsite() != null) websiteTIET.setText(user.getWebsite());

        progressDialog.dismiss();
    }

    private void checkUserDetails(String uid) {
        mDb.document("users/" + uid)
                .addSnapshotListener(
                        (documentSnapshot, e) -> {
                            if (e != null) {
                                Log.e(TAG, "checkUserDetails: Failed", e);
                                progressDialog.dismiss();
                                return;
                            }

                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);

                                if (user != null) updateFields(user);
                            } else {
                                Toast.makeText(this, "Update your account please", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                        }
                );
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            sendToLogin();
        } else {
            progressDialog.setTitle("Checking Use Details");
            progressDialog.setMessage("PLease wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            checkUserDetails(user.getUid());
        }
    }


    private void sendToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void gotoAddMoreDetailsActivty(View view) {

    }
}
