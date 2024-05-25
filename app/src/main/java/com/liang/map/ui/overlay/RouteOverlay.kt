package com.liang.map.ui.overlay

import android.graphics.Bitmap
import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.liang.map.R
import com.liang.map.util.AMapUtil

open class RouteOverlay(protected val mAMap: AMap, start: LatLonPoint, end: LatLonPoint) {
    private var stationMarkers: MutableList<Marker>? = ArrayList()
    private var allPolyLines: MutableList<Polyline> = ArrayList()
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    protected var startPoint: LatLng? = null
    protected var endPoint: LatLng? = null
    private var startBit: Bitmap? = null
    private var endBit: Bitmap? = null
    private var busBit: Bitmap? = null
    private var walkBit: Bitmap? = null
    private var driveBit: Bitmap? = null
    protected var nodeIconVisible = true

    init {
        this.startPoint = AMapUtil.convertToLatLng(start)
        this.endPoint = AMapUtil.convertToLatLng(end)
    }

    fun removeFromMap() {
        if (startMarker != null) {
            startMarker!!.remove()
        }
        if (endMarker != null) {
            endMarker!!.remove()
        }
        for (marker in stationMarkers!!) {
            marker.remove()
        }
        for (line in allPolyLines) {
            line.remove()
        }
        destroyBit()
    }

    private fun destroyBit() {
        if (startBit != null) {
            startBit!!.recycle()
            startBit = null
        }
        if (endBit != null) {
            endBit?.recycle()
            endBit = null
        }
        if (busBit != null) {
            busBit?.recycle()
            busBit = null
        }
        if (walkBit != null) {
            walkBit?.recycle()
            walkBit = null
        }
        if (driveBit != null) {
            driveBit?.recycle()
            driveBit = null
        }
    }

    private fun getStartBitmapDescriptor(): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_start)
    }

    private fun getEndBitmapDescriptor(): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_end)
    }

    protected fun getBusBitmapDescriptor(): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_bus)
    }

    protected fun getWalkBitmapDescriptor(): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_man)
    }

    protected fun getDriveBitmapDescriptor(): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_car)
    }

    protected fun addStartAndEndMarker() {
        startMarker = mAMap.addMarker(
            MarkerOptions()
                .position(startPoint).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9")
        )
        endMarker = mAMap.addMarker(
            MarkerOptions().position(endPoint)
                .icon(getEndBitmapDescriptor()).title("\u7EC8\u70B9")
        )
    }

    fun zoomToSpan() {
        if (startPoint != null) {
            try {
                val bounds = getLatLngBounds()
                mAMap.animateCamera(
                    CameraUpdateFactory
                        .newLatLngBounds(bounds, 50)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun getLatLngBounds(): LatLngBounds {
        val b = LatLngBounds.builder()
        b.include(LatLng(startPoint!!.latitude, startPoint!!.longitude))
        b.include(LatLng(endPoint!!.latitude, endPoint!!.longitude))
        return b.build()
    }

    fun setNodeIconVisibility(visible: Boolean) {
        try {
            nodeIconVisible = visible
            if (stationMarkers != null && stationMarkers!!.size > 0) {
                for (i in stationMarkers!!.indices) {
                    stationMarkers!![i].isVisible = visible
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    protected fun addStationMarker(options: MarkerOptions?) {
        if (options == null) {
            return
        }
        val marker = mAMap.addMarker(options)
        if (marker != null) {
            stationMarkers!!.add(marker)
        }
    }

    protected fun addPolyLine(options: PolylineOptions?) {
        if (options == null) {
            return
        }
        val polyline = mAMap.addPolyline(options)
        if (polyline != null) {
            allPolyLines.add(polyline)
        }
    }

    protected fun getRouteWidth(): Float {
        return 18f
    }

    protected fun getWalkColor(): Int {
        return Color.parseColor("#6db74d")
    }

    protected fun getBusColor(): Int {
        return Color.parseColor("#537edc")
    }

    protected fun getDriveColor(): Int {
        return Color.parseColor("#537edc")
    }
}