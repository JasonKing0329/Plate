package com.king.app.plate.model.repo

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.page.match.DrawRound
import io.reactivex.Observable
import kotlin.math.pow

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 17:07
 */
class RecordRepository: BaseRepository() {

    fun getRoundRecords(matchId: Long): Observable<List<DrawRound>> = Observable.create {
        var roundList = mutableListOf<DrawRound>()
        for (round in 0 until AppConstants.round) {
            var drawRound = DrawRound(round, mutableListOf())
            var num:Int = (AppConstants.draws / 2.toDouble().pow((round + 1).toDouble())).toInt()
            for (i in 0 until num) {
                var record = getDatabase().getRecordDao().getRecord(matchId, round, i)
                if (record == null) {
                    drawRound.recordList.add(RecordPack(record, mutableListOf(), mutableListOf()))
                }
                else {
                    var playerList = getDatabase().getRecordPlayerDao().getPlayersByRecord(record.id)
                    if (playerList.size > 0) {
                        for (player in  playerList) {
                            player.player = getDatabase().getPlayerDao().getPlayerById(player.playerId)
                        }
                    }
                    var scoreList = getDatabase().getRecordScoreDao().getScoresByRecord(record.id)
                    drawRound.recordList.add(RecordPack(record, playerList, scoreList))
                }
            }
            roundList.add(drawRound)
        }
        it.onNext(roundList)
        it.onComplete()
    }
}