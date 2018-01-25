package com.hanibey.smartorderadapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderbusiness.OrderService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.ChefOrder;
import com.hanibey.smartordermodel.Order;
import com.hanibey.smartordermodel.OrderItem;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tanju on 11.01.2018.
 */


public class ChefOrderListAdapter extends RecyclerView.Adapter<ChefOrderListAdapter.ViewHolder> {

    DatabaseReference orderRef;
    OrderService orderService;

    private List<ChefOrder> orderList;

    Activity activity;
    public ChefOrderListAdapter(Activity act, List<ChefOrder> orderItems) {
        super();
        this.orderList = orderItems;
        this.activity = act;
        this.orderRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Order);
        this.orderService = new OrderService();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chef_order_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        ChefOrder chefOrder = orderList.get(i);
        final String clientKey = chefOrder.ClientKey;
        final String orderKey = chefOrder.OrderKey;

        viewHolder.txtClientName.setText(chefOrder.ClientName);
        String[][] productNamesandQuantity = getProductNamesandQuantity(chefOrder.Items);
        viewHolder.txtProductName.setText(productNamesandQuantity[0][0]);
        viewHolder.txtQuantity.setText(productNamesandQuantity[0][1]);

        viewHolder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderRef.child(clientKey).child(orderKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ProgressDialog progressDialog = ProgressDialog.show(activity, "Lütfen Bekleyin...", "İşlem gerçekleşiyor...", false, false);

                        Order order = dataSnapshot.getValue(Order.class);
                        orderService.changeOrderStatus(order, Constant.OrderStatus.OnTheTable);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtClientName, txtProductName, txtQuantity;
        Button btnStatus;

        private ViewHolder(View itemView) {
            super(itemView);
            btnStatus = (Button) itemView.findViewById(R.id.btn_status);
            txtClientName = (TextView) itemView.findViewById(R.id.txt_client_name);
            txtProductName = (TextView) itemView.findViewById(R.id.txt_product_name);
            txtQuantity = (TextView) itemView.findViewById(R.id.txt_quantity);
        }
    }


    private String[][] getProductNamesandQuantity(ArrayList<OrderItem> orderItems){

        String[][] str = new String[1][2];

        String name="", quantity="";

        for (OrderItem item : orderItems){
            name =  name
                    + System.getProperty("line.separator")
                    + "------------------"
                    + System.getProperty("line.separator")
                    + item.ProductName;
            quantity = quantity
                    + System.getProperty("line.separator")
                    + "---------------"
                    + System.getProperty("line.separator")
                    + item.Quantity +" adet";
        }

        str[0][0] = name;
        str[0][1] = quantity;

       return  str;
    }

}
