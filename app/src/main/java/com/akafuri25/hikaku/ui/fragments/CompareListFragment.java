package com.akafuri25.hikaku.ui.fragments;


import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.data.Compare;
import com.akafuri25.hikaku.data.CompareDao;
import com.akafuri25.hikaku.data.DaoMaster;
import com.akafuri25.hikaku.data.DaoSession;
import com.akafuri25.hikaku.util.CustomViewPager;
import com.akafuri25.hikaku.util.events.CompareDataEvent;
import com.akafuri25.hikaku.util.events.RemoveCompareDataEvent;
import com.akafuri25.hikaku.util.events.UpdateViewEvent;
import com.viewpagerindicator.LinePageIndicator;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompareListFragment extends Fragment {


    String id;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.slideImage)
    CustomViewPager slideImage;
    @Bind(R.id.indicator)
    LinePageIndicator indicator;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.from)
    TextView from;
    @Bind(R.id.price)
    TextView price;
    @Bind(R.id.source)
    TextView source;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.gostore)
    Button gostore;
    @Bind(R.id.compareBtn)
    Button compareBtn;

    SQLiteDatabase db;
    DaoSession session;
    CompareDao compareDao;
    Compare compare;

    public CompareListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.compare_detail, container, false);
        ButterKnife.bind(this, view);
        id = getArguments().getString("id");

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), getContext().getString(R.string.dbName), null);
        db = helper.getWritableDatabase();
        session = new DaoMaster(db).newSession();
        compareDao = session.getCompareDao();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("CREATED", id);
        compare = compareDao.load(id);
        if(compare != null) {
            name.setText(compare.getName());
            source.setText(compare.getSource());
            from.setText(compare.getLocation());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.gostore, R.id.compareBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gostore:
                break;
            case R.id.compareBtn:
                new AlertDialog.Builder(getContext()).setMessage("Yakin ingin menghapus ?")
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface d, int i) {
                                Log.v("DELETE", id);
                                compareDao.deleteByKey(id);
                                for(Compare c : compareDao.loadAll()) {
                                    Log.v("COMPARE->", c.getProductId());
                                    Log.v("COMPARE->", c.getName());
                                }
                                EventBus.getDefault().post(new UpdateViewEvent(UpdateViewEvent.COMPARE, compareDao.loadAll()));
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                d.dismiss();
                            }
                        }).show();
                break;
        }
    }
}
