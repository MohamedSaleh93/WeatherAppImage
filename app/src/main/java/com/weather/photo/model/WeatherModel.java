package com.weather.photo.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author MohamedSaleh on 12/23/2017.
 */

public class WeatherModel {

    @SerializedName("weather")
    private List<WeatherDescriptionModel> weatherDescriptionModel;
    @SerializedName("main")
    private MainWeatherModel mainWeatherModel;
    private String countryName;

    public List<WeatherDescriptionModel> getWeatherDescriptionModel() {
        return weatherDescriptionModel;
    }

    public void setWeatherDescriptionModel(List<WeatherDescriptionModel> weatherDescriptionModel) {
        this.weatherDescriptionModel = weatherDescriptionModel;
    }

    public MainWeatherModel getMainWeatherModel() {
        return mainWeatherModel;
    }

    public void setMainWeatherModel(MainWeatherModel mainWeatherModel) {
        this.mainWeatherModel = mainWeatherModel;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
