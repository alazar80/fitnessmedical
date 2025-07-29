package com.example.sql;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/v2/search")
    Call<SearchResponse> fetchJuices(
        @Query("categories_tags_en") String category,
        @Query("fields") String fields
    );
}
