package com.hanibey.smartordermodel;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tanju on 8.10.2017.
 */

public class Order {

    public String Key;
    public String ClientKey;
    public String Description;
    public double TotalPrice;
    public String CreateDate;
    public String UpdateDate;
    public String Status;
    public ArrayList<OrderItem> Items = new ArrayList<>();

    public Order() { }

    public Order(String key, String clientKey, String description, double totalPrice, String createDate, String updateDate, String status, ArrayList<OrderItem> items) {
        super();
        this.Key = key;
        this.ClientKey = clientKey;
        this.Description = description;
        this.TotalPrice = totalPrice;
        this.CreateDate = createDate;
        this.UpdateDate = updateDate;
        this.Status = status;
        this.Items = items;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Key", Key);
        result.put("ClientKey", ClientKey);
        result.put("Description", Description);
        result.put("TotalPrice", TotalPrice);
        result.put("CreateDate", CreateDate);
        result.put("UpdateDate", UpdateDate);
        result.put("Status", Status);
        result.put("Items", Items);

        return result;
    }
}
