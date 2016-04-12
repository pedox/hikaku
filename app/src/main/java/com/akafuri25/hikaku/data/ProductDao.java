package com.akafuri25.hikaku.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.akafuri25.hikaku.data.Product;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRODUCT".
*/
public class ProductDao extends AbstractDao<Product, String> {

    public static final String TABLENAME = "PRODUCT";

    /**
     * Properties of entity Product.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property ProductId = new Property(0, String.class, "productId", true, "PRODUCT_ID");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Price = new Property(2, Double.class, "price", false, "PRICE");
        public final static Property Url = new Property(3, String.class, "url", false, "URL");
        public final static Property Source = new Property(4, String.class, "source", false, "SOURCE");
        public final static Property Image = new Property(5, String.class, "image", false, "IMAGE");
        public final static Property Location = new Property(6, String.class, "location", false, "LOCATION");
    };


    public ProductDao(DaoConfig config) {
        super(config);
    }
    
    public ProductDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRODUCT\" (" + //
                "\"PRODUCT_ID\" TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: productId
                "\"NAME\" TEXT," + // 1: name
                "\"PRICE\" REAL," + // 2: price
                "\"URL\" TEXT," + // 3: url
                "\"SOURCE\" TEXT," + // 4: source
                "\"IMAGE\" TEXT," + // 5: image
                "\"LOCATION\" TEXT);"); // 6: location
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRODUCT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Product entity) {
        stmt.clearBindings();
 
        String productId = entity.getProductId();
        if (productId != null) {
            stmt.bindString(1, productId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        Double price = entity.getPrice();
        if (price != null) {
            stmt.bindDouble(3, price);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(4, url);
        }
 
        String source = entity.getSource();
        if (source != null) {
            stmt.bindString(5, source);
        }
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(6, image);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(7, location);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Product readEntity(Cursor cursor, int offset) {
        Product entity = new Product( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // productId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2), // price
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // url
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // source
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // image
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // location
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Product entity, int offset) {
        entity.setProductId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPrice(cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2));
        entity.setUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSource(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setImage(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLocation(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Product entity, long rowId) {
        return entity.getProductId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Product entity) {
        if(entity != null) {
            return entity.getProductId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
