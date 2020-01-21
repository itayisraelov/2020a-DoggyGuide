package com.technion.doggyguide.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.DogOwnerElement;
import com.technion.doggyguide.users.UserProfile;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersCustomAdapter extends
        RecyclerView.Adapter<UsersCustomAdapter.UsersViewHolder> implements Filterable {

    private List<DogOwnerElement> mTubeList;
    private List<DogOwnerElement> mTubeListFiltered;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mUsersRef = db.collection("dogOwners");
    private String clickedUserId;

    public UsersCustomAdapter(List<DogOwnerElement> mList) {
        this.mTubeList = mList;
        this.mTubeListFiltered = mList;
    }

    @NonNull
    @Override
    public UsersCustomAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_single_layout, parent, false);
        return new UsersCustomAdapter.UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersCustomAdapter.UsersViewHolder holder, int position) {
        final DogOwnerElement dogOwner = mTubeListFiltered.get(position);
        holder.setName(dogOwner.getmName());
        holder.setUri(dogOwner.getmImageUrl());
        holder.setStatus(dogOwner.getmStatus());
        mUsersRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot doc : documents) {
                                DogOwnerElement element = doc.toObject(DogOwnerElement.class);
                                if (dogOwner.getmEmail()
                                        .equals(element.getmEmail()))
                                    clickedUserId = doc.getId();
                            }
                        }
                    }
                });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile_intent = new Intent (v.getContext(), UserProfile.class);
                profile_intent.putExtra("user_id", clickedUserId);
                v.getContext().startActivity(profile_intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTubeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String pattern = constraint.toString().toLowerCase().trim();
                if(pattern.isEmpty()){
                    mTubeListFiltered = mTubeList;
                } else {
                    List<DogOwnerElement> filteredList = new ArrayList<>();
                    for(DogOwnerElement tube: mTubeList){
                        if(tube.getmName().toLowerCase().startsWith(pattern)) {
                            filteredList.add(tube);
                        }
                    }
                    mTubeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mTubeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTubeListFiltered = (ArrayList<DogOwnerElement>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
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
