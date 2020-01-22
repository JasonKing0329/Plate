package com.king.app.plate.base

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 描述:
 *
 * 作者：景阳
 *
 * 创建时间: 2020/1/21 15:02
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    var loadingObserver = MutableLiveData<Boolean>()
    var messageObserver = MutableLiveData<String>()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun dispatchCommonError(e: Throwable) {
        messageObserver.value = "Load error: " + e.message
    }

    fun dispatchCommonError(errorTitle: String, e: Throwable) {
        messageObserver.value = errorTitle + ": " + e.message
    }

    fun getComposite() = compositeDisposable

    fun onDestroy() {
        compositeDisposable.clear()
    }

    fun showLoading(show: Boolean){
        loadingObserver.value=show
    }

}
