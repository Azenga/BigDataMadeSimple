package com.shadow.bigdatamadesimple.fragments.analyst;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.bigdatamadesimple.MessageActivity;
import com.shadow.bigdatamadesimple.R;
import com.shadow.bigdatamadesimple.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicInfoFragment extends Fragment {

    private static final String TAG = "BasicInfoFragment";

    private CircleImageView profileCIV;
    private TextView reccommendationsTV, hiresTV, nameTV, contactTV, locationTV, websiteTV;
    private Button hireBtn, messageBtn;


    public static final String ANALYST_ID_PARAM = "analyst-id-param";
    private String analystUid = null;


    private FirebaseFirestore mDb;
    private StorageReference mRef;

    public BasicInfoFragment() {
        // Required empty public constructor

        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("avatars");
    }

    public static BasicInfoFragment newInstance(String analystId) {

        Bundle args = new Bundle();
        args.putString(ANALYST_ID_PARAM, analystId);
        BasicInfoFragment fragment = new BasicInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) analystUid = getArguments().getString(ANALYST_ID_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);

        profileCIV = view.findViewById(R.id.profile_civ);
        reccommendationsTV = view.findViewById(R.id.reccommendations_tv);
        hiresTV = view.findViewById(R.id.hires_tv);
        hireBtn = view.findViewById(R.id.hire_btn);
        messageBtn = view.findViewById(R.id.message_btn);
        nameTV = view.findViewById(R.id.name_tv);
        contactTV = view.findViewById(R.id.contact_tv);
        locationTV = view.findViewById(R.id.location_tv);
        websiteTV = view.findViewById(R.id.website_tv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDb.document("users/" + analystUid)
                .addSnapshotListener(
                        (documentSnapshot, e) -> {
                            if (e != null) {
                                Log.e(TAG, "getUserDetails: Error", e);
                                return;
                            }

                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                user.setUid(documentSnapshot.getId());
                                if (user != null) updateUI(user);
                            }

                        }
                );

        messageBtn.setOnClickListener(
                v -> {
                    Intent intent = new Intent(getActivity(), MessageActivity.class);
                    intent.putExtra(MessageActivity.USER_UID_PARAM, analystUid);
                    startActivity(intent);
                }
        );

    }

    private void updateUI(User user) {

        if (user.getImageName() != null) {
            StorageReference profileImageRef = mRef.child(user.getImageName());
            final long MB = 1024 * 1024;
            profileImageRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profileCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(e -> Log.e(TAG, "updateUI: Getting Image", e));
        }

        nameTV.setText(user.getFullName());
        contactTV.setText(user.getContact());
        locationTV.setText(user.getStreet());
        websiteTV.setText(user.getWebsite());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
