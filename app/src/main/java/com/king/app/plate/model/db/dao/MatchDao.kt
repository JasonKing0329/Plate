package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.db.entity.Match

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:46
 */
@Dao
interface MatchDao:BaseDao<Match> {

    @Query("select * from `match` order by period desc, orderInPeriod desc")
    fun getMatches(): MutableList<Match>

    @Query("select * from `match` where id=:id")
    fun getMatchById(id: Long): Match

    @Query("select * from `match` where period=:period and orderInPeriod=:orderInPeriod")
    fun getMatchByOrder(period: Int, orderInPeriod: Int): Match

    @Query("select max(period) from `match` where period=:period")
    fun getMaxOrderInPeriod(period: Int): Int

    @Query("select * from `match` where isRankCreated=1 order by id desc")
    fun getRankMatches(): MutableList<Match>

    @Query("select * from `match` where isRankCreated=1 order by id desc limit 0,1")
    fun getLastRankMatch(): Match

    @Query("select * from `match` order by id desc limit 0,1")
    fun getLastMatch(): Match
}