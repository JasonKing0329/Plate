package com.king.app.plate.page.match

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterFinalScoreItemBinding

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/11 11:05
 */
class FinalScoreAdapter : BaseBindingAdapter<AdapterFinalScoreItemBinding, FinalPlayerScore>() {

    override fun onCreateBind(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AdapterFinalScoreItemBinding = AdapterFinalScoreItemBinding.inflate(inflater, parent, false)

    override fun onBindItem(
        binding: AdapterFinalScoreItemBinding?,
        position: Int,
        bean: FinalPlayerScore
    ) {
        binding!!.bean = bean
    }
}