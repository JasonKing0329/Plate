package com.king.app.plate.page.match

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.BodyCell
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.detail.DrawModel
import com.king.app.plate.model.repo.*
import io.reactivex.rxjava3.core.ObservableSource

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
    private var scoreRepository = ScoreRepository()
    private var rankRepository = RankRepository()
    private var drawModel = DrawModel()
    lateinit var match: Match

    private var drawData: DrawData = DrawData()

    var forResultBodyCell: BodyCell? = null

    fun loadData(matchId: Long) {
        loadMatch(matchId)
        createDrawBody()
    }

    private fun loadMatch(matchId: Long) {
        match = getDatabase().getMatchDao().getMatchById(matchId)
        drawData.match = match
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
        recordRepository.getRoundRecords(match.id)
            .flatMap {
                drawData.roundList = it
                drawRepository.createDrawBody() }
            .flatMap {
                drawData.body = it
                playerRepository.getRankPlayers() }
            .flatMap { drawModel.randomNormalDraw(drawData, it) }
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
                    // 重新加载
                    createDrawBody()
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
        var seed = rankRepository.getPlayerCurrentRank(playerId)
        if (seed > 8) {
            seed = 0
        }
        var player = getDatabase().getPlayerDao().getPlayerById(playerId)
        drawModel.fillCellPlayer(forResultBodyCell!!, player, seed)
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
            try {
                cell.player!!.playerSeed = seed.toInt()
                if (seed.toInt() > 0) {
                    cell.text = "[${seed.toInt()}]${cell.player!!.player!!.name}"
                }
                else{
                    cell.text = cell.player!!.player!!.name!!
                }
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
                        drawModel.fillCellPlayer(targetCell, player.player!!, player.playerSeed!!)
                        return
                    }
                }
            } catch (e: Exception) {e.printStackTrace()}
        }
    }

    fun createScore() {
        scoreRepository.createMatchScores(match.id)
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<Boolean>(getComposite()) {

                override fun onNext(t: Boolean) {
                    messageObserver.value = "success"
                    loadMatch(match.id)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "create error: $e"
                }

            })
    }

    fun createRank() {
        rankRepository.createRank(match.id)
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<Boolean>(getComposite()) {

                override fun onNext(t: Boolean) {
                    messageObserver.value = "success"
                    loadMatch(match.id)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "create error: $e"
                }

            })
    }

    fun isMatchCompleted(): Boolean {
        // 以决赛是否生成winnerId判定
        var record = getDatabase().getRecordDao().getRecord(match.id, AppConstants.round - 1, 0)
        return record != null && record.winnerId != 0.toLong()
    }

    fun getRecordPlayer(x: Int, y: Int, i: Int): RecordPlayer? {
        try {
            return drawData.body.bodyData[x][y].pack!!.playerList[i]
        } catch (e: Exception) {}
        return null
    }

    fun getRecordPlayer(x: Int, y: Int): RecordPlayer? {
        try {
            return drawData.body.bodyData[x][y].player
        } catch (e: Exception) {}
        return null
    }
}