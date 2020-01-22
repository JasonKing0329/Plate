package com.king.app.plate.page.login

import android.hardware.fingerprint.FingerprintManager
import android.view.LayoutInflater
import android.view.View

import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal

import com.king.app.plate.R
import com.king.app.plate.base.BindingDialogFragment
import com.king.app.plate.databinding.DialogFingerprintBinding
import com.king.app.plate.utils.DebugLog

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/3/1 14:30
 */
class FingerprintDialog : BindingDialogFragment<DialogFingerprintBinding>() {

    private var onFingerPrintListener: OnFingerPrintListener? = null

    fun setOnFingerPrintListener(onFingerPrintListener: OnFingerPrintListener) {
        this.onFingerPrintListener = onFingerPrintListener
    }

    override fun getBinding(inflater: LayoutInflater) = DialogFingerprintBinding.inflate(inflater)

    override fun onResume() {
        super.onResume()
        // 在xml里或initView里调用不起作用
        setWidth(resources.getDimensionPixelSize(R.dimen.dlg_fingerprint_width))
        setHeight(resources.getDimensionPixelSize(R.dimen.dlg_fingerprint_height))
    }

    override fun initView(view: View) {
        val fingerprint = FingerprintManagerCompat.from(activity!!)
        fingerprint.authenticate(
            null,
            0,
            CancellationSignal(),
            object : FingerprintManagerCompat.AuthenticationCallback() {
                override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errMsgId, errString)
                    DebugLog.e("errMsgId=" + errMsgId + " msg=" + errString!!.toString())
                    handleErrorCode(errMsgId, errString.toString())
                }

                override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                    super.onAuthenticationHelp(helpMsgId, helpString)
                    DebugLog.e("helpMsgId=" + helpMsgId + " msg=" + helpString!!.toString())
                    mBinding.tvMsg.text = helpString.toString()
                }

                override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    dismissAllowingStateLoss()
                    onFingerPrintListener!!.onPass()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    mBinding.tvMsg.text = "识别错误，请重试"
                }
            },
            null
        )
    }

    private fun handleErrorCode(code: Int, msg: String) {
        DebugLog.e("code=$code")
        when (code) {
            FingerprintManager.FINGERPRINT_ERROR_CANCELED -> {
            }
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE ->
                // 当前设备不可用，请稍后再试
                mBinding.tvMsg.text = msg
            FingerprintManager.FINGERPRINT_ERROR_LOCKOUT ->
                // 由于太多次尝试失败导致被锁，该操作被取消
                mBinding.tvMsg.text = msg
            FingerprintManager.FINGERPRINT_ERROR_NO_SPACE ->
                // 没有足够的存储空间保存这次操作，该操作不能完成
                mBinding.tvMsg.text = msg
            FingerprintManager.FINGERPRINT_ERROR_TIMEOUT ->
                // 操作时间太长，一般为30秒
                mBinding.tvMsg.text = msg
            FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS ->
                // 传感器不能处理当前指纹图片
                mBinding.tvMsg.text = msg
        }// 指纹传感器不可用，该操作被取消
    }

    interface OnFingerPrintListener {
        fun onPass()
    }
}
