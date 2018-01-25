package com.hanibey.smartorderrepository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hanibey.smartordermodel.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tanju on 30.09.2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME   = "smartOrder";
    private static final String TABLE_SETTINGS = "settings";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql = "CREATE TABLE " + TABLE_SETTINGS + "(id INTEGER PRIMARY KEY, branch_id INTEGER, branch_name TEXT, client_no TEXT)";
            db.execSQL(sql);
        }
        catch (Exception ex){
            Log.e("DBHelper Create", String.valueOf(ex));
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
            onCreate(db);
        }
        catch (Exception ex){
            Log.e("DBHelper Update", String.valueOf(ex));
        }

    }

    public String insertSettings(Settings setting) {

        String message;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("branch_id", setting.getBranchId());
            values.put("branch_name", setting.getBranchName());
            values.put("client_no", setting.getClientNo());

            db.insert(TABLE_SETTINGS, null, values);
            db.close();

            message = "İşlem başarılı.";
        }
        catch (Exception ex){
            message = String.valueOf(ex);
        }

       return  message;
    }

    public Settings getSettings() {

        Settings setting = new Settings();
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.query(TABLE_SETTINGS, new String[]{"id", "branch_id", "branch_name", "client_no"}, null, null, null, null, null);


            while (cursor.moveToNext()) {
                setting.setId(cursor.getInt(0));
                setting.setBranchId(cursor.getInt(1));
                setting.setBranchName(cursor.getString(2));
                setting.setClientNo(cursor.getString(3));

                break;
            }
        }
        catch (Exception ex){
            Log.e("DBHelper getSettings", String.valueOf(ex));
        }



        return setting;
    }

    public int getBranchId() {
        Settings setting = getSettings();
        int branchId = 0;

        if(setting != null && setting.getBranchId() > 0)
            branchId = setting.getBranchId();

        return branchId;
    }

    public void updateClientNo(int id, String clientNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("client_no", clientNo);

        // updating row
        db.update(TABLE_SETTINGS, values, "id=?",
                new String[] { String.valueOf(id) });
    }

    public void updateSettings(Settings setting) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("branch_id", setting.getBranchId());
        values.put("branch_name", setting.getBranchName());

        // updating row
        db.update(TABLE_SETTINGS, values, "id=?",
                new String[] { String.valueOf(setting.getId()) });
    }

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SETTINGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SETTINGS, null, null);
        db.close();
    }
}
