package com.akafuri25.hikaku.ui.fragments;


import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.data.Compare;
import com.akafuri25.hikaku.data.CompareDao;
import com.akafuri25.hikaku.data.DaoMaster;
import com.akafuri25.hikaku.data.DaoSession;
import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.util.CustomViewPager;
import com.akafuri25.hikaku.util.ImageSlide;
import com.akafuri25.hikaku.util.Rupiah;
import com.akafuri25.hikaku.util.events.UpdateViewEvent;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.viewpagerindicator.LinePageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

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

    String url;

    static String TAG = "_detail";
    RequestQueue queue;
    float density;

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

        queue = Volley.newRequestQueue(getContext());
        density = getResources().getDisplayMetrics().density;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compare = compareDao.load(id);

        if(compare != null) {

            url = getString(R.string.url) + "/api/v1/product/" + compare.getSource();

            name.setText(compare.getName());
            source.setText(compare.getSource());
            from.setText(compare.getLocation());
            price.setText(Rupiah.parse(compare.getPrice()));
            if(compare.getImages().isEmpty()) {
                loadData();
            } else {
                if(isVisible()) {
                    progressBar.setVisibility(View.GONE);
                }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setImage(new JSONArray(compare.getImages()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 100);
                    description.setText(compare.getDescription());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    void loadData() {
        if (queue != null) {
            queue.cancelAll(TAG);
        }
        Uri.Builder uri = Uri.parse(url).buildUpon();
        uri.appendQueryParameter("id", compare.getProductId());
        JsonObjectRequest request = new JsonObjectRequest(uri.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(isVisible()) {
                    progressBar.setVisibility(View.GONE);
                }

                try {
                    if(isVisible()) {
                        String desc = Html.fromHtml(response.getJSONObject("data").getString("description")).toString();
                        description.setText(desc);
                        compare.setDescription(desc);
                        compare.setImages(response.getJSONObject("data").getJSONArray("images").toString());
                        setImage(response.getJSONObject("data").getJSONArray("images"));
                    }
                    compareDao.update(compare);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(isVisible()) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(TAG);
        queue.add(request);
    }

    void setImage(JSONArray images) throws JSONException {
        ArrayList<String> slideList = new ArrayList<>();
        Log.v("SLIDE", compare.getName());
        for (int i = 0; i < images.length(); i++) {
            slideList.add(images.getString(i));
            Log.v("SLIDE-", images.getString(i));
        }
        slideImage.setSquare(true);
        ImageSlide adapter = new ImageSlide(getChildFragmentManager(), slideList);
        slideImage.setAdapter(adapter);
        indicator.setViewPager(slideImage);
        indicator.notifyDataSetChanged();
        indicator.setSelectedColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        indicator.setUnselectedColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        indicator.setLineWidth(20 * density);
        indicator.setGapWidth(5 * density);
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
                                compareDao.deleteByKey(id);
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
