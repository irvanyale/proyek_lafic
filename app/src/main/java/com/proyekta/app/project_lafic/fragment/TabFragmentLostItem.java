package com.proyekta.app.project_lafic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyekta.app.project_lafic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragmentLostItem extends Fragment {


    public TabFragmentLostItem() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_fragment_lost_item, container, false);
    }

}
