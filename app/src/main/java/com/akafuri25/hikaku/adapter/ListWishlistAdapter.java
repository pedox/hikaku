package com.akafuri25.hikaku.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.akafuri25.hikaku.util.Rupiah;
import com.akafuri25.hikaku.util.SquareImage;
import com.akafuri25.hikaku.util.events.ProductDetailEvent;
import com.akafuri25.hikaku.util.events.UpdateViewEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pedox on 4/8/16.
 */
public class ListWishlistAdapter extends RecyclerView.Adapter<ListWishlistAdapter.ViewHolder> {

    List<Wishlist> productList;
    DaoMaster.DevOpenHelper helper;
    DaoSession session;
    WishlistDao wishlistDao;
    SQLiteDatabase db;


    public ListWishlistAdapter(List<Wishlist> productList, Context context) {
        this.productList = productList;
        helper = new DaoMaster.DevOpenHelper(context, context.getString(R.string.dbName), null);
    }

    public void setProductList(List<Wishlist> productList) {
        this.productList = productList;
        this.notifyDataSetChanged();
    }

    @Override
    public ListWishlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_wishlist, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder h, final int i) {

        final Wishlist d = productList.get(i);
        h.title.setText(d.getName());
        h.price.setText(Rupiah.parse(d.getPrice()));
        h.source.setText("From : " + d.getSource());
        Picasso.with(h.itemView.getContext())
                .load(d.getImage())
                .into(h.image);


        assert h.productHolder != null;
        h.productHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new ProductDetailEvent(
                        new Product(
                                d.getProductId(),
                                d.getName(),
                                d.getPrice(),
                                d.getUrl(),
                                d.getSource(),
                                d.getImage(),
                                d.getLocation())
                ));
                h.itemView.getContext().startActivity(
                        new Intent(h.itemView.getContext(), ProductDetailActivity.class)
                );
            }
        });

        assert h.wishlistBtn != null;
        h.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Tambah ya ke Wish list Lists
                 */
                new AlertDialog.Builder(view.getContext())
                        .setMessage("Anda yakin ingin menghapus dari daftar wish list ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int ix) {

                                db = helper.getWritableDatabase();
                                session = new DaoMaster(db).newSession();
                                wishlistDao = session.getWishlistDao();
                                wishlistDao.delete(d);
                                productList = wishlistDao.loadAll();
                                notifyItemRemoved(i);
                                EventBus.getDefault().postSticky(new UpdateViewEvent(UpdateViewEvent.WISHLIST_DELETE));
                                db.close();
                                di.dismiss();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                d.dismiss();
                            }
                        }).show();
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
        @Nullable @Bind(R.id.wishlistBtn)
        LinearLayout wishlistBtn;
        @Nullable @Bind(R.id.productHolder)
        LinearLayout productHolder;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
