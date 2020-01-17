package com.technion.doggyguide.Chat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {
    private TextView mTitleView;
    private TextView mStatusView;
    private CircleImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar mChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----

        mTitleView = findViewById(R.id.custom_bar_title);
        mStatusView = findViewById(R.id.custom_bar_status);
        mProfileImage = findViewById(R.id.custom_bar_image);
        String userName = getIntent().getStringExtra("user_name");
        mTitleView.setText(userName);
        String userStatus = getIntent().getStringExtra("user_status");
        mStatusView.setText(userStatus);
        String userImage = getIntent().getStringExtra("user_image");
        Picasso.get().load(userImage).into(mProfileImage);



    }
}
