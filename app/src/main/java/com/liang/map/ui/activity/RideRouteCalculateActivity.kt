package com.liang.map.ui.activity

import android.os.Bundle
import com.amap.api.navi.AMapNaviView
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.NaviLatLng
import com.liang.map.databinding.ActivityRideRouteCalculateBinding
import com.liang.map.ui.activity.base.BaseNaviActivity

class RideRouteCalculateActivity : BaseNaviActivity<ActivityRideRouteCalculateBinding>() {

    override fun getViewBinding(): ActivityRideRouteCalculateBinding {
        return ActivityRideRouteCalculateBinding.inflate(layoutInflater)
    }

    override fun getAMapView(): AMapNaviView {
        return binding.naviView
    }

    override fun initView(savedInstanceState: Bundle?) {}

    override fun onInitNaviSuccess() {
        super.onInitNaviSuccess()
        aMapNavi.calculateRideRoute(
            NaviLatLng(startLatLng.latitude, startLatLng.longitude),
            NaviLatLng(endLatLng.latitude, endLatLng.longitude)
        )
    }

    override fun onCalculateRouteSuccess(result: AMapCalcRouteResult) {
        super.onCalculateRouteSuccess(result)
        aMapNavi.startNavi(NaviType.GPS)
    }
}