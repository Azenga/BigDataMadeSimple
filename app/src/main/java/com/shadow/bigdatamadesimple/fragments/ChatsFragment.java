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
import com.google.firebase.firestore.Query;
import com.shadow.bigdatamadesimple.R;
import com.shadow.bigdatamadesimple.adapters.AnalystAdapter;
import com.shadow.bigdatamadesimple.models.Message;
import com.shadow.bigdatamadesimple.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";

    private RecyclerView chattedAnalystsRV;
    private List<User> userList;
    private List<String> userUids;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public ChatsFragment() {
        userList = new ArrayList<>();
        userUids = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        chattedAnalystsRV = view.findViewById(R.id.analysts_rv);
        chattedAnalystsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        chattedAnalystsRV.setHasFixedSize(true);

        final String myUid = mAuth.getCurrentUser().getUid();

        mDb.collection("chats")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Toast.makeText(getActivity(), "A fatal error occurred", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onViewCreated: Failed reading messages", e);
                                return;
                            }

                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(getActivity(), "No chats yet", Toast.LENGTH_SHORT).show();
                            } else {
                                userUids.clear();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Message msg = snapshot.toObject(Message.class);

                                    if (msg.getSender().equals(myUid)) {
                                        userUids.add(msg.getReceiver());
                                    }
                                    if (msg.getReceiver().equals(myUid)) {
                                        userUids.add(msg.getSender());
                                    }
                                }

                                readUsers();
                            }
                        }
                );
    }

    private void readUsers() {

        mDb.collection("users")
                .whereEqualTo("group", "analyst")
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Toast.makeText(getActivity(), "A fatal error occurred", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "readUsers: getting users error", e);
                                return;
                            }

                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(getActivity(), "No analysts yet", Toast.LENGTH_SHORT).show();
                            } else {
                                userList.clear();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    User user = snapshot.toObject(User.class);
                                    user.setUid(snapshot.getId());

                                    for (String uid : userUids) {
                                        if (user.getUid().equals(uid)) {
                                            if (!userList.contains(user)) {
                                                userList.add(user);
                                            }
                                            break;
                                        }
                                    }
                                }

                                AnalystAdapter adapter = new AnalystAdapter(getActivity(), userList);
                                chattedAnalystsRV.setAdapter(adapter);

                            }
                        }
                );

    }
}
