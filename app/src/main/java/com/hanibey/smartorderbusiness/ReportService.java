package com.hanibey.smartorderbusiness;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.CurrentOrder;
import com.hanibey.smartordermodel.Order;
import com.hanibey.smartordermodel.OrderItem;
import com.hanibey.smartordermodel.Report;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;

/**
 * Created by Tanju on 19.01.2018.
 */

public class ReportService {

    DatabaseReference reportRef, orderRef;
    DateTimeService dateTimeService;

    Report report;

    public ReportService(){
        this.reportRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Report);
        this.orderRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Order);
        this.dateTimeService = new DateTimeService();
    }

    public void saveReport(final Order order){

        final String year = dateTimeService.getDate(Constant.DateTypes.Year);
        final String month = dateTimeService.getDate(Constant.DateTypes.Month);
        final String day = dateTimeService.getDate(Constant.DateTypes.Day);

        reportRef.child(year).child(month).child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

               if(snapshot.exists()){
                   report = snapshot.getValue(Report.class);
                   ArrayList<OrderItem> items = new ArrayList<>();
                   items.addAll(order.Items);
                   items.addAll(report.Items);

                   int count = Integer.valueOf(report.OrderCount) + 1;
                   report.OrderCount = String.valueOf(count);
                   report.TotalAmount += order.TotalPrice;
                   report.Items = groupByItems(items);
               }
               else {
                    report = new Report("1", order.TotalPrice, order.Items);
               }

                reportRef.child(year).child(month).child(day).setValue(report);
                orderRef.child(order.ClientKey).child(order.Key).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<OrderItem> groupByItems(ArrayList<OrderItem> orderItems){

        ArrayList<OrderItem> items = new ArrayList<>();

        for (OrderItem item : orderItems){
            int index = getItemIndex(items, item);
            if(index < 0){
                items.add(item);
            }else {
                int quantity = Integer.valueOf(items.get(index).Quantity) + Integer.valueOf(item.Quantity);
                items.get(index).Quantity = String.valueOf(quantity);
            }
        }

        return  items;
    }

    private int getItemIndex(ArrayList<OrderItem> orderItems, OrderItem orderItem){

        for(int i=0; i < orderItems.size(); i++)
        {
            if(orderItems.get(i).ProductKey.equals(orderItem.ProductKey)){
                return i;
            }
        }

        return -1;
    }


}
