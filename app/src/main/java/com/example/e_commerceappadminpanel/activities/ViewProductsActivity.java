package com.example.e_commerceappadminpanel.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.health.connect.changelog.ChangeLogTokenRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.e_commerceappadminpanel.Adapters.ViewProductsAdapter;
import com.example.e_commerceappadminpanel.R;
import com.example.e_commerceappadminpanel.databinding.ActivityViewProductsBinding;
import com.example.e_commerceappadminpanel.models.CategoryModel;
import com.example.e_commerceappadminpanel.models.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewProductsActivity extends AppCompatActivity {
    ActivityViewProductsBinding binding;
    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReference = firebaseDatabase.getReference();
    ArrayList<ProductModel> productModelArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initializing variables and objects
        productModelArrayList = new ArrayList<>();

        binding.viewProductsRecyclerView.setVisibility(View.GONE);
        binding.viewProductsProgressBar.setVisibility(View.VISIBLE);

        //Calling necessary functions here
        getALLProducts();
    }

    //Function for getting all the products and their details
    void getALLProducts() {
        DatabaseReference productsNodeRef = databaseReference.child("products");
        productsNodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productsSnapshot : snapshot.getChildren()) {
                    ProductModel productModel = productsSnapshot.getValue(ProductModel.class);
                    if (productModel != null) {
                        productModelArrayList.add(productModel);
                    }
                }
                setupRecyclerView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProductsActivity.this, "Failed to fetch Products!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function for setting up the recyclerview
    @SuppressLint("NotifyDataSetChanged")
    void setupRecyclerView() {
        ViewProductsAdapter viewProductsAdapter = new ViewProductsAdapter(ViewProductsActivity.this, productModelArrayList);
        binding.viewProductsRecyclerView.setLayoutManager(new LinearLayoutManager(ViewProductsActivity.this));
        binding.viewProductsRecyclerView.setAdapter(viewProductsAdapter);
        viewProductsAdapter.notifyDataSetChanged();
        binding.viewProductsProgressBar.setVisibility(View.GONE);
        binding.viewProductsRecyclerView.setVisibility(View.VISIBLE);
    }
}