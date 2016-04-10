package com.akafuri25.hikaku.util.events;

import android.support.design.widget.Snackbar;

/**
 * Created by pedox on 4/10/16.
 */
public class SnackEvent {

    String message;
    int length;

    public SnackEvent(String message) {
        this.message = message;
        this.length = Snackbar.LENGTH_LONG;
    }

    public SnackEvent(String message, int length) {
        this.message = message;
        this.length = length;
    }

    public String getMessage() {
        return message;
    }

    public int getLength() {
        return length;
    }
}
