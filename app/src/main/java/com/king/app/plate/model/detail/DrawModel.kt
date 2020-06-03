package com.king.app.plate.model.detail

import com.king.app.plate.model.bean.BodyCell
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
        fillFinalPlayers(cellList[0], cellList[1], players[0], players[1]);
        fillFinalPlayers(cellList[2], cellList[3], players[2], players[3]);
        fillFinalPlayers(cellList[4], cellList[5], players[0], players[2]);
        fillFinalPlayers(cellList[6], cellList[7], players[1], players[3]);
        fillFinalPlayers(cellList[8], cellList[9], players[0], players[3]);
        fillFinalPlayers(cellList[10], cellList[11], players[1], players[2]);
        // 重新按rank还原
        players.sortBy { it.rank }
    }

    /**
     * final group match, 确保排名高的在上方
     */
    private fun fillFinalPlayers(cellTop: BodyCell, cellBottom: BodyCell, rankPlayer: RankPlayer, rankPlayer1: RankPlayer) {
        var playerTop: RankPlayer;
        var playerBottom: RankPlayer;
        if (rankPlayer.rank < rankPlayer1.rank) {
            playerTop = rankPlayer;
            playerBottom = rankPlayer1
        }
        else {
            playerTop = rankPlayer1;
            playerBottom = rankPlayer
        }
        fillCellPlayer(cellTop, playerTop.player!!, playerTop.rank)
        fillCellPlayer(cellBottom, playerBottom.player!!, playerBottom.rank)
    }

    /**
     * 遵循ATP Final规则，小组排名按一下顺序确定：
     * 1.Number of wins 胜场
     * 2.Number of matches(这里可以忽略，所有player参赛场次都一样)
     * 3.In two-player-ties, head-to-head records 两人经过1,2后分不出胜负，比较胜负关系（仅2-2-1-1的情况）
     * 4.In three-player-ties, percentage of sets won, percentage of games won, then head-to-head
     * (三人连环套的情况，先比胜盘率，决出一人不同后，剩下两人再依次执行规则直到规则3（这一人有可能是第一也可能是第三）；胜盘率仍全一样，比较胜局率，规则同比较胜盘率)
     *
     * 最后，如果胜局率也完全一样，那就不考虑了，ATP也没有考虑，可能是真的不会出现吧
     */
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
                        map[pack.playerList[0].playerId]!!.gameWin += score.score1
                        map[pack.playerList[1].playerId]!!.gameLose += score.score2
                        map[pack.playerList[0].playerId]!!.setWin ++
                        map[pack.playerList[1].playerId]!!.setLose ++
                    }
                    else {
                        map[pack.playerList[0].playerId]!!.gameLose += score.score1
                        map[pack.playerList[1].playerId]!!.gameWin += score.score2
                        map[pack.playerList[0].playerId]!!.setLose ++
                        map[pack.playerList[1].playerId]!!.setWin ++
                    }
                }
            }
        }
        // 先按胜场降序排序（规则1）
        list.sortByDescending { it.matchWin }
        // 出现胜场数为2-2-2-0以及3-1-1-1时需要执行（规则4），其他情况胜场数相同的比较胜负关系（规则3）
        if (isLoopResult(list)) {
            // 规则4
            list = resolveThreeInTie(list)
        }
        else {
            // 规则3，只有2-2-1-1一种情况
            if (list[0].matchWin == list[1].matchWin) {
                resolveTwoInTie(list, 0, 1)
                resolveTwoInTie(list, 2, 3)
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

    /**
     * resolve three-player-ties(只有2-2-2-0以及3-1-1-1两种情况)
     * In three-player-ties, percentage of sets won, percentage of games won, then head-to-head
     * (三人连环套的情况，先比胜盘率，决出一人不同后，剩下两人回到规则3（这一人有可能是第一也可能是第三）；胜盘率仍全一样，比较胜局率，规则同比较胜盘率)
     * @param list 已按matchWin排序
     */
    private fun resolveThreeInTie(list: MutableList<FinalPlayerScore>): MutableList<FinalPlayerScore> {
        var result = mutableListOf<FinalPlayerScore>()
        // 3-1-1-1的情况
        if (list[0].matchWin > list[1].matchWin) {
            result.add(list[0])
            list.removeAt(0)
        }
        // 剩余的进行胜盘率与胜局率比较
        for (item in list) {
            // 计算胜盘率与胜局率
            item.setRate = item.setWin.toFloat() / (item.setWin + item.setLose)
            item.gameRate = item.gameWin.toFloat() / (item.gameWin + item.gameLose)
        }
        list.sortByDescending { it.setRate }
        // 2-2-2，1-1-1中第一个就大于后面两个，第一个胜出，剩下两个比较胜负关系
        if (list[0].setRate != list[1].setRate) {
            result.add(list[0])
            list.removeAt(0)
            // 剩下两个tie的人比较胜负关系，如果是2-2-2-0的情况，最后一个自动为最后一名
            resolveTwoInTie(list, 0, 1)
            // 加入到结果
            result.addAll(list)
        }
        else {
            // 2-2-2，1-1-1中前两个一样，最后一个不一样，前两个比较胜负关系
            if (list[1].setRate != list[2].setRate) {
                resolveTwoInTie(list, 0, 1)
                // 加入到结果
                result.addAll(list)
            }
            // 三人胜盘率都一样，进入到胜局率的比较
            else {
                list.sortByDescending { it.gameRate }
                // 2-2-2，1-1-1中第一个就大于后面两个，第一个胜出，剩下两个比较胜负关系
                if (list[0].gameRate != list[1].gameRate) {
                    result.add(list[0])
                    list.removeAt(0)
                    // 剩下两个tie的人比较胜负关系，如果是2-2-2-0的情况，最后一个自动为最后一名
                    resolveTwoInTie(list, 0, 1)
                    // 加入到结果
                    result.addAll(list)
                }
                else {
                    // 2-2-2，1-1-1中前两个一样，最后一个不一样，前两个比较胜负关系
                    if (list[1].gameRate != list[2].gameRate) {
                        resolveTwoInTie(list, 0, 1)
                        // 加入到结果
                        result.addAll(list)
                    }
                    // 三人胜局率都一样，不作考虑
                    else {

                    }
                }
            }
        }
        return result
    }

    /**
     * In two-player-ties, head-to-head records 两人比较胜负关系，决定交换在list中的位置
     */
    private fun resolveTwoInTie(list: MutableList<FinalPlayerScore>, index1: Int, index2: Int) {
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