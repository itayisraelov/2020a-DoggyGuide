package com.technion.doggyguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Credits extends AppCompatActivity {
    private TextView  mHomeText, mWalkText, mShowerText, mFeedText;
    private final String EXTRA = "EXTRA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credtis);
        getSupportActionBar().setTitle("Credits");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHomeText = findViewById(R.id.home_screen);
        mWalkText = findViewById(R.id.walk);
        mShowerText = findViewById(R.id.shower);
        mFeedText = findViewById(R.id.feed);
        mHomeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Credits.this, CreditPicActivity.class);
                intent.putExtra(EXTRA,"home");
                startActivity(intent);
            }
        });
        mWalkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Credits.this, CreditPicActivity.class);
                intent.putExtra(EXTRA,"walk");
                startActivity(intent);
            }
        });
        mShowerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Credits.this, CreditPicActivity.class);
                intent.putExtra(EXTRA,"shower");
                startActivity(intent);
            }
        });
        mFeedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Credits.this, CreditPicActivity.class);
                intent.putExtra(EXTRA,"feed");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
