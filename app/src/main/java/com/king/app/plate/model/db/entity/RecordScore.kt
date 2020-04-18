package com.king.app.plate.model.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/20 16:47
 */
@Entity(tableName = "record_score")
data class RecordScore (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var recordId: Long,
    var set: Int,
    var score1: Int,
    var score2: Int,
    var isTiebreak: Boolean,
    var scoreTie1: Int,
    var scoreTie2: Int
)