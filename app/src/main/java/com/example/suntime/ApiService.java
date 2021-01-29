package com.example.suntime;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @GET("json")
    Call<ResultItem> getSuntime(@Query("lat") String lat, @Query("lng") String lng);
}
