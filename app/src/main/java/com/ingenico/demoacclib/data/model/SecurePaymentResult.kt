package com.ingenico.demoacclib.data.model

import com.ingenico.ingp.secure.client.lib.ErrorCode

sealed class SecurePaymentResult {
    object Success : SecurePaymentResult()
    data class SpsError(val errorCode: ErrorCode) : SecurePaymentResult()
    data class UsdkError(val errorCode: com.ingenico.ingp.dev.sdk.ErrorCode) : SecurePaymentResult()
    data class ExceptionError(val e: Exception) : SecurePaymentResult()
}
