package com.king.app.plate.page.home

import android.app.Application
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.repo.MatchRepository
import com.king.app.plate.page.match.list.MatchItemBean
import com.king.app.plate.utils.DataExporter
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 13:42
 */
class HomeViewModel(application: Application): BaseViewModel(application) {

    var lastMatchVisibility: ObservableInt = ObservableInt(View.GONE)

    var lastMatchObserver = MutableLiveData<MatchItemBean>()

    fun loadData() {
        loadLastMatch()
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<MatchItemBean>(getComposite()) {
                override fun onNext(t: MatchItemBean) {
                    lastMatchVisibility.set(View.VISIBLE)
                    lastMatchObserver.value = t
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "error: $e"
                }

            })
    }

    fun saveData() {
        DataExporter.exportAsHistory()
        messageObserver.value = "success"
    }

    private fun loadLastMatch(): Observable<MatchItemBean> = Observable.create {
        var match = getDatabase().getMatchDao().getLastMatch()
        var player = MatchRepository().getWinner(match)
        it.onNext(MatchItemBean(match, player))
        it.onComplete()
    }
}