package com.example.sql;

import com.google.gson.annotations.SerializedName;

public class Food {
    @SerializedName("product_name")
    public String name;

    @SerializedName("image_front_url")
    public String imageUrl;
}
