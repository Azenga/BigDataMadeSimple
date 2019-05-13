package com.shadow.bigdatamadesimple.fragments.analyst;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shadow.bigdatamadesimple.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreAnalystFragment extends Fragment {


    private static final String ANALYST_UID_PARAM = "analyast-uid-param";
    private String mAnalystUid = null;

    public MoreAnalystFragment() {
        // Required empty public constructor
    }

    public static MoreAnalystFragment newInstance(String analystuid) {

        Bundle args = new Bundle();
        args.putString(ANALYST_UID_PARAM, analystuid);

        MoreAnalystFragment fragment = new MoreAnalystFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            mAnalystUid = getArguments().getString(ANALYST_UID_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_analyst, container, false);
    }

}
