package com.king.app.plate.page.h2h.list

import com.king.app.plate.model.db.entity.Player

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/6/28 14:08
 */
data class H2hListItem (
    var player1: Player,
    var player2: Player,
    var score1: Int,
    var score2: Int,
    var detail: String?
)