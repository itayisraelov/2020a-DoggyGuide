package com.technion.doggyguide.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.AlarmElement;

public class AlarmElementAdapter extends
        FirestoreRecyclerAdapter<AlarmElement, AlarmElementAdapter.AlarmElementViewHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AlarmElementAdapter(@NonNull FirestoreRecyclerOptions<AlarmElement> options) {
        super(options);
    }

    @NonNull
    @Override
    public AlarmElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item,
                parent, false);
        return new AlarmElementViewHolder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull AlarmElementViewHolder holder,
                                    int position, @NonNull AlarmElement model) {
        holder.textView.setText(model.getTime());

    }

    public static class AlarmElementViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private Switch aSwitch;

        public AlarmElementViewHolder(final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.alarm_time);
            aSwitch = itemView.findViewById(R.id.switch1);
        }

    }
}
