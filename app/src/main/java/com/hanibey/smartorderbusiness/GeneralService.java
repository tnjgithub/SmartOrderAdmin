package com.hanibey.smartorderbusiness;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartorderrepository.FirebaseDb;

/**
 * Created by Tanju on 12.01.2018.
 */

public class GeneralService {
    Dialog dialog;
    Activity activity;
    public GeneralService(Activity act){
        this.activity = act;
        dialog = new Dialog(activity);
    }

    public String getClientStatusText(String status){

        String sText="";

        switch (status){
            case Constant.ClientStatus.Online:
                sText = "Uygulama Açık";
                break;
            case Constant.ClientStatus.Background:
                sText = "Uygulama Arkaplanda";
                break;
            case Constant.ClientStatus.Offline:
                sText = "Uygulama Kapalı";
                break;
            default:
                sText = "";
                break;
        }
        return sText;
    }

    public void checkConnection(){

        DatabaseReference connectedRef = FirebaseDb.getDatabase().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    showConnectionDialog(false);
                } else {
                    showConnectionDialog(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }

    private void showConnectionDialog(boolean show){

        if(show){
            dialog.setContentView(R.layout.connection_layout);
            dialog.setCanceledOnTouchOutside(false);

            int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.80);
            int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.60);
            dialog.getWindow().setLayout(width, height);

            dialog.show();
        }
        else {
            dialog.dismiss();
        }
    }

}
