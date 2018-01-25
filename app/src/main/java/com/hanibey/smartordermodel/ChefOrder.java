package com.hanibey.smartordermodel;

import java.util.ArrayList;

/**
 * Created by Tanju on 11.01.2018.
 */

public class ChefOrder {

    public String Key;
    public String ClientKey;
    public String ClientName;
    public String OrderKey;
    public ArrayList<OrderItem> Items = new ArrayList<>();

    public ChefOrder() { }

    public ChefOrder(String key, String clientKey, String clientName, String orderKey, ArrayList<OrderItem> items) {
        super();
        this.Key = key;
        this.ClientKey = clientKey;
        this.ClientName = clientName;
        this.OrderKey = orderKey;
        this.Items = items;
    }

}
