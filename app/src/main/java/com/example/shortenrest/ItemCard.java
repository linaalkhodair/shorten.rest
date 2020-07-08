package com.example.shortenrest;


public class ItemCard {

    private String paramEdit;
    private String valueEdit;
    private String equal;


    public ItemCard() {

        equal = "=";

    }

    public ItemCard(String paramEdit, String valueEdit, String equal) {
        this.paramEdit = paramEdit;
        this.valueEdit = valueEdit;
        this.equal = equal;
    }

    public String getParamEdit() {
        return paramEdit;
    }

    public void setParamEdit(String paramEdit) {
        this.paramEdit = paramEdit;
    }

    public void setValueEdit(String valueEdit) {
        this.valueEdit = valueEdit;
    }

    public void setEqual(String equal) {
        this.equal = equal;
    }

    public String getValueEdit() {
        return valueEdit;
    }

    public String getEqual() {
        return equal;
    }

}
