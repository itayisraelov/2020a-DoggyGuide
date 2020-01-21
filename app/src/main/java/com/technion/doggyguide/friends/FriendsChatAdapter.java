package com.technion.doggyguide.friends;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.Chat.ChatActivity;
import com.technion.doggyguide.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsChatAdapter extends
        FirestoreRecyclerAdapter<Friends, FriendsChatAdapter.FriendsChatViewHolder> {
    private final String TAG = "Friends Chat Adapter";

    public FriendsChatAdapter(@NonNull FirestoreRecyclerOptions<Friends> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendsChatAdapter.FriendsChatViewHolder holder,
                                    int position, @NonNull final Friends model) {
        holder.setName(model.getmName());
        holder.setUri(model.getmImageUrl());
        holder.setStatus(model.getmStatus());
        //holder.setOnline(model.getOnline());

        final String user_id = getSnapshots().getSnapshot(position).getId();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent chatIntent = new Intent(v.getContext(), ChatActivity.class);
                chatIntent.putExtra("user_id", user_id);
                chatIntent.putExtra("user_name", model.getmName());
                chatIntent.putExtra("user_image", model.getmImageUrl());
                chatIntent.putExtra("user_status", model.getmStatus());
                v.getContext().startActivity(chatIntent);
            }
        });
    }

    @NonNull
    @Override
    public FriendsChatAdapter.FriendsChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,
                parent, false);
        return new FriendsChatAdapter.FriendsChatViewHolder(v);
    }

    public class FriendsChatViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mUserNameView;
        CircleImageView mUserUrlView;
        TextView mUserStatusView;

        FriendsChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            mUserNameView = mView.findViewById(R.id.user_name_id);
            mUserNameView.setText(name);
        }

        public void setUri(String getImageUrl) {
            mUserUrlView = mView.findViewById(R.id.user_image_id);
            Picasso.get().load(getImageUrl).into(mUserUrlView);
        }

        void setStatus(String getStatus) {
            mUserStatusView = mView.findViewById(R.id.user_status_id);
            mUserStatusView.setText(getStatus);
        }
    }
}
