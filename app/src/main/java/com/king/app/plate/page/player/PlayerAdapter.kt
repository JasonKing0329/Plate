package com.king.app.plate.page.player

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterPlayerBinding
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 16:06
 */
class PlayerAdapter: BaseBindingAdapter<AdapterPlayerBinding, PlayerItem>() {

    private var colorMap: MutableMap<Int?, Int?> = mutableMapOf()

    override fun onCreateBind(inflater: LayoutInflater, parent: ViewGroup): AdapterPlayerBinding =
        // 必须有后两个，否则宽度只能占recyclerview的一半
        AdapterPlayerBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterPlayerBinding?, position: Int, bean: PlayerItem) {
        binding!!.bean = bean

        var key = bean?.bean?.id
        var color = colorMap[key]
        if (color == null) {
            color = ColorUtils.randomWhiteTextBgColor()
            colorMap[key] = color
        }
        DrawableUtil.setGradientColor(binding!!.tvName, color)
    }
}