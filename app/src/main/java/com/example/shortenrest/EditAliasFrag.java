package com.example.shortenrest;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;




public class EditAliasFrag extends Fragment {

    EditText destURL;
    EditText domainEdit, snippetExample;
    EditText shortURL;
    Button searchBtn;
    Button saveBtn;
    TextView domainTV, destTV, addUtm;

    Credentials credentials = new Credentials();
    String domain = credentials.getDomain();
    String apiKey = credentials.getAPI_KEY();

    Button okBtn,cancelBtn;
    TextView dialogMsg;
    private Context mContext;

    ArrayList<ItemCard> arrayList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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
        snippetExample = view.findViewById(R.id.snippetExample);

        addUtm = view.findViewById(R.id.addUtm);
        SpannableString content = new SpannableString("Add UTM");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        addUtm.setText(content);

        buildRecyclerView(view);


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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validate not empty dest and domain
                String aliasName = getAliasName(shortURL.getText().toString());
                String destUrl = destURL.getText().toString();

                if(isValid(destUrl)){
                    editShortURL(aliasName, destUrl);
                    Toast.makeText(mContext, "Short URL has been updated successfully", Toast.LENGTH_SHORT).show();

                }

            }
        });

    } //end onViewCreated

    private void buildRecyclerView(View view){

        //for testing
        arrayList = new ArrayList<>(); //change name

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(mContext);
        adapter = new Adapter(arrayList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private boolean isValid(String url){

        boolean isValid = false;


        //ensure field is not empty
        if (url.isEmpty() || domainEdit.getText().toString().isEmpty()) {

            createDialog("Missing field, please fill in all fields and try again.");
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

    private boolean validate(){

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


    private String getAliasName(String shortURL){

        shortURL = shortURL.replace("https://","");
        Log.d("test1", "short: "+ shortURL);

        int domainLength = domain.length();

        shortURL = shortURL.substring(domainLength+1);
        Log.d("test2", "short: "+ shortURL);

        return shortURL;

    }


    private void getAlias(String aliasName){

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

                getUtm(destinatonUrl);

                 destTV.setVisibility(View.VISIBLE);
                 destURL.setVisibility(View.VISIBLE);
                 destURL.setText(destinatonUrl);
                 saveBtn.setVisibility(View.VISIBLE);

                //displaying snippets:
                    JSONArray array = jsonObj.getJSONArray("snippets");
                    getSnippet(array);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            if (e.getMessage().equals("Value null of type org.json.JSONObject$1 cannot be converted to JSONObject")) {
                createDialog("Alias is not found, please enter a valid alias and try again.");
            }
            e.printStackTrace();
        }



    } //end getAlias

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    private void getUtm(String url){

        boolean found = false;
        int index = url.indexOf("?");

        if (url.contains("?utm")) {

            while (!found) {


                char ch1 = url.charAt(index + 1);
                char ch2 = 'u';
                int dif = ch1 - ch2;
                if (dif == 0) { //equal
                    url = url.substring(index + 1);
                    found = true;
                    Map<String, String> map = getQueryMap(url);
                    Log.d("map : ", " " + map.toString());

                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        ItemCard itemCard = new ItemCard(entry.getKey(), entry.getValue(), "=");
                        insertItem(itemCard);
                    }

                } else {
                    index = url.indexOf("?",index);

                }

            }




        }
    }// end getUtm

    private void insertItem(ItemCard itemCard) {

        arrayList.add(itemCard);
        adapter.notifyDataSetChanged();
    }

    private void getSnippet(JSONArray array){

        for (int i=0; i<array.length(); i++){

            try {
                JSONObject snippetJson = array.getJSONObject(i);

                String analyticsType = snippetJson.getString("id");
                String params = snippetJson.getJSONObject("parameters").toString();
                snippetExample.setText(params);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void editShortURL(String aliasName, String destUrl){

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"destinations\": [{\"url\": \""+destUrl+"\", \"country\": null, \"os\": null}]}");
        Request request = new Request.Builder()
                .url("https://api.shorten.rest/aliases?aliasName="+aliasName)
                .method("PUT", body)
                .addHeader("x-api-key", "e9896260-b45b-11ea-9ec4-b1aa9a0ed929") //later change take api from class
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {

            Log.d("TEST101","response is :"+response);

        } catch (IOException e) {
            e.printStackTrace();
        }


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
