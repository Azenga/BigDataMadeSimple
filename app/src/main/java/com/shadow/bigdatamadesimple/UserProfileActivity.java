package com.shadow.bigdatamadesimple;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.bigdatamadesimple.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private Toolbar toolbar;
    private TextView recommendationTV, hiresTV, qualifications;
    private CircleImageView profileCIV;
    private Button messageBtn, hireBtn;

    private String userUid = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private StorageReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (getIntent() != null)
            userUid = getIntent().getStringExtra(MessageActivity.USER_UID_PARAM);

        initComponents();

        if (getSupportActionBar() == null) setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Username");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        messageBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra(MessageActivity.USER_UID_PARAM, userUid);
            startActivity(intent);
        });
    }

    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("avatars");

        recommendationTV = findViewById(R.id.recommendation_tv);
        hiresTV = findViewById(R.id.hires_tv);
        qualifications = findViewById(R.id.qualifications);
        profileCIV = findViewById(R.id.profile_civ);
        messageBtn = findViewById(R.id.message_btn);
        hireBtn = findViewById(R.id.hire_btn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_rate:
                Toast.makeText(this, "Rate Analyst", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_dummy:
                return false;
            default:
                return false;
        }
    }

    private void getUserDetails(String uid) {
        mDb.document("users/" + uid)
                .addSnapshotListener(
                        (documentSnapshot, e) -> {
                            if (e != null) {
                                Log.e(TAG, "getUserDetails: Error", e);
                                return;
                            }

                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                user.setUid(documentSnapshot.getId());
                                if (user != null) updateUI(user);
                            }

                        }
                );
    }

    private void updateUI(User user) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(user.getFullName());
        }

        if (user.getImageName() != null) {
            StorageReference profilePicRef = mRef.child(user.getImageName());

            final long MB = 1024 * 1024;

            profilePicRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profileCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Log.e(TAG, "populateImageView: Failed", e);
                            }
                    );
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            getUserDetails(user.getUid());
        }
    }
}
