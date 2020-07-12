package com.example.shortenrest;

public class SnippetCard {

    //this class represents the Snippet row

    String snippetContent;
    String snippetID;

    public SnippetCard(String snippetContent, String snippetID) {
        this.snippetContent = snippetContent;
        this.snippetID = snippetID;
    }

    public String getSnippetID() {
        return snippetID;
    }

    public void setSnippetID(String snippetID) {
        this.snippetID = snippetID;
    }

    public SnippetCard() {
    }

    public String getSnippetContent() {
        return snippetContent;
    }

    public void setSnippetContent(String snippetContent) {
        this.snippetContent = snippetContent;
    }
}
