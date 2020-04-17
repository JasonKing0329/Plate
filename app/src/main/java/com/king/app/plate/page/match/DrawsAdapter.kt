package com.king.app.plate.page.match

import com.king.app.plate.model.bean.BodyData
import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.view.draw.AbsDrawAdapter

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 16:49
 */
class DrawsAdapter : AbsDrawAdapter() {

    private var data: DrawData? = null

    var set: Int = 0
    var totalRound: Int = 0

    fun setData(data: DrawData?) {
        this.data = data
    }

    override fun getPlayerText(round: Int, indexInRound: Int): String {
        try {
            var playerIndex = indexInRound % 2;
            var name = getRecordPack(round, indexInRound)?.playerList?.get(playerIndex)?.player?.name
            return name!!;
        } catch (e: Exception) {
        }
        return ""
    }

    private fun getRecordPack(round: Int, indexInDraw: Int): RecordPack? {
        var roundBean = data?.roundList?.get(round)
        var packIndex = indexInDraw / 2
        var pack = roundBean?.recordList?.get(packIndex)
        return pack
    }

    override fun getScoreText(round: Int, set: Int, indexInRound: Int): String {
        try {
            var pack = getRecordPack(round, indexInRound)
            var scoreIndex = indexInRound % 2;
            var recordScore = pack?.scoreList?.get(scoreIndex)!!
            var basicScore = if (scoreIndex == 0) {
                recordScore.score1!!
            } else {
                recordScore.score2!!
            }

            return if (recordScore.isTiebreak!!) {
                var tie = if (scoreIndex == 0) {
                    recordScore.scoreTie1!!
                } else {
                    recordScore.scoreTie2!!
                }
                "$basicScore($tie)"
            } else {
                basicScore.toString()
            }
        } catch (e: Exception) {
        }
        return ""
    }

    override fun getText(x: Int, y: Int): String {
        try {
            return data?.body?.bodyData?.get(x)?.get(y)?.text!!
        } catch (e: Exception) {
        }
        return ""
    }

    fun updateText(x: Int, y: Int, text: String?) {
        try {
            var colList: MutableList<BodyData> = data?.body?.bodyData!![x]
            colList[y].text = text!!
        } catch (e: Exception) {
        }
    }
}