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

    @Query("select score from score where matchId=:matchId and playerId=:playerId")
    fun getScore(matchId: Long, playerId: Long): Int

    @Query("select * from score where playerId=:playerId order by score desc")
    fun getScoreList(playerId: Long): MutableList<Score>

    @Query("select s.* from score s join [match] m on s.matchId=m.id where s.playerId=:playerId and m.period=:period")
    fun getPeriodScoreList(playerId: Long, period: Int): MutableList<Score>

    @Query("select s.* from score s join [match] m on s.matchId=m.id where s.playerId=:playerId and m.[order]<=:orderMax and m.[order]>=:orderMin")
    fun getRankScoreList(playerId: Long, orderMin: Int, orderMax: Int): MutableList<Score>

    @Query("select sum(s.score) from score s join [match] m on s.matchId=m.id where s.playerId=:playerId and m.[order]<=:orderMax and m.[order]>=:orderMin")
    fun sumRankScore(playerId: Long, orderMin: Int, orderMax: Int): Int

}