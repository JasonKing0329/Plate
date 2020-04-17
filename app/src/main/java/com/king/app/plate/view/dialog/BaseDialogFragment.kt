package com.king.app.plate.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.king.app.plate.R

/**
 * 描述:DialogFragment基类
 *
 * <br></br>创建时间: 2017/4/19
 */
abstract class BaseDialogFragment : DialogFragment() {
    var windowParams: WindowManager.LayoutParams? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = cancelable
        setStyle(STYLE_NORMAL, R.style.BaseDialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        windowParams = dialog!!.window.attributes
        return onSubCreateView(inflater, container, savedInstanceState)
    }

    protected abstract fun onSubCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    /**
     * 是否可以点击空白处取消
     *
     * @return
     */
    val cancelable: Boolean
        get() = true

    fun setWidth(width: Int) {
        windowParams!!.width = width
        dialog!!.window.attributes = windowParams
    }

    fun setHeight(height: Int) {
        windowParams!!.height = height
        dialog!!.window.attributes = windowParams
    }

    /**
     * 设置dialog的偏移位置
     *
     * @param x 负数向左，正数向右
     * @param y 负数向上，正数向下
     */
    fun setPositionOffset(x: Int, y: Int) {
        windowParams!!.x = x
        windowParams!!.y = y
        dialog!!.window.attributes = windowParams
    }

    /**
     * move dialog
     *
     * @param x
     * @param y
     */
    protected fun move(x: Int, y: Int) {
        windowParams!!.x += x
        windowParams!!.y += y
        dialog!!.window.attributes = windowParams //must have
    }
}