package com.hanibey.smartorder.administration.admin;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderadapter.ProductListAdapter;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Product;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProduct extends Fragment {

    DatabaseReference productRef;

    public String selectedProductKey = "";
    public static final FragmentProduct instance = new FragmentProduct();
    public static FragmentProduct getInstance() {
        return instance;
    }

    ListView productListView;
    Spinner spCategory;
    List<String> categoryKeyList;

    ProgressDialog  progressDialog;

    public FragmentProduct() {
        productRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Product);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);

        productListView = (ListView) rootView.findViewById(R.id.product_listview);

        spCategory = (Spinner) rootView.findViewById(R.id.category_Spinner);
        setCategoryToSpinner();

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ProductRegisterActivity.getInstance().selectedProductKey = "";
                ProductRegisterActivity.getInstance().selectedCategoryKey = "";
                Intent intent = new Intent(getActivity(), ProductRegisterActivity.class);
                getContext().startActivity(intent);;
            }
        });

        // Inflate the layout for this fragment
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

    private  void setCategoryToSpinner(){

        progressDialog = ProgressDialog.show(getContext(), getString(R.string.uploading), getString(R.string.uploading_data_message), false, false);

        DatabaseReference categoryRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Category);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                categoryKeyList = new ArrayList<String>();
                List<String> categoryNameList = new ArrayList<String>();

                for(DataSnapshot pos : dataSnapshot.getChildren()){
                    String name = pos.child("Name").getValue(String.class);
                    categoryNameList.add(name);
                    categoryKeyList.add(pos.getKey());
                }

                ArrayAdapter dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryNameList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(dataAdapter);
                spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(categoryKeyList != null && categoryKeyList.size() > 0){
                            setProductToListView(categoryKeyList.get(position));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //setProductToListView(categoryKeyList.get(0));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showAlert(getString(R.string.unexpected_error));
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });
    }

    private void setProductToListView(String categoryKey){

        try{
            productRef.child(categoryKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Product> productList = new ArrayList<>();

                    for(DataSnapshot pos : dataSnapshot.getChildren()){
                        Product product = pos.getValue(Product.class);
                        product.Key = pos.getKey();
                        productList.add(product);
                    }

                    if(getActivity() != null){
                        ProductListAdapter adapter = new ProductListAdapter(getActivity(), productList);
                        productListView.setAdapter(adapter);
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

    private  void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void openFrament(int index){
        AdminMainActivity.getInstance().selectedFragmentIndex = index;
        Intent intent = new Intent(getActivity(), AdminMainActivity.class);
        getActivity().startActivity(intent);
    }

}
