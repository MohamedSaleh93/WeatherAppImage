package com.weather.photo.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.weather.photo.application.MainProgram;


/**
 * @author MohamedSaleh on 4/22/2017.
 */

public class SharedPreferenceManager {

    private static SharedPreferenceManager sharedPreferenceManager;
    private static final String MY_PREF = "WeatherPref";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private SharedPreferenceManager() {
        sharedPreferences = MainProgram.getContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferenceManager getInstance() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager();
        }
        return sharedPreferenceManager;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, @Nullable String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void cleanData() {
        editor.clear().commit();
    }

}
