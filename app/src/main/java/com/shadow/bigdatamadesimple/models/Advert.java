package com.shadow.bigdatamadesimple.models;

import java.io.Serializable;

public class Advert implements Serializable {

    private String content;
    private int duration;
    private String companyId;

    public Advert() {
    }

    public Advert(String content, int duration, String companyId) {
        this.content = content;
        this.duration = duration;
        this.companyId = companyId;
    }

    public String getContent() {
        return content;
    }

    public int getDuration() {
        return duration;
    }

    public String getCompanyId() {
        return companyId;
    }
}
