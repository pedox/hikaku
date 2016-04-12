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

    boolean swipe = true;
    boolean square = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwipe(boolean swipe) {
        this.swipe = swipe;
    }

    public void setSquare(boolean square) {
        this.square = square;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg) {
        if(!swipe) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!swipe) {
            ViewParent parent = getParent();
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(square) {
            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
        }
    }

}
