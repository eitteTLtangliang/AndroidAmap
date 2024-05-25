package com.liang.map.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import com.amap.api.navi.AmapRouteActivity


class CustomAmapRouteActivity: AmapRouteActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}