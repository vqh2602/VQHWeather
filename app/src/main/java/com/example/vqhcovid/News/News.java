package com.example.vqhcovid.News;

public class News {
    public String title;
    public String link;
    public String imageUrl;

    public News(String title, String link, String imageUrl) {
        this.title = title;
        this.link = link;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
