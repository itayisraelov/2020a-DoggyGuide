package com.technion.doggyguide.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;
import com.theartofdev.edmodo.cropper.CropImage;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserProfileActivity extends AppCompatActivity {
    TextView user_name_tv, dog_breed_tv, name_of_the_dog_tv, name_of_the_organization_tv
            , status_tv;
    Button change_user_image_bt, change_status_bt;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth users = FirebaseAuth.getInstance();
    String mDogOwners = "dogOwners";
    private CollectionReference usersRef = db.collection(mDogOwners);
    String userUid = users.getCurrentUser().getUid();
    CircleImageView mCircleImageView;
    private static final int GALLERY_PICK = 1;
    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);


        init_text_view_and_buttons();
        readFromDataBase();
    }

    private void init_text_view_and_buttons() {
        user_name_tv = findViewById(R.id.user_name_id);
        dog_breed_tv = findViewById(R.id.dog_breed_id);
        name_of_the_dog_tv = findViewById(R.id.name_of_the_dog_id);
        name_of_the_organization_tv = findViewById(R.id.name_of_the_organization_id);
        status_tv = findViewById(R.id.status_id);
        mCircleImageView = findViewById(R.id.user_image_id);

        change_user_image_bt = findViewById(R.id.change_user_image_id);
        change_user_image_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        change_status_bt = findViewById(R.id.change_status_id);
        change_status_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_status = new Intent(UserProfileActivity.this, StatusActivity.class);
                startActivity(intent_status);
                finish();
            }
        });
    }

    private void readFromDataBase() {
        DocumentReference docRef = usersRef.document(userUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DogOwnerElement dogOwnerElement = documentSnapshot.toObject(DogOwnerElement.class);
                if (dogOwnerElement != null){
                    user_name_tv.setText("User name:   " + dogOwnerElement.getmName());
                    dog_breed_tv.setText("Dog food:   " + dogOwnerElement.getmDogBreed());
                    name_of_the_dog_tv.setText("Name of the dog:   " + dogOwnerElement.getmDogName());
                    name_of_the_organization_tv.setText("Organization id:   " + dogOwnerElement.getmOrgId());
                    status_tv.setText("Status is:   " + dogOwnerElement.getmStatus());
                    Picasso.get().load(dogOwnerElement.getmImageUrl()).into(mCircleImageView);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog.setTitle("Uploading image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                //compressedImage
                File filePath = new File(resultUri.getPath());
                final byte[] data_;
                data_ = compressedImage(filePath);
                final StorageReference thumbs_filepath = mStorageRef.child("uploads").child(userUid + ".jpg");

                thumbs_filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            UploadTask uploadTask = thumbs_filepath.putBytes(data_);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> compressed_task) {
                                    if(compressed_task.isSuccessful()){
                                        Toast.makeText(UserProfileActivity.this, "Working", Toast.LENGTH_SHORT).show();
                                        if(compressed_task.getResult() != null){
                                            thumbs_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    usersRef.document(userUid).update("mImageUrl",uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            readFromDataBase();
                                                            mProgressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }else{
                                        Toast.makeText(UserProfileActivity.this, "Error in compressed Image", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private byte[] compressedImage(File filePath) {
        byte[] data_;
        Bitmap compressedImageBitmap = null;
        try {
            compressedImageBitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data_ = baos.toByteArray();
        return data_;
    }

}
