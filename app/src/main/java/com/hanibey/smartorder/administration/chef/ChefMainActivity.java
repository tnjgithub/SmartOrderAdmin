package com.hanibey.smartorder.administration.chef;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.StartActivity;
import com.hanibey.smartorder.administration.admin.AdminMainActivity;
import com.hanibey.smartorder.administration.cashregister.CashRegisterMainActivity;
import com.hanibey.smartorderadapter.ChefOrderListAdapter;
import com.hanibey.smartorderadapter.ClientGridAdapter;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.ChefOrder;
import com.hanibey.smartordermodel.Client;

import java.util.ArrayList;

public class ChefMainActivity extends AppCompatActivity {

    FirebaseDatabase database ;
    DatabaseReference chefOrderRef;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private MediaPlayer mediaPlayer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);

        database = FirebaseDatabase.getInstance();
        chefOrderRef = database.getReference(Constant.Nodes.ChefOrder);

        loadData();
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

        if (id == R.id.action_exit) {
            Intent intent = new Intent(ChefMainActivity.this, StartActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    private void loadData(){

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

                        ChefOrderListAdapter adapter = new ChefOrderListAdapter(ChefMainActivity.this, orderList);
                        recyclerView.setAdapter(adapter);
                        showNotification("Yeni sipariş geldi");
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

    }



    private void showNotification(String message){

        managerOfSound();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View mySbView = snackbar.getView();
        TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void managerOfSound() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound);
        mediaPlayer.start();
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

}
