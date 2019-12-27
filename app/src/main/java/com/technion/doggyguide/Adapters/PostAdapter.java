//package com.technion.doggyguide.Adapters;
//
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.ImageView;
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.technion.doggyguide.R;
//import com.technion.doggyguide.dataElements.EventElement;
//import com.technion.doggyguide.notifications.AlertRecieverEvent;
//
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.technion.doggyguide.dataElements.PostElement;
//
//import java.util.List;
//
//public class PostAdapter extends FirestoreRecyclerAdapter<PostElement, PostAdapter.PostHolder> {
//
//    private final String TAG;
//
//    {
//        TAG = "Post Adapter";
//    }
//
//    public PostAdapter(@NonNull FirestoreRecyclerOptions<PostElement> options) {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull PostElement model) {
//        //mImageView to complete
//        holder.mTitle.setText(model.getmTitle());
//        holder.textViewTime.setText(model.getStart_time() + "-" + model.getEnd_time());
//        holder.textViewDescription.setText(model.getDescription());
//        holder.textViewDate.setText(model.getDate());
//    }
//
//    @NonNull
//    @Override
//    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }
//
//
//    public class PostHolder extends RecyclerView.ViewHolder {
//        private ImageView mImageView;
//        private TextView mTitle;
//        private TextView mDateTime;
//        private Button mAccept;
//        private Button mIgnore;
//
//        public PostHolder(final View itemView) {
//            super(itemView);
//            mImageView = itemView.findViewById(R.id.post_image);
//            mTitle = itemView.findViewById(R.id.post_title);
//            mDateTime = itemView.findViewById(R.id.post_date_time);
//            mAccept = itemView.findViewById(R.id.post_accept);
//            mIgnore = itemView.findViewById(R.id.post_ignore);
//
//            //onListen to both buttons
//
//        }
//
//    }
//}
