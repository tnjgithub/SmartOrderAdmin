package com.hanibey.smartorderbusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.admin.FragmentCategory;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Category;
import com.hanibey.smartordermodel.Product;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 10.01.2018.
 */

public class CategoryService {

    DatabaseReference categoryRef;
    FirebaseStorage storage;
    StorageReference storageRef;

    Activity activity;
    public CategoryService(Activity act){
        this.activity = act;
        this.categoryRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Category);
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
    }

    public void showCategoryDialog() {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.add_category_layout);
        dialog.setCanceledOnTouchOutside(false);

        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.50);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.50);
        dialog.getWindow().setLayout(width, height);

        final EditText edtName = (EditText) dialog.findViewById(R.id.edt_name);
        setNameToEditText(edtName);

        final Button btnSave = (Button) dialog.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(edtName.getText())){
                    showAlert(activity.getString(R.string.not_empty_field));
                }
                else {
                    Category newCategory = new Category("", edtName.getText().toString());
                    if(FragmentCategory.getInstance().selectedCategoryKey.equals("")){
                        categoryRef.push().setValue(newCategory);
                        Log.w("Mesaj", "Yeni kayıt eklendi");
                    }
                    else {
                        Map<String, Object> postValues = newCategory.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(FragmentCategory.getInstance().selectedCategoryKey, postValues);
                        categoryRef.updateChildren(childUpdates);
                        Log.w("Mesaj", "kayıt güncellendi");
                        FragmentCategory.getInstance().selectedCategoryKey = "";
                    }

                    edtName.setText("");
                    dialog.dismiss();
                }

            }
        });

        //cancel button
        final Button btnCancelOrder = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCategory.getInstance().selectedCategoryKey = "";
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private  void setNameToEditText(final EditText edtName){
        String categoryKey = FragmentCategory.getInstance().selectedCategoryKey;
        if(!categoryKey.equals("")){
            categoryRef.child(categoryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("Name").getValue(String.class);
                        edtName.setText(name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });
        }
    }

    public void removeCategoryAndProducts(String cKey){

        final String categoryKey = cKey;

        try{
            final DatabaseReference productRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Product);
            productRef.child(categoryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        for(final DataSnapshot ds: dataSnapshot.getChildren()){

                            String imgName = ds.child("ImageName").getValue(String.class);
                            StorageReference imgRef = storageRef.child(Constant.IMAGE_FOLDER + imgName);
                            imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    productRef.child(categoryKey).child(ds.getKey()).removeValue();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    showAlert(exception.toString());
                                }
                            });
                        }
                    }

                    categoryRef.child(categoryKey).removeValue();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });
        }
        catch (Exception ex){
            Log.e("Hata", ex.toString());
            showAlert("Hata oluştu");
        }

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
