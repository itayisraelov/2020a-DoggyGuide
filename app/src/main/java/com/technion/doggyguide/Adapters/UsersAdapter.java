package com.technion.doggyguide.Adapters;

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
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;
import com.technion.doggyguide.users.UserProfile;
import com.technion.doggyguide.dataElements.Users;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersAdapter extends FirestoreRecyclerAdapter<DogOwnerElement, UsersAdapter.UsersViewHolder> {
    private final String TAG = "Users Adapter";

    public UsersAdapter(@NonNull FirestoreRecyclerOptions<DogOwnerElement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull DogOwnerElement model) {
        holder.setName(model.getmName());
        holder.setUri(model.getmImageUrl());
        holder.setStatus(model.getmStatus());

        final String user_id = getSnapshots().getSnapshot(position).getId();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile_intent = new Intent (v.getContext(), UserProfile.class);
                profile_intent.putExtra("user_id", user_id);
                v.getContext().startActivity(profile_intent);
            }
        });
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,
                parent, false);
        return new UsersAdapter.UsersViewHolder(v);
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mUserNameView;
        CircleImageView mUserUrlView;
        TextView mUserStatusView;


        public UsersViewHolder(@NonNull View itemView) {
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

        public void setStatus(String getStatus) {
            mUserStatusView = mView.findViewById(R.id.user_status_id);
            mUserStatusView.setText(getStatus);
        }
    }
}
