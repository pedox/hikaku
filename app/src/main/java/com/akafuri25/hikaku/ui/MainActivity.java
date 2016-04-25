package com.akafuri25.hikaku.ui;

import android.app.SearchManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.data.DaoMaster;
import com.akafuri25.hikaku.ui.fragments.CompareFragment;
import com.akafuri25.hikaku.ui.fragments.SearchFragment;
import com.akafuri25.hikaku.ui.fragments.WishlistFragment;
import com.akafuri25.hikaku.util.CustomViewPager;
import com.akafuri25.hikaku.util.MainFragmentManager;
import com.akafuri25.hikaku.util.events.CompareIdEvent;
import com.akafuri25.hikaku.util.events.SearchEvent;
import com.akafuri25.hikaku.util.events.SnackEvent;
import com.akafuri25.hikaku.util.events.UpdateViewEvent;
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
    MenuItem deleteMenu;
    boolean compareListIsEmpty = true;

    ArrayList<String> compareListId = new ArrayList<>();

    String searchTitle = "Hikaku";

    MainFragmentManager adapter;
    ArrayList<Fragment> fragments = new ArrayList<>();

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


        new DaoMaster(new DaoMaster.DevOpenHelper(this, getString(R.string.dbName), null)
                .getReadableDatabase()).newSession().getCompareDao().deleteAll();

        mBottomBar = BottomBar.attach(this, savedInstanceState);

        final SearchFragment searchFragment = new SearchFragment();
        final CompareFragment compareFragment = new CompareFragment();
        final WishlistFragment wishlistFragment = new WishlistFragment();
        fragments.add(searchFragment);
        fragments.add(compareFragment);
        fragments.add(wishlistFragment);
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
                        deleteMenu.setVisible(false);
                    }
                } else if(menuItemId == R.id.compare) {
                    if (deleteMenu != null) {
                        searchMenu.setVisible(false);
                        if(!compareListIsEmpty)
                            deleteMenu.setVisible(true);
                        searchMenu.collapseActionView();
                        searchView.clearFocus();
                    }
                } else {
                    if (searchMenu != null) {
                        searchMenu.setVisible(false);
                        searchMenu.collapseActionView();
                        searchView.clearFocus();
                        deleteMenu.setVisible(false);
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
        deleteMenu = menu.findItem(R.id.deleteAllCompare);
        deleteMenu.setVisible(false);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchMenu.expandActionView();

        deleteMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Are you sure to reset compare list ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                EventBus.getDefault().post(new UpdateViewEvent(UpdateViewEvent.RESET_COMPARE));
                                d.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                d.dismiss();
                            }
                        }).show();
                return false;
            }
        });

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
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CompareIdEvent event) {
        compareListId.add(event.getNewId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEvent(UpdateViewEvent event) {
        if(event.getType() == UpdateViewEvent.COMPARE_DELETE_FALSE) {
            compareListIsEmpty = true;
            deleteMenu.setVisible(false);
        }
        if(event.getType() == UpdateViewEvent.COMPARE_DELETE_TRUE) {
            compareListIsEmpty = false;
        }
    }

}
