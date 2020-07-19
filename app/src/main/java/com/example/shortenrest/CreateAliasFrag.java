package com.example.shortenrest;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;


public class CreateAliasFrag extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText longURL;
    private EditText shortURL;
    private Button shortenBtn;
    private ImageView copyIcon;
    private Spinner spinner;
    private String snippetID;

    private Button okBtn;
    private TextView dialogMsg, addUtm;

    private ImageView addSnippet, addIcon, removeIcon;

    private RelativeLayout relativeLayout;

    private boolean isSnippet;
    private boolean isUtm;

    private Context mContext;

    private SnippetList snippetList;

    private ArrayList<ItemCard> arrayList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    //Snippet recycler:
    private ArrayList<SnippetCard> snippetArrayList;
    private RecyclerView snippetRecyclerView;
    private RecyclerView.Adapter snippetAdapter;
    private RecyclerView.LayoutManager snippetLayoutManager;

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

        addSnippet = view.findViewById(R.id.addSnippet);

        addUtm = view.findViewById(R.id.addUtm);
        SpannableString content = new SpannableString("Add UTM");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        addUtm.setText(content);

        buildRecyclerView(view); //building utm recycler view
        addUtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem();

            }
        });

        buildSnippetRecyclerView(view);

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

                createAlias();

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

    } //end onViewCreated()


    //function to build the dropdown menu for adding pixels to url
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

                insertSnippet(snippetList.getParameterExample(snippetID), snippetID);
                isSnippet = true;
            }
        });

    } //end buildSnippetMenu

    //function to create the recycler view of snippets (pixels)
    private void buildSnippetRecyclerView(View view){
        snippetArrayList = new ArrayList<>();

        snippetRecyclerView = view.findViewById(R.id.snippetRecycler);
        snippetLayoutManager = new LinearLayoutManager(mContext);
        snippetAdapter = new SnippetAdapter(snippetArrayList);

        snippetRecyclerView.setLayoutManager(snippetLayoutManager);
        snippetRecyclerView.setAdapter(snippetAdapter);

    } //end buildSnippetRecyclerView


    //function to insert new snippet edit text when '+' button is clicked
    private void insertSnippet(String content, String id){

        snippetArrayList.add(new SnippetCard(content, id));
        snippetAdapter.notifyDataSetChanged();

    } //end insertSnippet


    //function to build the recycler view for UTMs
    private void buildRecyclerView(View view){

        //for testing
        arrayList = new ArrayList<>(); //change name

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(mContext);
        adapter = new Adapter(arrayList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }// end buildRecyclerView


    //function to insert a new row of utm
    private void insertItem() {

        arrayList.add(new ItemCard());
        adapter.notifyDataSetChanged();
    }// end insertItem

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //function to validate the entered destination url field
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

    }// end isValid


    //function that creates the request to the API to create a short URL
    //if utms have been added, it will check and add them to the url
    //if snippets have been added, it will check to add them to the destinations in the request
    private void createAlias() {

        SharedPreferences prefs = mContext.getSharedPreferences("Credentials", MODE_PRIVATE);
        String api = prefs.getString("apiKey", "NA");

        String domain = prefs.getString("domain", "short.fyi");
        Log.d("HERE----", "dom/"+domain);
        String url = "https://api.shorten.rest/aliases?domainName="+domain+"?aliasName=/@rnd";
        if (domain.equals("short.fyi")){
            url = "https://api.shorten.rest/aliases?aliasName=/@rnd";
        }

        String destinationUrl = longURL.getText().toString();
        String utmUrl = getUTMs();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body;

        if (isUtm){

            destinationUrl = utmUrl;
        }

        if (isSnippet) {
            String content = setSnippets(destinationUrl);

            body = RequestBody.create(mediaType, content);

        } else {

            body = RequestBody.create(mediaType, "{\"destinations\": [{\"url\": \""+destinationUrl+"\", \"country\": null, \"os\": null}]}");
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url) //add domain.. etc
                .method("POST", body)
                .addHeader("x-api-key", api) //later change take api from class ///
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Log.d("json","json:"+json);
            Log.d("HERE!","response is :"+response);
            JSONObject jsonObj = new JSONObject(json);

            if (response.code() == 200) {

                String shortened = jsonObj.getString("shortUrl");
                Log.d("shorturl", "=" + shortened);
                shortURL.setText(shortened);
                Toast.makeText(mContext, "Short URL has been created successfully", Toast.LENGTH_SHORT).show();
            } else {

                createDialog(jsonObj.getString("errorMessage")+", please try again.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    } //end createAlias


   //function that returns the url with snippets added, by iterating through the arrayList of all snippets added
    private String setSnippets(String destUrl){

        // String snippets[] = new String[snippetArrayList.size()];
        String content = "{\"destinations\": [{\"url\": \""+destUrl+"\", \"country\": null, \"os\": null}], \"snippets\": [";


        for (int i=0; i<snippetArrayList.size(); i++){

            SnippetCard snippetCard = snippetArrayList.get(i);
            if (snippetArrayList.size()-1 == i){ //last one
                content += "{\"id\": \""+snippetCard.getSnippetID()+"\", \"parameters\": "+snippetCard.getSnippetContent()+"}";
            }
            else {
                content += "{\"id\": \"" + snippetCard.getSnippetID() + "\", \"parameters\": " + snippetCard.getSnippetContent() + "},";
            }
            //snippets[i] = snippet;
        }

        content += "]}";
        return content;

    }//end setSnippets


    //function that creates the custom dialog by receiving the needed message
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

    //function that adds the UTMs that have been added to the url, by iterating through the arrayList of all UTMs
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


} //end CreateAliasFrag
