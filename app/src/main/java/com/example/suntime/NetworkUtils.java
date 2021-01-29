package com.example.suntime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtils {

    public final ApiService apiService;

    private volatile static NetworkUtils shared;
    public static NetworkUtils shared() {
        if (shared == null) {
            synchronized (NetworkUtils.class) {
                if (shared == null) {
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd hh:mm:ss")
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.sunrise-sunset.org/")
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                    ApiService apiService = retrofit.create(ApiService.class);
                    shared = new NetworkUtils(apiService);
                }
            }
        }
        return  shared;
    }
    private NetworkUtils(ApiService apiService){
        this.apiService = apiService;
    }

}
