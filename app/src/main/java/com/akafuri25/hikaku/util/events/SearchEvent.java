package com.akafuri25.hikaku.util.events;

/**
 * Created by pedox on 4/10/16.
 */
public class SearchEvent {

    String keyword;

    public SearchEvent(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
