package com.example.myapplication.tools;

import static android.content.Context.LOCATION_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

public class Location_Util {

    /**
     * 判断是否开启了GPS或网络定位开关
     */
    public static boolean isLocationProviderEnabled(Context context) {
        boolean result = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            result = true;
        }
        return result;
    }

}
