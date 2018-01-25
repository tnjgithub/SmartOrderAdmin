package com.hanibey.smartorderadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.CurrentOrder;
import com.hanibey.smartordermodel.OrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tanju on 11.01.2018.
 */

public class ChefOrderListItemAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<OrderItem> orderItemList;

    Activity activity;

    public ChefOrderListItemAdapter(Activity act, ArrayList<OrderItem> items) {
        this.mInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.orderItemList = items;
        this.activity = act;
    }

    @Override
    public int getCount() {
        return orderItemList.size();
    }

    @Override
    public OrderItem getItem(int position) {
        return orderItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = mInflater.inflate(R.layout.chef_order_item_layout, null);
        TextView txtProductName = (TextView) row.findViewById(R.id.txt_product_name);
        TextView txtQuantity = (TextView) row.findViewById(R.id.txt_quantity);

        OrderItem item = orderItemList.get(position);
        txtProductName.setText(item.ProductName);
        txtQuantity.setText(item.Quantity +" Adet");

        return row;
    }

}
