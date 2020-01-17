package com.technion.doggyguide.users;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
<<<<<<< HEAD
=======
import com.technion.doggyguide.friends.Friends;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
>>>>>>> itay_new_branch_sprint_2

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfile extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button mFriendReqBtn, mDeclineReqBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String clickedUserUid;
    String mDogOwners = "dogOwners";
    FirebaseAuth users = FirebaseAuth.getInstance();
    private CircleImageView mImage;
    private TextView mName, mStatus, mDogName, mfriendCount;
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
                        mFriendReqBtn.setText("Send Friend Request");
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
                        mFriendReqBtn.setText("Send Friend Request");
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
        clickedUserUid = getIntent().getStringExtra("user_id");
        mCurrent_state = "not_friends";
        mProgressDialog = new ProgressDialog(this);
        mName = findViewById(R.id.user_name_id_);
        mStatus = findViewById(R.id.status_id_);
        mDogName = findViewById(R.id.name_of_the_dog_id_);
        mfriendCount = findViewById(R.id.total_friends_);
        mFriendReqBtn = findViewById(R.id.friend_request);
        mDeclineReqBtn = findViewById(R.id.decline_friend_request);
        mImage = findViewById(R.id.user_image_id_);

        // Don't show the cancel button
        mDeclineReqBtn.setVisibility(View.INVISIBLE);
        mDeclineReqBtn.setEnabled(false);

        mProgressDialog.setTitle("Uploading data from users...");
        mProgressDialog.setMessage("Please wait while we upload and process the data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

<<<<<<< HEAD
=======
    private void acceptFriends() {
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        DocumentReference docRef = usersRef.document(clickedUserUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> req_1 = new HashMap<>();
                Friends friend_clicked = documentSnapshot.toObject(Friends.class);
                if (friend_clicked != null){
                    req_1.put("date", currentDate);
                    req_1.put("mName", friend_clicked.getmName());
                    req_1.put("mImageUrl", friend_clicked.getmImageUrl());
                    req_1.put("mStatus", friend_clicked.getmStatus());
//                    req_1.put("online", friend_clicked.getOnline());
                    mFriendsCollection
                            .document(mCurrentUserUid)
                            .collection("friends")
                            .document(clickedUserUid).set(req_1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DocumentReference docRef = usersRef.document(mCurrentUserUid);
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Map<String, Object> req_2 = new HashMap<>();
                                            Friends friend_current = documentSnapshot.toObject(Friends.class);
                                            if (friend_current != null){
                                                req_2.put("date", currentDate);
                                                req_2.put("mName", friend_current.getmName());
                                                req_2.put("mImageUrl", friend_current.getmImageUrl());
                                                req_2.put("mStatus", friend_current.getmStatus());
//                                                req_2.put("online", friend_current.getOnline());
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
                                        }
                                    });
                                }
                            });
                }
            }
        });

    }

>>>>>>> itay_new_branch_sprint_2
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
                                    mFriendReqBtn.setText("Accept Friend Request");

                                    // Don't show the cancel button
                                    mDeclineReqBtn.setVisibility(View.VISIBLE);
                                    mDeclineReqBtn.setEnabled(true);
                                } else if (req_type.equals("sent")) {
                                    mCurrent_state = "req_sent";
                                    mFriendReqBtn.setText("Cancel Friend Request");

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
                                                    mFriendReqBtn.setText("UnFriend This Person");

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
                        mFriendReqBtn.setText(text);

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
                                                            mFriendReqBtn.setText("Cancel Friend Request");

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
                    mName.setText("User name:   " + dogOwnerElement.getmName());
                    mDogName.setText("Name of the dog:   " + dogOwnerElement.getmDogName());
                    mStatus.setText("Status is:   " + dogOwnerElement.getmStatus());
                    Picasso.get().load(dogOwnerElement.getmImageUrl()).into(mImage);
                    mProgressDialog.dismiss();

                }
            }
        });
    }
}

