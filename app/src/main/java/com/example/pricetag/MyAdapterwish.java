package com.example.pricetag;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import static com.example.pricetag.R.drawable.ddaa;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.transition.Hold;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyAdapterwish extends RecyclerView.Adapter<MyAdapterwish.MyAdapterViewHolder>{
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    String userID;
    public static final String EXTRA_MESSAGE="MainActivityLabel";
    ArrayList<Productwish> product = new ArrayList<>();
    ViewGroup parent;

    public MyAdapterwish(ArrayList<Productwish> product){
        this.product=product;
    }
    @Override
    public MyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.product_list1,parent,false);
        return new MyAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyAdapterViewHolder holder, final int position) {
        final Productwish p = product.get(position);
        holder.item.setText(p.getTitle());
        holder.price.setText("₹"+p.getPrice());
        // holder.site.setText(p.getsite());
        String site = p.getsite();
        if (site.equals("Flipkart")) {
            holder.logo.setImageResource(R.drawable.flipkartlogo);
        } else if (site.equals("Snapdeal")) {
            holder.logo.setImageResource(R.drawable.snapdeallogo);
        }
        holder.rating.setText(p.getRating());
        holder.itemid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(),ItemDisplay.class);
                intent.putExtra("url",p.getlink());
                intent.putExtra("site",p.getsite());
                parent.getContext().startActivity(intent);
            }

        });
        if(!p.getImage().equals(""))
        Glide.with(parent.getContext()).load(p.getImage()).into(holder.image);
        else
            holder.image.setImageResource(ddaa);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String id = p.getid();
                deleteItem(holder,position);
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference collection = fStore.collection(userID).document(id);
                collection.delete();
            }
        });
    }



    @Override
    public int getItemCount() {
        return product.size();
    }

    private void deleteItem( MyAdapterViewHolder holder,int position) {
        product.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, product.size());
        holder.itemView.setVisibility(View.GONE);
    }
    public class MyAdapterViewHolder extends RecyclerView.ViewHolder{
        LinearLayout itemid;
        TextView item;
        TextView price;
        ImageView image;
        ImageView logo;
        TextView rating;
        TextView remove;
        public MyAdapterViewHolder(View itemView){
            super(itemView);
            item = itemView.findViewById(R.id.textViewTitle);
            price = itemView.findViewById(R.id.textViewPrice);
            image = itemView.findViewById(R.id.imageView);
            logo = itemView.findViewById(R.id.logo);
            rating = itemView.findViewById(R.id.textViewRating);
            itemid = itemView.findViewById(R.id.itemid);
            remove = itemView.findViewById(R.id.remove);
        }
    }

}
