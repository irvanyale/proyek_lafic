package com.proyekta.app.project_lafic.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.model.Member;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */
public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity";

    private RelativeLayout rlly_qrcode;
    private Button bttn_scan;
    private DecoratedBarcodeView scanner;
    private BeepManager beepManager;
    private AlertDialog alertDialog;

    private int CAMERA_PERMISSION_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        getSupportActionBar().setTitle("SCAN ITEM");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rlly_qrcode = (RelativeLayout)findViewById(R.id.rlly_qrcode);
        bttn_scan = (Button)findViewById(R.id.bttn_scan);
        scanner = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        beepManager = new BeepManager(this);

        bttn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bttn_scan.getText().equals("SCAN")){
                    bttn_scan.setText("STOP");
                    scanner.setVisibility(View.VISIBLE);
                    rlly_qrcode.setVisibility(View.GONE);
                    scanner.resume();
                } else {
                    bttn_scan.setText("SCAN");
                    scanner.setVisibility(View.GONE);
                    rlly_qrcode.setVisibility(View.VISIBLE);
                    scanner.pause();
                }
            }
        });

        scanner.decodeContinuous(callback);
        scanner.setStatusText("");

        scanner.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.pause();
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanner.resume();
                }
            }, 1000);

            beepManager.setVibrateEnabled(true);
            beepManager.playBeepSoundAndVibrate();

            if (result != null){
                if (result.getResult() == null){
                    Toast.makeText(ScanActivity.this, "Result Not Found", Toast.LENGTH_LONG).show();
                } else {
                    try{
                        JSONObject obj = new JSONObject(result.getResult().toString());

                        Log.d(TAG, "barcodeResult: "+obj.toString());

                        bttn_scan.setText("SCAN");
                        scanner.setVisibility(View.GONE);
                        rlly_qrcode.setVisibility(View.VISIBLE);
                        scanner.pause();

                        Member member = new Member();
                        member.setMEMBER_ID(obj.getString("MEMBER_ID"));
                        member.setNAMA_MEMBER(obj.getString("NAMA_MEMBER"));
                        member.setTELEPON(obj.getString("TELEPON"));
                        member.setEMAIL_MEMBER(obj.getString("EMAIL_MEMBER"));
                        member.setNOMOR_ID(obj.getString("NOMOR_ID"));

                        showDialogSendMessage(member);

                    } catch (JSONException e){
                        e.printStackTrace();
                        Log.d(TAG, "Result "+result.getResult().toString());
                        Toast.makeText(ScanActivity.this, "There is an error", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private void showDialogSendMessage(final Member member){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScanActivity.this);

        alertDialogBuilder.setTitle("Would you like send message to  "+member.getNAMA_MEMBER()+"?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ScanActivity.this, SendMessageActivity.class);
                        intent.putExtra("member_id", member.getMEMBER_ID());
                        intent.putExtra("nama", member.getNAMA_MEMBER());
                        intent.putExtra("telp", member.getTELEPON());
                        intent.putExtra("email", member.getEMAIL_MEMBER());
                        intent.putExtra("no_id", member.getNOMOR_ID());
                        startActivity(intent);
                        alertDialog.dismiss();
                        finish();
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
