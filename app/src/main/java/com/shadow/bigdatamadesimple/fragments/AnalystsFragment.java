package com.shadow.bigdatamadesimple.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.bigdatamadesimple.R;
import com.shadow.bigdatamadesimple.adapters.AnalystAdapter;
import com.shadow.bigdatamadesimple.models.User;

import java.util.ArrayList;
import java.util.List;

public class AnalystsFragment extends Fragment {

    private static final String TAG = "AnalystsFragment";

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;


    private List<User> users;

    public AnalystsFragment() {
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analysts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView analystRV = view.findViewById(R.id.analysts_rv);

        analystRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        analystRV.setHasFixedSize(true);

        //Getting the users
        mDb.collection("users")
                .whereEqualTo("group", "analyst")
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Toast.makeText(getActivity(), "A fatal error occurred", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onViewCreated: getting users error", e);
                                return;
                            }

                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(getActivity(), "No analysts yet", Toast.LENGTH_SHORT).show();
                            } else {
                                users.clear();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    if (!mAuth.getCurrentUser().getUid().equals(snapshot.getId())) {
                                        User user = snapshot.toObject(User.class);
                                        user.setUid(snapshot.getId());
                                        users.add(user);
                                    }
                                }

                                AnalystAdapter adapter = new AnalystAdapter(getActivity(), users);
                                analystRV.setAdapter(adapter);
                            }
                        }
                );
    }
}
