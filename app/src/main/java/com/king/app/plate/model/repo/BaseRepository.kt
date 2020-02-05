package com.king.app.plate.model.repo

import com.king.app.plate.model.db.AppDatabase

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 19:52
 */
abstract class BaseRepository {

    fun getDatabase() = AppDatabase.instance
}