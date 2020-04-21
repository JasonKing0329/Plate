package com.king.app.plate.page.h2h

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.R
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.repo.H2hRepository
import com.king.app.plate.model.repo.RankRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 13:28
 */
class H2hViewModel(application: Application): BaseViewModel(application) {

    var player1Name = ObservableField<String>()
    var player2Name = ObservableField<String>()
    var player1Rank = ObservableField<String>()
    var player2Rank = ObservableField<String>()
    var player1Win = ObservableField<String>()
    var player2Win = ObservableField<String>()
    var player1WinColor = ObservableInt()
    var player2WinColor = ObservableInt()

    var h2hItems = MutableLiveData<List<H2hItem>>()

    private var rankRepository = RankRepository()
    private var h2hRepository = H2hRepository()

    var player1: Player? = null

    var player2: Player? = null

    var indexToReceivePlayer = -1

    fun showLastH2h() {
        getLastH2h()
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<Boolean>(getComposite()) {
                override fun onNext(t: Boolean?) {
                    if (t!!) {
                        onPlayer1Changed()
                        onPlayer2Changed()
                    }
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "error: $e"
                }

            })
    }

    private fun getLastH2h(): Observable<Boolean> = Observable.create {
        var result = false
        var record = getDatabase().getRecordDao().getLastRecordNotBye()
        if (record != null) {
            var players = getDatabase().getRecordPlayerDao().getPlayersByRecord(record.id)
            if (players.size > 1) {
                result = true
                player1 = getDatabase().getPlayerDao().getPlayerById(players[0].playerId)
                player2 = getDatabase().getPlayerDao().getPlayerById(players[1].playerId)
            }
        }
        it.onNext(result)
        it.onComplete()
    }

    fun loadReceivePlayer(playerId: Long) {
        var player = getDatabase().getPlayerDao().getPlayerById(playerId)
        if (indexToReceivePlayer == 1) {
            player1 = player
            onPlayer1Changed()
        }
        else {
            player2 = player
            onPlayer2Changed()
        }
    }

    private fun onPlayer1Changed() {
        if (player1 != null) {
            player1Name.set(player1!!.name)
            var rank = rankRepository.getPlayerCurrentRank(player1!!.id)
            player1Rank.set("Rank $rank")
            onH2hChanged()
        }
    }

    private fun onPlayer2Changed() {
        if (player2 != null) {
            player2Name.set(player2!!.name)
            var rank = rankRepository.getPlayerCurrentRank(player2!!.id)
            player2Rank.set("Rank $rank")
            onH2hChanged()
        }
    }

    private fun onH2hChanged() {
        if (player1 != null && player2 != null) {
            h2hRepository.getH2hItems(player1!!.id, player2!!.id)
                .flatMap { calculateWin(it) }
                .compose(applySchedulers())
                .subscribe(object : NextErrorObserver<MutableList<H2hItem>>(getComposite()){
                    override fun onNext(t: MutableList<H2hItem>) {
                        h2hItems.value = t
                    }

                    override fun onError(e: Throwable) {
                        e?.printStackTrace()
                        messageObserver.value = "error: $e"
                    }
                })
        }
    }

    private fun calculateWin(list: MutableList<H2hItem>): ObservableSource<MutableList<H2hItem>> = ObservableSource {
        var win1 = 0
        var win2 = 0
        for (item in list) {
            if (item.recordPack.record!!.winnerId == player1!!.id) {
                win1 ++
            }
            else{
                win2 ++
            }
        }
        player1Win.set(win1.toString())
        player2Win.set(win2.toString())
        if (win1 > win2) {
            player1WinColor.set(getResource().getColor(R.color.red))
            player2WinColor.set(getResource().getColor(R.color.text_sub))
        }
        else if (win2 > win1) {
            player2WinColor.set(getResource().getColor(R.color.red))
            player1WinColor.set(getResource().getColor(R.color.text_sub))
        }
        else{
            player1WinColor.set(getResource().getColor(R.color.text_sub))
            player2WinColor.set(getResource().getColor(R.color.text_sub))
        }
        it.onNext(list)
        it.onComplete()
    }
}