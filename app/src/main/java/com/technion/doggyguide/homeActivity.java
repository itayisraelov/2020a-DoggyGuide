package com.technion.doggyguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.technion.doggyguide.homeScreen.ChatFragment;
import com.technion.doggyguide.homeScreen.EventsFragment;
import com.technion.doggyguide.homeScreen.HomeFragment;
import com.technion.doggyguide.homeScreen.NotificationsFragment;

public class homeActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.profile:
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment(), "Home_Fragment").commit();
                            break;
                        case R.id.nav_events:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new EventsFragment(), "Events_Fragment").commit();
                            break;
                        case R.id.nav_chat:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new ChatFragment(), "Chat_Fragment").commit();
                            break;
                        case R.id.nav_notifications:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new NotificationsFragment(), "Notifications_Fragment").commit();
                            break;
                    }
                    return true;
                }
            };
}
