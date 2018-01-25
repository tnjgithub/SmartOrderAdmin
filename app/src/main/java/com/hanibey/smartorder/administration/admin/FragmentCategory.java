package com.hanibey.smartorder.administration.admin;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderadapter.CategoryListAdapter;
import com.hanibey.smartorderbusiness.CategoryService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Category;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentCategory extends Fragment {

    DatabaseReference categoryRef;
    CategoryService categoryService;

    public String selectedCategoryKey = "";
    public static final FragmentCategory instance = new FragmentCategory();
    public static FragmentCategory getInstance() {
        return instance;
    }


    public FragmentCategory() {
        this.categoryRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Category);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryService = new CategoryService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        ListView categoryListView = (ListView) rootView.findViewById(R.id.category_listview);
        setCategoryToListView(categoryListView);

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                categoryService.showCategoryDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setCategoryToListView(final ListView categoryListview){

        try{
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getString(R.string.uploading),
                    getString(R.string.uploading_data_message), false, false);

            categoryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Category> categoryList = new ArrayList<>();

                    for(DataSnapshot pos : dataSnapshot.getChildren()){
                        Category category = pos.getValue(Category.class);
                        category.Key = pos.getKey();
                        categoryList.add(category);
                    }

                    if(getActivity() != null){
                        CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), categoryList);
                        categoryListview.setAdapter(adapter);
                    }
                    progressDialog.dismiss();
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
        }


    }

}
