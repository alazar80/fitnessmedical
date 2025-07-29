package com.example.sql;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    @SerializedName("products")
    public List<Food> products;
}
