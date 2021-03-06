package com.proyekta.app.project_lafic.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.activity.adapter.SearchFoundItemsAdapter;
import com.proyekta.app.project_lafic.activity.adapter.SearchLostItemsAdapter;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.helper.KategoriBarangHelper;
import com.proyekta.app.project_lafic.helper.PencarianBarangPenemuanHelper;
import com.proyekta.app.project_lafic.helper.SubKategoriBarangHelper;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.model.BarangPenemuan;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFoundItemsActivity extends AppCompatActivity {

    private RecyclerView rv_listItem;
    private SwipeRefreshLayout refresh;
    private Spinner spinner_id_kategori;
    private Spinner spinner_sub_kategori;
    private View divide_spinner;
    private List<KategoriBarang> listKategoriBarang;
    private List<String> kategoriBarang = new ArrayList<>();
    private List<String> listNamaKategoriBarang = new ArrayList<>();
    private List<String> listIdKategoriBarang = new ArrayList<>();
    private List<BarangPenemuan> listPencarianBarangPenemuan;
    private String idKategoriBarang = "";
    private ApiInterface client;
    private ProgressDialog dialog;
    private SearchFoundItemsAdapter searchFoundItemsAdapter;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_found_items);

        getSupportActionBar().setTitle("Search Found Items");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        listKategoriBarang = KategoriBarangHelper.getKategoriBarang();
        kategoriBarang.add("-1~Choose The Category~-");
        for (KategoriBarang data : listKategoriBarang){
            kategoriBarang.add(data.getID_KATEGORY() + "~" + data.getJENIS() + "~" + data.getKETERANGAN());
        }

        listIdKategoriBarang = Util.getIdKategori(kategoriBarang);
        listNamaKategoriBarang = Util.getNamaKategori(kategoriBarang);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listNamaKategoriBarang);
        spinner_id_kategori.setAdapter(adapter);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(this));

        listPencarianBarangPenemuan = PencarianBarangPenemuanHelper.getPencarianBarangPenemuan();

        searchFoundItemsAdapter = new SearchFoundItemsAdapter(this, listPencarianBarangPenemuan);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(searchFoundItemsAdapter);

        searchFoundItemsAdapter.setOnSendMessageListener(new SearchFoundItemsAdapter.setOnSendMessageListener() {
            @Override
            public void OnSendMessageListener(Member member) {
                showDialogSendMessage(member);
            }
        });

        searchFoundItemsAdapter.setOnShowImageListener(new SearchFoundItemsAdapter.setOnShowImageListener() {
            @Override
            public void OnShowImageListener(int position) {
                showDialogImageZoom(position);
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
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

        spinner_sub_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    loadDataPencarianBarangPenemuanByCategory(spinner_sub_kategori.getSelectedItem().toString());

                } else {
                    loadDataPencarianBarangPenemuanByCategory(spinner_sub_kategori.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initComponents() {
        spinner_id_kategori = (Spinner) findViewById(R.id.spinner_id_kategori);
        spinner_sub_kategori = (Spinner) findViewById(R.id.spinner_sub_kategori);
        divide_spinner = findViewById(R.id.divide_spinner);
        rv_listItem = (RecyclerView) findViewById(R.id.rv_listItem);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
    }

    private void showDialogImageZoom(int position){
        Dialog dialog = new Dialog(this, R.style.Theme_Dialog_Fullscreen_Margin);
        dialog.setContentView(R.layout.dialog_image_zoom);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        dialog.setCanceledOnTouchOutside(true);

        PhotoView image = (PhotoView) dialog.findViewById(R.id.image);
        Picasso.with(this)
                .load(ApiClient.BASE_URL_FOTO + listPencarianBarangPenemuan.get(position).getFOTO_PENEMUAN())
                .fit()
                .into(image);

        dialog.show();
    }

    private void refreshData(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadDataPencarianBarangPenemuanByQuery("");
            }
        });
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
    }

    private void loadDataPencarianBarangPenemuanByCategory(String kategori){
        if (refresh != null){
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        Call<List<BarangPenemuan>> call = client.getBarangPenemuanByCategory(kategori.replace(" ", "%20"));
        call.enqueue(new Callback<List<BarangPenemuan>>() {
            @Override
            public void onResponse(Call<List<BarangPenemuan>> call, Response<List<BarangPenemuan>> response) {
                if (response.isSuccessful()){
                    listPencarianBarangPenemuan.clear();
                    for (BarangPenemuan item : response.body()){
                        listPencarianBarangPenemuan.add(item);
                    }
                    searchFoundItemsAdapter.setList(listPencarianBarangPenemuan);
                } else {
                    Toast.makeText(SearchFoundItemsActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<BarangPenemuan>> call, Throwable t) {
                Toast.makeText(SearchFoundItemsActivity.this, "connection problem", Toast.LENGTH_SHORT).show();

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    private void loadDataPencarianBarangPenemuanByQuery(String keyword){

        if (refresh != null){
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        Call<List<BarangPenemuan>> call = client.getBarangPenemuanByQuery(keyword);
        call.enqueue(new Callback<List<BarangPenemuan>>() {
            @Override
            public void onResponse(Call<List<BarangPenemuan>> call, Response<List<BarangPenemuan>> response) {
                if (response.isSuccessful()){
                    listPencarianBarangPenemuan.clear();
                    for (BarangPenemuan item : response.body()){
                        listPencarianBarangPenemuan.add(item);
                    }
                    searchFoundItemsAdapter.setList(listPencarianBarangPenemuan);
                } else {
                    Toast.makeText(SearchFoundItemsActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<BarangPenemuan>> call, Throwable t) {
                Toast.makeText(SearchFoundItemsActivity.this, "connection problem", Toast.LENGTH_SHORT).show();

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    private void showDialogSendMessage(final Member member){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchFoundItemsActivity.this);

        alertDialogBuilder.setTitle("would you like to send message to "+member.getNAMA_MEMBER()+"?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SearchFoundItemsActivity.this, SendMessageActivity.class);
                        intent.putExtra("member_id", member.getMEMBER_ID());
                        intent.putExtra("nama", member.getNAMA_MEMBER());
                        intent.putExtra("telp", member.getTELEPON());
                        intent.putExtra("email", member.getEMAIL_MEMBER());
                        intent.putExtra("no_id", member.getNOMOR_ID());
                        intent.putExtra("jenis_pesan", "DEMAND FOR ITEM");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadDataPencarianBarangPenemuanByQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
