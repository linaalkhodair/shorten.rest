package com.example.shortenrest;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class EditAliasFrag extends Fragment {

    private EditText destURL;
    private EditText domainEdit;
    private EditText shortURL;
    private Button searchBtn;
    private Button saveBtn;
    private TextView domainTV, destTV, addUtm;
    private boolean isUtm;
    private int numOfUtm; //stores the number of originalUtms
    private ImageView copyIcon;


    private Spinner spinner;
    private String snippetID;
    private ImageView addSnippet;
    private boolean isSnippet;
    private SnippetList snippetList;
    private boolean hasPreviousSnippet;

    private RelativeLayout relativeLayout;

    private Button okBtn;
    private static String cleanUrl;
    private TextView dialogMsg;
    private Context mContext;
    private String plainUrl;

    private ArrayList<ItemCard> arrayList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    //Snippet recycler:
    private ArrayList<SnippetCard> snippetArrayList;
    private RecyclerView snippetRecyclerView;
    private RecyclerView.Adapter snippetAdapter;
    private RecyclerView.LayoutManager snippetLayoutManager;

    private String apiKey;
    private String domain;

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
        relativeLayout = view.findViewById(R.id.relativeLayout);
        copyIcon = view.findViewById(R.id.copyIcon);

        SharedPreferences prefs = mContext.getSharedPreferences("Credentials", MODE_PRIVATE);
        apiKey = prefs.getString("apiKey", "NA");
        domain = prefs.getString("domain", "short.fyi");



        copyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("shortUrl", shortURL.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "URL has been copied to clipboard.", Toast.LENGTH_SHORT).show();

            }
        });

        addSnippet = view.findViewById(R.id.addSnippet);
        //creating dropdown menu
        spinner = view.findViewById(R.id.spinner);

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


        buildSnippetRecyclerView(view);

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
                    //editShortURL(aliasName, destUrl);
                    editShortURL(aliasName, cleanUrl);

                }

            }
        });

    } //end onViewCreated


    //function that create the dropdown menu for pixels (snippets), if url already has snippets it will set the selection based on the previous snippets
    private void buildSnippetMenu(){

        String[] items = new String[]{"Select snippet","GoogleAnalytics", "FacebookPixel", "GoogleConversionPixel", "LinkedInPixel", "AdrollPixel", "TaboolaPixel", "BingPixel", "PinterestPixel", "SnapchatPixel"};
        spinner.setSelection(1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        if (hasPreviousSnippet) {

            for (int i=0; i<items.length; i++){

                if (items[i].equals(snippetID)) {
                    spinner.setSelection(i);
                }
            }

        }

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


    //function that adds a snippet edit text when the '+' button is clicked
    private void insertSnippet(String content, String id){

        snippetArrayList.add(new SnippetCard(content, id));
        snippetAdapter.notifyDataSetChanged();

    }// end insertSnippet


    //function that creates the recycler view for the UTMs
    private void buildRecyclerView(View view){

        //for testing
        arrayList = new ArrayList<>(); //change name

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(mContext);
        adapter = new Adapter(arrayList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }//end buildRecyclerView

    //function that builds the recycler view for the snippets
    private void buildSnippetRecyclerView(View view){
        snippetArrayList = new ArrayList<>();

        snippetRecyclerView = view.findViewById(R.id.snippetRecycler);
        snippetLayoutManager = new LinearLayoutManager(mContext);
        snippetAdapter = new SnippetAdapter(snippetArrayList);

        snippetRecyclerView.setLayoutManager(snippetLayoutManager);
        snippetRecyclerView.setAdapter(snippetAdapter);


    }//end buildSnippetRecyclerView


    //function that validates the entered destination url (if changed) when the save button is clicked
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

    }// end isValid

    //function that validates the entered short url
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
    }//end end validate


    //function that crops the shortURL to get the alias name in order to search for it using the API
    private String getAliasName(String shortURL){

        shortURL = shortURL.replace("https://","");
        Log.d("test1", "short: "+ shortURL);

        int domainLength = domain.length();

        shortURL = shortURL.substring(domainLength+1);
        Log.d("test2", "short: "+ shortURL);

        return shortURL;

    }// end getAliasName

    //function that makes a request to the API by sending the alias name, in order to get the destination url, domain, snippets, utms everything.
    private void getAlias(String aliasName){

        Log.d("HERE----", "dom/"+domain);
        String url = "https://api.shorten.rest/aliases?domainName="+domain+"?aliasName="+aliasName;
        if (domain.equals("short.fyi")){
            url = "https://api.shorten.rest/aliases?aliasName="+aliasName;
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(url)
                .method("GET",null)
                .addHeader("x-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            Log.d("HERE!","response is :"+response);

            String json = response.body().string();
            Log.d("json","json:"+json);
            JSONObject jsonObj = new JSONObject(json);

                if (response.code() == 200) {
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

                    cleanUrl = destinatonUrl;
                    getUtm(destinatonUrl);

                    Log.d("tesssst", "url:" + destinatonUrl);


                    destTV.setVisibility(View.VISIBLE);
                    destURL.setVisibility(View.VISIBLE);
                    destURL.setText(destinatonUrl);
                    saveBtn.setVisibility(View.VISIBLE);

                    //displaying snippets:
                    JSONArray array = jsonObj.getJSONArray("snippets");
                    getSnippet(array);

                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    createDialog(jsonObj.getString("errorMessage")+", please try again.");

                }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            if (e.getMessage().equals("Value null of type org.json.JSONObject$1 cannot be converted to JSONObject")) {
                createDialog("Alias is not found, please enter a valid alias and try again.");
            }
            e.printStackTrace();
        }



    } //end getAlias

    //function that create a map, key value pairs from the url to retrieve the UTMS embedded, by using '&' to filter through
    private static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }// end getQueryMap


    //function that receives returned destination url from API, in order to extract UTM parameters from it and display them
    private void getUtm(String url){

        boolean found = false;
        int index = url.indexOf("?");
        numOfUtm = 0;

        if (url.contains("?utm")) { // to make sure the '?' is for utms not other parameter

            while (!found) {

                char ch1 = url.charAt(index + 1); //charachter after the '?'
                char ch2 = 'u';
                int dif = ch1 - ch2;
                if (dif == 0) { //equal

                    cleanUrl = url.substring(0,index); //new
                    plainUrl = url.substring(0,index); //idkkkkk
                    Log.d("cleaned : ", " " + cleanUrl);

                    url = url.substring(index + 1); //to get text after '?' --> get utms



                    found = true;
                    Map<String, String> map = getQueryMap(url); //function that creates map for key values pairs of utm

                    numOfUtm = map.size();

                    Log.d("map : ", " " + map.toString());

                    for (Map.Entry<String, String> entry : map.entrySet()) { //for loop to store all utms in the array and display them
                        ItemCard itemCard = new ItemCard(entry.getKey(), entry.getValue(), "=");
                        insertItem(itemCard);
                    }

                } else {
                    index = url.indexOf("?",index);

                }

            } //end while loop

        } //end if



    }// end getUtm

    //function that inserts a row of UTM that has been already embedded in url (previously added)
    private void insertItem(ItemCard itemCard) {

        arrayList.add(itemCard);
        adapter.notifyDataSetChanged();
    }// end insertItem

    //function that inserts an empty row of UTM when the '+' button has been clicked
    private void insertItem() {

        arrayList.add(new ItemCard());
        adapter.notifyDataSetChanged();
    }// end insertItem

    //function that recieves a JSONarray from the API request with the list of snippets that have been added previously to the url
    //it iterates through the array to get the snippets -> 1- add them to the arraylist. 2- display them
    private void getSnippet(JSONArray array){

        if (array.length() == 0) {
            hasPreviousSnippet = false;
        }

        for (int i=0; i<array.length(); i++){

            try {
                JSONObject snippetJson = array.getJSONObject(i);

                String analyticsType = snippetJson.getString("id");
                snippetID = analyticsType;
                hasPreviousSnippet = true;
                String params = snippetJson.getJSONObject("parameters").toString();
                SnippetCard snippetCard = new SnippetCard(params, analyticsType);
                snippetArrayList.add(snippetCard);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        buildSnippetMenu();


    }// end getSnippet


    //function that makes a request to the API to update (edit) the short url, by updating  either snippets, utms, or destination url
    private void editShortURL(String aliasName, String destUrl){


        RequestBody body;
        MediaType mediaType = MediaType.parse("application/json");
        String  utmUrl = addUtm(); //to see if there are any new utms added and get the updated url accordingly

        String url = "https://api.shorten.rest/aliases?domainName="+domain+"?aliasName="+aliasName;
        if (domain.equals("short.fyi")){
            url = "https://api.shorten.rest/aliases?aliasName="+aliasName;
        }

        if (isUtm){

            destUrl = utmUrl;
        }

        if (isSnippet || hasPreviousSnippet) {
            String content = setSnippets(destUrl);

            body = RequestBody.create(mediaType, content);

        } else {

            body = RequestBody.create(mediaType, "{\"destinations\": [{\"url\": \""+destUrl+"\", \"country\": null, \"os\": null}]}");
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .method("PUT", body)
                .addHeader("x-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {

            JSONObject jsonObj = new JSONObject(response.body().string());
            Log.d("TEST101","response is :"+response);

            if (response.code() == 200){
                Toast.makeText(mContext, "Short URL has been updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                createDialog(jsonObj.getString("errorMessage")+", please try again.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cleanUrl = plainUrl; //plain url is a clean version without any utms, we go back to it each time for a 'clean start' hehe

    }// end editShortURL


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


    //functions checks if there are added utms in the array list to update url
    private String addUtm(){

        String url = destURL.getText().toString();
        //cleanUrl = destURL.getText().toString();
        int count = arrayList.size(); //size of array (all utms currently)


        if (count != 0 ){
            isUtm = true;
        }

        if (!(cleanUrl.charAt(cleanUrl.length()-1) == '?')) {

            cleanUrl = cleanUrl + "?"; //if it doesn't have previous utms, add '?'
        }

        for (int i = 0; i<count; i++){

            ItemCard itemCard = arrayList.get(i);

            if (count-1 == i){ //last one
                cleanUrl = cleanUrl+itemCard.getParamEdit()+"="+itemCard.getValueEdit();
            } else{

                cleanUrl = cleanUrl+itemCard.getParamEdit()+"="+itemCard.getValueEdit()+"&"; //later remove & for last element

            }

            Log.d("TestGet","hi "+itemCard.getParamEdit());

        } //end for loop

        Log.d("output","url is: "+cleanUrl);
        return cleanUrl;

    } //end addUtm


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

}// end EditAlasFrag
