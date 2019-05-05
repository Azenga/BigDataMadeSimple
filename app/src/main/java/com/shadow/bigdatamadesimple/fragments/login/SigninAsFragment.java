package com.shadow.bigdatamadesimple.fragments.login;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shadow.bigdatamadesimple.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninAsFragment extends Fragment {

    private SignInListener mListener;


    public SigninAsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin_as, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button loginAsAnalyst = view.findViewById(R.id.login_as_an_analyst);
        loginAsAnalyst.setOnClickListener(v -> mListener.launchSignAs("analyst"));

        Button loginAsCompany = view.findViewById(R.id.login_as_company);
        loginAsCompany.setOnClickListener(v -> mListener.launchSignAs("company"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SignInListener) mListener = (SignInListener) context;
        else throw new ClassCastException(context.toString() + " must implement SignInListener");
    }

    public interface SignInListener {
        void launchSignAs(String group);
    }

}
