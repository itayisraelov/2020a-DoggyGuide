package com.technion.doggyguide.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private CircleImageView mImage;
    private TextView mName, mStatus, mDogName, mfriendCount;
    Button mFriendReqBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userUid;
    private CollectionReference usersRef = db.collection("dog owners");
    private ProgressDialog mProgressDialog;
    private String mCurrent_state;
    FirebaseAuth users = FirebaseAuth.getInstance();
    private String mCurrentUserUid = users.getCurrentUser().getUid();
    //private DocumentReference mDog_owners = db.collection("dog owners").document(mCurrentUserUid);
    private CollectionReference mFriendReqCollection = db.collection("Friend_req");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userUid = getIntent().getStringExtra("user_id");

        mCurrent_state = "not_friends";


        mProgressDialog = new ProgressDialog(this);

        mName = findViewById(R.id.user_name_id_);
        mStatus =findViewById(R.id.status_id_);
        mDogName = findViewById(R.id.name_of_the_dog_id_);
        mfriendCount = findViewById(R.id.total_friends_);
        mFriendReqBtn = findViewById(R.id.friend_request);
        mImage = findViewById(R.id.user_image_id_);

        mProgressDialog.setTitle("Uploading data from users...");
        mProgressDialog.setMessage("Please wait while we upload and process the data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        readFromDataBase();

        mFriendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendReqBtn.setEnabled(false);


                // ----------not_friends -------------------------
                if(mCurrent_state.equals("not_friends")){
                    notFriends();
                }
                // ----------req_sent -------------------------
                if(mCurrent_state.equals("req_sent")){
                    reqSentAndNeedToCancel();
                }
            }
        });
    }

    private void reqSentAndNeedToCancel() {
        mFriendReqCollection
                .document(mCurrentUserUid)
                .collection("friends")
                .document(userUid)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendReqCollection
                        .document(userUid )
                        .collection("friends")
                        .document(mCurrentUserUid)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendReqBtn.setEnabled(true);
                        mCurrent_state = "not_friends";
                        mFriendReqBtn.setText("Friend request");
                    }
                });
            }
        });
    }

    private void notFriends() {
        Map<String, Object> req_1 = new HashMap<>();
        req_1.put("request_type", "send");
        mFriendReqCollection
                .document(mCurrentUserUid)
                .collection("friends")
                .document(userUid).set(req_1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Map<String, Object> req_2 = new HashMap<>();
                    req_2.put("request_type", "received");
                    mFriendReqCollection
                            .document(userUid)
                            .collection("friends")
                            .document( mCurrentUserUid).set(req_2)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqBtn.setEnabled(true);
                            mCurrent_state = "req_sent";
                            mFriendReqBtn.setText("Cancel Friend Request");
//                            Toast.makeText(UserProfile.this,
//                                    "request sending successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(UserProfile.this,
                            "Failed sending request", Toast.LENGTH_SHORT).show();
                }
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
                    mName.setText("User name:   " + dogOwnerElement.getName());
                    mDogName.setText("Name of the dog:   " + dogOwnerElement.getDog_name());
                    mStatus.setText("Status is:   " + dogOwnerElement.getmStatus());
                    Picasso.get().load(dogOwnerElement.getmImageUrl()).into(mImage);
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}
