package com.technion.doggyguide.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.technion.doggyguide.Adapters.UsersAdapter;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;
import com.technion.doggyguide.dataElements.Users;

import android.os.Bundle;
import android.view.MenuItem;


public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersListRecycleView;
    private UsersAdapter mAdapter;
    private CollectionReference mUsersRef;
    private FirebaseFirestore db;
    String mDogOwners = "dogOwners";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setTitle("All Members");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        db = FirebaseFirestore.getInstance();

        mUsersRef = db.collection(mDogOwners);
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
        Query query = mUsersRef.orderBy("mName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<DogOwnerElement> options = new FirestoreRecyclerOptions.Builder<DogOwnerElement>()
                .setQuery(query, DogOwnerElement.class)
                .build();

        mAdapter = new UsersAdapter(options);
        mUsersListRecycleView = findViewById(R.id.recyclerView_id);
        mUsersListRecycleView.setHasFixedSize(true);
        mUsersListRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mUsersListRecycleView.setAdapter(mAdapter);
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpRecyclerView();
    }
}
