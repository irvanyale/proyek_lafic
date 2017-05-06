package com.proyekta.app.project_lafic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

import com.proyekta.app.project_lafic.R;

public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /** Sets a layout for this activity */
        setContentView(R.layout.activity_splash);

        /** Creates a count down timer, which will be expired after 5000 milliseconds */
        new CountDownTimer(5000,1000) {

            /** This method will be invoked on finishing or expiring the timer */
            @Override
            public void onFinish() {
                /** Creates an intent to start new activity */
                Intent intent = new Intent(getBaseContext(), FirstActivity.class);

                //memulai activity baru ketika waktu timer habis
                startActivity(intent);

                //menutup layar activity
                finish();
            }
            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
}
