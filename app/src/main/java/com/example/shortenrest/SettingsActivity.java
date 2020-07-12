package com.example.shortenrest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    EditText api;
    EditText domain;
    Button saveBtn;

    Button okBtn;
    TextView dialogMsg, domainSet;

    final Credentials credentials = new Credentials();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        api = findViewById(R.id.apiKey);
        domain = findViewById(R.id.domain);
        saveBtn = findViewById(R.id.saveBtn);
        domainSet = findViewById(R.id.domainSet);



        if (!SplashScreen.isFirstTime) {

            api.setText(credentials.getAPI_KEY());
            domain.setText(credentials.getDomain());

            //no back button when its the first run
            Toolbar toolbar=findViewById(R.id.settings_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    credentials.setAPI_KEY(api.getText().toString());
                    if (domain.getText().toString().isEmpty()) {
                        credentials.setDomain("short.fyi");
                    }else {
                        credentials.setDomain(domain.getText().toString());
                    }
                    Toast.makeText(getApplicationContext(), "Settings has been saved successfully.", Toast.LENGTH_LONG).show();
                }

                if (SplashScreen.isFirstTime) {

                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                }
            }
        });




    } //end onCreate

    //function that  validats the entered fields
    public boolean validate(){

        String apiKey = api.getText().toString();
        String dom = domain.getText().toString();

        //ensure not empty
        if ( apiKey.isEmpty()) {
            createDialog("Missing field, please fill in all fields and try again.");
            return false;
        }

        //check the domain is a valid regular expression
        if (!isValidDomain(dom)) {
            createDialog("Invalid format, please enter a valid format and try again.");
            return false;
        }

        //check if api key exists in the sys?
        return true;

    } //end validate

    // Function to validate domain name.
    public static boolean isValidDomain(String str)
    {
        // Regex to check valid domain name.
        String regex = "^((?!-)[A-Za-z0-9-]"
                + "{1,63}(?<!-)\\.)"
                + "+[A-Za-z]{2,6}";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null) {
            return true; //we use default domain 'short.fyi'
        }

        // Pattern class contains matcher()
        // method to find the matching
        // between the given string and
        // regular expression.
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }//end of onSupportNavigateUp


    //function that creates the custom dialog by receiving the needed message
    private void createDialog(String message){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        okBtn=dialog.findViewById(R.id.ok_btn_alert_dialog);
        //  cancelBtn=dialog.findViewById(R.id.cancel_btn_dialog);
        dialogMsg=dialog.findViewById(R.id.dialog_message);


        dialogMsg.setText(message);

        okBtn.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         dialog.cancel();
                                     }//end of onClick
                                 }//end of OnClickListener
        );



        dialog.show();

    }//end of createDialog
}
