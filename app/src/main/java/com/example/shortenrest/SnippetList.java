package com.example.shortenrest;

import android.util.Log;

public class SnippetList {

    private String ID;
    private String parameterExample;

    public SnippetList(String ID, String parameterExample) {
        this.ID = ID;
        this.parameterExample = parameterExample;
    }

    public String getParameterExample(String ID){

        this.ID = ID;
        Log.d("insideHERE","id"+ID);
        switch (ID){

            case "GoogleAnalytics":
                parameterExample = "{\n" +
                        "  \"trackingId\": \"YOUR_TRACKING_ID\",\n" +
                        "  \"event\": \"YOUR_EVENT\"\n" +
                        "}";
                break;
            case "FacebookPixel":
                parameterExample = "{\n" +
                        "  \"id\": \"YOUR_ID\",\n" +
                        "  \"event\": \"EVENT_NAME\"\n" +
                        "}";
                break;
            case "GoogleConversionPixel":
                parameterExample = "{\n" +
                        "  \"conversionId\": \"YOUR_CONVERSION_ID\",\n" +
                        "  \"gtagConversionEvent\": {\n" +
                        "    \"sendTo\": \"INSERT_SENDTO\",\n" +
                        "    \"value\": \"1.0\",\n" +
                        "    \"currency\": \"USD\"\n" +
                        "  }\n" +
                        "}";
                break;
            case "LinkedInPixel":
                parameterExample = "{\n" +
                        "  \"partnerId\": \"YOUR_PARTNER_ID\"\n" +
                        "}";
                break;
            case "AdrollPixel":
                parameterExample = "{\n" +
                        "  \"advId\": \"YOUR_ADV_ID\",\n" +
                        "  \"pixId\": \"YOUR_PIX_ID\"\n" +
                        "}";
                break;
            case "TaboolaPixel":
                parameterExample = "{\n" +
                        "  \"id\": \"YOUR_TABOOLA_ID\",\n" +
                        "  \"eventName\": \"YOUR_EVENT_NAME\"\n" +
                        "}";
                break;
            case "BingPixel":
                parameterExample = "{\n" +
                        "  \"id\": \"YOUR_ID\"\n" +
                        "}";
                break;
            case "PinterestPixel":
                parameterExample = "{\n" +
                        "  \"tagId\": \"YOUR_TAG_ID\"\n" +
                        "}";
                break;
            case "SnapchatPixel":
                parameterExample = "{\n" +
                        "  \"pixelId\": \"YOUR_PIXEL_ID\",\n" +
                        "  \"userEmail\": \"INSERT_USER_EMAIL\",\n" +
                        "  \"eventName\": \"PAGE_VIEW\"\n" +
                        "}";
                break;
            default:
                parameterExample = "NA";
                break;


        } //end switch

        return parameterExample;

    } //end getParameterExample
}

