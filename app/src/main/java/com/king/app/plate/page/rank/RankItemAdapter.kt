package com.king.app.plate.page.rank

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.R
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterRankItemBinding
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 9:13
 */
class RankItemAdapter: BaseBindingAdapter<AdapterRankItemBinding, RankItem>() {

    private var colorMap: MutableMap<Long?, Int?> = mutableMapOf()

    override fun onCreateBind(inflater: LayoutInflater, parent: ViewGroup): AdapterRankItemBinding =
        AdapterRankItemBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterRankItemBinding?, position: Int, bean: RankItem) {
        binding!!.bean = bean
        when {
            bean.change < 0 -> {
                binding!!.tvRankChange.text = bean.change.toString()
                binding!!.tvRankChange.setTextColor(binding!!.tvRankChange.resources.getColor(R.color.green34A350))
            }
            bean.change > 0 -> {
                binding!!.tvRankChange.text = "+${bean.change.toString()}"
                binding!!.tvRankChange.setTextColor(binding!!.tvRankChange.resources.getColor(R.color.red))
            }
            else -> {
                binding!!.tvRankChange.text = "0"
                binding!!.tvRankChange.setTextColor(binding!!.tvRankChange.resources.getColor(R.color.text_sub))
            }
        }
        var key = bean.player.id
        var color = colorMap[key]
        if (color == null) {
            color = ColorUtils.randomWhiteTextBgColor()
            colorMap[key] = color
        }
        DrawableUtil.setGradientColor(binding!!.tvName, color)
    }
}