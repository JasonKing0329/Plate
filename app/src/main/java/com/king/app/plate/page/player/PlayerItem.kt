package com.king.app.plate.page.player

import com.king.app.plate.model.db.entity.Player

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 16:06
 */
data class PlayerItem (
    var bean: Player?,
    var currentDesc: String,
    var highDesc: String,
    var rank: Int
)