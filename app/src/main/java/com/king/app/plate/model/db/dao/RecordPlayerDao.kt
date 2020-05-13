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

    @Query("select * from `record_player` where recordId=:recordId and playerId=:playerId")
    fun getRecordPlayer(recordId: Long, playerId: Long): RecordPlayer

    @Query("select * from `record_player` where recordId=:recordId order by `order` asc")
    fun getPlayersByRecord(recordId: Long): MutableList<RecordPlayer>

    @Query("select rp.* from record r join record_player rp on r.id=rp.recordId where r.matchId=:matchId and r.groupFlag=:groupFlg group by playerId")
    fun getMatchGroupPlayers(matchId: Long, groupFlg: Int): MutableList<RecordPlayer>

    @Query("delete from `record_player` where recordId=:recordId")
    fun deletePlayersByRecord(recordId: Long)

}