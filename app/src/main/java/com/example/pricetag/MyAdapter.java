package com.example.pricetag;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyAdapterViewHolder>{


    public static final String EXTRA_MESSAGE="MainActivityLabel";
    Product[] product;
    ViewGroup parent;
    public MyAdapter(Product[] product){
        this.product=product;
    }
    @Override
    public MyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.product_list,parent,false);
        return new MyAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyAdapterViewHolder holder, int position) {
        final Product p = product[position];
        holder.item.setText(p.getTitle());
        holder.price.setText("₹"+Float.toString((p.getPrice())));
        // holder.site.setText(p.getsite());
        String site = p.getsite();
        if (site.equals("Flipkart")) {
            holder.logo.setImageResource(R.drawable.flipkartlogo);
        } else if (site.equals("Snapdeal")) {
            holder.logo.setImageResource(R.drawable.snapdeallogo);
        }
        holder.rating.setText(Float.toString(p.getRating()));
        holder.itemid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(),ItemDisplay.class);
                intent.putExtra("url",p.getlink());
                intent.putExtra("site",p.getsite());
                parent.getContext().startActivity(intent);
            }

        });
        //   holder.image.setImageBitmap(p.getImage());

    }



    @Override
    public int getItemCount() {
        return product.length;
    }


    public class MyAdapterViewHolder extends RecyclerView.ViewHolder{
        LinearLayout itemid;
        TextView item;
        TextView price;
        ImageView image;
        ImageView logo;
        TextView rating;
        public MyAdapterViewHolder(View itemView){
            super(itemView);
            item = itemView.findViewById(R.id.textViewTitle);
            price = itemView.findViewById(R.id.textViewPrice);
            image = itemView.findViewById(R.id.imageView);
            logo = itemView.findViewById(R.id.logo);
            rating = itemView.findViewById(R.id.textViewRating);
            itemid = itemView.findViewById(R.id.itemid);
        }
    }

}
