package com.dao;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.akafuri25.hikaku.data");

        Entity product = schema.addEntity("Product");
        product.addStringProperty("productId").unique().primaryKey();
        product.addStringProperty("name");
        product.addDoubleProperty("price");
        product.addStringProperty("url");
        product.addStringProperty("source");
        product.addStringProperty("image");
        product.addStringProperty("location");


        Entity ws = schema.addEntity("Wishlist");
        ws.addStringProperty("productId").unique().primaryKey();
        ws.addStringProperty("name");
        ws.addDoubleProperty("price");
        ws.addStringProperty("url");
        ws.addStringProperty("source");
        ws.addStringProperty("image");
        ws.addStringProperty("location");

        Entity cp = schema.addEntity("Compare");
        cp.addStringProperty("productId").unique().primaryKey();
        cp.addStringProperty("name");
        cp.addDoubleProperty("price");
        cp.addStringProperty("url");
        cp.addStringProperty("source");
        cp.addStringProperty("images");
        cp.addStringProperty("description");
        cp.addStringProperty("location");

        new DaoGenerator().generateAll(schema, args[0]);
    }

}
