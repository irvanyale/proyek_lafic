package com.proyekta.app.project_lafic.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.activity.AddItemActivity;
import com.proyekta.app.project_lafic.activity.adapter.ListItemsAdapter;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.util.StorageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageItemsFragment extends Fragment {

    private static final String TAG = "ManageItemsFragment";

    private RelativeLayout rlly_add_items;
    private RecyclerView rv_listItem;
    private ListItemsAdapter listItemsAdapter;
    private List<Barang> listBarang;

    public ManageItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_items, container, false);

        initComponents(view);

        listBarang = BarangHelper.getBarang();

        listItemsAdapter = new ListItemsAdapter(getActivity(), listBarang);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(listItemsAdapter);

        listItemsAdapter.setOnShowQRCodeListener(new ListItemsAdapter.setOnShowQRCodeListener() {
            @Override
            public void OnShowQRCodeListener(String url) {
                showDialogQRCode(url);
            }
        });

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
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
    }

    private void showDialogQRCode(final String id){
        Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog_Fullscreen_Margin);
        dialog.setContentView(R.layout.dialog_qrcode);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        ImageView imgv_share = (ImageView) dialog.findViewById(R.id.imgv_share);

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
        listItemsAdapter = new ListItemsAdapter(getActivity(), listBarang);
        listItemsAdapter.setOnShowQRCodeListener(new ListItemsAdapter.setOnShowQRCodeListener() {
            @Override
            public void OnShowQRCodeListener(String url) {
                showDialogQRCode(url);
            }
        });
        rv_listItem.setAdapter(listItemsAdapter);
    }
}
