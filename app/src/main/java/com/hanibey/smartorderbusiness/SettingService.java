package com.hanibey.smartorderbusiness;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.StartActivity;
import com.hanibey.smartordermodel.Settings;
import com.hanibey.smartordermodel.User;
import com.hanibey.smartorderrepository.DBHelper;

/**
 * Created by Tanju on 26.10.2017.
 */

public class SettingService {
    private DBHelper db;
    Context context;

    public  SettingService(Context con){
        db = new DBHelper(con);
        context = con;
    }

    public void showLoginDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.login_form_layout);
        dialog.setTitle("Giriş Yap");

        final EditText edtUserName = (EditText) dialog.findViewById(R.id.edtUserName);
        final EditText edtPassword = (EditText) dialog.findViewById(R.id.edtPassword);

        Button btnLogin = (Button) dialog.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();

                if(userName != "" && password != ""){

                    final User user = new User();
                    /*user.setUserName(edtUserName.getText().toString());
                    user.setPassword(edtPassword.getText().toString());*/

                }
                else {
                }


            }
        });


        dialog.show();
    }


    public int getBranchId() {
        Settings setting = db.getSettings();
        int branchId = 0;

        if(setting != null && setting.getBranchId() > 0)
            branchId = setting.getBranchId();

        return branchId;
    }

    public String getBranchName() {

        Settings setting = db.getSettings();
        String branchName = "-";

        if(setting != null && setting.getBranchId() > 0)
            branchName = setting.getBranchName();

        return branchName;
    }


    public boolean updateClientNo(String clientNo) {

        try {
            Settings setting = db.getSettings();
            db.updateClientNo(setting.getId(), clientNo);
            return  true;
        }
        catch (Exception ex){
            return  false;
        }

    }

    public String getClientNo() {
        Settings setting = db.getSettings();
        String clientNo = "-";

        if(setting != null && setting.getClientNo() != "")
            clientNo = setting.getClientNo();

        return clientNo;
    }
}
