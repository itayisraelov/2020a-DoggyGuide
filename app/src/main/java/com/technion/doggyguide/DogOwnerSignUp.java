package com.technion.doggyguide;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.technion.doggyguide.dataElements.DogOwnerElement;

import java.util.HashMap;
import java.util.Map;


public class DogOwnerSignUp extends AppCompatActivity {

    public final String TAG = "user SignUp Activity";
    private final int PICK_IMAGE_REQUEST = 1;
    private final String ORG_DOC_ID = "euHHrQzHbBKNZsvrmpbT";
    private final String MEMBERS_DOC_ID = "reference_to_members";
    private ImageView profileImgView;
    private TextView pickAnImg;
    private EditText nametxt;
    private EditText emailtxt;
    private EditText pwdtxt;
    private EditText pwdconfirmtxt;
    private EditText dog_nametxt;
    private EditText dog_breedtxt;
    private Button sigupbtn;
    private ProgressBar prog_bar;

    private Uri mImageUri;

    private StorageTask mUploadTask;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;

    private CollectionReference dogownersRef;
    private CollectionReference orgmembersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_owner_sign_up);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize texts
        profileImgView = findViewById(R.id.profile_image);
        pickAnImg = findViewById(R.id.img_picker);
        nametxt = findViewById(R.id.dogownername);
        emailtxt = findViewById(R.id.dogowneremail);
        pwdtxt = findViewById(R.id.dogownerpassword);
        pwdconfirmtxt = findViewById(R.id.dogownerpasswordconfirmation);
        dog_nametxt = findViewById(R.id.dogname);
        dog_breedtxt = findViewById(R.id.dogbreed);
        sigupbtn = findViewById(R.id.createaccountdogowner);
        prog_bar = findViewById(R.id.progress_bar);


        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Initialize collections references
        dogownersRef = db.collection("dog owners");
        orgmembersRef = db.collection("organizations/" + ORG_DOC_ID + "/members");


        profileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(v);
                //uploadFile(v);
            }
        });
        pickAnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(v);
                //uploadFile(v);
            }
        });

        sigupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(DogOwnerSignUp.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mImageUri != null)
                    uploadFile(v);
                signUpbtnHandler(v);
            }
        });

    }

    private void openFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadFile(View view) {
        StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + "."
                + getFileExtension(mImageUri));
        mUploadTask = fileRef.putFile(mImageUri);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            profileImgView.setImageURI(mImageUri);
        }
    }

    private void signUpbtnHandler(View view) {
        String email = emailtxt.getText().toString();
        String pwd = pwdtxt.getText().toString();
        String pwdconfirm = pwdconfirmtxt.getText().toString();
        if (!validateSignup(email, pwd, pwdconfirm))
            return;
        if (mImageUri == null) {
            Toast.makeText(DogOwnerSignUp.this,
                    "Please upload a profile pic", Toast.LENGTH_SHORT).show();
            return;
        }
//        TODO: add this piece of code to sprint 2
//        if(organizationExists(org_emailtxt.getText().toString())) {
//            signUpWithEmailAndPassword(email, pwd);
//        } else {
//            Toast.makeText(DogOwnerSignUp.this,
//                    "Such Organization does not exist.",
//                    Toast.LENGTH_LONG).show();
//        }
        signUpWithEmailAndPassword(email, pwd, view);

    }

    private void signUpWithEmailAndPassword(String email, String pwd, final View view) {
        mAuth.createUserWithEmailAndPassword(email, pwd).
                addOnCompleteListener(DogOwnerSignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserToDatabase(view);
                            mAuth.getCurrentUser().sendEmailVerification().
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(DogOwnerSignUp.this,
                                                        "Please check your email for verification.",
                                                        Toast.LENGTH_LONG).show();
                                                //TODO: insert the user id to the organizations' database
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

    private void addUserToDatabase(View view) {
        final String userID = mAuth.getCurrentUser().getUid();
        mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "Uploaded Successfully!");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        prog_bar.setProgress(0);
                                    }
                                }, 500);
                                DogOwnerElement dogowner = new DogOwnerElement(nametxt.getText().toString(),
                                        emailtxt.getText().toString(), dog_nametxt.getText().toString(),
                                        dog_breedtxt.getText().toString(),
                                        uri.toString());
                                dogownersRef.document(userID).set(dogowner);
                            }
                        });
                    }
                }

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                        taskSnapshot.getTotalByteCount());
                prog_bar.setProgress((int) progress);
            }
        });

        //adding a reference to organizations database
        Map<String, Object> member = new HashMap<>();
        member.put(userID, "dog owners/" + userID);
        orgmembersRef.document(MEMBERS_DOC_ID)
                .set(member)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Member reference document successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing member reference document", e);
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


    //TODO: add this piece of code to sprint 2
//    private boolean organizationExists(String org_ID) {
//        if (db.collection("organizations")
//                .whereEqualTo("org_ID", org_ID)
//                .get().isSuccessful())
//            return true;
//        return false;
//    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
