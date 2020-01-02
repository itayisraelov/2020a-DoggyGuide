package com.technion.doggyguide.profile;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_activity extends AppCompatActivity {
    TextView user_name_tv, dog_breed_tv, name_of_the_dog_tv, name_of_the_organization_tv
            , status_tv;
    Button change_image_bt, change_status_bt;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth users = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection("dog owners");
    String userUid = users.getCurrentUser().getUid();
    CircleImageView mCircleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        init_text_view_and_buttons();
        readFromDataBase();
    }

    private void init_text_view_and_buttons() {
        user_name_tv = findViewById(R.id.user_name_id);
        dog_breed_tv = findViewById(R.id.dog_breed_id);
        name_of_the_dog_tv = findViewById(R.id.name_of_the_dog_id);
        name_of_the_organization_tv = findViewById(R.id.name_of_the_organization_id);
        status_tv = findViewById(R.id.status_id);
        mCircleImageView = findViewById(R.id.image_id);

        change_image_bt = findViewById(R.id.change_image_id);
        change_image_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        change_status_bt = findViewById(R.id.change_status_id);
        change_status_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_status = new Intent(profile_activity.this, StatusActivity.class);
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
                    user_name_tv.setText("User name:   " + dogOwnerElement.getName());
                    dog_breed_tv.setText("Dog food:   " + dogOwnerElement.getDog_breed());
                    name_of_the_dog_tv.setText("Name of the dog:   " + dogOwnerElement.getDog_name());
                    name_of_the_organization_tv.setText("Organization id:   " + dogOwnerElement.getOrg_ID());
                    status_tv.setText("Status is:   " + dogOwnerElement.getmStatus());
                }
            }
        });
    }
}
