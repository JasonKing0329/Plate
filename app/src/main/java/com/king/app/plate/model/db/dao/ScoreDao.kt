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

    @Query("delete from score")
    fun deleteAll()

    @Query("select sum(score) from score where playerId=:playerId")
    fun sumScore(playerId: Long): Int
}