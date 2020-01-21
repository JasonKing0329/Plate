package com.king.app.plate.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.king.app.plate.model.db.entity.Player

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:46
 */
@Dao
interface PlayerDao:BaseDao<Player> {

    @Query("select * from `player`")
    fun getPlayers(): List<Player>

    @Query("select * from `player` where id=:id")
    fun getPlayerById(id: Int): Player
}