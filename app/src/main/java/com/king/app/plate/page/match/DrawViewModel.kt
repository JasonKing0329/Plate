package com.king.app.plate.page.match

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 11:24
 */
class DrawViewModel(application: Application): BaseViewModel(application) {

    var dataObserver: MutableLiveData<DrawData> = MutableLiveData()

    fun loadData(matchId: Int) {

    }
}