package com.king.app.plate.model.repo

import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.model.detail.MatchParser
import com.king.app.plate.model.detail.RecordParser
import com.king.app.plate.page.h2h.H2hItem
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 13:29
 */
class H2hRepository: BaseRepository() {

    fun getH2hItems(player1Id: Long, player2Id: Long): Observable<MutableList<H2hItem>> = Observable.create {
        var list = mutableListOf<H2hItem>()
        var records = getDatabase().getRecordDao().getH2hRecords(player1Id, player2Id)
        for (record in records) {
            var pack = RecordParser.getRecordPack(record, getDatabase())
            var match = getDatabase().getMatchDao().getMatchById(record.matchId)
            var round = RecordParser.getRoundSimpleText(record.round, match.level)
            var result = RecordParser.getH2hResult(pack)
            var score = RecordParser.getScoreText(pack)
            var level = MatchParser.getLevelText(match.level)
            var item = H2hItem(pack, match, level, round, result, score)
            list.add(item)
        }
        it.onNext(list)
        it.onComplete()
    }
}