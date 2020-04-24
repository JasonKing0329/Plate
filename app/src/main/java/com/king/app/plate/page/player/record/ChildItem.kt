package com.king.app.plate.page.player.record

import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.db.entity.Record

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/24 10:14
 */
class ChildItem (
    var record: Record,
    var player: Player,
    var playerColor: Int,
    var headerPosition: Int = 0,
    var itemPosition: Int = 0,
    var winner: String = "",
    var score: String = "",
    var round: String = "",
    var isWinner: Boolean = false
)