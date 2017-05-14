package com.proyekta.app.project_lafic.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.activity.BerandaActivity;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.adapter.ListLostItemsAdapter;
import com.proyekta.app.project_lafic.helper.BarangHilangHelper;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.util.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragmentLostItem extends Fragment {

    private static final String TAG = "TabFragmentLostItem";

    private RecyclerView rv_listItem;
    private List<BarangHilang> listBarangHilang;
    private ListLostItemsAdapter listLostItemsAdapter;
    private SwipeRefreshLayout refresh;

    private ApiInterface client;

    public TabFragmentLostItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_fragment_lost_item, container, false);

        initComponents(view);

        listBarangHilang = BarangHilangHelper.getBarangHilang();

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        listLostItemsAdapter = new ListLostItemsAdapter(getActivity(), listBarangHilang);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(listLostItemsAdapter);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
    }

    private void refreshData(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadBarangHilang();
            }
        });
    }

    private void loadBarangHilang(){

        if (refresh != null)
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });

        Call<List<BarangHilang>> call = client.getAllBarangHilang();
        call.enqueue(new Callback<List<BarangHilang>>() {
            @Override
            public void onResponse(Call<List<BarangHilang>> call, Response<List<BarangHilang>> response) {
                if (response.isSuccessful()){
                    listBarangHilang.clear();
                    List<BarangHilang> listItems = response.body();
                    for (BarangHilang data : listItems){
                        listBarangHilang.add(data);
                    }
                    listLostItemsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                }

                if (refresh != null){
                    refresh.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<List<BarangHilang>> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();

                if (refresh != null){
                    refresh.setRefreshing(false);
                }

            }
        });
    }

}
