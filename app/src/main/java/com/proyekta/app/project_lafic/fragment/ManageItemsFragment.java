package com.proyekta.app.project_lafic.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.activity.AddItemActivity;
import com.proyekta.app.project_lafic.fragment.adapter.ListItemsAdapter;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.helper.BarangStatusAmanHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.util.StorageUtil;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageItemsFragment extends Fragment {

    private static final String TAG = "ManageItemsFragment";

    private RelativeLayout rlly_footer;
    private RecyclerView rv_listItem;
    private ListItemsAdapter listItemsAdapter;
    private List<Barang> listBarang;
    private List<Barang> listBarangAman;
    private ApiInterface client;
    private ProgressDialog dialog;

    public ManageItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_items, container, false);

        initComponents(view);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        listBarang = BarangHelper.getBarang();
        listBarangAman = BarangStatusAmanHelper.getBarangAman();

        listItemsAdapter = new ListItemsAdapter(getActivity(), listBarangAman);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(listItemsAdapter);

        //tampil QRCode
        listItemsAdapter.setOnShowQRCodeListener(new ListItemsAdapter.setOnShowQRCodeListener() {
            @Override
            public void OnShowQRCodeListener(String url) {
                //showDialogQRCode(url);
            }
        });

        //tampil edit status
        listItemsAdapter.setOnShowEditBarangListener(new ListItemsAdapter.setOnShowEditBarangListener() {
            @Override
            public void OnShowEditBarangListener(Barang barang) {
                showDialogEditBarang(barang);
            }
        });

        rlly_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddItemActivity.class));
            }
        });

        return view;
    }

    private void initComponents(View view){
        rlly_footer = (RelativeLayout) view.findViewById(R.id.rlly_footer);
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
    }

    //tampil dialog QRCode
    private void showDialogQRCode(final String id){
        Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog_Fullscreen_Margin);
        dialog.setContentView(R.layout.dialog_qrcode);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        ImageView imgv_share = (ImageView) dialog.findViewById(R.id.imgv_share);

        //Image share QR code
        imgv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(id);
            }
        });

        dialog.setCanceledOnTouchOutside(true);

        Uri uri = Uri.fromFile(new File(StorageUtil.getFileDirectoryPath() + "/" + id + ".jpg"));

        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Picasso.with(getActivity())
                .load(uri)
                .fit()
                .error(R.drawable.ic_image)
                .into(image);

        dialog.show();
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

        btn_hilang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_hilang.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_red));
                btn_hilang.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));

                btn_aman.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_grey));
                btn_aman.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));

                edtx_lokasi_hilang.setVisibility(View.VISIBLE);

                statusBarang = "HILANG";
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
                    doPostBarangHilang(barang);
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

    private void doPostBarangHilang(final Barang barang){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());

        BarangHilang barangHilang = new BarangHilang();
        barangHilang.setBARANG_ID(barang.getBARANG_ID());
        barangHilang.setTANGGAL_HILANG(currentDateandTime);
        //barangHilang.setTANGGAL_KETEMU("-");
        barangHilang.setLOKASI_HILANG(edtx_lokasi_hilang.getText().toString());

        Call<BarangHilang> call = client.postBarangHilang(barangHilang);
        call.enqueue(new Callback<BarangHilang>() {
            @Override
            public void onResponse(Call<BarangHilang> call, Response<BarangHilang> response) {
                if (response.isSuccessful()){
                    loadBarang(barang.getMEMBER_ID());
                } else {
                    Toast.makeText(getActivity(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialogEditStatus.dismiss();
                }
            }

            @Override
            public void onFailure(Call<BarangHilang> call, Throwable t) {
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
                    listBarangAman.clear();
                    List<Barang> barang = response.body();
                    for (Barang data : barang){
                        listBarang.add(data);
                    }
                    listBarangAman = BarangStatusAmanHelper.getBarangAman();
                    listItemsAdapter.setList(listBarangAman);
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

    private void share(String id){
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        File file = new File(StorageUtil.getFileDirectoryPath(), id + ".jpg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        share.setType("image/jpg");
        startActivity(Intent.createChooser(share, "Share"));
    }

    @Override
    public void onResume() {
        super.onResume();
        listBarang = BarangHelper.getBarang();
        listBarangAman = BarangStatusAmanHelper.getBarangAman();
        listItemsAdapter.setList(listBarangAman);

        listItemsAdapter.setOnShowQRCodeListener(new ListItemsAdapter.setOnShowQRCodeListener() {
            @Override
            public void OnShowQRCodeListener(String url) {
                //showDialogQRCode(url);
            }
        });

        listItemsAdapter.setOnShowEditBarangListener(new ListItemsAdapter.setOnShowEditBarangListener() {
            @Override
            public void OnShowEditBarangListener(Barang barang) {
                showDialogEditBarang(barang);
            }
        });
    }
}
