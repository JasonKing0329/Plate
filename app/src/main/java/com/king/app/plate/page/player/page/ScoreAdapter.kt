package com.king.app.plate.page.player.page

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterScoreItemBinding

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/14 14:49
 */
class ScoreAdapter: BaseBindingAdapter<AdapterScoreItemBinding, ScoreItem>() {

    override fun onCreateBind(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AdapterScoreItemBinding = AdapterScoreItemBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterScoreItemBinding?, position: Int, bean: ScoreItem) {
        binding!!.bean = bean
    }
}