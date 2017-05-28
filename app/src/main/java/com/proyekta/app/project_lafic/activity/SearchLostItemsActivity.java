package com.proyekta.app.project_lafic.activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.activity.adapter.SearchLostItemsAdapter;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.helper.BarangHilangHelper;
import com.proyekta.app.project_lafic.helper.KategoriBarangHelper;
import com.proyekta.app.project_lafic.helper.PencarianBarangHilangHelper;
import com.proyekta.app.project_lafic.helper.SubKategoriBarangHelper;
import com.proyekta.app.project_lafic.helper.UserBarangPenemuanHelper;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.model.BarangPenemuan;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchLostItemsActivity extends AppCompatActivity {

    private RecyclerView rv_listItem;
    private SwipeRefreshLayout refresh;
    private Spinner spinner_id_kategori;
    private Spinner spinner_sub_kategori;
    private View divide_spinner;
    private List<KategoriBarang> listKategoriBarang;
    private List<String> kategoriBarang = new ArrayList<>();
    private List<String> listNamaKategoriBarang = new ArrayList<>();
    private List<String> listIdKategoriBarang = new ArrayList<>();
    private List<BarangHilang> listPencarianBarangHilang;
    private String idKategoriBarang = "";
    private ApiInterface client;
    private ProgressDialog dialog;
    private SearchLostItemsAdapter searchLostItemsAdapter;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lost_items);

        getSupportActionBar().setTitle("Search Lost Items");
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

        listPencarianBarangHilang = PencarianBarangHilangHelper.getPencarianBarangHilang();

        searchLostItemsAdapter = new SearchLostItemsAdapter(this, listPencarianBarangHilang);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(searchLostItemsAdapter);

        searchLostItemsAdapter.setOnSendMessageListener(new SearchLostItemsAdapter.setOnSendMessageListener() {
            @Override
            public void OnSendMessageListener(Member member) {
                showDialogSendMessage(member);
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
                    loadDataPencarianBarangHilangByCategory(spinner_sub_kategori.getSelectedItem().toString());

                } else {
                    loadDataPencarianBarangHilangByCategory(spinner_sub_kategori.getSelectedItem().toString());
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

    private String getIdMember(){
        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private void refreshData(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadDataPencarianBarangHilangByKeyword("");
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

    private void loadDataPencarianBarangHilangByCategory(String kategori){

        if (refresh != null){
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        Call<List<BarangHilang>> call = client.getAllBarangHilangByCategory(kategori.replace(" ", "%20"));
        call.enqueue(new Callback<List<BarangHilang>>() {
            @Override
            public void onResponse(Call<List<BarangHilang>> call, Response<List<BarangHilang>> response) {
                if (response.isSuccessful()){
                    listPencarianBarangHilang.clear();
                    for (BarangHilang item : response.body()){
                        listPencarianBarangHilang.add(item);
                    }
                    searchLostItemsAdapter.setList(listPencarianBarangHilang);
                } else {
                    Toast.makeText(SearchLostItemsActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<BarangHilang>> call, Throwable t) {
                Toast.makeText(SearchLostItemsActivity.this, "connection problem", Toast.LENGTH_SHORT).show();

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    private void loadDataPencarianBarangHilangByKeyword(String keyword){

        if (refresh != null){
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        Call<List<BarangHilang>> call = client.getAllBarangHilangByQuery(keyword);
        call.enqueue(new Callback<List<BarangHilang>>() {
            @Override
            public void onResponse(Call<List<BarangHilang>> call, Response<List<BarangHilang>> response) {
                if (response.isSuccessful()){
                    listPencarianBarangHilang.clear();
                    for (BarangHilang item : response.body()){
                        listPencarianBarangHilang.add(item);
                    }
                    searchLostItemsAdapter.setList(listPencarianBarangHilang);
                } else {
                    Toast.makeText(SearchLostItemsActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<BarangHilang>> call, Throwable t) {
                Toast.makeText(SearchLostItemsActivity.this, "connection problem", Toast.LENGTH_SHORT).show();

                if (refresh != null){
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    private void showDialogSendMessage(final Member member){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchLostItemsActivity.this);

        alertDialogBuilder.setTitle("would you like to send message to "+member.getNAMA_MEMBER()+"?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SearchLostItemsActivity.this, SendMessageActivity.class);
                        intent.putExtra("member_id", member.getMEMBER_ID());
                        intent.putExtra("nama", member.getNAMA_MEMBER());
                        intent.putExtra("telp", member.getTELEPON());
                        intent.putExtra("email", member.getEMAIL_MEMBER());
                        intent.putExtra("no_id", member.getNOMOR_ID());
                        intent.putExtra("jenis_pesan", "FOUND ITEM");
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
                loadDataPencarianBarangHilangByKeyword(query);
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
