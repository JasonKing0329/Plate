package com.king.app.plate.model.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/20 16:47
 */
@Entity(tableName = "match")
data class Match (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var period: Int?,
    var order: Int,
    var orderInPeriod: Int?,
    var date: String?,
    var name: String?,
    var level: Int,
    var draws: Int,
    var byeDraws: Int,
    var qualifyDraws: Int
)