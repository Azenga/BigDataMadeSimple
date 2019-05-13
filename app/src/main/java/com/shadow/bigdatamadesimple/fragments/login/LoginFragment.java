package com.shadow.bigdatamadesimple.fragments.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.shadow.bigdatamadesimple.MainActivity;
import com.shadow.bigdatamadesimple.R;
import com.shadow.bigdatamadesimple.RegisterActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private static final String GROUP_PARAM = "group";

    private String mGroup = null;
    private FirebaseAuth mAuth;

    //Widgets
    private TextInputEditText emailTIET, pwdTIET;

    private ProgressDialog progressDialog;


    public LoginFragment() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static LoginFragment newInstance(String group) {

        Bundle args = new Bundle();
        args.putString(GROUP_PARAM, group);

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) mGroup = getArguments().getString(GROUP_PARAM);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //TextInputEditTexts
        emailTIET = view.findViewById(R.id.email_txt);
        pwdTIET = view.findViewById(R.id.pwd_txt);

        //Button
        Button signinBtn = view.findViewById(R.id.signin_btn);
        signinBtn.setOnClickListener(v -> login());

        //TextView
        TextView gotoSignupTV = view.findViewById(R.id.goto_signup_tv);

        gotoSignupTV.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            intent.putExtra(RegisterActivity.USER_GROUP, mGroup);
            startActivity(intent);
        });

        //ProgressDialog
        progressDialog = new ProgressDialog(getActivity());

    }

    private void login() {
        String email = String.valueOf(emailTIET.getText());
        String pwd = String.valueOf(pwdTIET.getText());

        if (TextUtils.isEmpty(email)) {
            emailTIET.setError("Email is required");
            emailTIET.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {

            pwdTIET.setError("Password is required");
            pwdTIET.requestFocus();
            return;
        }

        progressDialog.setTitle("Signing In");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                if (mGroup.equalsIgnoreCase("company")) {

                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra(RegisterActivity.USER_GROUP, mGroup);
                                    startActivity(intent);

                                } else if (mGroup.equalsIgnoreCase("analyst")) {

                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra(RegisterActivity.USER_GROUP, mGroup);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getActivity(), "I just hope to never see this", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }

                            progressDialog.dismiss();
                        }
                );
    }


}
