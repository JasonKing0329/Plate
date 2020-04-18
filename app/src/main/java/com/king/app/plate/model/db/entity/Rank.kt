package com.king.app.plate.model.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/20 16:47
 */
@Entity(tableName = "rank")
data class Rank (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var matchId: Long,
    var playerId: Long,
    var rank: Int
)