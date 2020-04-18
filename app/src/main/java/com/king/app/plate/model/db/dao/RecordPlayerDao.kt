package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.db.entity.RecordPlayer

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:46
 */
@Dao
interface RecordPlayerDao:BaseDao<RecordPlayer> {

    @Query("select * from `record_player` where recordId=:recordId")
    fun getPlayersByRecord(recordId: Long): MutableList<RecordPlayer>

    @Query("delete from `record_player` where recordId=:recordId")
    fun deletePlayersByRecord(recordId: Long)

}