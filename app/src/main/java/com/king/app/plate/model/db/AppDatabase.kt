package com.king.app.plate.model.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.king.app.plate.PlateApplication
import com.king.app.plate.model.db.dao.*
import com.king.app.plate.model.db.entity.*

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 9:43
 */
@Database(entities = [Match::class, Player::class, Rank::class, Score::class
    , Record::class, RecordPlayer::class, RecordScore::class], version = 2)
abstract class AppDatabase:RoomDatabase() {

    abstract fun getMatchDao(): MatchDao
    abstract fun getPlayerDao(): PlayerDao
    abstract fun getRecordDao(): RecordDao
    abstract fun getRecordPlayerDao(): RecordPlayerDao
    abstract fun getRecordScoreDao(): RecordScoreDao
    abstract fun getRankDao(): RankDao
    abstract fun getScoreDao(): ScoreDao

    companion object {
        val DATABASE_NAME = "plate.db"
        val DATABASE_JOURNAL = "plate.db-journal"

        var instance = Single.instance;

        fun getDbFolder(): String {
            return PlateApplication.instance.getCacheDir().getParent() + "/databases/"
        }

        fun getDbPath(): String {
            return "${getDbFolder()}$DATABASE_NAME"
        }
    }

    object Single {
        val instance:AppDatabase= Room.databaseBuilder(
            PlateApplication.instance,
            AppDatabase::class.java,
            DATABASE_NAME
        ).allowMainThreadQueries()
            .addMigrations(object :Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("alter table 'match' add column isRankCreated INTEGER NOT NULL default 0")
                    database.execSQL("alter table 'match' add column isScoreCreated INTEGER NOT NULL default 0")
                }
            })
            .build()
    }
}