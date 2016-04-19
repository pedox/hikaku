package com.akafuri25.hikaku.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;

import com.akafuri25.hikaku.data.Compare;
import com.akafuri25.hikaku.data.CompareDao;
import com.akafuri25.hikaku.data.DaoSession;
import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.ui.fragments.CompareFragment;
import com.akafuri25.hikaku.ui.fragments.CompareListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedox on 4/12/16.
 */
public class MainFragmentManager extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    List<Compare> compareList = new ArrayList<>();
    boolean isCompare = false;

    public MainFragmentManager(FragmentManager fm, ArrayList<Fragment> frag) {
        super(fm);
        fragments = frag;
    }


    public MainFragmentManager(FragmentManager fm, List<Compare> compareList, boolean bagus) {
        super(fm);
        this.compareList = compareList;
        isCompare = bagus;
        for(Compare c : this.compareList) {
            Log.v("XCOMPARE->", c.getProductId());
            Log.v("XCOMPARE->", c.getName());
        }
    }


    @Override
    public Fragment getItem(int position) {
        if(isCompare) {
            Fragment fragment = new CompareListFragment();
            Bundle b = new Bundle();
            b.putString("id", compareList.get(position).getProductId());
            Log.v("GETID", compareList.get(position).getProductId());
            fragment.setArguments(b);
            return fragment;
        } else {
            return fragments.get(position);
        }
    }

    @Override
    public int getCount() {
        if(isCompare) {
            return compareList.size();
        } else {
            return fragments.size();
        }
    }

    public void update(List<Compare> compareList) {
        this.compareList.clear();
        this.compareList.addAll(compareList);
        this.notifyDataSetChanged();
    }

}
