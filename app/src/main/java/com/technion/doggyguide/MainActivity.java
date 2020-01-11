package com.technion.doggyguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.technion.doggyguide.loginScreen.DogOwnerConnectionFragment;
import com.technion.doggyguide.loginScreen.OrganizationConnectionFragment;
import com.technion.doggyguide.ui.main.SectionsPagerAdapter;

import java.util.List;


public class MainActivity extends AppCompatActivity implements
        DogOwnerConnectionFragment.OnFragmentInteractionListener,
        OrganizationConnectionFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            FirebaseInstanceId.getInstance()
                    .getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    final String mDeviceToken = instanceIdResult.getToken();
                    final DocumentReference mUserRef = db.collection("dog owners")
                            .document(user.getUid());
                    mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                List<String> tokens = (List<String>) doc.get("mTokens");
                                if (tokens != null) {
                                    tokens.add(mDeviceToken);
                                    mUserRef.update("mTokens", tokens);
                                }
                                Toast.makeText(MainActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, homeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }
            });

        } else {
            Toast.makeText(MainActivity.this, "Please login", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }
}