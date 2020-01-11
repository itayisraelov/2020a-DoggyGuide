package com.technion.doggyguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.technion.doggyguide.dataElements.DogOwnerElement;
import com.technion.doggyguide.loginScreen.DogOwnerConnectionFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GoogleSignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference orgmembersRef;


    private final String ORG_DOC_ID = "euHHrQzHbBKNZsvrmpbT";
    private final String MEMBERS_DOC_ID = "reference_to_members";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        Intent intent = getIntent();
        final AuthCredential credential = intent.getParcelableExtra(DogOwnerConnectionFragment.EXTRA_CREDINTIAL);
        final GoogleSignInAccount account = intent.getParcelableExtra(DogOwnerConnectionFragment.EXTRA_ACCOUNT);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        orgmembersRef = db.collection("organizations").document(ORG_DOC_ID).collection(MEMBERS_DOC_ID);


        TextView mSet = findViewById(R.id.action_ok);
        TextView mCancel = findViewById(R.id.action_cancel);


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn();
            }
        });

        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn(credential, account);
            }
        });
    }

    public void cancelBtn() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void setBtn(AuthCredential credential, GoogleSignInAccount account) {
        EditText mDogName =  findViewById(R.id.input_name);
        String dogname = mDogName.getText().toString();

        EditText mDogBreed = findViewById(R.id.input_breed);
        String dogbreed = mDogBreed.getText().toString();

        if (dogname.isEmpty() || dogbreed.isEmpty()) {
            Toast.makeText(this,
                    "Please enter the name and breed of your dog", Toast.LENGTH_SHORT).show();
            return;
        }
        firstGoogleSignIn(credential, account, dogname, dogbreed);
    }

    private void firstGoogleSignIn(AuthCredential credential,
                                   final GoogleSignInAccount account, final String dogname, final String dogbreed) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            addUserToDatabase(account, dogname, dogbreed);
                            Intent intent = new Intent(GoogleSignInActivity.this, homeActivity.class);
                            finish();
                            startActivity(intent);
                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToDatabase(final GoogleSignInAccount account, final String mDogName, final String mDogBreed) {
        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String mDeviceToken = instanceIdResult.getToken();
                        DogOwnerElement dogowner = new DogOwnerElement(account.getDisplayName(),
                                account.getEmail(), mDogName, mDogBreed,
                                account.getPhotoUrl().toString(),
                                "I am new in the system", Arrays.asList(mDeviceToken));
                        String userId = mAuth.getCurrentUser().getUid();
                        db.collection("dog owners")
                                .document(userId)
                                .set(dogowner);

                        Map<String, Object> member = new HashMap<>();
                        member.put("reference", "dog owners/" + userId);
                        orgmembersRef.add(member);
                    }
                });
    }
}
