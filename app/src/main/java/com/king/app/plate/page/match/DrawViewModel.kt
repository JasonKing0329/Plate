package com.king.app.plate.page.match

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.bean.DrawBody
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.repo.DrawRepository
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
    lateinit var match: Match

    private var drawData: DrawData = DrawData()

    fun loadData(matchId: Int) {
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
                    messageObserver.value = e.message
                }

            })
    }

    fun createDraw() {
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
                    messageObserver.value = e.message
                }

            })
    }

    private fun convertDrawData(): ObservableSource<DrawData> = ObservableSource {
        drawRepository.convertRoundsToBody(drawData.roundList, drawData.body!!)
        it.onNext(drawData)
        it.onComplete()
    }

    fun saveDraw() {

    }

}