package com.kinvey.sample.micflow;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by adobrev on 12/15/17.
 */

public class Book extends GenericJson {

    @Key("title")
    private String name;

    @Key("isbn")
    private String isbn;

    public Book(){};

    public Book(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}