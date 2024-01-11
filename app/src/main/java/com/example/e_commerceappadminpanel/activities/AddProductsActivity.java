package com.example.e_commerceappadminpanel.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.e_commerceappadminpanel.R;
import com.example.e_commerceappadminpanel.databinding.ActivityAddProductsBinding;
import com.example.e_commerceappadminpanel.models.CategoryModel;
import com.example.e_commerceappadminpanel.models.ProductModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

public class AddProductsActivity extends AppCompatActivity {
    ActivityAddProductsBinding binding;
    String productName = "", productDescription = "", productCategory = "";
    Float productPrice;
    ProductModel productModel;
    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReference = firebaseDatabase.getReference();
    static final int PRODUCT_IMAGE_SELECTION_REQ_CODE = 27;
    Uri productImagePath = null;
    DatabaseReference categoriesNodeRef;
    ArrayList<String> categoryNamesArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initializing variables and objects
        categoriesNodeRef = firebaseDatabase.getReference().child("categories");

        //Calling necessary functions here
        setUpCategoriesSpinner();

        binding.addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.productNameEditText.getText().toString().equals("") || binding.productPriceEditText.getText().toString().equals("") || binding.productDescriptionEditText.getText().toString().equals("") || productImagePath == null) {
                    if (binding.productNameEditText.getText().toString().equals("")) {
                        binding.productNameEditText.setError("Please Enter the name of the Product!");
                    }
                    if (binding.productPriceEditText.getText().toString().equals("")) {
                        binding.productPriceEditText.setError("Please set the price for the Product!");
                    }
                    if (binding.productDescriptionEditText.getText().toString().equals("")) {
                        binding.productDescriptionEditText.setError("Please Describe your Product!");
                    }
                    if (productImagePath == null) {
                        Toast.makeText(AddProductsActivity.this, "Please select the Product Image!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    productName = binding.productNameEditText.getText().toString();
                    productDescription = binding.productDescriptionEditText.getText().toString();
                    productPrice = Float.valueOf(binding.productPriceEditText.getText().toString());

                    productModel = new ProductModel(productName, productDescription, productCategory, "", productPrice, ""); //Adding the product details to the model

                    uploadData(productModel);
                }
            }
        });

        binding.productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProductImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCT_IMAGE_SELECTION_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            productImagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(), productImagePath);

                binding.productImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Function for selection product image
    void selectProductImage() {
        Intent selectImageIntent = new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(selectImageIntent, "Select Image From"), PRODUCT_IMAGE_SELECTION_REQ_CODE);
    }

    // Function for uploading the data on firebase
    void uploadData(ProductModel productModel) {
        binding.mainLinearLayout.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        DatabaseReference productsNodeRef = databaseReference.child("products");
        DatabaseReference productIdRef = productsNodeRef.push();

        productModel.setProductId(productIdRef.getKey());
        productModel.setProductCategory(productCategory);

        StorageReference productsImagesNodeRef = FirebaseStorage.getInstance().getReference().child("products_images").child(productModel.getProductId());
        UploadTask uploadTask = productsImagesNodeRef.putFile(productImagePath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                productsImagesNodeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        productModel.setProductImage(uri.toString());
                        productIdRef.setValue(productModel).addOnSuccessListener(new OnSuccessListener<Void>() {   //Note always set value to the idRef to be able to add more than one transactions and avoid updating the previous node everytime
                            @Override
                            public void onSuccess(Void unused) {
                                resetEverything(); //Reset every view to avoid corresponding uploads to the database
                                binding.progressBar.setVisibility(View.GONE);
                                binding.mainLinearLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(AddProductsActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.mainLinearLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(AddProductsActivity.this, "Something error occurred!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //Function for resetting all the views once the data is uploaded to the database successfully
    void resetEverything() {
        binding.productNameEditText.setText("");
        binding.productPriceEditText.setText("");
        binding.productDescriptionEditText.setText("");
        binding.productImageView.setImageResource(R.drawable.add_product_image_placeholder);
        productImagePath = null;
    }

    void setUpCategoriesSpinner() {
        categoriesNodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categoryNames = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryModel categoryModel = categorySnapshot.getValue(CategoryModel.class);
                    if (categoryModel != null) {
                        categoryNames.add(categoryModel.getCategoryName());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProductsActivity.this, android.R.layout.simple_spinner_item, categoryNames);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.productCategorySpinner.setAdapter(adapter);

                binding.productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        productCategory = categoryNames.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(AddProductsActivity.this, "Failed to fetch category names!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductsActivity.this, "Failed to Fetch Category Names!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}