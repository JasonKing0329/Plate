package com.king.app.plate.model.detail

import com.king.app.plate.model.bean.BodyCell
import com.king.app.plate.model.bean.DrawBody
import com.king.app.plate.model.bean.FinalGroupPlayers
import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.page.match.DrawData
import com.king.app.plate.page.match.DrawRound
import com.king.app.plate.page.match.FinalPlayerScore
import io.reactivex.rxjava3.core.Observable
import java.lang.Exception
import java.util.*
import kotlin.math.abs

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 16:05
 */
class DrawModel {

    private val seedCell = arrayOf(0, 7, 8, 15, 16, 23, 24, 31)
    private val byeCell = arrayOf(1, 6, 9, 14, 17, 22, 25, 30)

    /**
     * 要求players size必须满足draws-bye
     * @param drawData
     * @param players
     */
    fun randomNormalDraw(drawData: DrawData, players: MutableList<RankPlayer>): Observable<DrawData> = Observable.create {
        players.sortBy { it -> it.rank }
        var isPlayerHasRank = players[0].rank > 0
        // 第0列为要填充player的list
        var cellList = drawData.body.bodyData[0]
        if (isPlayerHasRank) {
            fillCellWithSeed(cellList, players)
        }
        else{
            fillCellWholeRandom(cellList, players)
        }
        it.onNext(drawData)
        it.onComplete()
    }

    private fun fillCellWholeRandom(cellList: MutableList<BodyCell>, players: MutableList<RankPlayer>) {
        players.shuffle()
        var pIndex = 0
        for (cell in cellList) {
            if (!isByeCell(cell.row)) {
                fillCellPlayer(cell, players[pIndex].player!!, 0)
                pIndex ++;
            }
        }
    }

    private fun fillCellWithSeed(cellList: MutableList<BodyCell>, players: MutableList<RankPlayer>) {
        // 3,4号种子
        var list34 = mutableListOf<RankPlayer>()
        list34.addAll(players.subList(2, 4))
        list34.shuffle()
        // 5-8号种子
        var list5to8 = mutableListOf<RankPlayer>()
        list5to8.addAll(players.subList(4, 8))
        list5to8.shuffle()
        // 非种子
        var listUnSeed = mutableListOf<RankPlayer>()
        listUnSeed.addAll(players.subList(8, players.size))
        listUnSeed.shuffle()
        for (cell in cellList) {
            if (isByeCell(cell.row)) {
                continue
            }
            when(cell.row) {
                // 1号种子
                0 -> fillCellPlayer(cell, players[0].player!!, players[0].rank)
                // 2号种子
                31 -> fillCellPlayer(cell, players[1].player!!, players[1].rank)
                // 3, 4号种子
                15, 16 -> {
                    fillCellPlayer(cell, list34[0].player!!, list34[0].rank)
                    list34.removeAt(0)
                }
                // 5-8号种子
                7, 8, 23, 24 -> {
                    fillCellPlayer(cell, list5to8[0].player!!, list5to8[0].rank)
                    list5to8.removeAt(0)
                }
                // 非种子
                else -> {
                    fillCellPlayer(cell, listUnSeed[0].player!!, 0)
                    listUnSeed.removeAt(0)
                }
            }
        }
    }

    fun fillCellPlayer(cell: BodyCell, player: Player, seed: Int) {
        var recordPlayer = RecordPlayer(0 ,0 ,player.id, seed ,seed ,cell.row , player)
        var recordPack = cell.pack
        // delete current
        recordPack!!.playerList.remove(cell.player)
        recordPack!!.playerList.add(recordPlayer)
        cell.player = recordPlayer
        cell.text = player.name!!
        cell.isModified = true
        if (seed > 0) {
            cell.text = "[$seed]${recordPlayer.player!!.name!!}"
        }
        else{
            cell.text = recordPlayer.player!!.name!!
        }
    }

    private fun isSeedCell(index: Int): Boolean {
        for (n in seedCell) {
            if (index == n) {
                return true
            }
        }
        return false
    }

    private fun isByeCell(index: Int): Boolean {
        for (n in byeCell) {
            if (index == n) {
                return true
            }
        }
        return false
    }

    fun createFinalGroupPlayers(list: MutableList<RankPlayer>): Observable<FinalGroupPlayers> = Observable.create {
        if (list.size == 8) {
            // 遵循12,34,56,78分别不在一个组
            list.sortBy { bean -> bean.rank }
            var redList = mutableListOf<RankPlayer>()
            var blueList = mutableListOf<RankPlayer>()
            redList.add(list[0])
            blueList.add(list[1])
            var random = Random()
            if (abs(random.nextInt()) % 2 == 0) {
                redList.add(list[2])
                blueList.add(list[3])
            }
            else {
                redList.add(list[3])
                blueList.add(list[2])
            }
            if (abs(random.nextInt()) % 2 == 0) {
                redList.add(list[4])
                blueList.add(list[5])
            }
            else {
                redList.add(list[5])
                blueList.add(list[4])
            }
            if (abs(random.nextInt()) % 2 == 0) {
                redList.add(list[6])
                blueList.add(list[7])
            }
            else {
                redList.add(list[7])
                blueList.add(list[6])
            }
            it.onNext(FinalGroupPlayers(redList, blueList))
            it.onComplete()
        }
        else {
            it.onError(Exception("Player is not enough"))
        }
    }

    /**
     * 要求players size必须满足draws-bye
     * @param drawData
     * @param players
     */
    fun createFinalGroupDraw(drawData: DrawData, players: MutableList<RankPlayer>) {
        // 第0列为要填充player的list
        var cellList = drawData.body.bodyData[0]
        players.shuffle()
        // shuffle后按照01-23,02-13,03-12的顺序
        fillCellPlayer(cellList[0], players[0].player!!, players[0].rank)
        fillCellPlayer(cellList[1], players[1].player!!, players[1].rank)
        fillCellPlayer(cellList[2], players[2].player!!, players[2].rank)
        fillCellPlayer(cellList[3], players[3].player!!, players[3].rank)
        fillCellPlayer(cellList[4], players[0].player!!, players[0].rank)
        fillCellPlayer(cellList[5], players[2].player!!, players[2].rank)
        fillCellPlayer(cellList[6], players[1].player!!, players[1].rank)
        fillCellPlayer(cellList[7], players[3].player!!, players[3].rank)
        fillCellPlayer(cellList[8], players[0].player!!, players[0].rank)
        fillCellPlayer(cellList[9], players[3].player!!, players[3].rank)
        fillCellPlayer(cellList[10], players[1].player!!, players[1].rank)
        fillCellPlayer(cellList[11], players[2].player!!, players[2].rank)
    }

    fun createFinalGroupResult(roundList: List<DrawRound>): MutableList<FinalPlayerScore> {
        var list = mutableListOf<FinalPlayerScore>()
        var map = mutableMapOf<Long, FinalPlayerScore>()
        if (roundList.isNotEmpty()) {
            var records = roundList[0].recordList
            for (pack in records) {
                var winnerId = pack.record!!.winnerId
                for (player in pack.playerList) {
                    var item = map[player.playerId];
                    if (item == null) {
                        item = FinalPlayerScore(player.player!!)
                        item.playerText = "[${player.playerRank}]${player.player!!.name}"
                        item.seed = player.playerRank!!
                        map[player.playerId] = item
                        list.add(item)
                    }
                    item.recordPacks.add(pack)
                    if (player.playerId == winnerId) {
                        item.matchWin ++
                    }
                    else if (winnerId != null) {
                        item.matchLose ++
                    }
                }
                var scores = pack.scoreList
                for (score in scores) {
                    if (score.score1 > score.score2) {
                        map[pack.playerList[0].playerId]!!.setWin ++
                        map[pack.playerList[1].playerId]!!.setLose ++
                    }
                    else {
                        map[pack.playerList[0].playerId]!!.setLose ++
                        map[pack.playerList[1].playerId]!!.setWin ++
                    }
                }
            }
        }
        // 先按胜场降序排序
        list.sortByDescending { it.matchWin }
        // 出现胜场数为2-2-2-0以及3-1-1-1时需要比较盘分，其他情况胜场数相同的比较胜负关系
        if (isLoopResult(list)) {
            // 比较胜盘数
            list.sortByDescending { it.setWin }
        }
        else {
            // 出现相同胜负场次的比较胜负关系，只有2-2-1-1一种情况
            if (list[0].matchWin == list[1].matchWin) {
                defineSameMatchWin(list, 0, 1)
                defineSameMatchWin(list, 2, 3)
            }
        }

        for (i in list.indices) {
            var item = list[i]
            item.rank = i + 1
            item.matchText = "${item.matchWin}胜${item.matchLose}负"
            item.setText = "${item.setWin}胜${item.setLose}负"
        }
        return list
    }

    private fun defineSameMatchWin(list: MutableList<FinalPlayerScore>, index1: Int, index2: Int) {
        // 从index1的交手记录中找到index2，并判断胜负关系
        for (pack in list[index1].recordPacks) {
            var winnerId = pack.record!!.winnerId
            for (player in pack.playerList) {
                // 找到两个player的记录
                if (player.playerId == list[index2].player.id) {
                    // 胜者为index2，交换
                    if (winnerId == player.playerId) {
                        var temp = list[index2]
                        list[index2] = list[index1]
                        list[index1] = temp
                    }
                    else {
                        return
                    }
                }
            }
        }
    }

    private fun isLoopResult(list: MutableList<FinalPlayerScore>): Boolean {
        var count2 = 0
        var count1 = 0
        for (score in list) {
            if (score.matchWin == 2) {
                count2 ++
            }
            else if (score.matchWin == 1) {
                count1 ++
            }
        }
        return count2 == 3 || count1 == 3
    }
}