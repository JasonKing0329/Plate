package com.king.app.plate.page.match

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.repo.MatchRepository

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 11:03
 */
class MatchViewModel(application: Application): BaseViewModel(application) {

    var matchesObserver = MutableLiveData<List<Match>>()
    var repository = MatchRepository()

    fun loadMatches() {
        var list = getDatabase().getMatchDao().getMatches().reversed()
        matchesObserver.value = list
    }

    fun insertOrUpdate(match: Match) {
        if (match.id == 0.toLong()) {
            getDatabase().getMatchDao().insert(match)
        }
        else{
            getDatabase().getMatchDao().update(match)
        }
        loadMatches()
    }

    fun deleteMatch(bean: Match) {
        repository.deleteMatch(bean)
        loadMatches()
    }
}