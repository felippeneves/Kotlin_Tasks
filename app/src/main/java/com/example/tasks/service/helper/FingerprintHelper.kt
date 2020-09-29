package com.example.tasks.service.helper

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager

class FingerprintHelper private constructor(){
    companion object {
        fun isAuthenticationAvaliable(context: Context): Boolean {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return false
            }

            val biometricManager: BiometricManager = BiometricManager.from(context)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> return true

                //Não possui o hardware
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> return false

                //Possui o hardware, mas o hardware não está disponível, seja por falta de acesso ou problema físico
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> return false

                //Possui o hardware, o hardware está disponível, porém não está configurado corretamente
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> return false
            }

            return false
        }
    }
}