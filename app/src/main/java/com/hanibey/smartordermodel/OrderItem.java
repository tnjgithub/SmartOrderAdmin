package com.hanibey.smartordermodel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 8.10.2017.
 */

public class OrderItem {

    public String ProductKey;
    public String ProductName;
    public double Price;
    public String Quantity;
    public String Status;

    public OrderItem() { }

    public OrderItem(String productKey, String productName,double price, String quantity, String status) {
        super();
        this.ProductKey = productKey;
        this.ProductName = productName;
        this.Price = price;
        this.Quantity = quantity;
        this.Status = status;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ProductKey", ProductKey);
        result.put("ProductName", ProductName);
        result.put("Price", Price);
        result.put("Quantity", Quantity);
        result.put("Status", Status);

        return result;
    }
}
