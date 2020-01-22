package com.king.app.plate.page.login

import android.Manifest
import android.content.Intent
import androidx.lifecycle.Observer
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityLoginBinding
import com.king.app.plate.model.fingerprint.FingerprintHelper
import com.king.app.plate.page.SettingsActivity
import com.king.app.plate.page.home.HomeActivity
import com.king.app.plate.utils.AppUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun getContentView(): Int = R.layout.activity_login

    override fun createViewModel(): LoginViewModel = generateViewModel(LoginViewModel::class.java)

    override fun initView() {
        mBinding.model = mModel
        mBinding.btnSetting.setOnClickListener { startActivity(Intent().setClass(this, SettingsActivity::class.java)) }
        mModel!!.loginObserver.observe(this, Observer { success -> superUser() })
        mModel!!.fingerprintObserver.observe(this, Observer { check -> checkFingerprint() })
    }

    override fun initData() {
        if (AppUtil.isAndroidP()) {
            AppUtil.closeAndroidPDialog()
        }

        RxPermissions(this)
            .request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isGrant ->
                initCreate()
            }, { throwable ->
                throwable.printStackTrace()
                finish()
            })
    }

    private fun initCreate() {
        mModel!!.initCreate()
    }

    private fun checkFingerprint() {
        if (FingerprintHelper.isDeviceSupport(this))
            if (FingerprintHelper.isEnrolled(this))
                startFingerPrintDialog()
            else
                showMessageLong("设备未注册指纹")
        else
            showMessageLong("设备不支持指纹")
    }

    /**
     * 通用指纹识别对话框
     */
    private fun startFingerPrintDialog() {
        val dialog = FingerprintDialog()
        dialog.setOnFingerPrintListener(object : FingerprintDialog.OnFingerPrintListener{
            override fun onPass() {
                superUser()
            }
        })
        dialog.show(supportFragmentManager, "FingerprintDialog")
    }

    private fun superUser() {
        startActivity(Intent().setClass(this, HomeActivity::class.java))
        finish()
    }

}
