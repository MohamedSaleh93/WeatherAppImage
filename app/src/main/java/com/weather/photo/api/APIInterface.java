package com.weather.photo.api;

import com.weather.photo.model.WeatherModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author MohamedSaleh on 12/23/2017.
 */

public interface APIInterface {

    @GET("/data/2.5/weather?")
    Call<WeatherModel> getWeatherTemperatureByLocation(@Query("lat") double latitude,
                                                       @Query("lon") double longitude,
                                                       @Query("APPID") String appId,
                                                       @Query("units") String unit);
}
