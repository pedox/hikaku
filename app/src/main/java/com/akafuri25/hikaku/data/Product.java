package com.akafuri25.hikaku.data;

/**
 * Created by pedox on 4/8/16.
 */
public class Product {

    String id;
    String name;
    int price;
    String url;
    String source;
    String image;

    public Product(String id, String name, int price, String url, String source, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.url = url;
        this.source = source;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public String getImage() {
        return image;
    }
}
