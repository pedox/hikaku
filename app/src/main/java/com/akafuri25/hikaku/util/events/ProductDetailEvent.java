package com.akafuri25.hikaku.util.events;

import com.akafuri25.hikaku.data.Product;

/**
 * Created by pedox on 4/12/16.
 */
public class ProductDetailEvent {

    Product product;

    public ProductDetailEvent(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
