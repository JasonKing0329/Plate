package com.king.app.plate.model.repo

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.BodyData
import com.king.app.plate.model.bean.DrawBody
import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.model.bean.RecordPack
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

    fun createDrawBody(): Observable<DrawBody> = Observable.create() {
        var drawBody = DrawBody()
        var data = mutableListOf<MutableList<BodyData>>()
        var round = (ln(AppConstants.draws.toDouble()) / ln(2.0)).toInt()
        var row = AppConstants.draws
        for (i in 0 until round) {
            for (j in 0 until (AppConstants.set + 1)) {
                var colList = mutableListOf<BodyData>()
                for (n in 0 until row) {
                    var type = if (j == 0) AppConstants.cellTypePlayer else AppConstants.cellTypeScore
                    colList.add(newBodyData(type))
                }
                data.add(colList)
            }
            row /= 2
        }
        // 加一列winner
        var winner = mutableListOf<BodyData>()
        winner.add(newBodyData(AppConstants.cellTypePlayer))
        data.add(winner)
        drawBody.bodyData = data

        it.onNext(drawBody)
        it.onComplete()
    }

    private fun newBodyData(type: Int): BodyData {
        var data = BodyData()
        data.text = ""
        data.type = type;
        return data
    }

    fun createPlayerDraw(players: List<RankPlayer>): ObservableSource<List<DrawRound>> =
        ObservableSource<List<DrawRound>>() {
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
                    null,
                    null,
                    player?.id,
                    unSeeds[i].rank,
                    unSeeds[i].rank,
                    1,
                    player
                )
                var pack = RecordPack(null, arrayListOf(rp), null)
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
                    null,
                    null,
                    player1?.id,
                    unSeeds[0].rank,
                    unSeeds[0].rank,
                    1,
                    player1
                )
                var rp2 = RecordPlayer(
                    null,
                    null,
                    player2?.id,
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
                            drawBody.bodyData!![playerColumn][index].text = recordPlayer.player?.name!!
                        }
                        index++
                        if (pack.playerList!!.size > 1) {
                            recordPlayer = pack.playerList!![1]
                            if (recordPlayer != null && recordPlayer.order == index) {
                                drawBody.bodyData!![playerColumn][index].text = recordPlayer.player?.name!!
                            }
                        }
                        index++
                        // score
                        var scores = pack.scoreList
                        if (scores != null && scores.isNotEmpty()) {
                            for (score in scores) {
                                var set = score.set!!;
                                drawBody.bodyData!![playerColumn + set][index - 1].text = getScoreText(score, 0)
                                drawBody.bodyData!![playerColumn + set][index].text = getScoreText(score, 1)
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
    fun saveDraw(drawData: DrawData): Observable<DrawData> = Observable.create() {
        var recordStep = 4;
        // 每4列，2行为一个record
        for (col in 0 until drawData.body.bodyData.size step recordStep) {
            var colPlayer = drawData.body.bodyData[col]
            var colSet1 = drawData.body.bodyData[col + 1]
            var colSet2 = drawData.body.bodyData[col + 2]
            var colSet3 = drawData.body.bodyData[col + 3]
            for (row in 0 until colPlayer.size step 2) {
                var body1 = colPlayer[row]
                var body2 = colPlayer[row + 1]
                var recordPack = body1.pack
                if (recordPack.record == null) {

                }
            }
        }
        it.onNext(drawData)
        it.onComplete()
    }

    private fun deleteScores(recordId: Int) {
        getDatabase().getRecordScoreDao().deleteByRecord(recordId);
    }
}