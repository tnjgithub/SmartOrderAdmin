package com.hanibey.smartordermodel;
import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 25.12.2017.
 */

public class Client {

    public String Key;
    public String Name;
    public String Status;
    public boolean ChangeStatus;

    public Client() { }

    public Client(String key, String name, String status, boolean changeStatus) {
        super();
        this.Key = key;
        this.Name = name;
        this.Status = status;
        this.ChangeStatus = changeStatus;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Key", Key);
        result.put("Name", Name);
        result.put("Status", Status);
        result.put("ChangeStatus", ChangeStatus);

        return result;
    }
}
