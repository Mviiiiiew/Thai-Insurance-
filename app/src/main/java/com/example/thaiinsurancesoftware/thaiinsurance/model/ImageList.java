package com.example.thaiinsurancesoftware.thaiinsurance.model;

/**
 * Created by MAN on 4/24/2017.
 */

public class ImageList {

    public  String name;
    public  String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ImageList(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public  ImageList(){

    }
}
