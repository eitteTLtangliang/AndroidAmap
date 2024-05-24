###  项目地址
https://github.com/eitteTLtangliang/AndroidAmap

### 获取AMAP apiKey
1.注册Amap账号，控制台->我的应用->新建应用； 
2.在Android Studio生成jks签名文件，keytool读取签名信息；
3.填写SHA1签名以及Package name；
4.填写完整信息后获取apiKey。

###  开发Amap地图功能
1.权限授予
  1.1.静态权限  AndroidManifest.xml 声明
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  1.2.动态权限  ACCESS_COARSE_LOCATION与ACCESS_FINE_LOCATION
       //2.location permission
       if (checkPermission()) {
           initLocation()
          } else {
             requestPermission {
             initLocation()
          }
       }
2.初始化Amap
  locationOption.locationMode = AMapLocationMode.Hight_Accuracy
  //locationOption.interval = 3000
  locationOption.isOnceLocation = true
  locationClient.setLocationListener(this)
  locationClient.setLocationOption(locationOption)
  mapView?.map?.apply {
    //setMyLocationStyle(myLocationStyle)
    uiSettings.isMyLocationButtonEnabled = true
    uiSettings.isScrollGesturesEnabled = true
    uiSettings.isScaleControlsEnabled = true
    mapType = AMap.MAP_TYPE_NORMAL
    etLocationSource(this@MainActivity)
    isMyLocationEnabled = true
 }
3.使用sensor水平方向改变指北方向
override fun onSensorChanged(event: SensorEvent) {
        if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
            return
        }
        when (event.sensor.type) {
            Sensor.TYPE_ORIENTATION -> {
                var x = event.values[0]
                x += getScreenRotationOnPhone().toFloat()
                x %= 360.0f
                if (x > 180.0f) x -= 360.0f else if (x < -180.0f) x += 360.0f
                if (abs(angle - x) < 3.0f) {
                    return
                }
                angle = if (java.lang.Float.isNaN(x)) 0f else x
                if (marker != null) {
                    marker!!.setRotateAngle(360 - angle)
                }
                lastTime = System.currentTimeMillis()
            }
        }
    }
4.搜索keyword，将当前定位的mapLocation传入至搜索页输入提示页，骑手可通过Enter Keyword找到目的地，app计算距离
val intent = Intent(this, SearchLocationActivity::class.java)
intent.putExtra(Constants.MAP_LOCATION, currentMapLocation)
launcher.launch(intent)

......
override fun onGetInputtips(tipList: List<Tip>, rCode: Int) {
        Log.i(TAG, "tipList:${tipList.size}, rCode:${rCode}")
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            for (i in tipList.indices) {
                val tip = tipList[i]
                Log.v(
                    TAG,
                    "onGetInputtips, name:${tip.name}, address:${tip.address}, adcode:${tip.adcode}, district:${tip.district}, poiID:${tip.poiID}, typeCode:${tip.typeCode}, point:${tip.point}"
                )
            }
            searchLocationAdapter.locations(mapLocation, tipList)
        } else {
            Toast.makeText(this, rCode, Toast.LENGTH_SHORT).show()
        }
}
5.Ride路径规划，App默认选择第一个推荐方案，其他方案待实际应用继续开发
private fun rideRoute(latLonPoint: LatLonPoint) {
        if (currentMapLocation == null) {
            Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show()
            return
        }
        mapView?.map?.apply {
            if (currentMapLocation == null) {
                Toast.makeText(
                    this@MainActivity,
                    "Positioning in progress, try again later",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val startPoint =
                LatLonPoint(currentMapLocation!!.latitude, currentMapLocation!!.longitude)
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
                AMapLocationClient.updatePrivacyShow(this@MainActivity, true, true)
                AMapLocationClient.updatePrivacyAgree(this@MainActivity, true)
                val query = RouteSearch.RideRouteQuery(fromAndTo /*, mode*/)
                routeSearch.calculateRideRouteAsyn(query)
            } catch (e: Exception) {
                Log.e(TAG, "ride route failed, e:${e.message}")
            }
        }
    }

......

override fun onRideRouteSearched(result: RideRouteResult, errorCode: Int) {
     dismissProgressDialog()
     mapView?.map?.clear()
    if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
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
                     RideRouteOverlay(mapView!!.map, ridePath, result.startPos, result.targetPos)
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
6.路径详情
详见RideRouteDetailActivity.kt
  

