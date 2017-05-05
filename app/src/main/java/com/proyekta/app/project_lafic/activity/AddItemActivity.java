package com.proyekta.app.project_lafic.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.proyekta.app.project_lafic.Application;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.api.AuthClient;
import com.proyekta.app.project_lafic.model.Item;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.DownloadUtil;
import com.proyekta.app.project_lafic.util.StorageUtil;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity {

    private static final String TAG = "AddItemActivity";

    private TextInputEditText edtx_id_kategori;
    private TextInputEditText edtx_nama;
    private TextInputEditText edtx_warna;
    private TextInputEditText edtx_tipe;
    private TextInputEditText edtx_status;
    private Button btn_submit;
    private String memberId;
    private ApiInterface client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        getSupportActionBar().setTitle("ADD ITEM");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        client = ApiClient.createService(ApiInterface.class, Application.token);

        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        memberId = user.get(SessionManagement.KEY_ID_MEMBER);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });
    }

    private void initComponents(){
        edtx_id_kategori = (TextInputEditText) findViewById(R.id.edtx_id_kategori);
        edtx_nama = (TextInputEditText) findViewById(R.id.edtx_nama);
        edtx_warna = (TextInputEditText) findViewById(R.id.edtx_warna);
        edtx_tipe = (TextInputEditText) findViewById(R.id.edtx_tipe);
        edtx_status = (TextInputEditText) findViewById(R.id.edtx_status);
        btn_submit = (Button) findViewById(R.id.btn_submit);
    }

    private void doSubmit(){
        String id_kategori = edtx_id_kategori.getText().toString();
        String nama = edtx_nama.getText().toString();
        String warna = edtx_warna.getText().toString();
        String tipe = edtx_tipe.getText().toString();
        String status = edtx_status.getText().toString();

        Log.d(TAG, "doSubmit: "+id_kategori);

        if (!id_kategori.trim().isEmpty() &&
                !nama.trim().isEmpty() &&
                !warna.trim().isEmpty() &&
                !tipe.trim().isEmpty() &&
                !status.trim().isEmpty()){

            //Item item = new Item(memberId, id_kategori, nama, status, warna, tipe);
            submit(memberId, id_kategori, nama, status, warna, tipe);

        } else {
            Toast.makeText(AddItemActivity.this, "Silahkan lengkapi data barang Anda", Toast.LENGTH_SHORT).show();
        }
    }

    private void submit(String id, String id_kategori, String nama, String status, String warna, String tipe){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<Item> call = client.doSubmit("", id, id_kategori, nama, status, warna, tipe,"");
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response.isSuccessful()){
                    Item data = response.body();

                    downloadQrCode(data.getQRCODE(), data.getBARANG_ID());

                    finish();
                } else {
                    Toast.makeText(AddItemActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);
                Toast.makeText(AddItemActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });
    }

    private void downloadQrCode(String url, String filename){
        String path = StorageUtil.getFileDirectoryPath();
        Log.d(TAG, "PATH: "+path);

        DownloadUtil.DownloadImage(this, url, path, filename);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
