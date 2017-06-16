package com.proyekta.app.project_lafic.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.helper.BarangStatusAmanHelper;
import com.proyekta.app.project_lafic.helper.KategoriBarangHelper;
import com.proyekta.app.project_lafic.helper.SubKategoriBarangHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.Foto;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.util.ImageUtil;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditItemActivity extends AppCompatActivity {

    private static final String TAG = "EditItemActivity";
    private int SELECT_FILE = 1;

    private RelativeLayout rlly_foto;
    private ImageView imgv_barang;
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
    private List<Barang> listBarang;
    private ProgressDialog dialog;
    private String path_gallery = "-1";
    private String barang_id = "";
    private String member_id = "";
    private String foto = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        getSupportActionBar().setTitle("EDIT ITEM");
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

        listBarang = BarangHelper.getBarang();

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

        rlly_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        loadDataBarang();

    }

    private void initComponents(){
        rlly_foto = (RelativeLayout) findViewById(R.id.rlly_foto);
        imgv_barang = (ImageView) findViewById(R.id.imgv_barang);
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

    private void loadDataBarang(){

        barang_id = getIntent().getStringExtra("barang_id");
        member_id = getIntent().getStringExtra("member_id");
        foto = getIntent().getStringExtra("foto");

        Picasso.with(this)
                .load(ApiClient.BASE_URL_FOTO + getIntent().getStringExtra("foto"))
                .error(R.drawable.ic_image)
                .fit()
                .into(imgv_barang);

        spinner_id_kategori.setSelection(Integer.parseInt(getIntent().getStringExtra("id_kategori")));
        spinner_sub_kategori.setSelection(Util.getIndex(spinner_sub_kategori, getIntent().getStringExtra("jenis_barang")));
        edtx_merk.setText(getIntent().getStringExtra("merk_barang"));
        edtx_warna.setText(getIntent().getStringExtra("warna_barang"));
        edtx_tipe.setText(getIntent().getStringExtra("keterangan"));
    }

    private String getMemberId(){
        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, SELECT_FILE);
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

    private void doSubmit(){
        String id_kategori = idKategoriBarang;
        String jenis = spinner_sub_kategori.getSelectedItem().toString();
        String merk = edtx_merk.getText().toString();
        String warna = edtx_warna.getText().toString();
        String tipe = edtx_tipe.getText().toString();

        if (!id_kategori.trim().isEmpty() &&
                !jenis.equals("Pilih Jenis Barang")){

            Barang item = new Barang();
            item.setBARANG_ID(barang_id);
            item.setMEMBER_ID(member_id);
            item.setID_KATEGORY(id_kategori);
            item.setMERK_BARANG(merk);
            item.setJENIS_BARANG(jenis);
            item.setWARNA_BARANG(warna);
            item.setKETERANGAN(tipe);
            item.setSTATUS("AMAN");
            if (!path_gallery.equals("-1")){
                uploadFoto(barang_id, path_gallery, item);
            } else {
                item.setFOTO(foto);
                doUpdateBarang(item);
            }

        } else {
            Toast.makeText(EditItemActivity.this, "please complete your data items", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFoto(String id, String filePath, final Barang item){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        File file = new File(filePath);
        //reduce image size
        File image = ImageUtil.ImageResizer(file) == null ? file : ImageUtil.ImageResizer(file);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody idBarang = RequestBody.create(MediaType.parse("text/plain"), id);

        Call<Barang> call = client.uploadFotoBarang(body, idBarang);
        call.enqueue(new Callback<Barang>() {
            @Override
            public void onResponse(Call<Barang> call, Response<Barang> response) {
                if (response.isSuccessful()){
                    item.setFOTO(response.body().getFOTO());
                    doUpdateBarang(item);
                } else {
                    Toast.makeText(EditItemActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Barang> call, Throwable t) {
                Toast.makeText(EditItemActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void doUpdateBarang(final Barang barang){

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<Barang> call = client.doUpdateBarang(barang);
        call.enqueue(new Callback<Barang>() {
            @Override
            public void onResponse(Call<Barang> call, Response<Barang> response) {
                if (response.isSuccessful()){
                    loadBarang(getMemberId());
                } else {
                    Toast.makeText(EditItemActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Barang> call, Throwable t) {
                Toast.makeText(EditItemActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
                    List<Barang> barang = response.body();
                    for (Barang data : barang){
                        listBarang.add(data);
                    }
                } else {
                    Toast.makeText(EditItemActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<List<Barang>> call, Throwable t) {
                Toast.makeText(EditItemActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == SELECT_FILE){
                if (data != null){
                    try {
                        Uri uriImage = data.getData();
                        String[] filePath = {MediaStore.Images.Media.DATA};

                        Cursor cursor = this.getContentResolver().query(uriImage, filePath, null, null, null);
                        if (cursor != null){
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePath[0]);
                            path_gallery = cursor.getString(columnIndex);
                            cursor.close();
                        }

                        Picasso.with(this)
                                .load(uriImage)
                                .error(R.drawable.ic_image)
                                .fit()
                                .into(imgv_barang);

                    } catch (Exception e){
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }
        }
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
