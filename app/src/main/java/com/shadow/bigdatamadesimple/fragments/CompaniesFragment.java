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
import com.shadow.bigdatamadesimple.adapters.UserAdapter;
import com.shadow.bigdatamadesimple.models.User;

import java.util.ArrayList;
import java.util.List;

public class CompaniesFragment extends Fragment {
    private static final String TAG = "CompaniesFragment";
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;


    private List<User> users;

    public CompaniesFragment() {
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
                .whereEqualTo("group", "company")
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "onViewCreated: getting companies error", e);
                                return;
                            }

                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(getActivity(), "No companies yet", Toast.LENGTH_SHORT).show();
                            } else {
                                users.clear();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    if (!mAuth.getCurrentUser().getUid().equals(snapshot.getId())) {
                                        User user = snapshot.toObject(User.class);
                                        user.setUid(snapshot.getId());
                                        users.add(user);
                                    }
                                }

                                UserAdapter adapter = new UserAdapter(getActivity(), users, true);
                                analystRV.setAdapter(adapter);

                            }
                        }
                );
    }
}
