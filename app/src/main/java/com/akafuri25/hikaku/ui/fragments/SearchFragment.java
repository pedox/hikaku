package com.akafuri25.hikaku.ui.fragments;


import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.adapter.ListProductAdapter;
import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.util.EndlessRecyclerOnScrollListener;
import com.akafuri25.hikaku.util.events.CompareDataEvent;
import com.akafuri25.hikaku.util.events.CompareIdEvent;
import com.akafuri25.hikaku.util.events.SearchEvent;
import com.akafuri25.hikaku.util.events.SearchStickyEvent;
import com.akafuri25.hikaku.util.events.SnackEvent;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    @Bind(R.id.introduction)
    LinearLayout introduction;
    @Bind(R.id.mainTitle)
    TextView mainTitle;
    @Bind(R.id.subTitle)
    TextView subTitle;
    @Bind(R.id.list_item)
    RecyclerView listItem;

    String url;
    String keyword;
    static String TAG = "_search";

    ArrayList<String> compareListId = new ArrayList<>();
    ArrayList<Product> compareData = new ArrayList<>();

    RequestQueue queue;
    @Bind(R.id.loadingBar)
    LinearLayout loadingBar;
    LinearLayoutManager linearLayoutManager;
    ListProductAdapter adapter;
    ArrayList<Product> listProduct = new ArrayList<>();
    int currSize;
    boolean stickyEvent = true;
    boolean isloading = false;
    int filter = 1;
    @Bind(R.id.filterText)
    TextView filterText;
    @Bind(R.id.filterBtn)
    FrameLayout filterBtn;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        queue = Volley.newRequestQueue(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, v);
        stickyEvent = true;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        url = getString(R.string.url) + "/api/v1/search";

        linearLayoutManager = new LinearLayoutManager(getActivity());
        listItem.setLayoutManager(linearLayoutManager);
    }

    private void parseData(JSONObject data) {
        try {
            JSONArray da = data.getJSONArray("data");
            if (data.getInt("page") == 1) {
                listProduct.clear();
            }
            for (int i = 0; i < da.length(); i++) {
                JSONObject d = da.getJSONObject(i);
                listProduct.add(
                        new Product(
                                d.getString("id"),
                                d.getString("name"),
                                d.getDouble("price"),
                                d.getString("url"),
                                d.getString("source"),
                                d.getString("image"),
                                d.getString("location")
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadList(JSONObject data) {
        adapter = new ListProductAdapter(listProduct, getContext());
        if (listProduct.size() > 0) {
            try {
                if (data.getInt("page") > 1 && !stickyEvent) {
                    adapter.notifyItemRangeInserted(currSize, listProduct.size() - 1);
                } else {
                    listItem.setVisibility(View.VISIBLE);
                    introduction.setVisibility(View.GONE);
                    adapter.setCompareListId(compareListId);
                    adapter.setCompareData(compareData);
                    listItem.setAdapter(adapter);
                    listItem.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {
                            if (!isloading) {
                                requestData(page);
                                currSize = adapter.getItemCount();
                                isloading = true;
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mainTitle.setText(R.string.not_foud_title);
            subTitle.setText(R.string.not_foud_subtitle);
            listItem.setVisibility(View.GONE);
            introduction.setVisibility(View.VISIBLE);
        }
    }

    private void requestData(int page) {
        if (queue != null) {
            queue.cancelAll(TAG);
        }
        filterBtn.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
        Uri.Builder uri = Uri.parse(url).buildUpon();
        uri.appendQueryParameter("keyword", keyword);
        uri.appendQueryParameter("filter", String.valueOf(filter));
        uri.appendQueryParameter("page", String.valueOf(page));
        JsonObjectRequest request = new JsonObjectRequest(uri.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isloading = false;
                EventBus.getDefault().postSticky(new SearchStickyEvent(response));
                loadingBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isloading = false;
                loadingBar.setVisibility(View.GONE);

                NetworkResponse err = error.networkResponse;
                String msg;
                if (err != null) {
                    switch (err.statusCode) {
                        case 404:
                            msg = "Instance not found";
                            break;
                        case 500:
                            msg = "Server crashed !";
                            break;
                        default:
                            msg = "Error contacting server";
                            break;
                    }

                    EventBus.getDefault().post(new SnackEvent(msg));
                }

                new AlertDialog.Builder(getContext())
                        .setMessage("Terjadi Kesalahan pada Server")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                d.dismiss();
                            }
                        }).show();

                Toast.makeText(getContext(), "Terjadi Kesalahan pada Server", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("scrollY", listItem.getScrollY());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle state) {
        super.onViewStateRestored(state);
        if (state != null) {
            listItem.setScrollY(state.getInt("scrollY"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SearchEvent event) {
        keyword = event.getKeyword();
        requestData(1);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN, priority = 0)
    public void onEventSticky(SearchStickyEvent event) {
//        if(stickyEvent) {
//            loadList(event.getData());
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onEvent(SearchStickyEvent event) {
        stickyEvent = false;
        parseData(event.getData());
        loadList(event.getData());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(CompareDataEvent event) {
        compareListId = event.getCompareListId();
        compareData = event.getCompareDatas();
    }

    @OnClick(R.id.filterBtn)
    public void onClick() {
        AlertDialog dialog;
        AlertDialog.Builder menu = new AlertDialog.Builder(getContext());
        menu.setTitle("Urutkan");
        menu.setItems(R.array.filter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Log.v("Selected", String.valueOf(i));

                switch (i) {
                    case 0:
                        filterText.setText("Berdasarkan : Relevansi");
                        break;
                    case 1:
                        filterText.setText("Berdasarkan : Harga Terendah");
                        break;
                    case 2:
                        filterText.setText("Berdasarkan : Harga Tertinggi");
                        break;
                }
                filter = i;

                if(listProduct.size() > 0)
                    requestData(1);
            }
        });
        menu.create().show();

    }
}
