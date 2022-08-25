package com.ingenico.demoacclib.feature.demo_pay

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ingenico.acc.EmvParamsConfig
import com.ingenico.acc.EnumKeyAlgorithm
import com.ingenico.acc.SecurePayment
import com.ingenico.acc.SecurePayment.encryptData
import com.ingenico.acc.workflow.GetCardResult
import com.ingenico.acc.workflow.GoOnChipResult
import com.ingenico.acc.workflow.Transaction
import com.ingenico.demoacclib.data.model.CardDataModel
import com.ingenico.demoacclib.data.model.TransactionModel
import com.ingenico.ingp.types.toHexString
import com.ingenico.lar.larlib.TLVDataList
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream

class DemoPayViewModel() : ViewModel() {
    var cardReadRetries = 0
    var transactionModel = TransactionModel()

    data class Result(
        var invalidTrack: Boolean? = false,
        var serviceCode: Char? = null, //ForceChipInsert
        var onSuccess: CardDataModel? = null
    )

    fun getEmvInitialized(context: Context) = liveData(Dispatchers.IO) {
        emit(EmvParamsConfig(context).getDefault())
    }

    fun checkGetCardResult(getCardResult: GetCardResult?) = liveData(Dispatchers.IO) {
        val track1 = getCardResult?.track1 ?: ""
        val track2 = getCardResult?.track2 ?: ""
        val track3 = getCardResult?.track3 ?: ""

        val result = Result()

        if (track2.isEmpty() || track2.length <= 18)
        //Invalid Track
            result.invalidTrack = true
        else {
            val fallback = cardReadRetries >= 1 && getCardResult?.entryMode == Transaction.MODE_MAG

            if (getCardResult?.entryMode == Transaction.MODE_MAG && !fallback
                && (getCardResult.serviceCode.startsWith("2") || getCardResult.serviceCode.startsWith(
                    "6"
                ))
            ) {
                //Force chip Insert
                result.serviceCode = getCardResult.serviceCode[0]
            } else {
                result.onSuccess = CardDataModel(
                    appLabel = getCardResult?.appLabel ?: "",
                    entryMode = getCardResult?.entryMode ?: 0,
                    tracks1 = track1,
                    tracks2 = track2.replace("=", "D"),
                    tracks3 = track3,
                    pan = getCardResult?.pan ?: "",
                    panLen = (getCardResult?.pan ?: "").length,
                    serviceCode = getCardResult?.serviceCode ?: "",
                    cardholderName = getCardResult?.cardholderName ?: "",
                    expirationDate = getCardResult?.expirationDate ?: "",
                    panSequenceNumber = getCardResult?.PANSequenceNumber ?: 0,
                    fallback = fallback
                )
            }
        }

        emit(result)
    }

    fun setGetCardDataInfo(getCardDataModel: CardDataModel) = liveData(Dispatchers.IO) {
        transactionModel.let {
            it.track1 = getCardDataModel.tracks1

            Log.e("track2", getCardDataModel.tracks2)
            it.track2 = cipherDataString(getCardDataModel.tracks2)
            Log.e("track2_Encr", it.track2)

            it.track3 = getCardDataModel.tracks3
            it.emvAppLabel = getCardDataModel.appLabel
            it.carholderName = getCardDataModel.cardholderName
            it.entryMode = getCardDataModel.entryMode

            val index = getCardDataModel.tracks2.indexOf('D') + 1

            if (getCardDataModel.expirationDate != "000000")
                it.expireDate = getCardDataModel.expirationDate
            else
                it.expireDate = getCardDataModel.tracks2.substring(index, index + 4)

            //Expiration Date(Ciphered data)
            it.expirationDate = cipherDataString(it.expirationDate)

            if (getCardDataModel.serviceCode != "000")
                it.serviceCode = getCardDataModel.serviceCode
            else
                it.serviceCode = getCardDataModel.tracks2.substring(index + 4, index + 4 + 3)

            //Service Code Date(Ciphered data)
            it.serviceCode = cipherDataString(it.serviceCode)

            if (getCardDataModel.pan.isNotEmpty())
                it.pan = getCardDataModel.pan //Encrypted
            else
                it.pan = it.track2.substringBefore('D')

            it.panLen = getCardDataModel.panLen
            it.panSequenceNumber = getCardDataModel.panSequenceNumber
            it.fallback = getCardDataModel.fallback
            it.last4pan = it.pan.substring(
                it.pan.length - 4,
                it.pan.length
            )

            //PAN(Ciphered data)
            it.pan = cipherDataString(it.pan)

        }
        emit(transactionModel)
    }

    private fun cipherDataString(input: String): String {
        val encInputByteArray: ByteArray?

        val inputByteArray: ByteArray = stringHexToByteArray(input) ?: byteArrayOf()

        //EncryptData
        encInputByteArray = encryptData(
            "0000 00",
            EnumKeyAlgorithm.DUKPT,
            inputByteArray
        )

        return encInputByteArray?.toHexString() ?: ""
    }

    private fun stringHexToByteArray(encrypt: String): ByteArray? {
        val baos = ByteArrayOutputStream(6144)

        val hex = encrypt.replace(" ", "").replace("-", "").replace(":", "")
        var i = 0

        while (i < hex.length - 1) {
            val output = hex.substring(i, i + 2)
            // convert hex to decimal
            val decimal = output.toInt(16)
            try {
                baos.write(decimal)
            } catch (e: Exception) {
            }
            i += 2
        }
        return baos.toByteArray()
    }

    fun setGoOnChipDataInfo(goOnChipResult: GoOnChipResult) = liveData(Dispatchers.IO) {
        transactionModel.let {
            it.bit55 = goOnChipResult.bit55
            it.bit55Length = goOnChipResult.bit55Length
            it.decision = goOnChipResult.desicion.ordinal
            it.didOfflinePIN = goOnChipResult.didOfflinePIN
            it.isBlockedPIN = goOnChipResult.isBlockedPIN
            it.didOnlinePIN = goOnChipResult.didOnlinePIN
            it.signature = goOnChipResult.signature
            it.PINKSN = goOnChipResult.pinKsn
            it.onlinePINBlock = goOnChipResult.onlinePINBlock
        }

        if (transactionModel.bit55.isNotEmpty()) {
            val tlv = TLVDataList.fromBinary(transactionModel.bit55)
            transactionModel.emvAid = tlv.getTLV("4F")?.value ?: ""
            //first generate
            transactionModel.emvTvr = tlv.getTLV("95")?.value ?: ""
            transactionModel.emvTsi = tlv.getTLV("9B")?.value ?: ""
            transactionModel.emvAppCryptogram = tlv.getTLV("9F26")?.value ?: ""
        }

        emit(transactionModel)
    }

    private fun encryptData(data: String) {

    }


}