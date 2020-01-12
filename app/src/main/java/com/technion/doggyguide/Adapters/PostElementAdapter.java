package com.technion.doggyguide.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.EventElement;
import com.technion.doggyguide.dataElements.PostElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostElementAdapter extends FirestoreRecyclerAdapter<PostElement, PostElementAdapter.PostHolder> {

    private final String TAG = "Post Adapter";
    String mDogOwners = "dogOwners";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public PostElementAdapter(@NonNull FirestoreRecyclerOptions<PostElement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostHolder holder, final int position, @NonNull final PostElement model) {
        db.document(mDogOwners + "/" + model.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageUrl = (String) documentSnapshot.get("mImageUrl");
                        Picasso.get()
                                .load(imageUrl)
                                .fit()
                                .centerCrop()
                                .into(holder.profileImage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                });
        String[] postingDate = model.getPosting_date().split(" ");
        //holder.profileImage.setImageResource(R.drawable.ic_person);
        holder.userName.setText(model.getName());
        holder.postingTime.setText(postingDate[0] + " " + postingDate[1] + " "
                            + postingDate[2] + " " + postingDate[3]);
        holder.postDate.setText(model.getPost_date());
        holder.postTime.setText(model.getStart_time() + "-" + model.getEnd_time());
        holder.postDescription.setText(model.getDescription());

        holder.ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Ignore Button Clicked");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String userID = mAuth.getCurrentUser().getUid();
                CollectionReference posts = db.collection(mDogOwners + "/"
                        + userID + "/posts");
                Snackbar.make(v, "Post ignored!", Snackbar.LENGTH_LONG).show();
                posts.document(model.getPostId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Post ignored!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.getMessage());
                            }
                        });
            }
        });

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userID = mAuth.getCurrentUser().getUid();
                Log.d(TAG, "Accept Button Clicked");
                String mydate = holder.postDate.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "E, MMM dd, yyyy");

                Date myDate = new Date();
                try {
                    myDate = dateFormat.parse(mydate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("d-M-yyyy");
                String date = timeFormat.format(myDate);
                String start_time = holder.postTime.getText().toString().split("-")[0];
                String end_time = holder.postTime.getText().toString().split("-")[1];
                String description = holder.postDescription.getText().toString();
                String eventID = userID + Calendar.getInstance().getTime().toString();
                EventElement newEvent = new EventElement("Upcoming Event", date,
                        start_time, end_time, description, eventID);
                db.collection(mDogOwners + "/" +
                        userID + "/events by date")
                        .document(newEvent.getDate())
                        .collection("events")
                        .document(newEvent.getEventId()).set(newEvent);
                CollectionReference posts = db.collection( mDogOwners + "/"
                        + userID + "/posts");
                Snackbar.make(v, "New event has been created!", Snackbar.LENGTH_LONG).show();
                //Delete the post from all friends also...
                posts.document(model.getPostId())
                        .delete();
            }
        });
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,
                parent, false);
        return new PostHolder(v);
    }


    public class PostHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView userName;
        private TextView postingTime;
        private TextView postDate;
        private TextView postTime;
        private TextView postDescription;
        private TextView acceptButton;
        private TextView ignoreButton;

        public PostHolder(final View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.post_image);
            userName = itemView.findViewById(R.id.post_name);
            postingTime = itemView.findViewById(R.id.posted_date);
            postDate = itemView.findViewById(R.id.post_date);
            postTime = itemView.findViewById(R.id.post_time);
            postDescription = itemView.findViewById(R.id.post_description);
            acceptButton = itemView.findViewById(R.id.post_accept);
            ignoreButton = itemView.findViewById(R.id.post_ignore);
        }

    }
}
