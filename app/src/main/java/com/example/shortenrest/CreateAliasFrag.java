package com.example.shortenrest;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class CreateAliasFrag extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText longURL;
    EditText shortURL;
    Button shortenBtn;
    ImageView copyIcon;
    Spinner spinner;
    String snippetID;

    Button okBtn;
    TextView dialogMsg, addUtm;

    EditText snippetExample;
    ImageView addSnippet, addIcon, removeIcon;

    RelativeLayout relativeLayout;

    boolean isSnippet;
    boolean isUtm;

    private Context mContext;

    SnippetList snippetList;

    ArrayList<ItemCard> arrayList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } //network request purposes

        longURL = view.findViewById(R.id.longURL);
        shortURL = view.findViewById(R.id.shortURL);
        shortenBtn = view.findViewById(R.id.shortenBtn);
        copyIcon = view.findViewById(R.id.copyIcon);

        addIcon = view.findViewById(R.id.addIcon);
        removeIcon = view.findViewById(R.id.removeIcon);
        relativeLayout = view.findViewById(R.id.relativeLayout);

        snippetExample = view.findViewById(R.id.snippetExample);
        addSnippet = view.findViewById(R.id.addSnippet);

        addUtm = view.findViewById(R.id.addUtm);
        SpannableString content = new SpannableString("Add UTM");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        addUtm.setText(content);

        buildRecyclerView(view);
        addUtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem();

            }
        });

        //creating dropdown menu
        spinner = view.findViewById(R.id.spinner);
        buildSnippetMenu();

        copyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("shortUrl", shortURL.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "URL has been copied to clipboard.", Toast.LENGTH_SHORT).show();

            }
        });


        shortenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (isValid()) {
                if (isSnippet) {
                    createAliasSnippet();
                } else {
                    createAlias();
                }

                Toast.makeText(mContext, "Short URL has been created successfully", Toast.LENGTH_SHORT).show();

                }

                //getUTMs();
            }
        });


        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIcon.setVisibility(View.INVISIBLE);
                removeIcon.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);

            }
        });

        removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeIcon.setVisibility(View.INVISIBLE);
                addIcon.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });

        EditText editText = new EditText(mContext);
    } //end onViewCreated()


    private void buildSnippetMenu(){

        String[] items = new String[]{"Select snippet","GoogleAnalytics", "FacebookPixel", "GoogleConversionPixel", "LinkedInPixel", "AdrollPixel", "TaboolaPixel", "BingPixel", "PinterestPixel", "SnapchatPixel"};
        spinner.setSelection(1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                snippetID = parent.getItemAtPosition(position).toString();
                Log.d("testLOL",snippetID);

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        addSnippet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snippetList = new SnippetList(snippetID, "");
                Log.d("herehehrhe",snippetList.getParameterExample(snippetID));
                snippetExample.setVisibility(View.VISIBLE);
                snippetExample.setText(snippetList.getParameterExample(snippetID));
                isSnippet = true;
            }
        });

    }

    private void buildRecyclerView(View view){

        //for testing
        arrayList = new ArrayList<>(); //change name

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(mContext);
        adapter = new Adapter(arrayList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void insertItem() {

        arrayList.add(new ItemCard());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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

        String destinationUrl = longURL.getText().toString();
        String utmUrl = getUTMs();

        if (isUtm){

            destinationUrl = utmUrl;
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"destinations\": [{\"url\": \""+destinationUrl+"\", \"country\": null, \"os\": null}]}");
        Request request = new Request.Builder()
                .url("https://api.shorten.rest/aliases?aliasName=/@rnd") //add domain.. etc
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


    public void createAliasSnippet(){

        String parameters = snippetExample.getText().toString();
        if (parameters.equals("NA")) {
            parameters = "";
        }
        parameters = parameters.replaceAll("\n","");

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"destinations\": [{\"url\": \""+longURL.getText().toString()+"\", \"country\": null, \"os\": null}], \"snippets\": [{\"id\": \""+snippetID+"\", \"parameters\": "+parameters+"}]}");
        Request request = new Request.Builder()
                .url("https://api.shorten.rest/aliases?aliasName=/@rnd") //add domain.. etc
                .method("POST", body)
                .addHeader("x-api-key", "e9896260-b45b-11ea-9ec4-b1aa9a0ed929") //later change take api from class
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Log.d("jsonSN","json:"+json);
            Log.d("HERE(SN)","response is :"+response);

            JSONObject jsonObj = new JSONObject(json);
            String shortened = jsonObj.getString("shortUrl");
            Log.d("shorturl(SN)","="+shortened);
            shortURL.setText(shortened);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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


    private String getUTMs(){

        String url = longURL.getText().toString();
        int count = arrayList.size();
        if (count != 0 ){
            isUtm = true;
        }

        url = url+"?";

        for (int i=0; i<count; i++){

        ItemCard itemCard = arrayList.get(i);
        url = url+itemCard.getParamEdit()+"="+itemCard.getValueEdit()+"&";

        Log.d("TestGet","hi "+itemCard.getParamEdit());

       } //end for loop

        Log.d("output","url is: "+url);
        return url;

    } //end getUTMs


}
