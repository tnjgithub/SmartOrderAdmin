package com.hanibey.smartorderbusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.admin.FragmentCategory;
import com.hanibey.smartorder.administration.admin.FragmentUser;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.User;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 19.12.2017.
 */

public class UserService {
    DatabaseReference userRef;
    EditText edtFirstName,edtLastName,edtUserName,edtPassword;
    Spinner typesSpinner;

    Activity activity;
    public UserService(Activity act){
        this.activity = act;
        this.userRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.User);
    }


    public void showUserDialog() {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.add_user_layout);
        dialog.setCanceledOnTouchOutside(false);

        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.75);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.75);
        dialog.getWindow().setLayout(width, height);

        edtFirstName = (EditText) dialog.findViewById(R.id.edtFirstName);
        edtLastName = (EditText) dialog.findViewById(R.id.edtLastName);
        edtUserName = (EditText) dialog.findViewById(R.id.edtUserName);
        edtPassword = (EditText) dialog.findViewById(R.id.edtPassword);
        typesSpinner = (Spinner) dialog.findViewById(R.id.userTpe_Spinner);

        String[] userTypeList = activity.getResources().getStringArray(R.array.user_types_items);
        ArrayAdapter dataAdapterTypes = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, userTypeList);
        dataAdapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typesSpinner.setAdapter(dataAdapterTypes);

        if(!FragmentUser.getInstance().selectedUserKey.equals(""))
            setDataToEditText(FragmentUser.getInstance().selectedUserKey);

        final Button btnSave = (Button) dialog.findViewById(R.id.btn_save_user);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(TextUtils.isEmpty(edtFirstName.getText())
                            || TextUtils.isEmpty(edtLastName.getText())
                            || TextUtils.isEmpty(edtUserName.getText())
                            || TextUtils.isEmpty(edtPassword.getText()))
                    {
                        showAlert(activity.getString(R.string.not_empty_field));
                    }
                    else {

                        String firstName = edtFirstName.getText().toString();
                        String lastName = edtLastName.getText().toString();
                        String userName = edtUserName.getText().toString().trim();
                        String password = edtPassword.getText().toString().trim();
                        String userTpe = String.valueOf(typesSpinner.getSelectedItemPosition());
                        final User user = new User(userName, firstName, lastName, userName, password, userTpe);

                        final String key = userName +"_"+password;

                        if(FragmentUser.getInstance().selectedUserKey.equals("")){

                            userRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()){
                                        showAlert("Birden fazla kullanıcı için aynı kullanıcı adı ve şifre tanımlanamaz!");
                                    }
                                    else {
                                        userRef.child(key).setValue(user);
                                        dialog.dismiss();
                                        Log.w("Mesaj", "Yeni kayıt eklendi");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    showAlert(activity.getString(R.string.unexpected_error));
                                }
                            });
                        }
                        else {
                            Map<String, Object> postValues = user.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(FragmentUser.getInstance().selectedUserKey, postValues);
                            userRef.updateChildren(childUpdates);
                            Log.w("Mesaj", "kayıt güncellendi");
                            FragmentUser.getInstance().selectedUserKey = "";
                            dialog.dismiss();
                        }
                    }
                }
                catch (Exception ex){
                    showAlert(activity.getString(R.string.unexpected_error));
                    Log.w("Hata", ex);
                }
            }
        });
        dialog.show();
    }

    private void setDataToEditText(String key){

        userRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                edtFirstName.setText(user.FirstName);
                edtLastName.setText(user.LastName);
                edtUserName.setText(user.UserName);
                edtPassword.setText(user.Password);
                typesSpinner.setSelection(Integer.valueOf(user.UserType));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });
    }

    public void removeUser(String userKey){
        userRef.child(userKey).removeValue();
    }

    private  void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
