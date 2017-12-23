package com.weather.photo.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.weather.photo.R;
import com.weather.photo.cache.CacheManager;
import com.weather.photo.sharedpref.SharedPreferenceManager;

/**
 * @author Mohamed Saleh
 */

public class SplashActivity extends AppCompatActivity {

    private final static int SPLASH_TIME = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CacheManager.getInstance();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME);
    }
}
