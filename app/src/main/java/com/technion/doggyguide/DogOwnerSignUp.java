package com.technion.doggyguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class DogOwnerSignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_owner_sign_up);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpbtnHandler(View view) {
        EditText email = findViewById(R.id.dogowneremail);
        EditText pwd = findViewById(R.id.dogownerpassword);
        EditText pwdconfirm = findViewById(R.id.dogownerpasswordconfirmation);
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            return;
        } else if (pwd.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return;
        } else if (pwdconfirm.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a password confirmation.", Toast.LENGTH_SHORT).show();
            return;
        } else if (!pwd.getText().toString().equals(pwdconfirm.getText().toString())) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), pwd.getText().toString()).
                addOnCompleteListener(DogOwnerSignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign up succeded
                            FirebaseAuth.getInstance().signOut();
                            finish();
                            //TODO: insert the user id to the organizations' database
                            Intent intent = new Intent(DogOwnerSignUp.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DogOwnerSignUp.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception", e.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
