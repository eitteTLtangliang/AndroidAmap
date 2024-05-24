package com.liang.map.ui.overlay

import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.RidePath
import com.amap.api.services.route.RideStep
import com.liang.map.R
import com.liang.map.util.AMapUtil.convertArrList
import com.liang.map.util.AMapUtil.convertToLatLng

class RideRouteOverlay(amap: AMap, private val ridePath: RidePath, start: LatLonPoint, end: LatLonPoint) : RouteOverlay(amap, start, end) {
    private var mPolylineOptions: PolylineOptions? = null
    private var walkStationDescriptor: BitmapDescriptor? = null

    fun addToMap() {
        initPolylineOptions()
        try {
            val ridePaths = ridePath.steps
            mPolylineOptions!!.add(startPoint)
            for (i in ridePaths.indices) {
                val rideStep = ridePaths[i]
                val latLng = convertToLatLng(
                    rideStep
                        .polyline[0]
                )
                addRideStationMarkers(rideStep, latLng)
                addRidePolyLines(rideStep)
            }
            mPolylineOptions!!.add(endPoint)
            addStartAndEndMarker()
            showPolyline()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun addRidePolyLines(rideStep: RideStep) {
        mPolylineOptions?.addAll(convertArrList(rideStep.polyline))
    }

    private fun addRideStationMarkers(rideStep: RideStep, position: LatLng) {
        addStationMarker(
            MarkerOptions()
                .position(position)
                .title(
                    """
            方向:${rideStep.action}
            道路:${rideStep.road}
            """.trimIndent()
                )
                .snippet(rideStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor)
        )
    }

    private fun initPolylineOptions() {
        if (walkStationDescriptor == null) {
            walkStationDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.amap_ride)
        }
        mPolylineOptions = null
        mPolylineOptions = PolylineOptions()
        mPolylineOptions!!.color(getDriveColor()).width(getRouteWidth())
    }

    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }
}