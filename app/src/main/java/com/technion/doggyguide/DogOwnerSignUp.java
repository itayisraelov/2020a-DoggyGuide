package com.technion.doggyguide;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.technion.doggyguide.dataElements.DogOwnerElement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import com.theartofdev.edmodo.cropper.CropImage;
import id.zelory.compressor.Compressor;
import me.drakeet.materialdialog.MaterialDialog;


public class DogOwnerSignUp extends AppCompatActivity {

    public final String TAG = "user SignUp Activity";
    private final int PICK_IMAGE_REQUEST = 101;
    private final int CAPTURE_IMAGE_REQUEST = 102;
    private final int REQUEST_CODE_WRITE_STORAGE = 1;
    private final String ORG_DOC_ID = "euHHrQzHbBKNZsvrmpbT";
    private final String MEMBERS_DOC_ID = "reference_to_members";
    byte[] data_;
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
    private ProgressDialog mProgressDialog;

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
        mProgressDialog = new ProgressDialog(this);

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
        dogownersRef = db.collection("dogOwners");
        orgmembersRef = db.collection("organizations").document(ORG_DOC_ID).collection(MEMBERS_DOC_ID);


        profileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantingPermission();
                openFileChooser();
            }
        });
        pickAnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantingPermission();
                openFileChooser();
            }
        });

        sigupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(DogOwnerSignUp.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mImageUri != null)
                    uploadFile();
                signUpbtnHandler(v);
            }
        });

    }

    private void grantingPermission() {
        int hasWriteStoragePermission = 0;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hasWriteStoragePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_WRITE_STORAGE);
            }
            return;
        }
    }

    private void openFileChooser() {
        final ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Take Photo");
        arrayAdapter.add("Select Gallery");
        ListView listView = new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (8 * scale + 0.5f);
        listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
        listView.setDividerHeight(0);
        listView.setAdapter(arrayAdapter);

        final MaterialDialog alert = new MaterialDialog(this).setContentView(listView);

        alert.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    alert.dismiss();
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePicture, CAPTURE_IMAGE_REQUEST);
                    }
                } else {
                    alert.dismiss();
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), PICK_IMAGE_REQUEST);
                }
            }
        });
        alert.show();
    }


    private void uploadFile() {
        String fileExtention = mImageUri.toString().substring(mImageUri.toString().lastIndexOf("."));
        StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + fileExtention);
        data_ = imageCompress(new File(mImageUri.getPath()));
        mUploadTask = fileRef.putBytes(data_);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
            profileImgView.setImageURI(mImageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(DogOwnerSignUp.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                mImageUri = result.getUri();
                mProgressDialog.dismiss();
            }
        }

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(DogOwnerSignUp.this.getContentResolver(),
                    bmp, "Title", null);
            mImageUri = Uri.parse(path);
            profileImgView.setImageURI(mImageUri);
        }
    }

    private byte[] imageCompress(File actualImage) {
        byte[] data_;
        Bitmap compressedImageBitmap = null;
        try {
            compressedImageBitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(actualImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data_ = baos.toByteArray();
        return data_;
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

        mProgressDialog.setTitle("SignUp");
        mProgressDialog.setMessage("Please wait until we can register you");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        signUpWithEmailAndPassword(email, pwd, view);
    }

    private void signUpWithEmailAndPassword(String email, String pwd, final View view) {
        mAuth.createUserWithEmailAndPassword(email, pwd).
                addOnCompleteListener(DogOwnerSignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserToDatabase();
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

    private void addUserToDatabase() {
        final String userID = mAuth.getCurrentUser().getUid();
        mUploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        Log.d(TAG, "Uploaded Successfully!");
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                prog_bar.setProgress(0);
                                            }
                                        }, 500);
                                        FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                                        String mDeviceToken = instanceIdResult.getToken();
                                                        DogOwnerElement dogowner = new DogOwnerElement(nametxt.getText().toString(),
                                                                emailtxt.getText().toString(), dog_nametxt.getText().toString(),
                                                                dog_breedtxt.getText().toString(),
                                                                uri.toString(),
                                                                "I am new in the system", Arrays.asList(mDeviceToken));
                                                        dogownersRef.document(userID).set(dogowner);
                                                    }
                                                });

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
        member.put("reference", "dogOwners/" + userID);
        orgmembersRef.add(member);
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