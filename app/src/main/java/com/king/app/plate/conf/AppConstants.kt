package com.king.app.plate.conf

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/14 15:53
 */
class AppConstants {
    companion object {
        var draws = 32
        var set = 3
        var bye = 8
        var round = 5

        var cellTypePlayer = 0
        var cellTypeScore = 1

        var matchLevelNormal = 0
        var matchLevelFinal = 1

        var playerSortName = 0
        var playerSortRank = 1

        /**
         * 0: no retire
         * 1: retire with score
         * 2: retire before match(W/0)
         */
        const val RETIRE_NONE = 0
        const val RETIRE_WITH_SCORE = 1
        const val RETIRE_WO = 2

        const val SCORE_RETIRE = "W/O"
        const val SCORE_RETIRE_NORMAL = "(对手退赛)"

        val ROUND_NORMAL = arrayOf("R32", "R16", "QF", "SF", "Final")

        val MATCH_LEVEL = arrayOf("Normal", "Final")
    }
}