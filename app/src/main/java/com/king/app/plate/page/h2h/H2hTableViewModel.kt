package com.king.app.plate.page.h2h

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.R
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.bean.H2hResultPack
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.page.h2h.list.H2hListItem
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/22 11:18
 */
class H2hTableViewModel(application: Application): BaseViewModel(application) {

    var playerList = MutableLiveData<MutableList<H2hTableItem>>()
    var h2hList = MutableLiveData<MutableList<H2hTableItem>>()
    var h2hSortedList = MutableLiveData<MutableList<H2hListItem>>()
    var showH2hDetail = MutableLiveData<H2hListItem>()

    var playerTextColor: Int = 0
    var playerBgColor: Int = 0
    var h2hTextColor: Int = 0
    var h2hBgColor: Int = 0
    var focusBgColor: Int = 0

    var initFocusPlayerId: Long? = null

    private var focusRow = -1
    private var focusColPosition = -1

    fun getRow(): Int {
        return getDatabase().getPlayerDao().getCount() + 1
    }

    fun loadData(focusPlayerId: Long) {
        initFocusPlayerId = focusPlayerId
        playerTextColor = getResource().getColor(R.color.text_main)
        h2hTextColor = getResource().getColor(R.color.text_second)
        playerBgColor = Color.parseColor("#d0d0d0")
        h2hBgColor = Color.parseColor("#efefef")
        focusBgColor = getResource().getColor(R.color.h2h_focus_bg)

        getPlayers()
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<MutableList<H2hTableItem>>(getComposite()) {
                override fun onNext(t: MutableList<H2hTableItem>?) {
                    playerList.value = t
                    getH2h()
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "getPlayers error: $e"
                }

            })
    }

    private fun getH2h() {
        loadingObserver.value = true
        getH2hList()
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<MutableList<H2hTableItem>>(getComposite()) {
                override fun onNext(t: MutableList<H2hTableItem>?) {
                    loadingObserver.value = false
                    h2hList.value = t

                    if (initFocusPlayerId != null) {
                        onFocusChanged()
                    }
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    loadingObserver.value = false
                    messageObserver.value = "getH2hList error: $e"
                }

            })
    }

    private fun getPlayers(): Observable<MutableList<H2hTableItem>> = Observable.create {
        var players = getDatabase().getPlayerDao().getPlayers()
        var list = mutableListOf<H2hTableItem>()
        for (player in players) {
            var item = H2hTableItem(player, player.name!!, playerBgColor, playerTextColor, false)
            list.add(item)

            if (initFocusPlayerId == player.id) {
                focusRow = list.size
            }
        }
        list.add(0, H2hTableItem(null, "", playerBgColor, playerTextColor, false))
        it.onNext(list)
        it.onComplete()
    }

    private fun getH2hList(): Observable<MutableList<H2hTableItem>> = Observable.create {
        var players = playerList.value
        var list = mutableListOf<H2hTableItem>()
        for (col in 1 until players!!.size) {
            var item = players[col]
            var itemPlayer = H2hTableItem(item.bean, item.name!!, playerBgColor, playerTextColor, false)
            list.add(itemPlayer)
            var playerCol: Player = item.bean as Player
            if (initFocusPlayerId == playerCol.id) {
                focusColPosition = list.size - 1
            }
            for (row in 1 until players!!.size) {
                var playerRow: Player = players[row].bean as Player
                if (playerCol.id == playerRow.id) {
                    list.add(H2hTableItem(null, "--", h2hBgColor, h2hTextColor, false))
                }
                else {
                    var result = getDatabase().getRecordDao().getH2hResult(playerRow.id, playerCol.id)
                    var text = if (result.player1Win == 0 && result.player2Win == 0) ""
                    else "${result.player1Win}-${result.player2Win}"
                    list.add(H2hTableItem(H2hResultPack(result, playerRow.id, playerCol.id), text, h2hBgColor, h2hTextColor, false))
                }
            }
        }
        it.onNext(list)
        it.onComplete()
    }

    fun focusRow(row: Int) {
        focusRow = if (row == focusRow) -1 else row
        onFocusChanged()
    }

    fun focusCol(position: Int) {
        focusColPosition = if (position == focusColPosition) -1 else position
        onFocusChanged()
    }

    private fun onFocusChanged() {
        if (h2hList.value != null) {
            for (i in 0 until getH2hSize()) {
                var row = i % getPlayerSize()
                if (row == 0) {
                    continue
                }
                if (isInFocusRow(row) || isInFocusCol(i)) {
                    h2hList.value!![i].isFocus = true
                    h2hList.value!![i].bgColor = focusBgColor
                }
                else {
                    h2hList.value!![i].isFocus = false
                    h2hList.value!![i].bgColor = h2hBgColor
                }
            }
        }
    }

    private fun isInFocusRow(row: Int): Boolean = row == focusRow

    private fun isInFocusCol(position: Int): Boolean = focusColPosition != -1 && position > focusColPosition && position < focusColPosition + getPlayerSize()

    private fun getPlayerSize(): Int {
        return if (playerList.value == null) 0 else playerList.value!!.size
    }

    private fun getH2hSize(): Int {
        return if (h2hList.value == null) 0 else h2hList.value!!.size
    }

    fun isH2hPlayer(position: Int): Boolean {
        return if (h2hList.value == null) false
        else {
            var item = h2hList.value!![position]
            item.bean is Player
        }
    }

    fun isH2hResult(position: Int): Boolean {
        return if (h2hList.value == null) false
        else {
            var item = h2hList.value!![position]
            item.bean is H2hResultPack
        }
    }

    fun collectListItem() {
        loadingObserver.value = true
        getListItem()
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<MutableList<H2hListItem>>(getComposite()) {
                override fun onNext(t: MutableList<H2hListItem>) {
                    loadingObserver.value = false
                    h2hSortedList.value = t
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    loadingObserver.value = false
                    messageObserver.value = "collectListItem error: $e"
                }
            })
    }

    private fun getListItem(): Observable<MutableList<H2hListItem>> = Observable.create{
        var list = mutableListOf<H2hListItem>()
        // 先查出所有player间的交手记录
        var players = getDatabase().getPlayerDao().getPlayers()
        for (i in players.indices) {
            for (j in i + 1 until players.size) {
                //TODO 暂时不做detail统计
//                var records = getDatabase().getRecordDao().getH2hRecords(players[i].id, players[j].id)
                var result = getDatabase().getRecordDao().getH2hResult(players[i].id, players[j].id)
                // 只显示3次以上交手记录
                if (result.player2Win + result.player1Win > 2) {
                    var item = H2hListItem(players[i], players[j], result.player1Win, result.player2Win, null)
                    list.add(item)
                }
            }
        }
        list.sortByDescending { it -> it.score1 + it.score2}
        it.onNext(list)
        it.onComplete()
    }
}