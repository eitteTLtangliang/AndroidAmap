package com.liang.map.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import com.amap.api.maps2d.model.Marker
import kotlin.math.abs


class SensorEventHelper(private val context: Context) : SensorEventListener {
    companion object {
        private const val TIME_SENSOR = 100
    }

    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var sensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    private var lastTime: Long = 0
    private var angle = 0f
    private var marker: Marker? = null

    fun registerSensorListener() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun unRegisterSensorListener() {
        sensorManager.unregisterListener(this, sensor)
    }

    fun setCurrentMarker(marker: Marker?) {
        this.marker = marker
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // TODO Auto-generated method stub
    }

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

    private fun getScreenRotationOnPhone(): Int {
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        when (display.rotation) {
            Surface.ROTATION_0 -> return 0
            Surface.ROTATION_90 -> return 90
            Surface.ROTATION_180 -> return 180
            Surface.ROTATION_270 -> return -90
        }
        return 0
    }
}