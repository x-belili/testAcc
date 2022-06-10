package com.ingenico.demoacclib.domain.usecase

import com.ingenico.demoacclib.data.repository.EmvCommonRepository
import com.ingenico.persistence.entity.EmvCommonEntity

class EmvCommonUseCase(private val emvCommonRepository: EmvCommonRepository) {
    fun fillMockupEmvCommon() {
        if (emvCommonRepository.getNumRecords() > 0)
            return

        emvCommonRepository.set(
            EmvCommonEntity(
                termType = "22",
                termCountryCode = "170",
                termCapabilities = "E0F8C8",
                termAddCapabilities = "F000F0A001",
                tacDefault = "DC4000A800",
                tacDenial = "0040000000",
                tacOnline = "DC4004F800",
                applicationVersionNumber = "1"
            )
        )
    }
}