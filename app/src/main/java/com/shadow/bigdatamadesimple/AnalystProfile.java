package com.shadow.bigdatamadesimple;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shadow.bigdatamadesimple.adapters.ViewPagerAdapter;
import com.shadow.bigdatamadesimple.fragments.analyst.BasicInfoFragment;
import com.shadow.bigdatamadesimple.fragments.analyst.MoreAnalystFragment;

public class AnalystProfile extends AppCompatActivity {


    private String userUid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyst_profile);

        if (getIntent() != null)
            userUid = getIntent().getStringExtra(MessageActivity.USER_UID_PARAM);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() == null)
            setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Analyst Name");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        TabLayout tabLayout = findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(BasicInfoFragment.newInstance(userUid), "Basic Info");
        adapter.addFragment(MoreAnalystFragment.newInstance(userUid), "More Details");

        ViewPager viewPager = findViewById(R.id.container);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }
}
