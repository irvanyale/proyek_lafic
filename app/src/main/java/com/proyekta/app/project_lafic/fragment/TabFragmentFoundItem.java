package com.proyekta.app.project_lafic.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.activity.SendMessageActivity;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.adapter.ListFoundItemsAdapter;
import com.proyekta.app.project_lafic.fragment.adapter.ListLostItemsAdapter;
import com.proyekta.app.project_lafic.helper.BarangPenemuanHelper;
import com.proyekta.app.project_lafic.model.BarangPenemuan;
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
public class TabFragmentFoundItem extends Fragment {

    private static final String TAG = "TabFragmentFoundItem";

    private RecyclerView rv_listItem;
    private List<BarangPenemuan> listBarangPenemuan;
    private ListFoundItemsAdapter listFoundItemsAdapter;
    private SwipeRefreshLayout refresh;
    private AlertDialog alertDialog;
    private ApiInterface client;

    public TabFragmentFoundItem() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_fragment_found_item, container, false);

        initComponents(view);

        listBarangPenemuan = BarangPenemuanHelper.getBarangPenemuan();

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        listFoundItemsAdapter = new ListFoundItemsAdapter(getActivity(), listBarangPenemuan);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(listFoundItemsAdapter);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        listFoundItemsAdapter.setOnSendMessageListener(new ListFoundItemsAdapter.setOnSendMessageListener() {
            @Override
            public void OnSendMessageListener(Member member) {
                showDialogSendMessage(member);
            }
        });

        listFoundItemsAdapter.setOnShowImageListener(new ListFoundItemsAdapter.setOnShowImageListener() {
            @Override
            public void OnShowImageListener(int position) {
                showDialogImageZoom(position);
            }
        });

        loadBarangPenemuan();

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
    }

    private void showDialogSendMessage(final Member member){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("would you like to send message to "+member.getNAMA_MEMBER()+"?");

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
                        intent.putExtra("jenis_pesan", "DEMAND ITEM");
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
                .load(ApiClient.BASE_URL_FOTO + listBarangPenemuan.get(position).getFOTO_PENEMUAN())
                .fit()
                .into(image);

        dialog.show();
    }

    private void refreshData(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadBarangPenemuan();
            }
        });
    }

    private void loadBarangPenemuan(){

        Call<List<BarangPenemuan>> call = client.getAllBarangPenemuan();
        call.enqueue(new Callback<List<BarangPenemuan>>() {
            @Override
            public void onResponse(Call<List<BarangPenemuan>> call, Response<List<BarangPenemuan>> response) {
                if (response.isSuccessful()){
                    listBarangPenemuan.clear();
                    List<BarangPenemuan> listItems = response.body();
                    for (BarangPenemuan data : listItems){
                        listBarangPenemuan.add(data);
                    }
                    listFoundItemsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Data failed to load", Toast.LENGTH_SHORT).show();
                }
                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<BarangPenemuan>> call, Throwable t) {
                Toast.makeText(getActivity(), "connection problem", Toast.LENGTH_SHORT).show();

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        listBarangPenemuan = BarangPenemuanHelper.getBarangPenemuan();
        listFoundItemsAdapter.setList(listBarangPenemuan);

        listFoundItemsAdapter.setOnSendMessageListener(new ListFoundItemsAdapter.setOnSendMessageListener() {
            @Override
            public void OnSendMessageListener(Member member) {
                showDialogSendMessage(member);
            }
        });

    }
}
