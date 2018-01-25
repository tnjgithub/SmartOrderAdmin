package com.hanibey.smartordermodel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 23.10.2017.
 */

public class Category {

    public String Key;
    public String Name;

    public Category() { }

    public Category(String key, String name) {
        super();
        this.Key = key;
        this.Name = name;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Key", Key);
        result.put("Name", Name);

        return result;
    }

}
