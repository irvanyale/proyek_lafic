package com.proyekta.app.project_lafic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.fragment.adapter.ListLostItemsAdapter;
import com.proyekta.app.project_lafic.helper.BarangHilangHelper;
import com.proyekta.app.project_lafic.model.BarangHilang;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragmentLostItem extends Fragment {

    private static final String TAG = "TabFragmentLostItem";

    private RecyclerView rv_listItem;
    private List<BarangHilang> listBarangHilang;
    private ListLostItemsAdapter listLostItemsAdapter;

    public TabFragmentLostItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_fragment_lost_item, container, false);

        initComponents(view);

        listBarangHilang = BarangHilangHelper.getBarangHilang();

        listLostItemsAdapter = new ListLostItemsAdapter(getActivity(), listBarangHilang);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(listLostItemsAdapter);

        Log.d(TAG, "onCreateView: tes"+listBarangHilang.size());

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
    }

}
