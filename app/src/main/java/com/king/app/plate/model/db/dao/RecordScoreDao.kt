package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.db.entity.RecordScore

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:46
 */
@Dao
interface RecordScoreDao:BaseDao<RecordScore> {

    @Query("select * from `record_score` where recordId=:recordId")
    fun getScoresByRecord(recordId: Int): List<RecordScore>

}