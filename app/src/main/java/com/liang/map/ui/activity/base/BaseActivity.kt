/*
 * Copyright(c) 2024.
 * project_name: Android AMap
 * email：tl594336505@163.com
 * Modified by：liang.tan
 * Copyright All rights reserved.
 */
@file:Suppress("DEPRECATION")

package com.liang.map.ui.activity.base

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.amap.api.maps.MapsInitializer
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.liang.map.util.Constants
import com.liang.map.util.DataStoreUtil

abstract class BaseActivity<V : ViewBinding> : AppCompatActivity() {
    companion object {
        private const val TAG = "Map-BaseActivity"
    }

    protected lateinit var binding: V

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
        initView(savedInstanceState)
    }

    abstract fun getViewBinding(): V

    abstract fun initView(savedInstanceState: Bundle?)

    protected fun showProgressDialog() {
        progressDialog.show()
    }

    protected fun dismissProgressDialog() {
        progressDialog.dismiss()
    }

    protected fun showPrivacyDialog() {
        if (DataStoreUtil.readBooleanData(Constants.PRIVACY_AGREE)) return
        MapsInitializer.updatePrivacyShow(this, true, true)
        val spannable =
            SpannableStringBuilder("\"亲，感谢您对XXX一直以来的信任！我们依据最新的监管要求更新了XXX《隐私权政策》，特向您说明如下\n1.为向您提供交易相关基本功能，我们会收集、使用必要的信息；\n2.基于您的明示授权，我们可能会获取您的位置（为您提供附近的商品、店铺及优惠资讯等）等信息，您有权拒绝或取消授权；\n3.我们会采取业界先进的安全措施保护您的信息安全；\n4.未经您同意，我们不会从第三方处获取、共享或向提供您的信息；\n")
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            35,
            42,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        AlertDialog.Builder(this)
            .setTitle("温馨提示(隐私合规示例)")
            .setMessage(spannable)
            .setPositiveButton(
                "同意"
            ) { _, _ ->
                MapsInitializer.updatePrivacyAgree(this, true)
                DataStoreUtil.saveSyncBooleanData(Constants.PRIVACY_AGREE, true)
            }
            .setNegativeButton(
                "不同意"
            ) { _, _ ->
                MapsInitializer.updatePrivacyAgree(
                    this,
                    false
                )
                finish()
            }
            .show()
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

    protected fun isGpsEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    protected fun registerGpsMonitor() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(gpsReceiver, intentFilter)
    }

    protected fun unregisterGpsMonitor() {
        unregisterReceiver(gpsReceiver)
    }

    private val gpsReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.R)
        override fun onReceive(context: Context?, intent: Intent?) {
            if (isFinishing || isDestroyed) {
                Log.w(TAG, "isFinishing:${isFinishing}, isDestroyed:${isDestroyed}")
                return
            }
            intent?.apply {
                when (action) {
                    LocationManager.PROVIDERS_CHANGED_ACTION -> {
                        val isGpsEnable = isGpsEnable()
                        Log.d(TAG, "providers change, isGpsEnable:${isGpsEnable}")
                        if (!isGpsEnable) {
                            Snackbar.make(
                                binding.root,
                                "GPS is turned off, please turn it on.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}