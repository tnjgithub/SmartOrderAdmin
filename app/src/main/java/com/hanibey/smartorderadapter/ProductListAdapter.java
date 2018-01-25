package com.hanibey.smartorderadapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.admin.ProductRegisterActivity;
import com.hanibey.smartorderbusiness.ActivityService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Product;
import com.hanibey.smartorderrepository.FirebaseDb;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Tanju on 20.12.2017.
 */

public class ProductListAdapter extends BaseAdapter {
    DatabaseReference productRef;

    private LayoutInflater mInflater;
    private List<Product> productList;
    ActivityService activityService;
    Activity activity;

    public ProductListAdapter(Activity act, List<Product> products) {
        mInflater = (LayoutInflater) act.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        productList = products;
        activity = act;
        activityService = new ActivityService(act);
        productRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Product);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = mInflater.inflate(R.layout.product_list_item_layout, null);
        final ImageView productImage = (ImageView) row.findViewById(R.id.product_image);
        TextView txtTitle = (TextView) row.findViewById(R.id.txt_productname);
        TextView txtPrice = (TextView) row.findViewById(R.id.txt_price);
        TextView txtKey = (TextView) row.findViewById(R.id.txt_key);

        Product product = productList.get(position);

        txtTitle.setText(product.Title);
        txtPrice.setText(String.valueOf(product.Price));
        txtKey.setText(product.Key);

        Picasso.with(activity).load(product.ImageUrl).into(productImage);

        final String categoryKey = product.CategoryKey;
        final String productKey = product.Key;
        final String imageName = product.ImageName;

        ImageButton btnUpdate = (ImageButton) row.findViewById(R.id.btnEditProduct);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductRegisterActivity.getInstance().selectedCategoryKey = categoryKey;
                ProductRegisterActivity.getInstance().selectedProductKey = productKey;
                Intent i = new Intent(activity, ProductRegisterActivity.class);
                activity.startActivity(i);
            }
        });

        ImageButton btnRemove = (ImageButton) row.findViewById(R.id.btnRemoveProduct);
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

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                StorageReference imgRef = storageRef.child("images/"+imageName);

                                imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        productRef.child(categoryKey).child(productKey).removeValue();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
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

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) productImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                zoomProductImage(bitmap);
            }
        });

        return row;
    }

    private void zoomProductImage(Bitmap btm) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.zoom_image_dialog_layout);

        ImageView img = (ImageView) dialog.findViewById(R.id.img_product);
        img.setImageBitmap(btm);
        //Picasso.with(context).load(url).into(img);

        int deviceWidth = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.80);
        int deviceHeight = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.80);

        int width, height;

        if(btm.getWidth() > deviceWidth)
            width = deviceWidth;
        else
            width = btm.getWidth() + 10;

        if(btm.getHeight() > deviceHeight)
            height = deviceWidth;
        else
            height = btm.getHeight() + 10;

        dialog.getWindow().setLayout(width, height);

        ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}


