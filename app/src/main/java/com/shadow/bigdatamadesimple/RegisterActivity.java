package com.shadow.bigdatamadesimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static final String USER_GROUP = "user-group";

    //Widgets
    private TextInputEditText emailTIET, pwdTIET;
    private Button signupBtn;
    private TextView gotoLoginTV;

    private ProgressDialog progressDialog;

    private String mUserGroup = null;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sign Up");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        initComponents();

        mUserGroup = getIntent().getStringExtra(USER_GROUP);

        signupBtn.setOnClickListener(view -> createAccount());
        gotoLoginTV.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void initComponents() {
        //TextInputEditTexts
        emailTIET = findViewById(R.id.email_txt);
        pwdTIET = findViewById(R.id.pwd_txt);

        //Button
        signupBtn = findViewById(R.id.signup_btn);

        //TextView
        gotoLoginTV = findViewById(R.id.goto_login_tv);

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
    }

    private void createAccount() {

        String email = String.valueOf(emailTIET.getText());
        String pwd = String.valueOf(pwdTIET.getText());

        if (TextUtils.isEmpty(email)) {
            emailTIET.setError("Email is Required");
            emailTIET.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            pwdTIET.setError("Password is Required");
            pwdTIET.requestFocus();
            return;
        }

        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(this, SetupAccountInfoActivity.class);
                                intent.putExtra(RegisterActivity.USER_GROUP, mUserGroup);
                                startActivity(intent);

                            } else {
                                Toast.makeText(this, "Oops!! operation failed try again later", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "createAccount: Failed", task.getException());
                            }

                            progressDialog.dismiss();
                        }
                );


    }

}
