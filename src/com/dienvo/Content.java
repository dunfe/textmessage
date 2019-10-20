package com.dienvo;

public class Content {
    private String numberOfWord;
    private String contentOfMessage;

    public Content(String numberOfWord, String contentOfMessage) {
        this.numberOfWord = numberOfWord;
        this.contentOfMessage = contentOfMessage;
    }

    public String getNumberOfWord() {
        return numberOfWord;
    }

    public void setNumberOfWord(String numberOfWord) {
        this.numberOfWord = numberOfWord;
    }

    public String getContentOfMessage() {
        return contentOfMessage;
    }

    public void setContentOfMessage(String contentOfMessage) {
        this.contentOfMessage = contentOfMessage;
    }
}
