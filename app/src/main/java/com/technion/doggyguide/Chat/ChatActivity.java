package com.technion.doggyguide.Chat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private String mUserStatus;
    private String mUserImage;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("Chat");
    private CollectionReference messagesRef = db.collection("messages");
    private CollectionReference usersRef = db.collection("dogOwners");

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

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();


        chatRef.document(mCurrentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!= null){
                    chatRef.document(mCurrentUserId)
                            .collection("friends")
                            .document(mChatUser).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (!doc.exists()) {
                                        Map chatAddMap = new HashMap();
                                        chatAddMap.put("seen", false);
                                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                                        Map chatUserMap = new HashMap();
                                        chatUserMap.put("Chat/" + mCurrentUserId + "/friends/" + mChatUser, chatAddMap);
                                        chatUserMap.put("Chat/" + mChatUser + "/friends/" + mCurrentUserId, chatAddMap);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void setInformationForToolBar() {
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


//    private void sendMessage() {
//        String message = mChatMessageView.getText().toString();
//        if(!TextUtils.isEmpty(message)){
//
//            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
//            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;
//
//            DatabaseReference user_message_push = chatRef.child("messages")
//                    .child(mCurrentUserId).child(mChatUser).push();
//
//            String push_id = user_message_push.getKey();
//
//            Map messageMap = new HashMap();
//            messageMap.put("message", message);
//            messageMap.put("seen", false);
//            messageMap.put("type", "text");
//            messageMap.put("time", ServerValue.TIMESTAMP);
//            messageMap.put("from", mCurrentUserId);
//
//            Map messageUserMap = new HashMap();
//            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
//            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
//
//            mChatMessageView.setText("");
//
//            chatRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
//            chatRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);
//
//            chatRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
//            chatRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);
//
//            chatRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                    if(databaseError != null){
//
//                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
//
//                    }
//                }
//            });
//        }
//    }
}

