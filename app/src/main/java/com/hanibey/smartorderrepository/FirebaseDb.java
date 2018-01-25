package com.hanibey.smartorderrepository;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Tanju on 17.01.2018.
 */

public class FirebaseDb {
    private static FirebaseDatabase database;

    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            //database.setPersistenceEnabled(true);
        }
        return database;
    }
}
