package com.example.shortenrest;

public class Credentials {

    private String API_KEY = "e9896260-b45b-11ea-9ec4-b1aa9a0ed929";
    private String domain = "short.fyi"; //default incase not specified


    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

}
