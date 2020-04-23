package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.db.entity.Score

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/20 11:47
 */
@Dao
interface ScoreDao: BaseDao<Score> {

    @Query("delete from score where matchId=:matchId")
    fun deleteMatchScore(matchId: Long)

    @Query("select sum(score) from score where playerId=:playerId")
    fun sumScore(playerId: Long): Int

    @Query("select score from score where matchId=:matchId and playerId=:playerId")
    fun getScore(matchId: Long, playerId: Long): Int

    @Query("select sum(s.score) from score s join 'match' m on s.matchId = m.id and m.period=:period and m.orderInPeriod<=:orderInPeriod where s.playerId=:playerId")
    fun sumScoreUntilMatch(period:Int, orderInPeriod:Int, playerId: Long): Int

    @Query("select * from score where playerId=:playerId order by score desc")
    fun getScoreList(playerId: Long): MutableList<Score>

}