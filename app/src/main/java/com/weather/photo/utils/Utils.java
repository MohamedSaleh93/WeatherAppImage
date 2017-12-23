package com.weather.photo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.weather.photo.model.WeatherModel;

/**
 * @author MohamedSaleh on 12/22/2017.
 */

public class Utils {

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static WeatherModel weatherModel;
    public static final String WEATHER_API_KEY = "70c8dcf36c9f5f6f31fb123aeccbb0bc";
    public static final String IMAGES_COUNT_KEY = "imagesCount";

    public static boolean isPermissionsGranted(@NonNull Context context) {
        int cameraPermission = ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION);
        int locationPermission = ContextCompat.checkSelfPermission(context, LOCATION_PERMISSION);
        if (cameraPermission == PackageManager.PERMISSION_GRANTED &&
                locationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static boolean showPermissionsExplanation(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(activity, LOCATION_PERMISSION);
    }
}
