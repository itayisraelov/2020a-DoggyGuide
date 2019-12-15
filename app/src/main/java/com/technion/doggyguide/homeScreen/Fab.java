package com.technion.doggyguide.homeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.technion.doggyguide.R;

public class Fab extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        getSupportActionBar().setTitle("Post a request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void postbtnHandler(View view) {

    }
}
