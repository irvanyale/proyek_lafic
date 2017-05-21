package com.proyekta.app.project_lafic.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;

import java.util.HashMap;

public class SendMessageActivity extends AppCompatActivity {

    private String memberId;
    private String namaMember;
    private String telpMember;
    private TextView txtv_from;
    private TextView txtv_to;
    private EditText edtx_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        getSupportActionBar().setTitle("SEND MESSAGE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        SessionManagement session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        memberId = user.get(SessionManagement.KEY_ID_MEMBER);
        namaMember = user.get(SessionManagement.KEY_NAMA);
        telpMember = user.get(SessionManagement.KEY_TELEPON);

        txtv_from.setText(namaMember);
        txtv_to.setText(getIntent().getStringExtra("nama"));
    }

    private void initComponents(){
        txtv_from = (TextView)findViewById(R.id.txtv_from);
        txtv_to = (TextView)findViewById(R.id.txtv_to);
        edtx_message = (EditText) findViewById(R.id.edtx_message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telpMember));
                startActivity(intent);
                break;
            case R.id.action_send:
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
