package com.technion.doggyguide.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.EventElement;
import com.technion.doggyguide.dataElements.PostElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostElementAdapter extends FirestoreRecyclerAdapter<PostElement, PostElementAdapter.PostHolder> {

    private final String TAG = "Post Adapter";


    public PostElementAdapter(@NonNull FirestoreRecyclerOptions<PostElement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull PostElement model) {
        String[] postingDate = model.getPosting_date().split(" ");
        holder.profileImage.setImageResource(R.drawable.ic_person);
        holder.userName.setText(model.getName());
        holder.postingTime.setText(postingDate[0] + " " + postingDate[1] + " "
                            + postingDate[2] + " " + postingDate[3]);
        holder.postDate.setText(model.getPost_date());
        holder.postTime.setText(model.getStart_time() + "-" + model.getEnd_time());
        holder.postDescription.setText(model.getDescription());
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
        private Button acceptButton;
        private Button ignoreButton;

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

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userID = mAuth.getCurrentUser().getUid();
                    Log.d(TAG, "Accept Button Clicked");
                    String mydate = postDate.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "E, dd MMM yyyy");

                    Date myDate = null;
                    try {
                        myDate = dateFormat.parse(mydate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat timeFormat = new SimpleDateFormat("d-M-yyyy");
                    String date = timeFormat.format(myDate);
                    String start_time = postTime.getText().toString().split("-")[0];
                    String end_time = postTime.getText().toString().split("-")[1];
                    String description = postDescription.getText().toString();
                    EventElement newEvent = new EventElement("Upcoming Event", date,
                            start_time, end_time, description);
                    db.collection("dog owners/" +
                             userID + "/events by date")
                            .document(newEvent.getDate()).collection("events").add(newEvent);
                    Snackbar.make(v, "New event has been created!", Snackbar.LENGTH_LONG).show();
                }
            });

            ignoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Ignore Button Clicked");
                }
            });
        }

    }
}
