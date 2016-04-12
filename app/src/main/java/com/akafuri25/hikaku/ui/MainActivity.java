package com.akafuri25.hikaku.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.ui.fragments.CompareFragment;
import com.akafuri25.hikaku.ui.fragments.SearchFragment;
import com.akafuri25.hikaku.ui.fragments.WishlistFragment;
import com.akafuri25.hikaku.util.events.SearchEvent;
import com.akafuri25.hikaku.util.events.SnackEvent;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    SearchView searchView;
    MenuItem searchMenu;
    @Bind(R.id.fragmentView)
    FrameLayout fragmentView;
    @Bind(R.id.coordinator)
    CoordinatorLayout coordinator;

//    @Bind(R.id.viewPager)
//    ViewPager viewPager;
    String searchTitle = "Hikaku";

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

        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                switch (menuItemId) {
                    case R.id.search:
                        changeFragment(searchFragment);
                        toolbar.setTitle(searchTitle);
                        break;
                    case R.id.compare:
                        changeFragment(new CompareFragment());
                        toolbar.setTitle("Compare");
                        break;
                    case R.id.wishlist:
                        changeFragment(new WishlistFragment());
                        toolbar.setTitle("Wish Lists");
                        break;
                    default:
                        changeFragment(new SearchFragment());
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

        changeFragment(searchFragment);

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
            searchTitle = "Result for : " + query;
            toolbar.setTitle(searchTitle);
            EventBus.getDefault().post(
                    new SearchEvent(query)
            );
        }
    }


    void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentView, fragment)
                .commit();
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
