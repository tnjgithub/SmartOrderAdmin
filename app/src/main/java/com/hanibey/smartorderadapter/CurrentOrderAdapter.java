package com.hanibey.smartorderadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.OrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tanju on 29.12.2017.
 */

public class CurrentOrderAdapter extends BaseAdapter {

    private List<OrderItem> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CurrentOrderAdapter(Context aContext, List<OrderItem> orderItems) {
        this.listData = orderItems;
        this.layoutInflater = LayoutInflater.from(aContext);
        this.context = aContext;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.current_order_layout, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
            holder.txtQuantity = (TextView) convertView.findViewById(R.id.txt_quantity);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_item_status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(listData.get(position).ProductName);
        holder.txtQuantity.setText(listData.get(position).Quantity +" Adet");
        holder.txtStatus.setText(getStatusText(listData.get(position).Status));

        setTextColor(listData.get(position).Status, holder.txtStatus);

        return convertView;
    }

    private static class ViewHolder {
        TextView txtTitle, txtQuantity, txtStatus;
    }

    private String getStatusText(String status){

        String text ="-";

        switch (status){
            case Constant.OrderItemStatus.New:
                text = "Yeni sipariş";
                break;
            case Constant.OrderItemStatus.Preparing:
                text = "hazırlanıyor...";
                break;
            case Constant.OrderItemStatus.OnTheTable:
                text = "Servis edildi";
                break;
            default:
                break;
        }

        return  text;
    }

    private void setTextColor(String status, TextView textView){

        switch (status){
            case Constant.OrderItemStatus.New:
                textView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                break;
            case Constant.OrderItemStatus.Preparing:
                textView.setTextColor(context.getResources().getColor(R.color.material_green_900));
                break;
            case Constant.OrderItemStatus.OnTheTable:
                textView.setTextColor(context.getResources().getColor(R.color.grey_500));
                break;
            default:
                textView.setTextColor(context.getResources().getColor(R.color.black));
                break;
        }


    }

}
