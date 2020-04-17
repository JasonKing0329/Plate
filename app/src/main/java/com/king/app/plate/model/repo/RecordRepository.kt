package com.king.app.plate.model.repo

import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.page.match.DrawRound
import io.reactivex.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 17:07
 */
class RecordRepository: BaseRepository() {

    fun getRoundRecords(matchId: Int): Observable<List<DrawRound>> = Observable.create {
        var roundList = mutableListOf<DrawRound>()
        var recordList = getDatabase().getRecordDao().getRecordsByMatch(matchId)
        var drawRound: DrawRound? = null
        for (record in recordList) {
            // recordList已按round排序
            if (drawRound == null || drawRound.round != record.round) {
                drawRound = DrawRound(record.round, mutableListOf())
                roundList.add(drawRound)
            }
            var playerList = getDatabase().getRecordPlayerDao().getPlayersByRecord(record.id)
            var scoreList = getDatabase().getRecordScoreDao().getScoresByRecord(record.id)
            var pack = RecordPack(record, playerList, scoreList)
            drawRound.recordList.add(pack)
        }
        it.onNext(roundList)
        it.onComplete()
    };
}