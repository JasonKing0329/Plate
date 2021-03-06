package com.king.app.plate.page.player

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.R
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterPlayerBinding
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil
import com.king.app.plate.utils.RippleUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 16:06
 */
class PlayerAdapter: BaseBindingAdapter<AdapterPlayerBinding, PlayerItem>() {

    override fun onCreateBind(inflater: LayoutInflater, parent: ViewGroup): AdapterPlayerBinding =
        // 必须有后两个，否则宽度只能占recyclerview的一半
        AdapterPlayerBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterPlayerBinding?, position: Int, bean: PlayerItem) {
        binding!!.clGroup.background = RippleUtil.getRippleBackground(
            Color.WHITE
            , binding!!.clGroup.resources.getColor(R.color.ripple_color))
        binding!!.bean = bean

        var color = if (bean.bean!!.defColor == null) ColorUtils.randomWhiteTextBgColor()
        else bean.bean!!.defColor!!
        DrawableUtil.setGradientColor(binding!!.tvName, color)
    }
}