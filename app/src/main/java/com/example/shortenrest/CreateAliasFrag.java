package com.example.shortenrest;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class CreateAliasFrag extends Fragment {

    EditText longURL;
    EditText shortURL;
    Button shortenBtn;

    Button okBtn,cancelBtn;
    TextView dialogMsg;

    private Context mContext;

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public CreateAliasFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_alias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        longURL = view.findViewById(R.id.longURL);
        shortURL = view.findViewById(R.id.shortURL);
        shortenBtn = view.findViewById(R.id.shortenBtn);

        shortenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (isValid()) {
                createAlias();
             }
            }
        });

    } //end onViewCreated()


    private boolean isValid(){

        boolean isValid = false;
        String url = longURL.getText().toString();

        //ensure field is not empty
        if (url.isEmpty()) {

            createDialog("Missing Destination URL field, please fill in field and try again.");
            return isValid;
        }

        //ensure a valid URL
        // Patterns.WEB_URL.matcher(url).matches();
        if(! URLUtil.isValidUrl(url)){
            createDialog("Invalid URL format, please enter a valid URL and try again.");
            return isValid;
        }


        isValid = true;
        return isValid;

    }

    private void createAlias() {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"destinations\": [{\"url\": \""+longURL.getText().toString()+"\", \"country\": null, \"os\": null}]}");
        Request request = new Request.Builder()
                .url("https://api.shorten.rest/aliases?aliasName=/@rnd") //change dcc
                .method("POST", body)
                .addHeader("x-api-key", "e9896260-b45b-11ea-9ec4-b1aa9a0ed929") //later change take api from class
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Log.d("json","json:"+json);
            Log.d("HERE!","response is :"+response);

            JSONObject jsonObj = new JSONObject(json);
            String shortened = jsonObj.getString("shortUrl");
            Log.d("shorturl","="+shortened);
            shortURL.setText(shortened);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    } //end createAlias

    private void createDialog(String message){
        final Dialog dialog = new Dialog(mContext);
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
