package com.hanibey.smartorder.administration;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.admin.AdminMainActivity;
import com.hanibey.smartorder.administration.cashregister.CashRegisterMainActivity;
import com.hanibey.smartorder.administration.chef.ChefMainActivity;
import com.hanibey.smartorderbusiness.GeneralService;
import com.hanibey.smartorderbusiness.LogService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.User;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.Date;

public class StartActivity extends AppCompatActivity {
    DatabaseReference userRef;
    LogService logService;
    private GeneralService generalService;

    EditText edtUserName, edtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        userRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.User);
        logService = new LogService();
        generalService = new GeneralService(this);

        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        //generalService.checkConnection();

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intentAdmin = new Intent(StartActivity.this, AdminMainActivity.class);
                startActivity(intentAdmin);*/

                if(TextUtils.isEmpty(edtUserName.getText()) || TextUtils.isEmpty(edtPassword.getText()))
                {
                    showAlert(getString(R.string.username_password_warning));
                }
                else {
                    userLogin(edtUserName.getText().toString().trim(), edtPassword.getText().toString().trim());
                }
            }
        });

    }

    private void userLogin(String uName, final String pas){

        final String key = uName +"_"+ pas;

        final ProgressDialog progressDialog = ProgressDialog.show(StartActivity.this, getString(R.string.checking),
                getString(R.string.checking_message), false, false);

        try{
            userRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        User user = dataSnapshot.getValue(User.class);
                        Constant.CurrentUser = user;
                        edtUserName.setText("");
                        edtUserName.setText("");
                        progressDialog.dismiss();

                        redirectToActivity(user.UserType);

                    }
                    else {
                        progressDialog.dismiss();
                        showAlert(getString(R.string.fual_login));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    saveLog("User Login(DatabaseError)", error.toString());
                    showAlert(getString(R.string.unexpected_error));
                    Log.w("Error", "Failed to read value.", error.toException());
                    progressDialog.dismiss();
                }
            });
        }
        catch (Exception ex){

            saveLog("User Login(catch)", ex.toString());

            showAlert(getString(R.string.unexpected_error));
            Log.e("Hata", ex.toString());
            progressDialog.dismiss();
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
        String className = "StartActivity";

        logService.saveLog(date, className, metodName, message);
    }

    private void redirectToActivity(String userType){

        switch (userType){
            case Constant.UserTypes.Admin:
                Intent intentAdmin = new Intent(StartActivity.this, AdminMainActivity.class);
                startActivity(intentAdmin);
                break;

            case Constant.UserTypes.CashRegister:
                Intent intentCashRegister = new Intent(StartActivity.this, CashRegisterMainActivity.class);
                startActivity(intentCashRegister);
                break;

            case Constant.UserTypes.Chef:
                Intent intentChef = new Intent(StartActivity.this, ChefMainActivity.class);
                startActivity(intentChef);
                break;

            default:
                showAlert("Kullanıcı bulanamadı!");
                break;
        }
    }

}
