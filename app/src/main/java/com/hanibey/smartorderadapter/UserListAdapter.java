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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.admin.FragmentUser;
import com.hanibey.smartorderbusiness.ActivityService;
import com.hanibey.smartorderbusiness.UserService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.User;

import java.util.List;

/**
 * Created by Tanju on 18.12.2017.
 */

public class UserListAdapter extends BaseAdapter {

    UserService userService;
    private LayoutInflater mInflater;
    private List<User> userList;
    ActivityService activityService;
    Activity activity;

    public UserListAdapter(Activity act, List<User> users) {
        mInflater = (LayoutInflater) act.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        userList = users;
        activity = act;
        activityService = new ActivityService(act);
        userService = new UserService(act);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = mInflater.inflate(R.layout.user_list_row_layout, null);
        TextView txtName = (TextView) row.findViewById(R.id.txt_username);
        TextView txtType = (TextView) row.findViewById(R.id.txt_usrtype);
        TextView txtKey = (TextView) row.findViewById(R.id.txt_key);

        User user = userList.get(position);

        txtName.setText(user.FirstName +" "+ user.LastName);
        String[] userTypeList = activity.getResources().getStringArray(R.array.user_types_items);
        txtType.setText(userTypeList[Integer.valueOf(user.UserType)]);
        txtKey.setText(user.Key);

        final String userKey = user.Key;

        ImageButton btnUpdate = (ImageButton) row.findViewById(R.id.btnEditUser);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUser.getInstance().selectedUserKey = userKey;
                userService.showUserDialog();
            }
        });

        ImageButton btnRemove = (ImageButton) row.findViewById(R.id.btnRemoveUser);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle("Uyarı!");
                alertDialogBuilder
                        .setMessage("Kaydı silmek istediğinizden emin misiniz?")
                        .setCancelable(false)
                        .setPositiveButton("Sil",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                userService.removeUser(userKey);
                            }
                        })
                        .setNegativeButton("Vazgeç",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        return row;
    }

}

