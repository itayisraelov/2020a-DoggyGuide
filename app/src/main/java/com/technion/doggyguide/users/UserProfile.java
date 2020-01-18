package com.technion.doggyguide.users;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfile extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageView mFriendReqBtn, mDeclineReqBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String clickedUserUid;
    String mDogOwners = "dogOwners";
    FirebaseAuth users = FirebaseAuth.getInstance();
    private CircleImageView mImage;
    private TextView mName, mStatus, mDogName, mFriendshipStatus;
    private CollectionReference usersRef = db.collection(mDogOwners);
    private ProgressDialog mProgressDialog;
    private String mCurrent_state;
    private String mCurrentUserUid = users.getCurrentUser().getUid();
    private CollectionReference mFriendReqCollection = db.collection("Friend_req");
    private CollectionReference mFriendsCollection = db.collection("Friends");
    private CollectionReference mNotificationsCollection = db.collection("notifications");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        clickedUserUid = getIntent().getStringExtra("user_id");
        usersRef.document(clickedUserUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        getSupportActionBar().setTitle(documentSnapshot.get("mName") + "'s Profile");
                    }
                });

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initSomeFields();
        readFromDataBase();
        requestFeature();

        mFriendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendReqBtn.setEnabled(false);
                // ----------not_friends -------------------------
                if (mCurrent_state.equals("not_friends")) {
                    notFriends();
                }
                // ----------req_sent -------------------------
                if (mCurrent_state.equals("req_sent")) {
                    reqSentAndNeedToCancel("not_friends", "Send Friend request");
                }
                // ----------req_received -------------------------
                if (mCurrent_state.equals("req_received")) {
                    acceptFriends();
                }
                // ----------unFriend -------------------------
                if (mCurrent_state.equals("friends")) {
                    cancelFriends();
                }
            }
        });
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

    private void acceptFriends() {
        /*---Inserting to dogOwners/userId1/firends/userId2---*/
        DocumentReference mNewFriend = db.document("dogOwners/" + clickedUserUid);
        Map<String, DocumentReference> data = new HashMap<>();
        data.put("reference", mNewFriend);
        CollectionReference myFriends = db.collection("dogOwners/" + mCurrentUserUid + "/friends");
        myFriends.document(clickedUserUid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reqSentAndNeedToCancel("friends", "UnFriend This Person");
                    }
                });

        /*---Itay implementation---*/
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        Map<String, Object> req_1 = new HashMap<>();
        req_1.put("date", currentDate);
        mFriendsCollection
                .document(mCurrentUserUid)
                .collection("c")
                .document(clickedUserUid).set(req_1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Object> req_2 = new HashMap<>();
                        req_2.put("date", currentDate);
                        mFriendsCollection
                                .document(clickedUserUid )
                                .collection("friends")
                                .document(mCurrentUserUid).set(req_2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reqSentAndNeedToCancel("friends", "UnFriend This Person");
                                    }
                                });
                    }
                });
    }

    private void cancelFriends() {
        /*---Removing from dogOwners/userId1/firends/userId2---*/
        DocumentReference mCancelFriend = db.document("dogOwners/" + mCurrentUserUid + "/friends/" + clickedUserUid);
        mCancelFriend.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mCurrent_state = "not_friends";
                        mFriendReqBtn.setImageResource(R.drawable.ic_person_send_request);
                        mFriendshipStatus.setText("Send Friend Request");
                        mFriendReqBtn.setEnabled(true);
                        // Don't show the cancel button
                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                        mDeclineReqBtn.setEnabled(false);
                    }
                });

        /*---Itay implementation---*/
        mFriendsCollection
                .document(mCurrentUserUid)
                .collection("friends")
                .document(clickedUserUid)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendsCollection
                        .document(clickedUserUid)
                        .collection("friends")
                        .document(mCurrentUserUid)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mCurrent_state = "not_friends";
                        mFriendReqBtn.setImageResource(R.drawable.ic_person_send_request);
                        mFriendshipStatus.setText("Send Friend Request");
                        mFriendReqBtn.setEnabled(true);
                        // Don't show the cancel button
                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                        mDeclineReqBtn.setEnabled(false);
                    }
                });
            }
        });
    }

    private void initSomeFields() {
        mCurrent_state = "not_friends";
        mProgressDialog = new ProgressDialog(this);
        mName = findViewById(R.id.user_name_id);
        mStatus = findViewById(R.id.status_id);
        mDogName = findViewById(R.id.dog_name);
        mFriendshipStatus = findViewById(R.id.friendship_status);
        mFriendReqBtn = findViewById(R.id.friend_request);
        mDeclineReqBtn = findViewById(R.id.decline_friend_request);
        mImage = findViewById(R.id.user_image_id);
        // Don't show the cancel button
        mDeclineReqBtn.setVisibility(View.INVISIBLE);
        mDeclineReqBtn.setEnabled(false);

        mProgressDialog.setTitle("Uploading data from users...");
        mProgressDialog.setMessage("Please wait while we upload and process the data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void requestFeature() {
        mFriendReqCollection
                .document(mCurrentUserUid)
                .collection("friends")
                .document(clickedUserUid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.getData() != null) {
                            boolean res = documentSnapshot.getData().containsKey("request_type");
                            if (res) {
                                String req_type = documentSnapshot.get("request_type").toString();
                                if (req_type.equals("received")) {
                                    mCurrent_state = "req_received";
                                    //mFriendReqBtn.setText("Accept Friend Request");

                                    // Don't show the cancel button
                                    mDeclineReqBtn.setVisibility(View.VISIBLE);
                                    mDeclineReqBtn.setEnabled(true);
                                } else if (req_type.equals("sent")) {
                                    mCurrent_state = "req_sent";
                                    mFriendReqBtn.setImageResource(R.drawable.ic_person_request_sent);

                                    // Don't show the cancel button
                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mDeclineReqBtn.setEnabled(false);
                                }
                                mProgressDialog.dismiss();
                            }
                        } else {
                            mFriendsCollection
                                    .document(mCurrentUserUid)
                                    .collection("friends")
                                    .document(clickedUserUid)
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            if (documentSnapshot.getData() != null) {
                                                if (documentSnapshot.getData().containsKey("date")) {
                                                    mCurrent_state = "friends";
                                                    mFriendReqBtn.setImageResource(R.drawable.ic_person_friends);

                                                    // Don't show the cancel button
                                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                    mDeclineReqBtn.setEnabled(false);
                                                }
                                            }
                                            mProgressDialog.dismiss();
                                        }
                                    });
                        }
                    }
                });
    }


    private void reqSentAndNeedToCancel(final String status, final String text) {
        mFriendReqCollection
                .document(mCurrentUserUid)
                .collection("friends")
                .document(clickedUserUid)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendReqCollection
                        .document(clickedUserUid)
                        .collection("friends")
                        .document(mCurrentUserUid)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendReqBtn.setEnabled(true);
                        mCurrent_state = status;
                        switch (text) {
                            case "Send Friend request":
                                mFriendReqBtn.setImageResource(R.drawable.ic_person_send_request);
                                mFriendshipStatus.setText(text);
                                break;
                            default:
                                mFriendReqBtn.setImageResource(R.drawable.ic_person_friends);
                                mFriendshipStatus.setText(text);
                                break;
                        }

                        // Don't show the cancel button
                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                        mDeclineReqBtn.setEnabled(false);
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
                .document(clickedUserUid).set(req_1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> req_2 = new HashMap<>();
                            req_2.put("request_type", "received");
                            mFriendReqCollection
                                    .document(clickedUserUid)
                                    .collection("friends")
                                    .document(mCurrentUserUid).set(req_2)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            final Map<String, Object> notification = new HashMap<>();
                                            notification.put("from", mCurrentUserUid);
                                            notification.put("type", "Friend request");
                                            notification.put("receiverId", clickedUserUid);
                                            notification.put("text", "Someone sent you a request. Tap to see who");
                                            mNotificationsCollection
                                                    .document(clickedUserUid)
                                                    .collection("keysNotifications")
                                                    .document()
                                                    .set(notification)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mCurrent_state = "req_sent";
                                                            mFriendReqBtn.setImageResource(R.drawable.ic_person_request_sent);
                                                            mFriendshipStatus.setText("Cancel Friend Request");
                                                            // Don't show the cancel button
                                                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                            mDeclineReqBtn.setEnabled(false);
                                                        }
                                                    });


                                            Toast.makeText(UserProfile.this,
                                                    "request sending successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(UserProfile.this,
                                    "Failed sending request", Toast.LENGTH_SHORT).show();
                        }
                        mFriendReqBtn.setEnabled(true);
                    }
                });
    }

    private void readFromDataBase() {
        final DocumentReference docRef = usersRef.document(clickedUserUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DogOwnerElement dogOwnerElement = documentSnapshot.toObject(DogOwnerElement.class);
                if (dogOwnerElement != null) {
                    mName.setText(dogOwnerElement.getmName());
                    mDogName.setText(dogOwnerElement.getmDogName());
                    mStatus.setText(dogOwnerElement.getmStatus());
                    Picasso.get().load(dogOwnerElement.getmImageUrl()).into(mImage);
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}

