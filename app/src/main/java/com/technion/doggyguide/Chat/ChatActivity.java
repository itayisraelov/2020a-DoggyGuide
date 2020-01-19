package com.technion.doggyguide.Chat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {
    private TextView mTitleView;
    private TextView mStatusView;
    private CircleImageView mProfileImage;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private String mChatUser;
    private String mChatUserId;
    private String mUserStatus;
    private String mUserImage;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("Chat");
    private CollectionReference messagesRef = db.collection("messages");
    private CollectionReference usersRef = db.collection("dogOwners");
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initToolBar();

        // ---- Custom Action bar Items ----
        mTitleView = findViewById(R.id.custom_bar_title);
        mStatusView = findViewById(R.id.custom_bar_status);
        mProfileImage = findViewById(R.id.custom_bar_image);

        setInformationForToolBar();

        mChatSendBtn = findViewById(R.id.chat_send_btn);
        mChatMessageView = findViewById(R.id.chat_message_view);
        mChatAddBtn = findViewById(R.id.chat_add_btn);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        initChatForThisUser();

        loadMessages();

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

    }

    private void loadMessages() {

    }

    private void initChatForThisUser() {
        chatRef.document(mCurrentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final Date date = new Date();
                if(documentSnapshot != null){
                    chatRef.document(mCurrentUserId)
                            .collection("friends")
                            .document(mChatUserId).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (!document.exists()) {
                                    Map<String, Object> chatAddMap = new HashMap<>();
                                    chatAddMap.put("seen", false);
                                    chatAddMap.put("date", date);
                                    chatRef.document(mCurrentUserId)
                                            .collection("friends")
                                            .document(mChatUserId)
                                            .set(chatAddMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Map<String, Object> chatAddMap = new HashMap<>();
                                            chatAddMap.put("seen", false);
                                            chatAddMap.put("date", date);
                                            chatRef.document(mChatUserId)
                                                    .collection("friends")
                                                    .document(mCurrentUserId )
                                                    .set(chatAddMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {}
                                            });
                                        }
                                    });
                                } else { }
                            } else { }
                        }
                    });
                }
            }
        });
    }

    private void setInformationForToolBar() {
        mChatUserId = getIntent().getStringExtra("user_id");
        mChatUser  = getIntent().getStringExtra("user_name");
        mTitleView.setText(mChatUser );
        mUserStatus = getIntent().getStringExtra("user_status");
        mStatusView.setText(mUserStatus);
        mUserImage = getIntent().getStringExtra("user_image");
        Picasso.get().load(mUserImage).into(mProfileImage);
    }

    private void initToolBar() {
        Toolbar mChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void sendMessage(){
        String message = mChatMessageView.getText().toString();
        if(!TextUtils.isEmpty(message)){
            final Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", new Date());
            messageMap.put("from", mCurrentUserId);

            messagesRef.document(mCurrentUserId)
                    .collection("friends")
                    .document(mChatUserId).
                    collection("messages")
                    .document().set(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    messagesRef.document(mChatUserId )
                            .collection("friends")
                            .document(mCurrentUserId).
                            collection("messages")
                            .document().set(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mChatMessageView.setText("");
                        }
                    });
                }
            });
            chatRef.document(mCurrentUserId)
                    .collection("friends")
                    .document(mChatUserId).update("date", new Date()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    chatRef.document(mCurrentUserId)
                            .collection("friends")
                            .document(mChatUserId).update("seen", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            chatRef.document(mChatUserId )
                                    .collection("friends")
                                    .document(mCurrentUserId).update("date", new Date()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    chatRef.document(mChatUserId )
                                            .collection("friends")
                                            .document(mCurrentUserId).update("seen", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }


}

