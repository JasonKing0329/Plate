package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.db.entity.Record

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:46
 */
@Dao
interface RecordDao:BaseDao<Record> {

    @Query("select * from `Record`")
    fun getRecords(): MutableList<Record>

    @Query("select * from `Record` where matchId=:matchId order by round, orderInRound")
    fun getRecordsByMatch(matchId: Long): MutableList<Record>

    @Query("select * from `record` where id=:id")
    fun getRecord(id: Long): Record

    @Query("select * from `record` where matchId=:matchId and round=:round and orderInRound=:orderInRound")
    fun getRecord(matchId: Long, round: Int, orderInRound: Int): Record

    @Query("delete from `record` where matchId=:matchId")
    fun deleteByMatch(matchId: Long)

    @Query("select r.* from record_player rp join record r on rp.recordId=r.id where rp.playerId=:player1Id and rp.recordId in (select recordId from record_player where playerId=:player2Id)")
    fun getH2hRecords(player1Id: Long, player2Id: Long): MutableList<Record>

}