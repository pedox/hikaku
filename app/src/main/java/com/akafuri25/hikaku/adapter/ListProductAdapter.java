package com.akafuri25.hikaku.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
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
import android.widget.Toast;

import com.akafuri25.hikaku.R;
import com.akafuri25.hikaku.data.Compare;
import com.akafuri25.hikaku.data.CompareDao;
import com.akafuri25.hikaku.data.DaoMaster;
import com.akafuri25.hikaku.data.DaoSession;
import com.akafuri25.hikaku.data.Product;
import com.akafuri25.hikaku.data.Wishlist;
import com.akafuri25.hikaku.data.WishlistDao;
import com.akafuri25.hikaku.ui.ProductDetailActivity;
import com.akafuri25.hikaku.util.CompareData;
import com.akafuri25.hikaku.util.Rupiah;
import com.akafuri25.hikaku.util.SquareImage;
import com.akafuri25.hikaku.util.events.CompareDataEvent;
import com.akafuri25.hikaku.util.events.CompareIdEvent;
import com.akafuri25.hikaku.util.events.ProductDetailEvent;
import com.akafuri25.hikaku.util.events.SnackEvent;
import com.akafuri25.hikaku.util.events.UpdateViewEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.hanks.library.SmallBang;

/**
 * Created by pedox on 4/8/16.
 */
public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ViewHolder> {

    List<Product> productList;
    ArrayList<String> compareListId;
    ArrayList<Product> compareData;
    DaoMaster.DevOpenHelper helper;
    DaoSession session;
    WishlistDao wishlistDao;
    CompareDao compareDao;
    SQLiteDatabase db;


    public ListProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        helper = new DaoMaster.DevOpenHelper(context, context.getString(R.string.dbName), null);
    }

    @Override
    public ListProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder h, int i) {

        final Product d = productList.get(i);
        h.title.setText(d.getName());
        h.price.setText(Rupiah.parse(d.getPrice()));
        h.source.setText("From : " + d.getSource());
        Picasso.with(h.itemView.getContext())
                .load(d.getImage())
                .into(h.image);


        assert h.image != null;
        h.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new ProductDetailEvent(d));
                h.itemView.getContext().startActivity(
                        new Intent(h.itemView.getContext(), ProductDetailActivity.class)
                );
            }
        });

        assert h.compareBtn != null;
        h.compareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Tambah ya ke Compare Lists
                 */
                db = helper.getWritableDatabase();
                session = new DaoMaster(db).newSession();
                compareDao = session.getCompareDao();
                if(compareDao.load(d.getProductId()) != null) {
                    Toast.makeText(h.itemView.getContext(), "Already in Compare lists", Toast.LENGTH_SHORT).show();
                } else {
                    if(compareDao.count() < 5) {
                        Toast.makeText(h.itemView.getContext(), "Added to compare", Toast.LENGTH_SHORT).show();
                        compareDao.insertOrReplace(new Compare(
                                d.getProductId(),
                                d.getName(),
                                d.getPrice(),
                                d.getUrl(),
                                d.getSource(),
                                "", "", d.getLocation()
                        ));
                        EventBus.getDefault().postSticky(new UpdateViewEvent(UpdateViewEvent.COMPARE, compareDao.loadAll()));
                        db.close();

                    } else {
                        Toast.makeText(h.itemView.getContext(), "Maximum compare data is 5", Toast.LENGTH_SHORT).show();
                    }
                }
                db.close();
            }
        });

        assert h.wishlistBtn != null;
        h.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Tambah ya ke Wish list Lists
                 */
                db = helper.getWritableDatabase();
                session = new DaoMaster(db).newSession();
                wishlistDao = session.getWishlistDao();
                if(wishlistDao.load(d.getProductId()) != null) {
                    Toast.makeText(h.itemView.getContext(), "Already in Wish lists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(h.itemView.getContext(), "Added to Wish lists", Toast.LENGTH_SHORT).show();
                    wishlistDao.insertOrReplace(new Wishlist(
                            d.getProductId(),
                            d.getName(),
                            d.getPrice(),
                            d.getUrl(),
                            d.getSource(),
                            d.getImage(),
                            d.getLocation()
                    ));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().postSticky(new UpdateViewEvent(UpdateViewEvent.WISHLIST));
                        }
                    }, 150);
                }
                db.close();
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


    public void setCompareListId(ArrayList<String> compareListId) {
        this.compareListId = compareListId;
    }

    public void setCompareData(ArrayList<Product> compareData) {
        this.compareData = compareData;
    }
}
