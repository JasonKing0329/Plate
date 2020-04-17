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
    var id: Int,
    var matchId: Int,
    var round: Int,
    var winnerId: Int?,
    var retireFlg: Int,
    var isBye: Boolean
)