package com.king.app.plate.page.home

import android.app.Application
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.utils.DataExporter

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 13:42
 */
class HomeViewModel(application: Application): BaseViewModel(application) {

    var editVisibility: ObservableInt = ObservableInt(View.GONE)
    var showLastMatchDraw = MutableLiveData<Long>()

    init {
        checkExistMatch()
    }

    private fun checkExistMatch() {
        editVisibility.set(View.VISIBLE)
    }

    fun saveData() {
        DataExporter.exportAsHistory()
        messageObserver.value = "success"
    }

    fun getLastMatch() {
        var match = getDatabase().getMatchDao().getLastMatch()
        if (match == null) {
            messageObserver.value = "No match created"
        }
        else{
            showLastMatchDraw.value = match.id
        }
    }
}