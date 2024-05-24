package com.liang.map.ui.activity

import com.amap.api.services.route.RidePath
import com.liang.map.databinding.ActivityRouteDetailBinding
import com.liang.map.ui.adapter.RideSegmentListAdapter
import com.liang.map.util.AMapUtil
import com.liang.map.util.Constants

class RideRouteDetailActivity : BaseActivity<ActivityRouteDetailBinding>() {
    private val ridePath by lazy { intent.getParcelableExtra<RidePath>(Constants.RIDE_PATH)!! }
    private val rideSegmentListAdapter by lazy { RideSegmentListAdapter(this, ridePath.steps) }

    override fun getViewBinding(): ActivityRouteDetailBinding {
        return ActivityRouteDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val duration = AMapUtil.getFriendlyTime(ridePath.duration.toInt())
        val distance = AMapUtil.getFriendlyLength(ridePath.distance.toInt())
        binding.apply {
            ivLeft.setOnClickListener { finish() }
            tvDigest.text = "${duration}(${distance})"
            rideSegmentList.adapter = rideSegmentListAdapter
        }
    }
}