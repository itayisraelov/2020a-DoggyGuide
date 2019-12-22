package com.technion.doggyguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.dataElements.DogOwnerElement;

import java.util.HashMap;
import java.util.Map;

public class DogOwnerSignUp extends AppCompatActivity {

    public final String TAG = "User SignUp Activity";

    private FirebaseAuth mAuth;

    private EditText nametxt;
    private EditText emailtxt;
    private EditText org_emailtxt;
    private EditText pwdtxt;
    private EditText pwdconfirmtxt;
    private EditText dog_nametxt;
    private EditText dog_breedtxt;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_owner_sign_up);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize texts
        nametxt = findViewById(R.id.dogownername);
        emailtxt = findViewById(R.id.dogowneremail);
        org_emailtxt = findViewById(R.id.organization);
        pwdtxt = findViewById(R.id.dogownerpassword);
        pwdconfirmtxt = findViewById(R.id.dogownerpasswordconfirmation);
        dog_nametxt = findViewById(R.id.dogname);
        dog_breedtxt = findViewById(R.id.dogbreed);
        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


    }

    public void signUpbtnHandler(View view) {
        String email = emailtxt.getText().toString();
        String pwd = pwdtxt.getText().toString();
        String pwdconfirm = pwdconfirmtxt.getText().toString();
        if (!validateSignup(email, pwd, pwdconfirm))
            return;
        signUpWithEmailAndPassword(email, pwd);

    }

    private void signUpWithEmailAndPassword(String email, String pwd) {
        mAuth.createUserWithEmailAndPassword(email, pwd).
                addOnCompleteListener(DogOwnerSignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign up succeded
                            mAuth.getCurrentUser().sendEmailVerification().
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(DogOwnerSignUp.this,
                                                        "Please check your email for verification.",
                                                        Toast.LENGTH_LONG).show();
                                                //TODO: insert the user id to the organizations' database
                                                addUserToDatabase();
                                                mAuth.signOut();
                                                Intent intent = new Intent(DogOwnerSignUp.this, MainActivity.class);
                                                finish();
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DogOwnerSignUp.this, task.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(DogOwnerSignUp.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception", e.getMessage());
            }
        });
    }

    public void addUserToDatabase() {
        DogOwnerElement ele = new DogOwnerElement(nametxt.getText().toString(),
                emailtxt.getText().toString(), org_emailtxt.getText().toString(),
                dog_nametxt.getText().toString(), dog_breedtxt.getText().toString());

        Log.d(TAG, "We are adding data to the database");

        Map<String, Object> dogowner = new HashMap<>();

        dogowner.put("name", ele.getName());
        dogowner.put("email", ele.getEmail());
        dogowner.put("OrgID", ele.getOrg_ID());
        dogowner.put("dog's name", ele.getDog_name());
        dogowner.put("dog's breed", ele.getDog_breed());

        String userID = mAuth.getCurrentUser().getUid();

        db.collection("dog owners").document(userID)
                .set(dogowner)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    private boolean validateSignup(String email, String pwd, String pwdconfirm) {
        if (email.isEmpty()) {
            emailtxt.setError("Please enter an email address.");
            emailtxt.requestFocus();
            return false;
        }
        if (pwd.isEmpty()) {
            pwdtxt.setError("Please enter a password.");
            pwdtxt.requestFocus();
            return false;
        }
        if (pwd.isEmpty()) {
            pwdconfirmtxt.setError("Please enter a password confirmation.");
            pwdconfirmtxt.requestFocus();
            return false;
        } else if (!pwd.equals(pwdconfirm)) {
            pwdconfirmtxt.setError("The confirmation password doesn't match.");
            pwdconfirmtxt.requestFocus();
            return false;
        }
        return true;
    }

}
