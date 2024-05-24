/*
 * Copyright(c) 2024.
 * project_name: Android AMap
 * email：tl594336505@163.com
 * Modified by：liang.tan
 * Copyright All rights reserved.
 */
@file:Suppress("DEPRECATION")

package com.liang.map.ui.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps2d.MapView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions

abstract class BaseActivity<V : ViewBinding> : AppCompatActivity() {
    companion object {
        const val TAG = "Map-Activity"
    }

    protected lateinit var binding: V
    protected var mapView: MapView? = null
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            isIndeterminate = false
            setCancelable(true)
            setMessage("Searching...")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        //TODO Pop up prompt agreeing to privacy policy
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)
        initView()
        mapView?.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    abstract fun getViewBinding(): V

    abstract fun initView()

    protected fun showProgressDialog() {
        progressDialog.show()
    }

    protected fun dismissProgressDialog() {
        progressDialog.dismiss()
    }

    protected fun checkPermission(): Boolean {
        return XXPermissions.isGranted(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    protected fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    protected fun requestPermission(block: (Boolean) -> Unit) {
        XXPermissions.with(this).permission(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                Log.v(TAG, "permissions:${permissions}, allGranted:${allGranted}")
                block.invoke(allGranted)
            }

            override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                Log.w(TAG, "permissions:${permissions}, doNotAskAgain:${doNotAskAgain}")
                XXPermissions.startPermissionActivity(this@BaseActivity)
                block.invoke(false)
            }
        })
    }

    protected fun isGpsEnable(context: Context): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}