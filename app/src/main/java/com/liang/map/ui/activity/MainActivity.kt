@file:Suppress("SimpleDateFormat")

package com.liang.map.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.MapView
import com.amap.api.maps.model.*
import com.amap.api.services.core.AMapException.CODE_AMAP_SUCCESS
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Tip
import com.amap.api.services.route.*
import com.hjq.permissions.XXPermissions
import com.liang.map.R
import com.liang.map.databinding.ActivityMainBinding
import com.liang.map.ui.activity.base.BaseMapActivity
import com.liang.map.ui.overlay.RideRouteOverlay
import com.liang.map.util.AMapUtil
import com.liang.map.util.Constants
import com.liang.map.util.SensorEventHelper


class MainActivity : BaseMapActivity<ActivityMainBinding>(), AMap.OnMapClickListener,
    AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter,
    RouteSearch.OnRouteSearchListener,
    LocationSource, AMapLocationListener,
    AMap.OnMyLocationChangeListener {
    companion object {
        private const val TAG = "Map-MainActivity"
    }

    private val locationClient by lazy { AMapLocationClient(this) }
    private val locationOption by lazy { AMapLocationClientOption() }
    private val routeSearch by lazy { RouteSearch(this) }
    private var aMapLocation: AMapLocation? = null
    private var aMapTip: Tip? = null
    private val sensorHelper by lazy { SensorEventHelper(this) }
    private var onLocationChangedListener: LocationSource.OnLocationChangedListener? = null
    private var circle: Circle? = null
    private var locMarker: Marker? = null
    private var firstFix = false
    private var exitTime: Long = 0
    private var isRideRoute = false

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getAMapView(): MapView {
        return binding.map
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        //1.map view
        //mapView = binding.map
        //2.location permission
        if (checkPermission()) {
            initLocation()
        } else {
            requestPermission {
                initLocation()
            }
        }
        //3.register for activity result
        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    isRideRoute = true
                    aMapTip = result.data?.getParcelableExtra(Constants.MAP_TIP)
                    Log.i(
                        TAG,
                        "onItemClick, name:${aMapTip?.name}, address:${aMapTip?.address}, adcode:${aMapTip?.adcode}, district:${aMapTip?.district}, poiID:${aMapTip?.poiID}, typeCode:${aMapTip?.typeCode}, point:${aMapTip?.point}"
                    )
                    aMapTip?.point?.let { rideRoute(LatLonPoint(it.latitude, it.longitude)) }
                }
            }
        //4.listener
        binding.edtAddress.setOnClickListener {
            closeKeyboard()
            val intent = Intent(this, SearchLocationActivity::class.java).apply {
                putExtra(Constants.MAP_LOCATION, aMapLocation)
            }
            launcher.launch(intent)
        }
        binding.btnNavigation.setOnClickListener {
            if (aMapLocation == null) {
                Toast.makeText(
                    this,
                    "Current location acquisition failed, please try again!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (aMapTip == null) {
                Toast.makeText(
                    this,
                    "Destination not set, please select destination!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, RideRouteCalculateActivity::class.java).apply {
                    putExtra(Constants.START_LAT_LON_POINT, aMapLocation)
                    putExtra(Constants.END_LAT_LON_POINT, aMapTip)
                }
                startActivity(intent)

                /* val params =
                     AmapNaviParams(Poi(aMapLocation!!.poiName,
                         LatLng(aMapLocation!!.latitude, aMapLocation!!.longitude), ""), null,
                         Poi(aMapTip!!.address, LatLng(aMapTip!!.point.latitude, aMapTip!!.point.longitude), ""), AmapNaviType.RIDE)
                 params.setUseInnerVoice(true)
                 AmapNaviPage.getInstance().showRouteActivity(applicationContext, params, null, CustomAmapRouteActivity::class.java)*/
            }
        }
        mapView.map.apply {
            setOnMapClickListener(this@MainActivity)
            setOnMarkerClickListener(this@MainActivity)
            setOnInfoWindowClickListener(this@MainActivity)
            setInfoWindowAdapter(this@MainActivity)
            setOnMyLocationChangeListener(this@MainActivity)
            routeSearch.setRouteSearchListener(this@MainActivity)
        }
    }

    private fun initLocation() {
        locationOption.locationMode = AMapLocationMode.Hight_Accuracy
        //locationOption.interval = 3000
        locationOption.isOnceLocation = true
        locationClient.setLocationListener(this)
        locationClient.setLocationOption(locationOption)

        mapView.map.apply {
            //setMyLocationStyle(myLocationStyle)
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
            uiSettings.isScaleControlsEnabled = true
            mapType = AMap.MAP_TYPE_NORMAL
            setLocationSource(this@MainActivity)
            isMyLocationEnabled = true
        }
    }

    private fun rideRoute(latLonPoint: LatLonPoint) {
        if (aMapLocation == null) {
            Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show()
            return
        }
        mapView.map.apply {
            if (aMapLocation == null) {
                Toast.makeText(
                    this@MainActivity,
                    "Positioning in progress, try again later",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val startPoint =
                LatLonPoint(aMapLocation!!.latitude, aMapLocation!!.longitude)
            val endPoint = LatLonPoint(latLonPoint.latitude, latLonPoint.longitude)
            addMarker(
                MarkerOptions().position(AMapUtil.convertToLatLng(startPoint))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
            )
            addMarker(
                MarkerOptions().position(AMapUtil.convertToLatLng(endPoint))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
            )
            showProgressDialog()
            val fromAndTo = RouteSearch.FromAndTo(startPoint, endPoint)
            //Cycling path planning
            try {
                val query = RouteSearch.RideRouteQuery(fromAndTo /*, mode*/)
                routeSearch.calculateRideRouteAsyn(query)
            } catch (e: Exception) {
                Log.e(TAG, "ride route failed, e:${e.message}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == XXPermissions.REQUEST_CODE) {
            initLocation()
        }
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker) {
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onMapClick(latLng: LatLng) {
    }

    override fun onBusRouteSearched(result: BusRouteResult?, errorCode: Int) {}

    override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {}

    override fun onWalkRouteSearched(result: WalkRouteResult?, errorCode: Int) {}

    override fun onRideRouteSearched(result: RideRouteResult, errorCode: Int) {
        dismissProgressDialog()
        mapView.map.clear()
        if (errorCode == CODE_AMAP_SUCCESS) {
            if (result.paths != null) {
                Log.i(TAG, "paths.size:${result.paths.size}")
                result.paths.forEach {
                    Log.d(
                        TAG, "ridePath, steps.size:${it.steps.size}, \n" +
                                "distance:${it.distance}, duration:${it.duration}"
                    )
                }
                if (result.paths.size > 0) {
                    //TODO At present, the first set of solutions has been selected, and other path solutions will continue to be improved according to needs
                    val ridePath = result.paths[0]
                    val rideRouteOverlay =
                        RideRouteOverlay(mapView.map, ridePath, result.startPos, result.targetPos)
                    rideRouteOverlay.removeFromMap()
                    rideRouteOverlay.addToMap()
                    rideRouteOverlay.zoomToSpan()
                    val distance = ridePath.distance.toInt()
                    val duration = ridePath.duration.toInt()
                    val description =
                        AMapUtil.getFriendlyTime(duration) + "(" + AMapUtil.getFriendlyLength(
                            distance
                        ) + ")"
                    binding.bottomLayout.visibility = View.VISIBLE
                    binding.tvFirstLine.text = description
                    binding.tvSecondLine.visibility = View.GONE
                    binding.bottomLayout.setOnClickListener {
                        val intent = Intent(this, RideRouteDetailActivity::class.java)
                        intent.putExtra(Constants.RIDE_PATH, ridePath)
                        intent.putExtra(Constants.RIDE_RESULT, result)
                        startActivity(intent)
                    }
                } else if (result.paths == null) {
                    Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, errorCode, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume, isRideRoute:${isRideRoute}")
        if (!isRideRoute) {
            locationClient.startLocation()
            sensorHelper.registerSensorListener()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause, isRideRoute:${isRideRoute}")
        if (!isRideRoute) {
            sensorHelper.unRegisterSensorListener()
            sensorHelper.setCurrentMarker(null)
            locationClient.stopLocation()
            firstFix = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient.onDestroy()
    }

    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        if (curTime - exitTime > 2000) {
            Toast.makeText(this, "Press again to exit the program!", Toast.LENGTH_SHORT).show()
            exitTime = curTime
        } else {
            super.onBackPressed()
        }
    }

    override fun onMyLocationChange(location: Location) {
        Log.v(
            TAG,
            "latitude:${location.latitude}, longitude:${location.longitude}, accuracy:${location.accuracy}"
        )
    }

    override fun onLocationChanged(amapLocation: AMapLocation) {
        Log.v(TAG, "errorCode:${amapLocation.errorCode}, address:${amapLocation.address}")
        aMapLocation = amapLocation
        if (onLocationChangedListener != null) {
            if (amapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
                binding.locationErrInfoText.visibility = View.GONE
                val location = LatLng(amapLocation.latitude, amapLocation.longitude)
                if (!firstFix) {
                    firstFix = true
                    addCircle(location, amapLocation.accuracy.toDouble())
                    addMarker(location)
                    sensorHelper.setCurrentMarker(locMarker)
                } else {
                    circle?.center = location
                    circle?.radius = amapLocation.accuracy.toDouble()
                    locMarker?.position = location
                }
                mapView.map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
            } else {
                val errText =
                    "location failed, ${amapLocation.errorCode}, :${amapLocation.errorInfo}"
                Log.e(TAG, errText)
                binding.locationErrInfoText.visibility = View.VISIBLE
                binding.locationErrInfoText.text = errText
            }
        }
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        onLocationChangedListener = listener
    }

    override fun deactivate() {
        onLocationChangedListener = null
        locationClient.stopLocation()
        locationClient.onDestroy()
    }

    private fun addCircle(latlng: LatLng, radius: Double) {
        if (circle != null) {
            return
        }
        val options = CircleOptions()
        options.strokeWidth(1f)
        options.fillColor(Color.argb(10, 0, 0, 180))
        options.strokeColor(Color.argb(180, 3, 145, 255))
        options.center(latlng)
        options.radius(radius)
        circle = mapView.map.addCircle(options)
    }

    private fun addMarker(latlng: LatLng) {
        if (locMarker != null) {
            return
        }
        val bMap = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.navi_map_gps_locked
        )
        val des = BitmapDescriptorFactory.fromBitmap(bMap)
        //BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        val options = MarkerOptions()
        options.icon(des)
        options.anchor(0.5f, 0.5f)
        options.position(latlng)
        locMarker = mapView.map.addMarker(options)
        locMarker?.title = "mylocation"
    }

}