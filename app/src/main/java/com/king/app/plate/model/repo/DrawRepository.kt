package com.king.app.plate.model.repo

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.*
import com.king.app.plate.model.db.entity.Record
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.db.entity.RecordScore
import com.king.app.plate.page.match.DrawData
import com.king.app.plate.page.match.DrawRound
import io.reactivex.Observable
import io.reactivex.ObservableSource
import kotlin.math.ln

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 19:41
 */
class DrawRepository: BaseRepository() {

    fun createDrawBody(): Observable<DrawBody> = Observable.create {
        var drawBody = DrawBody()
        var data = mutableListOf<MutableList<BodyCell>>()
        var round = (ln(AppConstants.draws.toDouble()) / ln(2.0)).toInt()
        var row = AppConstants.draws
        for (i in 0 until round) {
            for (j in 0 until (AppConstants.set + 1)) {
                var colList = mutableListOf<BodyCell>()
                for (n in 0 until row) {
                    var type = if (j == 0) AppConstants.cellTypePlayer else AppConstants.cellTypeScore
                    colList.add(newBodyData(type))
                }
                data.add(colList)
            }
            row /= 2
        }
        // 加一列winner
        var winner = mutableListOf<BodyCell>()
        winner.add(newBodyData(AppConstants.cellTypePlayer))
        data.add(winner)
        drawBody.bodyData = data

        it.onNext(drawBody)
        it.onComplete()
    }

    private fun newBodyData(type: Int): BodyCell {
        var data = BodyCell()
        data.text = ""
        data.type = type
        return data
    }

    fun createPlayerDraw(players: List<RankPlayer>): ObservableSource<List<DrawRound>> =
        ObservableSource {
            var list = arrayListOf<DrawRound>()
            var seeds = arrayListOf<RankPlayer>()
            var unSeeds = arrayListOf<RankPlayer>()
            for (player in players) {
                if (player.rank != 0 && player.rank < AppConstants.bye) {
                    seeds.add(player)
                } else {
                    unSeeds.add(player)
                }
            }
            unSeeds.shuffle()
            var byeRecords = arrayListOf<RecordPack>()
            for (i in 0 until AppConstants.bye) {
                var player = unSeeds[i].player
                var rp = RecordPlayer(
                    0,
                    0,
                    player!!.id,
                    unSeeds[i].rank,
                    unSeeds[i].rank,
                    1,
                    player
                )
                var pack = RecordPack(null, mutableListOf(rp), null)
                byeRecords.add(pack)
            }
            for (i in AppConstants.bye - 1 downTo 0) {
                unSeeds.removeAt(i)
            }
            var normalRecords = arrayListOf<RecordPack>()
            while (unSeeds.size > 0) {
                var player1 = unSeeds[0].player
                var player2 = unSeeds[1].player
                var rp1 = RecordPlayer(
                    0,
                    0,
                    player1!!.id,
                    unSeeds[0].rank,
                    unSeeds[0].rank,
                    1,
                    player1
                )
                var rp2 = RecordPlayer(
                    0,
                    0,
                    player2!!.id,
                    unSeeds[1].rank,
                    unSeeds[1].rank,
                    2,
                    player2
                )
                var pack = RecordPack(null, arrayListOf(rp1, rp2), null)
                normalRecords.add(pack)
                unSeeds.removeAt(0)
                unSeeds.removeAt(0)
            }
            byeRecords.addAll(normalRecords)
            byeRecords.shuffle()
            for (i in 0 until byeRecords.size) {
                var pl = byeRecords?.get(i)?.playerList!!
                pl[0]?.order = 2 * i;
                if (pl!!.size > 1) {
                    pl[1]?.order = 2 * i + 1;
                }
            }

            var firstRound = DrawRound(0, byeRecords)
            list.add(firstRound)
            it.onNext(list)
            it.onComplete()
        }

    fun convertRoundsToBody(rounds: List<DrawRound>?, drawBody: DrawBody) {
        if (rounds != null) {
            var draw = AppConstants.draws
            var recordColumn = AppConstants.set + 1
            for (i in rounds.indices) {
                var records = rounds[i].recordList
                var index = 0
                var recordIndex = 0
                while (index < draw) {
                    var pack = records[recordIndex]
                    if (pack.playerList != null) {
                        // player
                        var playerColumn = i * recordColumn;
                        var recordPlayer = pack.playerList!![0]
                        if (recordPlayer != null && recordPlayer.order == index) {
                            var bodyCell = drawBody.bodyData!![playerColumn][index]
                            bodyCell.text = recordPlayer.player?.name!!
                            bodyCell.player = recordPlayer
                            bodyCell.pack = pack
                        }
                        index++
                        if (pack.playerList!!.size > 1) {
                            recordPlayer = pack.playerList!![1]
                            if (recordPlayer != null && recordPlayer.order == index) {
                                var bodyCell = drawBody.bodyData!![playerColumn][index]
                                bodyCell.text = recordPlayer.player?.name!!
                                bodyCell.player = recordPlayer
                                bodyCell.pack = pack
                            }
                        }
                        index++
                        // score
                        var scores = pack.scoreList
                        if (scores != null && scores.isNotEmpty()) {
                            for (score in scores) {
                                var set = score.set!!;
                                var bodyCell1 = drawBody.bodyData!![playerColumn + set][index - 2]
                                bodyCell1.text = getScoreText(score, 0)
                                bodyCell1.pack = pack
                                var bodyCell2 = drawBody.bodyData!![playerColumn + set][index - 1]
                                bodyCell2.text = getScoreText(score, 1)
                                bodyCell2.pack = pack
                            }
                        }
                    } else {
                        index += 2
                    }
                    recordIndex++
                }
            }
        }
    }

    private fun getScoreText(score: RecordScore, index: Int): String {
        return if (score.isTiebreak!!) {
            if (index == 0) {
                "${score.score1}(${score.scoreTie1})"
            } else {
                "${score.score2}(${score.scoreTie2})"
            }
        } else {
            if (index == 0) {
                score.score1!!.toString()
            } else {
                score.score2!!.toString()
            }
        }
    }

    /**
     * 目前只考虑3盘的情况(recordStep = 4)
     */
    fun saveDraw(drawData: DrawData): Observable<DrawData> = Observable.create {
        var recordStep = 4
        // 每4列，2行为一个record
        for (col in 0 until drawData.body.bodyData.size step recordStep) {
            var round = col / recordStep
            var colPlayer = drawData.body.bodyData[col]
            // champion只有player行
            if (col == drawData.body.bodyData.size - 1) {
                break
            }
            var colSet1 = drawData.body.bodyData[col + 1]
            var colSet2 = drawData.body.bodyData[col + 2]
            var colSet3 = drawData.body.bodyData[col + 3]
            for (row in 0 until colPlayer.size step 2) {
                var orderInRound = row / 2
                // 每4列，2行为一个record
                var recordCells = RecordCells()
                var line1Cells = mutableListOf<BodyCell>()
                line1Cells.add(colPlayer[row])
                line1Cells.add(colSet1[row])
                line1Cells.add(colSet2[row])
                line1Cells.add(colSet3[row])
                var line2Cells = mutableListOf<BodyCell>()
                line2Cells.add(colPlayer[row + 1])
                line2Cells.add(colSet1[row + 1])
                line2Cells.add(colSet2[row + 1])
                line2Cells.add(colSet3[row + 1])
                recordCells.bodyCells.add(line1Cells)
                recordCells.bodyCells.add(line2Cells)
                updateRecordCellsData(drawData.match!!.id, round, orderInRound, recordCells)
            }
        }
        it.onNext(drawData)
        it.onComplete()
    }

    /**
     * 每4列2行为一个Record记录
     */
    private fun updateRecordCellsData(matchId: Long, round: Int, orderInRound: Int, cells: RecordCells) {
        var recordPack = cells.bodyCells[0][0].pack
        // new
        if (recordPack.record == null) {
            // 如果两个player只要存在一个，就创建record
            var players = recordPack.playerList
            if (players != null) {
                var record = Record(0, matchId, round, null, 0, false, orderInRound)
                if (players.size == 1) {
                    record.isBye = true
                    record.winnerId = players[0].id
                }
                else{
                    record.isBye = false
                }
                var recordId = getDatabase().getRecordDao().insert(record)
                for (player in players) {
                    player.recordId = recordId
                }
                getDatabase().getRecordPlayerDao().insertAll(recordPack.playerList!!)

                var set1 = getRecordSet(recordId, 1, cells.bodyCells[0][1], cells.bodyCells[1][1])
                var set2 = getRecordSet(recordId, 2, cells.bodyCells[0][2], cells.bodyCells[1][2])
                var set3 = getRecordSet(recordId, 3, cells.bodyCells[0][3], cells.bodyCells[1][3])
                recordPack.scoreList = mutableListOf()
                if (set1 != null) {
                    recordPack.scoreList!!.add(set1)
                }
                if (set2 != null) {
                    recordPack.scoreList!!.add(set2)
                }
                if (set3 != null) {
                    recordPack.scoreList!!.add(set3)
                }
                if (recordPack.scoreList!!.size > 0) {
                    getDatabase().getRecordScoreDao().insertAll(recordPack.scoreList!!)
                }
            }
        }
        // update
        else {
            var recordId = recordPack.record!!.id
            // player part
            if (cells.bodyCells[0][0].isModified || cells.bodyCells[1][0].isModified) {
                getDatabase().getRecordPlayerDao().deletePlayersByRecord(recordId)
                if (recordPack.playerList != null) {
                    getDatabase().getRecordPlayerDao().insertAll(recordPack.playerList!!)
                }
            }
            // score part
            // set 1
            if (cells.bodyCells[0][1].isModified || cells.bodyCells[1][1].isModified) {
                getDatabase().getRecordScoreDao().deleteByRecordAndSet(recordId, 1)
                var set1 = getRecordSet(recordId, 1, cells.bodyCells[0][1], cells.bodyCells[1][1])
                if (set1 != null) {
                    getDatabase().getRecordScoreDao().insert(set1)
                }
            }
            // set 2
            if (cells.bodyCells[0][2].isModified || cells.bodyCells[1][2].isModified) {
                getDatabase().getRecordScoreDao().deleteByRecordAndSet(recordId, 2)
                var set2 = getRecordSet(recordId, 2, cells.bodyCells[0][2], cells.bodyCells[1][2])
                if (set2 != null) {
                    getDatabase().getRecordScoreDao().insert(set2)
                }
            }
            // set 3
            if (cells.bodyCells[0][3].isModified || cells.bodyCells[1][3].isModified) {
                getDatabase().getRecordScoreDao().deleteByRecordAndSet(recordId, 3)
                var set3 = getRecordSet(recordId, 3, cells.bodyCells[0][3], cells.bodyCells[1][3])
                if (set3 != null) {
                    getDatabase().getRecordScoreDao().insert(set3)
                }
            }
        }
    }

    private fun getRecordSet(recordId: Long, set: Int, setScore1: BodyCell, setScore2: BodyCell): RecordScore? {
        if (setScore1.text.isNotEmpty() || setScore2.text.isNotEmpty()) {
            var recordScore = RecordScore(0, recordId, set, 0, 0, false, 0, 0)
            if (setScore1.text.isNotEmpty()) {
                if (setScore1.text.contains("(")) {
                    recordScore.isTiebreak = true
                    recordScore.score1 = setScore1.text[0].toInt()
                    recordScore.scoreTie1 = setScore1.text.substring(setScore1.text.indexOf('(') + 1, setScore1.text.indexOf(')')).toInt()
                }
                else {
                    recordScore.score1 = setScore1.text.toInt()
                }
            }
            if (setScore2.text.isNotEmpty()) {
                if (setScore2.text.contains("(")) {
                    recordScore.isTiebreak = true
                    recordScore.score2 = setScore2.text[0].toInt()
                    recordScore.scoreTie2 = setScore2.text.substring(setScore2.text.indexOf('(') + 1, setScore2.text.indexOf(')')).toInt()
                }
                else {
                    recordScore.score2 = setScore2.text.toInt()
                }
            }
            return recordScore
        }
        return null
    }

    private fun deleteScores(recordId: Long) {
        getDatabase().getRecordScoreDao().deleteByRecord(recordId)
    }
}