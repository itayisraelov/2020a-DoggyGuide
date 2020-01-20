package com.technion.doggyguide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class CreditPicActivity extends AppCompatActivity {
    private ImageView pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_pic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setWindowTitle("Credit");
        String extra = getIntent().getStringExtra("EXTRA");
        pic = findViewById(R.id.pic);
        switch (extra) {
            case "home":
                pic.setImageResource(R.mipmap.dog_home_screen);
                break;
            case "walk":
                pic.setImageResource(R.mipmap.dog_walking_);
                break;
            case "feed":
                pic.setImageResource(R.mipmap.dog_eatting_);
                break;
            case "shower":
                pic.setImageResource(R.mipmap.dog_shower_);
                break;
        }
    }
}
