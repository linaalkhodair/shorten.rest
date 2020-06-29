package com.example.shortenrest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

public class SplashScreen extends AppCompatActivity {

    private LinearLayout splashLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashLayout=findViewById(R.id.splashlayout);

        splashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }
        });

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, secondsDelayed * 2000);
    } //end onCreate



}
