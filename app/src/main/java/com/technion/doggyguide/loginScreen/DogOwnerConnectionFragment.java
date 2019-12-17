package com.technion.doggyguide.loginScreen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.technion.doggyguide.DogOwnerSignUp;
import com.technion.doggyguide.R;
import com.technion.doggyguide.homeActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DogOwnerConnectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DogOwnerConnectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DogOwnerConnectionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;

    private Button mLoginBtn;
    private Button mSignUpBtn;
    private EditText emailtxt;
    private EditText pwdtxt;



    public DogOwnerConnectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DogOwnerConnectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DogOwnerConnectionFragment newInstance(String param1, String param2) {
        DogOwnerConnectionFragment fragment = new DogOwnerConnectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dog_owner_connection, container, false);
        mLoginBtn = view.findViewById(R.id.btnDogownerlogin);
        mSignUpBtn = view.findViewById(R.id.btnDogownersignup);
        emailtxt = view.findViewById(R.id.home_dogowneremail);
        pwdtxt = view.findViewById(R.id.home_dogownerpassword);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailtxt.getText().toString();
                String pwd = pwdtxt.getText().toString();
                if (!validateEmailAndPwd(email, pwd))
                    return;
                signInWithEmailAndPassword(email, pwd);
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DogOwnerSignUp.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private boolean validateEmailAndPwd(String email, String pwd) {
        if (email.isEmpty()) {
            emailtxt.setError("Please enter your email address.");
            emailtxt.requestFocus();
            return false;
        } else if (pwd.isEmpty()) {
            pwdtxt.setError("Please enter your password.");
            pwdtxt.requestFocus();
            return false;
        }
        return true;
    }

    private void signInWithEmailAndPassword(String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(getActivity(), homeActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(),
                                        "This email is registered but hasn't been verified yet.\nPlease verify your email",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
