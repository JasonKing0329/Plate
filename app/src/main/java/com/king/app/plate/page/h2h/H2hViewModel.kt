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
import com.king.app.plate.utils.ColorUtils
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
    var player1CircleColor = MutableLiveData<Int>()
    var player2CircleColor = MutableLiveData<Int>()

    private var rankRepository = RankRepository()
    private var h2hRepository = H2hRepository()

    var player1: Player? = null

    var player2: Player? = null

    var indexToReceivePlayer = -1

    fun initPlayers(player1Id: Long, player2Id: Long) {
        player1 = getDatabase().getPlayerDao().getPlayerById(player1Id)
        player2 = getDatabase().getPlayerDao().getPlayerById(player2Id)
        onPlayer1Changed()
        onPlayer2Changed()
    }

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
            var color = if (player1!!.defColor == null) ColorUtils.randomWhiteTextBgColor()
            else player1!!.defColor!!
            player1CircleColor.value = color
            onH2hChanged()
        }
    }

    private fun onPlayer2Changed() {
        if (player2 != null) {
            player2Name.set(player2!!.name)
            var rank = rankRepository.getPlayerCurrentRank(player2!!.id)
            player2Rank.set("Rank $rank")
            var color = if (player2!!.defColor == null) ColorUtils.randomWhiteTextBgColor()
            else player2!!.defColor!!
            player2CircleColor.value = color
            onH2hChanged()
        }
    }

    private fun onH2hChanged() {
        if (player1 != null && player2 != null) {
            h2hRepository.getH2hItems(player1!!.id, player2!!.id)
                .flatMap { calculateWin(it, player1!!.id, player2!!.id) }
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

    private fun calculateWin(list: MutableList<H2hItem>, player1Id: Long, player2Id: Long): ObservableSource<MutableList<H2hItem>> = ObservableSource {
        var h2hResult = getDatabase().getRecordDao().getH2hResult(player1Id, player2Id)
        player1Win.set(h2hResult.player1Win.toString())
        player2Win.set(h2hResult.player2Win.toString())
        var player1ItemBg: Int = getResource().getColor(R.color.h2h_bg_more)
        var player2ItemBg: Int = getResource().getColor(R.color.h2h_bg_less)
        when {
            h2hResult.player1Win > h2hResult.player2Win -> {
                player1WinColor.set(player1CircleColor.value!!)
                player2WinColor.set(getResource().getColor(R.color.text_sub))
            }
            h2hResult.player2Win > h2hResult.player1Win -> {
                player2WinColor.set(player2CircleColor.value!!)
                player1WinColor.set(getResource().getColor(R.color.text_sub))
                player2ItemBg = getResource().getColor(R.color.h2h_bg_more);
                player1ItemBg = getResource().getColor(R.color.h2h_bg_less);
            }
            else -> {
                player1WinColor.set(getResource().getColor(R.color.text_sub))
                player2WinColor.set(getResource().getColor(R.color.text_sub))
            }
        }
        for (item in list) {
            item.bgColor = if (item.recordPack.record!!.winnerId == player1Id) {
                player1ItemBg
            }
            else {
                player2ItemBg
            }
        }
        it.onNext(list)
        it.onComplete()
    }
}