package com.technion.doggyguide.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.EventElement;

public class EventElementAdapter extends
        FirestoreRecyclerAdapter<EventElement, EventElementAdapter.EventHolder> {

    public EventElementAdapter(@NonNull FirestoreRecyclerOptions<EventElement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull EventElement model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewTime.setText(model.getStart_time() + "-" + model.getEnd_time());
        holder.textViewDescription.setText(model.getDescription());
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,
                parent, false);
        return new EventHolder(v);
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewTime;
        TextView textViewDescription;
        public EventHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.event_title);
            textViewTime = itemView.findViewById(R.id.event_time);
            textViewDescription = itemView.findViewById(R.id.event_description);
        }

    }
}
