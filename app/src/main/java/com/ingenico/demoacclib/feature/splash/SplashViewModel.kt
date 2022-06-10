package com.ingenico.demoacclib.feature.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ingenico.acc.SecurePayment
import com.ingenico.acc.UsdkManager
import com.ingenico.demoacclib.data.model.SecurePaymentResult
import com.ingenico.demoacclib.domain.usecase.EmvAidUseCase
import com.ingenico.demoacclib.domain.usecase.EmvCakeyUseCase
import com.ingenico.demoacclib.domain.usecase.EmvCommonUseCase
import com.ingenico.ingp.secure.client.lib.ErrorCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    application: Application,
    private val emvAidUseCase: EmvAidUseCase,
    private val emvCakeyUseCase: EmvCakeyUseCase,
    private val emvCommonUseCase: EmvCommonUseCase
) : ViewModel() {

    var app = application

    fun setEmvConfiguration() = liveData(Dispatchers.IO) {
        //acc emv parameters
        emvCommonUseCase.fillMockupEmvCommon()
        emvAidUseCase.fillMockupEmvAid()
        emvCakeyUseCase.fillMockupEmvCaKey()
        emit(true)
    }

    fun servicesConnection() = liveData(Dispatchers.IO) {
        try {
            //Initialize Secure Payment Service(SPS)
            SecurePayment.initialize(app)

            val statusSps = SecurePayment.connect()

            if (statusSps == ErrorCode.OK) {

                //Initialize Usdk manager
                UsdkManager.initialize(app)
                val statusUsdk = UsdkManager.connect()

                if (statusUsdk == com.ingenico.ingp.dev.sdk.ErrorCode.OK) {
                    emit(SecurePaymentResult.Success)
                } else {
                    emit(SecurePaymentResult.UsdkError(statusUsdk))
                }
            } else {
                emit(SecurePaymentResult.SpsError(statusSps))
            }
        } catch (e: Exception) {
            Log.d("ConectionServices", "Connection services exception = ${e.stackTrace}")
            emit(SecurePaymentResult.ExceptionError(e))
        }
    }
}