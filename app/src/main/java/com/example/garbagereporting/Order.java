package com.example.garbagereporting;

public class Order {
    private int orderNo;
    private String itemsWithPricing;
    private double totalAmount;

    public Order(int orderNo, String itemsWithPricing, double totalAmount) {
        this.orderNo = orderNo;
        this.itemsWithPricing = itemsWithPricing;
        this.totalAmount = totalAmount;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public String getItemsWithPricing() {
        return itemsWithPricing;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Order No: " + orderNo + "\nItems:\n" + itemsWithPricing + "\nTotal: $" + totalAmount;
    }
}
