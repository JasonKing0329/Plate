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

    @Query("select * from `match`")
    fun getMatches(): List<Match>

    @Query("select * from `match` where id=:id")
    fun getMatchById(id: Long): Match
}