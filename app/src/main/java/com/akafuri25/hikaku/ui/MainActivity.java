package com.akafuri25.hikaku.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.ui.fragments.CompareFragment;
import com.akafuri25.hikaku.ui.fragments.SearchFragment;
import com.akafuri25.hikaku.ui.fragments.WishlistFragment;
import com.akafuri25.hikaku.util.CustomViewPager;
import com.akafuri25.hikaku.util.MainFragmentManager;
import com.akafuri25.hikaku.util.events.SearchEvent;
import com.akafuri25.hikaku.util.events.SnackEvent;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.coordinator)
    CoordinatorLayout coordinator;
    @Bind(R.id.view_pager)
    CustomViewPager viewPager;

    SearchView searchView;
    MenuItem searchMenu;



    String searchTitle = "Hikaku";

    MainFragmentManager adapter;
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);
        searchTitle = getString(R.string.app_name);
        toolbar.setTitle(searchTitle);

        handleSearch(getIntent());

        mBottomBar = BottomBar.attach(this, savedInstanceState);

        final SearchFragment searchFragment = new SearchFragment();
        fragments.add(searchFragment);
        fragments.add(new CompareFragment());
        fragments.add(new WishlistFragment());
        adapter = new MainFragmentManager(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setSwipe(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomBar.selectTabAtPosition(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                switch (menuItemId) {
                    case R.id.search:
                        changeFragment(0);
                        toolbar.setTitle(searchTitle);
                        break;
                    case R.id.compare:
                        changeFragment(1);
                        toolbar.setTitle("Compare");
                        break;
                    case R.id.wishlist:
                        changeFragment(2);
                        toolbar.setTitle("Wish Lists");
                        break;
                    default:
                        changeFragment(0);
                        toolbar.setTitle(getString(R.string.app_name));
                        break;
                }

                if (menuItemId == R.id.search) {
                    if (searchMenu != null) {
                        searchMenu.setVisible(true);
                    }

                } else {
                    if (searchMenu != null) {
                        searchMenu.setVisible(false);
                        searchMenu.collapseActionView();
                        searchView.clearFocus();
                    }
                }

            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });

        changeFragment(0);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_header, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchMenu = menu.findItem(R.id.search);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchMenu.expandActionView();

        return super.onCreateOptionsMenu(menu);
    }

    private void handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchView.clearFocus();
            searchMenu.collapseActionView();
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchTitle = "Hasil : " + query;
            toolbar.setTitle(searchTitle);
            EventBus.getDefault().post(
                    new SearchEvent(query)
            );
        }
    }


    void changeFragment(int page) {
        viewPager.setCurrentItem(page, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SnackEvent event) {
        Snackbar.make(coordinator, event.getMessage(), event.getLength())
                .show();
    }
}
