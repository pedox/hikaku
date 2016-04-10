package com.akafuri25.hikaku.util.events;

import org.json.JSONObject;

/**
 * Created by pedox on 4/10/16.
 */
public class SearchStickyEvent {

    JSONObject data;

    public SearchStickyEvent(JSONObject data) {
        this.data = data;
    }

    public JSONObject getData() {
        return data;
    }
}
