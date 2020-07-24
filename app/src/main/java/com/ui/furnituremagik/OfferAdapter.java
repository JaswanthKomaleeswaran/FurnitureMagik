package com.ui.furnituremagik;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ImageViewHolder> {
    ArrayList<OfferDetails> offerList;
    Context context;
    OfferDetails offerDetails;
    private SQLiteDatabase sqLiteDatabase;
    private String sql;
    File file;


    public OfferAdapter(Activity activity, ArrayList<OfferDetails> offerList,SQLiteDatabase sqLiteDatabase) {
        context=activity;
        this.offerList=offerList;
        this.sqLiteDatabase=sqLiteDatabase;
    }

    @NonNull
    @Override
    public OfferAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View offerView = LayoutInflater.from(context).inflate(R.layout.offers_view, parent, false);
        return new OfferAdapter.ImageViewHolder(offerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        offerDetails=offerList.get(position);
        holder.product_name.setText(offerDetails.productName);
        holder.offer_item.setText(offerDetails.productItem);
        holder.price.setText("Rs. "+offerDetails.price);
        holder.discount_price.setText("Rs. "+offerDetails.discountPrice);
        holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.offer_image.setImageDrawable(Drawable.createFromPath(offerDetails.path));
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView product_name, offer_item, price, discount_price;
        ImageView offer_image,delete_offer;


        public ImageViewHolder(View offerView) {
            super(offerView);
            product_name = offerView.findViewById(R.id.product_name);
            offer_item = offerView.findViewById(R.id.offer_item);
            price = offerView.findViewById(R.id.price);
            discount_price = offerView.findViewById(R.id.discount_price);
            offer_image = offerView.findViewById(R.id.offer_image);
            delete_offer = offerView.findViewById(R.id.delete_offer);
            delete_offer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            offerDetails = offerList.get(position);
            try {
                sql="DELETE from offers_table where offerId = '"+offerDetails.offerId+"'";
                sqLiteDatabase.execSQL(sql);
                file=new File(offerDetails.path);
                file.delete();
                Snackbar.make(view, "Item Deleted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            catch (Exception e) {}
            notifyDataSetChanged();
        }
    }
}
