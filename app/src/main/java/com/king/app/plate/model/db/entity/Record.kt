package com.king.app.plate.model.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/20 16:47
 */
@Entity(tableName = "record")
data class Record (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var matchId: Long,
    var round: Int,
    var winnerId: Long?,
    var retireFlg: Int,
    var isBye: Boolean,
    var orderInRound: Int
)