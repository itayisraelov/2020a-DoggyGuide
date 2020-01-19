package com.technion.doggyguide;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.technion.doggyguide.friends.FriendsActivity;
import com.technion.doggyguide.homeScreen.ChatFragment;
import com.technion.doggyguide.homeScreen.EventsFragment;
import com.technion.doggyguide.homeScreen.HomeFragment;
import com.technion.doggyguide.homeScreen.NotificationsFragment;
import com.technion.doggyguide.profile.DogProfileActivity;
import com.technion.doggyguide.profile.UserProfileActivity;
import com.technion.doggyguide.ui.main.HomeSectionsPagerAdapter;
import com.technion.doggyguide.users.UsersActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class homeActivity extends AppCompatActivity implements
           HomeFragment.OnFragmentInteractionListener,
           EventsFragment.OnFragmentInteractionListener,
           ChatFragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGSC;
    private GoogleSignInOptions mGSO;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String mDogOwners = "dogOwners";
    private CollectionReference usersRef = db.collection(mDogOwners);
    FirebaseAuth users = FirebaseAuth.getInstance();


    private static final int[] TAB_ICONS = new int[] {R.drawable.ic_home,
            R.drawable.ic_chat_24px,
            R.drawable.ic_alarm_add,
            R.drawable.ic_event};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Credits:
                Intent intent__ = new Intent(homeActivity.this, Credits.class);
                startActivity(intent__);
                return true;
            case R.id.all_users:
                Intent intent_users = new Intent(homeActivity.this, UsersActivity.class);
                startActivity(intent_users);
                return true;
            case R.id.About:
                Intent intent_ = new Intent(homeActivity.this, About.class);
                startActivity(intent_);
                return true;
            case R.id.user_profile:
                Intent intent_profile = new Intent(homeActivity.this, UserProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.Dog_profile:
                Intent intent_dog_profil = new Intent(homeActivity.this, DogProfileActivity.class);
                startActivity(intent_dog_profil);
                return true;

            case R.id.friends:
                Intent friends_intent = new Intent(homeActivity.this, FriendsActivity.class);
                startActivity(friends_intent);
                return true;

            case R.id.logout:
                final DocumentReference userRef = db.document("dogOwners/" + mAuth.getCurrentUser().getUid());
                userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final List<String> mTokens = (List<String>) documentSnapshot.get("mTokens");
                        FirebaseInstanceId
                                .getInstance()
                                .getInstanceId()
                                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        String mDeviceToken = instanceIdResult.getToken();
                                        mTokens.remove((String) mDeviceToken);
                                        userRef.update("mTokens", mTokens);
                                    }
                                });
                    }
                });
                mAuth.signOut();
                mGSC.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(homeActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        HomeSectionsPagerAdapter homesectionsPagerAdapter = new HomeSectionsPagerAdapter(this,
                                                                                    getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.home_view_pager);
        viewPager.setAdapter(homesectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.home_tabs);
        tabs.setupWithViewPager(viewPager, true);
        tabs.getTabAt(0).setIcon(TAB_ICONS[0]);
        tabs.getTabAt(1).setIcon(TAB_ICONS[1]);
        tabs.getTabAt(2).setIcon(TAB_ICONS[2]);
        tabs.getTabAt(3).setIcon(TAB_ICONS[3]);


        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();


        mGSO = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.Web_Client_ID))
                .requestEmail()
                .build();

        mGSC = GoogleSignIn.getClient(this, mGSO);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(users.getCurrentUser() != null){
//            String mCurrentUserUid = users.getCurrentUser().getUid();
//            final DocumentReference currentUserDocument = usersRef.document(mCurrentUserUid);
//            currentUserDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("online", "true");
//                    currentUserDocument.set(data, SetOptions.merge());
//                }
//            });
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(users.getCurrentUser() != null){
//            String mCurrentUserUid = users.getCurrentUser().getUid();
//            final DocumentReference currentUserDocument = usersRef.document(mCurrentUserUid);
//            currentUserDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("online", "true");
//                    currentUserDocument.update("online","false");
//                }
//            });
//        }
    }
}


