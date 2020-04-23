package com.king.app.plate.page.player.page

import com.king.app.plate.model.db.entity.Rank
import com.king.app.plate.view.widget.chart.adapter.LineData

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/23 15:04
 */
data class RankChartData (
    var ranks: MutableList<Rank>,
    var max: Int,
    var lineData: LineData,
    var xTexts: Array<String?>,
    var degreeY: Int = 2
)