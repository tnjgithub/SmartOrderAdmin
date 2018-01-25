package com.hanibey.smartorderadapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.hanibey.smartorderbusiness.ActivityService;
import com.hanibey.smartorderbusiness.CategoryService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Category;
import com.hanibey.smartordermodel.Product;

import java.util.List;

/**
 * Created by Tanju on 20.12.2017.
 */

public class CategoryListAdapter extends BaseAdapter {

    CategoryService categoryService;

    private LayoutInflater mInflater;
    private List<Category> categoryList;

    Context activity;

    public CategoryListAdapter(Activity act, List<Category> categories) {
        this.mInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.categoryList = categories;
        this.activity = act;
        this.categoryService = new CategoryService(act);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Category getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = mInflater.inflate(R.layout.category_list_row_layout, null);
        TextView txtName = (TextView) row.findViewById(R.id.txt_name);
        TextView txtKey = (TextView) row.findViewById(R.id.txt_key);

        Category category = categoryList.get(position);

        txtName.setText(category.Name);
        txtKey.setText(category.Key);

        final String categoryKey = category.Key;

        ImageButton btnUpdate = (ImageButton) row.findViewById(R.id.btnEditCategory);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCategory.getInstance().selectedCategoryKey = categoryKey;
                categoryService.showCategoryDialog();
                //activityService.openFrament(Constant.Fragments.Category);
            }
        });

        ImageButton btnRemove = (ImageButton) row.findViewById(R.id.btnRemoveCategory);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle("Uyarı!");
                alertDialogBuilder
                        .setMessage("Bu kategoriyi sildiğinizde kategori altındaki bütün ürünler silinecektir. Silmek istiyor musunuz?")
                        .setCancelable(false)
                        .setPositiveButton("Sil",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                categoryService.removeCategoryAndProducts(categoryKey);
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

