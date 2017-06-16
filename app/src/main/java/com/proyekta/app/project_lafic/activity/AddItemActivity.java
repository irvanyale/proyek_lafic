package com.proyekta.app.project_lafic.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.proyekta.app.project_lafic.model.Foto;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.DownloadUtil;
import com.proyekta.app.project_lafic.util.ImageUtil;
import com.proyekta.app.project_lafic.util.StorageUtil;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

public class AddItemActivity extends AppCompatActivity {

    private static final String TAG = "AddItemActivity";
    private int SELECT_FILE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private Uri fileUri;

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
    private Button btn_imei;
    private Button btn_submit;
    private String memberId;
    private String memberNama;
    private String idKategoriBarang = "";
    private ApiInterface client;
    private List<KategoriBarang> listKategoriBarang;
    private List<String> kategoriBarang = new ArrayList<>();
    private List<String> listNamaKategoriBarang = new ArrayList<>();
    private List<String> listIdKategoriBarang = new ArrayList<>();
    private List<String> listKetKategoriBarang = new ArrayList<>();
    private List<Barang> barang;
    private ProgressDialog dialog;
    private String path_gallery = "-1";
    private String qrcode;

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
        memberNama = user.get(SessionManagement.KEY_NAMA);
        qrcode = user.get(SessionManagement.KEY_QRCODE);

        Log.d(TAG, "onCreate: "+qrcode);

        btn_imei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtx_tipe.setText(getIMEI());
            }
        });

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
                    //idKategoriBarang = listIdKategoriBarang.get(position);
                    idKategoriBarang = listKategoriBarang.get(position - 1).getID_KATEGORY();
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

        spinner_sub_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (idKategoriBarang.equals("1")){
                    if (position == 3){
                        btn_imei.setVisibility(View.VISIBLE);
                    } else {
                        btn_imei.setVisibility(View.GONE);
                    }
                } else {
                    btn_imei.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rlly_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogImage();
            }
        });

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
        btn_imei = (Button) findViewById(R.id.btn_imei);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        edtx_merk.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtx_warna.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    private void showDialogImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);

        LayoutInflater inf = LayoutInflater.from(AddItemActivity.this);
        View v = inf.inflate(R.layout.dialog_search, null);

        builder.setView(v);

        final AlertDialog ad = builder.create();

        final RadioGroup rgp_search = (RadioGroup) v.findViewById(R.id.rgp_search);
        RadioButton rbtn_lost_items = (RadioButton) v.findViewById(R.id.rbtn_lost_items);
        RadioButton rbtn_found_items = (RadioButton) v.findViewById(R.id.rbtn_found_items);
        Button btn_search = (Button) v.findViewById(R.id.btn_search);

        rbtn_lost_items.setText("Capture Image");
        rbtn_found_items.setText("Gallery");
        btn_search.setText("SUBMIT");

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (rgp_search.getCheckedRadioButtonId()){
                    case R.id.rbtn_lost_items:
                        captureImage();
                        ad.dismiss();
                        break;
                    case R.id.rbtn_found_items:
                        openGallery();
                        ad.dismiss();
                        break;
                }
            }
        });

        ad.show();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = StorageUtil.getOutputMediaFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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

    private String getMemberId(){
        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private String getIMEI(){
        String imei = "";
        try {
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = mngr.getDeviceId();
            if (imei == null) {
                WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                imei = info.getMacAddress();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        Log.d(TAG, "getIMEI: "+imei);
        return imei;
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
                !jenis.equals("Pilih Jenis Barang")){

            //Item item = new Item(memberId, id_kategori, nama, status, warna, tipe);
            submit(memberId, id_kategori, jenis, merk, "AMAN", warna, tipe);

        } else {
            Toast.makeText(AddItemActivity.this, "please complete your data items", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFoto(String id, String filePath){

        File file = new File(filePath);
        //reduce image size
        File image = ImageUtil.ImageResizer(file) == null ? file : ImageUtil.ImageResizer(file);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody idBarang = RequestBody.create(MediaType.parse("text/plain"), id);

        Call<Foto> call = client.uploadFotoBarang(body, idBarang);
        call.enqueue(new Callback<Foto>() {
            @Override
            public void onResponse(Call<Foto> call, Response<Foto> response) {
                if (response.isSuccessful()){
                    loadBarang();
                } else {
                    Toast.makeText(AddItemActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Foto> call, Throwable t) {
                Toast.makeText(AddItemActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void submit(String id, String id_kategori, String jenis, String merk, String status, String warna, String tipe){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<Barang> call = client.doSubmit("", id, id_kategori, jenis, merk, status, warna, tipe, "");
        call.enqueue(new Callback<Barang>() {
            @Override
            public void onResponse(Call<Barang> call, Response<Barang> response) {
                if (response.isSuccessful()){
                    Barang data = response.body();

                    downloadQrCode(data.getQRCODE(), data.getBARANG_ID()+"_"+data.getJENIS_BARANG());

                    if (!path_gallery.equals("-1")){
                        uploadFoto(data.getBARANG_ID(), path_gallery);
                    } else {
                        loadBarang();
                        dialog.dismiss();
                    }

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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
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
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    path_gallery = fileUri.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    imgv_barang.setImageBitmap(bitmap);

                } catch (NullPointerException e) {
                    e.printStackTrace();
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
