package com.akafuri25.hikaku.util.events;

import com.akafuri25.hikaku.data.Compare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedox on 4/19/16.
 */
public class UpdateViewEvent {

    public static int UPDATE_ALL = 0x001;
    public static int COMPARE = 0x002;
    public static int WISHLIST = 0x003;


    List<Compare> compareList;


    int type;

    public UpdateViewEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public UpdateViewEvent(int type, List<Compare> compareList) {
        this.type = type;
        this.compareList = compareList;
    }

    public List<Compare> getCompareList() {
        return compareList;
    }
}
