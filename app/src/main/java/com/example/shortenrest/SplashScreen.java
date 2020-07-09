package com.example.shortenrest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Set;

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
                    //show start activity

                    isFirstTime = true;
                    startActivity(new Intent(SplashScreen.this, SettingsActivity.class));
                    Toast.makeText(SplashScreen.this, "First Run", Toast.LENGTH_LONG)
                            .show();
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
                    //show start activity
                    isFirstTime = true;
                    startActivity(new Intent(SplashScreen.this, SettingsActivity.class));
                    Toast.makeText(SplashScreen.this, "First Run", Toast.LENGTH_LONG)
                            .show();
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
