package com.shadow.bigdatamadesimple.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shadow.bigdatamadesimple.AddAdvertActivity;
import com.shadow.bigdatamadesimple.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdvertsFragment extends Fragment {

    private FloatingActionButton gotoAddAdvertBtn;
    private RecyclerView advertsRV;


    public AdvertsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_post, container, false);

        gotoAddAdvertBtn = view.findViewById(R.id.goto_add_advert_fab);
        advertsRV = view.findViewById(R.id.adverts_rv);

        gotoAddAdvertBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddAdvertActivity.class)));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }
}
