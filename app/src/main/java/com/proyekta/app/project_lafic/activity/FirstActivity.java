package com.proyekta.app.project_lafic.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */
public class FirstActivity extends AppCompatActivity {

    //Permision code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;
    private boolean isValid = true;
    private int REQUEST_PERMISSION_CODE = 200;

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        initComponents();

        checkPermission();

        /*if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
            Toast.makeText(FirstActivity.this,"You already have the permission",Toast.LENGTH_LONG).show();
            //Existing the method with return
            return;
        }

        //If the app has not the permission then asking for the permission
        requestStoragePermission();*/
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
        Intent i = new Intent(this.getApplicationContext(), WelcomeScreenActivity.class);
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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                    ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.READ_PHONE_STATE) +
                    ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                isValid = false;
                ActivityCompat.requestPermissions(FirstActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA
                }, REQUEST_PERMISSION_CODE);
            }
        }
    }

    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result1 == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }

        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission granted now you can use LAFIC",Toast.LENGTH_LONG).show();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                            builder.setMessage("Some permission was denied, the apps maybe run error.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(FirstActivity.this,"Some permission was denied, the apps maybe run error.",Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.show();
                        }
                    });
                }
                return;
        }
    }
}
