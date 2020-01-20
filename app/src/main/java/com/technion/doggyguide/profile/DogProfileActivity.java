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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;
import com.theartofdev.edmodo.cropper.CropImage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import me.drakeet.materialdialog.MaterialDialog;

public class DogProfileActivity extends AppCompatActivity
        implements AboutChangeDialog.AboutChangeDialogListener{
    private final int PICK_IMAGE_REQUEST = 101;
    private final int CAPTURE_IMAGE_REQUEST = 102;
    private final int REQUEST_CODE_WRITE_STORAGE = 1;
    TextView user_name_tv, name_of_the_dog_tv, status_tv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth users = FirebaseAuth.getInstance();
    String mDogOwners = "dogOwners";
    String userUid = users.getCurrentUser().getUid();
    CircleImageView mCircleImageView;
    private CollectionReference usersRef = db.collection(mDogOwners);
    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;
    byte[] data_;
    private Uri mImageUri;
    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);
        getSupportActionBar().setTitle("Dog's Profile");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);


        init_text_view_and_buttons();
        readFromDataBase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile_menu, menu);
        return true;
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


    private void init_text_view_and_buttons() {
        user_name_tv = findViewById(R.id.user_name_id);
        name_of_the_dog_tv = findViewById(R.id.name_of_the_dog_id);
        status_tv = findViewById(R.id.status_id);
        mCircleImageView = findViewById(R.id.user_image_id);


        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantingPermission();
                openFileChooser();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
            mCircleImageView.setImageURI(mImageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(DogProfileActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                mImageUri = result.getUri();
                mProgressDialog.dismiss();
                uploadFile();
            }
        }

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(DogProfileActivity.this.getContentResolver(),
                    bmp, "Title", null);
            mImageUri = Uri.parse(path);
            mCircleImageView.setImageURI(mImageUri);
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

    private void uploadFile() {
        String fileExtention = mImageUri.toString().substring(mImageUri.toString().lastIndexOf("."));
        StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + fileExtention);
        data_ = imageCompress(new File(mImageUri.getPath()));
        mUploadTask = fileRef.putBytes(data_);
        mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                usersRef.document(userUid)
                                        .update("mDogImageUrl", uri.toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("User Profile", "Image updated");
                                            }
                                        });
                            }
                        });
                    }
                }
            }
        });
    }

    public void changeAbout(View view) {
        AboutChangeDialog aboutChangeDialog = new AboutChangeDialog();
        aboutChangeDialog.show(getSupportFragmentManager(), "Change About Dialog");
    }

    private void readFromDataBase() {
        DocumentReference docRef = usersRef.document(userUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DogOwnerElement dogOwnerElement = documentSnapshot.toObject(DogOwnerElement.class);
                if (dogOwnerElement != null) {
                    user_name_tv.setText(dogOwnerElement.getmDogName());
                    name_of_the_dog_tv.setText(dogOwnerElement.getmName());
                    status_tv.setText(dogOwnerElement.getmAboutDog());
                    if (dogOwnerElement.getmDogImageUrl() != null)
                        Picasso.get().load(dogOwnerElement.getmDogImageUrl()).into(mCircleImageView);
                }
            }
        });
    }

    @Override
    public void applyChange(String status) {
        status_tv.setText(status);
        usersRef.document(userUid).update("mAboutDog", status);
    }
}
