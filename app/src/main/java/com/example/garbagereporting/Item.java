package com.example.garbagereporting;

public class Item {
    private String itemId; // Unique item ID
    private String name;    // Name of the item
    private String category; // Category of the item
    private double price;    // Price of the item
    private double quantity; // Quantity of the item
    private String imageUrl; // URL of the uploaded image

    // Default constructor required for Firebase calls
    public Item() {
        // Firebase requires a default constructor
    }

    // Constructor including all fields
    public Item(String itemId, String name, double price, double quantity, String category, String imageUrl) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Constructor without itemId and imageUrl (if you want to create an Item without these fields)
    public Item(String name, String category, double price, double quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
