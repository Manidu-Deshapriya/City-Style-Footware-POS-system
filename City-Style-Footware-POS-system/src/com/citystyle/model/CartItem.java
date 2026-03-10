package com.citystyle.model;

public class CartItem {
    private int shoeId;
    private String modelName;
    private int quantity;
    private double price;
    private String size;
    private String color;

    public CartItem(int shoeId, String modelName, int quantity, double price, String size, String color) {
        this.shoeId = shoeId;
        this.modelName = modelName;
        this.quantity = quantity;
        this.price = price;
        this.size = size;
        this.color = color;
    }

    public int getShoeId() {
        return shoeId;
    }

    public String getModelName() {
        return modelName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public double getTotal() {
        return quantity * price;
    }
}
