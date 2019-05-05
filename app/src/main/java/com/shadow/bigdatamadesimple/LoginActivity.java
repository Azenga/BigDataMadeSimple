package com.shadow.bigdatamadesimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.bigdatamadesimple.fragments.login.LoginFragment;
import com.shadow.bigdatamadesimple.fragments.login.SigninAsFragment;

public class LoginActivity extends AppCompatActivity implements SigninAsFragment.SignInListener {
    private static final String TAG = "LoginActivity";

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sign In");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        displayFragment(new SigninAsFragment());
    }

    public void displayFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void getUserWithGroup(String uid) {
        mDb.document("users/" + uid)
                .get()
                .addOnSuccessListener(
                        documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String group = documentSnapshot.getString("group");

                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra(RegisterActivity.USER_GROUP, group);
                                startActivity(intent);

                                progressDialog.dismiss();
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {
                            Log.e(TAG, "getUserWithGroup: ", e);
                            Toast.makeText(this, "A fatal error occurred", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                        }
                );

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            progressDialog.setTitle("Authenticating Signed In Account");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            getUserWithGroup(user.getUid());
        }

    }

    @Override
    public void launchSignAs(String group) {
        displayFragment(LoginFragment.newInstance(group));
    }
}
