package com.king.app.plate.page.home

import android.app.Application
import android.view.View
import androidx.databinding.ObservableInt
import com.king.app.plate.base.BaseViewModel

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 13:42
 */
class HomeViewModel(application: Application): BaseViewModel(application) {

    var editVisibility: ObservableInt = ObservableInt(View.GONE)

    init {
        checkExistMatch()
    }

    private fun checkExistMatch() {
        editVisibility.set(View.VISIBLE)
    }
}