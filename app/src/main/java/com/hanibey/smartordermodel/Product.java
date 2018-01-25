package com.hanibey.smartordermodel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanju on 22.09.2017.
 */

public class Product {

    public String Key;
    public String CategoryKey;
    public String Title;
    public double Price;
    public String Description;
    public String ImageName;
    public String ImageUrl;

    public Product() { }

    public Product(String key, String categoryKey, String title, double price, String description, String imageName, String imageUrl) {
        super();
        this.Key = key;
        this.CategoryKey = categoryKey;
        this.Title = title;
        this.Price = price;
        this.Description = description;
        this.ImageName = imageName;
        this.ImageUrl = imageUrl;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Key", Key);
        result.put("CategoryKey", CategoryKey);
        result.put("Title", Title);
        result.put("Price", Price);
        result.put("Description", Description);
        result.put("ImageName", ImageName);
        result.put("ImageUrl", ImageUrl);

        return result;
    }

}
