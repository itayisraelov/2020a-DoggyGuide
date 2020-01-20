package com.technion.doggyguide.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;
import java.text.SimpleDateFormat;


import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirestoreRecyclerAdapter<Messages, MessageAdapter.MessageViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    MessageAdapter(@NonNull FirestoreRecyclerOptions<Messages> options) {
        super(options);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,
                parent, false);
        return new MessageAdapter.MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        CircleImageView profileImage;
        TextView displayName;
        ImageView messageImage;
        TextView timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message_text_layout);
            profileImage = itemView.findViewById(R.id.message_profile_layout);
            displayName = itemView.findViewById(R.id.name_text_layout);
            messageImage =  itemView.findViewById(R.id.message_image_layout);
            timestamp = itemView.findViewById(R.id.time_text_layout);

        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, @NonNull Messages model) {

        final String from_user = model.getFrom();
        String message_type = model.getType();
        //time
        String pattern = "  HH:mm:ss    MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(model.getTime().toDate());
        holder.timestamp.setText(date);

        final String mDogOwners = "dogOwners";
        db.collection(mDogOwners)
                .document(from_user)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null){
                            DogOwnerElement dogOwnerElement = documentSnapshot.toObject(DogOwnerElement.class);
                            if (dogOwnerElement != null) {
                                String name = dogOwnerElement.getmName();
                                String image = dogOwnerElement.getmImageUrl();

                                holder.displayName.setText(name);
                                Picasso.get().load(image).into(holder.profileImage);
                            }
                        }
                    }
                });

        if(message_type.equals("text")) {
            holder.messageText.setText(model.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);
        } else {
            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(model.getMessage()).into(holder.messageImage);
        }
    }


}
