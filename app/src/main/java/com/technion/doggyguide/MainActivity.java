package com.technion.doggyguide;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.technion.doggyguide.loginScreen.DogOwnerConnectionFragment;
import com.technion.doggyguide.loginScreen.OrganizationConnectionFragment;
import com.technion.doggyguide.ui.main.SectionsPagerAdapter;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements
        DogOwnerConnectionFragment.OnFragmentInteractionListener,
        OrganizationConnectionFragment.OnFragmentInteractionListener {

    FirebaseAuth users = FirebaseAuth.getInstance();

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
    public void onStart() {
        super.onStart();
        FirebaseUser user = users.getCurrentUser();
        if (user != null) {
            Toast.makeText(MainActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, homeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MainActivity.this,"Please login",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }
}