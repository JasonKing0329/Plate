package com.king.app.plate.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * 描述:
 *
 * 作者：景阳
 *
 * 创建时间: 2017/7/25 15:28
 */
class AlertDialogFragment : DialogFragment() {

    private var title: String? = null

    private var message: String? = null

    private var positiveText: String? = null

    private var negativeText: String? = null

    private var neutralText: String? = null

    private var items: Array<CharSequence>? = null

    private var itemListener: DialogInterface.OnClickListener? = null

    private var positiveListener: DialogInterface.OnClickListener? = null

    private var negativeListener: DialogInterface.OnClickListener? = null

    private var neutralListener: DialogInterface.OnClickListener? = null

    private var dismissListener: DialogInterface.OnDismissListener? = null

    fun setTitle(title: String): AlertDialogFragment {
        this.title = title
        return this
    }

    fun setMessage(message: String): AlertDialogFragment {
        this.message = message
        return this
    }

    fun setPositiveText(positiveText: String): AlertDialogFragment {
        this.positiveText = positiveText
        return this
    }

    fun setNegativeText(negativeText: String): AlertDialogFragment {
        this.negativeText = negativeText
        return this
    }

    fun setNeutralText(neutralText: String): AlertDialogFragment {
        this.neutralText = neutralText
        return this
    }

    fun setPositiveListener(positiveListener: DialogInterface.OnClickListener): AlertDialogFragment {
        this.positiveListener = positiveListener
        return this
    }

    fun setNegativeListener(negativeListener: DialogInterface.OnClickListener): AlertDialogFragment {
        this.negativeListener = negativeListener
        return this
    }

    fun setNeutralListener(neutralListener: DialogInterface.OnClickListener): AlertDialogFragment {
        this.neutralListener = neutralListener
        return this
    }

    fun setDismissListener(dismissListener: DialogInterface.OnDismissListener): AlertDialogFragment {
        this.dismissListener = dismissListener
        return this
    }

    fun setItems(
        items: Array<CharSequence>,
        listener: DialogInterface.OnClickListener
    ): AlertDialogFragment {
        this.items = items
        this.itemListener = listener
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(title)
            .setMessage(message)
        if (items != null) {
            builder.setItems(items, itemListener)
        }
        if (positiveText != null) {
            builder.setPositiveButton(positiveText, positiveListener)
        }
        if (negativeText != null) {
            builder.setNegativeButton(negativeText, negativeListener)
        }
        if (neutralText != null) {
            builder.setNeutralButton(neutralText, neutralListener)
        }
        builder.setOnDismissListener(dismissListener)
        return builder.create()
    }
}
