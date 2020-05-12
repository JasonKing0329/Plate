package com.king.app.plate.model.repo

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.model.db.entity.Record
import com.king.app.plate.page.match.DrawRound
import io.reactivex.rxjava3.core.Observable
import kotlin.math.pow

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 17:07
 */
class RecordRepository: BaseRepository() {

    fun getFinalGroupRoundRecords(matchId: Long, groupFlag: Int): Observable<List<DrawRound>> =
        getRoundRecords(matchId, 0, 1, AppConstants.draws_final_group, groupFlag)

    fun getFinalRestRoundRecords(matchId: Long): Observable<List<DrawRound>> =
        // 签位对应轮次，SF开头，但是draws应该取32
        getRoundRecords(matchId, AppConstants.ROUND_SF, AppConstants.round_final_rest, AppConstants.draws,  null)

    fun getRoundRecords(matchId: Long): Observable<List<DrawRound>> =
        getRoundRecords(matchId, 0, AppConstants.round, AppConstants.draws, null)

    /**
     * @draws 签位要对应轮次，比如以SF开头的，startRound是3，则应该按照32签位来计算
     */
    private fun getRoundRecords(matchId: Long, startRound: Int, totalRound: Int, draws: Int, groupFlag: Int?): Observable<List<DrawRound>> = Observable.create {
        var roundList = mutableListOf<DrawRound>()
        for (round in startRound until (startRound + totalRound)) {
            var drawRound = DrawRound(round, mutableListOf())
            var num:Int = (draws / 2.toDouble().pow((round + 1).toDouble())).toInt()
            for (i in 0 until num) {
                var record = if (groupFlag == null)
                    getDatabase().getRecordDao().getRecord(matchId, round, i)
                else
                    getDatabase().getRecordDao().getRecord(matchId, round, i, groupFlag!!)

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

    fun getPlayerResultRecords(playerId: Long): MutableList<Record> {
        var list = getDatabase().getRecordDao().getPlayerRecords(playerId)
        var result = mutableListOf<Record>()
        for (record in list) {
            // not complete
            if (record.winnerId == 0.toLong()) {
                continue
            }
            // final
            if (record.winnerId == playerId && record.round == AppConstants.round - 1) {
                result.add(record)
            }
            // before final
            else if (record.winnerId != playerId) {
                result.add(record)
            }
        }
        return result
    }
}