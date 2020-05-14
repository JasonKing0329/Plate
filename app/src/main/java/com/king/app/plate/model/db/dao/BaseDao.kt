package com.king.app.plate.model.db.dao

import androidx.room.*

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:47
 */
@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: MutableList<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: T): Long

    @Delete
    fun delete(element: T)

    @Delete
    fun deleteList(elements:MutableList<T>)

    @Delete
    fun deleteSome(vararg elements:T)

    @Update
    fun update(element: T)

    @Update
    fun update(element: MutableList<T>)

}