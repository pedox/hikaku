package com.akafuri25.hikaku.ui.fragments;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.adapter.ListWishlistAdapter;
import com.akafuri25.hikaku.data.DaoMaster;
import com.akafuri25.hikaku.data.DaoSession;
import com.akafuri25.hikaku.data.Wishlist;
import com.akafuri25.hikaku.data.WishlistDao;
import com.akafuri25.hikaku.util.events.UpdateViewEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistFragment extends Fragment {


    @Bind(R.id.mainTitle)
    TextView mainTitle;
    @Bind(R.id.subTitle)
    TextView subTitle;
    @Bind(R.id.introduction)
    LinearLayout introduction;
    @Bind(R.id.list_item)
    RecyclerView listItem;
    List<Wishlist> wishlists;
    ListWishlistAdapter adapter;

    DaoMaster.DevOpenHelper helper;
    SQLiteDatabase db;
    DaoSession session;
    WishlistDao wishlistDao;

    public WishlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        ButterKnife.bind(this, view);

        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        helper = new DaoMaster.DevOpenHelper(getContext(), getContext().getString(R.string.dbName), null);
        db = helper.getReadableDatabase();
        session = new DaoMaster(db).newSession();
        wishlistDao = session.getWishlistDao();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainTitle.setText("Wish Lists");
        subTitle.setText(R.string.wishlist_subtitle);
        listItem.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadView();
    }

    void loadView() {
        wishlists = wishlistDao.loadAll();

        if(wishlists.size() > 0) {
            introduction.setVisibility(View.GONE);
            listItem.setVisibility(View.VISIBLE);
            adapter = new ListWishlistAdapter(wishlists, getContext());
            listItem.setAdapter(adapter);
        } else {
            introduction.setVisibility(View.VISIBLE);
            listItem.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEvent(UpdateViewEvent event) {
        if(event.getType() == UpdateViewEvent.WISHLIST) {
            loadView();
        }
        if(event.getType() == UpdateViewEvent.WISHLIST_DELETE) {
            if(wishlistDao.count() <= 0) {
                listItem.setVisibility(View.GONE);
                introduction.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
}
