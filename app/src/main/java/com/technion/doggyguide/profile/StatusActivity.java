package com.technion.doggyguide.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;

public class StatusActivity extends AppCompatActivity {
    EditText mEditText;
    Button mSaveChanges;
    TextView mStatus;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth users = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection("dogOwners");
    String userUid = users.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mEditText = findViewById(R.id.insert_status_id);
        mSaveChanges = findViewById(R.id.save_changes_id);


        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = usersRef.document(userUid);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        DogOwnerElement dogOwnerElement = documentSnapshot.toObject(DogOwnerElement.class);
                        if (dogOwnerElement != null){
                            dogOwnerElement.setmStatus(mEditText.getText().toString());
                            usersRef.document(userUid).set(dogOwnerElement);
                            finish();
                            Intent intent_profile = new Intent(StatusActivity.this, UserProfileActivity.class);
                            startActivity(intent_profile);
                        }
                    }
                });
            }
        });
    }
}
