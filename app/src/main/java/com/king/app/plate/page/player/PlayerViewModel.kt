package com.king.app.plate.page.player

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.repo.RankRepository
import com.king.app.plate.model.repo.ScoreRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 16:28
 */
class PlayerViewModel(application: Application): BaseViewModel(application) {

    var playersObserver = MutableLiveData<List<PlayerItem>>()

    var scoreRepository = ScoreRepository();

    var rankRepository = RankRepository();

    fun loadPlayers() {
        showLoading(true)
        getPlayers()
            .flatMap { toPlayerItems(it) }
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<List<PlayerItem>>(getComposite()) {

                override fun onNext(t: List<PlayerItem>) {
                    showLoading(false)
                    playersObserver.value = t
                }

                override fun onError(e: Throwable) {
                    showLoading(false)
                    e.printStackTrace()
                    messageObserver.value = e.message
                }
            })
    }

    private fun getPlayers(): Observable<List<Player>> = Observable.create {
        var list = getDatabase().getPlayerDao().getPlayers()
        if (list?.isNotEmpty())

        else {
            createPlayers()
        }
        it.onNext(list)
        it.onComplete()
    }

    private fun toPlayerItems(list: List<Player>): ObservableSource<List<PlayerItem>> = ObservableSource {
        var items: ArrayList<PlayerItem> = arrayListOf()
        var matchId = getDatabase().getMatchDao().getLastRankMatch()?.id
        for (bean in list) {
            var score = scoreRepository.sumPlayerScore(bean.id)
            var rank = rankRepository.getPlayerRank(matchId, bean.id)
            var high = rankRepository.getPlayerHighRank(bean.id)
            var low = rankRepository.getPlayerLowRank(bean.id)
            var item = PlayerItem(bean, "Current Rank $rank (Score $score)", "Highest/Lowest Rank $high/$low")
            items.add(item)
        }
        it.onNext(items)
        it.onComplete()
    }

    private fun createPlayers() {
        var list: ArrayList<Player> = arrayListOf()
        for (index in 'A'..'Z') {
            if (index == 'I' || index == 'O')
                continue
            var player = Player(0, index.toString(), null)
            list.add(player)
        }
        getDatabase().getPlayerDao().insertAll(list)
    }
}