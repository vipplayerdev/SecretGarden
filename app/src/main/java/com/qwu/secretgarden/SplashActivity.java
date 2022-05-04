package com.qwu.secretgarden;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private int leftSeconds = 3;

    //time hander
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if(leftSeconds > 0){
                leftSeconds -= 1;
                handler.postDelayed(runnable, 1000);
            }else{
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                handler.removeCallbacks(runnable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(runnable, 1000);

    }
}