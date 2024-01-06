package com.example.e_commerceappadminpanel.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.e_commerceappadminpanel.R;
import com.example.e_commerceappadminpanel.databinding.ActivityAddCategoriesBinding;
import com.example.e_commerceappadminpanel.models.CategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddCategoriesActivity extends AppCompatActivity {
    ActivityAddCategoriesBinding binding;
    String categoryName = "";
    Uri categoryImagePath = null;
    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReference = firebaseDatabase.getReference();
    CategoryModel categoryModel;
    final int CATEGORY_ICON_SELECTION_REQ_CODE = 64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initializing variables and objects here

        binding.addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.categoryNameEditText.getText().toString().equals("") || categoryImagePath == null) {
                    if (binding.categoryNameEditText.getText().toString().equals("")) {
                        binding.categoryNameEditText.setError("Please enter the Category Name!");
                    }
                    if (categoryImagePath == null) {
                        Toast.makeText(AddCategoriesActivity.this, "Please select the Category Icon", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    categoryName = binding.categoryNameEditText.getText().toString();
                    categoryModel = new CategoryModel(categoryName, "", ""); //Adding the available values to the category object
                    uploadData(categoryModel);
                }
            }
        });

        binding.categoryIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CATEGORY_ICON_SELECTION_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            categoryImagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(), categoryImagePath);
                binding.categoryIconImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Function for upload the data to the database
    void uploadData(CategoryModel categoryModel) {
        binding.mainLinearLayout.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        DatabaseReference categoryNodeRef = databaseReference.child("categories"); //Creating child node inside main database node or referring to it if it already exists.
        DatabaseReference categoryIdRef = categoryNodeRef.push(); //Generating unique id for each category and also creating unique node for each category using that id.

        categoryModel.setCategoryId(categoryIdRef.getKey());

        //Creating Storage References for storing category icons
        //Note : Here category_images is the node under main node and is storing category icons for all the categories. And it's child with .child(categoryMode.getCategoryId() is used for creating specific node for each category icon under the "category_images" node
        StorageReference categoryIconImagesNodeRef = FirebaseStorage.getInstance().getReference().child("category_images").child(categoryModel.getCategoryId());
        UploadTask uploadTask = categoryIconImagesNodeRef.putFile(categoryImagePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                categoryIconImagesNodeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        categoryModel.setCategoryIconImage(uri.toString());
                        categoryIdRef.setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() { //Note always set value to the idRef to be able to add more than one transactions and avoid updating the previous node everytime
                            @Override
                            public void onSuccess(Void unused) {
                                resetEverything(); //Reset every view to avoid corresponding uploads to the database
                                binding.progressBar.setVisibility(View.GONE);
                                binding.mainLinearLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(AddCategoriesActivity.this, "Category Successfully added", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.mainLinearLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(AddCategoriesActivity.this, "Some Error occurred!", Toast.LENGTH_SHORT).show();
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

    //Function for selecting the category icon
    void selectImage() {
        Intent categoryIconSelectionIntent = new Intent();
        categoryIconSelectionIntent.setType("image/*");
        categoryIconSelectionIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(categoryIconSelectionIntent, "Select Icon From"), CATEGORY_ICON_SELECTION_REQ_CODE);
    }

    //Function for resetting all the views once the data is uploaded to the database successfully
    void resetEverything() {
        binding.categoryNameEditText.setText("");
        binding.categoryIconImageView.setImageResource(R.drawable.add_product_image_placeholder);
        categoryImagePath = null;
    }
}