package com.ingenico.demoacclib.data.model

data class CardDataModel(
    var appLabel: String,
    var entryMode: Int,
    var tracks1: String,
    var tracks2: String,
    var tracks3: String,
    var pan: String,
    var panLen: Int,
    var serviceCode: String,
    var cardholderName: String,
    var expirationDate: String,
    var panSequenceNumber: Int = 0,
    var fallback: Boolean = false
)