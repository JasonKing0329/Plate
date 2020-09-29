package com.king.app.plate.page.h2h

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterH2hItemBinding

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 13:24
 */
class H2hItemAdapter: BaseBindingAdapter<AdapterH2hItemBinding, H2hItem>() {
    override fun onCreateBind(inflater: LayoutInflater, parent: ViewGroup): AdapterH2hItemBinding
        = AdapterH2hItemBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterH2hItemBinding?, position: Int, bean: H2hItem) {
        binding!!.bean = bean
        binding!!.group.setBackgroundColor(bean.bgColor)
    }
}