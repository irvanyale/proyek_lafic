package com.proyekta.app.project_lafic.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.util.DownloadUtil;
import com.proyekta.app.project_lafic.util.StorageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeFragment extends Fragment {

    private static final String TAG = "QRCodeFragment";

    private Button btn_download;
    private ImageView imgv_qrcode;
    private TextView txvw_qrcode;
    private String idMember;
    private String qrcode;

    public QRCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        initComponents(view);

        getUserQRCode();

        loadQRCode();

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadQrCode(qrcode);
            }
        });

        return view;
    }

    private void initComponents(View view){
        btn_download = (Button) view.findViewById(R.id.btn_download);
        imgv_qrcode = (ImageView) view.findViewById(R.id.imgv_qrcode);
        txvw_qrcode = (TextView) view.findViewById(R.id.txvw_qrcode);
    }

    private void loadQRCode(){
        try {
            File file = new File(StorageUtil.getFileDirectoryPath() + "/" + idMember + ".jpg");
            Uri uri = Uri.fromFile(file);

            Picasso.with(getActivity())
                    .load(uri)
                    .fit()
                    .into(imgv_qrcode);

            imgv_qrcode.setVisibility(file.exists() ? View.VISIBLE: View.GONE);
            txvw_qrcode.setVisibility(file.exists() ? View.GONE : View.VISIBLE);

        } catch (NullPointerException e){
            txvw_qrcode.setVisibility(View.VISIBLE);
        }
    }

    private String getUserQRCode(){
        try {
            SessionManagement session = new SessionManagement(getActivity());
            HashMap<String, String> user = session.getUserDetails();
            idMember = user.get(SessionManagement.KEY_ID_MEMBER);
            qrcode = user.get(SessionManagement.KEY_QRCODE);

        } catch (Exception e){
            e.printStackTrace();
        }

        return qrcode;
    }

    private void downloadQrCode(String url){
        String path = StorageUtil.getFileDirectoryPath();

        DownloadUtil.DownloadImage(getActivity(), url, path, idMember);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               loadQRCode();
            }
        }, 5000);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_qrcode, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share_qrcode){
            share(idMember);
        }

        return super.onOptionsItemSelected(item);
    }
}
