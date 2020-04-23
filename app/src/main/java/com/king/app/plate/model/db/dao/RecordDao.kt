package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.bean.H2hResult
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

    @Query("select record.* from record_player join record on record_player.recordId=record.id where record_player.playerId=:playerId")
    fun getPlayerRecords(playerId: Long): MutableList<Record>

    @Query("delete from `record` where matchId=:matchId")
    fun deleteByMatch(matchId: Long)

    /**
     * 查询两个player所有交手record
     */
    @Query("select r.* from record_player rp " +
            "join record r on rp.recordId=r.id " +
            "where rp.playerId=:player1Id and r.winnerId!=0 and rp.recordId in (select recordId from record_player where playerId=:player2Id)")
    fun getH2hRecords(player1Id: Long, player2Id: Long): MutableList<Record>

    /**
     * 查询两个player之间的交手记录
     */
    @Query("select sum(case  when r.winnerId=:player1Id then 1 else 0 end) as player1Win, sum(case  when r.winnerId=:player2Id then 1 else 0 end) as player2Win " +
            "from record_player rp " +
            "join record r on rp.recordId=r.id " +
            "where rp.recordId in (select recordId from record_player where playerId=:player1Id) " +
            "and rp.playerId=:player2Id")
    fun getH2hResult(player1Id: Long, player2Id: Long): H2hResult

    /**
     * 查询最后一条不为轮空的记录
     */
    @Query("select * from `record` where isBye=0 order by id desc limit 0, 1")
    fun getLastRecordNotBye(): Record

    /**
     * 查询playerId交手过的player数量
     */
    @Query("select count(*) from " +
            "(select * from record_player " +
            "where recordId in (select recordId from record_player where playerId=:playerId) and playerId!=:playerId " +
            "group by playerId)")
    fun getPlayerCountByH2h(playerId: Long): Int

    /**
     * 查询playerId所有不论空且已完赛的record数量
     */
    @Query("select count(*) from record_player " +
            "join record on record_player.recordId = record.id and record.isBye!=1 and record.winnerId!=0 " +
            "where record_player.playerId =:playerId")
    fun getPlayerRecordCount(playerId: Long): Int

}