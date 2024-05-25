package com.liang.map.ui.activity.base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.amap.api.location.AMapLocationClient
import com.amap.api.navi.*
import com.amap.api.navi.enums.AMapNaviParallelRoadStatus
import com.amap.api.navi.model.*
import com.amap.api.services.core.LatLonPoint
import com.liang.map.util.Constants
import com.liang.map.util.TTSController

abstract class BaseNaviActivity<V : ViewBinding> : /*BaseActivity<V>*/ Activity(), AMapNaviListener,
    AMapNaviViewListener, ParallelRoadListener {
    companion object {
        private const val TAG = "Map-BaseNaviActivity"
    }

    protected lateinit var binding: V
    private lateinit var aMapNaviView: AMapNaviView
    protected val aMapNavi: AMapNavi by lazy { AMapNavi.getInstance(application) }
    protected var ttsManager: TTSController? = null
    protected val startLatLng by lazy { intent.getParcelableExtra<LatLonPoint>(Constants.START_LAT_LON_POINT)!! }
    protected val endLatLng by lazy { intent.getParcelableExtra<LatLonPoint>(Constants.END_LAT_LON_POINT)!! }
    protected var wayPointList = arrayListOf<NaviLatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        Log.i(
            TAG, "onCreate, \n" +
                    "startLatLng(${startLatLng.latitude}, ${startLatLng.longitude}), \n" +
                    "endLatLng(${endLatLng.latitude}, ${endLatLng.longitude})"
        )
        aMapNaviView = getAMapView()
        aMapNaviView.apply {
            onCreate(savedInstanceState)
            setAMapNaviViewListener(this@BaseNaviActivity)
        }
        aMapNavi.apply {
            addAMapNaviListener(this@BaseNaviActivity)
            addParallelRoadListener(this@BaseNaviActivity)
            setUseInnerVoice(true, true)
            setEmulatorNaviSpeed(75)
            naviSetting.isScreenAlwaysBright = true
        }
        initView(savedInstanceState)
//        registerGpsMonitor()
    }

    abstract fun getAMapView():AMapNaviView

    abstract fun getViewBinding(): V

    abstract fun initView(savedInstanceState: Bundle?)

    override fun onResume() {
        super.onResume()
        aMapNaviView.onResume()
    }

    override fun onPause() {
        super.onPause()
        aMapNaviView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        aMapNaviView.onDestroy()
        aMapNavi.stopNavi()
        AMapNavi.destroy()
        //unregisterGpsMonitor()
    }

    override fun onInitNaviFailure() {
        Log.w(TAG, "init_navi_failed")
        Toast.makeText(this, "Init navi Failed", Toast.LENGTH_SHORT).show()
    }

    override fun onInitNaviSuccess() {
        Log.i(TAG, "initNavi_success")
    }

    override fun onStartNavi(type: Int) {
        Log.v(TAG, "start_navi, type:${type}")
    }

    override fun onTrafficStatusUpdate() {
        Log.v(TAG, "traffic_status_update")
    }

    override fun onLocationChange(location: AMapNaviLocation) {
        Log.v(
            TAG,
            "location_change, locationType:${location.locationType}, accuracy:${location.accuracy}"
        )
    }

    override fun onGetNavigationText(type: Int, text: String) {
        Log.v(TAG, "get_navigation_text, type:${type}, text:${text}")
    }

    override fun onGetNavigationText(info: String) {
        Log.v(TAG, "get_navigation_text, info:${info}")
    }

    override fun onEndEmulatorNavi() {
        Log.v(TAG, "end_emulator_navi")
    }

    override fun onArriveDestination() {
        Log.v(TAG, "arrive_destination")
    }

    override fun onCalculateRouteFailure(code: Int) {
        Log.v(TAG, "calculate_route_failure, code:${code}")
    }

    override fun onReCalculateRouteForYaw() {
        Log.v(TAG, "recalculate_route_for_yaw")
    }

    override fun onReCalculateRouteForTrafficJam() {
        Log.v(TAG, "recalculate_route_for_traffic_jam")
    }

    override fun onArrivedWayPoint(wayID: Int) {
        Log.v(TAG, "arrived_way_point, wayID:${wayID}")
    }

    override fun onGpsOpenStatus(enabled: Boolean) {
        Log.v(TAG, "gps_open_status, enabled:${enabled}")
    }

    override fun onNaviSetting() {
        Log.v(TAG, "navi_setting")
    }

    override fun onNaviMapMode(naviMode: Int) {
        Log.v(
            TAG,
            "navi_map_mode, Navigation mode front of the car, ${if (naviMode == 0) "front of the car facing up state" else "North facing up mode"}"
        )
    }

    override fun onNaviCancel() {
        Log.w(TAG, "navi_cancel")
        finish()
    }

    override fun onNaviTurnClick() {
        Log.v(TAG, "navi_turn_click")
    }

    override fun onNextRoadClick() {
        Log.v(TAG, "next_road_click")
    }

    override fun onScanViewButtonClick() {
        Log.v(TAG, "scanView_button_click")
    }

    override fun updateCameraInfo(aMapCameraInfos: Array<AMapNaviCameraInfo>) {
        Log.v(TAG, "update_camera_info")
    }

    override fun onServiceAreaUpdate(amapServiceAreaInfos: Array<AMapServiceAreaInfo>) {
        Log.v(TAG, "service_area_update")
    }

    override fun onNaviInfoUpdate(naviinfo: NaviInfo) {
        Log.v(TAG, "navi_info_update")
    }

    override fun OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfo: AMapNaviTrafficFacilityInfo?) {
        Log.v(TAG, "update_traffic_facility")
    }

    override fun showCross(aMapNaviCross: AMapNaviCross) {
        Log.v(TAG, "show_cross")
    }

    override fun hideCross() {
        Log.v(TAG, "hide_cross")
    }

    override fun showLaneInfo(
        laneInfos: Array<AMapLaneInfo>,
        laneBackgroundInfo: ByteArray,
        laneRecommendedInfo: ByteArray
    ) {
        Log.v(TAG, "show_lane_info")
    }

    override fun hideLaneInfo() {
        Log.v(TAG, "hide_lane_info")
    }

    override fun onCalculateRouteSuccess(ints: IntArray) {
        Log.i(TAG, "calculate_route_success, ints:${ints.toList()}")
    }

    override fun notifyParallelRoad(i: Int) {
        Log.v(TAG, "notify_parallel_road, i:${i}")
    }

    override fun OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfos: Array<AMapNaviTrafficFacilityInfo?>?) {
        Log.v(TAG, "update_traffic_facility, Update transportation facility information")
    }

    override fun updateAimlessModeStatistics(aimLessModeStat: AimLessModeStat?) {
        Log.v(TAG, "update_aimless_modeStatistics, Update cruise mode statistics")
    }

    override fun updateAimlessModeCongestionInfo(aimLessModeCongestionInfo: AimLessModeCongestionInfo?) {
        Log.v(
            TAG,
            "update_aimless_mode_congestion_info, Update congestion information for cruise mode"
        )
    }

    override fun onPlayRing(i: Int) {
        Log.v(TAG, "playing")
    }

    override fun onLockMap(isLock: Boolean) {
        Log.v(TAG, "lock_map, Callback when the lock map state changes")
    }

    override fun onNaviViewLoaded() {
        Log.d(TAG, "navi_view_loaded")
        Log.w(
            TAG,
            "navi_view_loaded, Please do not use AMapNaviView. getMap(). setOnMapLoadedListener(); Overwrite navigation SDK's internal line drawing logic"
        )
    }

    override fun onMapTypeChanged(type: Int) {
        Log.v(TAG, "map_type_changed, type:${type}")
    }

    override fun onNaviViewShowMode(mode: Int) {
        Log.v(TAG, "navi_view_show_mode, mode:${mode}")
    }

    override fun onNaviBackClick(): Boolean {
        Log.v(TAG, "navi_back_click")
        return false
    }

    override fun showModeCross(aMapModelCross: AMapModelCross) {
        Log.v(TAG, "navi_back_click")
    }

    override fun hideModeCross() {
        Log.v(TAG, "hide_mode_cross")
    }

    override fun updateIntervalCameraInfo(
        info0: AMapNaviCameraInfo,
        info1: AMapNaviCameraInfo,
        interval: Int
    ) {
        Log.v(TAG, "update_interval_camera_info, interval:${interval}")
    }

    override fun showLaneInfo(info: AMapLaneInfo) {
        Log.v(TAG, "show_lane_info, ${info.isRecommended}, laneCount:${info.laneCount}")
    }

    override fun onCalculateRouteSuccess(result: AMapCalcRouteResult) {
        Log.i(
            TAG,
            "calculate_route_success, calcRouteType:${result.calcRouteType}, routeId:${result.routeid}, errorDescription:${result.errorDescription}, errorCode:${result.errorCode}, errorDetail:${result.errorDetail}"
        )
    }

    override fun onCalculateRouteFailure(result: AMapCalcRouteResult) {
        Log.e(TAG, "--------------------------------------------")
        Log.i(TAG, "路线计算失败：错误码=" + result.errorCode + ",Error Message= " + result.errorDescription)
        Log.i(TAG, "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/")
        Log.e(TAG, "--------------------------------------------")
        Toast.makeText(
            this,
            "errorInfo：" + result.errorDetail + ", Message：" + result.errorDescription,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onNaviRouteNotify(data: AMapNaviRouteNotifyData) {
        Log.v(
            TAG,
            "navi_route_notify, notifyType:${data.notifyType}, distance:${data.distance}, roadName:${data.roadName}, isSuccess:${data.isSuccess}, reason:${data.reason}"
        )
    }

    override fun onGpsSignalWeak(weak: Boolean) {
        Log.v(TAG, "gps_signal_weak, weak:${weak}")
    }

    override fun notifyParallelRoad(status: AMapNaviParallelRoadStatus) {
        Log.v(
            TAG,
            "notify_parallel_road, status:${status.status}, ElevatedRoadStatusFlag:${status.getmElevatedRoadStatusFlag()}, ParallelRoadStatusFlag:${status.getmParallelRoadStatusFlag()}"
        )
        val state =
            if (status.getmElevatedRoadStatusFlag() == AMapNaviParallelRoadStatus.STATUS_MAIN_ROAD) {
                "Currently on the elevated road"
            } else if (status.getmElevatedRoadStatusFlag() == AMapNaviParallelRoadStatus.STATUS_SIDE_ROAD) {
                "Currently under the elevated road"
            } else if (status.getmParallelRoadStatusFlag() == AMapNaviParallelRoadStatus.STATUS_MAIN_ROAD) {
                "Currently on the main road"
            } else if (status.getmParallelRoadStatusFlag() == AMapNaviParallelRoadStatus.STATUS_SIDE_ROAD) {
                "Currently on the auxiliary road"
            } else ""
        if (state.isNotEmpty()) {
            Toast.makeText(this, state, Toast.LENGTH_SHORT).show()
            Log.d(TAG, state)
        }
    }
}