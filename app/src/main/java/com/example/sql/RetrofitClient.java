package com.example.sql;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    public static ApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit.create(ApiService.class);
    }
}
