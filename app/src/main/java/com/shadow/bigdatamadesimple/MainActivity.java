package com.shadow.bigdatamadesimple;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shadow.bigdatamadesimple.adapters.ViewPagerAdapter;
import com.shadow.bigdatamadesimple.fragments.AnalystsFragment;
import com.shadow.bigdatamadesimple.fragments.BlogPostFragment;
import com.shadow.bigdatamadesimple.fragments.ChatsFragment;
import com.shadow.bigdatamadesimple.fragments.CompaniesFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabs;
    private ViewPager container;

    private String mGroup = null;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        mAuth = FirebaseAuth.getInstance();

        mGroup = getIntent().getStringExtra(RegisterActivity.USER_GROUP);

        SharedPreferences preferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);

        if (mGroup == null) {
            mGroup = preferences.getString("group", "");
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("group", mGroup);
            editor.apply();
        }

        if (getSupportActionBar() == null) setSupportActionBar(toolbar);

        if (mGroup.equalsIgnoreCase("company")) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Big Data Analysts");
        } else {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Big Data Made Simple");
        }


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new BlogPostFragment(), "Posts");
        if (mGroup.equals("company")) {
            adapter.addFragment(new AnalystsFragment(), "Analysts");
        } else if (mGroup.equals("analyst")) {
            adapter.addFragment(new CompaniesFragment(), "Companies");
        }

        container.setAdapter(adapter);

        tabs.setupWithViewPager(container);
    }

    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        container = findViewById(R.id.viewpager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_edit_profile_pic:
                Intent profilePic = new Intent(this, AddProfileImageActivity.class);
                profilePic.putExtra(RegisterActivity.USER_GROUP, mGroup);
                startActivity(profilePic);
                return true;
            case R.id.nav_edit:
                Intent intent = new Intent(this, SetupAccountInfoActivity.class);
                intent.putExtra(RegisterActivity.USER_GROUP, mGroup);
                startActivity(intent);
                return true;
            case R.id.nav_logout:
                mAuth.signOut();
                sendToLogin();
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            sendToLogin();
        }
    }
}
