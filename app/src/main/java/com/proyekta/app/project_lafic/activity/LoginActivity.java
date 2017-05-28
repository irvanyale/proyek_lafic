package com.proyekta.app.project_lafic.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.proyekta.app.project_lafic.Application;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.api.AuthClient;
import com.proyekta.app.project_lafic.helper.KategoriBarangHelper;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Login;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.model.Token;
import com.proyekta.app.project_lafic.util.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button btn_login;
    private Button btn_sign;
    private CheckBox chbox_login;
    private EditText edtxt_username;
    private EditText edtxt_password;
    private AuthClient client;
    private SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = ApiClient.createService(AuthClient.class);

        session = new SessionManagement(LoginActivity.this);

        session.checkKeepLogin();

        initComponents();

        btn_login.setOnClickListener(_handlerClick);
        btn_sign.setOnClickListener(_handlerClick);

        chbox_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                session.createKeepLogin(isChecked);
            }
        });

    }

    private void initComponents(){
        edtxt_username = (EditText) findViewById(R.id.edtxt_username);
        edtxt_password = (EditText) findViewById(R.id.edtxt_password);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_sign = (Button)findViewById(R.id.btn_sign);
        chbox_login = (CheckBox) findViewById(R.id.chbox_login);
    }

    private void doLogin(){
        String username = edtxt_username.getText().toString();
        String password = edtxt_password.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()){
            login(username, password);
        } else {
            Toast.makeText(LoginActivity.this, "Username dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    private void login(String username, String password){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<Login> call = client.createToken(username, password);
        Log.d(TAG, "login: "+username+" "+password);

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                Log.d(TAG, "onResponse: "+response.body());
                if (response.isSuccessful()){
                    Login data = response.body();
                    Application.token = data.getToken();
                    Util.setToken(LoginActivity.this, data.getToken());
                    Log.d(TAG, "TOKEN COY: "+data.getToken());
                    session.createLoginSession(
                            data.getId_member(),
                            data.getNama(),
                            data.getPassword(),
                            data.getEmail(),
                            data.getTelepon(),
                            data.getKelamin(),
                            data.getNomor_id(),
                            data.getStatus(),
                            data.getFoto(),
                            data.getQrcode());
                    session.checkLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
                    Log.e("Error Code", String.valueOf(response.code()));
                    Log.e("Error Body", response.errorBody().toString());
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private View.OnClickListener _handlerClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_login:
                    hideKeyboard(v);
                    doLogin();
                    break;
                case R.id.btn_sign:
                    startActivity(new Intent(LoginActivity.this, RegistrasiActivity.class));
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
