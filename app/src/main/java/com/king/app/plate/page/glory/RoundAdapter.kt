package com.king.app.plate.page.glory

import android.view.LayoutInflater
import android.view.ViewGroup
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterGloryRoundBinding
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/9/27 16:47
 */
class RoundAdapter: BaseBindingAdapter<AdapterGloryRoundBinding, RoundItem>() {

    override fun onCreateBind(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AdapterGloryRoundBinding = AdapterGloryRoundBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterGloryRoundBinding?, position: Int, bean: RoundItem) {
        binding!!.bean = bean

        var color = if (bean.player.defColor == null) ColorUtils.randomWhiteTextBgColor()
        else bean.player.defColor!!
        DrawableUtil.setGradientColor(binding!!.tvPlayer, color)
    }

    fun sortByChampion() {
        list?.let {
            // sortedBy返回新引用，sortBy就地排序必须是mutableList
            list = it.sortedByDescending { item -> item.champion }
            notifyDataSetChanged()
        }
    }

    fun sortByRunnerUp() {
        list?.let {
            list = it.sortedByDescending { item -> item.runnerUp }
            notifyDataSetChanged()
        }
    }

    fun sortBySf() {
        list?.let {
            list = it.sortedByDescending { item -> item.sf }
            notifyDataSetChanged()
        }
    }

    fun sortByQf() {
        list?.let {
            list = it.sortedByDescending { item -> item.qf }
            notifyDataSetChanged()
        }
    }

    fun sortByR16() {
        list?.let {
            list = it.sortedByDescending { item -> item.r16 }
            notifyDataSetChanged()
        }
    }

    fun sortByR32() {
        list?.let {
            list = it.sortedByDescending { item -> item.r32 }
            notifyDataSetChanged()
        }
    }
}

data class RoundItem(
    var player: Player,
    var champion: Int = 0,
    var runnerUp: Int = 0,
    var sf: Int = 0,
    var qf: Int = 0,
    var r16: Int = 0,
    var r32: Int = 0
)