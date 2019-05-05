package com.shadow.bigdatamadesimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProfileImageActivity extends AppCompatActivity {
    private static final String TAG = "AddProfileImageActivity";

    public static final int RC_CHOOSE_IMAGE = 999;
    public static final int RC_CROP_IMAGE = 888;
    //Widgets
    private TextView skipUploadProfileTV;
    private CircleImageView profileCIV;
    private Button changeImageBtn, uploadImageBtn;
    private ProgressDialog progressDialog;

    private boolean mImageSelected = false;
    private String mGroup = null;

    private StorageReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_image);

        initComponents();

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("avatars");
        mDb = FirebaseFirestore.getInstance();

        if (getIntent() != null) mGroup = getIntent().getStringExtra(RegisterActivity.USER_GROUP);


        changeImageBtn.setOnClickListener(view -> chooseImage());
        skipUploadProfileTV.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));

        uploadImageBtn.setOnClickListener(view -> uploadImage());
    }

    private void uploadImage() {
        if (!mImageSelected) {
            Toast.makeText(this, "Choose an Image to Upload", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) profileCIV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] bytes = baos.toByteArray();

        String uid = mAuth.getCurrentUser().getUid();

        StorageReference profileCIVRef = mRef.child(uid + ".jpg");

        progressDialog.setTitle("Uploading profile Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        UploadTask profilePicUploadTask = profileCIVRef.putBytes(bytes);

        profilePicUploadTask.addOnSuccessListener(
                taskSnapshot -> {
                    final String imageName = taskSnapshot.getMetadata().getName();
                    updateNameInFirestore(imageName);
                }
        )
                .addOnFailureListener(
                        e -> {
                            Toast.makeText(this, "Image upload failed, try again later", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "uploadImage: Failed", e);
                            progressDialog.dismiss();
                        }
                );


    }

    private void updateNameInFirestore(String imageName) {
        mDb.document("users/" + mAuth.getCurrentUser().getUid())
                .update("imageName", imageName)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Profile Image Successfully set", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra(RegisterActivity.USER_GROUP, mGroup);
                                startActivity(intent);
                                finish();

                                finish();
                            } else {
                                Toast.makeText(this, "Updating Image name Failed", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "updateNameInFirestore: Failed", task.getException());
                            }

                            progressDialog.dismiss();
                        }
                );

    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_CHOOSE_IMAGE);
    }

    private void initComponents() {

        //TextView
        skipUploadProfileTV = findViewById(R.id.skip_upload_profile_tv);

        //CircleImageView
        profileCIV = findViewById(R.id.profile_civ);

        //Buttons
        changeImageBtn = findViewById(R.id.change_image_btn);
        uploadImageBtn = findViewById(R.id.upload_image_btn);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if ((resultCode == RESULT_OK) && (data != null)) {

            switch (requestCode) {
                case RC_CHOOSE_IMAGE:
                    openCroppingActivity(data.getData());
                    break;
                case RC_CROP_IMAGE:
                    populateProfileCIV(data);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void populateProfileCIV(Intent data) {

        mImageSelected = true;

        Bundle bundle = data.getExtras();
        Bitmap bitmap = bundle.getParcelable("data");
        profileCIV.setImageBitmap(bitmap);
    }

    private void openCroppingActivity(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", true);
        intent.putExtra("outputX", 224);
        intent.putExtra("outputY", 224);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        PackageManager packageManager = getPackageManager();

        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);

        if (apps.size() > 0) startActivityForResult(intent, RC_CROP_IMAGE);
        else Toast.makeText(this, "Install a cropping application", Toast.LENGTH_SHORT).show();

    }

    private void updateUI(String uid) {
        progressDialog.setTitle("Checking previous Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String imageName = uid + ".jpg";
        populateImageView(imageName);
    }

    private void populateImageView(String imageName) {
        StorageReference profilePicRef = mRef.child(imageName);

        final long MB = 1024 * 1024;

        profilePicRef.getBytes(MB)
                .addOnSuccessListener(
                        bytes -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            profileCIV.setImageBitmap(bitmap);
                            progressDialog.dismiss();
                        }
                )
                .addOnFailureListener(
                        e -> {
                            Toast.makeText(this, "Error getting Image", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Log.e(TAG, "populateImageView: Failed", e);
                        }
                );
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            updateUI(user.getUid());
        }
    }
}
