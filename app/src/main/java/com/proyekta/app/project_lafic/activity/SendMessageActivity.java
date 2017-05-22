package com.proyekta.app.project_lafic.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.model.Pesan;
import com.proyekta.app.project_lafic.util.Util;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessageActivity extends AppCompatActivity {

    private String memberId;
    private String namaMember;
    private String telpMember;
    private TextView txtv_from;
    private TextView txtv_to;
    private TextView txtv_subject;
    private EditText edtx_message;
    private ApiInterface client;

    private String user_id;
    private String user_nama;
    private String user_telp;
    private String jenis_pesan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        getSupportActionBar().setTitle("SEND MESSAGE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();
        setDataUser();

        client = ApiClient.createService(ApiInterface.class, Util.getToken(this));

        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        memberId = user.get(SessionManagement.KEY_ID_MEMBER);
        namaMember = user.get(SessionManagement.KEY_NAMA);
        telpMember = user.get(SessionManagement.KEY_TELEPON);

        txtv_from.setText(namaMember);
        txtv_to.setText(user_nama);
        txtv_subject.setText(jenis_pesan);
    }

    private void initComponents(){
        txtv_from = (TextView)findViewById(R.id.txtv_from);
        txtv_to = (TextView)findViewById(R.id.txtv_to);
        txtv_subject = (TextView)findViewById(R.id.txtv_subject);
        edtx_message = (EditText) findViewById(R.id.edtx_message);
    }

    private void setDataUser(){
        user_id = getIntent().getStringExtra("member_id");
        user_nama = getIntent().getStringExtra("nama");
        user_telp = getIntent().getStringExtra("telp");
        jenis_pesan = getIntent().getStringExtra("jenis_pesan");
    }

    private Pesan getDataMessage(){
        Pesan pesan = new Pesan();
        pesan.setMEMBER_ID(user_id);
        pesan.setKEPADA(user_nama);
        pesan.setPENGIRIM_ID(memberId);
        pesan.setPENGIRIM(namaMember);
        pesan.setJUDUL_PESAN(txtv_subject.getText().toString());
        pesan.setISI_PESAN(edtx_message.getText().toString());

        return pesan;
    }

    private void sendMessage(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Call<Pesan> call = client.sendMessage(getDataMessage());
        call.enqueue(new Callback<Pesan>() {
            @Override
            public void onResponse(Call<Pesan> call, Response<Pesan> response) {
                if (response.isSuccessful()){
                    Toast.makeText(SendMessageActivity.this, "Pesan berhasil terkirim", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SendMessageActivity.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Pesan> call, Throwable t) {
                Toast.makeText(SendMessageActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+user_telp));
                startActivity(intent);
                break;
            case R.id.action_send:
                sendMessage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
