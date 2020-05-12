package com.king.app.plate.page.match

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterFinalPlayerBinding
import com.king.app.plate.databinding.AdapterFinalScoreItemBinding
import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/11 11:05
 */
class FinalPlayerAdapter : BaseBindingAdapter<AdapterFinalPlayerBinding, RankPlayer>() {

    override fun onCreateBind(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AdapterFinalPlayerBinding = AdapterFinalPlayerBinding.inflate(inflater, parent, false)

    override fun onBindItem(
        binding: AdapterFinalPlayerBinding?,
        position: Int,
        bean: RankPlayer
    ) {
        binding!!.bean = bean

        var color = if (bean.player!!.defColor == null) ColorUtils.randomWhiteTextBgColor()
        else bean.player!!.defColor!!
        DrawableUtil.setGradientColor(binding!!.tvName, color)
    }
}