package com.example.shortenrest;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;




public class EditAliasFrag extends Fragment {

    EditText destURL;
    EditText domainEdit;
    EditText shortURL;
    Button searchBtn;
    Button saveBtn;
    TextView domainTV, destTV;

    Credentials credentials = new Credentials();
    String domain = credentials.getDomain();
    String apiKey = credentials.getAPI_KEY();

    Button okBtn,cancelBtn;
    TextView dialogMsg;
    private Context mContext;

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public EditAliasFrag() {
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
        return inflater.inflate(R.layout.fragment_edit_alias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shortURL = view.findViewById(R.id.shortEdit); //make sure it is without '/' in  the beginning
        searchBtn = view.findViewById(R.id.searchBtn);
        destURL = view.findViewById(R.id.destURL);
        domainEdit = view.findViewById(R.id.domainEdit);
        saveBtn = view.findViewById(R.id.saveChngs);
        domainTV = view.findViewById(R.id.domainTextView);
        destTV = view.findViewById(R.id.destTextView);


        destURL.setVisibility(View.INVISIBLE);
        domainEdit.setVisibility(View.INVISIBLE);
        saveBtn.setVisibility(View.INVISIBLE);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    String aliasName = getAliasName(shortURL.getText().toString());

                    getAlias(aliasName);
                }
            }
        });

    } //end onViewCreated


    public boolean validate(){

        String url = shortURL.getText().toString();

        //check if url is empty
        if (url.isEmpty()) {
            createDialog("Missing Short URL field, please fill in field and try again.");
            return false;
        }


        //check if input is too short compared to domain?
        if (url.length() < domain.length()) {
            //create dialog
            createDialog("Alias is not found, please enter a valid alias and try again.");
            return false;
        }

        return true;
    }


    public String getAliasName(String shortURL){

        shortURL.replace("https://","");
        Log.d("test1", "short: "+ shortURL);

        int domainLength = domain.length();

        shortURL = shortURL.substring(domainLength+1);
        Log.d("test2", "short: "+ shortURL);

        return shortURL;

    }


    public void getAlias(String aliasName){

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url("https://api.shorten.rest/aliases?aliasName="+aliasName)
                .method("GET",null)
                .addHeader("x-api-key", "e9896260-b45b-11ea-9ec4-b1aa9a0ed929") //later change take api from class
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            Log.d("HERE!","response is :"+response);

            String json = response.body().string();
            Log.d("json","json:"+json);
            JSONObject jsonObj = new JSONObject(json);

            if (jsonObj.getString("name").isEmpty()) {
                createDialog("Alias is not found, please enter a valid alias and try again.");
            }

            else {


                //displaying domain
                String domainName = jsonObj.getString("domainName");
                domainEdit.setVisibility(View.VISIBLE);
                domainTV.setVisibility(View.VISIBLE);
                domainEdit.setText(domainName);

                //displaying destination
                int length = jsonObj.getJSONArray("destinations").length(); //come back...
                Log.d("herehh", "len:" + length);

                JSONObject object = jsonObj.getJSONArray("destinations").getJSONObject(0);
                String destinatonUrl = object.getString("url");
                Log.d("tesssst", "url:" + destinatonUrl);


                destTV.setVisibility(View.VISIBLE);
                destURL.setVisibility(View.VISIBLE);
                destURL.setText(destinatonUrl);

            } //end else

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            if (e.getMessage().equals("Value null of type org.json.JSONObject$1 cannot be converted to JSONObject")) {
                createDialog("Alias is not found, please enter a valid alias and try again.");
            }
            e.printStackTrace();
        }



    } //end getAlias


    public void editShortURL(){

    }


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
