package com.king.app.plate.model.fingerprint

import android.content.Context
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

class FingerprintHelper {

    companion object {
        fun isDeviceSupport(context: Context): Boolean = FingerprintManagerCompat.from(context).isHardwareDetected
        fun isEnrolled(context: Context): Boolean = FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
    }
}
