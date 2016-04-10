package com.akafuri25.hikaku.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

/**
 * Created by pedox on 4/8/16.
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(ev);
    }
}
