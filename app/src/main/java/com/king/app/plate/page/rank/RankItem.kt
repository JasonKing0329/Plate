package com.king.app.plate.page.rank

import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.db.entity.Rank

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 9:10
 */
data class RankItem (
    var bean: Rank,
    var change: Int,
    var player: Player,
    var score: Int
)