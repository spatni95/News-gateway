package com.example.hw5_news_gateway;

import java.io.Serializable;

public class SingleArticleBean implements Serializable {
    private String article_Name;
    private String article_author;
    private String article_title;
    private String article_description;
    private String article_url;
    private String article_urlToImage;
    private String article_publishedAt;

    public SingleArticleBean(String article_Name, String article_author, String article_title, String article_description, String article_url, String article_urlToImage, String article_publishedAt) {
        this.article_Name = article_Name;
        this.article_author = article_author;
        this.article_title = article_title;
        this.article_description = article_description;
        this.article_url = article_url;
        this.article_urlToImage = article_urlToImage;
        this.article_publishedAt = article_publishedAt;
    }

    public String getArticle_Name() {
        return article_Name;
    }

    public void setArticle_Name(String article_Name) {
        this.article_Name = article_Name;
    }

    public String getArticle_author() {
        return article_author;
    }

    public void setArticle_author(String article_author) {
        this.article_author = article_author;
    }

    public String getArticle_title() {
        return article_title;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }

    public String getArticle_description() {
        return article_description;
    }

    public void setArticle_description(String article_description) {
        this.article_description = article_description;
    }

    public String getArticle_url() {
        return article_url;
    }

    public void setArticle_url(String article_url) {
        this.article_url = article_url;
    }

    public String getArticle_urlToImage() {
        return article_urlToImage;
    }

    public void setArticle_urlToImage(String article_urlToImage) {
        this.article_urlToImage = article_urlToImage;
    }

    public String getArticle_publishedAt() {
        return article_publishedAt;
    }

    public void setArticle_publishedAt(String article_publishedAt) {
        this.article_publishedAt = article_publishedAt;
    }

}

