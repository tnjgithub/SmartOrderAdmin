package com.hanibey.smartordermodel;

/**
 * Created by Tanju on 29.12.2017.
 */

public class CurrentOrder {

    public String ClientKey;
    public Order Order;

    public CurrentOrder(String clientKey, Order order) {
        super();
        this.ClientKey = clientKey;
        this.Order = order;
    }

}
