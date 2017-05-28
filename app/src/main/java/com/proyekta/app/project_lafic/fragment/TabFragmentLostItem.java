package com.proyekta.app.project_lafic.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.activity.BerandaActivity;
import com.proyekta.app.project_lafic.activity.ScanActivity;
import com.proyekta.app.project_lafic.activity.SendMessageActivity;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.adapter.ListLostItemsAdapter;
import com.proyekta.app.project_lafic.helper.BarangHilangHelper;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

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
    private AlertDialog alertDialog;
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

        listLostItemsAdapter.setOnSendMessageListener(new ListLostItemsAdapter.setOnSendMessageListener() {
            @Override
            public void OnSendMessageListener(Member member) {
                showDialogSendMessage(member);
            }
        });

        listLostItemsAdapter.setOnShowImageListener(new ListLostItemsAdapter.setOnShowImageListener() {
            @Override
            public void OnShowImageListener(int position) {
                showDialogImageZoom(position);
            }
        });

        loadBarangHilang();

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
    }

    private void showDialogSendMessage(final Member member){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("Would you like to send message to "+member.getNAMA_MEMBER()+"?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getActivity(), SendMessageActivity.class);
                        intent.putExtra("member_id", member.getMEMBER_ID());
                        intent.putExtra("nama", member.getNAMA_MEMBER());
                        intent.putExtra("telp", member.getTELEPON());
                        intent.putExtra("email", member.getEMAIL_MEMBER());
                        intent.putExtra("no_id", member.getNOMOR_ID());
                        intent.putExtra("jenis_pesan", "PENEMUAN BARANG");
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void refreshData(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadBarangHilang();
            }
        });
    }

    private void showDialogImageZoom(int position){
        Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog_Fullscreen_Margin);
        dialog.setContentView(R.layout.dialog_image_zoom);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        dialog.setCanceledOnTouchOutside(true);

        PhotoView image = (PhotoView) dialog.findViewById(R.id.image);
        Picasso.with(getActivity())
                .load(ApiClient.BASE_URL_FOTO + listBarangHilang.get(position).getFOTO())
                .fit()
                .into(image);

        dialog.show();
    }

    private void loadBarangHilang(){

        /*if (refresh != null){
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }*/

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
                    Toast.makeText(getActivity(), "Data failed to load", Toast.LENGTH_SHORT).show();
                }

                if (refresh != null){
                    refresh.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<List<BarangHilang>> call, Throwable t) {
                Toast.makeText(getActivity(), "connection problem", Toast.LENGTH_SHORT).show();

                if (refresh != null){
                    refresh.setRefreshing(false);
                }

            }
        });
    }

}
