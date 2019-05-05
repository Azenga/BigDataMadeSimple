package com.shadow.bigdatamadesimple.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Message implements Serializable {

    @Exclude
    String id;

    private String sender, receiver, content;

    public Message(){}
    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public void setId(String id) {
        this.id = id;
    }
}
