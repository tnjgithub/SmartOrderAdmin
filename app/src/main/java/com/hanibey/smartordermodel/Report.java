package com.hanibey.smartordermodel;

import java.util.ArrayList;

/**
 * Created by Tanju on 19.01.2018.
 */

public class Report {
    public String OrderCount;
    public double TotalAmount;
    public ArrayList<OrderItem> Items = new ArrayList<>();

    public Report() { }

    public Report(String orderCount, double totalAmount, ArrayList<OrderItem> items) {
        super();
        this.OrderCount = orderCount;
        this.TotalAmount = totalAmount;
        this.Items = items;
    }
}
