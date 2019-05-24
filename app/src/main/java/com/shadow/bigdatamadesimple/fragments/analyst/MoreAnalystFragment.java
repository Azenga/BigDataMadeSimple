package com.shadow.bigdatamadesimple.fragments.analyst;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.bigdatamadesimple.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreAnalystFragment extends Fragment {

    private static final String TAG = "MoreAnalystFragment";

    private TextView specificsTV, jobTypeTV, countriesTV, workRemotelyTV;


    private static final String ANALYST_UID_PARAM = "analyst-uid-param";
    private String mAnalystUid = null;

    private FirebaseFirestore mDb;

    public MoreAnalystFragment() {
        // Required empty public constructor
        mDb = FirebaseFirestore.getInstance();
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
        View view = inflater.inflate(R.layout.fragment_more_analyst, container, false);

        specificsTV = view.findViewById(R.id.specifics_tv);
        jobTypeTV = view.findViewById(R.id.job_type_tv);
        countriesTV = view.findViewById(R.id.countries_tv);
        workRemotelyTV = view.findViewById(R.id.work_remotely_tv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mAnalystUid != null) {
            mDb.document("analysts/" + mAnalystUid)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                specificsTV.setText(documentSnapshot.getString("specifics"));
                                jobTypeTV.setText(documentSnapshot.getString("jobType"));
                                countriesTV.setText(documentSnapshot.get("countries").toString());

                                if (documentSnapshot.getBoolean("workRemotely")) {
                                    specificsTV.setText("YES");
                                } else {
                                    specificsTV.setText("NO");
                                }


                            }
                    )
                    .addOnFailureListener(e -> Log.e(TAG, "onViewCreated: ", e));
        }
    }
}
