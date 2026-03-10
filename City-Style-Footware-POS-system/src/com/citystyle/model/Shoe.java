package com.citystyle.model;

public class Shoe {
    private int shoeId;
    private String modelName;
    private String size;
    private int minSize;
    private int maxSize;
    private String color;
    private double price;
    private double promotionalPrice;
    private String brandName;
    private String categoryName;
    private String supplierName;
    private int stock;

    public Shoe(int shoeId, String modelName, String brandName, String categoryName, String supplierName, String size,
            int minSize,
            int maxSize, String color,
            double price, double promotionalPrice, int stock) {
        this.shoeId = shoeId;
        this.modelName = modelName;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.supplierName = supplierName;
        this.size = size;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.color = color;
        this.price = price;
        this.promotionalPrice = promotionalPrice;
        this.stock = stock;
    }

    public int getShoeId() {
        return shoeId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSize() {
        return size;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getColor() {
        return color;
    }

    public double getPrice() {
        return price;
    }

    public double getPromotionalPrice() {
        return promotionalPrice;
    }

    public int getStock() {
        return stock;
    }

    public double getEffectivePrice() {
        if (promotionalPrice > 0)
            return promotionalPrice;
        return price;
    }
}
