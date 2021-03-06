package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.db.entity.Rank

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:46
 */
@Dao
interface RankDao:BaseDao<Rank> {

    @Query("select * from `rank` where matchId=:matchId order by matchId desc, rank asc")
    fun getMatchRanks(matchId: Long): MutableList<Rank>

    @Query("select * from `rank` where playerId=:playerId")
    fun getPlayerRanks(playerId: Long): MutableList<Rank>

    @Query("select * from `rank` where playerId =:playerId and matchId =:matchId")
    fun getPlayerRank(playerId: Long, matchId: Long): Rank

    @Query("select min(rank) from `rank` where playerId =:playerId")
    fun getPlayerHighRank(playerId: Long): Int

    @Query("select count(rank) from `rank` where playerId =:playerId and rank=:rank")
    fun countRankWeek(playerId: Long, rank: Int): Int

    @Query("select count(rank) from `rank` where playerId =:playerId and rank>=:rankHigh and rank<=:rankLow")
    fun countRankRegionWeek(playerId: Long, rankLow: Int, rankHigh: Int): Int

    @Query("select max(rank) from `rank` where playerId =:playerId")
    fun getPlayerLowRank(playerId: Long): Int

    @Query("delete from rank where matchId=:matchId")
    fun deleteMatchRank(matchId: Long);

    @Query("select matchId from `rank` where rank =:rank and playerId=:playerId limit 0, 1")
    fun getRankFirstMatch(rank: Int, playerId: Long): Long
}