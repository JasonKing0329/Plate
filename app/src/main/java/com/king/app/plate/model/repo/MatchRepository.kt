package com.king.app.plate.model.repo

import com.king.app.plate.model.db.entity.Match

/**
 * @description:
 * @author：Jing
 * @date: 2020/4/18 19:50
 */
class MatchRepository: BaseRepository() {

    fun deleteMatch(bean: Match) {
        deleteMatchDetails(bean.id)
        getDatabase().getMatchDao().delete(bean)
    }

    fun deleteMatchDetails(matchId: Long) {
        var records = getDatabase().getRecordDao().getRecordsByMatch(matchId)
        for (record in records) {
            getDatabase().getRecordPlayerDao().deletePlayersByRecord(record.id)
            getDatabase().getRecordScoreDao().deleteByRecord(record.id)
        }
        getDatabase().getRecordDao().deleteByMatch(matchId)
    }
}