package com.king.app.plate.page.match

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.bean.DrawBody
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.repo.DrawRepository
import com.king.app.plate.model.repo.PlayerRepository
import io.reactivex.ObservableSource

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 11:24
 */
class DrawViewModel(application: Application): BaseViewModel(application) {

    var dataObserver: MutableLiveData<DrawData> = MutableLiveData()
    var playerRepository = PlayerRepository()
    var drawRepository = DrawRepository()
    lateinit var match: Match

    lateinit var drawBody: DrawBody
    lateinit var drawData: DrawData

    fun loadData(matchId: Int) {
        match = getDatabase().getMatchDao().getMatchById(matchId)
        createDrawBody()
    }

    private fun createDrawBody() {
        drawRepository.createDrawBody()
            .flatMap { toDrawData(mutableListOf(), it) }
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<DrawData>(getComposite()) {
                override fun onNext(t: DrawData) {
                    dataObserver.value = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = e.message
                }

            })
    }

    fun createDraw() {
        playerRepository.getRankPlayers()
            .flatMap { drawRepository.createPlayerDraw(it) }
            .flatMap { toDrawData(it, drawBody) }
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<DrawData>(getComposite()) {
                override fun onNext(t: DrawData) {
                    dataObserver.value = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = e.message
                }

            })
    }

    private fun toDrawData(rounds: List<DrawRound>, drawBody: DrawBody): ObservableSource<DrawData> = ObservableSource {
        var data = DrawData(null, rounds, drawBody)
        drawRepository.convertRoundsToBody(rounds, drawBody)
        drawData = data;
        it.onNext(data)
        it.onComplete()
    }

    fun saveDraw() {

    }

}