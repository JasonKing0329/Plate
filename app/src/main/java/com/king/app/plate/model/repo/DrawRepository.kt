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
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 19:41
 */
class DrawRepository: BaseRepository() {

    /**
     * 初始化按照32签5轮，RecordPack 16-8-4-2-1 逐个填空
     */
    fun createDrawBody(): Observable<DrawBody> = Observable.create {
        var drawBody = DrawBody()
        var data = mutableListOf<MutableList<BodyCell>>()
        var round = (ln(AppConstants.draws.toDouble()) / ln(2.0)).toInt()
        var maxRow = AppConstants.draws
        var recordPack: RecordPack? = null
        for (i in 0 until round) {
            for (j in 0 until (AppConstants.set + 1)) {
                var colList = mutableListOf<BodyCell>()
                data.add(colList)
                for (row in 0 until maxRow) {
                    // 每2行(set + 1)列为一个record，第(0,0)格创建RecordPack，其他格共享这一个RecordPack
                    recordPack = if (row % 2 == 0 && j == 0) {
                        RecordPack(null, mutableListOf(), mutableListOf())
                    } else{
                        if (row % 2 == 0) {
                            data[i * (AppConstants.set + 1)][row].pack
                        } else{
                            data[i * (AppConstants.set + 1)][row - 1].pack
                        }
                    }
                    var type = if (j == 0) AppConstants.cellTypePlayer else AppConstants.cellTypeScore
                    colList.add(newBodyData(row, type, recordPack))
                }
            }
            maxRow /= 2
        }
        // 加一列winner
        var winner = mutableListOf<BodyCell>()
        winner.add(newBodyData(0, AppConstants.cellTypePlayer, recordPack))
        data.add(winner)
        drawBody.bodyData = data

        it.onNext(drawBody)
        it.onComplete()
    }

    private fun newBodyData(raw: Int, type: Int, recordPack: RecordPack?): BodyCell {
        var data = BodyCell()
        data.row = raw
        data.text = ""
        data.type = type
        data.pack = recordPack
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
                var pack = RecordPack(null, mutableListOf(rp), mutableListOf())
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
                var pack = RecordPack(null, arrayListOf(rp1, rp2), mutableListOf())
                normalRecords.add(pack)
                unSeeds.removeAt(0)
                unSeeds.removeAt(0)
            }
            byeRecords.addAll(normalRecords)
            byeRecords.shuffle()
            for (i in 0 until byeRecords.size) {
                var pl = byeRecords?.get(i)?.playerList!!
                pl[0]?.order = 2 * i
                if (pl!!.size > 1) {
                    pl[1]?.order = 2 * i + 1
                }
            }

            var firstRound = DrawRound(0, byeRecords)
            list.add(firstRound)
            it.onNext(list)
            it.onComplete()
        }

    /**
     * 将轮次record数据转化为DrawBody
     */
    fun convertRoundsToBody(rounds: List<DrawRound>?, drawBody: DrawBody) {
        if (rounds != null) {
            var draw = AppConstants.draws
            var recordColumn = AppConstants.set + 1
            // 轮次
            for (i in rounds.indices) {
                var records = rounds[i].recordList
                var index = 0
                var recordIndex = 0
                // 每轮生产相应的record签表数据
                while (index < draw && recordIndex < records.size) {
                    var pack = records[recordIndex]
                    // 肯定有至少一个player
                    if (pack.playerList.size > 0) {
                        // player
                        var playerIndex = 0// 用于控制轮空格
                        var playerColumn = i * recordColumn
                        var recordPlayer = pack.playerList!![playerIndex]
                        // cell1-player1
                        var bodyCell = drawBody.bodyData!![playerColumn][index]
                        bodyCell.pack = pack
                        if (recordPlayer.order == index) {
                            bodyCell.text = getPlayerText(recordPlayer)
                            bodyCell.player = recordPlayer
                            playerIndex ++
                        }
                        index++

                        // cell2-player2
                        bodyCell = drawBody.bodyData!![playerColumn][index]
                        bodyCell.pack = pack
                        if (playerIndex < pack.playerList!!.size) {
                            recordPlayer = pack.playerList!![playerIndex]
                            if (recordPlayer.order == index) {
                                bodyCell.text = getPlayerText(recordPlayer)
                                bodyCell.player = recordPlayer
                            }
                        }
                        index++
                        // score
                        var scores = pack.scoreList
                        if (scores != null && scores.isNotEmpty()) {
                            for (score in scores) {
                                var set = score.set!!
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

    private fun getPlayerText(player: RecordPlayer): String {
        var result = ""
        if (player.player != null) {
            result = if (player.playerSeed!! > 0)
                "[${player.playerSeed}]${player.player!!.name}"
            else
                player.player!!.name!!
        }
        return result
    }

    private fun getScoreText(score: RecordScore, index: Int): String {
        return if (score.isTiebreak!!) {
            if (index == 0) {
                if (score.score1 == 7) "7"
                else "${score.score1}(${score.scoreTie1})"
            } else {
                if (score.score2 == 7) "7"
                else "${score.score2}(${score.scoreTie2})"
            }
        } else {
            if (index == 0) score.score1!!.toString()
            else score.score2!!.toString()
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
        var isScoreModified = false
        var recordPack = cells.bodyCells[0][0].pack
        // new
        if (recordPack!!.record == null) {
            // 如果两个player只要存在一个，就创建record
            var players = recordPack.playerList
            if (players.size > 0) {
                // insert record
                var record = Record(0, matchId, round, null, 0, false, orderInRound)
                if (players.size == 1) {
                    record.isBye = true
                    record.winnerId = players[0].id
                }
                else{
                    record.isBye = false
                }
                var recordId = getDatabase().getRecordDao().insert(record)
                record.id = recordId
                recordPack.record = record

                // insert players
                for (player in players) {
                    player.recordId = recordId
                }
                getDatabase().getRecordPlayerDao().insertAll(recordPack.playerList!!)

                // insert scores
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
                    isScoreModified = true
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

                // 没有player了，删掉所有record相关
                if (recordPack.playerList.size == 0) {
                    getDatabase().getRecordPlayerDao().deletePlayersByRecord(recordId)
                    getDatabase().getRecordScoreDao().deleteByRecord(recordId)
                    getDatabase().getRecordDao().delete(recordPack.record!!)
                    return
                }
                else{
                    for (rp in recordPack.playerList) {
                        rp.recordId = recordId
                    }
                    getDatabase().getRecordPlayerDao().insertAll(recordPack.playerList!!)
                }
                // 重新查询
                recordPack.playerList = getDatabase().getRecordPlayerDao().getPlayersByRecord(recordId)
                // player发生改变也会造成胜负关系改变
                isScoreModified = true
            }
            // score part
            // set 1
            if (cells.bodyCells[0][1].isModified || cells.bodyCells[1][1].isModified) {
                isScoreModified = true
                getDatabase().getRecordScoreDao().deleteByRecordAndSet(recordId, 1)
                var set1 = getRecordSet(recordId, 1, cells.bodyCells[0][1], cells.bodyCells[1][1])
                if (set1 != null) {
                    getDatabase().getRecordScoreDao().insert(set1)
                }
            }
            // set 2
            if (cells.bodyCells[0][2].isModified || cells.bodyCells[1][2].isModified) {
                isScoreModified = true
                getDatabase().getRecordScoreDao().deleteByRecordAndSet(recordId, 2)
                var set2 = getRecordSet(recordId, 2, cells.bodyCells[0][2], cells.bodyCells[1][2])
                if (set2 != null) {
                    getDatabase().getRecordScoreDao().insert(set2)
                }
            }
            // set 3
            if (cells.bodyCells[0][3].isModified || cells.bodyCells[1][3].isModified) {
                isScoreModified = true
                getDatabase().getRecordScoreDao().deleteByRecordAndSet(recordId, 3)
                var set3 = getRecordSet(recordId, 3, cells.bodyCells[0][3], cells.bodyCells[1][3])
                if (set3 != null) {
                    getDatabase().getRecordScoreDao().insert(set3)
                }
            }
        }
        if (isScoreModified) {
            // scoreList可能发生了增减，重新查询
            recordPack.scoreList = getDatabase().getRecordScoreDao().getScoresByRecord(recordPack.record!!.id)
            try {
                defineWinner(recordPack)
            } catch (e: Exception) {}
        }
    }

    private fun defineWinner(recordPack: RecordPack) {
        // 不够两盘，不可判胜负
        if (recordPack.scoreList.size < 2) {
            recordPack.record!!.winnerId = 0
            getDatabase().getRecordDao().update(recordPack.record!!)
            return
        }
        var win1 = 0
        var win2 = 0
        var set1Index = getSetWinIndex(recordPack.scoreList[0])
        if (set1Index == 1) {
            win1 ++
        }
        else if (set1Index == 2) {
            win2 ++
        }
        var set2Index = getSetWinIndex(recordPack.scoreList[1])
        if (set2Index == 1) {
            win1 ++
        }
        else if (set2Index == 2) {
            win2 ++
        }
        if (win1 == win2) {
            // 决胜盘
            var set3Index = getSetWinIndex(recordPack.scoreList[2])
            if (set3Index == 1) {
                win1 ++
            }
            else if (set3Index == 2) {
                win2 ++
            }
        }
        // 只有某一方获胜盘数达到2才算分出胜负
        if (win1 == 2 || win2 == 2) {
            // 修改winnerId
            if (win1 > win2) {
                recordPack.record!!.winnerId = recordPack.playerList[0].playerId
                getDatabase().getRecordDao().update(recordPack.record!!)
            }
            else {
                recordPack.record!!.winnerId = recordPack.playerList[1].playerId
                getDatabase().getRecordDao().update(recordPack.record!!)
            }
        }
        // 其他情况，胜负未分，置winnerId为0
        else{
            recordPack.record!!.winnerId = 0
            getDatabase().getRecordDao().update(recordPack.record!!)
        }
    }

    private fun getSetWinIndex(recordScore: RecordScore): Int {
        // 抢七
        if (recordScore.isTiebreak) {
            // 净胜分大于等于2，且最大分大于等于7，可判胜负
            if (abs(recordScore.scoreTie1 - recordScore.scoreTie2) >= 2 && max(recordScore.scoreTie1, recordScore.scoreTie2) >= 7) {
                return if (recordScore.scoreTie1 > recordScore.scoreTie2) 1 else 2
            }
        }
        // 非抢七
        else {
            // 净胜局大于等于2，且最大局数大于等于6，可判胜负
            if (abs(recordScore.score1 - recordScore.score2) >= 2 && max(recordScore.score1, recordScore.score2) >= 6) {
                return if (recordScore.score1 > recordScore.score2) 1 else 2
            }
        }
        return 0
    }

    private fun getRecordSet(recordId: Long, set: Int, setScore1: BodyCell, setScore2: BodyCell): RecordScore? {
        if (setScore1.text.isNotEmpty() || setScore2.text.isNotEmpty()) {
            var recordScore = RecordScore(0, recordId, set, 0, 0, false, 0, 0)
            if (setScore1.text.isNotEmpty()) {
                if (setScore1.text.contains("(")) {
                    recordScore.isTiebreak = true
                    recordScore.score1 = setScore1.text.substring(0, 1).toInt()
                    recordScore.scoreTie1 = setScore1.text.substring(setScore1.text.indexOf('(') + 1, setScore1.text.indexOf(')')).toInt()
                }
                else {
                    recordScore.score1 = setScore1.text.toInt()
                }
            }
            if (setScore2.text.isNotEmpty()) {
                if (setScore2.text.contains("(")) {
                    recordScore.isTiebreak = true
                    recordScore.score2 = setScore2.text.substring(0, 1).toInt()
                    recordScore.scoreTie2 = setScore2.text.substring(setScore2.text.indexOf('(') + 1, setScore2.text.indexOf(')')).toInt()
                }
                else {
                    recordScore.score2 = setScore2.text.toInt()
                }
            }
            // 7-6(2)这样的比分，7的一方scoreTie需要重算
            if (recordScore.isTiebreak) {
                if (recordScore.score1 == 7) recordScore.scoreTie1 = if (recordScore.scoreTie2 < 6) 7 else recordScore.scoreTie2 + 2
                else if (recordScore.score2 == 7) recordScore.scoreTie2 = if (recordScore.scoreTie1 < 6) 7 else recordScore.scoreTie1 + 2
            }
            return recordScore
        }
        return null
    }
}