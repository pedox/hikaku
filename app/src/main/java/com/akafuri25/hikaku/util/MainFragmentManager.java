package com.akafuri25.hikaku.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by pedox on 4/12/16.
 */
public class MainFragmentManager extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments;

    public MainFragmentManager(FragmentManager fm, ArrayList<Fragment> frag) {
        super(fm);
        fragments = frag;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
