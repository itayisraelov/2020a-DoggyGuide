package com.technion.doggyguide.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.technion.doggyguide.R;

public class StatusChangeDialog extends AppCompatDialogFragment {
    private EditText editTextStatus;
    private StatusChangeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_status_dialog, null);

        builder.setView(view)
                .setTitle("Change Status")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String status = editTextStatus.getText().toString();
                        listener.applyChange(status);
                    }
                });

        editTextStatus = view.findViewById(R.id.edit_username);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (StatusChangeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement StatusChangeDialogListener");
        }
    }

    public interface StatusChangeDialogListener {
        void applyChange(String status);
    }

}
