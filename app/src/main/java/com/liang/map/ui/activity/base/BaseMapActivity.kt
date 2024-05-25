package com.liang.map.ui.activity.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.amap.api.maps.MapView

abstract class BaseMapActivity<V : ViewBinding> : BaseActivity<V>() {
    protected lateinit var mapView: MapView

    @CallSuper
    override fun initView(savedInstanceState: Bundle?) {
        mapView = getAMapView()
        mapView.onCreate(savedInstanceState)
    }

    abstract fun getAMapView(): MapView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}