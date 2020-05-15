package com.example.hw5_news_gateway;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class NewsMenuBean implements Serializable {
    private String id;
    private String name;
    private String category;
    private String url;
    private String color_code_list[] = {"#000000", "#f9d418", "#838fea", "#158c13", "#f9042d", "#6fbdf2", "#242b60", "#f435ce","#3d1b1b","#ef550e","#3bef0e","#0eefdc","#0e55ef","#ef0e91","#330101","#776767"};

    public NewsMenuBean(String id, String name, String url, String category) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public String getColor(int i) {

        if(i<color_code_list.length)
            return color_code_list[i];
        return "#FFFFFF";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    public String toString() {
        return name;
    }
}
