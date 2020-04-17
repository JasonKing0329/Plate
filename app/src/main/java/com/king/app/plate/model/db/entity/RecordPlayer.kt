package com.king.app.plate.model.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/20 16:47
 */
@Entity(tableName = "record_player")
data class RecordPlayer (
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var recordId: Int?,
    var playerId: Int?,
    var playerRank: Int?,
    var playerSeed: Int?,
    var order: Int?,

    @Ignore
    var player: Player?
) {// 加入@Ignore后必须指定一个默认构造器
    constructor() : this(null, null, null, null, null, null, null)
}