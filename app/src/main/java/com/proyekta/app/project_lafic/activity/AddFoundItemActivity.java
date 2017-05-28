package com.proyekta.app.project_lafic.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.helper.BarangPenemuanHelper;
import com.proyekta.app.project_lafic.helper.KategoriBarangHelper;
import com.proyekta.app.project_lafic.helper.SubKategoriBarangHelper;
import com.proyekta.app.project_lafic.helper.UserBarangPenemuanHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.BarangPenemuan;
import com.proyekta.app.project_lafic.model.Foto;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.util.ImageUtil;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFoundItemActivity extends AppCompatActivity {

    private static final String TAG = "AddFoundItemActivity";
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
    private TextInputEditText edtx_lokasi;
    private Button btn_submit;
    private String memberId;
    private String idKategoriBarang = "";
    private ApiInterface client;
    private List<KategoriBarang> listKategoriBarang;
    private List<String> kategoriBarang = new ArrayList<>();
    private List<String> listNamaKategoriBarang = new ArrayList<>();
    private List<String> listIdKategoriBarang = new ArrayList<>();
    private List<String> listKetKategoriBarang = new ArrayList<>();
    private List<BarangPenemuan> listBarangPenemuen;
    private ProgressDialog dialog;
    private String path_gallery = "-1";

    private TextView edtx_tanggal_hilang;
    private TextView edtx_waktu_hilang;
    private android.app.DatePickerDialog DatePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_found_item);

        getSupportActionBar().setTitle("ADD FOUND ITEM");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        listKategoriBarang = KategoriBarangHelper.getKategoriBarang();
        kategoriBarang.add("-1~Pilih Kategori~-");
        for (KategoriBarang data : listKategoriBarang){
            kategoriBarang.add(data.getID_KATEGORY() + "~" + data.getJENIS() + "~" + data.getKETERANGAN());
        }

        listIdKategoriBarang = Util.getIdKategori(kategoriBarang);
        listNamaKategoriBarang = Util.getNamaKategori(kategoriBarang);
        listKetKategoriBarang = Util.getKeteranganKategori(kategoriBarang);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listNamaKategoriBarang);
        spinner_id_kategori.setAdapter(adapter);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(this));

        listBarangPenemuen = UserBarangPenemuanHelper.getUserBarangPenemuan();

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

        edtx_tanggal_hilang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.show();
            }
        });

        edtx_waktu_hilang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        showDatePicker();
        showTimePicker();

        rlly_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
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
        edtx_lokasi = (TextInputEditText) findViewById(R.id.edtx_lokasi);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        edtx_tanggal_hilang = (TextView) findViewById(R.id.edtx_tanggal_hilang);
        edtx_waktu_hilang = (TextView) findViewById(R.id.edtx_waktu_hilang);

        edtx_tanggal_hilang.setInputType(InputType.TYPE_NULL);
        edtx_waktu_hilang.setInputType(InputType.TYPE_NULL);

        edtx_merk.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtx_warna.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    private void showDatePicker(){

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(this, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                edtx_tanggal_hilang.setText(sdf.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void showTimePicker(){

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                edtx_waktu_hilang.setText(hourOfDay+":"+minute+":00");
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, SELECT_FILE);
    }

    private String getMemberId(){
        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
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
                edtx_tipe.setHint("Information(No. Seri / IMEI)");
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
        String jenis = spinner_sub_kategori.getSelectedItem().toString();
        String merk = edtx_merk.getText().toString();
        String warna = edtx_warna.getText().toString();
        String tipe = edtx_tipe.getText().toString();
        String lokasi = edtx_lokasi.getText().toString();
        String datelost = edtx_tanggal_hilang.getText().toString();
        String timelost = edtx_waktu_hilang.getText().toString();

        if (!jenis.equals("Pilih Jenis Barang")){

            BarangPenemuan barangPenemuan = new BarangPenemuan();
            barangPenemuan.setMEMBER_ID(memberId);
            barangPenemuan.setJENIS_BARANG(jenis);
            barangPenemuan.setMERK_BARANG(merk);
            barangPenemuan.setWARNA_BARANG(warna);
            barangPenemuan.setKETERANGAN(tipe);
            barangPenemuan.setFOTO_PENEMUAN("");
            barangPenemuan.setTANGGAL_KETEMU(datelost+" "+timelost);
            barangPenemuan.setLOKASI_KETEMU(lokasi);

            Log.d(TAG, "doSubmit: "+datelost+" "+timelost);

            submit(barangPenemuan);

        } else {
            Toast.makeText(AddFoundItemActivity.this, "please complete your data", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFoto(String id, String filePath){

        File file = new File(filePath);
        //reduce image size
        File image = ImageUtil.ImageResizer(file) == null ? file : ImageUtil.ImageResizer(file);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody idBarang = RequestBody.create(MediaType.parse("text/plain"), id);

        Call<Foto> call = client.uploadFotoBarangPenemuan(body, idBarang);
        call.enqueue(new Callback<Foto>() {
            @Override
            public void onResponse(Call<Foto> call, Response<Foto> response) {
                if (response.isSuccessful()){
                    loadBarangPenemuan();
                } else {
                    Toast.makeText(AddFoundItemActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Foto> call, Throwable t) {
                Toast.makeText(AddFoundItemActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void submit(final BarangPenemuan barangPenemuan){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<BarangPenemuan> call = client.postBarangPenemuan(barangPenemuan);
        call.enqueue(new Callback<BarangPenemuan>() {
            @Override
            public void onResponse(Call<BarangPenemuan> call, Response<BarangPenemuan> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "onResponsePenemuan: "+response.body().getBARANG_PENEMUAN_ID());
                    uploadFoto(response.body().getBARANG_PENEMUAN_ID(), path_gallery);
                } else {
                    Toast.makeText(AddFoundItemActivity.this, "there is an error", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<BarangPenemuan> call, Throwable t) {
                Toast.makeText(AddFoundItemActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void loadBarangPenemuan(){

        Call<List<BarangPenemuan>> call = client.getBarangPenemuanMember(getMemberId());
        call.enqueue(new Callback<List<BarangPenemuan>>() {
            @Override
            public void onResponse(Call<List<BarangPenemuan>> call, Response<List<BarangPenemuan>> response) {
                if (response.isSuccessful()){
                    listBarangPenemuen.clear();
                    List<BarangPenemuan> listItems = response.body();
                    for (BarangPenemuan data : listItems){
                        listBarangPenemuen.add(data);
                    }
                    finish();
                } else {
                    Toast.makeText(AddFoundItemActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<BarangPenemuan>> call, Throwable t) {
                Toast.makeText(AddFoundItemActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
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
