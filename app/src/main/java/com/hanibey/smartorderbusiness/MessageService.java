package com.hanibey.smartorderbusiness;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.cashregister.CashRegisterMainActivity;
import com.hanibey.smartorderadapter.MessageAdapter;

/**
 * Created by Tanju on 11.01.2018.
 */

public class MessageService {

    Activity activity;
    public MessageService(Activity act){
        this.activity = act;
    }

    public void updateMessageWindows(){

        final ListView messageListView = (ListView) activity.findViewById(R.id.message_list_view);

        MessageAdapter adapter = new MessageAdapter(activity, CashRegisterMainActivity.getInstance().messageList);
        messageListView.setAdapter(adapter);

        final ImageButton btnExpendCollepse = (ImageButton) activity.findViewById(R.id.btn_expend_collapse);
        btnExpendCollepse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageListView.getVisibility() == View.VISIBLE){
                    messageListView.setVisibility(View.GONE);
                    btnExpendCollepse.setImageResource(R.drawable.ic_collapse_arrow);
                }
                else {
                    messageListView.setVisibility(View.VISIBLE);
                    btnExpendCollepse.setImageResource(R.drawable.ic_expand_arrow);
                }
            }
        });

    }
}
