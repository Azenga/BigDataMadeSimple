package com.shadow.bigdatamadesimple.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shadow.bigdatamadesimple.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogPostFragment extends Fragment {


    public BlogPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog_post, container, false);
    }

}
