package com.king.app.plate.model.detail

import com.king.app.plate.model.bean.BodyCell
import com.king.app.plate.model.bean.DrawBody
import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.page.match.DrawData
import io.reactivex.rxjava3.core.Observable

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
}