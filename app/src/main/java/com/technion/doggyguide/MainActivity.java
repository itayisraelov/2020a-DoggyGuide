package com.technion.doggyguide;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;



import com.google.android.material.tabs.TabLayout;


import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.technion.doggyguide.loginScreen.DogOwnerConnectionFragment;
import com.technion.doggyguide.loginScreen.OrganizationConnectionFragment;

import com.technion.doggyguide.ui.main.SectionsPagerAdapter;


public class MainActivity extends AppCompatActivity implements
        DogOwnerConnectionFragment.OnFragmentInteractionListener,
        OrganizationConnectionFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(MainActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, homeActivity.class);
            finish();
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this,"Please login",Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }
}