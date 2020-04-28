package com.king.app.plate.page.player.record

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.detail.RecordParser
import com.king.app.plate.utils.ColorUtils
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/24 10:38
 */
class RecordViewModel(application: Application): BaseViewModel(application) {

    var titleText = ObservableField<String>()

    var headList = MutableLiveData<MutableList<HeadItem>>()

    lateinit var player: Player

    fun loadData(playerId: Long, expandAll: Boolean) {
        getData(playerId, expandAll)
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<MutableList<HeadItem>>(getComposite()){
                override fun onNext(t: MutableList<HeadItem>?) {
                    headList.value = t
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "error: $e"
                }

            })
    }

    private fun getData(playerId: Long, expandAll: Boolean): Observable<MutableList<HeadItem>> = Observable.create {
        player = getDatabase().getPlayerDao().getPlayerById(playerId)
        titleText.set("Records of ${player.name}")

        var list = mutableListOf<HeadItem>()
        var records = getDatabase().getRecordDao().getPlayerRecords(playerId)
        records.reverse()

        // 已按matchId排序
        var lastHead = HeadItem()
        for (record in records) {
            if (record.matchId != lastHead.match?.id) {
                // 去除没有记录的match
                if (lastHead != null && lastHead.childList.size == 0) {
                    list.remove(lastHead)
                }
                var headItem = HeadItem()
                headItem.isExpand = expandAll
                headItem.match = getDatabase().getMatchDao().getMatchById(record.matchId)
                list.add(headItem)
                lastHead = headItem
            }
            // it must be final if win
            if (record.winnerId == playerId && record.round == AppConstants.round - 1) {
                lastHead.result = RecordParser.getRecordResult(record.round, true, lastHead.match!!.level)
            }
            // must be completed
            else if (record.winnerId != playerId && record.winnerId != 0.toLong()) {
                lastHead.result = RecordParser.getRecordResult(record.round, false, lastHead.match!!.level)
            }

            var pack = RecordParser.getRecordPack(record, getDatabase())
            // 轮空，不计入
            if (pack.playerList.size == 1) {
                continue
            }
            var opponent: Player? = null
            for (player in pack.playerList) {
                if (player.playerId != playerId) {
                    opponent = getDatabase().getPlayerDao().getPlayerById(player.playerId)
                }
            }
            var color = if (opponent!!.defColor == null) ColorUtils.randomWhiteTextBgColor()
            else opponent!!.defColor!!
            var childItem = ChildItem(record, opponent!!, color)
            var winner = RecordParser.getH2hResult(pack)
            var score = RecordParser.getScoreText(pack)
            var round = RecordParser.getRoundSimpleText(record.round, lastHead.match!!.level)
            childItem.isWinner = record.winnerId == playerId
            childItem.winner = winner
            childItem.score = score
            childItem.round = round
            lastHead.childList.add(childItem)
        }
        // 去除没有记录的match
        if (lastHead != null && lastHead.childList.size == 0) {
            list.remove(lastHead)
        }
        it.onNext(list)
        it.onComplete()
    }

    /**
     * BaseExpandableAdapter 会把注入的list的结构改变（以现在的二级为例，一级list本来只有HeadItem，最终HeadItem里的ChildItem list会被加入到一级list中）
     */
    fun expandAll() {
        if (headList.value != null) {
            for (i in headList.value!!.indices) {
                if (headList.value!![i] is HeadItem) {
                    headList.value!![i].isExpand = true
                }
            }
        }
    }

    /**
     * BaseExpandableAdapter 会把注入的list的结构改变（以现在的二级为例，一级list本来只有HeadItem，最终HeadItem里的ChildItem list会被加入到一级list中）
     */
    fun collapseAll() {
        if (headList.value != null) {
            for (i in headList.value!!.indices) {
                if (headList.value!![i] is HeadItem) {
                    headList.value!![i].isExpand = false
                }
            }
        }
    }
}