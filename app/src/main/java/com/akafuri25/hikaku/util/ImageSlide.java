package com.akafuri25.hikaku.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.akafuri25.hikaku.ui.fragments.ImageSlideFragment;

import java.util.ArrayList;

/**
 * Created by pedox on 4/12/16.
 */
public class ImageSlide extends FragmentStatePagerAdapter {

    ArrayList<String> images;

    public ImageSlide(FragmentManager fm, ArrayList<String> images) {
        super(fm);
        this.images = images;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment slide = new ImageSlideFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", images.get(position));
        Log.v("LOAD TO IMAGE", images.get(position));
        slide.setArguments(bundle);
        return slide;
    }

    @Override
    public int getCount() {
        return images.size();
    }
}
