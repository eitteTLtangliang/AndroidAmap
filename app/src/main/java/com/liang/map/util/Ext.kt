package com.liang.map.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings

fun Context.isGpsEnable(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Activity.openGps() {
    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(settingsIntent)
}