package com.king.app.plate.page.match

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.FinalGroupPlayers
import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.detail.DrawModel
import com.king.app.plate.model.repo.DrawRepository
import com.king.app.plate.model.repo.PlayerRepository
import com.king.app.plate.model.repo.RecordRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import java.lang.Exception
import java.util.*
import kotlin.math.abs

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/11 10:44
 */
class FinalDrawViewModel(application: Application): BaseViewModel(application) {

    companion object {
        val DRAW_RED = 0
        val DRAW_BLUE = 1
        val DRAW_WIN = 2
    }

    private var drawRepository = DrawRepository()
    private var recordRepository = RecordRepository()
    private var playerRepository = PlayerRepository()
    private var drawModel = DrawModel()

    var showGroupRedPlayers = MutableLiveData<MutableList<RankPlayer>>()
    var showGroupBluePlayers = MutableLiveData<MutableList<RankPlayer>>()

    lateinit var match: Match
    private lateinit var groupPlayers: FinalGroupPlayers

    private var groupRedDrawData = DrawData()
    private var groupBlueDrawData = DrawData()
    private var restDrawData = DrawData()
    var redDataObserver: MutableLiveData<DrawData> = MutableLiveData()
    var blueDataObserver: MutableLiveData<DrawData> = MutableLiveData()
    var restDataObserver: MutableLiveData<DrawData> = MutableLiveData()

    var groupRedResults = MutableLiveData<MutableList<FinalPlayerScore>>()
    var groupBlueResults = MutableLiveData<MutableList<FinalPlayerScore>>()

    fun loadData(matchId: Long) {
        loadMatch(matchId)
        loadPlayers()
        loadDraws()
    }

    private data class DrawPart(
        var drawData: DrawData,
        var observer: MutableLiveData<DrawData>
    )

    private data class ResultPart(
        var list: MutableList<FinalPlayerScore>,
        var observer: MutableLiveData<MutableList<FinalPlayerScore>>
    )

    private fun loadDraws() {
        Observable.concat(loadRedGroup(), loadBlueGroup(), loadRestRound())
            .compose(applySchedulers())
            .subscribe(object : Observer<DrawPart> {

                override fun onNext(t: DrawPart) {
                    t.observer.value = t.drawData
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "create error: $e"
                }

                override fun onComplete() {
                    if (groupPlayers != null && (groupPlayers.groupRed.isNotEmpty() || groupPlayers.groupBlue.isNotEmpty())) {
                        loadGroupResults()
                    }
                }

                override fun onSubscribe(d: Disposable?) {

                }
            })
    }

    private fun loadPlayers() {
        getPlayers()
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<FinalGroupPlayers>(getComposite()) {

                override fun onNext(t: FinalGroupPlayers) {
                    groupPlayers = t
                    showGroupBluePlayers.value = groupPlayers.groupBlue
                    showGroupRedPlayers.value = groupPlayers.groupRed
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "create error: $e"
                }
            })
    }

    private fun loadMatch(matchId: Long) {
        match = getDatabase().getMatchDao().getMatchById(matchId)
        groupRedDrawData.match = match
        groupBlueDrawData.match = match
        restDrawData.match = match
    }

    private fun loadRedGroup(): Observable<DrawPart> {
        return recordRepository.getFinalGroupRoundRecords(match.id, 0)
            .flatMap {
                groupRedDrawData.roundList = it
                drawRepository.createFinalGroupDraw()
            }
            .flatMap {
                groupRedDrawData.body = it
                convertDrawData(groupRedDrawData)
            }
            .map { DrawPart(it, redDataObserver) };
    }

    private fun loadBlueGroup(): Observable<DrawPart> {
        return recordRepository.getFinalGroupRoundRecords(match.id, 1)
            .flatMap {
                groupBlueDrawData.roundList = it
                drawRepository.createFinalGroupDraw()
            }
            .flatMap {
                groupBlueDrawData.body = it
                convertDrawData(groupBlueDrawData)
            }
            .map { DrawPart(it, blueDataObserver) };
    }

    private fun loadRestRound(): Observable<DrawPart> {
        return recordRepository.getFinalRestRoundRecords(match.id)
            .flatMap {
                restDrawData.roundList = it
                drawRepository.createFinalRestDraw()
            }
            .flatMap {
                restDrawData.body = it
                convertRestDrawData(restDrawData)
            }
            .map { DrawPart(it, restDataObserver) };
    }

    private fun convertDrawData(drawData: DrawData): ObservableSource<DrawData> = ObservableSource {
        drawRepository.convertFinalGroupToBody(drawData.roundList, drawData.body)
        it.onNext(drawData)
        it.onComplete()
    }

    private fun convertRestDrawData(drawData: DrawData): ObservableSource<DrawData> = ObservableSource {
        drawRepository.convertFinalRestToBody(drawData.roundList, drawData.body)
        // 显示winner
        var finalRecord = getDatabase().getRecordDao().getRecord(match.id, AppConstants.ROUND_F, 0)
        if (finalRecord?.winnerId != null) {
            var recordPlayers = getDatabase().getRecordPlayerDao().getPlayersByRecord(finalRecord.id)
            for (recordPlayer in recordPlayers) {
                if (recordPlayer.playerId == finalRecord.winnerId) {
                    var column = AppConstants.round_final_rest * (AppConstants.set + 1)
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

    fun createDraw() {
        playerRepository.getRankPlayers(null, 8)
            .flatMap { drawModel.createFinalGroupPlayers(it) }
            .flatMap {
                groupPlayers = it
                createGroupDraws()
            }
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<Boolean>(getComposite()) {
                override fun onNext(t: Boolean) {
                    showGroupBluePlayers.value = groupPlayers.groupBlue
                    showGroupRedPlayers.value = groupPlayers.groupRed
                    redDataObserver.value = groupRedDrawData
                    blueDataObserver.value = groupBlueDrawData
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "create error: $e"
                }

            })
    }

    private fun getPlayers(): Observable<FinalGroupPlayers> = Observable.create {
        var redList = playerRepository.getFinalGroupPlayers(match.id, 0)
        redList.sortBy { player -> player.rank }
        var blueList = playerRepository.getFinalGroupPlayers(match.id, 1)
        blueList.sortBy { player -> player.rank }
        it.onNext(FinalGroupPlayers(redList, blueList))
        it.onComplete()
    }

    private fun createGroupDraws(): Observable<Boolean> = Observable.create {
        drawModel.createFinalGroupDraw(groupRedDrawData, groupPlayers.groupRed)
        drawModel.createFinalGroupDraw(groupBlueDrawData, groupPlayers.groupBlue)
        it.onNext(true)
        it.onComplete()
    }

    private fun loadGroupResults() {
        Observable.concat(getFinalGroupResults(groupRedDrawData, groupRedResults), getFinalGroupResults(groupBlueDrawData, groupBlueResults))
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<ResultPart>(getComposite()) {
                override fun onNext(t: ResultPart) {
                    t.observer.value = t.list
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "create error: $e"
                }
            })
    }

    private fun getFinalGroupResults(
        drawData: DrawData,
        observer: MutableLiveData<MutableList<FinalPlayerScore>>
    ): Observable<ResultPart> = Observable.create {
        var list = drawModel.createFinalGroupResult(drawData.roundList)
        it.onNext(ResultPart(list, observer))
        it.onComplete()
    }

    fun saveDraw() {
        var observables = listOf(drawRepository.saveFinalGroupDraw(groupRedDrawData, 0)
            , drawRepository.saveFinalGroupDraw(groupBlueDrawData, 1)
            , drawRepository.saveFinalRestDraw(restDrawData))
        Observable.zip(observables) { true }
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<Boolean>(getComposite()) {

                override fun onNext(t: Boolean) {
                    messageObserver.value = "success"
                    // 重新加载
                    loadData(match.id)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "save error: $e"
                }

            })
    }

    fun getRecordPlayer(x: Int, y: Int, drawType: Int): RecordPlayer? {
        try {
            return when(drawType) {
                DRAW_RED -> groupRedDrawData.body.bodyData[x][y].player
                DRAW_BLUE -> groupBlueDrawData.body.bodyData[x][y].player
                DRAW_WIN -> restDrawData.body.bodyData[x][y].player
                else -> null
            }
        } catch (e: Exception) {}
        return null
    }

    fun getRecordPlayer(x: Int, y: Int, i: Int, drawType: Int): RecordPlayer? {
        try {
            return when(drawType) {
                DRAW_RED -> groupRedDrawData.body.bodyData[x][y].pack!!.playerList[i]
                DRAW_BLUE -> groupBlueDrawData.body.bodyData[x][y].pack!!.playerList[i]
                DRAW_WIN -> restDrawData.body.bodyData[x][y].pack!!.playerList[i]
                else -> null
            }
        } catch (e: Exception) {}
        return null
    }

    fun deletePlayer(x: Int, y: Int, drawType: Int) {
        var cell = when(drawType) {
            DRAW_RED -> groupRedDrawData.body.bodyData[x][y]
            DRAW_BLUE -> groupBlueDrawData.body.bodyData[x][y]
            DRAW_WIN -> restDrawData.body.bodyData[x][y]
            else -> null
        }
        if (cell != null) {
            cell.pack!!.playerList.remove(cell.player)
            cell.player = null
            cell.text = ""
            cell.isModified = true
        }
    }

    fun getWinnerFor(x: Int, y: Int) {
        try {
            var targetCell = restDrawData.body.bodyData[x][y]
            // SF取小组赛赛果
            if (x == 0) {
                var result: FinalPlayerScore? = when (y) {
                    0 -> groupRedResults.value!![0]// red第一
                    1 -> groupBlueResults.value!![1]// blue第二
                    2 -> groupRedResults.value!![1]// red第二
                    3 -> groupBlueResults.value!![0]// blue第一
                    else -> null
                }
                drawModel.fillCellPlayer(targetCell, result!!.player, result!!.seed)
            }
            // F取SF结果
            else {
                var recordPack = restDrawData.body.bodyData[x - (AppConstants.set + 1)][y * 2].pack
                for (player in recordPack!!.playerList) {
                    if (player.playerId == recordPack!!.record?.winnerId) {
                        drawModel.fillCellPlayer(targetCell, player.player!!, player.playerSeed!!)
                        return
                    }
                }
            }
        } catch (e: Exception) {
            messageObserver.value = "Winner is not ready"
        }
    }

}