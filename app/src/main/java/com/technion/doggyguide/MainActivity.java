package com.technion.doggyguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;


import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


import com.technion.doggyguide.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements
        DogOwnerConnectionFragment.OnFragmentInteractionListener,
        OrganizationConnectionFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }
}