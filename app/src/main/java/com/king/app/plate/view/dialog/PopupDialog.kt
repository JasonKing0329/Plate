package com.king.app.plate.view.dialog

import android.content.DialogInterface
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import com.king.app.plate.R
import com.king.app.plate.databinding.DialogBaseBinding
import com.king.app.plate.utils.DebugLog
import com.king.app.plate.utils.ScreenUtils

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 10:39
 */
open class PopupDialog: BaseDialogFragment(), PopupContentHolder {

    private lateinit var binding: DialogBaseBinding

    var title: String? = ""

    var backgroundColor = 0

    var hideClose = false

    var showDelete = false

    var onDeleteListener: View.OnClickListener? = null

    var content: PopupContent<*, *>? = null

    var maxDialogHeight = 0

    var forceHeight = 0

    var onDismissListener: DialogInterface.OnDismissListener? = null

    override fun onSubCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogBaseBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {

        if (title != null) {
            binding.tvTitle.setText(title);
        }
        if (backgroundColor != 0) {
            var drawable = binding.groupDialog.background as GradientDrawable;
            drawable.setColor(backgroundColor);
        }
        if (hideClose) {
            binding.ivClose.visibility = View.GONE;
        }
        if (showDelete) {
            binding.ivDelete.visibility = View.VISIBLE;
            binding.ivDelete.setOnClickListener(onDeleteListener);
        }

        initDragParams();

        if (content != null) {
            content!!.setDialogHolder(this);
            replaceContentFragment(content, "ContentView");
        }

        binding.groupFtContainer.post{
            DebugLog.e("groupFtContent height=" + binding.groupFtContainer.getHeight());
            limitMaxHeight();
        };

        binding.ivClose.setOnClickListener { dismissAllowingStateLoss() };
    }

    private fun replaceContentFragment(
        target: PopupContent<*, *>?,
        tag: String?
    ) {
        if (target != null) {
            val ft =
                childFragmentManager.beginTransaction()
            ft.replace(R.id.group_ft_container, target, tag)
            ft.commit()
        }
    }

    private fun initDragParams() {
        binding.groupDialog.setOnTouchListener(DialogTouchListener())
    }

    private fun limitMaxHeight() {
        if (forceHeight > 0) {
            val params = binding.groupFtContainer.layoutParams
            params.height = forceHeight
            binding.groupFtContainer.layoutParams = params
        }
        else{
            val maxContentHeight: Int = getMaxHeight()
            if (binding.groupFtContainer.height > maxContentHeight) {
                val params = binding.groupFtContainer.layoutParams
                params.height = maxContentHeight
                binding.groupFtContainer.layoutParams = params
            }
        }
    }

    /**
     * 子类可选择覆盖
     * @return
     */
    open fun getMaxHeight(): Int {
        return if (maxDialogHeight != 0) {
            maxDialogHeight
        } else {
            ScreenUtils.getScreenHeight() * 3 / 5
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    /**
     * move dialog
     */
    inner class DialogTouchListener : OnTouchListener {

        var startPoint = Point()
        var touchPoint = Point()

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.rawX //
                    val y = event.rawY
                    startPoint.x = x.toInt()
                    startPoint.y = y.toInt()
                    DebugLog.d("ACTION_DOWN x=$x, y=$y")
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = event.rawX
                    val y = event.rawY
                    touchPoint.x = x.toInt()
                    touchPoint.y = y.toInt()
                    val dx = touchPoint.x - startPoint.x
                    val dy = touchPoint.y - startPoint.y
                    move(dx.toInt(), dy.toInt())
                    startPoint.x = x.toInt()
                    startPoint.y = y.toInt()
                }
                MotionEvent.ACTION_UP -> {
                }
                else -> {
                }
            }
            return true
        }
    }
}