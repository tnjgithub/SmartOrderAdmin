package com.hanibey.smartorderbusiness;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.AppLog;
import com.hanibey.smartorderrepository.FirebaseDb;

/**
 * Created by Tanju on 25.12.2017.
 */

public class LogService {

    DatabaseReference logRef;

    public  LogService(){
        logRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.AppLog);
    }

    public void saveLog(String logDate, String className, String metodName, String logMessage){
        AppLog log = new AppLog(logDate, className, metodName, logMessage);
        logRef.push().setValue(log);
    }

}
