package com.akafuri25.hikaku.util;

import android.support.v4.app.Fragment;

import com.akafuri25.hikaku.data.Product;

import java.util.ArrayList;

/**
 * Created by pedox on 4/17/16.
 */
public class CompareData {

    Fragment fragment;
    Product product;
    String description;
    ArrayList<String> images;

    public CompareData(Fragment fragment, Product product) {
        this.fragment = fragment;
        this.product = product;
        this.description = description;
        this.images = images;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Product getProduct() {
        return product;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
