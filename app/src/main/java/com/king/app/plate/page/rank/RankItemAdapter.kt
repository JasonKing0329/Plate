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

    var onRankItemListener: OnRankItemListener? = null

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
                binding!!.tvRankChange.text = "+${bean.change}"
                binding!!.tvRankChange.setTextColor(binding!!.tvRankChange.resources.getColor(R.color.red))
            }
            else -> {
                binding!!.tvRankChange.text = "0"
                binding!!.tvRankChange.setTextColor(binding!!.tvRankChange.resources.getColor(R.color.text_sub))
            }
        }
        when {
            bean.changeScore < 0 -> {
                binding!!.tvScoreChange.text = bean.changeScore.toString()
                binding!!.tvScoreChange.setTextColor(binding!!.tvScoreChange.resources.getColor(R.color.green34A350))
            }
            bean.changeScore > 0 -> {
                binding!!.tvScoreChange.text = "+${bean.changeScore}"
                binding!!.tvScoreChange.setTextColor(binding!!.tvScoreChange.resources.getColor(R.color.red))
            }
            else -> {
                binding!!.tvScoreChange.text = "0"
                binding!!.tvScoreChange.setTextColor(binding!!.tvScoreChange.resources.getColor(R.color.text_sub))
            }
        }
        var color = if (bean.player.defColor == null) ColorUtils.randomWhiteTextBgColor()
        else bean.player.defColor!!
        DrawableUtil.setGradientColor(binding!!.tvName, color)

        binding.tvScore.setOnClickListener { onRankItemListener?.onClickScore(bean) }
    }

    interface OnRankItemListener {
        fun onClickScore(bean: RankItem)
    }
}