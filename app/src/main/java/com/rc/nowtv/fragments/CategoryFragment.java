package com.rc.nowtv.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.rc.nowtv.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    public CategoryFragment() {
        // Required empty public constructor
    }

    private View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(rootView.getContext()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(rootView.getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }


}
