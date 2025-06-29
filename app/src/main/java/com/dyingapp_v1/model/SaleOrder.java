package com.dyingapp_v1.model;

import java.util.List;

public class SaleOrder {
    private String id;
    private String orderStatus;
    private String orderDate;
    private int orderTotal;

    public SaleOrder(String id, String orderStatus, String orderDate, int orderTotal) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
    }

    public String getId() { return id; }
    public String getOrderStatus() { return orderStatus; }
    public String getOrderDate() { return orderDate; }
    public int getOrderTotal() { return orderTotal; }
}