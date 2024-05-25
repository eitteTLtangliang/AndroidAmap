package com.liang.map

import android.app.Application
import android.util.Log
import com.liang.map.util.DataStoreUtil

class AMapApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        DataStoreUtil.init(this)
        Log.v("AMapApplication", "onCreate")
    }
}