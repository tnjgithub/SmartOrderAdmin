package com.hanibey.smartorder.administration.cashregister;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.StartActivity;
import com.hanibey.smartorder.administration.admin.FragmentCategory;
import com.hanibey.smartorder.administration.chef.ChefMainActivity;
import com.hanibey.smartorderadapter.ChefOrderListAdapter;
import com.hanibey.smartorderadapter.ClientGridAdapter;
import com.hanibey.smartorderbusiness.LogService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Category;
import com.hanibey.smartordermodel.ChefOrder;
import com.hanibey.smartordermodel.Client;
import com.hanibey.smartordermodel.CurrentOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CashRegisterMainActivity extends AppCompatActivity {

    LogService logService;
    FirebaseDatabase database ;
    DatabaseReference clientRef, chefOrderRef;

    RecyclerView recyclerView, recyclerOrder;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    TextView txtClientCount;

    public ArrayList<CurrentOrder> currentOrders = new ArrayList<>();
    public ArrayList<String> messageList = new ArrayList<>();
    public static final CashRegisterMainActivity instance = new CashRegisterMainActivity();
    public static CashRegisterMainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashregister_main);

        txtClientCount = (TextView) findViewById(R.id.txtClientCount);

        logService = new LogService();
        database = FirebaseDatabase.getInstance();
        clientRef = database.getReference(Constant.Nodes.Client);
        chefOrderRef = database.getReference(Constant.Nodes.ChefOrder);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_client);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, getColomnCount());
        recyclerView.setLayoutManager(mLayoutManager);

        setClientToGrid();

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_order_list);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showOrderListDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(Constant.CurrentUser != null){
            String name = Constant.CurrentUser.FirstName +" "+Constant.CurrentUser.LastName;
            menu.findItem(R.id.user_info).setTitle(name);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_exit || id == R.id.user_info) {
            Intent intent = new Intent(CashRegisterMainActivity.this, StartActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setClientToGrid(){

        final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.uploading), getString(R.string.uploading_data_message), false, false);

        try{
            clientRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Client> clientList = new ArrayList<>();

                    for(DataSnapshot pos : dataSnapshot.getChildren()){
                        Client client = pos.getValue(Client.class);
                        client.Key = pos.getKey();
                        clientList.add(client);
                    }

                    mAdapter = new ClientGridAdapter(CashRegisterMainActivity.this, clientList);
                    recyclerView.setAdapter(mAdapter);
                    txtClientCount.setText(getString(R.string.client_count) +": " + String.valueOf(dataSnapshot.getChildrenCount()));
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    progressDialog.dismiss();
                    saveLog("setClientToGrid(DatabaseError)", error.toString());
                    showAlert(getString(R.string.unexpected_error));
                }
            });
        }
        catch (Exception ex){
            progressDialog.dismiss();
            saveLog("setClientToGrid(catch)", ex.toString());
            showAlert(getString(R.string.unexpected_error));
        }

    }

    private  void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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

    private void saveLog(String metodName, String message){
        DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd.MM.yyyy hh:mm", new Date()).toString();
        String className = "CashRegisterMainActivity";

        logService.saveLog(date, className, metodName, message);
    }

    private void showOrderListDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.current_order_list_dialog_layout);
        dialog.setCanceledOnTouchOutside(false);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
        dialog.getWindow().setLayout(width, height);

        recyclerOrder = (RecyclerView) dialog.findViewById(R.id.recycler);
        recyclerOrder.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerOrder.setLayoutManager(layoutManager);

        final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.uploading), getString(R.string.uploading_data_message), false, false);

        try{
            chefOrderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<ChefOrder> orderList = new ArrayList<>();

                    if(dataSnapshot.hasChildren()){
                        for(DataSnapshot pos : dataSnapshot.getChildren()){
                            ChefOrder order = pos.getValue(ChefOrder.class);
                            orderList.add(order);
                        }
                        ChefOrderListAdapter adapter = new ChefOrderListAdapter(CashRegisterMainActivity.this, orderList);
                        recyclerOrder.setAdapter(adapter);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    progressDialog.dismiss();
                }
            });
        }
        catch (Exception ex){
            progressDialog.dismiss();
        }

        final Button btnClose = (Button) dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private  int getColomnCount(){

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);


        if (diagonalInches > 15){
            return 10;
        }
        else if (diagonalInches > 12 && diagonalInches <= 15){
            return 8;
        }
        else if (diagonalInches > 9 && diagonalInches <= 12){
            return 6;
        }
        else if (diagonalInches > 6 && diagonalInches <= 9){
            return 4;
        }
        else { return  1;}
    }

}
