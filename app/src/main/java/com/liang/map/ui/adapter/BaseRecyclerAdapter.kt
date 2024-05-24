/*
 * Copyright(c) 2024.
 * project_name: Android AMap
 * email：tl594336505@163.com
 * Modified by：liang.tan
 * Copyright All rights reserved.
 */

package com.liang.map.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerAdapter<V : ViewBinding> : RecyclerView.Adapter<BaseRecyclerAdapter.BaseRecyclerHolder<V>>() {

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerHolder<V>

    override fun onBindViewHolder(holder: BaseRecyclerHolder<V>, position: Int) {
        onBaseBindViewHolder(position, holder.binding)
    }

    abstract fun onBaseBindViewHolder(position: Int, binding: V)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    open class BaseRecyclerHolder<V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)
}

interface OnItemClickListener<T> {
    fun onItemClick(t: T)
}