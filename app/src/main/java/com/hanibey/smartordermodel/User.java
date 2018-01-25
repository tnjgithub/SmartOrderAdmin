package com.hanibey.smartordermodel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 30.10.2017.
 */

public class User{
    public String Key;
    public String FirstName;
    public String LastName;
    public String UserName;
    public String Password;
    public String UserType;

    public User() { }

    public User(String key, String firstName, String lastName, String userName, String password, String userType) {
        super();
        this.Key = key;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.UserName = userName;
        this.Password = password;
        this.UserType = userType;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Key", Key);
        result.put("FirstName", FirstName);
        result.put("LastName", LastName);
        result.put("UserName", UserName);
        result.put("Password", Password);
        result.put("UserType", UserType);

        return result;
    }

}
