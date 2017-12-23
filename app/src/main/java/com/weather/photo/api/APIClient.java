package com.weather.photo.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author MohamedSaleh on 12/23/2017.
 */

public class APIClient {

    private static Retrofit retrofit;
    private final static String BASE_WEATHER_URL = "http://api.openweathermap.org";
    public static Retrofit getClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}
