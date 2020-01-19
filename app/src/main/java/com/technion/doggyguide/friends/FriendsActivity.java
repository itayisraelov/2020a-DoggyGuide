package com.technion.doggyguide.friends;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import androidx.appcompat.app.AppCompatActivity;
import com.technion.doggyguide.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;



public class FriendsActivity extends AppCompatActivity {

    private CollectionReference mFriendsRef;
    FirebaseAuth users = FirebaseAuth.getInstance();
    private String mCurrentUserUid = users.getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFriendsRef = db.collection("Friends")
                .document(mCurrentUserUid)
                .collection("friends");
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

    private void setUpRecyclerView() {
        Query query = mFriendsRef.orderBy("mName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Friends> options = new FirestoreRecyclerOptions.Builder<Friends>()
                .setQuery(query, Friends.class)
                .build();

        FriendsAdapter mAdapter = new FriendsAdapter(options);
        RecyclerView mFriendsListRecycleView = findViewById(R.id.FriendsRecyclerView_id);
        mFriendsListRecycleView.setHasFixedSize(true);
        mFriendsListRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mFriendsListRecycleView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpRecyclerView();
    }
}

