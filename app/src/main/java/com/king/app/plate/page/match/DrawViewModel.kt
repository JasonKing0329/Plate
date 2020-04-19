package com.king.app.plate.page.match

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.BodyCell
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.repo.DrawRepository
import com.king.app.plate.model.repo.MatchRepository
import com.king.app.plate.model.repo.PlayerRepository
import com.king.app.plate.model.repo.RecordRepository
import io.reactivex.ObservableSource

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 11:24
 */
class DrawViewModel(application: Application): BaseViewModel(application) {

    var dataObserver: MutableLiveData<DrawData> = MutableLiveData()
    private var playerRepository = PlayerRepository()
    private var drawRepository = DrawRepository()
    private var recordRepository = RecordRepository()
    private var matchRepository = MatchRepository()
    lateinit var match: Match

    private var drawData: DrawData = DrawData()

    var forResultBodyCell: BodyCell? = null

    fun loadData(matchId: Long) {
        match = getDatabase().getMatchDao().getMatchById(matchId)
        drawData.match = match
        createDrawBody()
    }

    private fun createDrawBody() {
        recordRepository.getRoundRecords(match.id)
            .flatMap {
                drawData.roundList = it
                drawRepository.createDrawBody()
            }
            .flatMap {
                drawData.body = it
                convertDrawData()
            }
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<DrawData>(getComposite()) {
                override fun onNext(t: DrawData) {
                    dataObserver.value = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "create error: $e"
                }

            })
    }

    fun createNewDraw() {
        matchRepository.deleteMatchDetails(match.id)
        playerRepository.getRankPlayers()
            .flatMap { drawRepository.createPlayerDraw(it) }
            .flatMap {
                drawData.roundList = it
                convertDrawData()
            }
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<DrawData>(getComposite()) {
                override fun onNext(t: DrawData) {
                    dataObserver.value = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "create error: $e"
                }

            })
    }

    private fun convertDrawData(): ObservableSource<DrawData> = ObservableSource {
        drawRepository.convertRoundsToBody(drawData.roundList, drawData.body)
        // 显示winner
        var finalRecord = getDatabase().getRecordDao().getRecord(match.id, AppConstants.round - 1, 0)
        if (finalRecord?.winnerId != null) {
            var recordPlayers = getDatabase().getRecordPlayerDao().getPlayersByRecord(finalRecord.id)
            for (recordPlayer in recordPlayers) {
                if (recordPlayer.playerId == finalRecord.winnerId) {
                    var column = AppConstants.round * (AppConstants.set + 1)
                    recordPlayer.player = getDatabase().getPlayerDao().getPlayerById(recordPlayer.playerId)
                    if (recordPlayer.playerSeed!! > 0) {
                        drawData.body.bodyData[column][0].text = "[${recordPlayer.playerSeed}]${recordPlayer.player!!.name!!}"
                    }
                    else{
                        drawData.body.bodyData[column][0].text = recordPlayer.player!!.name!!
                    }
                    break
                }
            }
        }
        it.onNext(drawData)
        it.onComplete()
    }

    fun saveDraw() {
        drawRepository.saveDraw(drawData)
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<DrawData>(getComposite()) {

                override fun onNext(t: DrawData) {
                    messageObserver.value = "success"
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "save error: $e"
                }

            })
    }

    fun setForResultBodyCell(x: Int, y: Int) {
        forResultBodyCell = drawData.body.bodyData[x][y]
    }

    fun updateForResultPlayer(playerId: Long) {
        fillCellPlayer(forResultBodyCell!!, playerId, 0)
    }

    private fun fillCellPlayer(cell: BodyCell, playerId: Long, seed: Int) {
        var player = getDatabase().getPlayerDao().getPlayerById(playerId)
        var recordPlayer = RecordPlayer(0 ,0 ,playerId, seed ,seed ,cell.row , player)
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

    fun deletePlayer(x: Int, y: Int) {
        var cell = drawData.body.bodyData[x][y]
        cell.pack!!.playerList.remove(cell.player)
        cell.player = null
        cell.text = ""
        cell.isModified = true
    }

    fun updatePlayerSeed(x: Int, y: Int, seed: String) {
        var cell = drawData.body.bodyData[x][y]
        if (cell.player != null && cell.player!!.player != null) {
            cell.player!!.playerSeed = seed.toInt()
            try {
                cell.text = "[${seed.toInt()}]${cell.player!!.player!!.name}"
                cell.isModified = true
            } catch (e: Exception) {}
        }
    }

    fun getWinnerFor(x: Int, y: Int) {
        if (x > 0) {
            var targetCell = drawData.body.bodyData[x][y]
            try {
                var recordPack = drawData.body.bodyData[x - (AppConstants.set + 1)][y * 2].pack
                for (player in recordPack!!.playerList) {
                    if (player.playerId == recordPack!!.record?.winnerId) {
                        fillCellPlayer(targetCell, player.playerId, player.playerSeed!!)
                        return
                    }
                }
            } catch (e: Exception) {e.printStackTrace()}
        }
    }
}