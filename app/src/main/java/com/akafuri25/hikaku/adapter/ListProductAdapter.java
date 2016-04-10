package com.akafuri25.hikaku.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.util.SquareImage;
import com.akafuri25.hikaku.util.events.SnackEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pedox on 4/8/16.
 */
public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ViewHolder> {

    List<Product> productList;

    public ListProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public ListProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        Product d = productList.get(i);
        h.title.setText(d.getName());
        h.price.setText("Rp -. " + d.getPrice());
        h.source.setText("From : " + d.getSource());
        Picasso.with(h.itemView.getContext())
                .load(d.getImage())
                .into(h.image);

        h.compareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SnackEvent("Added to compare", Snackbar.LENGTH_SHORT));
            }
        });

        h.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SnackEvent("Added to Wish lists", Snackbar.LENGTH_SHORT));
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable @Bind(R.id.title)
        TextView title;
        @Nullable @Bind(R.id.price)
        TextView price;
        @Nullable @Bind(R.id.source)
        TextView source;
        @Nullable @Bind(R.id.image)
        SquareImage image;
        @Nullable @Bind(R.id.compareBtn)
        LinearLayout compareBtn;
        @Nullable @Bind(R.id.wishlistBtn)
        LinearLayout wishlistBtn;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
