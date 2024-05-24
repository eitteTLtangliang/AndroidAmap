/*
 * Copyright(c) 2024.
 * project_name: Android AMap
 * email：tl594336505@163.com
 * Modified by：liang.tan
 * Copyright All rights reserved.
 */
package com.liang.map.ui.activity

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.services.core.AMapException
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.Inputtips.InputtipsListener
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.liang.map.databinding.ActivitySearchLocationBinding
import com.liang.map.ui.adapter.OnItemClickListener
import com.liang.map.ui.adapter.SearchLocationAdapter
import com.liang.map.util.Constants


class SearchLocationActivity : BaseActivity<ActivitySearchLocationBinding>(), InputtipsListener {
    private val mapLocation by lazy { intent.getParcelableExtra<AMapLocation>(Constants.MAP_LOCATION) }
    private val searchLocationAdapter by lazy { SearchLocationAdapter() }

    override fun getViewBinding(): ActivitySearchLocationBinding {
        return ActivitySearchLocationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.tvLeft.setOnClickListener {
            finish()
        }
        binding.inputEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim { it <= ' ' }
                val city = mapLocation?.city
                Log.d(TAG, "keyword:${keyword}, city:${city}")
                val inputQuery = InputtipsQuery(keyword, city)
                inputQuery.cityLimit = city != null
                val inputTips = Inputtips(this@SearchLocationActivity, inputQuery)
                inputTips.setInputtipsListener(this@SearchLocationActivity)
                inputTips.requestInputtipsAsyn()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.inputList.adapter = searchLocationAdapter
        searchLocationAdapter.setOnItemClickListener(object : OnItemClickListener<Tip> {
            override fun onItemClick(t: Tip) {
                val data = Intent()
                data.putExtra(Constants.MAP_TIP, t)
                setResult(RESULT_OK, data)
                finish()
            }
        })
    }

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
}