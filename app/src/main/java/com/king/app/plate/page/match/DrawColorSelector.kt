package com.king.app.plate.page.match

import android.view.LayoutInflater
import android.view.View
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.FragmentDrawColorBinding
import com.king.app.plate.model.SettingProperty
import com.king.app.plate.model.bean.DrawColors
import com.king.app.plate.view.dialog.PopupContent
import com.king.app.plate.view.draw.DrawsView

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/8 14:11
 */
class DrawColorSelector: PopupContent<FragmentDrawColorBinding, EmptyViewModel>() {

    var onColorChangedListener: OnColorChangedListener? = null

    private var colors = arrayOf(
        intArrayOf(0, 0),
        intArrayOf(0, 0),
        intArrayOf(0, 0),
        intArrayOf(0, 0),
        intArrayOf(0),
        intArrayOf(0)
    )

    override fun getBinding(inflater: LayoutInflater): FragmentDrawColorBinding = FragmentDrawColorBinding.inflate(layoutInflater)

    override fun createViewModel(): EmptyViewModel = generateViewModel(EmptyViewModel::class.java)

    override fun initView(view: View) {
        val views = arrayOf(
            arrayOf(mBinding.tvR32Focus, mBinding.tvR32Dim),
            arrayOf(mBinding.tvR16Focus, mBinding.tvR16Dim),
            arrayOf(mBinding.tvQfFocus, mBinding.tvQfDim),
            arrayOf(mBinding.tvSfFocus, mBinding.tvSfDim),
            arrayOf(mBinding.tvF),
            arrayOf(mBinding.tvW)
        )
        var bean = SettingProperty.getDrawColors()
        var initColors = bean?.colors ?: DrawsView.defaultCellColors
        for (i in views.indices) {
            for (j in views[i].indices) {
                colors[i][j] = initColors[i][j]
                views[i][j].setBackgroundColor(colors[i][j])
                views[i][j].setTextColor(AppConstants.DRAW_TEXT_DEF)
                var isFocus = j == 0
                views[i][j].setOnClickListener { pickColor(i, isFocus, colors[i][j], it) }
            }
        }
        mBinding.tvOk.setOnClickListener { SettingProperty.setDrawColors(DrawColors(colors)) }
    }

    override fun initData() {

    }

    private fun pickColor(round: Int, isFocus: Boolean, initColor:Int, showView: View) {
        ColorPickerDialogBuilder.with(context)
            .setTitle("Pick color")
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .initialColor(initColor)
            .setOnColorSelectedListener {  }
            .setPositiveButton("Ok"
            ) { d, lastSelectedColor, allColors ->
                showView.setBackgroundColor(lastSelectedColor)
                var focusIndex = if (isFocus) 0 else 1
                colors[round][focusIndex] = lastSelectedColor
                onColorChangedListener?.onColorChanged(colors)
            }
            .setNegativeButton("Cancel", null)
            .build().show()
    }

    interface OnColorChangedListener {
        fun onColorChanged(colors: Array<IntArray>)
    }
}