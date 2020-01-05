package com.technion.doggyguide.users;

import androidx.appcompat.app.AppCompatActivity;

import com.technion.doggyguide.MainActivity;
import com.technion.doggyguide.R;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    private TextView mUserIdTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUserIdTV = findViewById(R.id.user_profile_id);
        String user_id = getIntent().getStringExtra("user_id");
        mUserIdTV.setText(user_id);
    }
}
