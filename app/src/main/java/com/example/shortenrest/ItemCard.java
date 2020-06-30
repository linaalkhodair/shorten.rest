package com.example.shortenrest;

public class ItemCard {

    private String paramEdit;
    private String valueEdit;
    private String equal;


    public ItemCard(String paramEdit, String valueEdit, String equal) {
        this.paramEdit = paramEdit;
        this.valueEdit = valueEdit;
        this.equal = equal;
    }

    public String getParamEdit() {
        return paramEdit;
    }

    public String getValueEdit() {
        return valueEdit;
    }

    public String getEqual() {
        return equal;
    }
}
