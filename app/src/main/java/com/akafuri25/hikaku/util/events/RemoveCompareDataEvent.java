package com.akafuri25.hikaku.util.events;

/**
 * Created by pedox on 4/17/16.
 */
public class RemoveCompareDataEvent {

    String id;

    public RemoveCompareDataEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
