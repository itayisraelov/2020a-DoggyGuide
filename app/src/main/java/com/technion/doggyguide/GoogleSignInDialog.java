package com.technion.doggyguide;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.technion.doggyguide.R;


import javax.annotation.Nullable;

public class GoogleSignInDialog extends DialogFragment {
    private static final String TAG = "GOOGLE DIALOG";

    public interface OnInputListener {
        void sendInput(String dogname, String dogbreed);
    }

    public OnInputListener mOnInputListener;

    private EditText mDogName, mDogBreed;
    private TextView mSet, mCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_google_sign_in, container, false);
        mDogBreed = view.findViewById(R.id.input_breed);
        mDogName = view.findViewById(R.id.input_name);
        mSet = view.findViewById(R.id.action_ok);
        mCancel = view.findViewById(R.id.action_cancel);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dogname = mDogName.getText().toString();
                String dogbreed = mDogBreed.getText().toString();
                if (dogname.isEmpty() || dogbreed.isEmpty()) {
                    Toast.makeText(getContext(),
                            "Please enter the name and breed of your dog" ,Toast.LENGTH_LONG).show();
                    return;
                }
                mOnInputListener.sendInput(dogname, dogbreed);
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
