package com.king.app.plate.view.dialog

import androidx.databinding.ViewDataBinding
import com.king.app.plate.base.BaseFragment
import com.king.app.plate.base.BaseViewModel

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/7 10:05
 */
abstract class PopupContent<T : ViewDataBinding, VM : BaseViewModel> :
    BaseFragment<T, VM>() {
    private var dialogHolder: PopupContentHolder? = null
    fun setDialogHolder(dialogHolder: PopupContentHolder?) {
        this.dialogHolder = dialogHolder
    }

    protected fun dismiss() {
        dialogHolder!!.dismiss()
    }

    protected fun dismissAllowingStateLoss() {
        dialogHolder!!.dismissAllowingStateLoss()
    }

}