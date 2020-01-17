package com.technion.doggyguide.friends;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.Chat.ChatActivity;
import com.technion.doggyguide.R;
import com.technion.doggyguide.users.UserProfile;
import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsAdapter extends FirestoreRecyclerAdapter<Friends, FriendsAdapter.FriendsViewHolder> {

    private final String TAG = "Friends Adapter";

    FriendsAdapter(@NonNull FirestoreRecyclerOptions<Friends> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendsAdapter.FriendsViewHolder holder,
                                    int position, @NonNull final Friends model) {
        holder.setName(model.getmName());
        holder.setUri(model.getmImageUrl());
        holder.setStatus(model.getmStatus());
        //holder.setOnline(model.getOnline());

        final String user_id = getSnapshots().getSnapshot(position).getId();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Click Event for each item.
                        if(i == 0){
                            Intent profileIntent = new Intent(v.getContext(), UserProfile.class);
                            profileIntent.putExtra("user_id", user_id);
                            profileIntent.putExtra("user_name", model.getmName());
                            profileIntent.putExtra("user_image", model.getmImageUrl());
                            profileIntent.putExtra("user_status", model.getmStatus());
                            v.getContext().startActivity(profileIntent);
                        }
                        if(i == 1){
                            Intent chatIntent = new Intent(v.getContext(), ChatActivity.class);
                            chatIntent.putExtra("user_id", user_id);
                            chatIntent.putExtra("user_name", model.getmName());
                            chatIntent.putExtra("user_image", model.getmImageUrl());
                            chatIntent.putExtra("user_status", model.getmStatus());
                            v.getContext().startActivity(chatIntent);
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_single_layout,
                parent, false);
        return new FriendsAdapter.FriendsViewHolder(v);
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mUserNameView;
        CircleImageView mUserUrlView;
        TextView mUserStatusView;

        FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            mUserNameView = mView.findViewById(R.id.friend_name_id);
            mUserNameView.setText(name);
        }

        public void setUri(String getImageUrl) {
            mUserUrlView = mView.findViewById(R.id.friend_image_id);
            Picasso.get().load(getImageUrl).into(mUserUrlView);
        }

        void setStatus(String getStatus) {
            mUserStatusView = mView.findViewById(R.id.friend_status_id);
            mUserStatusView.setText(getStatus);
        }

//        void setOnline(String online) {
//            ImageView onlineStatus  = mView.findViewById(R.id.user_single_online_icon);
//
//            if(online.equals("true")){
//                onlineStatus.setVisibility(View.VISIBLE);
//            }else{
//                onlineStatus.setVisibility(View.INVISIBLE);
//            }
//        }
    }
}
