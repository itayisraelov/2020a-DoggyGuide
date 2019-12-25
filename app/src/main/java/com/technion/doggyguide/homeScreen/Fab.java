package com.technion.doggyguide.homeScreen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.DatePickerFabFragment;
import com.technion.doggyguide.R;
import com.technion.doggyguide.TimePickerFabFragment;
import com.technion.doggyguide.dataElements.PostElement;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fab extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private final String TAG = "FAB POST";

    private int clicked_btn_id;

    private EditText postname = findViewById(R.id.post_name);
    private TextView postdate;
    private TextView poststarttime;
    private TextView postendtime;
    private EditText postdescription = findViewById(R.id.post_description);


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userID = mAuth.getCurrentUser().getUid();

    private CollectionReference postsRef = db.collection("posts");
    private CollectionReference dogownersRef = db.collection("dog owners");
    private CollectionReference friendsRef = db.collection("dog owners/" + userID + "/friends");
    private CollectionReference userpostsRef = db.collection("dog owners/" + userID + "/posts");

    //start and end time for the post event
    private String start_time;
    private String end_time;
    private String post_time;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        getSupportActionBar().setTitle("Post a request");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void datePickerHandler(View view) {
        clicked_btn_id = R.id.post_datepicker;
        DialogFragment datepicker = new DatePickerFabFragment();
        datepicker.show(getSupportFragmentManager(), "Date Picker");
    }

    public void startTimeHandler(View view) {
        clicked_btn_id = R.id.post_starttimepicker;
        DialogFragment starttimepicker = new TimePickerFabFragment();
        starttimepicker.show(getSupportFragmentManager(), "start time picker");
    }

    public void endTimeHandler(View view) {
        clicked_btn_id = R.id.post_endtimepicker;
        DialogFragment endtimepicker = new TimePickerFabFragment();
        endtimepicker.show(getSupportFragmentManager(), "end time picker");
    }

    public void postbtnHandler(View view) {
        //TODO: create a post and post it to all friends feed
        post_time = Calendar.getInstance().getTime().toString();
        final String postID = userID + post_time;
        addPostToDatabase(postID);
        addPostRefToUser(postID);
        addPostRefToFriends(postID);


        //add reference to the post to my friends post

    }


    private void addPostToDatabase(String postID) {
        String name = postname.getText().toString();
        String description = postdescription.getText().toString();
        PostElement post = new PostElement(name, start_time, end_time, post_time, description);
        postsRef.document(postID).set(post);
    }

    private void addPostRefToUser(String postID) {
        Map<String, Object> post_ref = new HashMap<>();
        post_ref.put(postID,"posts/" + postID);
        userpostsRef.document(postID)
                .set(post_ref)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Dog owner document successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing dog owner document", e);
                    }
                });
    }

    private void addPostRefToFriends(final String postID) {
        friendsRef.document(userID + "-friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                for(String friendID : document.getData().keySet())
                                    dogownersRef.document(friendID)
                                            .collection("posts")
                                            .document(postID)
                                            .set("posts/" + postID);
                            } else {
                                Log.d(TAG, "No such document");

                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());

                        }
                    }
                });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String pickeddate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        postdate =  findViewById(R.id.post_date);
        postdate.setText(pickeddate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        switch (clicked_btn_id){
            case R.id.post_starttimepicker:
                poststarttime = findViewById(R.id.post_starttime);
                poststarttime.setText("Starts at " + hourOfDay + ":" + minute);
                start_time = hourOfDay + ":" + minute;
                break;
            case R.id.post_endtimepicker:
                postendtime = findViewById(R.id.post_endtime);
                postendtime.setText("Ends at " + hourOfDay + ":" + minute);
                end_time = hourOfDay + ":" + minute;
                break;
        }
    }
}
