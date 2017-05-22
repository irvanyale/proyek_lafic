package com.proyekta.app.project_lafic.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.adapter.ListItemsAdapter;
import com.proyekta.app.project_lafic.fragment.adapter.UserLostItemsAdapter;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.helper.BarangStatusAmanHelper;
import com.proyekta.app.project_lafic.helper.BarangStatusHilangHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.SuksesResponse;
import com.proyekta.app.project_lafic.util.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserLostItemsFragment extends Fragment {

    private static final String TAG = "UserLostItemsFragment";

    private RecyclerView rv_listItem;
    private UserLostItemsAdapter lostItemsAdapter;
    private List<Barang> listBarang;
    private List<Barang> listBarangHilang;
    private ApiInterface client;
    private ProgressDialog dialog;

    public UserLostItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_lost_items, container, false);

        initComponents(view);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        listBarang = BarangHelper.getBarang();
        listBarangHilang = BarangStatusHilangHelper.getBarangHilang();

        lostItemsAdapter = new UserLostItemsAdapter(getActivity(), listBarangHilang);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(lostItemsAdapter);

        //tampil edit status
        lostItemsAdapter.setOnShowEditBarangListener(new UserLostItemsAdapter.setOnShowEditBarangListener() {
            @Override
            public void OnShowEditBarangListener(Barang barang) {
                showDialogEditBarang(barang);
            }
        });

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
    }

    private String statusBarang = "AMAN";
    private EditText edtx_lokasi_hilang;
    private Dialog dialogEditStatus;

    private void showDialogEditBarang(final Barang barang){
        dialogEditStatus = new Dialog(getActivity(), R.style.Theme_Dialog_Fullscreen_Margin);
        dialogEditStatus.setContentView(R.layout.dialog_edit_barang);

        Window window = dialogEditStatus.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        final Button btn_aman = (Button) dialogEditStatus.findViewById(R.id.btn_aman);
        final Button btn_hilang = (Button) dialogEditStatus.findViewById(R.id.btn_hilang);
        Button btn_update = (Button) dialogEditStatus.findViewById(R.id.btn_update);
        edtx_lokasi_hilang = (EditText) dialogEditStatus.findViewById(R.id.edtx_lokasi_hilang);

        btn_hilang.setVisibility(View.GONE);

        btn_aman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_aman.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_green));
                btn_aman.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));

                btn_hilang.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_grey));
                btn_hilang.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));

                edtx_lokasi_hilang.setVisibility(View.GONE);

                statusBarang = "AMAN";
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateBarang(barang);
            }
        });

        dialogEditStatus.setCanceledOnTouchOutside(true);
        dialogEditStatus.show();
    }

    private void doUpdateBarang(final Barang barang){

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();

        barang.setSTATUS(statusBarang);
        Call<Barang> call = client.doUpdateBarang(barang);
        call.enqueue(new Callback<Barang>() {
            @Override
            public void onResponse(Call<Barang> call, Response<Barang> response) {
                if (response.isSuccessful()){
                    deleteBarangHilang(barang);
                } else {
                    Toast.makeText(getActivity(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialogEditStatus.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Barang> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }
        });
    }

    private void loadBarang(String idMember){

        Call<List<Barang>> call = client.getAllBarang(idMember);
        call.enqueue(new Callback<List<Barang>>() {
            @Override
            public void onResponse(Call<List<Barang>> call, Response<List<Barang>> response) {
                if (response.isSuccessful()){
                    listBarang.clear();
                    listBarangHilang.clear();
                    List<Barang> barang = response.body();
                    for (Barang data : barang){
                        listBarang.add(data);
                    }
                    listBarangHilang = BarangStatusHilangHelper.getBarangHilang();
                    lostItemsAdapter.setList(listBarangHilang);
                } else {
                    Toast.makeText(getActivity(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }

            @Override
            public void onFailure(Call<List<Barang>> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }
        });
    }

    private void deleteBarangHilang(final Barang barang){

        Call<SuksesResponse> call = client.deleteBarangHilang(barang.getBARANG_ID());
        call.enqueue(new Callback<SuksesResponse>() {
            @Override
            public void onResponse(Call<SuksesResponse> call, Response<SuksesResponse> response) {
                if (response.isSuccessful()){
                    loadBarang(barang.getMEMBER_ID());
                } else {
                    Toast.makeText(getActivity(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialogEditStatus.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SuksesResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }
        });
    }
}
