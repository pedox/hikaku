package com.akafuri25.hikaku.util.events;

import android.util.Log;

import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.util.CompareData;

import java.util.ArrayList;

/**
 * Created by pedox on 4/17/16.
 */
public class CompareDataEvent {

    ArrayList<String> compareListId = new ArrayList<>();
    ArrayList<Product> compareDatas = new ArrayList<>();

    public CompareDataEvent(ArrayList<String> compareListId, ArrayList<Product> compareDatas) {
        this.compareListId = compareListId;
        this.compareDatas = compareDatas;
    }

    public ArrayList<String> getCompareListId() {
        return compareListId;
    }

    public ArrayList<Product> getCompareDatas() {
        return compareDatas;
    }

    public void removeItem(String id) {
        int index = compareListId.indexOf(id);
        Log.v("GetIndex", String.valueOf(index));
        compareDatas.remove(index);
        compareListId.remove(index);


    }
}
