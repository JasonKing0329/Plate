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

    @Query("select * from `rank` where playerId =:playerId and matchId =:matchId")
    fun getPlayerRank(playerId: Int?, matchId: Int?): Rank
}