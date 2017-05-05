package com.proyekta.app.project_lafic.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.activity.AddItemActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageItemsFragment extends Fragment {

    private RelativeLayout rlly_add_items;

    public ManageItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_items, container, false);

        initComponents(view);

        rlly_add_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddItemActivity.class));
            }
        });

        return view;
    }

    private void initComponents(View view){
        rlly_add_items = (RelativeLayout) view.findViewById(R.id.rlly_add_items);
    }
}
