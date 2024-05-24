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

