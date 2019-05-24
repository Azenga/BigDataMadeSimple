package com.shadow.bigdatamadesimple;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAdvertActivity extends AppCompatActivity {

    private TextInputEditText advertContentTIET, durationTIET;
    private Button addAdvertBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advert);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Advert");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initComponents();

        addAdvertBtn.setOnClickListener(view -> sendAdvert());
    }

    private void sendAdvert() {
        String content = String.valueOf(advertContentTIET.getText());
        String duration = String.valueOf(durationTIET.getText());

        Map<String, Object> advertMap = new HashMap<>();

        advertMap.put("content", content);
        advertMap.put("companyId", mAuth.getCurrentUser().getUid());
        advertMap.put("timestamp", FieldValue.serverTimestamp());
        advertMap.put("duration", Integer.parseInt(duration.trim()));
    }

    private void initComponents() {
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        advertContentTIET = findViewById(R.id.advert_content_txt);
        durationTIET = findViewById(R.id.duration_txt);
        addAdvertBtn = findViewById(R.id.add_advert_btn);
    }
}
