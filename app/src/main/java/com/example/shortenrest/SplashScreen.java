package com.example.shortenrest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;


public class SplashScreen extends AppCompatActivity {

    private LinearLayout splashLayout;
    static boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashLayout=findViewById(R.id.splashlayout);

        final Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        splashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstRun) {
                    //this is to display settings page when first time installing the app

                    isFirstTime = true;
                    startActivity(new Intent(SplashScreen.this, SettingsActivity.class));

                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("isFirstRun", false).commit();
                } else {

                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    isFirstTime = false;
                }

            }
        });

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isFirstRun) {
                    //this is to display settings page when first time installing the app
                    isFirstTime = true;
                    startActivity(new Intent(SplashScreen.this, SettingsActivity.class));
                    finish();
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("isFirstRun", false).commit();
                }

                else{
                    isFirstTime = false;
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, secondsDelayed * 2000);
    } //end onCreate



}
