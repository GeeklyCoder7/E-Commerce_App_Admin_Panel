package com.example.e_commerceappadminpanel.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_commerceappadminpanel.R;
import com.example.e_commerceappadminpanel.activities.ViewProductsActivity;
import com.example.e_commerceappadminpanel.databinding.ViewProductdetailsCardLayoutBinding;
import com.example.e_commerceappadminpanel.models.ProductModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ViewProductsAdapter extends RecyclerView.Adapter<ViewProductsAdapter.ViewProductsAdapterViewHolder> {
    ArrayList<ProductModel> productModelArrayList;
    Context context;

    public ViewProductsAdapter(Context context, ArrayList<ProductModel> productModelArrayList) {
        this.productModelArrayList = productModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewProductsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewProductsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.view_productdetails_card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProductsAdapterViewHolder holder, int position) {
        ProductModel productModel = productModelArrayList.get(position);
        Glide.with(context).load(productModel.getProductImage()).into(holder.binding.viewProductImageView);
        holder.binding.productNameTextView.setText(productModel.getProductName());
        holder.binding.productIdTextView.setText(productModel.getProductId());
        holder.binding.productPriceTextView.setText(String.valueOf(productModel.getProductPrice()));
        holder.binding.productCategoryTextView.setText(productModel.getProductCategory());
        holder.binding.productDescriptionTextView.setText(productModel.getProductDescription());
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    public static class ViewProductsAdapterViewHolder extends RecyclerView.ViewHolder {
        ViewProductdetailsCardLayoutBinding binding;
        public ViewProductsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewProductdetailsCardLayoutBinding.bind(itemView);
        }
    }
}
