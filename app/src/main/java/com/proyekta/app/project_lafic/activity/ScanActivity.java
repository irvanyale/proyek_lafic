package com.proyekta.app.project_lafic.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity";

    private RelativeLayout rlly_qrcode;
    private Button bttn_scan;
    private DecoratedBarcodeView scanner;
    private BeepManager beepManager;

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

    private void checkPermission(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {}
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

                        Toast.makeText(ScanActivity.this, obj.toString(), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e){
                        e.printStackTrace();
                        Log.d(TAG, "Result "+result.getResult().toString());
                        Toast.makeText(ScanActivity.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

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
