package com.shadow.bigdatamadesimple;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

public class SplashActivity extends Activity {


    public static final int RC_APP_PERMISSIONS = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(this);

        Button accountsBtn = findViewById(R.id.accounts_btn);

        accountsBtn.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));

        checkPermissions();
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You have the permissions", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, RC_APP_PERMISSIONS);
            }

        } else {
            Toast.makeText(this, "You have all the prmissions", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            switch (requestCode) {
                case RC_APP_PERMISSIONS:
                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }
        } else {
            Toast.makeText(this, "Sorry! requested permissions denied", Toast.LENGTH_SHORT).show();
        }
    }
}
