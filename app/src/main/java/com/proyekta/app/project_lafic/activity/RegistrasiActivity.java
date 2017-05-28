package com.proyekta.app.project_lafic.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.model.Member;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrasiActivity extends AppCompatActivity {

    private static final String TAG = "RegistrasiActivity";

    private EditText edtxt_username;
    private EditText edtxt_idnumber;
    private EditText edtxt_email;
    private EditText edtxt_telp;
    private EditText edtxt_password;
    private EditText edtxt_repassword;
    private Button btn_register;
    private TextView txtv_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        btn_register.setOnClickListener(_handlerClick);
        txtv_login.setOnClickListener(_handlerClick);
    }

    private void initComponents(){
        edtxt_username = (EditText) findViewById(R.id.edtxt_username);
        edtxt_idnumber = (EditText) findViewById(R.id.edtxt_idnumber);
        edtxt_email = (EditText) findViewById(R.id.edtxt_email);
        edtxt_telp = (EditText) findViewById(R.id.edtxt_telp);
        edtxt_password = (EditText) findViewById(R.id.edtxt_password);
        edtxt_repassword = (EditText) findViewById(R.id.edtxt_repassword);
        btn_register = (Button) findViewById(R.id.btn_register);
        txtv_login = (TextView) findViewById(R.id.txtv_login);
    }

    private void doRegister(){
        String name = edtxt_username.getText().toString();
        String id = edtxt_idnumber.getText().toString();
        String email = edtxt_email.getText().toString();
        String telp = edtxt_telp.getText().toString();
        String password = edtxt_password.getText().toString();
        String repassword = edtxt_repassword.getText().toString();

        if (!name.trim().isEmpty() &&
                !id.trim().isEmpty() &&
                !email.trim().isEmpty() &&
                !telp.trim().isEmpty() &&
                !password.trim().isEmpty() &&
                !repassword.trim().isEmpty()){
            if (password.equals(repassword)){
                //Member member = new Member(name, password, email, telp, "", id);
                register(name, password, email, telp, "", id);
            } else {
                Toast.makeText(RegistrasiActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegistrasiActivity.this, "please complete your data", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(String nama, String password, String email, String telp, String kelamin, String nomorId){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<Member> call = api.doRegister(nama, password, email, telp, "", nomorId, "");
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.isSuccessful()){
                    showDialog();
                } else {
                    Toast.makeText(RegistrasiActivity.this, "There is an error", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Toast.makeText(RegistrasiActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrasiActivity.this);

        LayoutInflater inf = LayoutInflater.from(RegistrasiActivity.this);
        View v = inf.inflate(R.layout.dialog_register, null);

        builder.setView(v);

        final AlertDialog ad = builder.create();

        RelativeLayout rvly_next = (RelativeLayout)v.findViewById(R.id.rvly_next);

        rvly_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrasiActivity.this, LoginActivity.class));
                finish();
            }
        });

        ad.show();
    }

    private View.OnClickListener _handlerClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_register:
                    doRegister();
                    break;
                case R.id.txtv_login:
                    startActivity(new Intent(RegistrasiActivity.this, LoginActivity.class));
                    finish();
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
}
