package com.proyekta.app.project_lafic.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proyekta.app.project_lafic.Application;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.HomeFragment;
import com.proyekta.app.project_lafic.fragment.ManageItemsFragment;
import com.proyekta.app.project_lafic.fragment.MessagesFragment;
import com.proyekta.app.project_lafic.fragment.ProfileFragment;
import com.proyekta.app.project_lafic.fragment.QRCodeFragment;
import com.proyekta.app.project_lafic.fragment.UserFoundItemsFragment;
import com.proyekta.app.project_lafic.fragment.UserLostItemsFragment;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.helper.BarangHilangHelper;
import com.proyekta.app.project_lafic.helper.KategoriBarangHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */
public class BerandaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BerandaActivity";

    private CircleImageView imgv_user;
    private NavigationView navigationView;
    private SessionManagement session;
    private TextView txtv_nama;
    private TextView txtv_email;
    private Toolbar toolbar;
    private ApiInterface client;
    private List<KategoriBarang> kategoriBarang;
    private List<Barang> barang;
    private List<BarangHilang> barangHilang;
    private ProgressDialog dialog;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Beranda");

        initComponents();

        getUserAccount();

        kategoriBarang = KategoriBarangHelper.getKategoriBarang();
        kategoriBarang.clear();

        barang = BarangHelper.getBarang();
        barang.clear();

        barangHilang = BarangHilangHelper.getBarangHilang();
        barangHilang.clear();

        client = ApiClient.createService(ApiInterface.class, Util.getToken(this));

        loadBarang();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initComponents(){
        View headerLayout = navigationView.getHeaderView(0);
        imgv_user = (CircleImageView) headerLayout.findViewById(R.id.imgv_user);
        txtv_nama = (TextView) headerLayout.findViewById(R.id.txtv_nama);
        txtv_email = (TextView) headerLayout.findViewById(R.id.txtv_email);
    }

    public void getUserAccount(){
        try {
            SessionManagement session = new SessionManagement(this);
            HashMap<String, String> user = session.getUserDetails();
            String nama = user.get(SessionManagement.KEY_NAMA);
            String email = user.get(SessionManagement.KEY_EMAIL);
            String foto = user.get(SessionManagement.KEY_FOTO);

            txtv_nama.setText(nama);
            txtv_email.setText(email);

            Picasso.with(this)
                    .load(ApiClient.BASE_URL_FOTO + foto)
                    .error(R.drawable.ic_profile_circle_white)
                    .fit()
                    .into(imgv_user);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadBarang(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<List<Barang>> call = client.getAllBarang(getMemberId());
        call.enqueue(new Callback<List<Barang>>() {
            @Override
            public void onResponse(Call<List<Barang>> call, Response<List<Barang>> response) {
                if (response.isSuccessful()){
                    List<Barang> listBarang = response.body();
                    for (Barang data : listBarang){
                        barang.add(data);
                    }
                    loadKategoriBarang();
                } else {
                    Toast.makeText(BerandaActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Barang>> call, Throwable t) {
                Toast.makeText(BerandaActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void loadKategoriBarang(){

        Call<List<KategoriBarang>> call = client.getKategoriBarang();
        call.enqueue(new Callback<List<KategoriBarang>>() {
            @Override
            public void onResponse(Call<List<KategoriBarang>> call, Response<List<KategoriBarang>> response) {
                if (response.isSuccessful()){
                    List<KategoriBarang> listKategoriBarang = response.body();
                    for (KategoriBarang data : listKategoriBarang){
                        kategoriBarang.add(data);
                    }
                    loadBarangHilang();
                } else {
                    Toast.makeText(BerandaActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<KategoriBarang>> call, Throwable t) {
                Toast.makeText(BerandaActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void loadBarangHilang(){

        Call<List<BarangHilang>> call = client.getAllBarangHilang();
        call.enqueue(new Callback<List<BarangHilang>>() {
            @Override
            public void onResponse(Call<List<BarangHilang>> call, Response<List<BarangHilang>> response) {
                if (response.isSuccessful()){
                    List<BarangHilang> listBarangHilang = response.body();
                    for (BarangHilang data : listBarangHilang){
                        barangHilang.add(data);
                    }
                    replaceFragment(new HomeFragment());
                } else {
                    Toast.makeText(BerandaActivity.this, "Data failed to load", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<BarangHilang>> call, Throwable t) {
                Toast.makeText(BerandaActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private String getMemberId(){
        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private void showDialogLogout(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BerandaActivity.this);

        alertDialogBuilder.setTitle("Logout?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        session = new SessionManagement(BerandaActivity.this);
                        session.logoutUser();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void showDialogSearch(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BerandaActivity.this);

        LayoutInflater inf = LayoutInflater.from(BerandaActivity.this);
        View v = inf.inflate(R.layout.dialog_search, null);

        builder.setView(v);

        final AlertDialog ad = builder.create();

        final RadioGroup rgp_search = (RadioGroup) v.findViewById(R.id.rgp_search);
        Button btn_search = (Button) v.findViewById(R.id.btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (rgp_search.getCheckedRadioButtonId()){
                    case R.id.rbtn_lost_items:
                        startActivity(new Intent(BerandaActivity.this, SearchLostItemsActivity.class));
                        ad.dismiss();
                        break;
                    case R.id.rbtn_found_items:
                        startActivity(new Intent(BerandaActivity.this, SearchFoundItemsActivity.class));
                        ad.dismiss();
                        break;
                }
            }
        });

        ad.show();
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()){
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(this, "Press BACK again to Exit", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.beranda, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_scan:
                startActivity(new Intent(BerandaActivity.this, ScanActivity.class));
                break;
            case R.id.action_search:
                showDialogSearch();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        switch (id){
            case R.id.nav_home:
                fragment = new HomeFragment();
                getSupportActionBar().setTitle("Home");
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                getSupportActionBar().setTitle("Edit Profile");
                break;
            case R.id.nav_items:
                fragment = new ManageItemsFragment();
                getSupportActionBar().setTitle("Manage Items");
                break;
            case R.id.nav_lost:
                fragment = new UserLostItemsFragment();
                getSupportActionBar().setTitle("Lost Items");
                break;
            case R.id.nav_found:
                fragment = new UserFoundItemsFragment();
                getSupportActionBar().setTitle("Found Items");
                break;
            case R.id.nav_message:
                fragment = new MessagesFragment();
                getSupportActionBar().setTitle("Messages");
                break;
            case R.id.nav_qrcode:
                fragment = new QRCodeFragment();
                getSupportActionBar().setTitle("QR CODE");
                break;
            case R.id.nav_logout:
                showDialogLogout();
                break;
        }

        if (fragment != null){
            replaceFragment(fragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
