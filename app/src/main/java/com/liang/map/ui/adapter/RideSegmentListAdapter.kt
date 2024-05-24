/*
 * Copyright(c) 2024.
 * project_name: Android AMap
 * email：tl594336505@163.com
 * Modified by：liang.tan
 * Copyright All rights reserved.
 */
package com.liang.map.ui.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.amap.api.services.route.RideStep
import com.liang.map.R
import com.liang.map.util.AMapUtil

class RideSegmentListAdapter(private val context: Context, rideSteps: List<RideStep>) : BaseAdapter() {
    private val _rideSteps = mutableListOf<RideStep>()

    companion object {
        private const val TAG = "Map-RideSegmentListAdapter"
    }

    init {
        Log.v(TAG, "before, size:${rideSteps.size}")
        _rideSteps.add(RideStep())
        _rideSteps.addAll(rideSteps)
        _rideSteps.add(RideStep())
        Log.v(TAG, "after, size:${_rideSteps.size}")
    }

    override fun getCount(): Int {
        return _rideSteps.size
    }

    override fun getItem(position: Int): Any {
        return _rideSteps[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder: ViewHolder?
        val rootView = if (convertView == null) {
            holder = ViewHolder()
            val view = View.inflate(context, R.layout.item_bus_segment, null)
            holder.lineName = view.findViewById<View>(R.id.bus_line_name) as TextView
            holder.dirIcon = view.findViewById<View>(R.id.bus_dir_icon) as ImageView
            holder.dirUp = view.findViewById<View>(R.id.bus_dir_icon_up) as ImageView
            holder.dirDown = view.findViewById<View>(R.id.bus_dir_icon_down) as ImageView
            holder.splitLine = view.findViewById<View>(R.id.bus_seg_split_line) as ImageView
            view.tag = holder
            view
        } else {
            holder = convertView.tag as ViewHolder
            convertView
        }
        val item = _rideSteps[position]
        return when (position) {
            0 -> {
                holder.dirIcon!!.setImageResource(R.drawable.dir_start)
                holder.lineName!!.text = "Start out"
                holder.dirUp!!.visibility = View.INVISIBLE
                holder.dirDown!!.visibility = View.VISIBLE
                holder.splitLine!!.visibility = View.INVISIBLE
                rootView
            }
            _rideSteps.size - 1 -> {
                holder.dirIcon!!.setImageResource(R.drawable.dir_end)
                holder.lineName!!.text = "Arrive at the destination"
                holder.dirUp!!.visibility = View.VISIBLE
                holder.dirDown!!.visibility = View.INVISIBLE
                rootView
            }
            else -> {
                holder.splitLine!!.visibility = View.VISIBLE
                holder.dirUp!!.visibility = View.VISIBLE
                holder.dirDown!!.visibility = View.VISIBLE
                val actionName = item.action
                val resID: Int = AMapUtil.getWalkActionID(actionName)
                holder.dirIcon!!.setImageResource(resID)
                holder.lineName!!.text = item.instruction
                rootView
            }
        }
    }

    private class ViewHolder {
        var lineName: TextView? = null
        var dirIcon: ImageView? = null
        var dirUp: ImageView? = null
        var dirDown: ImageView? = null
        var splitLine: ImageView? = null
    }
}