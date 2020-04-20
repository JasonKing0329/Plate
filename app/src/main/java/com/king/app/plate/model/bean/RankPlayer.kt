package com.king.app.plate.model.bean

import com.king.app.plate.model.db.entity.Player

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/14 16:00
 */
data class RankPlayer (
    var player:Player?,
    var rank:Int,
    var score:Int
)