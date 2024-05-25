package com.liang.map.ui.activity

import android.os.Bundle
import android.util.Log
import com.amap.api.maps.model.LatLng
import com.amap.api.navi.AMapNaviIndependentRouteListener
import com.amap.api.navi.AMapNaviView
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.enums.PathPlanningStrategy
import com.amap.api.navi.enums.TravelStrategy
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.AMapCarInfo
import com.amap.api.navi.model.AMapNaviPathGroup
import com.amap.api.navi.model.NaviPoi
import com.liang.map.databinding.ActivityRideRouteCalculateBinding
import com.liang.map.ui.activity.base.BaseNaviActivity


class RideRouteCalculateActivity : BaseNaviActivity<ActivityRideRouteCalculateBinding>() {
    companion object {
        private const val TAG = "Map-RideRouteCalculateActivity"
    }

    override fun getViewBinding(): ActivityRideRouteCalculateBinding {
        return ActivityRideRouteCalculateBinding.inflate(layoutInflater)
    }

    override fun getAMapView(): AMapNaviView {
        return binding.naviView
    }

    override fun initView(savedInstanceState: Bundle?) {}

    override fun onInitNaviSuccess() {
        super.onInitNaviSuccess()

        /*val start = NaviLatLng(startLatLng.latitude, startLatLng.longitude)
        val end = NaviLatLng(endLatLng.point.latitude, endLatLng.point.longitude)
        val success = aMapNavi.calculateRideRoute(start, end)
        aMapNavi.isNaviTravelView = true
        Log.v(TAG, "success:${success}")*/

        /*val start = NaviPoi("起点", LatLng(startLatLng.latitude, startLatLng.longitude), "")
        val end = NaviPoi("终点", LatLng(endLatLng.point.latitude, endLatLng.point.longitude), "")
        aMapNavi.independentCalculateRoute(start, end, arrayListOf(), PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT, 2,
            object : AMapNaviIndependentRouteListener {
                override fun onIndependentCalculateSuccess(group: AMapNaviPathGroup) {
                    Log.i(TAG, "onIndependentCalculateSuccess")
                    group.selectRouteWithIndex(group.pathCount -1)
                    aMapNavi.startNaviWithPath(NaviType.GPS, group)
                }

                override fun onIndependentCalculateFail(p0: AMapCalcRouteResult) {
                    Log.w(TAG, "onIndependentCalculateFail")
                }
            })*/

        /*val success = aMapNavi.calculateEleBikeRoute(start, end)
        Log.v(TAG, "success:${success}")*/

        //car
        val strategy = aMapNavi.strategyConvert(true, false, false, false, false)
        val carInfo = AMapCarInfo()
        carInfo.carNumber = "京DFZ588"
        aMapNavi.setCarInfo(carInfo)
        aMapNavi.calculateDriveRoute(startNaviLatLngList, endNaviLatLngList, wayPointList, strategy)
    }

    override fun onCalculateRouteSuccess(result: AMapCalcRouteResult) {
        super.onCalculateRouteSuccess(result)
        aMapNavi.startNavi(NaviType.GPS)
    }
}