package com.akafuri25.hikaku.ui.fragments;


import android.app.usage.UsageEvents;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.data.Compare;
import com.akafuri25.hikaku.data.CompareDao;
import com.akafuri25.hikaku.data.DaoMaster;
import com.akafuri25.hikaku.data.DaoSession;
import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.data.WishlistDao;
import com.akafuri25.hikaku.util.MainFragmentManager;
import com.akafuri25.hikaku.util.CompareData;
import com.akafuri25.hikaku.util.events.CompareDataEvent;
import com.akafuri25.hikaku.util.events.RemoveCompareDataEvent;
import com.akafuri25.hikaku.util.events.UpdateViewEvent;
import com.viewpagerindicator.LinePageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompareFragment extends Fragment {

    @Bind(R.id.mainTitle)
    TextView mainTitle;
    @Bind(R.id.subTitle)
    TextView subTitle;
    @Bind(R.id.introduction)
    LinearLayout introduction;
    @Bind(R.id.pager)
    ViewPager pager;
    MainFragmentManager adapter;
    List<Compare> compareList = new ArrayList<>();
    @Bind(R.id.indicator)
    LinePageIndicator indicator;

    DaoMaster.DevOpenHelper helper;
    SQLiteDatabase db;
    DaoSession session;
    CompareDao compareDao;

    public CompareFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compare, container, false);
        ButterKnife.bind(this, view);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        helper = new DaoMaster.DevOpenHelper(getContext(), getContext().getString(R.string.dbName), null);
        db = helper.getReadableDatabase();
        session = new DaoMaster(db).newSession();
        compareDao = session.getCompareDao();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainTitle.setText("Compare");
        subTitle.setText(R.string.compare_subtitle);
        compareList = compareDao.loadAll();
        setAdapterView();
    }

    void setAdapterView() {
        if(compareList.size() > 0) {
            introduction.setVisibility(View.GONE);
            pager.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.VISIBLE);
            if(adapter != null) {
                adapter.update(compareList);
            } else {
                adapter = new MainFragmentManager(getChildFragmentManager(), compareList, true);
            }
            pager.setAdapter(adapter);
            indicator.setViewPager(pager);
            indicator.notifyDataSetChanged();
            pager.setOffscreenPageLimit(5);
            EventBus.getDefault().post(new UpdateViewEvent(UpdateViewEvent.COMPARE_DELETE_TRUE));
        } else {
            EventBus.getDefault().post(new UpdateViewEvent(UpdateViewEvent.COMPARE_DELETE_FALSE));
            introduction.setVisibility(View.VISIBLE);
            pager.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEvent(UpdateViewEvent event) {
        if(event.getType() == UpdateViewEvent.COMPARE) {
            compareList = event.getCompareList();
            setAdapterView();
        }
        if(event.getType() == UpdateViewEvent.RESET_COMPARE) {
            compareDao.deleteAll();
            compareList = compareDao.loadAll();
            setAdapterView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
