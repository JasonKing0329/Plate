package com.king.app.plate.model.db.entity

import androidx.room.Entity
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
    var order: Int?
)