package com.hanibey.smartorderadapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.admin.FragmentCategory;
import com.hanibey.smartorder.administration.cashregister.CashRegisterMainActivity;
import com.hanibey.smartorderbusiness.CategoryService;
import com.hanibey.smartorderbusiness.MessageService;
import com.hanibey.smartordermodel.Category;

import java.util.List;

/**
 * Created by Tanju on 11.01.2018.
 */

public class MessageAdapter extends BaseAdapter {

    MessageService messageService;

    private LayoutInflater mInflater;
    private List<String> messageList;

    public MessageAdapter(Activity act, List<String> messages) {
        this.mInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageList = messages;
        messageService = new MessageService(act);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public String getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = mInflater.inflate(R.layout.message_item_layout, null);
        TextView txtMessage = (TextView) row.findViewById(R.id.txt_message);

        String message = messageList.get(position);
        txtMessage.setText(message);

      final int index = position;
        ImageButton btnRemove = (ImageButton) row.findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CashRegisterMainActivity.getInstance().messageList.remove(index);
                messageService.updateMessageWindows();
            }
        });

        return row;
    }

}

