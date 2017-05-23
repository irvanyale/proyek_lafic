package com.proyekta.app.project_lafic.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.adapter.UserFoundItemsAdapter;
import com.proyekta.app.project_lafic.model.BarangPenemuan;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFoundItemsFragment extends Fragment {

    private RecyclerView rv_listItem;
    private UserFoundItemsAdapter foundItemsAdapter;
    private List<BarangPenemuan> barangPenemuan;
    private ApiInterface client;
    private ProgressDialog dialog;

    public UserFoundItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_found_items, container, false);



        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
    }

}
