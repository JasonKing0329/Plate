package com.king.app.plate.page.match

import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.model.db.entity.Player

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/11 11:02
 */
data class FinalPlayerScore (
    var player: Player,
    var playerText: String = "",
    var rank: Int = 0,
    var seed: Int = 0,
    var matchText: String = "",
    var setText: String = "",
    var matchWin: Int = 0,
    var matchLose: Int = 0,
    var setWin: Int = 0,
    var setLose: Int = 0,
    var setRate: Float = 0f,
    var gameWin: Int = 0,
    var gameLose: Int = 0,
    var gameRate: Float = 0f,
    var recordPacks: MutableList<RecordPack> = mutableListOf()
)