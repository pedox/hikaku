package com.akafuri25.hikaku.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.util.CustomViewPager;
import com.akafuri25.hikaku.util.ImageSlide;
import com.akafuri25.hikaku.util.Rupiah;
import com.akafuri25.hikaku.util.events.ProductDetailEvent;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.viewpagerindicator.LinePageIndicator;

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

public class ProductDetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.price)
    TextView price;
    @Bind(R.id.source)
    TextView source;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    Product product;
    String url;

    static String TAG = "_detail";
    RequestQueue queue;

    @Bind(R.id.slideImage)
    CustomViewPager slideImage;
    @Bind(R.id.indicator)
    LinePageIndicator indicator;
    @Bind(R.id.from)
    TextView from;
    @Bind(R.id.compareBtn)
    LinearLayout compareBtn;
    @Bind(R.id.wishlistBtn)
    LinearLayout wishlistBtn;
    float density;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        density = getResources().getDisplayMetrics().density;

        url = getString(R.string.url) + "/api/v1/product/" + product.getSource();

        queue = Volley.newRequestQueue(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(product.getName());

        price.setText(Rupiah.parse(product.getPrice()));
        name.setText(getString(R.string.from) + product.getSource());
        name.setText(product.getName());
        source.setText(getString(R.string.from) + product.getSource());
        from.setText(product.getLocation());
        slideImage.setSquare(true);

        request();
    }

    private void request() {
        if (queue != null) {
            queue.cancelAll(TAG);
        }
        Uri.Builder uri = Uri.parse(url).buildUpon();
        uri.appendQueryParameter("id", product.getProductId());
        JsonObjectRequest request = new JsonObjectRequest(uri.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    description.setText(
                            Html.fromHtml(response.getJSONObject("data").getString("description")).toString()
                    );
                    setImage(response.getJSONObject("data").getJSONArray("images"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductDetailActivity.this, "Item not found", Toast.LENGTH_SHORT).show();
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
        for (int i = 0; i < images.length(); i++) {
            slideList.add(images.getString(i));
        }
        ImageSlide adapter = new ImageSlide(getSupportFragmentManager(), slideList);
        slideImage.setAdapter(adapter);
        indicator.setViewPager(slideImage);
        indicator.setSelectedColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        indicator.setUnselectedColor(ContextCompat.getColor(this, R.color.colorPrimary));
        indicator.setLineWidth(20 * density);
        indicator.setGapWidth(5 * density);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(ProductDetailEvent event) {
        product = event.getProduct();
    }

    @OnClick(R.id.gostore)
    public void onClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getUrl()));
        startActivity(browserIntent);
    }
}
