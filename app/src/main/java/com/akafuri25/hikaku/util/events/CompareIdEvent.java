package com.akafuri25.hikaku.util.events;

import java.util.ArrayList;

/**
 * Created by pedox on 4/17/16.
 */
public class CompareIdEvent {

    ArrayList<String> compareListId;
    String newId;

    public CompareIdEvent(ArrayList<String> compareListId) {
        this.compareListId = compareListId;
    }

    public CompareIdEvent(String newId) {
        this.newId = newId;
    }

    public String getNewId() {
        return newId;
    }

    public ArrayList<String> getCompareListId() {
        return compareListId;
    }
}
