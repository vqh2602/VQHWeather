package com.example.vqhcovid.API;

import com.example.vqhcovid.Modul.Covid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

//    api https://api.apify.com/v2/key-value-stores/EaCBL1JNntjR3EakU/records/LATEST?disableRedirect=true
//    https://disease.sh/v3/covid-19/countries/vietnam"
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://disease.sh/")
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiService.class);

    @GET("v3/covid-19/countries/vietnam")
    Call<Covid> convertapitovn(@Query("") String disableRedirect);

    @GET("v3/covid-19/all")
    Call<Covid> convertapitoall(@Query("") String disableRedirect);

}




