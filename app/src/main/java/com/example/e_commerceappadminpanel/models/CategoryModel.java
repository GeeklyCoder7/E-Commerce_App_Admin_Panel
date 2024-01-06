package com.example.e_commerceappadminpanel.models;

public class CategoryModel {
    String categoryName, categoryId, categoryIconImage;

    public CategoryModel(String categoryName, String categoryIconImage, String categoryId) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.categoryIconImage = categoryIconImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryIconImage() {
        return categoryIconImage;
    }

    public void setCategoryIconImage(String categoryIconImage) {
        this.categoryIconImage = categoryIconImage;
    }
}
