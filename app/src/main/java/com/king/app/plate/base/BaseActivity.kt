package com.king.app.plate.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.king.app.plate.PlateApplication

/**
 * 描述:
 *
 * 作者：景阳
 *
 * 创建时间: 2020/1/21 15:50
 */
abstract class BaseActivity<T : ViewDataBinding, VM : BaseViewModel> : RootActivity() {

    lateinit var mBinding: T

    lateinit var mModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getContentView())
        mModel = createViewModel()
        if (mModel != null) {
            mModel!!.loadingObserver.observe(this, Observer { show ->
                if (show) {
                    showProgress("loading...")
                } else {
                    dismissProgress()
                }
            })
            mModel!!.messageObserver.observe(this, Observer { message -> showMessageShort(message) })
        }

        initView()
        initData()
    }

    abstract fun getContentView(): Int

    protected abstract fun createViewModel(): VM

    fun generateViewModel(vm: Class<VM>) = ViewModelProvider(this, ViewModelFactory(PlateApplication.instance)).get(vm)

    protected abstract fun initView()

    protected abstract fun initData()

    override fun onDestroy() {
        mModel?.onDestroy()
        super.onDestroy()
    }
}
