package com.example.marketnamqul.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketnamqul.Interface.ItemClickListener;
import com.example.marketnamqul.R;
import com.squareup.picasso.Picasso;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView textProductName, textProductDescription, textProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        textProductName = (TextView) itemView.findViewById(R.id.product_name);
        textProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        textProductPrice = (TextView) itemView.findViewById(R.id.product_price);

    }

    public void  setItemClickListner(ItemClickListener listner)
    {
        this.listener = listner;
    }


    @Override
    public void onClick(View v)
    {
        listener.onClick(v, getAdapterPosition(),false);
    }
}
