package com.example.e_commerceappadminpanel.models;

public class ProductModel {
    String productName, productDescription, productCategory, productImage, productId;
    Float productPrice;

    public ProductModel(String productName, String productDescription, String productCategory, String productImage, Float productPrice, String productId) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.productId = productId;
    }

    public ProductModel() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
