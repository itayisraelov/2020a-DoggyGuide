package com.technion.doggyguide.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.technion.doggyguide.R;
import com.technion.doggyguide.homeScreen.HomeFragment;
import android.os.Bundle;


public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersListRecycleView;
    private UsersAdapter mAdapter;
    private CollectionReference mUsersRef;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        db = FirebaseFirestore.getInstance();
        mUsersRef = db.collection("dog owners");
    }

    private void setUpRecyclerView() {
        Query query = mUsersRef.orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
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
