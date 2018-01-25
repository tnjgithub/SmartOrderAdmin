package com.hanibey.smartorderbusiness;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.ChefOrder;
import com.hanibey.smartordermodel.Client;
import com.hanibey.smartordermodel.Order;
import com.hanibey.smartordermodel.OrderItem;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 11.01.2018.
 */

public class OrderService {

    DatabaseReference orderRef, chefOrderRef, clientRef;

    private DateTimeService dateTimeService;
    public OrderService(){
        this.orderRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Order);
        this.chefOrderRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.ChefOrder);
        this.clientRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Client);
        this.dateTimeService = new DateTimeService();
    }

    public void changeOrderStatus(Order order, String status){

        String date = dateTimeService.getCurrentDate();
        String note = order.Description;
        double totalPrice = order.TotalPrice;

        ArrayList<OrderItem> orderItems = changeItemStatus(order.Items, status);
        Order updateOrder = new Order(order.Key, order.ClientKey, note, totalPrice, order.CreateDate, date, status, orderItems);
        Map<String, Object> postValues = updateOrder.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(order.Key, postValues);
        orderRef.child(order.ClientKey).updateChildren(childUpdates);

        if(status.equals(Constant.OrderStatus.Preparing)){
            saveChefOrder(updateOrder);
        }
        else  if(status.equals(Constant.OrderStatus.OnTheTable)){
            removeChefOrder(order.Key);
        }
        updateClientChangeStatus(order.ClientKey);
    }

    private void updateClientChangeStatus(String clientKey){

        clientRef.child(clientKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean changeStatus = dataSnapshot.child("ChangeStatus").getValue(Boolean.class);
                clientRef.child(dataSnapshot.getKey()).child("ChangeStatus").setValue(!changeStatus);

                /*Client client = dataSnapshot.getValue(Client.class);
                boolean changeStatus = client.ChangeStatus;
                Client updateClient = new Client(client.Key, client.Name, client.Status, !changeStatus);
                Map<String, Object> postValues = updateClient.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(client.Key, postValues);
                clientRef.updateChildren(childUpdates);*/
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }

        });


    }

    private ArrayList<OrderItem> changeItemStatus(ArrayList<OrderItem> selectedOrderItems, String orderStatus){

        if(orderStatus.equals(Constant.OrderStatus.Preparing)){
            for (OrderItem item : selectedOrderItems){
                if(item.Status.equals(Constant.OrderItemStatus.New)){
                    item.Status = Constant.OrderItemStatus.Preparing;
                }
            }
        }
        else if(orderStatus.equals(Constant.OrderStatus.OnTheTable)){
            for (OrderItem item : selectedOrderItems){
                if(item.Status.equals(Constant.OrderItemStatus.Preparing)){
                    item.Status = Constant.OrderItemStatus.OnTheTable;
                }
            }
        }


        return  selectedOrderItems;
    }

    public void saveChefOrder(Order order){

        final Order currOrder = order;

        clientRef.child(order.ClientKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String clientName = dataSnapshot.child("Name").getValue(String.class);
                ArrayList<OrderItem> items = getOrderItems(currOrder.Items);
                ChefOrder chefOrder = new ChefOrder(currOrder.Key, currOrder.ClientKey, clientName, currOrder.Key, items);
                chefOrderRef.child(currOrder.Key).setValue(chefOrder);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeChefOrder(String orderKey){
        chefOrderRef.child(orderKey).removeValue();
    }

    private ArrayList<OrderItem> getOrderItems(ArrayList<OrderItem> items){

        ArrayList<OrderItem> itemList = new ArrayList<>();
        for(OrderItem item : items){
            if(item.Status.equals(Constant.OrderItemStatus.Preparing)){
                itemList.add(item);
            }
        }

        return itemList;
    }


}
