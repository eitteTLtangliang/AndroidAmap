/*
 * Copyright(c) 2024.
 * project_name: Android AMap
 * email：tl594336505@163.com
 * Modified by：liang.tan
 * Copyright All rights reserved.
 */

package com.liang.map.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.maps2d.AMapUtils
import com.amap.api.maps2d.model.LatLng
import com.amap.api.services.help.Tip
import com.liang.map.databinding.AdapterItemSearchLocationBinding

class SearchLocationAdapter : BaseRecyclerAdapter<AdapterItemSearchLocationBinding>() {
    private val _tips = mutableListOf<Tip>()
    private var _mapLocation: AMapLocation? = null
    private var _onItemClickListener: OnItemClickListener<Tip>? = null

    fun setOnItemClickListener(listener: OnItemClickListener<Tip>) {
        this._onItemClickListener = listener
    }

    fun locations(mapLocation: AMapLocation?, tips: List<Tip>) {
        this._tips.clear()
        this._mapLocation = mapLocation
        this._tips.addAll(tips)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerHolder<AdapterItemSearchLocationBinding> {
        val binding = AdapterItemSearchLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BaseRecyclerHolder(binding)
    }

    override fun onBaseBindViewHolder(position: Int, binding: AdapterItemSearchLocationBinding) {
        val tip = this._tips[position]
        binding.apply {
            llRoot.setOnClickListener {
                _onItemClickListener?.onItemClick(tip)
            }
            val distance = if (_mapLocation == null || tip.point == null) "" else "${
                AMapUtils.calculateLineDistance(
                    LatLng(tip.point.latitude, tip.point.longitude),
                    LatLng(_mapLocation!!.latitude, _mapLocation!!.longitude)
                ).toInt()
            }m"
            poiFieldId.text = tip.name
            poiValueId.text = "${tip.district}\t\t\t\t\t${distance}"
        }
    }

    override fun getItemCount(): Int {
        return this._tips.size
    }
}
