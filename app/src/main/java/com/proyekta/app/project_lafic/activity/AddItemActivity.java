package com.proyekta.app.project_lafic.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proyekta.app.project_lafic.Application;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.api.AuthClient;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.helper.KategoriBarangHelper;
import com.proyekta.app.project_lafic.helper.SubKategoriBarangHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.DownloadUtil;
import com.proyekta.app.project_lafic.util.StorageUtil;
import com.proyekta.app.project_lafic.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity {

    private static final String TAG = "AddItemActivity";

    private Spinner spinner_id_kategori;
    private Spinner spinner_sub_kategori;
    private View divide_spinner;
    private TextView txtv_kategori;
    private TextInputEditText edtx_merk;
    private TextInputEditText edtx_warna;
    private TextInputEditText edtx_tipe;
    private TextInputEditText edtx_status;
    private Button btn_submit;
    private String memberId;
    private String idKategoriBarang = "";
    private ApiInterface client;
    private List<KategoriBarang> listKategoriBarang;
    private List<String> kategoriBarang = new ArrayList<>();
    private List<String> listNamaKategoriBarang = new ArrayList<>();
    private List<String> listIdKategoriBarang = new ArrayList<>();
    private List<String> listKetKategoriBarang = new ArrayList<>();
    private List<Barang> barang;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        getSupportActionBar().setTitle("ADD ITEM");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        listKategoriBarang = KategoriBarangHelper.getKategoriBarang();
        kategoriBarang.add("-1~Pilih Kategori~-");
        for (KategoriBarang data : listKategoriBarang){
            kategoriBarang.add(data.getID_KATEGORY() + "~" + data.getJENIS() + "~" + data.getKETERANGAN());
            Log.d(TAG, "onCreate: "+kategoriBarang);
        }

        listIdKategoriBarang = Util.getIdKategori(kategoriBarang);
        listNamaKategoriBarang = Util.getNamaKategori(kategoriBarang);
        listKetKategoriBarang = Util.getKeteranganKategori(kategoriBarang);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listNamaKategoriBarang);
        spinner_id_kategori.setAdapter(adapter);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(this));

        barang = BarangHelper.getBarang();

        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        memberId = user.get(SessionManagement.KEY_ID_MEMBER);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });

        spinner_id_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    idKategoriBarang = listIdKategoriBarang.get(position);
                    Log.d(TAG, "onItemSelected: "+idKategoriBarang);

                    subKategoriAdapter(idKategoriBarang);

                } else {
                    idKategoriBarang = "";
                    subKategoriAdapter(idKategoriBarang);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initComponents(){
        spinner_id_kategori = (Spinner) findViewById(R.id.spinner_id_kategori);
        spinner_sub_kategori = (Spinner) findViewById(R.id.spinner_sub_kategori);
        divide_spinner = findViewById(R.id.divide_spinner);
        txtv_kategori = (TextView) findViewById(R.id.txtv_kategori);
        edtx_merk = (TextInputEditText) findViewById(R.id.edtx_merk);
        edtx_warna = (TextInputEditText) findViewById(R.id.edtx_warna);
        edtx_tipe = (TextInputEditText) findViewById(R.id.edtx_tipe);
        edtx_status = (TextInputEditText) findViewById(R.id.edtx_status);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        edtx_merk.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtx_warna.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    private void subKategoriAdapter(String id){
        if (!id.equals("")){
            spinner_sub_kategori.setVisibility(View.VISIBLE);
            divide_spinner.setVisibility(View.VISIBLE);
        } else {
            spinner_sub_kategori.setVisibility(View.GONE);
            divide_spinner.setVisibility(View.GONE);
        }

        //subkategori
        List<String> listSubKategori = SubKategoriBarangHelper.setListSubKategori(id);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listSubKategori);
        spinner_sub_kategori.setAdapter(adapter);

        //tampil form
        switch (id){
            case "1":
                edtx_merk.setVisibility(View.VISIBLE);
                edtx_warna.setVisibility(View.VISIBLE);
                edtx_tipe.setVisibility(View.VISIBLE);
                edtx_tipe.setHint("Information (No. Seri / IMEI)");
                break;
            case "2":
                edtx_merk.setVisibility(View.GONE);
                edtx_warna.setVisibility(View.GONE);
                edtx_tipe.setVisibility(View.VISIBLE);
                edtx_tipe.setHint("No. ID (NIM, NIK, dll)");
                break;
            case "3":
                edtx_merk.setVisibility(View.VISIBLE);
                edtx_warna.setVisibility(View.VISIBLE);
                edtx_tipe.setVisibility(View.GONE);
                break;
        }
    }

    private String getMemberId(){
        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private void doSubmit(){
        String id_kategori = idKategoriBarang;
        String jenis = spinner_sub_kategori.getSelectedItem().toString();
        String merk = edtx_merk.getText().toString();
        String warna = edtx_warna.getText().toString();
        String tipe = edtx_tipe.getText().toString();
        String status = edtx_status.getText().toString();

        Log.d(TAG, "doSubmit: "+id_kategori);

        if (!id_kategori.trim().isEmpty() &&
                !jenis.equals("Choose kind items")){

            //Item item = new Item(memberId, id_kategori, nama, status, warna, tipe);
            submit(memberId, id_kategori, jenis, merk, "SECURE", warna, tipe);

        } else {
            Toast.makeText(AddItemActivity.this, "please complete your data items", Toast.LENGTH_SHORT).show();
        }
    }

    private void submit(String id, String id_kategori, String jenis, String merk, String status, String warna, String tipe){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<Barang> call = client.doSubmit("", id, id_kategori, jenis, merk, status, warna, tipe,"");
        call.enqueue(new Callback<Barang>() {
            @Override
            public void onResponse(Call<Barang> call, Response<Barang> response) {
                if (response.isSuccessful()){
                    Barang data = response.body();

                    //downloadQrCode(data.getQRCODE(), data.getBARANG_ID());

                    loadBarang();

                } else {
                    Toast.makeText(AddItemActivity.this, "there is an error", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Barang> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);
                Toast.makeText(AddItemActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });
    }

    private void downloadQrCode(String url, String filename){
        String path = StorageUtil.getFileDirectoryPath();
        Log.d(TAG, "PATH: "+path);

        DownloadUtil.DownloadImage(this, url, path, filename);
    }

    private void loadBarang(){

        Call<List<Barang>> call = client.getAllBarang(getMemberId());
        call.enqueue(new Callback<List<Barang>>() {
            @Override
            public void onResponse(Call<List<Barang>> call, Response<List<Barang>> response) {
                if (response.isSuccessful()){
                    barang.clear();
                    List<Barang> listBarang = response.body();
                    for (Barang data : listBarang){
                        barang.add(data);
                    }
                    finish();
                } else {
                    Toast.makeText(AddItemActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Barang>> call, Throwable t) {
                Toast.makeText(AddItemActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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
