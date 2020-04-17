package com.king.app.plate.view.dialog

import com.king.app.plate.base.IFragmentHolder

/**
 * @desc
 * @auth 景阳
 * @time 2018/3/24 0024 23:18
 */
interface PopupContentHolder : IFragmentHolder {
    fun dismiss()
    fun dismissAllowingStateLoss()
}