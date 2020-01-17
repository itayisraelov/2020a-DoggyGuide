package com.technion.doggyguide.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.technion.doggyguide.R;
import android.os.Bundle;


public class UsersActivity extends AppCompatActivity {

    private CollectionReference mUsersRef;
    String mDogOwners = "dogOwners";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mUsersRef = db.collection(mDogOwners);
    }

    private void setUpRecyclerView() {
        Query query = mUsersRef.orderBy("mName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        UsersAdapter mAdapter = new UsersAdapter(options);
        RecyclerView mUsersListRecycleView = findViewById(R.id.recyclerView_id);
        mUsersListRecycleView.setHasFixedSize(true);
        mUsersListRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mUsersListRecycleView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpRecyclerView();
    }
}
