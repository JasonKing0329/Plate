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

    @Query("select * from `record_score` where recordId=:recordId order by `set` asc")
    fun getScoresByRecord(recordId: Long): MutableList<RecordScore>

    @Query("delete from record_score where recordId=:recordId")
    fun deleteByRecord(recordId: Long)

    @Query("delete from record_score where recordId=:recordId and `set`=:set")
    fun deleteByRecordAndSet(recordId: Long, set: Int)
}