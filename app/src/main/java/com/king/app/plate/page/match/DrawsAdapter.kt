package com.king.app.plate.page.match

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

    fun setData(data: DrawData) {
        this.data = data
    }

    override fun getText(x: Int, y: Int): String {
        try {
            var round = getRound(x)
            return if (round == null) {
                getNullRound(x / (set + 1))
            }
            else {
                when (x % (set + 1) == 0) {
                    true -> {// players
                        getPlayerName(round, y)
                    }
                    false -> {// score
                        getScore(round, x % (set + 1), y)
                    }
                }
            }
        } catch (e: Exception) {
        }
        return ""
    }

    private fun getNullRound(round: Int): String {
        return if (round + 1 == totalRound) getWinner() else ""
    }

    private fun getWinner(): String {
        var size = data?.roundList?.size!!
        return ""
    }

    private fun getRound(x: Int): DrawRound? = data?.roundList?.get(x / (set + 1))

    private fun getPlayerName(round: DrawRound, index: Int): String {
        var record = round.recordList?.get(index % 2)
        var player = if (index % 2 == 0) {
            record?.player1
        } else {
            record?.player2
        }

        return when (player?.playerSeed == null) {
            true -> player?.player?.name!!
            false -> "[".plus(player?.playerSeed).plus("]").plus(player?.player?.name!!)
        }
    }

    private fun getScore(round: DrawRound, set: Int, index: Int): String {
        var record = round.recordList?.get(index % 2)
        var score = record.scoreList?.get(set - 1)
        return when(score?.isTiebreak!!) {
            true -> when(index % 2 == 0) {
                true -> score?.score1!!.toString()?.plus("(")?.plus(score?.scoreTie1).plus(")")
                false -> score?.score2!!.toString()?.plus("(")?.plus(score?.scoreTie2).plus(")")
            }
            false -> when(index % 2 == 0) {
                true -> score?.score1!!.toString()
                false -> score?.score2!!.toString()
            }
        }
    }

    fun updateText(x: Int, y: Int, text: String?) {

    }
}