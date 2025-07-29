package com.example.sql;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BingApi {
    @GET("HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US")
    Call<BingResponse> getImage();
}
