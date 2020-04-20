package com.king.app.plate.model.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.king.app.plate.PlateApplication
import com.king.app.plate.model.db.dao.*
import com.king.app.plate.model.db.entity.*

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:43
 */
@Database(entities = [Match::class, Player::class, Rank::class, Score::class
    , Record::class, RecordPlayer::class, RecordScore::class], version = 1)
abstract class AppDatabase:RoomDatabase() {

    abstract fun getMatchDao(): MatchDao
    abstract fun getPlayerDao(): PlayerDao
    abstract fun getRecordDao(): RecordDao
    abstract fun getRecordPlayerDao(): RecordPlayerDao
    abstract fun getRecordScoreDao(): RecordScoreDao
    abstract fun getRankDao(): RankDao

    companion object {
        val DATABASE_NAME = "plate.db"
        val DATABASE_JOURNAL = "plate.db-journal"

        var instance = Single.instance;

        fun getDbPath(): String {
            return PlateApplication.instance.getCacheDir().getParent() + "/databases/" + DATABASE_NAME
        }
    }

    object Single {
        val instance:AppDatabase= Room.databaseBuilder(
            PlateApplication.instance,
            AppDatabase::class.java,
            DATABASE_NAME
        ).allowMainThreadQueries()
            .build()
    }
}