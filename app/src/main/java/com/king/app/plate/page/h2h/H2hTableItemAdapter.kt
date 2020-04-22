package com.king.app.plate.page.h2h

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterH2hAllItemBinding

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/22 11:16
 */
class H2hTableItemAdapter: BaseBindingAdapter<AdapterH2hAllItemBinding, H2hTableItem>() {

    override fun onCreateBind(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AdapterH2hAllItemBinding = AdapterH2hAllItemBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterH2hAllItemBinding?, position: Int, bean: H2hTableItem) {
        binding!!.bean = bean
    }
}