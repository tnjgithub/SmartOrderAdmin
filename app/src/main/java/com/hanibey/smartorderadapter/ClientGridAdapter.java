package com.hanibey.smartorderadapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.cashregister.CashRegisterMainActivity;
import com.hanibey.smartorderbusiness.DateTimeService;
import com.hanibey.smartorderbusiness.GeneralService;
import com.hanibey.smartorderbusiness.MessageService;
import com.hanibey.smartorderbusiness.OrderService;
import com.hanibey.smartorderbusiness.ReportService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Client;
import com.hanibey.smartordermodel.CurrentOrder;
import com.hanibey.smartordermodel.Order;
import com.hanibey.smartordermodel.OrderItem;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 25.12.2017.
 */

public class ClientGridAdapter extends RecyclerView.Adapter<ClientGridAdapter.ViewHolder> {

    private DatabaseReference orderRef;

    private ReportService reportService;
    private MessageService messageService;
    private OrderService orderService;
    private GeneralService generalService;
    private MediaPlayer mediaPlayer = null;

    private ArrayList<Client> clientList;
    private String orderStatus;

    private Context context;
    private Activity activity;
    public ClientGridAdapter(Context con, ArrayList<Client> clients) {
        super();
        this.context = con;
        this.activity = (Activity) con;
        this.clientList = clients;
        this.orderRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Order);
        this.reportService = new ReportService();
        this.messageService = new MessageService(activity);
        this.orderService = new OrderService();
        this.generalService = new GeneralService(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.client_grid_item_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        final ViewHolder holder = viewHolder;
        Client client = clientList.get(i);

        final String clientKey = client.Key;
        final String name = client.Name;
        final String status = client.Status;

        viewHolder.txtName.setText(name);
        viewHolder.txtClientStatus.setText(generalService.getClientStatusText(status));

        Query lastQuery = orderRef.child(clientKey).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        Order order = ds.getValue(Order.class);

                        if(!order.Status.equals(Constant.OrderStatus.Paid)){

                            CurrentOrder currOrder = new CurrentOrder(clientKey, order);
                            CashRegisterMainActivity.getInstance().currentOrders.add(currOrder);

                            orderStatus = order.Status;
                            if(orderStatus.equals(Constant.OrderStatus.New)){
                                holder.txtOrderStatus.setText("Yeni sipariş");
                                holder.imgOrderStatus.setImageResource(R.drawable.status_orange);
                                holder.txtTotalAmount.setVisibility(View.GONE);
                                showNotification(name +" cihazından yeni sipariş var");
                            }
                            else if(orderStatus.equals(Constant.OrderStatus.Preparing)){
                                holder.txtOrderStatus.setText("Sipariş hazırlanıyor...");
                                holder.imgOrderStatus.setImageResource(R.drawable.status_yellow);
                                holder.txtTotalAmount.setVisibility(View.GONE);
                            }
                            else if(orderStatus.equals(Constant.OrderStatus.OnTheTable)){
                                holder.txtOrderStatus.setText("Ödeme bekliyor...");
                                holder.imgOrderStatus.setImageResource(R.drawable.status_red);
                                holder.txtTotalAmount.setVisibility(View.VISIBLE);
                                holder.txtTotalAmount.setText(String.valueOf(order.TotalPrice));
                            }
                            else if(orderStatus.equals(Constant.OrderStatus.AdditionalOrder)){
                                holder.txtOrderStatus.setText("Siparişe eklenti yapıldı");
                                holder.imgOrderStatus.setImageResource(R.drawable.status_blue);
                                showNotification(name +" cihazından ek sipariş var");
                                holder.txtTotalAmount.setVisibility(View.GONE);
                            }
                        }
                        else {
                            holder.txtOrderStatus.setText("Sipariş bekliyor...");
                            holder.imgOrderStatus.setImageResource(R.drawable.status_green);
                            holder.txtTotalAmount.setVisibility(View.GONE);
                        }

                    }
                }
                else {
                    holder.txtOrderStatus.setText("Sipariş bekliyor...");
                    holder.imgOrderStatus.setImageResource(R.drawable.status_green);
                    holder.txtTotalAmount.setVisibility(View.GONE);
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {

            }

        });

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Order order = getOrder(clientKey);
                order.ClientKey = clientKey;

                if(order != null){
                    showCurrentListDialog(order);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView txtName, txtClientStatus, txtOrderStatus, txtTotalAmount;
        private ImageView imgOrderStatus;

        private ItemClickListener clickListener;

        private ViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txt_client_name);

            txtClientStatus = (TextView) itemView.findViewById(R.id.txt_client_status);
            txtOrderStatus = (TextView) itemView.findViewById(R.id.txt_order_status);
            txtTotalAmount = (TextView) itemView.findViewById(R.id.txt_total_amount);
            imgOrderStatus = (ImageView) itemView.findViewById(R.id.img_order_status);

            CashRegisterMainActivity.getInstance().currentOrders = new ArrayList<>();

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        private void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }

    private interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    private void showCurrentListDialog(Order order){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.order_list_dialog);

        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.70);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.70);
        dialog.getWindow().setLayout(width, height);

        final TextView txtOrderDescription = (TextView) dialog.findViewById(R.id.txt_order_description);
        final TextView txtTotalPrice = (TextView) dialog.findViewById(R.id.txt_total_price);

        txtTotalPrice.setText(String.valueOf("Toplam: " + order.TotalPrice));
        if(TextUtils.isEmpty(order.Description)){
            txtOrderDescription.setText("");
        }
        else {
            txtOrderDescription.setText(order.Description);
        }

        final Button btnStatus = (Button) dialog.findViewById(R.id.btn_status);
        btnStatus.setText(getStatusButtonText(order.Status));

        final String status = order.Status;
        final Order currOrder = order;
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){
                    case Constant.OrderStatus.New:
                    case Constant.OrderStatus.AdditionalOrder:
                        orderService.changeOrderStatus(currOrder, Constant.OrderStatus.Preparing);
                        break;

                    case Constant.OrderStatus.Preparing:
                        orderService.changeOrderStatus(currOrder, Constant.OrderStatus.OnTheTable);
                        break;

                    case Constant.OrderStatus.OnTheTable:
                        orderService.changeOrderStatus(currOrder, Constant.OrderStatus.Paid);
                        reportService.saveReport(currOrder);
                        break;

                    default:
                        break;
                }

                dialog.dismiss();
            }
        });

        ArrayList<OrderItem> selectedOrderItems = groupByItems(order.Items);
        final ListView orderListView = (ListView) dialog.findViewById(R.id.order_listView);
        CurrentOrderAdapter adapter = new CurrentOrderAdapter(context, selectedOrderItems);
        orderListView.setAdapter(adapter);

        dialog.show();
    }

    private String getStatusButtonText(String status){
        String statusText="";
        switch (status){
            case Constant.OrderStatus.New:
            case Constant.OrderStatus.AdditionalOrder:
                statusText = "Onayla";
                break;

            case Constant.OrderStatus.Preparing:
                statusText = "Masaya Gönder";
                break;

            case Constant.OrderStatus.OnTheTable:
                statusText = "Siparişi Bitir";
                break;

            default:
                statusText="";
                break;
        }

        return  statusText;
    }

    private Order getOrder(String clientKey){
        Order order = null;
        if(CashRegisterMainActivity.getInstance().currentOrders != null
                && CashRegisterMainActivity.getInstance().currentOrders.size() > 0){
            for(CurrentOrder o : CashRegisterMainActivity.getInstance().currentOrders){
                if(o.ClientKey.equals(clientKey)){
                    order = o.Order;
                }
            }
        }

        return order;
    }

    private ArrayList<OrderItem> groupByItems(ArrayList<OrderItem> selectedItems){

        ArrayList<OrderItem> selectedOrderItems = new ArrayList<>();

        for (OrderItem item : selectedItems){
            int index = getItemIndex(selectedOrderItems, item);
            if(index < 0){
                selectedOrderItems.add(item);
            }else {
                int quantity = Integer.valueOf(selectedOrderItems.get(index).Quantity) + Integer.valueOf(item.Quantity);
                selectedOrderItems.get(index).Quantity = String.valueOf(quantity);
            }
        }

        return  selectedOrderItems;
    }

    private int getItemIndex(ArrayList<OrderItem> selectedOrderItems, OrderItem orderItem){

        for(int i=0; i<selectedOrderItems.size(); i++)
        {
            if(selectedOrderItems.get(i).ProductKey.equals(orderItem.ProductKey) && selectedOrderItems.get(i).Status.equals(orderItem.Status)){
                return i;
            }
        }

        return -1;
    }

    private void showNotification(String message){

        managerOfSound();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) activity.findViewById(R.id.coordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View mySbView = snackbar.getView();
        TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();

        CashRegisterMainActivity.getInstance().messageList.add(message);
        messageService.updateMessageWindows();
    }

    private void managerOfSound() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.notification_sound);
        mediaPlayer.start();
    }

}
