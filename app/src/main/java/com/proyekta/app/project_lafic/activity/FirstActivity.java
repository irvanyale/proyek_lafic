package com.proyekta.app.project_lafic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.proyekta.app.project_lafic.R;
/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */
public class FirstActivity extends AppCompatActivity {
    Button btnLogin, btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        initComponents();
    }

    private void initComponents(){
        btnLogin = (Button) findViewById(R.id.btnSignin);
        btnRegister = (Button) findViewById(R.id.btnJoin);
    }

    public void login(){
        Intent i = new Intent(this.getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
    public void register(){
        Intent i = new Intent(this.getApplicationContext(), RegistrasiActivity.class);
        startActivity(i);
    }

    public void btn_onClick(View view){
        Button x = (Button) view;
        if(x == btnLogin){
            login();
        }else if (x == btnRegister){
            register();
        }
    }
}
