package com.shadow.bigdatamadesimple.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    @Exclude
    private String uid;

    private String fullName, contact, street, pobox, city, state, zipcode, country, website, group;
    private String imageName;

    public User() {
    }

    public User(String fullName, String contact, String street, String pobox, String city, String state, String zipcode, String country, String website, String group) {
        this.fullName = fullName;
        this.contact = contact;
        this.street = street;
        this.pobox = pobox;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.country = country;
        this.website = website;
        this.group = group;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPobox() {
        return pobox;
    }

    public void setPobox(String pobox) {
        this.pobox = pobox;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
