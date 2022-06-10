package com.ingenico.demoacclib.domain.usecase

import com.ingenico.demoacclib.data.repository.EmvAidRepository
import com.ingenico.persistence.DatabaseConstants
import com.ingenico.persistence.entity.EmvAidEntity

class EmvAidUseCase(private val emvAidRepository: EmvAidRepository) {
    fun fillMockupEmvAid() {
        if (emvAidRepository.getNumRecords() > 0)
            return

        var idx = 0

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "VISA CREDIT",
                debitFlag = false,
                AID = "A0000000031010",
                technology = DatabaseConstants.DB_EMV_CONTACT,
                termCapabilities = "E0F8C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0083",
                appVersion2 = "0084",
                appVersion3 = "008C",
                appVersion4 = "008D",
                tacDefault = "DC4000A800",
                tacDenial = "0010000000",
                tacOnline = "DC4004F800"
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "VISA DEBIT",
                debitFlag = true,
                AID = "A0000000032010",
                technology = DatabaseConstants.DB_EMV_CONTACT,
                termCapabilities = "E0F8C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0083",
                appVersion2 = "0084",
                appVersion3 = "008C",
                appVersion4 = "008D",
                tacDefault = "DC4000A800",
                tacDenial = "0010000000",
                tacOnline = "DC4004F800",
                clessCvmLimit = 15000,
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "MASTER CREDIT",
                debitFlag = false,
                AID = "A0000000041010",
                technology = DatabaseConstants.DB_EMV_CONTACT,
                termCapabilities = "E0F8C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0002",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "FC50BC8000",
                tacDenial = "0000000000",
                tacOnline = "FC50BC8000",
                clessCvmLimit = 15000,
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "MASTER DEBIT",
                debitFlag = true,
                AID = "A0000000043060",
                technology = DatabaseConstants.DB_EMV_CONTACT,
                termCapabilities = "E0F8C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0002",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "FC509C8800",
                tacDenial = "0000000000",
                tacOnline = "FC509C8800",
                clessCvmLimit = 15000,
                clessTransactionLimit = 99999999
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "AMEX CREDIT",
                debitFlag = false,
                AID = "A00000002501",
                technology = DatabaseConstants.DB_EMV_CONTACT,
                termCapabilities = "E0F8C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0001",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "DC50FC9800",
                tacDenial = "0010000000",
                tacOnline = "DE00FC9800"
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "DISCOVER CREDIT",
                debitFlag = false,
                AID = "A0000001523010",
                technology = DatabaseConstants.DB_EMV_CONTACT,
                termCapabilities = "E0F8C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0001",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "DC00002000",
                tacDenial = "0010000000",
                tacOnline = "FCE09CF800"
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "PAYWAVE CREDIT",
                debitFlag = false,
                AID = "A0000000031010",
                technology = DatabaseConstants.DB_EMV_PAYWAVE,
                termCapabilities = "E060C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0083",
                appVersion2 = "0084",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "DC4000A800",
                tacDenial = "0010000000",
                tacOnline = "DC4004F800",
                clessFloorLimit = 0,
                clessCvmLimit = 15000,
                clessTransactionLimit = 1000000,
                clessFloorLimitDolar = 0,
                clessCvmLimitDolar = 800000,
                clessTransactionLimitDolar = 9990000

            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "PAYWAVE DEBIT",
                debitFlag = true,
                AID = "A0000000032010",
                technology = DatabaseConstants.DB_EMV_PAYWAVE,
                termCapabilities = "E060C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0083",
                appVersion2 = "0084",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "DC4000A800",
                tacDenial = "0010000000",
                tacOnline = "DC4004F800",
                clessFloorLimit = 0,
                clessCvmLimit = 15000,
                clessTransactionLimit = 9990000,
                clessFloorLimitDolar = 0,
                clessCvmLimitDolar = 0,
                clessTransactionLimitDolar = 9990000,
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx++,
                name = "PAYPASS CREDIT",
                debitFlag = false,
                AID = "A0000000041010",
                technology = DatabaseConstants.DB_EMV_PAYPASS,
                termCapabilities = "E060C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0002",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "F45084800C",
                tacDenial = "0000000000",
                tacOnline = "F45084800C",
                clessFloorLimit = 0,
                clessCvmLimit = 15000,
                clessTransactionLimit = 100000,
                clessCdcvmLimit = 90000,
                clessFloorLimitDolar = 0,
                clessCvmLimitDolar = 999000,
                clessTransactionLimitDolar = 100000,
                clessCdcvmLimitDolar = 999000
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx,
                name = "PAYPASS DEBIT",
                debitFlag = true,
                AID = "A0000000043060",
                technology = DatabaseConstants.DB_EMV_PAYPASS,
                termCapabilities = "E060C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0002",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "F45004800C",
                tacDenial = "0000800000",
                tacOnline = "F45004800C",
                clessFloorLimit = 0,
                clessCvmLimit = 15000,
                clessTransactionLimit = 9999999,
                clessCdcvmLimit = 9999999,
                clessFloorLimitDolar = 0,
                clessCvmLimitDolar = 0,
                clessTransactionLimitDolar = 9999999,
                clessCdcvmLimitDolar = 9999999,
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx,
                name = "EXPRESSPAY CREDIT",
                debitFlag = false,
                AID = "A00000002501",
                technology = DatabaseConstants.DB_EMV_EXPRESSPAY,
                termCapabilities = "E060C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0001",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "DC50840000",
                tacDenial = "0000000000",
                tacOnline = "C400000000",
                clessFloorLimit = 0,
                clessCvmLimit = 15000,
                clessTransactionLimit = 80000,
                clessFloorLimitDolar = 0,
                clessCvmLimitDolar = 15000,
                clessTransactionLimitDolar = 80000,
            )
        )

        emvAidRepository.addRecord(
            EmvAidEntity(
                id = idx,
                name = "DISCOVER DPAS",
                debitFlag = false,
                AID = "A0000001523010",
                technology = DatabaseConstants.DB_EMV_DISCOVER_DPAS,
                termCapabilities = "E060C8",
                termAddCapabilities = "F000F0A001",
                appVersion1 = "0001",
                appVersion2 = "",
                appVersion3 = "",
                appVersion4 = "",
                tacDefault = "DC00002000",
                tacDenial = "0010000000",
                tacOnline = "FCE09CF800",
                clessFloorLimit = 0,
                clessCvmLimit = 15000,
                clessTransactionLimit = 80000,
                clessFloorLimitDolar = 0,
                clessCvmLimitDolar = 15000,
                clessTransactionLimitDolar = 999000,
            )
        )
    }
}