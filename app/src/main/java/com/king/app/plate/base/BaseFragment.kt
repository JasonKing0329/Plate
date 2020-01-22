package com.king.app.plate.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 14:22
 */
abstract class BaseFragment<T : ViewDataBinding, VM: BaseViewModel>: RootFragment() {

    lateinit var mBinding: T

    lateinit var mModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getBinding(inflater)
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
        val view = mBinding.root
        initView(view)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
        super.onViewCreated(view, savedInstanceState)
    }

    protected abstract fun getBinding(inflater: LayoutInflater): T

    protected abstract fun createViewModel(): VM

    protected abstract fun initView(view: View)

    protected abstract fun initData()

}